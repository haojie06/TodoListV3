package com.example.haojie06.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WriteActivity extends AppCompatActivity  {
    final int DATE_DIALOG = 0;    //日期选择对话框id
    final int TIME_DIALOG = 1;    //时间选择对话框id
    Calendar c = null;      //声明一个日历对象

    int flag = 0;
    String wTime = null;
    String wYear, wMonth, wDay, wHour, wminute;
    String saveTitle, saveContent, time, saveColor = "0";
    TimePickerDialog mDialogAll;
    private FloatingActionButton fab2;
    private EditText title,content;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        final LinedEditText content = (LinedEditText) findViewById(R.id.content_edit);

        Button colorGray = (Button) findViewById(R.id.gray);
        Button colorGreen = (Button) findViewById(R.id.green);
        Button colorBlue = (Button) findViewById(R.id.blue);
        Button colorYellow = (Button) findViewById(R.id.yellow);
        Button colorRed = (Button) findViewById(R.id.red);
        Button setClock = (Button) findViewById(R.id.set_clock);
        final EditText editText = (LinedEditText) findViewById(R.id.content_edit);

        //用于记录闹钟的时间

        //按下按钮选择闹钟时间   还不是很懂原理
        setClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar d = Calendar.getInstance(Locale.CHINA);
                // 创建一个日历引用d，通过静态方法getInstance() 从指定时区 Locale.CHINA 获得一个日期实例
                Date myDate = new Date();
                // 创建一个Date实例
                d.setTime(myDate);
                // 设置日历的时间，把一个新建Date实例myDate传入

                int year = d.get(Calendar.YEAR);
                int month = d.get(Calendar.MONTH);
                int day = d.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(WriteActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        flag += 1;
                        wYear = String.valueOf(year);
                        if (month < 10) {
                            wMonth = "0" + String.valueOf(month);
                        } else {
                            wMonth = String.valueOf(month);
                        }

                        if (day < 10) {
                            wDay = "0" + String.valueOf(day);
                        } else {
                            wDay = String.valueOf(day);
                        }

                    }
                }, year, month, day);
                datePickerDialog.setMessage("请选择日期");
                datePickerDialog.show();

                //接下来选择时间
                /**
                 * 0：初始化小时
                 * 0：初始化分
                 * true:是否采用24小时制
                 */

                TimePickerDialog timePickerDialog = new TimePickerDialog(WriteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        flag += 1;
                        if (hourOfDay < 10) {
                            wHour = "0" + String.valueOf(hourOfDay);
                        } else {
                            wHour = String.valueOf(hourOfDay);
                        }

                        if (minute < 10) {
                            wminute = "0" + String.valueOf(minute);
                        } else {
                            wminute = String.valueOf(minute);
                        }

                        Log.e("Time", wHour + wminute);
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });

        //通过按钮设置标签颜色
        colorGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveColor = "0";
                editText.setBackgroundResource(R.drawable.writehuise);
                Toast.makeText(WriteActivity.this,"You choose Gray!",Toast.LENGTH_SHORT).show();
            }
        });

        colorGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveColor = "1";
                editText.setBackgroundResource(R.drawable.writelvse);
                Toast.makeText(WriteActivity.this,"You choose Green!",Toast.LENGTH_SHORT).show();
            }
        });

        colorBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setBackgroundResource(R.drawable.writelanse);
                saveColor = "2";
                Toast.makeText(WriteActivity.this,"You choose Blue!",Toast.LENGTH_SHORT).show();
            }
        });

        colorYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setBackgroundResource(R.drawable.writehuangse);
                saveColor = "3";
                Toast.makeText(WriteActivity.this,"You choose Yellow!",Toast.LENGTH_SHORT).show();
            }
        });

        colorRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setBackgroundResource(R.drawable.writehongse);
                saveColor = "4";
                Toast.makeText(WriteActivity.this,"You choose Red!",Toast.LENGTH_SHORT).show();
            }
        });


        fab2 = (FloatingActionButton)findViewById(R.id.fab_ok);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveContent = content.getText().toString();
                //获得写下todo的时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy--MM--dd");

                time = format.format(new Date());
                Intent intent = getIntent();
                //处理设置的闹钟
                //if(flag == 1)
                String gYear, gMonth, gDay, gHour, gMinute;

                if (flag == 2)
                    wTime = wYear + wMonth + wDay + wHour + wminute;
                else
                    wTime = "null";
                flag = 0;

                Log.e("-----------------wTime", wTime);
                intent.putExtra("clockTime", wTime);
                intent.putExtra("obj","obj");
                intent.putExtra("content",saveContent);
                intent.putExtra("time",time);
                intent.putExtra("color",saveColor);

                setResult(1,intent);
                finish();

            }
        });
    }


}
