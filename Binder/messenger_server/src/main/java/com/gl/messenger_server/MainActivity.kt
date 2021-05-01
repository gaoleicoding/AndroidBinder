package com.gl.messenger_server

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import com.gl.messenger_server.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    private val tv by lazy { AppCompatTextView(this) }

    private val handler = Handler(Looper.getMainLooper())

    private fun log(info: String) {
        Log.i("sample-allens", info)
        handler.post {
            tv.append(info)
            tv.append("\n")
        }
    }
}