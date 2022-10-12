package com.kanantaghili.mywhatsapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kanantaghili.mywhatsapp.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var info: TextView
    private lateinit var username: EditText
    private lateinit var button: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private fun init() {
        val binding: ActivityForgotPasswordBinding =
            ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        username = binding.usernameForgot
        info = binding.forgotInfo
        button = binding.changerBtn

        firebaseAuth = Firebase.auth
        firebaseFirestore = Firebase.firestore
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun changeClick(v: View?) {
        val username: String = username.text.toString().replace("\\s+".toRegex(), "")

        firebaseAuth
            .sendPasswordResetEmail(username)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    info.text = "Check your email"
                } else {
                    info.text = "Wrong username"
                }
            }
    }
}