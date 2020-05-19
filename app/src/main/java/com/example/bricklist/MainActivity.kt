package com.example.bricklist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : BaseActivity() {

    var show_archived = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        readConfig()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        //this.setTitle("BrickList")

        listInventories()
    }

    fun readConfig(){
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        this.show_archived = preferences.getBoolean("show_archived ", false)
    }

    fun addNewInventory(v: View){
        val intent = Intent(this, AddInventoryActivity::class.java)
        startActivity(intent)
    }

    fun listInventories(){
        val helper = MyDBHandler(this,null,null, 1)
        helper.checkDB()
        val inventoryList = helper.getAllInventories()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_inventories)
        val adapter = InventoriesAdapter(inventoryList)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.setAdapter(adapter)

    }

    override fun onResume() {
        super.onResume()
        listInventories()
    }

}
