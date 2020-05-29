package com.example.bricklist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

class InventoryActivity : AppCompatActivity() {


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }








    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        val extras = intent.extras?: return
        val name = extras.getString("Name")
        val inventoryID= extras.getInt("Id")
        val helper = MyDBHandler(this,null,null, 1)

        val cal = Calendar.getInstance()
        val now:Int = cal.getTimeInMillis().toInt()
        val inventory = helper.getInventoryById(inventoryID)
        inventory!!.lastAccessed = now
        helper.updateInventory(inventory)
        toolbar.setTitle(name)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        listParts(inventoryID)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            super.finish()
        }
        else if(id == R.id.action_save){
            val toast = Toast.makeText(applicationContext, "Project saved to XML file", Toast.LENGTH_LONG)
            toast.show()
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

    }

    fun writeXML(v: View) {
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.newDocument()
        val rootElement = doc.createElement("INVENTORY")
    }

}
