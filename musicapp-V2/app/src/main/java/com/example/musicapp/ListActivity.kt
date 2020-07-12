package com.example.musicapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import java.util.ArrayList


var songList: MutableList<String> = ArrayList()

class ListActivity : AppCompatActivity() {



    var list_music: ListView? = null
    var adapter: MusicAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_list_layout)

        list_music = findViewById<ListView>(R.id.myListView) as ListView
        if(songList.isEmpty()){
            addsongs()
        }

        var adapter = MusicAdapter(songList, this)
        list_music?.adapter = adapter

        list_music!!.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = songList.get(position)
                val intent = Intent(this@ListActivity, MusicPlayer::class.java)

                intent.putExtra("song", item)
                intent.putExtra("position", position)
                startActivity(intent)
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }

}

fun addsongs(){
    val fields = R.raw::class.java.fields
    for ( i in fields.indices) {
        songList.add (fields[i].name)
        Log.i("fields", fields[i].name)
    }
}
