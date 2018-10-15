package com.example.onglai.gnssanalyzer;

public class GnssWord {

    private int mSatelliteNumber;
    private int mCnoSatellite;
    private int mFlagOfSatellite;

    public GnssWord(int satelliteNumber, int cnoSatellite, int flagOfSatellite) {

        mSatelliteNumber = satelliteNumber;
        mCnoSatellite = cnoSatellite;
        mFlagOfSatellite = flagOfSatellite;
    }

    public int getSatelliteNumber() { return mSatelliteNumber; }

    public int getCnoSatellite() { return mCnoSatellite; }

    public int getFlagOfSatellite() { return mFlagOfSatellite; }
}
