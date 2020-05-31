package com.example.bricklist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class PartsAdapter(private val parts: ArrayList<InventoryPart>) : RecyclerView.Adapter<PartsAdapter.ViewHolder>()  {

    lateinit var context: Context

    inner class ViewHolder(listItemView: View, context: Context) : RecyclerView.ViewHolder(listItemView) {
        public val nameTextView = itemView.findViewById<TextView>(R.id.name)
        public val colorTextView = itemView.findViewById<TextView>(R.id.color)
        public val qtyTextView = itemView.findViewById<TextView>(R.id.qty)
        public  val iView = itemView.findViewById<ImageView>(R.id.imageView)
        val addButton = itemView.findViewById<Button>(R.id.add)
        val subButton = itemView.findViewById<Button>(R.id.sub)
        val row = itemView.findViewById<TableRow>(R.id.p_row)


        fun bind(name:String, colorName:String, qtyStore:Int, qtySet:Int, img: Bitmap?)
        {
            nameTextView.setText(name)
            colorTextView.setText(colorName)
            qtyTextView.setText("${qtyStore} of ${qtySet}")
            img?.let{
                iView.setImageBitmap(img)
            }
            if(qtySet == qtyStore){
                qtyTextView.setTextColor(Color.GREEN)
                row.setBackgroundColor(0x804CAF50.toInt())
            }
            else{
                qtyTextView.setTextColor(Color.RED)
                row.setBackgroundColor(Color.WHITE)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartsAdapter.ViewHolder {
        this.context= parent.context
        val inflater = LayoutInflater.from(context)
        val partView: View = inflater.inflate(R.layout.part_row, parent, false)
        return ViewHolder(partView, context)
    }

    override fun getItemCount(): Int {
        return parts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val part: InventoryPart = parts.get(position)
        val helper = MyDBHandler(this.context,null,null, 1)
        val name = helper.getPartName(part.ItemID)
        val colorName = helper.getColorName(part.ColorID)


        val image = helper.getImage(part.ColorID, part.ItemID)
        var img: Bitmap? = null
        image?.let{
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            img = bmp
        }
        holder.addButton.setOnClickListener{
           if (part.QuantityInStore<part.QuantityInSet) {
               part.QuantityInStore += 1
               helper.updatePart(part)
               holder.bind(name, colorName, part.QuantityInStore, part.QuantityInSet,img)
           }
        }
        holder.subButton.setOnClickListener{
            if(part.QuantityInStore>0) {
                part.QuantityInStore-=1
                helper.updatePart(part)
                holder.bind(name, colorName, part.QuantityInStore, part.QuantityInSet,img)
            }
        }

        holder.bind(name, colorName, part.QuantityInStore, part.QuantityInSet, img)
    }
}