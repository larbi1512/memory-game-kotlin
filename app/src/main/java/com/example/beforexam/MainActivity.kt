package com.example.beforexam

import ImageAdapter
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var adapter: ImageAdapter
    private var firstClickedPosition: Int? = null
    private var isClickable = true
    private lateinit var restartButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridView = findViewById(R.id.gridView)
        adapter = ImageAdapter(this)
        gridView.adapter = adapter
        showAllImagesForDelay()


        restartButton = findViewById(R.id.restartButton)
        restartButton.setOnClickListener { restartGame() }

        gridView.setOnItemClickListener(itemClickListener)
    }

    private val itemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        if (isClickable && !adapter.isMatched(position) && firstClickedPosition != position) {
            handleImageClick(position)
        }
    }

    private fun handleImageClick(position: Int) {
        showImage(position)

        if (firstClickedPosition == null) {
            firstClickedPosition = position
        } else {
            isClickable = false // Disable clicks during the delay

            val handler = Handler()
            handler.postDelayed({
                if (adapter.getItem(firstClickedPosition!!) == adapter.getItem(position)) {
                    Toast.makeText(this, "Match!", Toast.LENGTH_SHORT).show()
                    adapter.setMatched(firstClickedPosition!!)
                    adapter.setMatched(position)
                } else {
                    hideImages(firstClickedPosition!!, position)
                }

                firstClickedPosition = null
                isClickable = true

                if (adapter.isGameOver()) {
                    Toast.makeText(this, "Game Over!", Toast.LENGTH_LONG).show()
                    restartButton.visibility = View.VISIBLE
                }
                adapter.notifyDataSetChanged()
            }, 500) // 500 milliseconds delay
        }
    }

    private fun showImage(position: Int) {
        adapter.showImage(position)
    }

    private fun hideImages(position1: Int, position2: Int) {
        adapter.hideImage(position1)
        adapter.hideImage(position2)
    }

    private fun restartGame() {
        // Reset game state
        adapter.resetGame()
        isClickable = true
        restartButton.visibility = View.GONE
        showAllImagesForDelay()
    }

    private fun showAllImagesForDelay() {
        // Show all images in the grid
        adapter.showAllImages()

        // Delay for a certain period (e.g., 3000 milliseconds)
        Handler().postDelayed({
            // Hide all images after the delay
            adapter.hideAllImages()
            // Make the grid clickable
            isClickable = true
        }, 3000) // 3000 milliseconds delay
    }
}
