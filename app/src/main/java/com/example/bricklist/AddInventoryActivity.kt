package com.example.bricklist




import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import javax.xml.parsers.DocumentBuilderFactory


class AddInventoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_inventory)
        val button = findViewById<Button>(R.id.addButton)
        button.setEnabled(false)
        val codeTextView = findViewById<TextView>(R.id.code)
        codeTextView.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                button.setEnabled(false)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            super.finish()
        }
        return super.onOptionsItemSelected(item)
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
                var count = 0
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
        invd.execute(prefix+code).get()
    }

    private inner class Check:AsyncTask<String, Int, String>() {
        var result:Boolean = false
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            if(result=="true"){
                this.result = true
            }
            super.onPostExecute(result)
        }

        override fun doInBackground(vararg params: String?): String {
            try{
                HttpURLConnection.setFollowRedirects(false);
                val url = URL(params[0])
                val con =  url.openConnection() as HttpURLConnection
                con.setRequestMethod("HEAD")
                if(con.getResponseCode()==200){
                    this.result = true
                    return "true"
                }
                else{
                    this.result = false
                    return "false"
                }
            }catch (e:Exception) {
                this.result = false
                return "false"
            }
        }
    }

    fun checkInventoryExists(v:View){

        val codeTextView = findViewById<TextView>(R.id.code)
        val code = codeTextView.text
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val prefix = preferences.getString("url_pref", getString(R.string.URL_prefix))
        val address = prefix + code +".xml"
        val task = Check()
        val x = task.execute(address).get()
        if(task.result==false){
            val toast = Toast.makeText(applicationContext, "Wrong URL: $address", Toast.LENGTH_LONG)
            toast.show()
        }
        else{
            val button = findViewById<Button>(R.id.addButton)
            button.setEnabled(true)
        }
    }

    fun addInventory(v:View){
        val nameText = findViewById<TextView>(R.id.name)
        println(nameText.text)
        if(nameText.text.isEmpty()){
            val toast = Toast.makeText(applicationContext, "Name cannot be blank", Toast.LENGTH_LONG)
            toast.show()
        }else{
            val dbHandler = MyDBHandler(this, null, null, 1)
            val code = findViewById<TextView>(R.id.code).getText().toString()
            val name = findViewById<TextView>(R.id.name).getText().toString()

            val inventory = Inventory(name, 1, 0)

            val inventoryID = dbHandler.addInventory(inventory)
            downloadData(code)

            val notAdded:ArrayList<String> = addParts(inventoryID.toInt())

            if(notAdded.size > 0 ){
                val builder = AlertDialog.Builder(this)
                builder.setTitle("These parts could not be added:")

                builder.setItems(notAdded.toTypedArray(), null)
                builder.setPositiveButton("OK"){_,_ ->
                    finish()
                }
                val dialog = builder.create()
                dialog.show()
            }else {
                finish()
            }
        }

    }

    fun addParts(inventoryID: Int):ArrayList<String> {
        val notAddedParts = ArrayList<String>()
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
                                    "ITEMID" -> {itemID = node.textContent}  //code
                                    "QTY" -> {quantityInSet = node.textContent.toInt()}
                                    "COLOR" -> {color = node.textContent.toInt()}
                                }
                            }
                        }

                        val itemTypeID = dbHandler.getTypeID(itemType)
                        val itemTableID = dbHandler.getItemID(itemID)
                        if(itemTypeID!= -1 && itemTableID != -1){
                            val colorid = dbHandler.getColorID(color)
                            val part = InventoryPart(inventoryID, itemTypeID, itemTableID, quantityInSet, colorid)
                            dbHandler.addInventoryPart(part)

                            val code = dbHandler.getPartCode(itemTableID, colorid)

                            val imgDownloader = ImageDownloader()
                            imgDownloader.execute(colorid.toString(), itemID, code.toString(), itemTableID.toString())
                        }
                        else{
                            val colorid = dbHandler.getColorID(color)
                            val colorName = dbHandler.getColorName(colorid)
                            notAddedParts.add("itemId: $itemID color: $colorName")
                        }
                    }
                }
            }
        }

        return notAddedParts
    }

    private inner class ImageDownloader:AsyncTask<String, Int, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

        override fun doInBackground(vararg params: String?): String {
            // color, itemID, code, itemTableID
            var bmp: Bitmap

            val links: ArrayList<String> = ArrayList<String>()
            val link1 = getString(R.string.link1, params[2])
            val link2 = getString(R.string.link2, params[0], params[1])
            val link3 = getString(R.string.link3, params[1])


            links.add(link1)
            links.add(link2)
            links.add(link3)

            links.forEach {
                try{

                    val url = URL(it)
                    val connection = url.openConnection()
                    connection.connect()
                    val lenghtOfFile = connection.contentLength
                    val isStream = url.openStream()
                    val data = ByteArray(1024)
                    val image = ByteArrayOutputStream();
                    var count = 0
                    var total: Long = 0
                    var progress = 0
                    count = isStream.read(data)
                    while(count!=-1){
                        total+=count.toLong()
                        val progress_tmp = total.toInt()*100/lenghtOfFile
                        if(progress_tmp % 10 == 0 && progress != progress_tmp){
                            progress = progress_tmp
                        }
                        image.write(data, 0, count)
                        count = isStream.read(data)
                    }
                    isStream.close()
                    image.close()

                    val img = BitmapFactory.decodeByteArray(image.toByteArray(),0, image.toByteArray().size)
                    addImage(img,params[0]!!, params[3]!!)
                    return "Success"
                }catch(e:Exception){
                    //println("Error for $it")
                }
            }
            //TODO add default image

            return "Error"
        }
    }

    fun addImage(img:Bitmap, color: String, id:String){
        val dbHandler = MyDBHandler(this, null, null, 1)
        val bos = ByteArrayOutputStream()
        img.compress(Bitmap.CompressFormat.PNG,100, bos)
        val bArray = bos.toByteArray()
        bos.close()
        dbHandler.addImage(bArray, color.toInt(), id.toInt())
    }
}

