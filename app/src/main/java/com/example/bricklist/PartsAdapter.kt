package com.example.bricklist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PartsAdapter(private val parts: ArrayList<InventoryPart>) : RecyclerView.Adapter<PartsAdapter.ViewHolder>()  {
    inner class ViewHolder(listItemView: View, context: Context) : RecyclerView.ViewHolder(listItemView) {
        public val nameTextView = itemView.findViewById<TextView>(R.id.name)
        public val colorTextView = itemView.findViewById<TextView>(R.id.color)
        public val qtyTextView = itemView.findViewById<TextView>(R.id.qty)
        val addButton = itemView.findViewById<Button>(R.id.add)
        val subButton = itemView.findViewById<Button>(R.id.sub)
        val helper = MyDBHandler(context,null,null, 1)


        fun bind(part: InventoryPart)
        {
            // TODO pobieranie potrzebnych informacji

            val name = helper.getPartName(part.ItemID)
            val colorName = helper.getColorName(part.ColorID)

            nameTextView.setText(name)
            colorTextView.setText(colorName)
            qtyTextView.setText("${part.QuantityInStore} of ${part.QuantityInSet}")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartsAdapter.ViewHolder {
        val context: Context = parent.context
        val inflater = LayoutInflater.from(context)
        val partView: View = inflater.inflate(R.layout.part_row, parent, false)

        return ViewHolder(partView, context)
    }

    override fun getItemCount(): Int {
        return parts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val part: InventoryPart = parts.get(position)
        holder.bind(part)

    }


}