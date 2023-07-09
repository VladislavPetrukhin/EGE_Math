package com.vlad.ege_math.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import com.vlad.ege_math.R
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vlad.ege_math.databinding.FragmentMotivationBinding


class MotivationFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentMotivationBinding>(
            inflater, R.layout.fragment_motivation, container, false
        )
        binding.videoView.setVideoPath("android.resource://" + requireContext().packageName + "/" + R.raw.video)

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)
        binding.videoView.start()
        return binding.root
    }
}