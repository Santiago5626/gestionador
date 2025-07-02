package com.gestionador

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        
        // Setup Bottom Navigation
        bottomNavigation.setupWithNavController(navController)
        
        // Override bottom navigation behavior to always go to root fragments
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.clientesFragment -> {
                    // Always navigate to clientesFragment, clearing the back stack
                    navController.popBackStack(R.id.clientesFragment, false)
                    if (navController.currentDestination?.id != R.id.clientesFragment) {
                        navController.navigate(R.id.clientesFragment)
                    }
                    true
                }
                R.id.prestamosFragment -> {
                    // Always navigate to prestamosFragment, clearing the back stack
                    navController.popBackStack(R.id.prestamosFragment, false)
                    if (navController.currentDestination?.id != R.id.prestamosFragment) {
                        navController.navigate(R.id.prestamosFragment)
                    }
                    true
                }
                R.id.activosFragment -> {
                    // Always navigate to activosFragment, clearing the back stack
                    navController.popBackStack(R.id.activosFragment, false)
                    if (navController.currentDestination?.id != R.id.activosFragment) {
                        navController.navigate(R.id.activosFragment)
                    }
                    true
                }
                else -> false
            }
        }
    }
}
