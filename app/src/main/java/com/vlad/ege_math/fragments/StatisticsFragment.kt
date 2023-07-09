package com.vlad.ege_math.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.vlad.ege_math.R
import com.vlad.ege_math.databinding.FragmentStatisticsBinding
import kotlin.math.round

class StatisticsFragment : Fragment() {

    lateinit var binding: FragmentStatisticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_statistics, container, false
        )
        checkTestsRes()
        checkTrialVariantsRes()
        binding.deleteResults.setOnClickListener {
            showAlertDialog()
        }
        return binding.root
    }

    private fun deleteResults() {  //удаляет сохраненные в sharepref результаты
        val sharedPref = requireContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        for (i in 1..resources.getInteger(R.integer.test_count)){   //все тесты
            editor.remove("test${i}_done")
        }
        editor.remove("pr1_res")  //пробник
        editor.apply()
    }

    private fun checkTestsRes(){  //проверяет статистику по тестам
        val countTests = resources.getInteger(R.integer.test_count)
        var countDoneTests = 0
        var testRes:Boolean
        var string = ""
        val sharedPref = requireContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        for (i in 1..countTests){
            testRes = sharedPref.getBoolean("test${i}_done",false)
            if(testRes){    //проверяем выполнен ли тест, добавляем в строку соответсвующий текст
                string += "Тест №$i - Выполнено \n"
                countDoneTests++
            }else{
                string += "Тест №$i - Не выполнено \n"
            }
            binding.testResTextView.text = string  //выставляем полученную строку в textview
        }
        val percentDoneTests = countDoneTests*100 / countTests  //определяем процент сделанных тестов
        string = "Тесты: ${percentDoneTests}%"
        binding.testResTitleTextView.text = string
    }
    private fun checkTrialVariantsRes(){  //проверяет пробники
        val sharedPref = requireContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val pr1Res = sharedPref.getString("pr1_res","-1")?.toInt()
        val string = if(pr1Res == -1){
            "Первый пробник: не приступали"
        }else{
            "Первый пробник: ${pr1Res}/29 правильно \n"
        }
        binding.probnikResTextView.text = string
    }
    private fun showAlertDialog() {  //справшивает действительно ли сбросить статичтику
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Сброс статистики")
            .setMessage("Вы действительно хотите сбросить статистику?")
            .setPositiveButton("Да") { dialog, _ ->
                dialog.dismiss()
                deleteResults()  //удаляем статистику
                checkTestsRes()  //обновляем результаты
                checkTrialVariantsRes()
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false) // Запретить закрытие окна при нажатии вне его пределов

        val dialog = builder.create()
        dialog.show()
    }
}