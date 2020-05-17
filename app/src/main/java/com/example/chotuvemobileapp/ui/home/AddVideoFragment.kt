package com.example.chotuvemobileapp.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.data.videos.Video
import com.example.chotuvemobileapp.data.videos.VideoDataSource
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_add_video.*
import java.time.LocalDateTime

class AddVideoFragment : Fragment() {

    private val PICK_VIDEO_REQUEST = 1
    private var uri = null as Uri?
    private var fileSize = null as String?
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

        AddVideoToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_32dp)
        AddVideoToolbar.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_addVideoFragment_to_nav_home)
        }

        UploadButton.alpha = .5F
        UploadButton.isEnabled = false
        UploadVideoProgressBar.visibility = View.INVISIBLE

        // Public visibility by default.
        publicRadioButton.isChecked = true

        UploadButton.setOnClickListener{
            try {
                AddVideoScreen.alpha = 0.2F
                AddVideoScreen.isClickable = false
                UploadVideoProgressBar.visibility = View.VISIBLE
                val fileName = getFileName(uri!!)
                val storageReference : StorageReference = mStorageRef.child(fileName)
                storageReference.putFile(uri!!)
                    .addOnSuccessListener {   // Get a URL to the uploaded content
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            val visibility: String = if (publicRadioButton.isChecked) "public"
                            else "private"
                            val videoToSend = Video(VideoTitleInputText.text.toString(),
                                VideoDescriptionInputText.text.toString(),
                                visibility,
                                uri.toString(),
                                LocalDateTime.now().toString(),
                                fileName,
                                fileSize!!
                            )

                            VideoDataSource.addVideo(videoToSend){
                                when(it){
                                    "Success" ->{
                                        UploadVideoProgressBar.visibility = View.GONE
                                        AddVideoScreen.alpha = 1F
                                        Toast.makeText(context, getString(R.string.video_uploaded_message), Toast.LENGTH_LONG).show()
                                        findNavController().navigate(R.id.action_addVideoFragment_to_nav_home)
                                    }
                                    else -> {
                                        fail()
                                    }
                                }
                            }
                        }.addOnFailureListener{
                            fail()
                        }
                    }
                    .addOnFailureListener { Exception ->
                        Toast.makeText(context, Exception.message, Toast.LENGTH_LONG).show()
                        fail()
                    }
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
                UploadButton.isEnabled = true
                UploadButton.alpha = 1F
                uri = data!!.data

                fileSize = context?.let { getFileSize(it, uri) }

                Glide.with(requireContext()).load(uri).into(SelectFileButton)

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
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private  fun fail(){
        UploadVideoProgressBar.visibility = View.GONE
        AddVideoScreen.alpha = 1F
        AddVideoScreen.isClickable = true
    }

    private fun getFileSize(context: Context, uri: Uri?): String? {
        var fileSize: String? = null
        val cursor = context.contentResolver
            .query(uri!!, null, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getString(sizeIndex)
                }
            }
        } finally {
            cursor!!.close()
        }
        return fileSize
    }

}
