package com.kanantaghili.mywhatsapp.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kanantaghili.mywhatsapp.R
import com.kanantaghili.mywhatsapp.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var info: TextView
    private lateinit var newPassword1: EditText
    private lateinit var newPassword2: EditText
    private lateinit var currentPassword: EditText
    private lateinit var visibilitySwitch: SwitchCompat
    private lateinit var button: Button
    private lateinit var enteredNewPassword1: String
    private lateinit var enteredNewPassword2: String
    private lateinit var enteredCurrentPassword: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private fun init() {
        val binding: ActivityChangePasswordBinding =
            ActivityChangePasswordBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        currentPassword = binding.usernameChange
        newPassword1 = binding.newPassword1
        newPassword2 = binding.newPassword2
        visibilitySwitch = binding.switchChange
        info = binding.Info
        button = binding.changerBtn

        firebaseAuth = Firebase.auth
        firebaseFirestore = Firebase.firestore
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        visibilitySwitch()

        this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun visibilitySwitch() {
        visibilitySwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                newPassword1.inputType = InputType.TYPE_CLASS_TEXT
                newPassword2.visibility = View.GONE
            } else {
                newPassword1.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                newPassword2.visibility = View.VISIBLE
            }
        }
    }

    fun changeClick(v: View?) {
        enteredCurrentPassword = currentPassword.text.toString().replace("\\s+".toRegex(), "")
        enteredNewPassword1 = newPassword1.text.toString().replace("\\s+".toRegex(), "")
        enteredNewPassword2 = newPassword2.text.toString().replace("\\s+".toRegex(), "")

        if (TextUtils.isEmpty(enteredCurrentPassword) || TextUtils.isEmpty(enteredNewPassword1) || TextUtils.isEmpty(
                enteredNewPassword2
            ) && !visibilitySwitch.isChecked
        ) {
            info.setText(R.string.entered_information_cant_be_empty)
            return
        }
        if (enteredNewPassword1 == enteredNewPassword2 || visibilitySwitch.isChecked) {
            val user = Firebase.auth.currentUser!!

            val credential = EmailAuthProvider.getCredential(user.email!!, enteredCurrentPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener {
                    user.updatePassword(enteredNewPassword1)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "User password updated", Toast.LENGTH_LONG)
                                    .show()
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    task.exception?.localizedMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
        } else info.setText(R.string.not_same)
    }
}