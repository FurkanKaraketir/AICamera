@file:Suppress("DEPRECATION")

package com.furkankrktr.aicamera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var secilenGorsel: Uri? = null
    private var secilenBitmap: Bitmap? = null

    private var resultList = ArrayList<ResultModel>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = ResultAdapter(resultList)
        imageView.setOnClickListener {


            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //izin verilmedi, iste
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                    ),
                    1
                )

            } else {
                //izin var
                CropImage.activity().start(this)

            }
        }

        buttonAi.setOnClickListener {

            resultList.clear()

            if (secilenBitmap != null) {
                val image: InputImage

                try {
                    image = InputImage.fromBitmap(secilenBitmap!!, 0)

                    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

                    labeler.process(image)

                        .addOnSuccessListener { labels ->
                            // Task completed successfully
                            for (label in labels) {
                                val text = label.text
                                println(text)
                                val confidence = label.confidence
                                println(confidence)
                                val gelenResult = ResultModel(text, confidence)
                                resultList.add(gelenResult)
                            }

                            recyclerView.adapter?.notifyDataSetChanged()

                        }
                        .addOnFailureListener { e ->
                            // Task failed with an exception
                            e.printStackTrace()
                        }
                } catch (e: IOException) {
                    e.printStackTrace()
                }


            } else {
                Toast.makeText(this, "Choose an Image", Toast.LENGTH_SHORT).show()
            }


        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        //İzin Yeni Verildi
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.activity().start(this)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == RESULT_OK) {
                secilenGorsel = result.uri


                if (Build.VERSION.SDK_INT >= 28) {
                    val sources = ImageDecoder.createSource(this.contentResolver, secilenGorsel!!)
                    secilenBitmap = ImageDecoder.decodeBitmap(sources)
                    imageView.setImageBitmap(secilenBitmap)


                } else {
                    secilenBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, secilenGorsel)
                    imageView.setImageBitmap(secilenBitmap)
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val e = result.error
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()

            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}