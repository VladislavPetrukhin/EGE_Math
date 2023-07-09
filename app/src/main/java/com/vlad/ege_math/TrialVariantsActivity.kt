package com.vlad.ege_math

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.vlad.ege_math.databinding.ActivityTrialVariantsBinding

class TrialVariantsActivity : AppCompatActivity() {

    lateinit var binding: ActivityTrialVariantsBinding
    private val TAG = "TrialVariantsActivityLg"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Решите задание"

        val exercise = intent.getIntExtra("position", 1).toString()
        Log.d(TAG,exercise)
        val receivedAnswer = intent.getStringExtra("answerText").toString() //получаем сохраненный ранее ответ
        Log.d(TAG,receivedAnswer)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_trial_variants)
        binding.testTrialEditText.editText?.setText("")

        val sharedPref: SharedPreferences =
            this.getSharedPreferences("MyPref", Context.MODE_PRIVATE)

        val defaultSharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val fontSize = defaultSharedPref.getString("pref_key_font_size",resources.getInteger(R.integer.mediumTextSize).toString()).toString()
        binding.testTrialTextView.textSize = fontSize.toFloat()

        if (receivedAnswer.isNotEmpty()){
                Log.d(TAG,"not empty: $receivedAnswer")
                binding.testTrialEditText.editText?.setText(receivedAnswer)
            }

        inflateExercise(exercise)

        binding.testTrialAnswerButton.setOnClickListener {
            var answers = sharedPref.getString("answers", "")
            val string = binding.testTrialEditText.editText?.text.toString().trim()
                .replace("/","").replace(":","")   //удаляем из ответа / и : если пользователь их ввел. потому что это разделители в ответах
            answers += "/$exercise:$string" //сохраняем ответ.  / разделяет номера, : раздляет в номере ответ и номер задания
            val editor = sharedPref.edit()
            editor.putString("answers", answers)
            editor.putString("userSelectedMode", "trialVariants")
            editor.apply()
            Log.d(TAG,answers.toString())
            intent = Intent(this, RecyclerViewTrialVariantsActivity::class.java) //выхожим обратно
            startActivity(intent)
        }
    }
    private fun inflateExercise(exercise: String) {
        val sharedPref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val prNumber = sharedPref.getString("prNumber", "1")
        Log.d(TAG, prNumber.toString())
        Log.d(TAG, exercise.toString())
        val textResourceId =
            resources.getIdentifier("pr${prNumber}_$exercise", "string", packageName)
        binding.testTrialTextView.text = resources.getText(textResourceId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val sharedPref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("userSelectedMode", "selectTrialVariantExercise")
            editor.apply()
            intent = Intent(this, RecyclerViewTrialVariantsActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val sharedPref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("userSelectedMode", "selectTrialVariantExercise")
        editor.apply()
        intent = Intent(this, RecyclerViewTrialVariantsActivity::class.java)
        startActivity(intent)
    }
}