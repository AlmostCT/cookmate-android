package almostct.top.foodhack.ui.cooking

import activitystarter.Arg
import almostct.top.foodhack.R
import almostct.top.foodhack.model.Receipt
import almostct.top.foodhack.ui.comments.CommentsActivity
import almostct.top.foodhack.ui.common.InjectableActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.app.NavUtils
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.view.GestureDetector
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.marcinmoskala.activitystarter.argExtra
import icepick.State
import kotterknife.bindView
import java.lang.Math.abs


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class CookingActivity : InjectableActivity() {

    @get:Arg
    var currentRecipe: Receipt by argExtra()

    private val mHideHandler = Handler()
    private lateinit var mContentView: View
    private val contentText by bindView<TextView>(R.id.fullscreen_content_text)
    private val progress by bindView<ProgressBar>(R.id.step_progress)
    private val countdownText by bindView<TextView>(R.id.step_countdown)

    private val commentsButton by bindView<Button>(R.id.dummy_button)

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
                if (abs(event1.x - event2.x) < 150) return false
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
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)

        if (savedInstanceState == null) currentStepTimeLeft = currentRecipe.steps[currentStep].time
        updateStep()
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
    var currentStepTimeLeft: Long = 0

    private lateinit var countDown: CountDownTimer

    private fun nextStep() = incState(1)

    private fun previousStep() = incState(-1)

    private fun incState(delta: Int) {
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
        progress.setProgress(percentage(currentStepTimeLeft, totalTime), false)
        countdownText.text = formatSeconds(currentStepTimeLeft)
        countDown = object : CountDownTimer(currentStepTimeLeft * 1000, 1000) {
            override fun onFinish() {
                Log.d(LOG_TAG, "Finish")
                currentStepTimeLeft = 0
            }

            override fun onTick(millisUntilFinished: Long) {
                Log.d(LOG_TAG, "on timer tick: $millisUntilFinished")
                currentStepTimeLeft -= 1
                progress.setProgress(percentage(currentStepTimeLeft, totalTime), false)
                countdownText.text = formatSeconds(currentStepTimeLeft)
            }
        }
        countDown.start()
    }

    override fun onBackPressed() {
        countDown.cancel()
        super.onBackPressed()
    }
}
