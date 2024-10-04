package com.example.drawing_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController



class FirstFragment : Fragment() {

    // Get an instance of DrawingViewModel using activityViewModels to share data between fragments
    private val drawingViewModel: DrawingViewModel by activityViewModels()

    // Creates its view hierarchy and inflates the layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                //extracts from here
                drawingListScreen(viewModel = drawingViewModel, onNavigateToDraw = {
                    findNavController().navigate(R.id.action_firstFragment_to_secondFragment)
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FirstFragment", "Fragment view destroyed")
    }
}
