package com.ashfaque.demopoi

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ashfaque.demopoi.Constants.LOCATION_PERMISSION_REQUEST_CODE


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
            }
        }
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
}

