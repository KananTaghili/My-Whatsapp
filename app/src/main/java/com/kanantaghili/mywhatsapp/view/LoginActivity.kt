package com.kanantaghili.mywhatsapp.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kanantaghili.mywhatsapp.R
import com.kanantaghili.mywhatsapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameLogin: EditText
    private lateinit var passwordLogin: EditText
    private lateinit var info: TextView
    private lateinit var checkBox: CheckBox
    private lateinit var firebaseAuth: FirebaseAuth

    private fun init() {
        val binding: ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        usernameLogin = binding.usernameLogin
        passwordLogin = binding.passwordLogin
        info = binding.loginInfo
        checkBox = binding.checkBox

        firebaseAuth = Firebase.auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        checkbox()

        this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
            }
        })
    }

    private fun checkbox() {
        checkBox.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                checkBox.setText(R.string.hide)
                passwordLogin.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                checkBox.setText(R.string.show)
                passwordLogin.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordLogin.setSelection(passwordLogin.text!!.length)
        }
        usernameLogin.post { usernameLogin.requestFocus() }
    }

    fun forgotClick(v: View?) {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    fun enterClick(v: View?) {
        val username = usernameLogin.text.toString().replace("\\s+".toRegex(), "")
        val password = passwordLogin.text.toString().replace("\\s+".toRegex(), "")

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            info.setText(R.string.minimum_5_characters)
            return
        }

        firebaseAuth
            .signInWithEmailAndPassword(username, password)
            .addOnSuccessListener {
                val intent = Intent(this, UsersActivity::class.java)
                startActivity(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }
}