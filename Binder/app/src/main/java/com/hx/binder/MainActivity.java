package com.hx.binder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button bindService;
    private Button unbindService;
    private Button plus;
    private Button toUpperCase;
    private Button doubleAge;
    private IMyAidlInterface myAIDLInterface;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            myAIDLInterface = null;
            Toast.makeText(MainActivity.this, "onServiceDisconnected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myAIDLInterface = IMyAidlInterface.Stub.asInterface(service);
            Toast.makeText(MainActivity.this, "onServiceConnected", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService = (Button) findViewById(R.id.bind_service);
        unbindService = (Button) findViewById(R.id.unbind_service);
        plus = (Button) findViewById(R.id.plus);
        toUpperCase = (Button) findViewById(R.id.toUpperCase);
        doubleAge = (Button) findViewById(R.id.doubleAge);
        //button点击事件
        bindService.setOnClickListener(this);
        unbindService.setOnClickListener(this);
        plus.setOnClickListener(this);
        toUpperCase.setOnClickListener(this);
        doubleAge.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind_service:
                Intent intent = new Intent("com.hx.action.remoteService");
                //5.0以上安卓设备，service intent必须为显式指出
                Intent eintent = new Intent(getExplicitIntent(this,intent));
                bindService(eintent, connection, Context.BIND_AUTO_CREATE);
//              bindService(intent, connection, BIND_AUTO_CREATE);
                break;
            case R.id.unbind_service:
                if(myAIDLInterface != null){
                    unbindService(connection);
                }
                break;
            case R.id.plus:
                if (myAIDLInterface != null) {
                    try {
                        int result = myAIDLInterface.plus(13, 19);
                        Toast.makeText(this, result + "", Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "服务器被异常杀死，请重新绑定服务端", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.toUpperCase:
                if (myAIDLInterface != null) {
                    try {
                        String upperStr = myAIDLInterface.toUpperCase("hello aidl service");
                        Toast.makeText(this, upperStr + "", Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "服务器被异常杀死，请重新绑定服务端", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.doubleAge:
                if (myAIDLInterface != null) {
                    try {
                        Student student_before= new Student();
                        student_before.setName("jack");
                        student_before.setAge(18);
                        Student student_after = myAIDLInterface.doubleAge(student_before);
                        Toast.makeText(this, "student age after modify is " + student_after.getAge(), Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "服务器被异常杀死，请重新绑定服务端", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
}
