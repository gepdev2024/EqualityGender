package com.example.equalitygender.fragments.masyarakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.equalitygender.R
import com.example.equalitygender.adapters.masyarakat.MasyarakatInformasiAdapter
import com.example.equalitygender.models.Informasi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore

class InformasiFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var informasiAdapter: MasyarakatInformasiAdapter
    private val informasiList = mutableListOf<Informasi>()
    private val allInformasiList = mutableListOf<Informasi>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_informasi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.recyclerViewInformasi)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        informasiAdapter = MasyarakatInformasiAdapter(informasiList)
        recyclerView.adapter = informasiAdapter

        view.findViewById<Button>(R.id.buttonFilterKategori).setOnClickListener {
            showKategoriDialog()
        }

        fetchInformasi()
    }

    private fun fetchInformasi() {
        firestore.collection("informasi").get()
            .addOnSuccessListener { documents ->
                informasiList.clear()
                allInformasiList.clear()
                for (document in documents) {
                    val informasi = document.toObject(Informasi::class.java).apply { id = document.id }
                    informasiList.add(informasi)
                    allInformasiList.add(informasi)
                }
                informasiAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun showKategoriDialog() {
        val categories = arrayOf("Semua", "Kebijakan dan Hukum", "Edukasi", "Kekerasan", "Ekonomi", "Lain-lain")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Kategori")
            .setItems(categories) { dialog, which ->
                val selectedCategory = categories[which]
                filterInformasiByKategori(selectedCategory)
            }
            .show()
    }

    private fun filterInformasiByKategori(category: String) {
        informasiList.clear()
        if (category == "Semua") {
            informasiList.addAll(allInformasiList)
        } else {
            informasiList.addAll(allInformasiList.filter { it.kategori == category })
        }
        informasiAdapter.notifyDataSetChanged()
    }
}
