package com.kanantaghili.mywhatsapp.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kanantaghili.mywhatsapp.R
import com.kanantaghili.mywhatsapp.model.Message

class MessageAdapter(private val message: ArrayList<Message>, private val userName: String) :
    RecyclerView.Adapter<MessageAdapter.MessageHolder>(), OnLongClickListener {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: MessageHolder, position: Int) {
        viewHolder.text.text = message[position].text.toString()

        viewHolder.itemView.setOnLongClickListener {
            val clipboard: ClipboardManager =
                viewHolder.itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val context = viewHolder.itemView.context
            val myClip: ClipData = ClipData.newPlainText("Label", message[position].text)
            clipboard.setPrimaryClip(myClip)
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onLongClick(p0: View?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return message.size
    }

    class MessageHolder(binding: View, viewType: Int) : RecyclerView.ViewHolder(binding) {
        var text: TextView

        init {
            text = if (viewType == 1) {
                binding.findViewById(R.id.messageText1)
            } else {
                binding.findViewById(R.id.messageText2)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MessageHolder {
        val binding = if (viewType == 1) {
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recycler_row_2, viewGroup, false)
        } else {
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recycler_row_3, viewGroup, false)
        }
        return MessageHolder(binding, viewType)
    }

    override fun getItemViewType(position: Int): Int {

        return if (message[position].from.equals(userName)) {
            1
        } else {
            2
        }
    }
}