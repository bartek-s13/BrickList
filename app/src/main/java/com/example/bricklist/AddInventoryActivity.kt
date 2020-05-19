package com.example.bricklist

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_add_inventory.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.*
import java.net.MalformedURLException
import java.net.URL
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
                val prefix = getString(R.string.URL_prefix)
                val url = URL(prefix+ params[0] +".xml")
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
        val invd= InventoryDownloader()
        invd.execute(code)
    }

    fun addInventory(v:View){
        val dbHandler = MyDBHandler(this, null, null, 1)
        val code = findViewById<TextView>(R.id.code).getText().toString()
        val name = findViewById<TextView>(R.id.name).getText().toString()
        val inventory = Inventory(name, 1, 0)
        val inventoryID = dbHandler.addInventory(inventory)
        downloadData(code)


    }

    fun addParts(inventoryID: Int) {
        val parts = ArrayList<InventoryPart>()
        val filename = "inventory.xml"
        val path = filesDir
        val inDir = File(path, "XML")

        if (inDir.exists()){
            val file = File(inDir, filename)
            if(file.exists()){
                val xmlDoc : Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
                val items: NodeList = xmlDoc.getElementsByTagName("item")
                for( i in 0..items.length-1){
                    val itemNode: Node = items.item(i)
                    if(itemNode.getNodeType() == Node.ELEMENT_NODE){
                        val elem = itemNode as Element
                        val children = elem.childNodes
                    }
                }
            }
        }
//TODO pobieranie zdjęcia



    }
}
