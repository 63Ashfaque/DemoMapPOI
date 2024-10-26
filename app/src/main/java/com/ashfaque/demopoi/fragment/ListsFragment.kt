package com.ashfaque.demopoi.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.ashfaque.demopoi.R
import com.ashfaque.demopoi.utils_folder.Utils
import com.ashfaque.demopoi.adapter.MyAdapter
import com.ashfaque.demopoi.databinding.FragmentListsBinding
import com.ashfaque.demopoi.notification.showNotification
import com.ashfaque.demopoi.roomdb.DataBaseName
import com.ashfaque.demopoi.roomdb.EntityDataClass
import com.ashfaque.demopoi.roomdb.generateGeoJson
import com.ashfaque.demopoi.shared_preference.SharedPreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListsFragment : Fragment(),MyAdapter.MyClickListener  {

    private var mBinding: FragmentListsBinding? = null
    private lateinit var dataBase: DataBaseName
    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentListsBinding.inflate(inflater, container, false)
        dataBase = DataBaseName.getDataBase(requireContext())
        sharedPreferenceManager = SharedPreferenceManager.getInstance(requireActivity())


        mBinding!!.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                dataBase.interfaceDao().searchRecordBy(query).observe(viewLifecycleOwner) { results ->
                   manageRecyclerView(results)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        dataBase.interfaceDao().getAllRecord().observe(viewLifecycleOwner) { results ->
            manageRecyclerView(results)
        }

        mBinding!!.cdFilter.setOnClickListener {
            showCustomPopup(mBinding!!.cdFilter)
        }

        return mBinding!!.root
    }

    private fun manageRecyclerView(results: List<EntityDataClass>?) {

        if (results.isNullOrEmpty()) {
            // Show "No data found" message
            mBinding?.tvNoDataFound?.visibility = View.VISIBLE
            mBinding?.recyclerView?.visibility = View.GONE
            mBinding?.fabSync?.visibility = View.GONE
        } else {
            // Hide "No data found" message
            mBinding?.tvNoDataFound?.visibility = View.GONE
            mBinding?.recyclerView?.visibility = View.VISIBLE
            mBinding?.fabSync?.visibility = View.VISIBLE

            // Set adapter with results
            val adapter = MyAdapter(results, this@ListsFragment)
            mBinding?.recyclerView?.adapter = adapter
        }

    }

    private fun showCustomPopup(anchor: View) {
        val dialog = Dialog(anchor.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_custom_filter_popup)

        val listView = dialog.findViewById<ListView>(R.id.listViewTags)

        val adapter = ArrayAdapter(
            anchor.context,
            android.R.layout.simple_dropdown_item_1line,
            Utils.tagNames
        )
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedTag = Utils.tagNames[position]
            dataBase.interfaceDao().searchRecordBy(selectedTag).observe(viewLifecycleOwner) { results ->
                manageRecyclerView(results)
            }
            dialog.dismiss()
        }

        dialog.window?.apply {
            val location = IntArray(2)
            anchor.getLocationOnScreen(location)
            setGravity(Gravity.TOP or Gravity.START)
            attributes = attributes.apply {
                x = location[0]
                y = location[1] + anchor.height
            }
            setBackgroundDrawableResource(android.R.color.transparent)
        }

        dialog.show()
    }


    override fun onUpdateItemClick(item: EntityDataClass) {
        //Utils.showToast(requireContext(),"onUpdateItemClick")
        val dialogFragment = SavePinDialogFragment(item,true)
        dialogFragment.show(parentFragmentManager, "SavePinDialog")
    }

    override fun onDeleteItemClick(item: EntityDataClass) {
//        Utils.showToast(requireContext(),"onDeleteItemClick")
        CoroutineScope(Dispatchers.IO).launch {
            val result= dataBase.interfaceDao().deleteData(item)
            if (result > 0) {
                withContext(Dispatchers.Main) {
                    Utils.showToast(requireContext(),  "Item Delete")

                    showNotification(requireContext(), item.title,
                         "Item Delete")
                }
            } else {
                withContext(Dispatchers.Main) {
                    Utils.showToast(requireContext(), "Not Delete")
                }
            }
        }
    }

    override fun onGeoJsonItemClick(item: EntityDataClass) {

        val geoJsonResult = generateGeoJson(item)
        Utils.logDebug(geoJsonResult)
        showCopyDialog(geoJsonResult)
    }

    private fun showCopyDialog(text: String) {
        // Create an AlertDialog
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // Inflate the dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_copy_to_clipboard, null)
        dialogBuilder.setView(dialogView)

        // Get references to the TextView and Button
        val textView = dialogView.findViewById<TextView>(R.id.tvText)
        val copyButton = dialogView.findViewById<Button>(R.id.btnCopy)

        // Set the text to the TextView
        textView.text = text

        // Set up the copy button click listener
        copyButton.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        // Create and show the dialog
        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Set transparent background
        alertDialog.show()
    }

}