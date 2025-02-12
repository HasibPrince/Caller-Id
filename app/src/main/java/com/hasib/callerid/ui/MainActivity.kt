package com.hasib.callerid.ui

import android.Manifest
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hasib.callerid.R
import com.hasib.callerid.databinding.ActivityMainBinding
import com.hasib.callerid.domian.model.Contact
import com.hasib.callerid.domian.model.Result
import com.hasib.callerid.domian.model.doOnError
import com.hasib.callerid.domian.model.doOnLoading
import com.hasib.callerid.domian.model.doOnSuccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ContactsAdapter.ContactBlockListener {


    private lateinit var binding: ActivityMainBinding
    private lateinit var contactsAdapter: ContactsAdapter

    private val contactViewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivityUI()

        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(this)

        setupSearch()

        contactViewModel.contactsLiveData.observe(this) {
            handleResult(it)
        }

        contactViewModel.blockContactsLiveData.observe(this) {
            contactsAdapter.notifyItem(it)
        }

        managePermissions()

        intent.getStringExtra(KEY_MESSAGE)?.let {
            showDialog(getString(R.string.title_incoming_call), it)
        }
    }

    private fun showDialog(title: String, message: String): AlertDialog? = MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }
        .show()

    private fun initActivityUI() {
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
    }

    private fun handleResult(result: Result<List<Contact>>) {
        result.doOnLoading {
            binding.progressBar.visibility = View.VISIBLE
        }
        lifecycleScope.launch {
            result.doOnSuccess {
                loadContactsAndDisplay(it)
            }

            result.doOnError {
                binding.progressBar.visibility = View.GONE
                showDialog(getString(R.string.error), it.message ?: getString(R.string.unknownError))
            }
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
        this.contactsAdapter = ContactsAdapter(contacts, this)
        binding.recyclerViewContacts.adapter = contactsAdapter
    }

    private fun managePermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
        )

        if (isPermissionsGranted()) {
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

    private fun isPermissionsGranted(): Boolean = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_PHONE_STATE
    ) == PackageManager.PERMISSION_GRANTED

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
            if (resultCode == RESULT_OK) {
                Timber.d("onActivityResult: User accepted the request.");
            } else {
                Timber.d("onActivityResult: User denied the request.");
            }
        }
    }

    override fun onBlockClicked(contact: Contact) {
        contactViewModel.blockNumber(contact)
    }

    companion object {
        private const val REQUEST_ID = 1;
        const val KEY_MESSAGE = "message"
    }
}