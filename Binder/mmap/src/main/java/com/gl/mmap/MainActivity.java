package com.gl.mmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileReader;


/**
 * 1.Binder机制无法跨进程传输超过1M的数据
 * 2.匿名共享内存并没有大小的限制，适合跨进程传输较大的数据
 * 3.匿名共享内存需要先通过Binder传递共享内存的文件句柄
 * PS：机智的小伙伴可能已经发现，我并没有使用AIDL，而是直接裸写了binder的使用，其实裸写一次以后有助于理解AIDL
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定服务
        Intent intent = new Intent(this, RemoteService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    //通过binder机制跨进程调用服务端的接口
                    service.transact(1, data, reply, 0);
                    //获得RemoteService创建的匿名共享内存的fd
                    FileDescriptor fd = reply.readFileDescriptor().getFileDescriptor();
                    //读取匿名共享内存中的数据，并打印log
                    BufferedReader br = new BufferedReader(new FileReader(fd));
                    Log.v("kobe-result", br.readLine());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

        }, Context.BIND_AUTO_CREATE);
    }
}