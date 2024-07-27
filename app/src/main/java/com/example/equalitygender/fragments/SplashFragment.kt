package com.example.equalitygender.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.equalitygender.Admin
import com.example.equalitygender.Masyarakat
import com.example.equalitygender.R
import com.google.firebase.auth.FirebaseAuth


class SplashFragment : Fragment() {


    private lateinit var mAuth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        val isLogin: Boolean = mAuth.currentUser != null

        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({

            if (isLogin) {
//                val intent = Intent(activity, Admin::class.java)
//                startActivity(intent)
//                activity?.finish()
                navController.navigate(R.id.action_splashFragment_to_loginFragment)

            }else
            navController.navigate(R.id.action_splashFragment_to_loginFragment)

//        }, 2000)
    }, 10)

    }

    private fun init(view: View) {
        mAuth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)
    }
}