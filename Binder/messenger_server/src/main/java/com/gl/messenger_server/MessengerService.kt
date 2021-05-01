package com.gl.messenger_server

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log


/***
 * 运行以后 这个服务 可以使用 Messenger的方式进行AIDL
 * 详细可以参考  https://juejin.cn/post/6906688564071923719#heading-10
 */

//step1:继承Service
class MessengerService : Service() {

    //step2:新建类继承Handler
    class MessengerHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val acceptBundle = msg.obj as Bundle
            when (msg.what) {
                10001 -> {
                    val info = acceptBundle.get("data") as String
                    log("服务端:收到String类型的数据:$info")

                    //回复信使
                    val messenger = msg.replyTo as Messenger
                    val message: Message = Message.obtain(null, msg.what)
                    val bundle = Bundle()
                    bundle.putString("data", "我是服务端,我收到了你的消息{$info}")
                    message.obj = bundle
                    messenger.send(message)
                }
                10002 -> {
                    val index = acceptBundle.get("index") as Int
                    log("服务端:收到Int类型的数据:$index")

                    //回复信使
                    val messenger = msg.replyTo as Messenger
                    val message: Message = Message.obtain(null, msg.what)
                    val bundle = Bundle()
                    bundle.putInt("index", 10086)
                    message.obj = bundle
                    messenger.send(message)
                }

                10003 -> {
                    val user = acceptBundle.get("user") as User
                    log("服务端:收到自定义类型的数据:$user")

                    //回复信使
                    val messenger = msg.replyTo as Messenger
                    val message: Message = Message.obtain(null, msg.what)
                    val bundle = Bundle()
                    bundle.putSerializable("user",User("allens",20))
                    message.obj = bundle
                    messenger.send(message)
                }
            }
        }

        private fun log(info: String) {
            Log.i("sample-allens", info)
        }
    }


    //step3:创建信使
    private val mMessenger = Messenger(MessengerHandler())

    override fun onBind(intent: Intent?): IBinder? {
        //step4:将Messenger对象的Binder返回给客户端
        return mMessenger.binder
    }

}