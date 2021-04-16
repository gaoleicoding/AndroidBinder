package com.gl.binder_pool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

/**
 * 一、为什么要有Binder连接池？
 * 产生原因：因为当有多个不同的业务块都要使用AIDL来进行通信，则需要创建多个Service，每创建一个Service就需要消耗系统资源。
 * 解决思路：将所有的AIDL放在一个Service中处理
 *
 * 二、实现步骤：
 * 1. 首先，为每个业务模块创建AIDL接口并实现此接口及其业务方法。
 * 2. 创建IBinderPool的AIDL接口，定义IBinder queryBinder(int BinderCode)方法。外部通过调用此方法传入对应的code值来获取对应的Binder对象。
 * 3. 创建BinderPoolService，通过new BinderPool.BinderPoolImpl实例化Binder对象，通过onBind方法返回出去。
 * 4. 创建BinderPool类，单例模式，在构造方法中绑定Service，在onServiceConnected方法获取到BinderPoolImpl对象，
 *    这个BinderPoolImpl类是BinderPool的内部类，并实现了IBinderPool的业务方法。BinderPool类中向外暴露了queryBinder方法，
 *    这个方法其实调用的是BinderPoolImpl对象的queryBinder方法。
 */
public class BinderPool {

    private static final String TAG = "BinderPool" ;

    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SECURITY_CENTER = 1;

    // 编译器每次都需要从主存中读取
    private IBinderPool mBinderPool;
    private static volatile BinderPool sInstance;
    private Context mContext;

    private CountDownLatch mCountDownLatch; // 同步机制

    private BinderPool(Context context) {
        mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    // 单例
    public static BinderPool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BinderPool.class) {
                if (sInstance == null) {
                    sInstance = new BinderPool(context);
                }
            }
        }
        return sInstance;
    }

    // 连接服务池
    private synchronized void connectBinderPoolService() {
        mCountDownLatch = new CountDownLatch(1); // 只保持一个绑定服务
        Intent service = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(service, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 失效重联机制, 当Binder死亡时, 重新连接
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.e(TAG, "Binder失效");
            mBinderPool.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mBinderPool = null;
            connectBinderPoolService();
        }
    };

    // Binder的服务连接
    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 查询Binder
     *
     * @param binderCode binder代码
     * @return Binder
     */
    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        try {
            if (mBinderPool != null) {
                binder = mBinderPool.queryBinder(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return binder;
    }

    /**
     * Binder池实现
     */
    public static class BinderPoolImpl extends IBinderPool.Stub {
        public BinderPoolImpl() {
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_COMPUTE:
                    binder = new ComputeImpl();
                    break;
                case BINDER_SECURITY_CENTER:
                    binder = new SecurityCenterImpl();
                    break;
                default:
                    break;
            }
            return binder;
        }
    }
}
