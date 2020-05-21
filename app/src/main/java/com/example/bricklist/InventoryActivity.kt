package com.example.bricklist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InventoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        val extras = intent.extras?: return
        val name = extras.getString("Name")
        val invenotryID= extras.getInt("Id")

        toolbar.setTitle(name)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        listParts(invenotryID)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            super.finish()
        }
        return super.onOptionsItemSelected(item)
    }


    fun listParts(invenotryID: Int){
        val helper = MyDBHandler(this,null,null, 1)
        helper.checkDB()
        val partsList = helper.getInventoryParts(invenotryID)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_parts)
        val adapter = PartsAdapter(partsList)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.setAdapter(adapter)
        partsList.forEach{
            println("item id: ${it.ItemID}")
        }
    }

}
