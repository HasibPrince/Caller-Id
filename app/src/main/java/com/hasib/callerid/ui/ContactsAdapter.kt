package com.hasib.callerid.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hasib.callerid.data.model.Contact
import com.hasib.callerid.databinding.ItemContactBinding

class ContactsAdapter(
    private val contacts: List<Contact>,
    private val listener: ContactBlockListener
) :
    RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>(), Filterable {

    private var filteredContacts: List<Contact> = contacts

    class ContactViewHolder(binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameTextView: TextView = binding.contactName
        val phoneTextView: TextView = binding.contactPhone
        val blockButton: View = binding.blockButton
        val blockText: TextView = binding.blockText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = filteredContacts[position]
        holder.nameTextView.text = contact.name
        holder.phoneTextView.text = contact.phoneNumber
        holder.blockText.text = if (contact.isBlocked) "Unblock" else "Block"
        holder.blockButton.setOnClickListener {
            listener.onBlockClicked(contact)
        }
    }

    fun notifyItem(contact: Contact) {
        val index = filteredContacts.indexOf(contact)
        if (index != -1) {
            notifyItemChanged(index)
        }
    }

    override fun getItemCount(): Int = filteredContacts.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim()?.lowercase()
                val results = FilterResults()

                results.values = if (query.isNullOrEmpty()) {
                    contacts
                } else {
                    contacts.filter {
                        it.name.lowercase().contains(query) || it.phoneNumber.contains(query)
                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredContacts = results?.values as List<Contact>
                notifyDataSetChanged()
            }
        }
    }

    interface ContactBlockListener {
        fun onBlockClicked(contact: Contact)
    }

}