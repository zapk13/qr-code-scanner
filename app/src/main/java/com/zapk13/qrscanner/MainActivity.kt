package com.zapk13.qrscanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zapk13.qrscanner.data.HistoryRepository
import com.zapk13.qrscanner.databinding.ActivityMainBinding
import com.zapk13.qrscanner.scanner.QRCodeAnalyzer
import com.zapk13.qrscanner.util.LinkUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var historyRepository: HistoryRepository
    private var cameraExecutor: ExecutorService? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var isScanPaused = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showScanner()
            startCamera()
        } else {
            showPermissionPanel()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyRepository = HistoryRepository(this)
        historyRepository.getAll()

        binding.toolbar.setOnMenuItemClickListener(::onMenuItemClick)
        binding.grantPermissionButton.setOnClickListener {
            requestCameraPermission()
        }

        if (hasCameraPermission()) {
            showScanner()
        } else {
            showPermissionPanel()
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasCameraPermission() && !isScanPaused) {
            startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor?.shutdown()
        cameraExecutor = null
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun showPermissionPanel() {
        binding.permissionPanel.isVisible = true
        binding.previewView.isVisible = false
        binding.scanFrame.isVisible = false
        binding.hintText.isVisible = false
        binding.overlayTop.isVisible = false
        binding.overlayBottom.isVisible = false
        binding.overlayLeft.isVisible = false
        binding.overlayRight.isVisible = false
    }

    private fun showScanner() {
        binding.permissionPanel.isVisible = false
        binding.previewView.isVisible = true
        binding.scanFrame.isVisible = true
        binding.hintText.isVisible = true
        binding.overlayTop.isVisible = true
        binding.overlayBottom.isVisible = true
        binding.overlayLeft.isVisible = true
        binding.overlayRight.isVisible = true
    }

    private fun startCamera() {
        if (!hasCameraPermission() || isScanPaused) return

        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            cameraProvider = future.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        val provider = cameraProvider ?: return
        provider.unbindAll()

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = binding.previewView.surfaceProvider
        }

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(getCameraExecutor(), QRCodeAnalyzer(::onQrCodeScanned))
            }

        provider.bindToLifecycle(
            this,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            analysis
        )
    }

    private fun stopCamera() {
        cameraProvider?.unbindAll()
    }

    private fun getCameraExecutor(): ExecutorService {
        if (cameraExecutor == null) {
            cameraExecutor = Executors.newSingleThreadExecutor()
        }
        return cameraExecutor!!
    }

    private fun onQrCodeScanned(content: String) {
        if (isScanPaused) return
        isScanPaused = true
        stopCamera()

        runOnUiThread {
            historyRepository.add(content)
            showScanResultDialog(content)
        }
    }

    private fun showScanResultDialog(content: String) {
        val isUrl = LinkUtils.isOpenableUrl(content)
        val builder = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.scanned_content)
            .setMessage(content)
            .setCancelable(false)
            .setNegativeButton(R.string.dismiss) { _, _ ->
                resumeScanning()
            }

        if (isUrl) {
            builder.setPositiveButton(R.string.open_in_browser) { _, _ ->
                LinkUtils.openInBrowser(this, content)
                resumeScanning()
            }
            builder.setNeutralButton(R.string.copy) { _, _ ->
                LinkUtils.copyToClipboard(this, content)
                resumeScanning()
            }
        } else {
            builder.setPositiveButton(R.string.copy) { _, _ ->
                LinkUtils.copyToClipboard(this, content)
                resumeScanning()
            }
        }

        builder.show()
    }

    private fun resumeScanning() {
        isScanPaused = false
        if (hasCameraPermission()) {
            startCamera()
        }
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            else -> false
        }
    }
}
