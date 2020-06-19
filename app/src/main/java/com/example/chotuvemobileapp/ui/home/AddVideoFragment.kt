package com.example.chotuvemobileapp.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chotuvemobileapp.HomeActivity
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.data.videos.Video
import com.example.chotuvemobileapp.data.videos.VideoDataSource
import com.example.chotuvemobileapp.helpers.PickRequest
import com.example.chotuvemobileapp.helpers.Utilities.DATE_FORMAT_LONG
import com.example.chotuvemobileapp.helpers.Utilities.getFileName
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_add_video.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AddVideoFragment : Fragment() {
    private var uri = null as Uri?
    private var fileSize = null as String?
    private var fileName = null as String?
    private val mStorageRef by lazy {
        FirebaseStorage.getInstance().reference
    }
    private val prefs by lazy {
        requireActivity().applicationContext.getSharedPreferences(getString(R.string.shared_preferences_file),
        Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AddVideoToolbar.setNavigationOnClickListener{
            val home=  activity as HomeActivity
            home.openDrawer()
        }

        UploadButton.alpha = .5F
        UploadButton.isEnabled = false
        UploadVideoProgressBar.visibility = View.INVISIBLE
        // Public visibility by default.
        publicRadioButton.isChecked = true

        UploadButton.setOnClickListener{
            try {
                AddVideoScreen.alpha = 0.2F
                requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                UploadVideoProgressBar.visibility = View.VISIBLE

                val storageReference : StorageReference = mStorageRef.child(fileName!!)
                storageReference.putFile(uri!!)
                    .addOnSuccessListener {   // Get a URL to the uploaded content
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            val videoToSend = Video(VideoTitleInputText.text.toString(),
                                VideoDescriptionInputText.text.toString(),
                                if (publicRadioButton.isChecked) "public" else "private",
                                uri.toString(),
                                nowDateTimeStr(),
                                fileName!!,
                                fileSize!!
                            )
                            VideoDataSource.addVideo(videoToSend, prefs){
                                when(it){
                                    "Success" ->{
                                        UploadVideoProgressBar.visibility = View.GONE
                                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                        AddVideoScreen.alpha = 1F
                                        Toast.makeText(context, getString(R.string.video_uploaded_message), Toast.LENGTH_LONG).show()
                                        findNavController().navigate(R.id.nav_home)
                                    }
                                    else -> {
                                        fail()
                                        Toast.makeText(context, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }.addOnFailureListener{ Exception ->
                            Toast.makeText(context, Exception.message, Toast.LENGTH_LONG).show()
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
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "video/*"
            }
            startActivityForResult(Intent.createChooser(intent, "Select Video"), PickRequest.Video.value)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PickRequest.Video.value) {
            if (resultCode == Activity.RESULT_OK) {
                UploadButton.isEnabled = true
                UploadButton.alpha = 1F
                uri = data!!.data

                fileSize = context?.let { getFileSize(it, uri!!) }
                fileName = getFileName(uri!!, requireActivity().contentResolver)

                Glide.with(requireContext()).load(uri).centerCrop().into(SelectFileButton)

                VideoTitleInputText.setText(fileName!!, TextView.BufferType.EDITABLE)
            }
        }
    }

    private  fun fail(){
        UploadVideoProgressBar.visibility = View.GONE
        AddVideoScreen.alpha = 1F
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    @SuppressLint("Recycle")
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

    private fun nowDateTimeStr() : String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_LONG)
        return current.format(formatter)
    }
}
