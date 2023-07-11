package com.vlad.ege_math

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.vlad.ege_math.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {
    private var position = 0
    private var numberOfQuestion:Int = 1
    lateinit var binding:ActivityTestBinding
    private var userAnswers = ArrayList<String>()
    private var finishedTest = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Решите задание"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_test)
        position = intent.getIntExtra("position",1)

        val textResourceId = resources.getIdentifier("test_name$position","string",packageName)
        supportActionBar?.title = resources.getText(textResourceId)

        val defaultSharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val fontSize = defaultSharedPref.getString("pref_key_font_size", resources.getInteger(R.integer.mediumTextSize).toString()).toString()
        binding.testTextView.textSize = fontSize.toFloat()

        inflateExercise()

        binding.testAnswerButton.setOnClickListener {
            numberOfQuestion++
            userAnswers.add(binding.testEditText.editText?.text.toString().trim())
            if (finishedTest) {
//                var intent = Intent(this,RecyclerViewActivity::class.java)
//                intent.putExtra("type","test")
//                startActivity(intent)
                finish()
            } else if (numberOfQuestion > resources.getInteger(R.integer.exercisesInTests_count)) {  //если еще не все номера в тесте сделали, продолжаем, заполяя поля новым заданием
                    finishTest()
                } else {
                    inflateExercise()
                }
        }
    }

    private fun inflateExercise() { //выбираем текст в зависимости от позиции на которую нажали в recyclerview
        val textResourceId = resources.getIdentifier("test${position}_$numberOfQuestion","string",packageName)
        binding.testTextView.text = resources.getText(textResourceId)
        binding.testEditText.editText?.setText("")
    }

    private fun finishTest() { //заканчиваем тест и проверяем ответы
        val textResourceId = resources.getIdentifier("testAnswer$position","string",packageName)
        val correctAnswers = resources.getString(textResourceId).split(":")
        var quantityOfCorrectAnswers = 0
        for (i in correctAnswers.indices){
            if(userAnswers[i] == correctAnswers[i]){
                quantityOfCorrectAnswers++
            }
        }
        if (quantityOfCorrectAnswers == correctAnswers.size){
            binding.testTextView.text = resources.getString(R.string.correct_answers)
            val sharedPref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("test${position}_done", true)
            editor.apply()
        }else{
            val string =
                resources.getString(R.string.not_correct_answers) + quantityOfCorrectAnswers +
                        "/" + resources.getInteger(R.integer.exercisesInTests_count)
            binding.testTextView.text = string
        }
        binding.testEditText.visibility = View.GONE
        binding.testAnswerButton.text = "Окей"
        finishedTest = true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}