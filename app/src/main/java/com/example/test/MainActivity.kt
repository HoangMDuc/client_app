package com.example.test

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mService : Messenger? = null
    private  var isBound : Boolean = false

   private val mConnection = object : ServiceConnection  {
       override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
           Toast.makeText(applicationContext, "Connected", Toast.LENGTH_SHORT).show()
           mService = Messenger(service)
           isBound = true
       }

       override fun onServiceDisconnected(name: ComponentName?) {
           Toast.makeText(applicationContext, "Unconnected", Toast.LENGTH_SHORT).show()
           isBound = false
           mService = null
       }

   }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.sampleText.text = stringFromJNI()
        binding.button.setOnClickListener {
            handleClick()
        }
    }

    private fun handleClick() {
        Toast.makeText(this, isBound.toString(), Toast.LENGTH_SHORT).show()
        if(!isBound) return
        val msg : Message = Message.obtain(null, 1, 0 , 0)
        try {
            mService?.send(msg)
        }catch (e : RemoteException) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(this, "OK" , Toast.LENGTH_SHORT).show()
        val intent = Intent()
        intent.setComponent( ComponentName("com.example.kka", "com.example.kka.MyService"))
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE )
    }
    /**
     * A native method that is implemented by the 'test' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'test' library on application startup.
        init {
            System.loadLibrary("test")
        }
    }


}