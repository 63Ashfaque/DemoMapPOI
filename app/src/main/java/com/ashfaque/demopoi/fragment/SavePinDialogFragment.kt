package com.ashfaque.demopoi.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.ashfaque.demopoi.R
import com.ashfaque.demopoi.utils_folder.Utils
import com.ashfaque.demopoi.utils_folder.Utils.isValidDate
import com.ashfaque.demopoi.databinding.FragmentSavePinDialogBinding
import com.ashfaque.demopoi.roomdb.DataBaseName
import com.ashfaque.demopoi.roomdb.EntityDataClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavePinDialogFragment(private val entityDataClass: EntityDataClass,
                            private val isEdit:Boolean,
                            private val onDismissCallback: (() -> Unit)? = null) : DialogFragment() {

    private var _binding: FragmentSavePinDialogBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var dataBase: DataBaseName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.TransparentDialog)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSavePinDialogBinding.inflate(inflater, container, false)

        dataBase = DataBaseName.getDataBase(requireContext())

        mBinding.dialogTitle.text = "Save this Pin?"
        mBinding.edLatLong.setText("${entityDataClass.lat},${entityDataClass.lng}")

        if(isEdit)
        {
            mBinding.edTitle.setText(entityDataClass.title)
            mBinding.edOwnerName.setText(entityDataClass.ownerName)
            mBinding.TagName.setText(entityDataClass.tag)
            mBinding.edEstablishedDate.setText(entityDataClass.establishedDate)
            mBinding.edLocationName.setText(entityDataClass.locationName)
        }

        mBinding.saveButton.setOnClickListener {

            if(mBinding.edTitle.text.toString().isEmpty())
            {
                Utils.showToast(requireContext(), "Please enter Title")
            }
            else if(mBinding.edOwnerName.text.toString().isEmpty())
            {
                Utils.showToast(requireContext(), "Please enter Owner Name")
            }
            else if(mBinding.edLocationName.text.toString().isEmpty())
            {
                Utils.showToast(requireContext(), "Please enter Location Name")
            }
            else if(mBinding.TagName.text.toString().isEmpty())
            {
                Utils.showToast(requireContext(), "Please enter Tag Name")
            }
            else if(mBinding.edEstablishedDate.text.toString().isEmpty())
            {
                Utils.showToast(requireContext(), "Please enter Established Date")
            }
            else if(mBinding.edLatLong.text.toString().isEmpty())
            {
                Utils.showToast(requireContext(), "Please enter Lat Long")
            }else if (isValidDate(mBinding.edEstablishedDate.text.toString())) {

                if(isEdit)
                {
                    val value= EntityDataClass(
                        entityDataClass.id,
                        mBinding.edTitle.text.toString(),
                        mBinding.edOwnerName.text.toString(),
                        mBinding.TagName.text.toString(),
                        mBinding.edEstablishedDate.text.toString(),
                        mBinding.edLocationName.text.toString(),
                        entityDataClass.lat,
                        entityDataClass.lng,
                        entityDataClass.createdDate,
                    )
                    insertUpdatePOI(value)
                }
                else
                {
                    val value= EntityDataClass(
                        0,
                        mBinding.edTitle.text.toString(),
                        mBinding.edOwnerName.text.toString(),
                        mBinding.TagName.text.toString(),
                        mBinding.edEstablishedDate.text.toString(),
                        mBinding.edLocationName.text.toString(),
                        entityDataClass.lat,
                        entityDataClass.lng,
                        Utils.getCurrentDate("dd/MM/yyyy hh:mm a"),
                    )
                    insertUpdatePOI(value)
                }

                //dismiss()
            } else {
                Utils.showToast(requireContext(), "Please enter a valid date in dd/mm/yyyy format")
            }

        }

        mBinding.cancelButton.setOnClickListener {
            dismiss()
        }

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_dropdown_item_1line, Utils.tagNames)
        mBinding.TagName.setAdapter(adapter)
        mBinding.TagName.setText("Other", false)


        mBinding.edEstablishedDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed here
            }

            override fun afterTextChanged(s: Editable?) {
                // Format the date input if needed
                val input = s.toString()
                if (input.length == 2 && !input.endsWith("/")) {
                    mBinding.edEstablishedDate.setText("$input/")
                    mBinding.edEstablishedDate.setSelection(input.length + 1) // Move cursor to end
                } else if (input.length == 5 && !input.endsWith("/")) {
                    mBinding.edEstablishedDate.setText("$input/")
                    mBinding.edEstablishedDate.setSelection(input.length + 1) // Move cursor to end
                }
            }
        })


        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference to avoid memory leaks
    }

    private fun insertUpdatePOI(entityDataClass:EntityDataClass) {

        Utils.logDebug("entityDataClass.id :- ${entityDataClass.id}")

        CoroutineScope(Dispatchers.IO).launch {

            val result = if(!isEdit) {
                dataBase.interfaceDao().insertData(entityDataClass).toInt()
            } else {
                dataBase.interfaceDao().updateData(entityDataClass)
            }
            Utils.logDebug(result.toString())

            if (result > 0) {
                withContext(Dispatchers.Main) {
                    Utils.showToast(requireContext(),  if (!isEdit) "Insert Successful" else "Update Successful")
                    onDismissCallback?.invoke()
                    dismiss()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Utils.showToast(requireContext(), if (!isEdit)"Already Present" else "Not Update")
                }
            }
        }
    }


}

