package com.gl.messenger_client

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.gl.messenger_client.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {


    //step1:新建类继承Handler
    class ClientHandler(private val activity: MainActivity) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                10001 -> {
                    val info = (msg.obj as Bundle).get("data") as String
                    activity.log("收到回信:$info")
                }
                10002 -> {
                    val info = (msg.obj as Bundle).get("index") as Int
                    activity.log("收到回信:$info")
                }
                10003 -> {
                    val info = (msg.obj as Bundle).get("user") as User
                    activity.log("收到回信:$info")
                }
            }
        }
    }

    //step2:创建信使
    private val clientMessenger: Messenger = Messenger(ClientHandler(this))

    //step3:创建连接接口
    private var serverMessenger: Messenger? = null

    private val connect = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            //获取到服务端的Messenger
            serverMessenger = Messenger(service)
            log("service connect")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            log("service disconnect")
        }
    }


    companion object {
        const val PKG = "com.gl.messenger_server"
        const val CLS = "com.gl.messenger_server.MessengerService"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //使用此用例之前 请先运行 sample-service
        binding.linear.addView(MaterialButton(this).apply {
            text = "绑定服务"
            setOnClickListener {
                //step4:使用bindService连接
                val intent = Intent()
                //参数1:appId
                //参数2:对应Service的路劲
                intent.component = ComponentName(PKG, CLS)
                //参数1:当前的intent意图。
                //参数2:连接监听器
                //参数3:类型 BIND_AUTO_CREATE:只要绑定存在，就自动创建服务
                val bindService = context.bindService(intent, connect, BIND_AUTO_CREATE)
                if (!bindService) {
                    log("绑定服务失败")
                }
            }
        })



        binding.linear.addView(MaterialButton(this).apply {
            text = "解除绑定"
            setOnClickListener {
                context.unbindService(connect)
            }
        })

        binding.linear.addView(MaterialButton(this).apply {
            text = "发送String类型"
            setOnClickListener {
                //创建Message
                val message: Message = Message.obtain(null, 10001)
                val bundle = Bundle()
                bundle.putString("data", "hello 你好")
                message.obj = bundle
                //将客户端的Messenger发给服务端
                message.replyTo = clientMessenger
                //使用send方法发送
                serverMessenger?.send(message)
            }
        })

        binding.linear.addView(MaterialButton(this).apply {
            text = "发送Int类型"
            setOnClickListener {
                //创建Message
                val message: Message = Message.obtain(null, 10002)
                val bundle = Bundle()
                bundle.putInt("index", 22)
                message.obj = bundle
                //将客户端的Messenger发给服务端
                message.replyTo = clientMessenger
                //使用send方法发送
                serverMessenger?.send(message)
            }
        })

        binding.linear.addView(MaterialButton(this).apply {
            text = "发送自定义类型"
            setOnClickListener {
                //创建Message
                val message: Message = Message.obtain(null, 10003)
                val bundle = Bundle()
                bundle.putSerializable("user",User("江海洋",18))
                message.obj = bundle
                //将客户端的Messenger发给服务端
                message.replyTo = clientMessenger
                //使用send方法发送
                serverMessenger?.send(message)
            }
        })

        //添加TextView 显示日志信息
        binding.linear.addView(tv)
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