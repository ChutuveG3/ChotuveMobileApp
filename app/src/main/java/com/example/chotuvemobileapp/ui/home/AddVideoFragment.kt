package com.example.chotuvemobileapp.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide

import com.example.chotuvemobileapp.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_add_video.*
const val PICK_VIDEO_REQUEST = 1

class AddVideoFragment : Fragment() {
    private var uri = null as Uri?
    private lateinit var mStorageRef : StorageReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_add_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mStorageRef = FirebaseStorage.getInstance().reference

        // Public visibility by default.
        publicRadioButton.isChecked = true

        UploadButton.setOnClickListener{
            try {
                val riversRef : StorageReference = mStorageRef.child(getFileName(uri!!))
                riversRef.putFile(uri!!)
                    .addOnSuccessListener { taskSnapshot -> // Get a URL to the uploaded content
                        Toast.makeText(context, "OK updated", Toast.LENGTH_LONG).show()
                        // val downloadUrl: Uri = taskSnapshot.getDownloadUrl()
                        findNavController().navigate(R.id.action_addVideoFragment_to_nav_home)
                    }
                    .addOnFailureListener { Exception ->
                        Toast.makeText(context, Exception.message, Toast.LENGTH_LONG).show()
                    }
            } catch (e: NullPointerException ) {
                Toast.makeText(context, "Select file before upload", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
            }
        }

        SelectFileButton.setOnClickListener {
            startSelectVideoActivity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                uri = data!!.data

                Glide.with(requireContext()).load(uri).into(VideoThumbnail)
                // Set file name as default title.
                // VideoTitleInputText.text = (EditText)getFileName(uri!!)
                VideoTitleInputText.setText(getFileName(uri!!), TextView.BufferType.EDITABLE)
            }
        }
    }

    private fun startSelectVideoActivity() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "video/*"
        }
        startActivityForResult(Intent.createChooser(intent, "Select video"), PICK_VIDEO_REQUEST)
    }

    private fun getFileName(uri: Uri) : String {
        var result = null as String?
        if (uri.scheme.equals("content")) {
            val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }

        if (result == null) {
            result = uri.path;
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}
