package com.example.chotuvemobileapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val preferences by lazy {
        applicationContext.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = preferences.getString("token", "Fail")
        if (token == "Fail") {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        setContentView(R.layout.activity_home)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profile, R.id.nav_slideshow
            ), drawerLayout
        )

        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_logout -> {
                    logout()
                    true
                }
                else -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    navController.navigate(it.itemId)
                    navView.menu.findItem(it.itemId).isChecked = true
                    true
                }
            }
        }

    }

    private fun logout() {
        preferences.edit().remove("token")
            .remove("username")
            .remove("email")
            .remove("password")
            .apply()
        startActivity(Intent(this, HomeActivity::class.java))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) drawer_layout.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }

    fun openDrawer(){
        drawer_layout.openDrawer(GravityCompat.START)
    }
}
