package com.example.bricklist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_inventory.*
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
    lateinit var colorList:List<String>
    lateinit var typeList:List<String>
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
        getParts(inventoryID)
        setRecycler(this.partsList.toList())
        setSpinners()
    }

    fun setSpinners(){
        val colorAdapter = ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_dropdown_item,
            this.colorList
            )
        color_spinner.adapter = colorAdapter

        val typeAdapter = ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_dropdown_item,
            this.typeList
        )
        type_spinner.adapter = typeAdapter

        type_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View?, position: Int, id: Long) {
                filterList(color_spinner.selectedItem.toString(), type_spinner.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }

        }

        color_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View?, position: Int, id: Long) {
                filterList(color_spinner.getItemAtPosition(position).toString(), type_spinner.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            super.finish()
        }
        else if(id == R.id.action_save){
            writeXML()
        }
        return super.onOptionsItemSelected(item)
    }

    fun getParts(invenotryID: Int){
        val helper = MyDBHandler(this,null,null, 1)
        helper.checkDB()
        val cList:ArrayList<String> = ArrayList<String>()
        val tList:ArrayList<String> = ArrayList<String>()
        cList.add("-")
        tList.add("-")
        this.partsList = helper.getInventoryParts(invenotryID)
        this.partsList.forEach {
            val c = it.ColorID
            val t = helper.getPartName(it.ItemID)
            cList.add(helper.getColorName(c))
            tList.add(t)
        }
        this.colorList = cList.distinct()
        this.typeList = tList.distinct()
    }

    fun filterList(color:String, type:String){
        println(type)
        val helper = MyDBHandler(this,null,null, 1)
        var current = this.partsList.toList()
        if(color != "-"){
            val cID = helper.getColorIDbyName(color)
            current = current.filter{it -> it.ColorID==cID}
        }

        if(type != "-"){
            val tID = helper.getItemIDbyName(type)
            println("Id: $tID")
            current = current.filter{it -> it.ItemID==tID}
        }
        setRecycler(current)
    }

    fun setRecycler(currentList:List<InventoryPart>){
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_parts)
        val adapter = PartsAdapter(currentList)
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
        val path = this.getExternalFilesDir(null)
        val outDir = File(path, "Inventories")
        outDir.mkdir()
        val file_name = this.inventory!!.name
        val file = File(outDir, "$file_name.xml")
        transformer.transform(DOMSource(doc), StreamResult(file))
        val toast = Toast.makeText(applicationContext, "Project saved to the memory card", Toast.LENGTH_LONG)
        toast.show()
    }

}
