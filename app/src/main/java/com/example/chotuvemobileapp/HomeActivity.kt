package com.example.chotuvemobileapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.chotuvemobileapp.data.repositories.LogoutDataSource
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val preferences by lazy {
        applicationContext.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }
    private val navController by lazy {
        findNavController(R.id.nav_host_fragment)
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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profile, R.id.nav_friends
            ), drawerLayout
        )

        navView.setupWithNavController(navController)

        navView
            .getHeaderView(0)
            .findViewById<TextView>(R.id.DrawerUsername).text  = preferences.getString("username", "")


        navView.setNavigationItemSelectedListener {item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    val username = preferences.getString("username", "")
                    LogoutDataSource.logout(preferences, username!!) { response_id ->
                        when (response_id) {
                            "Success" -> logout()
                            else -> Toast.makeText(applicationContext, getString(R.string.internal_error),
                                Toast.LENGTH_LONG).show()
                        }
                    }
                    true
                }
                R.id.nav_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    BottomNavMenu.visibility = View.VISIBLE
                    navController.navigate(getCurrentBottomMenuOption())
                    navView.menu.findItem(item.itemId).isChecked = true
                    window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
                    true
                }
                else -> {
                    BottomNavMenu.visibility = View.GONE
                    drawerLayout.closeDrawer(GravityCompat.START)
                    navController.navigate(item.itemId)
                    navView.menu.findItem(item.itemId).isChecked = true
                    window.navigationBarColor = ContextCompat.getColor(this, R.color.transparent)
                    true
                }
            }
        }
        BottomNavMenu.selectedItemId = R.id.MenuHome
        BottomNavMenu.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.MenuHome -> {
                    navController.navigate(R.id.nav_home)
                    true
                }
                R.id.MenuAddVideo ->{
                    navController.navigate(R.id.nav_add_video)
                    true
                }
                R.id.MenuInbox ->{
                    navController.navigate(R.id.nav_notifications)
                    true
                }
                R.id.MenuMessages ->{
                    navController.navigate(R.id.nav_messages)
                    true
                }
                else -> false
            }
        }

    }

    private fun logout() {
        preferences.edit()
            .remove("token")
            .remove("username")
            .remove("password")
            .apply()
        finish()
        startActivity(Intent(this, HomeActivity::class.java))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
            navController.currentDestination!!.id == R.id.nav_home -> finish()
            navController.currentDestination!!.id == R.id.nav_profile || navController.currentDestination!!.id == R.id.nav_friends -> {
                navController.navigate(getCurrentBottomMenuOption())
                BottomNavMenu.visibility = View.VISIBLE
            }
            else -> {
                navController.navigate(R.id.nav_home)
                BottomNavMenu.selectedItemId = R.id.MenuHome
            }
        }
    }

    private fun getCurrentBottomMenuOption() : Int{
        return when (BottomNavMenu.selectedItemId){
            R.id.MenuMessages -> R.id.nav_messages
            R.id.MenuInbox -> R.id.nav_notifications
            R.id.MenuAddVideo -> R.id.nav_add_video
            else -> R.id.nav_home
        }
    }

    fun openDrawer(){
        drawer_layout.openDrawer(GravityCompat.START)
    }
}
