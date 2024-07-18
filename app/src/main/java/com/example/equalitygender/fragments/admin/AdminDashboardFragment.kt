package com.example.equalitygender.fragments.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.equalitygender.Admin
import com.example.equalitygender.R
import com.example.equalitygender.fragments.masyarakat.InformasiFragment
import com.example.equalitygender.fragments.masyarakat.LaporFragment

class AdminDashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false)

        val button2: Button = view.findViewById(R.id.button2)
        val button21: Button = view.findViewById(R.id.button21)
        val button22: Button = view.findViewById(R.id.button22)

        button2.setOnClickListener {
            navigateToFragment(LaporFragment(), R.id.laporan)
        }

        button21.setOnClickListener {
            navigateToFragment(LaporFragment(), R.id.laporan)
        }

        button22.setOnClickListener {
            navigateToFragment(InformasiFragment(), R.id.informasi)
        }

        return view
    }

    private fun navigateToFragment(fragment: Fragment, menuItemId: Int) {
        (activity as? Admin)?.let {
            it.makeCurrentFragment(fragment)
            it.setActiveMenuItem(menuItemId)
        }
    }
}
