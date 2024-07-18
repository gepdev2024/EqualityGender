package com.example.equalitygender.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.equalitygender.R
import com.example.equalitygender.adapters.AdminInformasiAdapter

import com.example.equalitygender.models.Informasi
import com.google.firebase.firestore.FirebaseFirestore

class AdminInformasiFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adminInformasiAdapter: AdminInformasiAdapter
    private val informasiList = mutableListOf<Informasi>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_informasi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.recyclerViewInformasi)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adminInformasiAdapter = AdminInformasiAdapter(informasiList,
            onEditClick = { informasi -> editInformasi(informasi) },
            onDeleteClick = { informasi -> deleteInformasi(informasi) })
        recyclerView.adapter = adminInformasiAdapter

        fetchInformasi()

        view.findViewById<ImageButton>(R.id.imageButtonCreate).setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fl_wraper, AdminInformasiCreateFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        view.findViewById<Button>(R.id.buttonFilterKategori).setOnClickListener { button ->
            showCategoryMenu(button)
        }
    }

    private fun fetchInformasi(category: String? = null) {
        val query = if (category.isNullOrEmpty()) {
            firestore.collection("informasi")
        } else {
            firestore.collection("informasi").whereEqualTo("kategori", category)
        }

        query.get()
            .addOnSuccessListener { documents ->
                informasiList.clear()
                for (document in documents) {
                    val informasi = document.toObject(Informasi::class.java).apply { id = document.id }
                    informasiList.add(informasi)
                }
                adminInformasiAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun showCategoryMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_kategori, popupMenu.menu)
        popupMenu.menu.add(0, R.id.kategori_semua, 0, "Semua") // Add "Semua" option
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.kategori_semua -> fetchInformasi(null) // Show all categories
                R.id.kategori_kebijakan_dan_hukum -> fetchInformasi("Kebijakan dan Hukum")
                R.id.kategori_edukasi -> fetchInformasi("Edukasi")
                R.id.kategori_kekerasan -> fetchInformasi("Kekerasan")
                R.id.kategori_ekonomi -> fetchInformasi("Ekonomi")
                R.id.kategori_lain_lain -> fetchInformasi("Lain-lain")
                else -> fetchInformasi()
            }
            true
        }
        popupMenu.show()
    }

    private fun editInformasi(informasi: Informasi) {
        // Handle edit informasi
    }

    private fun deleteInformasi(informasi: Informasi) {
        firestore.collection("informasi").document(informasi.id).delete()
            .addOnSuccessListener {
                informasiList.remove(informasi)
                adminInformasiAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Artikel berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Gagal menghapus artikel", Toast.LENGTH_SHORT).show()
            }
    }

}
