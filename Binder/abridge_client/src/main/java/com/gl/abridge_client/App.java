package com.gl.abridge_client;

import android.app.Application;

import com.sjtu.yifei.IBridge;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //注意这里的packagename，需要通信的多个app只能使用一个packagename
        //即使用一个app作为server启动这个共享服务来进行通信
    }
    @Override
    public void onTerminate() {

        super.onTerminate();
    }
}