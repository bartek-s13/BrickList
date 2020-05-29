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
import java.io.File
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.collections.ArrayList

class InventoryActivity : AppCompatActivity() {

    lateinit var partsList:ArrayList<InventoryPart>
    var inventory:Inventory? = null
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
        this.inventory = helper.getInventoryById(inventoryID)
        this.inventory!!.lastAccessed = now
        helper.updateInventory(this.inventory!!)
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
            writeXML()
            val toast = Toast.makeText(applicationContext, "Project saved to XML file", Toast.LENGTH_LONG)
            toast.show()
        }
        return super.onOptionsItemSelected(item)
    }


    fun listParts(invenotryID: Int){
        val helper = MyDBHandler(this,null,null, 1)
        helper.checkDB()
        this.partsList = helper.getInventoryParts(invenotryID)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_parts)
        val adapter = PartsAdapter(this.partsList)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.setAdapter(adapter)

    }

    fun writeXML() {
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.newDocument()
        val rootElement = doc.createElement("INVENTORY")
        val helper = MyDBHandler(this,null,null, 1)
        this.partsList.forEach{
            if(it.QuantityInSet>it.QuantityInStore) {
                val item = doc.createElement("ITEM")

                val type = helper.getType(it.TypeID)
                val itemtype = doc.createElement("ITEMTYPE")
                itemtype.appendChild(doc.createTextNode(type))
                item.appendChild(itemtype)

                val id = helper.getID(it.ItemID)
                val itemID = doc.createElement("ITEMID")
                itemID.appendChild(doc.createTextNode(id))
                item.appendChild(itemID)

                val color = doc.createElement("COLOR")
                color.appendChild(doc.createTextNode(it.ColorID.toString()))
                item.appendChild(color)

                val qty = it.QuantityInSet - it.QuantityInStore
                val qtyField = doc.createElement("QTYFILLED")
                qtyField.appendChild(doc.createTextNode(qty.toString()))
                item.appendChild(qtyField)

                rootElement.appendChild(item)

            }
        }
        doc.appendChild(rootElement)
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        val path = this.filesDir
        val outDir = File(path, "Output")
        outDir.mkdir()
        val file_name = this.inventory!!.name
        val file = File(outDir, "$file_name.xml")
        transformer.transform(DOMSource(doc), StreamResult(file))
    }

}
