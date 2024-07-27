package com.example.equalitygender.fragments.admin

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

class AdminInformasiCreateFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var kategoriSpinner: Spinner
    private lateinit var imagePreview: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonSubmit: Button
    private lateinit var inputTanggal: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_informasi_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        kategoriSpinner = view.findViewById(R.id.spinner_kategori)
        imagePreview = view.findViewById(R.id.image_preview)
        progressBar = view.findViewById(R.id.progress_bar)
        buttonSubmit = view.findViewById(R.id.button_submit)
        inputTanggal = view.findViewById(R.id.input_tanggal)

        setupSpinner()

        view.findViewById<Button>(R.id.button_upload_gambar).setOnClickListener {
            openFileChooser()
        }

        buttonSubmit.setOnClickListener {
            if (auth.currentUser != null) {
                saveInformation()
            } else {
                Toast.makeText(requireContext(), "Please log in to submit information", Toast.LENGTH_SHORT).show()
            }
        }

        inputTanggal.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDatePickerDialog()
            }
        }

        inputTanggal.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun setupSpinner() {
        val categories = arrayOf("Pilih Kategori", "Kebijakan dan Hukum", "Edukasi", "Kekerasan", "Ekonomi", "Lain-lain")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kategoriSpinner.adapter = adapter
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            inputTanggal.setText(dateFormat.format(selectedDate.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
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
            imagePreview.visibility = View.VISIBLE
            Glide.with(this).load(imageUri).into(imagePreview)
        }
    }

    private fun saveInformation() {
        val title = view?.findViewById<EditText>(R.id.input_judul)?.text.toString()
        val category = kategoriSpinner.selectedItem.toString()
        val date = inputTanggal.text.toString()
        val description = view?.findViewById<EditText>(R.id.input_deskripsi)?.text.toString()

        if (title.isEmpty() || category.isEmpty() || date.isEmpty() || description.isEmpty() || imageUri == null) {
            Toast.makeText(requireContext(), "Please fill out all fields and upload an image", Toast.LENGTH_SHORT).show()
            return
        }

        buttonSubmit.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        val storageRef = storage.reference.child("images/${UUID.randomUUID()}")
        val uploadTask = imageUri?.let { storageRef.putFile(it) }

        uploadTask?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val info = hashMapOf(
                    "judul" to title,
                    "kategori" to category,
                    "tanggal" to date,
                    "gambar" to uri.toString(),
                    "deskripsi" to description
                )

                firestore.collection("informasi")
                    .add(info)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Artikel berhasil ditambah!", Toast.LENGTH_SHORT).show()
                        clearFields()
                        parentFragmentManager.popBackStack()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error adding article: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }.addOnCompleteListener {
                progressBar.visibility = View.GONE
                buttonSubmit.visibility = View.VISIBLE
            }
        }?.addOnFailureListener {
            Toast.makeText(requireContext(), "Image upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            buttonSubmit.visibility = View.VISIBLE
        }
    }

    private fun clearFields() {
        view?.findViewById<EditText>(R.id.input_judul)?.text?.clear()
        inputTanggal.text?.clear()
        view?.findViewById<EditText>(R.id.input_deskripsi)?.text?.clear()
        imagePreview.visibility = View.GONE
        imageUri = null
    }
}
