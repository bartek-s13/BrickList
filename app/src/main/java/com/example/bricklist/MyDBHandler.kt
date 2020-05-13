package com.example.bricklist

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHandler(context: Context, name: String?,
                  factory: SQLiteDatabase.CursorFactory?, version:Int):SQLiteOpenHelper(context, DATABASE_NAME, factory,
    DATABASE_VERSION) {

    companion object{
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "BrickList.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun addInventory(inventory: Inventory) {
        val values = ContentValues()
        values.put("Name", inventory.name)
        values.put("Active", inventory.active)
        values.put("LastAccessed", inventory.lastAccessed)
        val db = this.writableDatabase
        db.insert("Inventories", null, values)
        db.close()
    }

    fun addInventoryPart(inventory: InventoryPart) {
        val values = ContentValues()
        values.put("InventoryID", inventory.InventoryID)
        values.put("TypeID", inventory.TypeID)
        values.put("ItemID", inventory.ItemID)
        values.put("QuantityInSet", inventory.QuantityInSet)
        values.put("QuantityInStore", inventory.QuantityInStore)
        values.put("ColorID", inventory.ColorID)
        values.put("Extra", inventory.Extra)
        val db = this.writableDatabase
        db.insert("InventoriesParts", null, values)
        db.close()
    }

    fun getAllInventories():ArrayList<Inventory>{
        val query = "SELECT * FROM INVENTORIES ORDER BY LASTACESSED"
        val db = this.readableDatabase
        val inventories =  ArrayList<Inventory>()
        val cursor = db.rawQuery(query, null)        //The sort order
        if (cursor.moveToFirst()) {
            do {
                val id = Integer.parseInt((cursor.getString(0)))
                val name =  cursor.getString(1)
                val active = Integer.parseInt((cursor.getString(2)))
                val lastAccessed = Integer.parseInt((cursor.getString(3)))
                val inventory = Inventory(id, name, active, lastAccessed)
                inventories.add(inventory)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return inventories
    }

    fun getInventoryParts(inventoryID:Int):ArrayList<InventoryPart>{
        val query = "SELECT * FROM INVENTORIESPARTS WHERE ID = $inventoryID"
        val db = this.readableDatabase
        val inventoryParts =  ArrayList<InventoryPart>()
        val cursor = db.rawQuery(query, null)        //The sort order
        if (cursor.moveToFirst()) {
            do {

                val id = Integer.parseInt((cursor.getString(0)))
                val InventoryID = Integer.parseInt((cursor.getString(1)))
                val TypeID = Integer.parseInt((cursor.getString(2)))
                val ItemID = Integer.parseInt((cursor.getString(3)))
                val QuantityInSet = Integer.parseInt((cursor.getString(4)))
                val QuantityInStore = Integer.parseInt((cursor.getString(5)))
                val ColorID = Integer.parseInt((cursor.getString(6)))
                val Extra = Integer.parseInt((cursor.getString(7)))

                val inventoryPart = InventoryPart(id, InventoryID, TypeID,
                    ItemID, QuantityInSet, QuantityInStore, ColorID, Extra)
                inventoryParts.add(inventoryPart)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return inventoryParts
    }

}