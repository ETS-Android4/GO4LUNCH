package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Close {

    @SerializedName("day")
    private Integer day;

    @SerializedName("time")
    private String time;

    public Close(Integer day, String time) {
        this.day = day;
        this.time = time;

    }

    public Integer getDay() { return day; }

    public String getTime() { return time; }

}
