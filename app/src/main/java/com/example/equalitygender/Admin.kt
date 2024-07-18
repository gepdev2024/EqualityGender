package com.example.equalitygender

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.equalitygender.fragments.admin.AdminDashboardFragment
import com.example.equalitygender.fragments.admin.AdminInformasiFragment
import com.example.equalitygender.fragments.admin.AdminLaporanFragment
import com.example.equalitygender.fragments.admin.AdminProfilFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment

class Admin : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.admin_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            insets
        }

        val dashboardFragment = AdminDashboardFragment()
        val informasiFragment = AdminInformasiFragment()
        val laporanFragment = AdminLaporanFragment()
        val profilFragment = AdminProfilFragment()

        makeCurrentFragment(dashboardFragment)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.dashboard -> makeCurrentFragment(dashboardFragment)
                R.id.informasi -> makeCurrentFragment(informasiFragment)
                R.id.laporan -> makeCurrentFragment(laporanFragment)
                R.id.profil -> makeCurrentFragment(profilFragment)
            }
            true
        }
    }

    fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wraper, fragment)
            commit()
        }
    }

    fun setActiveMenuItem(itemId: Int) {
        bottomNavigation.selectedItemId = itemId
    }
}
