package com.example.drawing_app

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.flask.colorpicker.ColorPickerView


class SecondFragment : Fragment() {
    private lateinit var drawingView: DrawingView
    private val drawingViewModel: DrawingViewModel by activityViewModels()
    private lateinit var colorPickerView: ColorPickerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)
        drawingView = view.findViewById(R.id.drawingView)
        colorPickerView = view.findViewById(R.id.colorPickerView)

        drawingViewModel.bitmap.observe(viewLifecycleOwner, Observer { bitmap ->
            bitmap?.let {
                drawingView.setBitMap(it)
                Log.d("SecondFragment", "Restoring saved drawing from ViewModel")
            }
        })


        drawingView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                drawingView.addDrawing(event.x, event.y)
                true
            } else {
                false
            }
        }

        view.findViewById<Button>(R.id.penColorButton).setOnClickListener {
            toggleColorPicker()
        }

        colorPickerView.addOnColorSelectedListener { color ->
            drawingView.setPenColor(color)
            colorPickerView.visibility = View.GONE //so it hides after choosing color
        }
        return view
    }

    override fun onPause() {
        super.onPause()
        drawingViewModel.setBitmap(drawingView.getBitMap()!!)
        Log.d("SecondFragment", "Saving drawing state in onPause")
    }

    private fun toggleColorPicker() {
        if (colorPickerView.visibility == View.GONE) {
            colorPickerView.visibility = View.VISIBLE
        } else {
            colorPickerView.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("SecondFragment", "Fragment view destroyed")
    }
}// end of second fragment class implementation