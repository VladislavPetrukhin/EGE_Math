package com.vlad.ege_math

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.vlad.ege_math.databinding.ActivityRecyclerviewBinding

class RecyclerViewActivity : AppCompatActivity() {

    private val recyclerViewItems: ArrayList<String> = ArrayList()
    lateinit var binding: ActivityRecyclerviewBinding
    private val TAG = "LogRecyclerViewActivity"
    private var userSelectedMode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recyclerview)
        userSelectedMode = intent.getStringExtra("userSelectedMode").toString()
        saveToSharePref("userSelectedMode",userSelectedMode)

        Log.d(TAG, userSelectedMode)
        createRecyclerView()
    }

    private fun createRecyclerView() {
        inflateRecyclerViewItems()

        val adapter = RecyclerViewAdapter(recyclerViewItems, this)
        val columnCount = resources.getInteger(R.integer.columnCount)
        val layoutManager = GridLayoutManager(this,columnCount)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
    }

    private fun inflateRecyclerViewItems() {
        Log.d(TAG, userSelectedMode)
        recyclerViewItems.clear()
        when (userSelectedMode) {  //заполняем recyclerViewItems в зависимости от выбранного раздела пользователем
            "teory" -> {
                supportActionBar?.title = "Теория"
                val count = resources.getInteger(R.integer.teory_count)
                var textResourceId: Int
                for (i in 1..count) {
                    textResourceId = resources.getIdentifier("teory$i", "string", packageName)
                    recyclerViewItems.add(resources.getString(textResourceId))
                }
            }

            "test" -> {
                supportActionBar?.title = "Тесты"
                val count = resources.getInteger(R.integer.test_count)
                var textResourceId: Int
                for (i in 1..count) {
                    textResourceId = resources.getIdentifier("test_name$i", "string", packageName)
                    recyclerViewItems.add(resources.getString(textResourceId))
                }
            }

            "trialVariants" -> {
                supportActionBar?.title = "Пробные варианты"
                val count = resources.getInteger(R.integer.trial_variants_count)
                var textResourceId: Int
                for (i in 1..count) {
                    textResourceId = resources.getIdentifier("prName$i", "string", packageName)
                    recyclerViewItems.add(resources.getString(textResourceId))
                }
            }
            else -> {
                Log.e(TAG, "ERROR")
            }
        }
    }

   fun goToActivity(position: Int) {  //переходим в нужное активити в зависимости от выбора пользователя, передаем позицию выбранного номера в recyclerciew
        val sharedPref: SharedPreferences =
            this.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val userSelectedMode = sharedPref.getString("userSelectedMode", "").toString()
        Log.d(TAG, userSelectedMode)

        var intent = Intent()
        when (userSelectedMode) {
            "teory" -> {
                intent = Intent(this, TeoryActivity::class.java)
            }

            "test" -> {
                intent = Intent(this, TestActivity::class.java)
            }

            "trialVariants" -> {
                intent = Intent(this, RecyclerViewTrialVariantsActivity::class.java)
               // saveToSharePref("prNumber",(position + 1).toString())
                //clearAnswers()
            }
            else -> {
                Log.e(TAG, "ERROR!")
            }
        }
        intent.putExtra("position", position + 1)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (userSelectedMode == "selectTrialVariantExercise") {
                userSelectedMode = "trialVariants"
                saveToSharePref("userSelectedMode",userSelectedMode)
                createRecyclerView()
            } else {
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (userSelectedMode == "selectTrialVariantExercise") {
            userSelectedMode = "trialVariants"
            saveToSharePref("userSelectedMode",userSelectedMode)
            createRecyclerView()
        } else {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun saveToSharePref(name:String,text:String){
        val sharedPref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(name, text)
        editor.apply()
    }
}

