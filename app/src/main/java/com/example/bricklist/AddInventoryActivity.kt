package com.example.bricklist


import android.R.attr.bitmap
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.ByteBuffer
import javax.xml.parsers.DocumentBuilderFactory


class AddInventoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_inventory)
    }

    private inner class InventoryDownloader:AsyncTask<String, Int, String>(){
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

        override fun doInBackground(vararg params: String?): String {
            try{
                val url = URL(params[0] +".xml")
                val connection = url.openConnection()
                connection.connect()
                val lenghtOfFile = connection.contentLength
                val isStream = url.openStream()
                val testDirectory = File("$filesDir/XML")
                if(!testDirectory.exists()) testDirectory.mkdir()
                val fos = FileOutputStream("$testDirectory/inventory.xml")
                val data = ByteArray(1024)
                var count =0
                var total: Long = 0
                var progress = 0
                count = isStream.read(data)
                while(count!=-1){
                    total+=count.toLong()
                    val progress_tmp = total.toInt()*100/lenghtOfFile
                    if(progress_tmp % 10 == 0 && progress != progress_tmp){
                        progress = progress_tmp
                    }
                    fos.write(data, 0, count)
                    count = isStream.read(data)
                }
                isStream.close()
            }catch(e:MalformedURLException){
                return "Malformed url"
            }catch(e: FileNotFoundException){
                return "File not found"
            }catch(e:IOException){
                return "Io Exception"
            }
            return "Success"
        }


    }

    fun downloadData(code: String){
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val prefix = preferences.getString("url_pref", getString(R.string.URL_prefix))
        val invd= InventoryDownloader()
        invd.execute(prefix+code)
    }

    fun addInventory(v:View){
        val dbHandler = MyDBHandler(this, null, null, 1)
        val code = findViewById<TextView>(R.id.code).getText().toString()
        val name = findViewById<TextView>(R.id.name).getText().toString()
        val inventory = Inventory(name, 1, 0)
        val inventoryID = dbHandler.addInventory(inventory)

        downloadData(code)

        addParts(inventoryID.toInt())

    }

    fun addParts(inventoryID: Int) {

        val filename = "inventory.xml"
        val path = filesDir
        val inDir = File(path, "XML")
        val dbHandler = MyDBHandler(this, null, null, 1)
        if (inDir.exists()){
            val file = File(inDir, filename)
            if(file.exists()){
                val xmlDoc : Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
                val items: NodeList = xmlDoc.getElementsByTagName("ITEM")
                for( i in 0..items.length-1){
                    val itemNode: Node = items.item(i)
                    if(itemNode.getNodeType() == Node.ELEMENT_NODE){
                        val elem = itemNode as Element
                        val children = elem.childNodes
                        var itemType: String = ""
                        var itemID: String = ""
                        var quantityInSet: Int = 0
                        var color: Int = 0
                        for (j in 0..children.length - 1){
                            val node = children.item(j)
                            if (node is Element){
                                when (node.nodeName){
                                    "ITEMTYPE" -> {itemType = node.textContent}
                                    "ITEMID" -> {itemID = node.textContent}
                                    "QTY" -> {quantityInSet = node.textContent.toInt()}
                                    "COLOR" -> {color = node.textContent.toInt()}
                                }
                            }

                        }
                        println(itemType)
                        println(itemID)
                        // TODO komunikat o nie istnieniu klocka i sprawdzenie
                        val itemTypeID = dbHandler.getTypeID(itemType)
                        val itemTableID = dbHandler.getItemID(itemID)
                        if(itemTypeID!= -1 && itemTableID != -1){
                            val part = InventoryPart(inventoryID, itemTypeID, itemTableID, quantityInSet, color)
                            dbHandler.addInventoryPart(part)
                            val code = dbHandler.getPartCode(itemTableID, color)
                            //getImage(itemTableID, color, itemID)

                        }


                    }
                }
            }
        }
    }

    fun getImage(itemTableID: Int, color:Int, itemID:String) {
        //FIXME
        val dbHandler = MyDBHandler(this, null, null, 1)
        var bmp: Bitmap
        val code = dbHandler.getPartCode(itemTableID, color)
       // val links = applicationContext.resources.getStringArray(R.array.links)
        val links: ArrayList<String> = ArrayList<String>()
        val link1 = getString(R.string.link1,code.toString())
        val link2 = getString(R.string.link2,color.toString(), itemID)
        val link3 = getString(R.string.link3,itemID)
        println(link1)
        println(link2)
        println(link3)
        println("------")
        links.add(link1)
        links.add(link2)
        links.add(link3)
        var responseCode = -1
        links.forEach {
            val url = URL(it)
            val con = url.openConnection() as HttpURLConnection
            con.doInput = true
            con.connect()
            responseCode = con.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                println("HTTP OK dl $it")
                try {
                    println("HTTP OK dl $it")
                    val inputStream = con.inputStream
                    bmp = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()

                    val stream = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()

                    //FIXME
                    //TODO
                    //dbHandler.addImage(byteArray, code)
                }catch(e:Exception){}
            }
        }

    }



}

