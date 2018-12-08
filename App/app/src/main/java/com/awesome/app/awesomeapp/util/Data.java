package com.awesome.app.awesomeapp.util;

import java.util.HashMap;

public class Data {

    public static String[] label_classes = new String[]{
            "Alarm",
            "Car Horn",
            "Crying",
            "Dog",
            "Door Knock",
            "Doorbell",
            "Gun",
            "Emergency Siren"
    };

    public static HashMap<Integer, String>  labelMap = new HashMap<Integer, String> ();
    static
    {
        labelMap = new HashMap<Integer, String>();
        labelMap.put(0, "Alarm");
        labelMap.put(1, "Car Horn");
        labelMap.put(2, "Crying");
        labelMap.put(3, "Dog");
        labelMap.put(4, "Door Knock");
        labelMap.put(5, "Doorbell");
        labelMap.put(6, "Gun");
        labelMap.put(7, "Emergency Siren");
    }
    //android:background="@drawable/bottom_gray_border"
}
