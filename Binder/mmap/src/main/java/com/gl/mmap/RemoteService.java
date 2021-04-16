package com.gl.mmap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

import java.io.FileDescriptor;
import java.lang.reflect.Method;


public class RemoteService extends Service {

   @Override
   public IBinder onBind(Intent intent) {
       return new MyBinder();
   }

   public class MyBinder extends Binder {
       @Override
       protected boolean onTransact(int code, Parcel data,
                                    Parcel reply, int flags) throws RemoteException {
           if (code == 1) {
               try {
                   String str = "kobewang";
                   byte[] contentBytes = str.getBytes();
                   //创建匿名共享内存
                   MemoryFile mf = new MemoryFile("memfile", contentBytes.length);
                   //写入字符数据
                   mf.writeBytes(contentBytes, 0, 0, contentBytes.length);
                   Method method = MemoryFile.class.getDeclaredMethod("getFileDescriptor");
                   //通过反射获得文件句柄
                   FileDescriptor fd = (FileDescriptor) method.invoke(mf);
                   ParcelFileDescriptor pfd = ParcelFileDescriptor.dup(fd);
                   //将文件句柄写到binder调用的返回值中。
                   reply.writeFileDescriptor(fd);
                   return true;
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
           return super.onTransact(code, data, reply, flags);
       }
   }
}