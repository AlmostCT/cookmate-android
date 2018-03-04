package almostct.top.foodhack.ui.cooking

import activitystarter.Arg
import almostct.top.foodhack.App
import almostct.top.foodhack.R
import almostct.top.foodhack.api.Client
import almostct.top.foodhack.api.RecognitionResponse
import almostct.top.foodhack.model.Recipe
import almostct.top.foodhack.ui.comments.CommentsActivity
import almostct.top.foodhack.ui.common.InjectableActivity
import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.view.GestureDetector
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.marcinmoskala.activitystarter.argExtra
import icepick.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_cooking.*
import kotterknife.bindView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import ru.yandex.speechkit.*
import ru.yandex.speechkit.gui.callback.DefaultRecognizerListener
import java.lang.Math.abs


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class CookingActivity : InjectableActivity() {

    @get:Arg
    var currentRecipe: Recipe by argExtra()

    private lateinit var cli: Client

    private val mHideHandler = Handler()
    private lateinit var mContentView: View
    private val contentText by bindView<TextView>(R.id.fullscreen_content_text)
    private val progress by bindView<ProgressBar>(R.id.step_progress)
    private val countdownText by bindView<TextView>(R.id.step_countdown)

    private val commentsButton by bindView<Button>(R.id.dummy_button)

    private val cookingRegular by bindView<View>(R.id.cooking_regular)
    private val cookingCongrats by bindView<View>(R.id.cooking_congratulations)

    private val shareButton by bindView<View>(R.id.cooking_share)
    private val closeButton by bindView<View>(R.id.cooking_close)

    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        mContentView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
    private var mControlsView: View? = null
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        val actionBar = supportActionBar
        actionBar?.show()
        mControlsView!!.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private lateinit var mDetector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cooking)
        val actionBar = supportActionBar
        actionBar?.title = currentRecipe.name

        cli = (application as App).client

        mVisible = true
        mControlsView = findViewById(R.id.fullscreen_content_controls)
        mContentView = findViewById(R.id.fullscreen_content)

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener { toggle() }

        // Set up screen swiper
        mDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            private val DEBUG_TAG = "Gestures"
            override fun onFling(
                event1: MotionEvent, event2: MotionEvent,
                velocityX: Float, velocityY: Float
            ): Boolean {
                Log.d(DEBUG_TAG, "onFling: x1=${event1.x} x2=${event2.x} velocity=$velocityX")
                if (event1.y - event2.y > 150) {
                    Log.d(DEBUG_TAG, "Swipe up!")
                    if (ContextCompat.checkSelfPermission(this@CookingActivity, RECORD_AUDIO) != PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(arrayOf(RECORD_AUDIO), 42)
                        }
                    } else {
                        // Reset the current recognizer.
                        resetRecognizer()
                        // To create a new recognizer, specify the language, the model - a scope of recognition to get the most appropriate results,
                        // set the listener to handle the recognition events.
                        recognizer = Recognizer.create(
                            Recognizer.Language.RUSSIAN,
                            Recognizer.Model.NOTES,
                            RecognizerCallback()
                        )
                        // Don't forget to call start on the created object.
                        Log.e(LOG_TAG, "Start from swipe")
                        recognizer?.start()
                    }
                    return true
                }
                if (abs(event1.x - event2.x) < 150 || transitionDisabled) return false
                if (event1.x < event2.x) previousStep() else nextStep()
                return true
            }
        })
        mContentView.setOnTouchListener({ v, it -> mDetector.onTouchEvent(it) })

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.fullscreen_content_controls).setOnTouchListener(mDelayHideTouchListener)
        commentsButton.setOnClickListener { startActivity(Intent(this, CommentsActivity::class.java)) }
        closeButton.setOnClickListener { finish() }
    }

    private var recognizer: Recognizer? = null

    private fun resetRecognizer() {
        if (recognizer != null) {
            recognizer?.cancel()
            recognizer = null
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)

        if (savedInstanceState == null) currentStepTimeLeft = currentRecipe.steps[currentStep].time
        if (transitionDisabled) recipeFinished() else updateStep()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        val actionBar = supportActionBar
        actionBar?.hide()
        mControlsView!!.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @SuppressLint("InlinedApi")
    private fun show() {
        // Show the system bar
        mContentView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [.AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [.AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300

        private const val LOG_TAG = "COOKING"
    }

    /// === START USER SHIT ===

    @JvmField
    @State
    var currentStep: Int = 0

    @JvmField
    @State
    var transitionDisabled: Boolean = false

    @JvmField
    @State
    var currentStepTimeLeft: Long = 0

    private lateinit var countDown: CountDownTimer

    private fun nextStep() = incState(1)

    private fun previousStep() = incState(-1)

    private fun incState(delta: Int) {
        if (currentStep + delta == currentRecipe.steps.size) return recipeFinished()

        currentStep = (currentStep + delta).coerceIn(0, currentRecipe.steps.size - 1)
        currentStepTimeLeft = currentRecipe.steps[currentStep].time
        if (currentStepTimeLeft == 0L) { // no time on this step
            countdownText.visibility = View.GONE
        } else {
            countdownText.visibility = View.VISIBLE
        }
        countDown.cancel()
        updateStep()
    }

    private fun percentage(a: Long, b: Long) = 100 - (a.toDouble() / b.toDouble() * 100).toInt()

    private fun formatSeconds(sec: Long) =
        "${(sec / 60).toString().padStart(2, '0')}:${(sec % 60).toString().padStart(2, '0')}"

    private fun updateStep() {
        contentText.text = currentRecipe.steps[currentStep].longDescription
        val totalTime = currentRecipe.steps[currentStep].time
        progress.progress = percentage(currentStepTimeLeft, totalTime)
        countdownText.text = formatSeconds(currentStepTimeLeft)
        countDown = object : CountDownTimer(currentStepTimeLeft * 1000, 1000) {
            override fun onFinish() {
                Log.d(LOG_TAG, "Finish")
                currentStepTimeLeft = 0
            }

            override fun onTick(millisUntilFinished: Long) {
                Log.d(LOG_TAG, "on timer tick: $millisUntilFinished")
                currentStepTimeLeft -= 1
                progress.progress = percentage(currentStepTimeLeft, totalTime)
                countdownText.text = formatSeconds(currentStepTimeLeft)
            }
        }
        countDown.start()
    }

    private fun recipeFinished() {
        transitionDisabled = true
        cookingRegular.visibility = View.GONE
        commentsButton.visibility = View.GONE
        cookingCongrats.visibility = View.VISIBLE

        viewKonfetti.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.RECT, Shape.CIRCLE)
            .addSizes(Size(12))
            .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
            .stream(300, 3000L)
    }

    override fun onBackPressed() {
        countDown.cancel()
        super.onBackPressed()
    }

    private inner class RecognizerCallback : DefaultRecognizerListener() {

        override fun onRecordingBegin(arg0: Recognizer?) {
            Log.d(LOG_TAG, "recording begins")
        }

        override fun onRecognitionDone(arg0: Recognizer?, arg1: Recognition?) {
            val s = arg1?.bestResultText?.trim()?.trimEnd('.').orEmpty()
            Toast.makeText(this@CookingActivity, "Recognized: $s", Toast.LENGTH_SHORT).show()
            cli.recognize("5a9b3e403cfe433ec0f5e775", currentStep + 1, s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseHandler, { Log.e(LOG_TAG, "Succ", it) })
            Log.d(LOG_TAG, s)
        }

        override fun onError(arg0: Recognizer?, arg1: Error?) {
            Log.e(LOG_TAG, "Pizda : ${arg1?.code} ( ${arg1?.string} )", RuntimeException())
        }
    }

    private var vocalizer: Vocalizer? = null

    private fun resetVocalizer() {
        if (vocalizer != null) {
            vocalizer!!.cancel()
            vocalizer = null
        }
    }

    private val responseHandler: (RecognitionResponse) -> Unit = { (cmd) ->
        Log.d(LOG_TAG, "Got: $cmd")
        when (cmd) {
            "Prev" -> previousStep()
            "Next" -> nextStep()
            else -> {
                val s = if (cmd == "Time") getTimePhrase(currentStepTimeLeft) else cmd
                resetVocalizer()
                vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, s, true, Vocalizer.Voice.ALYSS)
                vocalizer!!.setListener(errorRespondingVocalizerListener)
                vocalizer!!.start()
            }
        }
    }

    private val errorRespondingVocalizerListener = object : VocalizerListener {
        override fun onSynthesisBegin(p0: Vocalizer?) {
        }

        override fun onPlayingBegin(p0: Vocalizer?) {
        }

        override fun onVocalizerError(p0: Vocalizer?, p1: Error?) {
            Log.e(LOG_TAG, "Hui : ${p1?.code} ( ${p1?.string} )", RuntimeException())
        }

        override fun onSynthesisDone(p0: Vocalizer?, p1: Synthesis?) {
        }

        override fun onPlayingDone(p0: Vocalizer?) {
        }
    }
}

