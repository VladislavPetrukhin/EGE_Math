package com.vlad.ege_math

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.vlad.ege_math.databinding.ActivityTeoryBinding

class TeoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Изучите теорию"

        val binding = DataBindingUtil.setContentView<ActivityTeoryBinding>(this, R.layout.activity_teory)

        val defaultSharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val fontSize = defaultSharedPref.getString("pref_key_font_size", resources.getInteger(R.integer.mediumTextSize).toString()).toString()
        binding.teoryTextView.textSize = fontSize.toFloat()  //меняем размер шрифта

        val position = intent.getIntExtra("position",1).toString()

        var textResourceId = resources.getIdentifier("teory$position","string",packageName)
        supportActionBar?.title = resources.getText(textResourceId)

        textResourceId = resources.getIdentifier("text$position","string",packageName)
            binding.teoryTextView.text = resources.getText(textResourceId)  //выбираем текст в зависимости от позиции на которую нажали в recyclerview
        if (position == "8"){
            val text1 = resources.getText(textResourceId)
            textResourceId = resources.getIdentifier("text${position}_2","string",packageName)
            val text2 = resources.getText(textResourceId)
            val builder = StringBuilder()
            builder.append(text1)
            builder.append(text2)
            val string = builder.toString()
            binding.teoryTextView.text= string
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}