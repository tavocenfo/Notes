package com.gquesada.notes.ui.main.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        initDrawer()
        observe()
        if (savedInstanceState == null) {
            viewModel.onAppOpened()
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
                R.id.nav_notes -> viewModel.navigateTo(NavigationScreen.NoteList)
                R.id.nav_logout -> viewModel.onLogOut()
            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun observe() {
        viewModel.navigationEvent.observe(this) { event ->
            when (event) {
                NavigationScreen.Login ->
                    navigateToFragment(LoginFragment(), event.isInitialScreen)

                is NavigationScreen.NoteList ->
                    navigateToFragment(NoteListFragment(), event.isInitialScreen)

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
}