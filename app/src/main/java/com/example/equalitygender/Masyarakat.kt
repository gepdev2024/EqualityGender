package com.example.equalitygender

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.equalitygender.fragments.masyarakat.BerandaFragment
import com.example.equalitygender.fragments.masyarakat.BerbagiFragment
import com.example.equalitygender.fragments.masyarakat.InformasiFragment
import com.example.equalitygender.fragments.masyarakat.LaporFragment
import com.example.equalitygender.fragments.masyarakat.ProfilFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment



class Masyarakat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.masyarakat_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            insets
        }
        val berandaFragment = BerandaFragment()
        val informasiFragment = InformasiFragment()
        val berbagiFragment = BerbagiFragment()
        val laporFragment = LaporFragment()
        val profilFragment = ProfilFragment()

        makeCurrentFragment(berandaFragment)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener{
            when (it.itemId){
                R.id.beranda -> makeCurrentFragment(berandaFragment)
                R.id.informasi -> makeCurrentFragment(informasiFragment)
                R.id.berbagi -> makeCurrentFragment(berbagiFragment)
                R.id.lapor -> makeCurrentFragment(laporFragment)
                R.id.profil -> makeCurrentFragment(profilFragment)
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wraper, fragment)
            commit()
        }
}