package com.example.bricklist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        listInventories()
    }


    fun addNewInventory(v: View){
        val intent = Intent(this, AddInventoryActivity::class.java)
    startActivity(intent)
}

    fun listInventories(){

        val helper = MyDBHandler(this,null,null, 1)
        helper.checkDB()
        val inventoryList:ArrayList<Inventory>
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        if(preferences.getBoolean("show_archived", false)){
            inventoryList = helper.getAllInventories()
        }else{
            inventoryList = helper.getNotArchivedInventories()
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_inventories)
        val adapter = InventoriesAdapter(inventoryList, { inventory : Inventory -> invenoryClicked(inventory) }, this, preferences.getBoolean("show_archived", false))
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.setAdapter(adapter)
    }

    override fun onResume() {
        super.onResume()
        listInventories()
    }

    private fun invenoryClicked(inventory: Inventory){
        val intent = Intent(this, InventoryActivity::class.java)
        intent.putExtra("Name", inventory.name)
        intent.putExtra("Id", inventory.id)
        startActivity(intent)
    }
}
