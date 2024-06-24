package com.example.equalitygender

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.equalitygender.fragments.BerandaFragment
import com.example.equalitygender.fragments.BerbagiFragment
import com.example.equalitygender.fragments.InformasiFragment
import com.example.equalitygender.fragments.LaporFragment
import com.example.equalitygender.fragments.ProfilFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
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