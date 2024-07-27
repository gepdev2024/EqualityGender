package com.example.equalitygender.fragments.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.equalitygender.Admin
import com.example.equalitygender.Awal
import com.example.equalitygender.R
import com.example.equalitygender.fragments.masyarakat.InformasiFragment
import com.example.equalitygender.fragments.masyarakat.LaporFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboardFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var textViewPendingReports: TextView
    private lateinit var textViewProcessedReports: TextView
    private lateinit var textViewTotalInformation: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        textViewPendingReports = view.findViewById(R.id.textViewPendingReports)
        textViewProcessedReports = view.findViewById(R.id.textViewProcessedReports)
        textViewTotalInformation = view.findViewById(R.id.textViewTotalInformation)

        fetchReportCounts()
        fetchInformationCount()

        val buttonPendingReports: Button = view.findViewById(R.id.buttonPendingReports)
        val buttonProcessedReports: Button = view.findViewById(R.id.buttonProcessedReports)
        val buttonTotalInformation: Button = view.findViewById(R.id.buttonTotalInformation)

        buttonPendingReports.setOnClickListener {
            navigateToFragment(LaporFragment(), R.id.laporan)
        }

        buttonProcessedReports.setOnClickListener {
            navigateToFragment(LaporFragment(), R.id.laporan)
        }

        buttonTotalInformation.setOnClickListener {
            navigateToFragment(InformasiFragment(), R.id.informasi)
        }

        view.findViewById<ImageButton>(R.id.buttonLogout).setOnClickListener {
            logout()
        }
    }

    private fun fetchReportCounts() {
        firestore.collection("reports")
            .whereEqualTo("status", "Laporan Diproses")
            .get()
            .addOnSuccessListener { documents ->
                textViewPendingReports.text = documents.size().toString()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }

        firestore.collection("reports")
            .whereEqualTo("status", "Laporan Disetujui")
            .get()
            .addOnSuccessListener { documents ->
                textViewProcessedReports.text = documents.size().toString()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun fetchInformationCount() {
        firestore.collection("informasi")
            .get()
            .addOnSuccessListener { documents ->
                textViewTotalInformation.text = documents.size().toString()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun navigateToFragment(fragment: Fragment, menuItemId: Int) {
        (activity as? Admin)?.let {
            it.makeCurrentFragment(fragment)
            it.setActiveMenuItem(menuItemId)
        }
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), Awal::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
