package com.hx.binder_server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.hx.binder.IMyAidlInterface;
import com.hx.binder.Student;

public class MyRemoteService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        MainActivity.showlog("onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MainActivity.showlog("onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.showlog("onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        MainActivity.showlog("onBind()");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MainActivity.showlog("onUnbind()");
        return super.onUnbind(intent);
    }

    IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub() {
        @Override
        public String toUpperCase(String str) throws RemoteException {
            if (str != null) {
                return str.toUpperCase();
            }
            return null;
        }

        @Override
        public int plus(int a, int b) throws RemoteException {
            return a + b;
        }

        @Override
        public Student doubleAge(Student student) throws RemoteException {
            Student s = student;
            int age = s.getAge();
            s.setAge(2*age);
            return s;
        }
    };
}
