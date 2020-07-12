package com.example.musicapp


import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class MusicAdapter(private var songlist:MutableList<String>, var activity: Activity) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = View.inflate(activity,R.layout.music_list,null)


        val music = view.findViewById<TextView>(R.id.textView) as TextView
        music.text = songlist[position]

        return view
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItem(position: Int): Any {
        return songlist[position]
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemId(position: Int): Long {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return position.toLong()
    }

    override fun getCount(): Int {
        return songlist.size
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}