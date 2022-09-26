package com.kanantaghili.mywhatsapp.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kanantaghili.mywhatsapp.R
import com.kanantaghili.mywhatsapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var nameRegister: EditText
    private lateinit var surnameRegister: EditText
    private lateinit var usernameRegister: EditText
    private lateinit var passwordRegister: EditText
    private lateinit var info: TextView

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private fun init() {
        val binding: ActivityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        nameRegister = binding.nameRegister
        surnameRegister = binding.surnameRegister
        usernameRegister = binding.usernameRegister
        passwordRegister = binding.passwordRegister
        info = binding.registerInfo

        firebaseAuth = Firebase.auth
        firebaseFirestore = Firebase.firestore
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    fun createClick(v: View?) {
        val name = nameRegister.text.toString().replace("\\s+".toRegex(), "")
        val surname = surnameRegister.text.toString().replace("\\s+".toRegex(), "")
        val username = usernameRegister.text.toString().replace("\\s+".toRegex(), "")
        val password = passwordRegister.text.toString().replace("\\s+".toRegex(), "")

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(surname) || TextUtils.isEmpty(username) || TextUtils.isEmpty(
                password
            )
        ) {
            info.setText(R.string.entered_information_cant_be_empty)
            return
        }

        val alert = AlertDialog.Builder(this)
        alert.setTitle(R.string.save)
        alert.setMessage(R.string.are_you_sure)
        alert.setCancelable(false)
        alert.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->

            firebaseAuth
                .createUserWithEmailAndPassword(username, password)
                .addOnSuccessListener {

                    val postData = HashMap<String, Any?>()

                    postData["name"] = name
                    postData["surname"] = surname
                    postData["username"] = username
                    postData["password"] = password
                    postData["uid"] = firebaseAuth.uid.toString()
                    postData["date"] = FieldValue.serverTimestamp()

                    firebaseFirestore
                        .collection("Users")
                        .document(firebaseAuth.uid.toString())
                        .set(postData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "User Created", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, UsersActivity::class.java)
                            startActivity(intent)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                }.addOnFailureListener { e: Exception ->
                    Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }
        alert.setNegativeButton("No") { dialog: DialogInterface?, _: Int ->
            dialog?.dismiss()
        }
        alert.show()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finish()"))
    override fun onBackPressed() {
        finish()
    }
}