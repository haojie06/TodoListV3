package com.example.haojie06.todolist.util;

import com.example.haojie06.todolist.gson.WeatherBean;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by haojie06 on 2017/12/20.
 */

public class Utility {
    /*
    *将JSON解析为实体类
    */
    public static WeatherBean handleWeatherResponse(String response)
    {
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,WeatherBean.class);
        }catch (Exception e){e.printStackTrace();}
        return null;
    }
}
