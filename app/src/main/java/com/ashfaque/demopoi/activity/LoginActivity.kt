package com.ashfaque.demopoi.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ashfaque.demopoi.utils_folder.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.ashfaque.demopoi.R
import com.ashfaque.demopoi.notification.showNotification
import com.ashfaque.demopoi.utils_folder.Constants
import com.ashfaque.demopoi.utils_folder.Constants.NOTIFICATION_PERMISSION_REQUEST_CODE
import com.ashfaque.demopoi.utils_folder.Utils


class LoginActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            showNotification(this,"Track-It","Welcome Ashfaque")
            startActivity(Intent(this, NavigationActivity::class.java) )
            finish()
        }

}



    override fun onResume() {
        super.onResume()

        if (!Utils.isLocationEnabled(this)) {
           // Utils.showToast(this, "Location is disabled, redirecting to settings...")
            showSimpleDialog()
        }else
        {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                checkLocationPermission()
                checkNotificationPermission()
            }
        }

        checkLocationPermission()
        checkNotificationPermission()
    }

    private fun checkLocationPermission() {

        Utils.logDebug("checkLocationPermission")
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    private fun showSimpleDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location is disabled, redirecting to settings...")

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            openLocationSettings()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Utils.logDebug("Location permission granted")
                } else {
                    Utils.logDebug("Location permission denied")
                }
            }
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Utils.logDebug("Notification permission granted")
                } else {
                    Utils.logDebug("Notification permission denied")
                }
            }
        }
    }

}

