package com.kanantaghili.mywhatsapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kanantaghili.mywhatsapp.R
import com.kanantaghili.mywhatsapp.adapter.UserAdapter
import com.kanantaghili.mywhatsapp.databinding.ActivityUsersBinding
import com.kanantaghili.mywhatsapp.model.User


class UsersActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private val postArrayList: ArrayList<User> = ArrayList()
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityUsersBinding = ActivityUsersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firebaseAuth = Firebase.auth
        firebaseFirestore = Firebase.firestore

        getDataFromFirestore()

        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter = UserAdapter(postArrayList)
        binding.recyclerView.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDataFromFirestore() {
        firebaseFirestore
            .collection("Users")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
                }
                if (snapshot != null) {
                    for (snap in snapshot.documents) {
                        val name = snap.get("name") as String
                        val surname = snap.get("surname") as String
                        val username = snap.get("username") as String
                        val uid = snap.get("uid") as String

                        if (username.lowercase() != firebaseAuth.currentUser?.email) {
                            val post = User(name, surname, uid)
                            postArrayList.add(post)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.my_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.change) {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        Firebase.auth.signOut()
        finish()
    }
}