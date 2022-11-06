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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kanantaghili.mywhatsapp.adapter.MessageAdapter
import com.kanantaghili.mywhatsapp.databinding.ActivityMessageBinding
import com.kanantaghili.mywhatsapp.model.Message
import com.kanantaghili.mywhatsapp.model.User
import java.io.Serializable

@Suppress("DEPRECATION", "UNCHECKED_CAST")
class MessageActivity : AppCompatActivity() {
    private var firebaseAuth = Firebase.auth
    private var firebaseFirestore = Firebase.firestore
    private lateinit var otherUser: User
    private lateinit var binding: ActivityMessageBinding
    private lateinit var message: EditText
    private lateinit var uid: String
    private lateinit var myLayoutManager: LinearLayoutManager
    private lateinit var adapter: MessageAdapter
    private var messageArrayList = ArrayList<Message>()
    private var scrollLastPosition = 0
    private var scrollFirstPosition = 0
    private var flag = true

    private fun init() {
        binding = ActivityMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        message = binding.writeMessage

        binding.messageRecycler.layoutManager = LinearLayoutManager(this)
        myLayoutManager = binding.messageRecycler.layoutManager as LinearLayoutManager

        uid = firebaseAuth.currentUser?.uid.toString()
        otherUser = intent.getSerializable("otherUser", User::class.java)

        adapter = MessageAdapter(messageArrayList, firebaseAuth.currentUser?.email.toString())
        binding.messageRecycler.adapter = adapter

        supportActionBar?.title = otherUser.name
        supportActionBar?.subtitle = otherUser.surname
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        getMessage()

        binding.sendImage.setOnClickListener {
            if (!TextUtils.isEmpty(message.text.toString().replace("\\s+".toRegex(), ""))) {
                val sendMessage = message.text.toString()
                setMessage(sendMessage)
                message.setText("")
            }
        }

        if (flag) {
            binding.writeMessage.setOnFocusChangeListener { _, _ ->
                scrollLastPosition = myLayoutManager.findLastVisibleItemPosition()
                layoutChanger()
                flag = false
            }
        }

        binding.writeMessage.setOnClickListener {
            scrollLastPosition = myLayoutManager.findLastVisibleItemPosition()
            scrollFirstPosition = myLayoutManager.findFirstVisibleItemPosition()
        }
    }

    private fun layoutChanger() {
        binding.messageRecycler.addOnLayoutChangeListener { _, _, _, _, bottom,
                                                            _, _, _, oldBottom ->

            if (scrollLastPosition == messageArrayList.size - 1 && bottom < oldBottom) {
                binding.messageRecycler.smoothScrollToPosition(messageArrayList.size - 1)
            } else if (scrollLastPosition < messageArrayList.size - 1 && bottom != oldBottom) {
                if (bottom > oldBottom) {
                    binding.messageRecycler.postDelayed({
                        binding.messageRecycler.scrollToPosition(scrollFirstPosition)
                    }, 0)
                }
                binding.messageRecycler.postDelayed({
                    binding.messageRecycler.smoothScrollToPosition(scrollLastPosition)
                }, 10)
//                binding.messageRecycler.smoothScrollToPosition(scrollLastPosition)
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