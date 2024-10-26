package com.ashfaque.demopoi.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ashfaque.demopoi.databinding.FragmentProfileBinding
import com.ashfaque.demopoi.shared_preference.SharedPrefConstants
import com.ashfaque.demopoi.shared_preference.SharedPreferenceManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container,
            false)

        sharedPreferenceManager = SharedPreferenceManager.getInstance(requireActivity())
        val value=sharedPreferenceManager.getFloat(SharedPrefConstants.RADIUS_IN_METER)
        updateSliderValueText(value)

        mBinding.slider.addOnChangeListener { _, value, _ ->
            updateSliderValueText(value)
        }

        return mBinding.root
    }

    @SuppressLint("SetTextI18n")
    private fun updateSliderValueText(value: Float) {
        sharedPreferenceManager.saveFloat(SharedPrefConstants.RADIUS_IN_METER, value)
        mBinding.tv.setText("Radius In Meters $value m")
        mBinding.slider.setValue(value)
    }

}