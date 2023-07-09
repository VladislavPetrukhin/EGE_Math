package com.vlad.ege_math

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewTrialVariantsAdapter(
    private val recyclerViewItems: ArrayList<String>,
    private val recyclerViewActivity: RecyclerViewTrialVariantsActivity,
    private val filledAnswers: ArrayList<String>,
    private val isAnswersChecked: Boolean,
    private val checkedAnswers: ArrayList<Boolean>,
    private val context: Context) :
    RecyclerView.Adapter<RecyclerViewTrialVariantsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View, private val recyclerViewActivity: RecyclerViewTrialVariantsActivity,
    private var context: Context,
    private val filledAnswers: ArrayList<String>) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.itemTextViewTrialVariants)
        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                var text = ""
                if (position != RecyclerView.NO_POSITION) {
                    if(position < 29){  //если нажали на номер задания, то тогда надо передать сохранный ответ
                        text = filledAnswers[position]
                    }
                    recyclerViewActivity.goToActivity(position,text)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_item_trial_variants, parent, false)
        return ViewHolder(view,recyclerViewActivity,parent.context,filledAnswers)

    }
    override fun getItemCount(): Int {
        return recyclerViewItems.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: String = recyclerViewItems[position]
        holder.textView.text = item
        Log.d("RecyclerViewLog", position.toString())
        val colorRes = if (isAnswersChecked) {  //если проверка выполнена, тогда меняем цвет на зеленый или красный (правильно решен номер или нет)
            if (position < checkedAnswers.size && checkedAnswers[position]) {
                R.color.positive_recycle_item_color
            } else if(position < 29) {
                R.color.negative_recycle_item_color
            }else{  //кнопки проверки и очистки не трогаем. белый или серый в зависимости от темы
                if(isDarkTheme(context)){
                    R.color.default_dark_recycle_item_color
                }else{
                    R.color.default_light_recycle_item_color
                }
            }
        } else { //если проверка не выполнена, тогда желтый если есть сохраненный ответ. если ответа нет, тогда дефолтный цвет в зависимости от темы
            if (position < filledAnswers.size && filledAnswers[position].isNotEmpty()) {
                R.color.neutral_recycle_item_color
            } else {
                if(isDarkTheme(context)){
                    R.color.default_dark_recycle_item_color
                }else{
                    R.color.default_light_recycle_item_color
                }
            }
        }
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, colorRes))
    }
    private fun isDarkTheme(context: Context): Boolean {
        return context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

}