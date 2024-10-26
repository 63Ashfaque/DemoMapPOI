package com.ashfaque.demopoi

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.ashfaque.demopoi.databinding.ActivityNavigationBinding
import com.ashfaque.demopoi.fragment.ListsFragment
import com.ashfaque.demopoi.fragment.PoiMapFragment
import com.ashfaque.demopoi.fragment.ProfileFragment

class NavigationActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityNavigationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_navigation)
        mBinding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Default fragment to show on app start
        if (savedInstanceState == null) {
            mBinding.chipNavigationBar.setItemSelected(R.id.nav_map, true)
            loadFragment(PoiMapFragment())
        }

        // Set item selection listener for ChipNavigationBar
        mBinding.chipNavigationBar.setOnItemSelectedListener { id ->
            var fragment: Fragment? = null
            when (id) {
                R.id.nav_map -> fragment = PoiMapFragment()
                R.id.nav_checklist -> fragment = ListsFragment()
                R.id.nav_profile -> fragment = ProfileFragment()
            }
            fragment?.let {
                loadFragment(it)
            }
        }

    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}