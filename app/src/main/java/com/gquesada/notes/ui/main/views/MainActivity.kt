package com.gquesada.notes.ui.main.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.gquesada.notes.R
import com.gquesada.notes.ui.addnotes.views.AddNoteFragment
import com.gquesada.notes.ui.login.LoginFragment
import com.gquesada.notes.ui.main.viewmodels.MainViewModel
import com.gquesada.notes.ui.main.viewmodels.NavigationScreen
import com.gquesada.notes.ui.notes.NoteListFragment
import com.gquesada.notes.ui.tag.views.TagListFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var toolbar: MaterialToolbar

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Las notificaciones hasn sido activadas", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "Las notificaciones son necesarias para el funcionamiento correcto del app",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        initDrawer()
        observe()
        askNotificationPermission()
        if (savedInstanceState == null) {
            viewModel.onAppOpened(intent.extras)
        }
        //getNotificationToken()
    }

    private fun getNotificationToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("NOT_TEST", "Error fetching notification token")
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("NOT_TEST", "Token = $token")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun initDrawer() {
        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        findViewById<NavigationView>(R.id.navigation_view).setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_notes -> viewModel.navigateTo(NavigationScreen.NoteList())
                R.id.nav_logout -> viewModel.onLogOut()
            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun observe() {
        viewModel.navigationEvent.observe(this) { event ->
            when (event) {
                is NavigationScreen.Login ->
                    navigateToFragment(
                        LoginFragment().apply { arguments = event.arguments },
                        event.isInitialScreen
                    )

                is NavigationScreen.NoteList ->
                    navigateToFragment(NoteListFragment().apply {
                        arguments = event.arguments
                    }, event.isInitialScreen)

                NavigationScreen.AddNotes ->
                    navigateToFragment(AddNoteFragment.newInstance(), event.isInitialScreen)

                NavigationScreen.TagList ->
                    navigateToFragment(TagListFragment(), event.isInitialScreen)

                is NavigationScreen.EditNote ->
                    navigateToFragment(AddNoteFragment.newInstance(event.note))
            }
        }
        viewModel.shouldDrawerBeEnabled.observe(this, ::setDrawerEnabled)
    }

    private fun setDrawerEnabled(enabled: Boolean) {
        val lockMode =
            if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        drawerLayout.setDrawerLockMode(lockMode)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = enabled
        supportActionBar?.setDisplayHomeAsUpEnabled(enabled)
        supportActionBar?.setDisplayShowHomeEnabled(enabled)
    }


    private fun navigateToFragment(fragment: Fragment, isInitialFragment: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (!isInitialFragment) {
            transaction.addToBackStack(fragment.javaClass.name)
        }
        transaction
            .commit()
    }

    private fun askNotificationPermission() {
        // validacion para pedier la autorizacion de notificaciones solo en
        // dispositivos cuya version de Android sea igual o mayor a la version 33
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // si esto es verdadero es porque el app ya cuenta con el permiso de mostrar notificaciones
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Bloque de codigo que podemos ejecutar cuando las notificaciones estas activadas
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Mostrar un UI component en el cual pidamos la autorizacion del usuario para mostrar notificaciones
                // y le indiquemos por que son necesarios
                // en caso positivo mostrar el dialogo nativo con el cual el SO solicita el permiso
                // en caso negativo ocultar el componente
                AlertDialog.Builder(this)
                    .setTitle("Notifications")
                    .setMessage("Con la aprobacion de notificaciones nos permitiras enviar alertas de cuando una nota fue agregada")
                    .setPositiveButton("Aceptar") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    .setNegativeButton("Cancelar") { _, _ -> }
                    .show()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}