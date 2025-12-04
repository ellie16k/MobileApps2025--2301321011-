package com.example.smartexpensetrackerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.smartexpensetrackerapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        // Navigation
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> navController.navigate(R.id.homeFragment)
                R.id.nav_add_expense -> navController.navigate(R.id.addExpenseFragment)
                R.id.nav_add_income -> navController.navigate(R.id.addIncomeFragment)
                R.id.nav_stats -> navController.navigate(R.id.statsFragment)
                R.id.nav_user -> navController.navigate(R.id.userFragment)
            }
            true
        }
    }
}
