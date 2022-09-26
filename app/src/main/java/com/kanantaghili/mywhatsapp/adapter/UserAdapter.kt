package com.kanantaghili.mywhatsapp.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kanantaghili.mywhatsapp.databinding.RecyclerRowBinding
import com.kanantaghili.mywhatsapp.view.MessageActivity
import com.kanantaghili.mywhatsapp.model.User

class UserAdapter(private val user: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserHolder>() {

    class UserHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): UserHolder {
        val binding =
            RecyclerRowBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return UserHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: UserHolder, position: Int) {
        viewHolder.binding.recyclerText.text = user[position].name + " " + user[position].surname
        viewHolder.itemView.setOnClickListener {
            val intent = Intent(viewHolder.itemView.context, MessageActivity::class.java)
            intent.putExtra("otherUser", user[position])
            viewHolder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return user.size
    }
}