package com.example.vino

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.vino.databinding.ActivityMainBinding
import com.example.vino.ui.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        // TODO badges if have todos
        val badge = navView.getOrCreateBadge(R.id.navigation_todos)
        badge.isVisible

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_todos
            )
        )

        setSupportActionBar(binding.appBar) // this is for the top bar, right item

        // Can use mix of both vvv
        //setupActionBarWithNavController(navController, appBarConfiguration) // displays arrow and fragment title on non top navigation items
        navView.setupWithNavController(navController) // to use with bottom bar, shows other fragments

        binding.appBar.setNavigationOnClickListener {
            //Toast.makeText(applicationContext, "Logo", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_bar_account -> {
                showSettingsDialog()
//                val intent = Intent(this, SettingsActivity::class.java)
//                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }

    private fun showSettingsDialog() {
        SettingsDialogFragment.display(supportFragmentManager);
    }
}
