package com.game.workoutappkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.game.workoutappkotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.flStart?.setOnClickListener{
            Toast.makeText(this,
                    "Here will start the exercise",
            Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,ExerciseActivity::class.java))
        }

        disableDarkTheme()

        binding?.flBMI?.setOnClickListener{
            startActivity(Intent(this,BMIActivity::class.java))
        }

    }

    private fun disableDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}