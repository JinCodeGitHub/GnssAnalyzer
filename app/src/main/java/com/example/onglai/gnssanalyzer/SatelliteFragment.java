package com.example.onglai.gnssanalyzer;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SatelliteFragment extends Fragment implements GnssListener {


    public static final String TAG = "SatelliteFragment";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<GnssWord> mGnssWordArrayList;
    private static GnssAdapter mAdapter;
    private static MyViewModel mViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_satellite, container, false);

        mViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        mRecyclerView = v.findViewById(R.id.recyclerview_satellite);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(savedInstanceState == null) {
            mGnssWordArrayList = new ArrayList<>();
        } else {
            mGnssWordArrayList = mViewModel.mGnssWordArrayList;
        }
        mAdapter = new GnssAdapter(mGnssWordArrayList);
        mRecyclerView.setAdapter(mAdapter);

        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView;

        textView = (TextView) MainActivity.getInstance().findViewById(R.id.lat);
        if(mViewModel.mLatitude != null)
            textView.setText(mViewModel.mLatitude);
        textView = (TextView) MainActivity.getInstance().findViewById(R.id.lon);
        if(mViewModel.mLongitude != null)
            textView.setText(mViewModel.mLongitude);

        String formattedDate = mViewModel.mGpsDate;
        textView = (TextView) MainActivity.getInstance().findViewById(R.id.date);
        if (formattedDate != null)
            textView.setText(formattedDate);
        String formattedTime = mViewModel.mGpsTime;
        textView = (TextView) MainActivity.getInstance().findViewById(R.id.time);
        if (formattedTime != null)
            textView.setText(formattedTime);

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("Gnss Analyzer", "Location changed");

        double mLatitude = location.getLatitude();
        double mLongitude = location.getLongitude();
        NumberFormat numberFormat = new DecimalFormat("#0.0000000");

        mViewModel.mLatitude = numberFormat.format(mLatitude);
        mViewModel.mLongitude = numberFormat.format(mLongitude);

        TextView textLat = (TextView) MainActivity.getInstance().findViewById(R.id.lat);
        if(textLat != null)
            textLat.setText(numberFormat.format(mLatitude));
        TextView textLon = (TextView) MainActivity.getInstance().findViewById(R.id.lon);
        if(textLon != null)
            textLon.setText(numberFormat.format(mLongitude));

        Log.i("time provider", location.getProvider());

        if(location.getProvider().equals("gps")) {
            Date dateObject = new Date(location.getTime());

            String formattedDate = formatDate(dateObject);
            mViewModel.mGpsDate = formattedDate;
            TextView date_view = (TextView) MainActivity.getInstance().findViewById(R.id.date);
            if (date_view != null)
                date_view.setText(formattedDate);
            String formattedTime = formatTime(dateObject);
            mViewModel.mGpsTime = formattedTime;
            TextView time_view = (TextView) MainActivity.getInstance().findViewById(R.id.time);
            if (time_view != null)
                time_view.setText(formattedTime);
        }

    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-YYYY");
        return simpleDateFormat.format(dateObject);
    }
    private String formatTime(Date dateObject) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        return simpleDateFormat.format(dateObject);
    }

    @Override
    public void onLocationStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onGnssMeasurementsReceived(GnssMeasurementsEvent event) {
        Log.i("Gnss Analyzer", "Sat fragment");
        updateGnssMeasurements(event);
    }

    @Override
    public void onGnssMeasurementsStatusChanged(int status) {

    }

    @Override
    public void onGnssNavigationMessageReceived(GnssNavigationMessage event) {

    }

    @Override
    public void onGnssNavigationMessageStatusChanged(int status) {

    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onSatelliteStatusChanged(GnssStatus gnssStatus) {

    }

    @Override
    public void onListenerRegistration(String listener, boolean result) {

    }

    @Override
    public void onNmeaReceived(long l, String s) {

    }

    @Override
    public void onTTFFReceived(long l) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateGnssMeasurements(GnssMeasurementsEvent event) {
        mGnssWordArrayList.clear();
        for(GnssMeasurement gnssMeasurement: event.getMeasurements()) {
            mGnssWordArrayList.add(new GnssWord(gnssMeasurement.getSvid(),
                    (int)gnssMeasurement.getCn0DbHz(),
                    gnssMeasurement.getConstellationType()));
        }
        mViewModel.mGnssWordArrayList = mGnssWordArrayList;
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private class GnssAdapter extends RecyclerView.Adapter<GnssAdapter.MyViewHolder> {

        private ArrayList<GnssWord> gnssWordArrayList;

        // Constructor
        public GnssAdapter(ArrayList<GnssWord> gnssWordArrayList) {
            this.gnssWordArrayList = gnssWordArrayList;
        }

        public void setList(ArrayList<GnssWord> gnssWordArrayList) {
            this.gnssWordArrayList = gnssWordArrayList;
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            private final TextView mTextViewSatelliteNumber;
            private final TextView mTextViewCno;
            private final ImageView mImageViewFlag;

            MyViewHolder(View itemView) {
                super(itemView);

                mTextViewSatelliteNumber = (TextView) itemView.findViewById(R.id.gnss_satellite_no);
                mTextViewCno = (TextView) itemView.findViewById(R.id.gnss_cno);
                mImageViewFlag = (ImageView) itemView.findViewById(R.id.gnss_flag);
            }

            private TextView getSatelliteNumber() { return mTextViewSatelliteNumber; }

            private TextView getCno() { return mTextViewCno; }

            private ImageView getFlag() { return mImageViewFlag; }

        }


        @NonNull
        @Override
        public GnssAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gnss_word_view,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GnssAdapter.MyViewHolder holder, int position) {

            holder.getSatelliteNumber().setText(Integer.toString(this.gnssWordArrayList.get(position).getSatelliteNumber()));

            setSatelliteCnoView(this.gnssWordArrayList.get(position).getCnoSatellite(), holder.getCno());

            switch (this.gnssWordArrayList.get(position).getFlagOfSatellite()) {
                case GnssStatus.CONSTELLATION_GPS:
                    holder.getFlag().setImageResource(R.drawable.ic_america_square);
                    break;

                case GnssStatus.CONSTELLATION_BEIDOU:
                    holder.getFlag().setImageResource(R.drawable.ic_china_square);
                    break;

                case GnssStatus.CONSTELLATION_GALILEO:
                    holder.getFlag().setImageResource(R.drawable.ic_eu_square);
                    break;

                case GnssStatus.CONSTELLATION_GLONASS:
                    holder.getFlag().setImageResource(R.drawable.ic_russia_square);
                    break;

                case GnssStatus.CONSTELLATION_QZSS:
                    holder.getFlag().setImageResource(R.drawable.ic_japan_square);
                    break;

            }
        }

        @Override
        public int getItemCount() {
            if(this.gnssWordArrayList != null) {
                return this.gnssWordArrayList.size();
            }
            return 0;
        }

        public void setSatelliteCnoView(int satCno, TextView satelliteCnoView) {

            if(satCno >= 0 && satCno <= 10) {
                satelliteCnoView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.gnssRed));

            } else if(satCno > 10 && satCno <= 20) {
                satelliteCnoView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.gnssOrange));

            } else if(satCno > 20 && satCno <= 30) {
                satelliteCnoView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.gnssYellow));

            } else {
                satelliteCnoView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.gnssGreen));

            }
            satelliteCnoView.setHeight(11*satCno);
        }

    }

}