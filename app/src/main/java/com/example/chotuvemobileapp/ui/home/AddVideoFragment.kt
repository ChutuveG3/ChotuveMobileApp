package com.example.chotuvemobileapp.ui.home

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide

import com.example.chotuvemobileapp.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_add_video.*

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

        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "video/*"
        }
        startActivityForResult(Intent.createChooser(intent, "Select video"), 0)

        UploadButton.setOnClickListener{
            val riversRef : StorageReference = mStorageRef.child(FileNameText.text.toString())

            riversRef.putFile(uri!!)
                .addOnSuccessListener { taskSnapshot -> // Get a URL to the uploaded content
                    Toast.makeText(context, "OK updated", Toast.LENGTH_LONG).show()
                    // val downloadUrl: Uri = taskSnapshot.getDownloadUrl()
                    findNavController().navigate(R.id.action_addVideoFragment_to_nav_home)
                }
                .addOnFailureListener { Exception ->
                    val errorMessage = Exception.message
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            uri = data!!.data
        }
        Glide.with(requireContext()).load(uri).into(VideoThumbnail)
        FileNameText.text = getFileName(uri!!)
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
