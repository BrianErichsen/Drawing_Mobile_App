package com.example.drawing_app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

class SecondFragment : Fragment() {

    private lateinit var drawingView: DrawingView
    private val drawingViewModel: DrawingViewModel by activityViewModels()

    // creates its view hierarchy and inflates the layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)
        // DrawingView
        drawingView = view.findViewById(R.id.drawing_view)
        drawingView.observeViewModel(drawingViewModel, viewLifecycleOwner)
        drawingViewModel.bitmap.observe(viewLifecycleOwner, Observer { bitmap ->
            bitmap?.let {
                drawingView.setBitMap(it)
            }
        })

        // pinner
        val shapeSpinner: Spinner = view.findViewById(R.id.shape_spinner)
        shapeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val shape = when (position) {
                    0 -> DrawingView.PenShape.CIRCLE
                    1 -> DrawingView.PenShape.SQUARE
                    2 -> DrawingView.PenShape.TRIANGLE
                    3 -> DrawingView.PenShape.OVAL
                    else -> DrawingView.PenShape.CIRCLE
                }
                drawingViewModel.setPenShape(shape)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        val colorButton: Button = view.findViewById(R.id.color_button)
        colorButton.setOnClickListener {
            showColorPickerDialog()
        }


        val sizeSeekBar: SeekBar = view.findViewById(R.id.size_seekbar)
        sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val penSize = progress.toFloat()
                drawingViewModel.setPenSize(penSize)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


        val eraserButton: Button = view.findViewById(R.id.eraser_button)
        eraserButton.setOnClickListener {
            drawingView.enableEraser()
        }

        return view
    }


    private fun showColorPickerDialog() {
        val colors = arrayOf("Red", "Green", "Blue", "Yellow", "Black")
        val colorValues = arrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.BLACK)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose Pen Color")
        builder.setItems(colors) { _, which ->
            drawingViewModel.setPenColor(colorValues[which])
        }
        builder.show()
    }

    override fun onPause() {
        super.onPause()
        drawingViewModel.setBitmap(drawingView.getBitMap()!!)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FirstFragment", "Fragment view destroyed")
    }
}