fun getTimePhrase(timeLeft: Long): String {
    if (timeLeft == 0L) {
        return "Переходи к следующему шагу как захочешь или скажи мне"
    } else {
        if (timeLeft < 10) {
            return "Осталось меньше десяти секунд"
        }
        if (timeLeft < 60) {
            return "Осталось меньше ${timeToText[((timeLeft + 9) / 10 * 10).toInt()]!!} секунд"
        }
        if (timeLeft < 3600) {
            return "Осталось меньше ${timeToText[(((timeLeft + 59) / 60 + 4) / 5 * 5).toInt()]!!} минут"
        }

        if (timeLeft == 3600L) {
            return "Осталось меньше одного часа"
        }

        if (timeLeft < 10800) {
            return "Осталось меньше ${timeToText[(timeLeft / 3600).toInt()]!!} часов"
        }
        return "Осталось ещё очень много времени, пока можно и отдохнуть"
    }
}

private val timeToText = mutableMapOf<Int, String>(
    1 to "одного",
    2 to "двух",
    3 to "трех",
    4 to "четырех",
    5 to "пяти",
    10 to "десяти",
    15 to "пятнадцати",
    20 to "двадцати",
    25 to "двадцати пяти",
    30 to "тридцати",
    35 to "тридцати пяти",
    40 to "сорока",
    45 to "сорока пяти",
    50 to "пятидесяти",
    55 to "пятидесяти пяти"
)
