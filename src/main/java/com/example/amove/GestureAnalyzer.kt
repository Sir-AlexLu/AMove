package com.example.amove

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerOptions
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class GestureAnalyzer(private val context: Context) {
    private var cameraExecutor: ExecutorService? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var handLandmarker: HandLandmarker? = null

    fun startCamera(previewView: PreviewView) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    context as MainActivity,
                    cameraSelector,
                    preview
                )
                setupHandLandmarker()
                Log.d("AMove", "Camera started successfully")
            } catch (e: Exception) {
                Log.e("AMove", "Camera start failed: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun stopCamera() {
        cameraProvider?.unbindAll()
        cameraExecutor?.shutdown()
        handLandmarker?.close()
        Log.d("AMove", "Camera stopped and resources released")
    }

    private fun setupHandLandmarker() {
        val modelPath = "hand_landmarker.task"
        val file = File(context.filesDir, modelPath)

        if (!File(context.assetsDir, modelPath).exists()) {
            Log.e("AMove", "Model file not found in assets! Please add $modelPath")
            return
        }

        val options = HandLandmarkerOptions.builder()
            .setBaseOptions(
                HandLandmarkerOptions.BaseOptions.builder()
                    .setModelAssetPath(modelPath)
                    .build()
            )
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setMinHandDetectionConfidence(0.5f)
            .setMinTrackingConfidence(0.5f)
            .setResultListener { result, _ -> handleHandResult(result) }
            .build()

        handLandmarker = HandLandmarker.createFromOptions(context, options)
        Log.d("AMove", "HandLandmarker initialized")
    }

    private fun handleHandResult(result: HandLandmarkerResult) {
        if (result.landmarks().isNotEmpty()) {
            val x = result.landmarks()[0][8].x()
            val y = result.landmarks()[0][8].y()
            Log.d("AMove", "Index finger tip at: x=$x, y=$y")

            when {
                x < 0.3 -> GestureAccessibilityService.instance?.performSwipeLeft()
                x > 0.7 -> GestureAccessibilityService.instance?.performSwipeRight()
                y < 0.3 -> GestureAccessibilityService.instance?.performScrollUp()
                y > 0.7 -> GestureAccessibilityService.instance?.performScrollDown()
                else -> GestureAccessibilityService.instance?.performTap()
            }
        }
    }
}
