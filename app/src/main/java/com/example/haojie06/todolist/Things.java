package com.example.haojie06.todolist;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by haojie06 on 2017/11/18.
 */

public class Things extends DataSupport implements Serializable {
    private String content, time;
    private String color;
    private int num = 1;
    private String clockTime;
    /*
    public Things(String title, String content, String time,String color) {// int color
        this.title = title;
        this.content = content;
        this.time = time;
        this.color = color;
    }
*/

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getnum() {
        return num;
    }

    public void setnum(int id) {
        this.num = id;
    }

    public String getClockTime() {
        return clockTime;
    }

    public void setClockTime(String clockTime) {
        this.clockTime = clockTime;
    }
}
