package com.example.haojie06.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

//利用该广播接收器实现开机启动程序
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "开机启动To do list", Toast.LENGTH_LONG).show();

    }
}
