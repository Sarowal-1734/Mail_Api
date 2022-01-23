package com.example.mailapi.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mailapi.R
import com.example.mailapi.models.Message
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MailAdapter : RecyclerView.Adapter<MailAdapter.MailViewHolder>() {

    inner class MailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewFrom: TextView = itemView.findViewById(R.id.textViewFrom)
        val textViewSubject: TextView = itemView.findViewById(R.id.textViewSubject)
        val textViewBody: TextView = itemView.findViewById(R.id.textViewBody)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MailViewHolder {
        return MailViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mail_preview, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MailViewHolder, position: Int) {
        val mail = differ.currentList[position]
        holder.apply {
            textViewFrom.text = "From: " + mail.from.address
            textViewSubject.text = "Subject: " + mail.subject
            textViewBody.text = "Body: " + mail.intro
            textViewDate.text = getFormattedDate(mail.updatedAt)
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(mail) }
        }
    }

    private fun getFormattedDate(updatedAtDate: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.FRANCE)
        val date = formatter.parse(updatedAtDate)
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Message) -> Unit)? = null

    fun setOnItemClickListener(listener: (Message) -> Unit) {
        onItemClickListener = listener
    }

}