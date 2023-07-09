package com.vlad.ege_math

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.vlad.ege_math.databinding.ActivityRecyclerViewTrialVariantsBinding

class RecyclerViewTrialVariantsActivity : AppCompatActivity() {

    private val recyclerViewItems: ArrayList<String> = ArrayList()
    private var filledAnswers: ArrayList<String> = ArrayList()  //введенные и сохраненнные ответы пользователя
    private var checkedAnswers: ArrayList<Boolean> = ArrayList() //здесь содержится информация правильно или нет пользователь решил номер
    private var isAnswersChecked = false  //проверка ответов выполнена или еще нет
    lateinit var binding: ActivityRecyclerViewTrialVariantsBinding
    private lateinit var adapter:RecyclerViewTrialVariantsAdapter
    private val TAG = "LogRecyclerTrialVariantsViewActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Выберите номер задания"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recycler_view_trial_variants)
        createRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun createRecyclerView() {
        inflateRecyclerViewItems()
        filledAnswers = getAnswers()
        adapter = RecyclerViewTrialVariantsAdapter(recyclerViewItems, this,
            filledAnswers,isAnswersChecked,checkedAnswers, this)
        val columnCount = resources.getInteger(R.integer.columnCountTrialVariantsExercise)
        val layoutManager = GridLayoutManager(this, columnCount)
        binding.recyclerViewTrialVariants.adapter = adapter
        binding.recyclerViewTrialVariants.layoutManager = layoutManager
        adapter.notifyDataSetChanged()
    }
    private fun getAnswers():ArrayList<String> {
        val sharedPref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val string = sharedPref.getString("answers", "").toString()  //получаем из sharedPref строку в которой сохранены ответы пользователя
        Log.d(TAG, string)
        val userRawAnswers = string.split("/").drop(1)  //разделяем их. / это символ используемый в качестве разделителя номеров
        val userAnswers = ArrayList<String>()  //userRawAnswers - это все ответы пользователя. они идут вперемешку. userAnswers - это отсортированные по порядку
        for (i in 1..resources.getInteger(R.integer.exercisesInTrialVariants_count)) {  //заполняем ответы пользователя пустыми строками
            userAnswers.add("")
        }
        var number: Int
        var answer: String
        for (i in userRawAnswers.indices) {  //сортируем ответы по по порядку. : это символ используемый в качестве разлелителя в номере между номеров задания и ответом на него
            number = userRawAnswers[i].split(":")[0].toInt()
            answer = userRawAnswers[i].split(":")[1]
            userAnswers[number - 1] = answer
        }
        return userAnswers
    }

    private fun inflateRecyclerViewItems() {
            recyclerViewItems.clear()
        for (i in 0 until resources.getInteger(R.integer.exercisesInTrialVariants_count)) {
            recyclerViewItems.add((i + 1).toString())
        }
                recyclerViewItems.add("Проверка")
                recyclerViewItems.add("Удалить ответы")
            }

    fun goToActivity(position: Int,answerText: String) {  //обработка нажатия на recycleritem. если нажали на номер, то открывается номер и переадается ответ, который мог быть сохранен ранее
        val intent: Intent
        when (position) {
            resources.getInteger(R.integer.exercisesInTrialVariants_count) -> {
                showAlertDialogCheck()
            }
            resources.getInteger(R.integer.exercisesInTrialVariants_count)+1 -> {
                showAlertDialogDelete()
            }
            else -> {
                intent = Intent(this, TrialVariantsActivity::class.java)
                intent.putExtra("position", position + 1)
                intent.putExtra("answerText",answerText)
                startActivity(intent)
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
                intent = Intent(this, RecyclerViewActivity::class.java)
                intent.putExtra("userSelectedMode","trialVariants")
                startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun checkAnswers() {
        isAnswersChecked = true
        checkedAnswers.clear()
        val userAnswers = getAnswers()  //получаем отсоритрованные ответы пользователя
        val textResourceId = resources.getIdentifier("pr1_1_answers", "string", packageName) //получаем строку с правильными ответами пользователя
        val correctAnswers = resources.getString(textResourceId).split(":") //делим ее на list
        var quantityOfCorrectAnswers = 0
        for (i in correctAnswers.indices) {
            if (correctAnswers[i] == userAnswers[i]) {  //если ответы совпали, то правильно
                quantityOfCorrectAnswers++
                checkedAnswers.add(true)
            }
            else{
                checkedAnswers.add(false)
            }
        }
        Log.d(TAG, quantityOfCorrectAnswers.toString())
        saveToSharePref("pr1_res",quantityOfCorrectAnswers.toString())
        popup(quantityOfCorrectAnswers)  //всплывающее окно где скасзано сколько правильно
        createRecyclerView() //обновить recyclerview чтобы перекрасились элементы
    }
    private fun clearAnswers() {
        val sharedPref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.remove("answers")
        editor.apply()
        isAnswersChecked = false
        createRecyclerView()
    }
    private fun popup(count:Int){
        // Создание объекта PopupWindow
        val popupView = layoutInflater.inflate(R.layout.popup_layout, null)
        val popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        // Настройка параметров всплывающего окна
        popupWindow.isFocusable = true // Разрешить фокус на всплывающем окне
        popupWindow.isOutsideTouchable = false // Разрешить закрытие всплывающего окна при касании за его пределами

        // Отображение всплывающего окна
        val parentView: View = findViewById(R.id.recyclerViewTrialVariants)
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0)

        val textview = popupView.findViewById<TextView>(R.id.popupTextView)
        val button = popupView.findViewById<Button>(R.id.popupButton)
        val string = "Правильных ответов: $count"
        textview.text = string
        button.setOnClickListener {
            popupWindow.dismiss()
        }
    }
    private fun saveToSharePref(name:String,text:String){
        val sharedPref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(name, text)
        editor.apply()
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        intent = Intent(this, RecyclerViewActivity::class.java)
        intent.putExtra("userSelectedMode","trialVariants")
        startActivity(intent)
    }
    private fun showAlertDialogDelete() {  //справшивает подтверждение действия
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Сброс ответов")
            .setMessage("Вы действительно хотите удалить ответы?")
            .setPositiveButton("Да") { dialog, _ ->
                dialog.dismiss()
                clearAnswers()  //очистка ответов
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false) // Запретить закрытие окна при нажатии вне его пределов

        val dialog = builder.create()
        dialog.show()
    }
    private fun showAlertDialogCheck() {  //справшивает подтверждение действия
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Проверка")
            .setMessage("Вы действительно хотите проверить ответы?")
            .setPositiveButton("Да") { dialog, _ ->
                dialog.dismiss()
                checkAnswers() //проверка ответов
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false) // Запретить закрытие окна при нажатии вне его пределов

        val dialog = builder.create()
        dialog.show()
    }
}