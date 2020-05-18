package com.example.bricklist

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_add_inventory.*
import java.io.*
import java.net.MalformedURLException
import java.net.URL

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
                println("Malformed url")
                return "Malformed url"
            }catch(e: FileNotFoundException){
                println("File not found")
                return "File not found"
            }catch(e:IOException){
                println("Io Exception")
                return "Io Exception"
            }
            println("Succcess")
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
        dbHandler.addInventory(inventory)
        downloadData(code)


    }

    fun parse_xml(path:String, fileName:String){

    }
}
