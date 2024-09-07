package com.example.drawing_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment


class FirstFragment : Fragment() {
    // creates its view hierarchy and inflates the layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        val but: Button = view.findViewById(R.id.goToSecond)
        //sets listener for button to go to second fragment when clicked
        but.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SecondFragment())
                .addToBackStack(null)
                .commit()
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FirstFragment", "Fragment view destroyed")
    }
}//end of class first fragment