package com.hasib.callerid.ui

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
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

private const val REQUEST_ID = 1;

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
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
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

        intent.getStringExtra("message")?.let {
           AlertDialog.Builder(this)
               .setTitle("Incoming Call")
               .setMessage(it)
               .setPositiveButton("OK") { dialog, _ ->
                   dialog.dismiss()
               }
               .show()
        }
    }

    private fun handleResult(result: Result<List<Contact>>) {
        result.doOnLoading {
            binding.progressBar.visibility = View.VISIBLE
        }

        result.doOnSuccess {
            loadContactsAndDisplay(it)
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

    private fun managePermissions() {
        val permissions = arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
            )

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            contactViewModel.fetchContacts()
        } else {
            ActivityCompat.requestPermissions(
                this, permissions, 100
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestCallScreeningRole()
        } else {
            requestDialRole()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            contactViewModel.fetchContacts()
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestCallScreeningRole() {
        val roleManager = getSystemService(ROLE_SERVICE) as RoleManager;
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
        startActivityForResult(intent, REQUEST_ID);
    }

    fun requestDialRole() {
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        if (!telecomManager.defaultDialerPackage.equals(packageName)) {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
            }
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ID) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("TAG", "onActivityResult: User accepted the request.");
            } else {
                Log.d("TAG", "onActivityResult: User denied the request.");
            }
        }
    }
}