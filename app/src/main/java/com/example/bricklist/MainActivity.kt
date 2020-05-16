package com.example.bricklist

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    var show_archived = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        readConfig()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    fun readConfig(){
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        this.show_archived = preferences.getBoolean("show_archived ", false)
    }

    fun addNewInventory(v: View){
        val intent = Intent(this, AddInventoryActivity::class.java)
        startActivity(intent)
    }

}
