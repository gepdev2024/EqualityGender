package com.example.equalitygender.fragments.masyarakat

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.equalitygender.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class CreateReportFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var imageViewMedia: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonSubmit: Button
    private lateinit var editTextTanggalKejadian: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        imageViewMedia = view.findViewById(R.id.imageViewMedia)
        progressBar = view.findViewById(R.id.progressBar)
        buttonSubmit = view.findViewById(R.id.buttonSubmit)
        editTextTanggalKejadian = view.findViewById(R.id.editTextTanggalKejadian)

        view.findViewById<Button>(R.id.buttonUploadMedia).setOnClickListener {
            openFileChooser()
        }

        editTextTanggalKejadian.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showDatePickerDialog()
            }
        }

        buttonSubmit.setOnClickListener {
            if (auth.currentUser != null) {
                saveReport()
            } else {
                // Handle not logged in state
            }
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == android.app.Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageViewMedia.visibility = View.VISIBLE
            Glide.with(this).load(imageUri).into(imageViewMedia)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            editTextTanggalKejadian.setText(dateFormat.format(selectedDate.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun saveReport() {
        val kategori = view?.findViewById<EditText>(R.id.editTextKategori)?.text.toString()
        val tanggalKejadian = editTextTanggalKejadian.text.toString()
        val lokasiKejadian = view?.findViewById<EditText>(R.id.editTextLokasiKejadian)?.text.toString()
        val deskripsiKejadian = view?.findViewById<EditText>(R.id.editTextDeskripsiKejadian)?.text.toString()
        val kodeLaporan = UUID.randomUUID().toString().take(8).toUpperCase()

        if (kategori.isEmpty() || tanggalKejadian.isEmpty() || lokasiKejadian.isEmpty() || deskripsiKejadian.isEmpty() || imageUri == null) {
            // Handle validation
            return
        }

        buttonSubmit.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        val storageRef = storage.reference.child("reports/${UUID.randomUUID()}")
        val uploadTask = imageUri?.let { storageRef.putFile(it) }

        uploadTask?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val report = hashMapOf(
                    "kategori" to kategori,
                    "tanggalKejadian" to tanggalKejadian,
                    "mediaUrl" to uri.toString(),
                    "lokasiKejadian" to lokasiKejadian,
                    "deskripsiKejadian" to deskripsiKejadian,
                    "status" to "Laporan Diproses",
                    "kodeLaporan" to kodeLaporan,
                    "timestamp" to System.currentTimeMillis(),
                    "userId" to auth.currentUser!!.uid
                )

                firestore.collection("reports")
                    .add(report)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Laporan berhasil dikirim", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                    }
                    .addOnCompleteListener {
                        progressBar.visibility = View.GONE
                        buttonSubmit.visibility = View.VISIBLE
                    }
            }
        }?.addOnFailureListener {
            // Handle upload failure
            progressBar.visibility = View.GONE
            buttonSubmit.visibility = View.VISIBLE
        }
    }
}
