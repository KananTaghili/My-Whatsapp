package com.kanantaghili.mywhatsapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kanantaghili.mywhatsapp.adapter.MessageAdapter
import com.kanantaghili.mywhatsapp.databinding.ActivityMessageBinding
import com.kanantaghili.mywhatsapp.model.Message
import com.kanantaghili.mywhatsapp.model.User
import java.io.Serializable

class MessageActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var otherUser: User
    private lateinit var message: EditText
    private lateinit var binding: ActivityMessageBinding
    private lateinit var uid: String
    private var messageArrayList: ArrayList<Message> = ArrayList()
    private lateinit var adapter: MessageAdapter

    private fun init() {
        binding = ActivityMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        message = binding.writeMessage
        binding.messageRecycler.layoutManager = LinearLayoutManager(this)

        firebaseAuth = Firebase.auth
        firebaseFirestore = Firebase.firestore

        uid = firebaseAuth.currentUser?.uid.toString()
        otherUser = intent.getSerializable("otherUser", User::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        supportActionBar?.title = otherUser.name
        supportActionBar?.subtitle = otherUser.surname

        getMessage()

        adapter = MessageAdapter(messageArrayList, firebaseAuth.currentUser?.email.toString())
        binding.messageRecycler.adapter = adapter

        binding.sendImage.setOnClickListener {
            if (!TextUtils.isEmpty(message.text.toString().replace("\\s+".toRegex(), ""))) {
                val message2: String = message.text.toString()
                setMessage(message2)
                message.setText("")
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getMessage() {

        firebaseFirestore
            .collection("Messages")
            .document(uid)
            .collection(otherUser.uid.toString())
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
                }
                messageArrayList.clear()

                if (snapshot != null) {
                    for (snap in snapshot.documents) {
                        val message = snap.get("message") as String
                        val from = snap.get("from") as String

                        val messageModel = Message(message, from)
                        messageArrayList.add(messageModel)
                        adapter.notifyDataSetChanged()
                        binding.messageRecycler.scrollToPosition(messageArrayList.size - 1)
                    }
                }
            }
    }

    private fun setMessage(message: String) {

        val postData = HashMap<String, Any?>()

        postData["message"] = message
        postData["from"] = firebaseAuth.currentUser?.email?.lowercase()
        postData["date"] = Timestamp.now()

        firebaseFirestore
            .collection("Messages")
            .document(uid)
            .collection(otherUser.uid.toString())
            .add(postData)
            .addOnSuccessListener {

                firebaseFirestore
                    .collection("Messages")
                    .document(otherUser.uid.toString())
                    .collection(uid)
                    .add(postData)
                    .addOnSuccessListener {

                    }.addOnFailureListener {
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun <T : Serializable?> Intent.getSerializable(key: String, m_class: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSerializableExtra(key, m_class)!!
        } else {
            this.getSerializableExtra(key) as T
        }
    }
}

