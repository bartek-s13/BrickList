package com.example.bricklist

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.AccessController.getContext

class MyDBHandler(context: Context, name: String?,
                  factory: SQLiteDatabase.CursorFactory?, version:Int):SQLiteOpenHelper(context, DATABASE_NAME, factory,
    DATABASE_VERSION) {

    companion object{
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "BrickList.db"
    }

    var dbContext:Context

    init{
        this.dbContext = context

    }

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
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



    fun checkDB(){
        val DB_DESTINATION = "/data/data/com.example.bricklist/databases/BrickList.db";
        val initialiseDatabase = (File(DB_DESTINATION)).exists()
        println("sprawdzenie")
        if (initialiseDatabase == false) {
            println("instalacja")
            val inputStream:InputStream = this.dbContext.assets.open("databases/BrickList.db")

            try {
                val outputFile = File(dbContext.getDatabasePath(DATABASE_NAME).path)
                //val outputFile = File("/data/data/com.example.bricklist/databases/BrickList.db")
                // val outputStream = FileOutputStream("/data/data/com.example.bricklist/databases/BrickList.db")
                val outputStream = FileOutputStream(outputFile)
                val buffer = ByteArray(1024)
                var length: Int = 0
                while (inputStream.read(buffer).also({ length = it }) > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
            } catch (exception: Throwable) {
                throw RuntimeException("The $DATABASE_NAME database couldn't be installed.", exception)
            }
        }
    }

    fun test(){
        //val query = "SELECT * FROM Colors"
        val db = this.readableDatabase
        //val cursor = db.rawQuery(query, null)
        //cursor.close()
        db.close()
    }



}