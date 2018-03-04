package almostct.top.foodhack.ui.cooking

import activitystarter.Arg
import almostct.top.foodhack.App
import almostct.top.foodhack.R
import almostct.top.foodhack.api.Client
import almostct.top.foodhack.api.RecognitionResponse
import almostct.top.foodhack.model.Receipt
import almostct.top.foodhack.ui.comments.CommentsActivity
import almostct.top.foodhack.ui.common.InjectableActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.view.GestureDetector
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
import kotlinx.android.synthetic.main.activity_cooking2.*
import kotterknife.bindView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import ru.yandex.speechkit.*
import ru.yandex.speechkit.gui.callback.DefaultRecognizerListener

class CookingActivity2 : InjectableActivity() {

    @get:Arg
    var currentRecipe: Receipt by argExtra()

    private lateinit var cli: Client

    private val contentText by bindView<TextView>(R.id.fullscreen_content_text)
    private val progress by bindView<ProgressBar>(R.id.step_progress)
    private val countdownText by bindView<TextView>(R.id.step_countdown)

    private val commentsButton by bindView<Button>(R.id.dummy_button)

    private val cookingRegular by bindView<View>(R.id.cooking_regular)
    private val cookingCongrats by bindView<View>(R.id.cooking_congratulations)

    private val shareButton by bindView<View>(R.id.cooking_share)
    private val closeButton by bindView<View>(R.id.cooking_close)

    private lateinit var mDetector: GestureDetectorCompat

    override fun getActivityTitle(): String {
        return currentRecipe.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cooking2)

        cli = (application as App).client

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
                    if (ContextCompat.checkSelfPermission(
                            this@CookingActivity2,
                            Manifest.permission.RECORD_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 42)
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
                        Log.e(CookingActivity2.LOG_TAG, "Start from swipe")
                        recognizer?.start()
                    }
                    return true
                }
                if (Math.abs(event1.x - event2.x) < 150 || transitionDisabled) return false
                if (event1.x < event2.x) previousStep() else nextStep()
                return true
            }
        })
        val function: (View, MotionEvent) -> Boolean = { v, it ->
            Log.d(LOG_TAG, "Hui pizda $it")
            mDetector.onTouchEvent(it)
            true
        }
        findViewById(R.id.cooking_root).setOnTouchListener(function)
        contentText.setOnTouchListener(function)

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
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

        if (savedInstanceState == null) currentStepTimeLeft = currentRecipe.steps[currentStep].time
        if (transitionDisabled) recipeFinished() else updateStep()
    }

    companion object {
        private const val LOG_TAG = "COOKING2"
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
        fullscreen_content_ingredients.text = currentRecipe.steps[currentStep].products.joinToString(
            separator = "\n",
            transform = { "${it.name}: ${it.amount}" })
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

        viewKonfetti2.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.RECT, Shape.CIRCLE)
            .addSizes(Size(12))
            .setPosition(-50f, viewKonfetti2.width + 50f, -50f, -50f)
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
            Toast.makeText(this@CookingActivity2, "Recognized: $s", Toast.LENGTH_SHORT).show()
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
                val s = if (cmd == "Time") getTimePhrase2(currentStepTimeLeft) else cmd
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

private fun getTimePhrase2(timeLeft: Long): String {
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

