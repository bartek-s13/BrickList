package com.example.bricklist

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.lang.Integer.max
import java.lang.Integer.min


class SwipeToArchiveCallback(context: Context, adapter: InventoriesAdapter):ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ){



    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.getAdapterPosition()
        println(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView: View = viewHolder.itemView
        val paint = Paint()
        paint.setColor(Color.WHITE)
        paint.setTextSize(80f)
        paint.setTextAlign(Paint.Align.CENTER)
        val inbox: String = "Archive"
        val background = ColorDrawable()

        background.setBounds(itemView.right+max(dX.toInt(),(dX/3).toInt()), itemView.top,  itemView.right, itemView.bottom)
        background.color = Color.GRAY
        background.draw(c)

        c.drawText(inbox, itemView.getRight().toFloat()-200.0f , itemView.bottom - (itemView.bottom - itemView.top-80)/2f, paint);

        super.onChildDraw(c, recyclerView, viewHolder, dX/3, dY, actionState, isCurrentlyActive)
    }
}