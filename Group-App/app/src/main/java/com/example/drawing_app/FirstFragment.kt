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
        val view = inflater.inflate(R.layout.fragment_first, container, false)

        // Set up the button for navigating to the second fragment
        val button: Button = view.findViewById(R.id.goToSecond)
        button.setOnClickListener {
            findNavController().navigate(R.id.action_firstFragment_to_secondFragment)
        }

        // Find the ComposeView and set up Jetpack Compose for displaying the drawing list
        val composeView = view.findViewById<ComposeView>(R.id.composeView)

        // Observe the drawing list from the ViewModel and update the ComposeView content when the list changes
        drawingViewModel.drawingList.observe(viewLifecycleOwner, Observer { drawings ->
            composeView.setContent {
                drawingList(drawings)  // Rename function to drawingList (lowercase)
            }
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FirstFragment", "Fragment view destroyed")
    }
}
