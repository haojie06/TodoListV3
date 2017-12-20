package com.example.haojie06.todolist.Notice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.haojie06.todolist.MainActivity;
import com.example.haojie06.todolist.R;
import com.example.haojie06.todolist.Things;

import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClockService extends Service {
    //广播接收器动态注册
    int flag = 0;
    private IntentFilter intentFilter;
    private TimeChangeReceiver timeChangeReceiver;

    public ClockService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("onCreate", "service create");
        intentFilter = new IntentFilter();
        //接收每分钟系统发出的广播
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        timeChangeReceiver = new TimeChangeReceiver();
        //动态注册 timechangereceiver 便可收到 TIME_TICK 的广播
        registerReceiver(timeChangeReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, final int startId) {
        //在一个新的线程中执行
        Log.e("onStart", "service start");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.w("run", "turn to start");
                if (flag == 1) {
                    Log.w("start", "GET DATABASE");
                    String nYear, nMonth, nDay, nHour, nMinute;
                    Connector.getDatabase();
                    List<Things> compareList = DataSupport.findAll(Things.class);
                    //获得当前时间

                    Calendar d = Calendar.getInstance(Locale.CHINA);
                    // 创建一个日历引用d，通过静态方法getInstance() 从指定时区 Locale.CHINA 获得一个日期实例
                    Date myDate = new Date();
                    // 创建一个Date实例
                    d.setTime(myDate);

                    // 设置日历的时间，把一个新建Date实例myDate传入

                    int year = d.get(Calendar.YEAR);
                    int month = d.get(Calendar.MONTH);
                    int day = d.get(Calendar.DAY_OF_MONTH);
                    int hour = d.get(Calendar.HOUR_OF_DAY);
                    int minute = d.get(Calendar.MINUTE);

                    nYear = String.valueOf(year);
                    if (month < 10) {
                        nMonth = "0" + String.valueOf(month);
                    } else {
                        nMonth = String.valueOf(month);
                    }

                    if (day < 10) {
                        nDay = "0" + String.valueOf(day);
                    } else {
                        nDay = String.valueOf(day);
                    }

                    if (hour < 10) {
                        nHour = "0" + String.valueOf(hour);
                    } else {
                        nHour = String.valueOf(hour);
                    }

                    if (minute < 10) {
                        nMinute = "0" + String.valueOf(minute);
                    } else {
                        nMinute = String.valueOf(minute);
                    }

                    String nTime = nYear + nMonth + nDay + nHour + nMinute;
                    Log.e("----------------nowTime", nTime);

                    for (Things things : compareList) {
                        Log.e("equal", "时间判断之前");
                        if (nTime.equals(things.getClockTime())) {
                            Log.e("equal", "设定的时间与现在的时间相等了！！！！！！");
                            String content = things.getContent();
                            // 获取系统默认铃声的Uri
                            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                            Notification notification = new Notification();
                            notification.sound = uri;
                            /**
                             * 手机处于锁屏状态时，呼吸灯
                             *
                             notification.ledARGB = Color.GREEN;// 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
                             notification.ledOnMS = 1000;// 指定 LED 灯亮起的时长，以毫秒为单位
                             notification.ledOffMS = 1000;// 指定 LED 灯暗去的时长，也是以毫秒为单位
                             notification.flags = Notification.FLAG_SHOW_LIGHTS;// 指定通知的一些行为，其中就包括显示
                             */Intent intent = new Intent(getApplication(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            PendingIntent contentIntent = PendingIntent.getActivity(
                                    ClockService.this, 0, new Intent(ClockService.this, MainActivity.class), 0);
                            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(ClockService.this)
                                            .setContentTitle(content)
                                            .setContentText("定时提醒")
                                            .setWhen(System.currentTimeMillis())
                                            .setSmallIcon(R.mipmap.ic_launcher_round)
                                            .setSound(uri)
                                            .setVibrate(new long[]{0, 1000, 1000, 1000, 500, 1000, 500, 1000})
                                            .setAutoCancel(true)
                                            .setContentIntent(contentIntent);
                            //  .build();
                            manager.notify(1, mBuilder.build());
                        } else {
                            Log.w("equal", "不想等");
                        }
                    }
                }
                //在接收到时间变化后与数据库进行比较,如果相同发出提示

                //flag归零
                flag = 0;
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册
        unregisterReceiver(timeChangeReceiver);
        Toast.makeText(this, "Service end", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class TimeChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("receiver", "TIME CHANGE");
            flag = 1;
            Intent startService = new Intent(getApplicationContext(), ClockService.class);
            startService(startService);
        }
    }
}
