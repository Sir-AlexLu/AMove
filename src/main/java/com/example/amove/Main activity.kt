package com.example.amove

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.amove.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var gestureAnalyzer: GestureAnalyzer? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Log.d("AMove", "Camera permission granted")
            } else {
                Log.e("AMove", "Camera permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.startButton.setOnClickListener {
            Log.d("AMove", "Starting gesture detection")
            gestureAnalyzer = GestureAnalyzer(this).also {
                it.startCamera(binding.previewView)
            }
        }

        binding.stopButton.setOnClickListener {
            Log.d("AMove", "Stopping gesture detection")
            gestureAnalyzer?.stopCamera()
        }
    }
}
