package com.example.haojie06.todolist;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.example.haojie06.todolist.Notice.ClockService;
import com.example.haojie06.todolist.Notice.ThingsAdapter;
import com.example.haojie06.todolist.gson.WeatherBean;
import com.example.haojie06.todolist.util.HttpUtil;
import com.example.haojie06.todolist.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.example.haojie06.todolist.gson.WeatherBean;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    //定位
    public LocationClient mLocationClient;
    private TextView weatherText;
    public List<Things> thingsList;
    String longitude,latitude,city,location;//用于获取天气情况
    //天气情况
    TextView temp,cityName;
    String temperature,pm,weather;
    String Num;
    TextView undoNum;
    ThingsAdapter adapter;
    ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            final int dragFlags;
            final int swipeFlags;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                swipeFlags = 0;
            } else {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            }
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();//得到拖动的holder的position

            int toPosition = target.getAdapterPosition();//得到目标holder的position
            int temp = 0;
            //执行交换
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    temp = 0;
                    temp = thingsList.get(i).getnum();
                    thingsList.get(i).setnum(thingsList.get(i + 1).getnum());
                    thingsList.get(i).save();
                    thingsList.get(i + 1).setnum(temp);
                    thingsList.get(i + 1).save();
                    Collections.swap(thingsList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    temp = 0;
                    temp = thingsList.get(i).getnum();
                    thingsList.get(i).setnum(thingsList.get(i - 1).getnum());
                    thingsList.get(i - 1).setnum(temp);
                    thingsList.get(i).save();
                    thingsList.get(i - 1).save();
                    Collections.swap(thingsList, i, i - 1);
                }
            }

            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            Things delThing = thingsList.get(position);
            delThing.delete();
            thingsList.remove(position);
            Num = String.valueOf(thingsList.size());
            undoNum.setText(Num);
            adapter.notifyItemRemoved(position);

        }

        /*拖拽时改变颜色
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE){
                viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundColor(0);
        }*/
    });
    private RecyclerView recyclerView;

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            final StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度： ").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("经度： ").append(bdLocation.getLongitude()).append("\n");
            currentPosition.append("国家: ").append(bdLocation.getCountry()).append("\n");
            currentPosition.append("省份: ").append(bdLocation.getProvince()).append("\n");
            currentPosition.append("城市: ").append(bdLocation.getCity()).append("\n");
            currentPosition.append("区: ").append(bdLocation.getDistrict()).append("\n");
            currentPosition.append("街道: ").append(bdLocation.getStreet()).append("\n");


            currentPosition.append("定位方式： ");
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation)
                currentPosition.append("GPS");
            else
                currentPosition.append("网络");


           // longitude = String.valueOf(bdLocation.getLongitude());
           // latitude = String.valueOf(bdLocation.getLatitude());
            longitude = String .format("%.3f",bdLocation.getLongitude());
            latitude = String .format("%.3f",bdLocation.getLatitude());
            Log.d("--------------longitude",longitude);
            Log.d("--------------latitude",latitude);
            location = longitude+","+latitude;
            Log.d("--------------location",location);
           // weatherText.setText(currentPosition);
            city = bdLocation.getCity();
            Log.d("--------------location",city+location);
            final String city = bdLocation.getCity();

            try {
                //Log.e("!!!!!!!!!!!!!!!!!!!!!!",city);
                String url = "https://free-api.heweather.com/v5/now?city="+city+"&key=fd5d4d77225e4592a6b9ef00def5ebca";
                // /创建okHttpClient对象
                OkHttpClient mOkHttpClient = new OkHttpClient();
                //创建一个Request
                final Request request = new Request.Builder()
                        .url(url)
                        .build();
                //new call
                Call call = mOkHttpClient.newCall(request);
                //请求加入调度
                call.enqueue(new Callback() {
                    List<WeatherBean> weatherBeans;
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String string = response.body().string();
                        Gson gosn = new Gson();

                        weatherBeans = new ArrayList<WeatherBean>();
                        WeatherBean bean =gosn.fromJson(string,WeatherBean.class);
                        weatherBeans.add(bean);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                temperature = weatherBeans.get(0).getHeWeather5().get(0).getNow().getTmp();
                                weather = weatherBeans.get(0).getHeWeather5().get(0).getNow().getCond().getTxt();
                                String text = weatherBeans.get(0).getHeWeather5().get(0).getNow().getCond().getTxt();
                                String cityn = weatherBeans.get(0).getHeWeather5().get(0).getBasic().getCity();
                                Toast.makeText(MainActivity.this,city+" "+temperature+"°c "+weather,Toast.LENGTH_SHORT).show();
                                //String tep = w.get(0).getHeWeather5().get(0).getNow().getTmp();
                                cityName = (TextView) findViewById(R.id.weather_city);
                                temp = (TextView) findViewById(R.id.weather_temperature);
                                cityName.setText(cityn);
                                temp.setText(temperature+"°C" + " " + text );


                            }
                        });

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this,"失败",Toast.LENGTH_SHORT    ).show();
            }
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        //活动开始时创建活动
        Intent startIntent = new Intent(this, ClockService.class);
        startService(startIntent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI标题栏
       // setStatusBar();
      //  setLayoutPadding
        //使用定位获得位置
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
       // weatherText = (TextView) findViewById(R.id.weather);

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }

        //天气
        //接收到位置后再获取天气信息
            /*
            *根据经纬度
            */
            /*
        final String weatherUrl = "https://free-api.heweather.com/s6/weather/now?location=beijing&key=1008af5cb5c843fa842eb0c7f754195e";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseText = response.body().string();
                        final WeatherBean weather = Utility.handleWeatherResponse(responseText);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (weather != null)
                                {
                                    String city = weather.getHeWeather5().get(0).getBasic().getCity();
                                    Toast.makeText(MainActivity.this,city,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });*/




                //建立数据库  （使用litepal
                thingsList = new ArrayList<>();

        Connector.getDatabase();
        Log.d("msg", "创建数据库");
        try {
            thingsList = DataSupport.order("num").find(Things.class);
        } catch (DataSupportException ex) {
            ex.printStackTrace();
        }
        //建立recycleview
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ThingsAdapter(thingsList);
        recyclerView.setAdapter(adapter);
        helper.attachToRecyclerView(recyclerView);
        undoNum = (TextView) findViewById(R.id.undoNum);//显示未完成的任务数量

        adapter = new ThingsAdapter(thingsList);
        recyclerView.setAdapter(adapter);

       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
     //   setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FileInputStream in = null;
        BufferedReader reader = null;                                            //为什么声明都写在前面？
        //之后设置监听，fab监听为新增任务
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                startActivityForResult(intent, 4);
            }
        });
        //现在还无法排序！！！！！！！！！！！！！！！！！！！！！！！！！！！！

        //获得初始时候还剩下多少todo
        Num = String.valueOf(thingsList.size());
        undoNum.setText(Num);

    }

    //更改定位的行为
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        // option.setScanSpan(10000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    //Location
    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "同意所有权限才能使用程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "未知错误", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            Toast.makeText(MainActivity.this, "IT's null", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (resultCode == 1) {
                // Things recThing = (Things) (this.getIntent().getSerializableExtra("thing"));
                //thingsList.add(recThing);
                int code = resultCode;
                String str = data.getStringExtra("obj");
                Log.e("msg", str);
                String getContent, getTime;
                String getColor, getClockTime;
                //获取当前id
                getContent = data.getStringExtra("content");
                getTime = data.getStringExtra("time");
                getColor = data.getStringExtra("color");
                getClockTime = data.getStringExtra("clockTime");
                Things recThing = new Things();//getColor
                recThing.setColor(getColor);
                recThing.setContent(getContent);
                recThing.setTime(getTime);
                recThing.setnum(thingsList.size());
                recThing.setClockTime(getClockTime);
                //将对象储存至数据库
                recThing.save();
                //添加到list用于显示
                thingsList.add(recThing);
                Num = String.valueOf(thingsList.size());
                undoNum.setText(Num);
                adapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        for (Things th : thingsList)
            th.save();
        Toast.makeText(MainActivity.this, "Bye bye", Toast.LENGTH_SHORT).show();
        super.onDestroy();
        Log.e("Ondestroy", "I am running");

    }

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));//!
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    /**
     * 获取状态栏高度
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //设置布局距离状态栏高度
    public  void setLayoutPadding(Activity activity, DrawerLayout drawerLayout) {
        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        contentLayout.getChildAt(1)
                .setPadding(contentLayout.getPaddingLeft(), getStatusBarHeight() + contentLayout.getPaddingTop(),
                        contentLayout.getPaddingRight(), contentLayout.getPaddingBottom());
    }
}




