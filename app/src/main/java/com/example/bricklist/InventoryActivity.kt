package com.example.bricklist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar

class InventoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        val extras = intent.extras?: return
        val name = extras.getString("Name")
        val code = extras.getInt("Id")
        println(name)
        toolbar.setTitle(name)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            super.finish()
        }
        return super.onOptionsItemSelected(item)
    }


}
