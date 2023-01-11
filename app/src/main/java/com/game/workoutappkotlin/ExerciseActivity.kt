package com.game.workoutappkotlin

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.game.workoutappkotlin.databinding.ActivityExerciseBinding
import com.game.workoutappkotlin.databinding.DialogCustomBackConfirmationBinding
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding : ActivityExerciseBinding? = null
    private var resetTimer : CountDownTimer? = null
    private var restProgress = 0

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0

    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts : TextToSpeech? = null

    private var player: MediaPlayer? = null
    private var exerciseStatusAdapter : ExerciseStatusAdapter? = null


    private var restTimeDuration : Long = 1
    private var exerciseTimerDuration : Long = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)
        if(supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this,this)

        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        setupRestView()
        setupExerciseStatusRecyclerviewAdapter()

    }

    override fun onBackPressed() {
        customDialogForBackButton()
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.tvYes.setOnClickListener{
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }

        dialogBinding.tvNo.setOnClickListener{
            customDialog.dismiss()
        }

        customDialog.show()
    }

    private fun setupExerciseStatusRecyclerviewAdapter() {
        binding?.rvExerciesStatus?.layoutManager =
            LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        exerciseStatusAdapter = ExerciseStatusAdapter(exerciseList!!)

        binding?.rvExerciesStatus?.adapter = exerciseStatusAdapter
    }

    private fun setupRestView() {

        try {
            val soundURI = Uri.parse(
                "android.resource://com.game.workoutappkotlin/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping = false
            player?.start()
        }catch (e : Exception) {
            e.printStackTrace()
        }

        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.upcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE

         if(resetTimer != null) {
             resetTimer?.cancel()
             restProgress = 0
         }

        binding?.tvUpcomingExerciseName?.text =
            exerciseList!![currentExercisePosition + 1].getName()

        setRestProgressBar()
    }

    private fun setRestProgressBar() {
        binding?.progressBar?.progress = restProgress
        resetTimer = object : CountDownTimer(restTimeDuration * 1000,1000) {
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseStatusAdapter!!.notifyDataSetChanged()

                setupExerciseView()
            }
        }.start()
    }


    // TODO(Step 4 - Setting up the Exercise View with a 30 seconds timer.)
    // START
    /**
     * Function is used to set the progress of the timer using the progress for Exercise View.
     */
    private fun setupExerciseView() {

        // Here according to the view make it visible as this is Exercise View so exercise view is visible and rest view is not.
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.upcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE

        /**
         * Here firstly we will check if the timer is running and it is not null then cancel the running timer and start the new one.
         * And set the progress to the initial value which is 0.
         */
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()

        setExerciseProgressBar()
    }

    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    // TODO(Step 3 - After REST View Setting up the 30 seconds timer for the Exercise view and updating it continuously.)
    // START
    /**
     * Function is used to set the progress of the timer using the progress for Exercise View for 30 Seconds
     */
    private fun setExerciseProgressBar() {

        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {



              if(currentExercisePosition < exerciseList?.size!! - 1) {
                  exerciseList!![currentExercisePosition].setIsSelected(false)
                  exerciseList!![currentExercisePosition].setIsCompleted(true)
                  exerciseStatusAdapter!!.notifyDataSetChanged()
                  setupRestView()
              } else {
                  finish()
                  startActivity(Intent(this@ExerciseActivity,FinishActivity::class.java))
              }
            }
        }.start()
    }
    // END

    override fun onDestroy() {
        super.onDestroy()

        if(resetTimer != null) {
            resetTimer?.cancel()
            restProgress = 0
        }

        if(tts != null) {
            tts!!.stop()
        }

        if(player != null) {
            player?.stop()
        }

        binding = null

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            }
        } else {
            Log.e("TTS","Initialization Failed")

        }
    }
}