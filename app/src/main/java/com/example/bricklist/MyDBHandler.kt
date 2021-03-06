package com.example.bricklist

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
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

    fun addInventory(inventory: Inventory):Long {
        val values = ContentValues()
        values.put("Name", inventory.name)
        values.put("Active", inventory.active)
        values.put("LastAccessed", inventory.lastAccessed)
        val db = this.writableDatabase
        val id = db.insert("Inventories", null, values)
        db.close()
        return id
    }

    fun addInventoryPart(part: InventoryPart) {
        val values = ContentValues()
        values.put("InventoryID", part.InventoryID)
        values.put("TypeID", part.TypeID)
        values.put("ItemID", part.ItemID)
        values.put("QuantityInSet", part.QuantityInSet)
        values.put("QuantityInStore", part.QuantityInStore)
        values.put("ColorID", part.ColorID)
        values.put("Extra", part.Extra)
        val db = this.writableDatabase
        db.insert("InventoriesParts", null, values)
        db.close()
    }

    fun getAllInventories():ArrayList<Inventory>{
        val query = "SELECT * FROM INVENTORIES ORDER BY LastAccessed DESC"
        val db = this.readableDatabase
        val inventories =  ArrayList<Inventory>()
        val cursor = db.rawQuery(query, null)
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

    fun getNotArchivedInventories():ArrayList<Inventory>{
        val query = "SELECT * FROM INVENTORIES where active = 1 ORDER BY LastAccessed DESC"
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
        val query = "SELECT * FROM INVENTORIESPARTS WHERE InventoryID = $inventoryID"
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

        if (initialiseDatabase == false) {

            val inputStream:InputStream = this.dbContext.assets.open("databases/BrickList.db")

            try {
                val outputFile = File(dbContext.getDatabasePath(DATABASE_NAME).path)
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

    fun getTypeID(code:String): Int{
        val query = "SELECT id FROM ItemTypes WHERE Code = \'$code\'"
        val db = this.readableDatabase
        var TypeID = -1
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst() && cursor.getCount() >= 1) {
            TypeID = Integer.parseInt((cursor.getString(0)))
        }
        cursor.close()
        db.close()
        return TypeID
    }

    fun getType(id:Int):String{
        val query = "SELECT code FROM ItemTypes WHERE id = $id"
        val db = this.readableDatabase
        var code = ""
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst() && cursor.getCount() >= 1) {
            code = (cursor.getString(0))
        }
        cursor.close()
        db.close()
        return code
    }

    fun getItemID(code:String): Int{
        val query = "select id from Parts where Code = \'$code\'"
        val db = this.readableDatabase
        var ItemID = -1
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst() && cursor.getCount() >= 1) {
            ItemID = Integer.parseInt((cursor.getString(0)))
        }
        cursor.close()
        db.close()
        return ItemID
    }



    fun getID(itemID:Int):String{
        val query = "select code from Parts where id = $itemID"
        val db = this.readableDatabase
        var ItemID = ""
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst() && cursor.getCount() >= 1) {
            ItemID = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return ItemID
    }

    fun getColorName(colorId: Int):String{
        //TODO polska nazwa
        val query = "SELECT name FROM Colors WHERE id = $colorId"
        val db = this.readableDatabase
        var colorName : String = ""
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            colorName =cursor.getString(0)
        }
        cursor.close()
        db.close()
        return colorName
    }

    fun getColorID(colorCode:Int):Int{
        val query = "SELECT id FROM Colors WHERE code = $colorCode"
        val db = this.readableDatabase
        var colorID : Int = -1
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            colorID =Integer.parseInt(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return colorID
    }

    fun getPartName(itemID:Int):String{
        val query = "SELECT name FROM Parts WHERE id= $itemID"
        var name: String = ""
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return name
    }

    fun getItemIDbyName(name:String): Int{
        val query = "select id from Parts where Name = \'$name\'"
        val db = this.readableDatabase
        var ItemID = -1
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst() && cursor.getCount() >= 1) {
            ItemID = Integer.parseInt((cursor.getString(0)))
        }
        cursor.close()
        db.close()
        return ItemID
    }

    fun updatePart(part:InventoryPart){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("InventoryID", part.InventoryID)
        values.put("TypeID", part.TypeID)
        values.put("ItemID", part.ItemID)
        values.put("QuantityInSet", part.QuantityInSet)
        values.put("QuantityInStore", part.QuantityInStore)
        values.put("ColorID", part.ColorID)
        values.put("Extra", part.Extra)
        db.update("InventoriesParts", values, "id = " + part.id, null)
        db.close()
    }

    fun getPartCode(itemID:Int, colorId:Int):Int{
        val query = "select code from Codes where ItemID = $itemID and ColorID = $colorId"
        val db = this.readableDatabase
        var code = -1
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst() && cursor.getCount() >= 1) {
            code = Integer.parseInt((cursor.getString(0)))
        }
        cursor.close()
        db.close()
        return code
    }

    fun checkInCodes(color:Int, id:Int):Boolean{
        val query = "select id from Codes where colorid =$color and itemid = $id"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        if(cursor.getCount() <=0) {
            cursor.close()
            db.close()
            return false
        }
        cursor.close()
        db.close()
        return true
    }

    fun addRow(color:Int, id:Int){
        val values = ContentValues()
        values.put("ColorID", color)
        values.put("ItemID", id)
        values.put("Code", "-1")
        val db = this.writableDatabase
        val i = db.insert("codes", null, values)
        db.close()
    }

    fun addImage(image: ByteArray, color:Int, id:Int){
        if(checkInCodes(color, id)==false){
            addRow(color, id)
        }
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("Image", image)
        db.update("Codes", values, "colorid =$color and itemid = $id", null)
        db.close()
    }

    fun getImage(color:Int, id:Int):ByteArray?{
        val query = "select image from Codes where colorid =$color and itemid = $id"
        val db = this.readableDatabase
        var image:ByteArray? = null
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst() && cursor.getCount() >= 1) {
            image = cursor.getBlob(0)
        }
        cursor.close()
        db.close()
        return image
    }

    fun checkImage(color:Int, id:Int):Boolean{
        val query = "select image from Codes where colorid =$color and itemid = $id"
        val db = this.readableDatabase
        var image:ByteArray? = null
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst() && cursor.getCount() >= 1) {
            image = cursor.getBlob(0)
        }
        cursor.close()
        db.close()
        return image == null
    }

    fun updateInventory(inventory:Inventory){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("Id", inventory.id)
        values.put("Name", inventory.name)
        values.put("Active", inventory.active)
        values.put("LastAccessed", inventory.lastAccessed)
        db.update("Inventories", values,"id = ${inventory.id}" ,null)
        db.close()
    }

    fun getInventoryById(inventoryId:Int):Inventory?{
        val query = "SELECT * FROM INVENTORIES where id = $inventoryId"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var inventory:Inventory? = null
        if(cursor.moveToFirst() && cursor.getCount() >= 1) {
            val id = Integer.parseInt((cursor.getString(0)))
            val name =  cursor.getString(1)
            val active = Integer.parseInt((cursor.getString(2)))
            val lastAccessed = Integer.parseInt((cursor.getString(3)))
            inventory = Inventory(id, name, active, lastAccessed)
        }
        cursor.close()
        db.close()
        return inventory
    }

    fun getColorIDbyName(name:String):Int{
        val query = "SELECT id FROM Colors WHERE name = \'$name\'"
        val db = this.readableDatabase
        var colorID : Int = -1
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            colorID =Integer.parseInt(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return colorID
    }
}