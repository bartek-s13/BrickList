package com.example.bricklist

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PartsAdapter(private val parts: ArrayList<InventoryPart>) : RecyclerView.Adapter<PartsAdapter.ViewHolder>()  {

    lateinit var context: Context

    inner class ViewHolder(listItemView: View, context: Context) : RecyclerView.ViewHolder(listItemView) {
        public val nameTextView = itemView.findViewById<TextView>(R.id.name)
        public val colorTextView = itemView.findViewById<TextView>(R.id.color)
        public val qtyTextView = itemView.findViewById<TextView>(R.id.qty)
        val addButton = itemView.findViewById<Button>(R.id.add)
        val subButton = itemView.findViewById<Button>(R.id.sub)



        fun bind(name:String, colorName:String, qtyStore:Int, qtySet:Int)
        {
            // TODO pobieranie zdjęcia



            nameTextView.setText(name)
            colorTextView.setText(colorName)
            qtyTextView.setText("${qtyStore} of ${qtySet}")

            if(qtySet == qtyStore){
                qtyTextView.setTextColor(Color.GREEN)
            }
            else{
                qtyTextView.setTextColor(Color.RED)
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

        holder.addButton.setOnClickListener{
           if (part.QuantityInStore<part.QuantityInSet) {
               part.QuantityInStore += 1
               helper.updatePart(part)
               holder.bind(name, colorName, part.QuantityInStore, part.QuantityInSet)
           }
        }
        holder.subButton.setOnClickListener{
            if(part.QuantityInStore>0) {
                part.QuantityInStore-=1
                helper.updatePart(part)
                holder.bind(name, colorName, part.QuantityInStore, part.QuantityInSet)
            }
        }

        holder.bind(name, colorName, part.QuantityInStore, part.QuantityInSet)
    }
}