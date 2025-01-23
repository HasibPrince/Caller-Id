package com.hasib.callerid.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hasib.callerid.R
import com.hasib.callerid.data.model.Contact
import com.hasib.callerid.data.model.Result
import com.hasib.callerid.data.model.doOnLoading
import com.hasib.callerid.data.model.doOnSuccess
import com.hasib.callerid.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contactsAdapter: ContactsAdapter

    private val contactViewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ActivityMainBinding.inflate(layoutInflater).apply {
            binding = this
            setContentView(root)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(this)

        setupSearch()

        contactViewModel.contactsLiveData.observe(this) {
            handleResult(it)
        }

        managePermissions()
    }

    private fun handleResult(result: Result<List<Contact>>) {
        result.doOnLoading {
            binding.progressBar.visibility = View.VISIBLE
        }

        result.doOnSuccess {
            loadContactsAndDisplay(it)
        }
    }

    private fun managePermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            contactViewModel.fetchContacts()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 100)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            contactViewModel.fetchContacts()
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                contactsAdapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                contactsAdapter.filter.filter(newText)
                return true
            }
        })
    }

    private fun loadContactsAndDisplay(contacts: List<Contact>) {
        binding.progressBar.visibility = View.GONE
        this.contactsAdapter = ContactsAdapter(contacts)
        binding.recyclerViewContacts.adapter = contactsAdapter
    }
}