package com.vlad.ege_math.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.vlad.ege_math.R
import com.vlad.ege_math.databinding.FragmentRulesBinding

class RulesFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentRulesBinding>(
            inflater, R.layout.fragment_rules, container, false
        )
        val defaultSharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val fontSize = defaultSharedPref.getString("pref_key_font_size", resources.getInteger(R.integer.mediumTextSize).toString()).toString()  //размер шрифта
        binding.rulesTextView.textSize = fontSize.toFloat()
        return binding.root
    }
}