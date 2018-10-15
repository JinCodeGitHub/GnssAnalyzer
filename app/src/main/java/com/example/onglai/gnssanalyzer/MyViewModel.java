package com.example.onglai.gnssanalyzer;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

public class MyViewModel extends ViewModel {

    public String mLatitude;

    public String mLongitude;

    public String mGpsDate;

    public String mGpsTime;

    public ArrayList<GnssWord> mGnssWordArrayList;
}
