package com.vlad.ege_math.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vlad.ege_math.R
import com.vlad.ege_math.databinding.FragmentFeedbackBinding

class FeedbackFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentFeedbackBinding>(
            inflater, R.layout.fragment_feedback, container, false
        )
        binding.buttonFeedBack.setOnClickListener { writeEmail() }
        return binding.root
    }
    private fun writeEmail(){  //отправляем письмо на почту
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL,
            arrayOf(requireContext().resources.getString(R.string.feedbackEmail)))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Сообщение по приложению ЕГЭ Химия")
        val emailAppIntent = Intent.createChooser(intent, "Выберите почтовое приложение")
        emailAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }
}