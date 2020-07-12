package com.example.musicapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.DEVICE_NAME
import android.support.v4.app.ActivityCompat
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_music_player.*


class MusicPlayer : AppCompatActivity() , BLE.Callback {

    var songimg: ImageView? = null
    private var currentSong: Int = 0
    internal lateinit var songtitle: TextView
    private var mediaPlayer: MediaPlayer? = null
    private var ble: BLE? = null
    private var messages: TextView? = null
    private var playing = false
    private var gesture = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        val play = findViewById<ImageButton>(R.id.play)
        val pause = findViewById<ImageButton>(R.id.pause)
        val next = findViewById<ImageButton>(R.id.next)
        val previous = findViewById<ImageButton>(R.id.previous)
        connectButt.setBackgroundColor(Color.argb(255, 0, 0, 255))

        songtitle = findViewById<TextView>(R.id.textView)

        //val volumeSeekBar = findViewById<SeekBar>(R.id.seekBar)
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //volumeSeekBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        //volumeSeekBar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        //volumeSeekBar.setOnSeekBarChangeListener()

        val adapter: BluetoothAdapter?
        adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter != null) {
            if (!adapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)

            }
        }

        // Get bluetooth
        messages = findViewById(R.id.bluetoothText)
        messages!!.movementMethod = ScrollingMovementMethod()
        ble = BLE(applicationContext, DEVICE_NAME)

        // Check permissions
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1
        )



        Log.i("Oncreate", "InOnCreate")
        play.setOnClickListener {
            playSong()
        }

        pause.setOnClickListener {
            pauseSong()
        }

        next.setOnClickListener {
            nextSong()
        }

        previous.setOnClickListener {
            previousSong()
        }
        Log.i("AfterListner", "InOnCreate")
        //var songList = ArrayList<String>()
        //var fields = resources.assets.list("/Users/ashikarohit/Downloads/musicapp/app/src/main/assets")
        var position: Int = intent.getIntExtra("position", 0)
        currentSong = position
        songtitle.text = songList.get(position)
        playSong()

    }

    private fun playSong() {
        songtitle.text = songList.get(currentSong)
        if (mediaPlayer != null) {
            mediaPlayer?.release()
        }
        var resID = resources.getIdentifier(songList.get(currentSong), "raw", packageName)
        mediaPlayer = MediaPlayer.create(this, resID)
        mediaPlayer!!.start()
        songtitle.text = songList.get(currentSong)
        play.visibility = View.INVISIBLE
        pause.visibility = View.VISIBLE
        playing = true
    }

    private fun pauseSong() {
        mediaPlayer?.pause()
        pause.visibility = View.INVISIBLE
        play.visibility = View.VISIBLE
        playing = false
    }

    private fun nextSong() {
        currentSong = (currentSong + 1) % songList.size
        playSong()
        playing = true
    }

    private fun previousSong() {
        if (currentSong == 0) {
            currentSong = songList.size-1
        } else {
            currentSong = currentSong - 1
        }
        playSong()
       // playing = true
    }


    override fun onBackPressed() {
        pauseSong()
        finish()
    }
/*
    fun Red (v: View){
        ble!!.send("red")

    }

    fun Start_widget(){
        ble!!.send("start")
    }

    fun Green (v: View){
        ble!!.send("green")

    }

    fun Yellow (v: View){
        ble!!.send("yellow")

    }

    fun Blue (v: View){
        ble!!.send("blue")

    }

    fun Start (v: View){
        ble!!.send("start")

    }

    fun Interval (v: View){

       // val msg: String = editText_interval.text.toString()

        //check if the EditText have values or not
        if(msg.trim().length>0) {
            ble!!.send(msg)
        }



    }


    fun clearText (v: View){
        messages!!.text=""

    }*/

    override fun onResume() {
        super.onResume()
        //updateButtons(false)
        ble!!.registerCallback(this)


    }


    override fun onStop() {
        super.onStop()
        ble!!.unregisterCallback(this)
        ble!!.disconnect()
    }

    fun connect(v: View) {
        //connectButt.setBackgroundColor(0xECE766)
        if(gesture == false){
            startScan()
            gesture = true
            connectButt.setBackgroundColor(Color.argb(255, 255, 255, 0))
        }else{
            connectButt.setBackgroundColor(Color.argb(255, 0, 0, 255))
            ble!!.disconnect()
        }

    }

    private fun startScan() {
        writeLine("Scanning for devices ...")
        ble!!.connectFirstAvailable()
    }


    /**
     * Figure out which button got pressed to
     */
    fun buttTouch(v: View) {
        ble!!.send("readtemp")
        Log.i("BLE", "READ TEMP")
    }


    /**
     * Writes a line to the messages textbox
     * @param text: the text that you want to write
     */
    private fun writeLine(text: CharSequence) {
        runOnUiThread {
            messages!!.append(text)
            messages!!.append("\n")
        }
    }

    /**
     * Called when a UART device is discovered (after calling startScan)
     * @param device: the BLE device
     */
    override fun onDeviceFound(device: BluetoothDevice) {
        writeLine("Found device : " + device.name)
        writeLine("Waiting for a connection ...")

    }

    /**
     * Prints the devices information
     */
    override fun onDeviceInfoAvailable() {
        writeLine(ble!!.deviceInfo)
    }

    /**
     * Called when UART device is connected and ready to send/receive data
     * @param ble: the BLE UART object
     */
    override fun onConnected(ble: BLE) {
        writeLine("Connected!")
        connectButt.setBackgroundColor(Color.argb(255, 0, 255, 0))

    }

    /**
     * Called when some error occurred which prevented UART connection from completing
     * @param ble: the BLE UART object
     */
    override fun onConnectFailed(ble: BLE) {
        writeLine("Error connecting to device!")
        connectButt.setBackgroundColor(Color.argb(255, 0, 0, 255))

    }
    /**
     * Called when the UART device disconnected
     * @param ble: the BLE UART object
     */
    override fun onDisconnected(ble: BLE) {
        writeLine("Disconnected!")
        connectButt.setBackgroundColor(Color.argb(255, 0, 0, 255))
    }

    /**
     * Called when data is received by the UART
     * @param ble: the BLE UART object
     * @param rx: the received characteristic
     */
    override fun onReceive(ble: BLE, rx: BluetoothGattCharacteristic) {
        var a = rx.getStringValue(0)
        if (a == "shake") {
            if (playing) {
                pauseSong()
                playing = false
            } else {
                playSong()
                playing = true
            }
            writeLine("Found shake");
        } else if (a == "next") {
            writeLine("Found next");
            playing = true
            nextSong()
        } else if (a == "prev") {
            writeLine("Found prev");
            playing = true
            previousSong()
        }

    }

    companion object {
        private val DEVICE_NAME = "PMP590 is awesome"
        private val REQUEST_ENABLE_BT = 0
    }
}