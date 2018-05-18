package com.asso.conference.mainPage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asso.conference.HomeActivity;
import com.asso.conference.R;
import com.asso.conference.bluetooth.BluetoothDevice;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;


public class HomePageFragment extends Fragment {

    TextView messageView;
    RelativeLayout devicesView;
    Timer timer;

    long MILISECONDS_WITH_NO_RESPONSE = 15000;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(
                R.layout.view_home,
                container,
                false);


        messageView = (TextView) view.findViewById(R.id.message);
        devicesView = (RelativeLayout) view.findViewById(R.id.devices);
        Button updateButton = (Button) view.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(e->{
            displayDevices();
        });


        displayDevices();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                mHandler.obtainMessage(1).sendToTarget();
            }
        }, 0, 1000);

        return view;
    }

    @Override
    public void onDestroyView(){
        timer.cancel();
        super.onDestroyView();
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            displayDevices();
        }
    };

    private void displayDevices() {

        int nDevicesInView = devicesView.getChildCount() -1;
        long currentTime = System.currentTimeMillis();
        devicesView.removeViewsInLayout(1, nDevicesInView);
        HashMap<String,BluetoothDevice> btDevices = ((HomeActivity)this.getContext()).bluetoothDevices;
        Iterator it = btDevices.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            BluetoothDevice btDevice = ((BluetoothDevice)pair.getValue());
            //ImageView to show if device is available or not
            ImageView device = new ImageView(this.getContext());
            ImageView range = new ImageView(this.getContext());
            // check whether the last signal is acceptable to show a green or grey dot
            if( currentTime > btDevice.getLastSignal() + MILISECONDS_WITH_NO_RESPONSE || btDevice.getLastSignal() == 0){
                device.setImageResource(android.R.drawable.presence_offline);
            } else {
                device.setImageResource(android.R.drawable.presence_online);
                range.setImageResource(R.drawable.bluetooth_range);
            }

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.width = 50;
            layoutParams.height = 50;
            layoutParams.leftMargin = (int) (btDevice.getX() * devicesView.getWidth()) - layoutParams.width/2;
            layoutParams.topMargin = (int) (btDevice.getY() * devicesView.getHeight()) - layoutParams.height/2;


            RelativeLayout.LayoutParams rangeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            rangeLayoutParams.width = (btDevice.getRssi()+100)*4;
            rangeLayoutParams.height = (btDevice.getRssi()+100)*4;
            rangeLayoutParams.leftMargin = layoutParams.leftMargin + layoutParams.width/2 - rangeLayoutParams.width/2;
            rangeLayoutParams.topMargin = layoutParams.topMargin + layoutParams.height/2 - rangeLayoutParams.height/2;
            devicesView.addView(range, rangeLayoutParams);

            devicesView.addView(device, layoutParams);

            // TextView to show roomId of device
            TextView roomId = new TextView(this.getContext());
            roomId.setText(btDevice.getRoomId()+"");
            RelativeLayout.LayoutParams roomIdLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

            roomIdLayoutParams.leftMargin = layoutParams.leftMargin + 13;
            roomIdLayoutParams.topMargin = layoutParams.topMargin - 50;
            devicesView.addView(roomId, roomIdLayoutParams);

            // TextView to show rssi of device
            TextView rssi = new TextView(this.getContext());
            Date d = new Date(btDevice.getLastSignal());
            rssi.setText(btDevice.getRssi()+"\n(" + d.getHours() +":"+d.getMinutes() +":" + d.getSeconds() +")");
            RelativeLayout.LayoutParams rssiLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

            rssiLayoutParams.leftMargin = layoutParams.leftMargin;
            rssiLayoutParams.topMargin = layoutParams.topMargin + 50;
            devicesView.addView(rssi, rssiLayoutParams);
        }
    }

    public static Fragment newInstance(){
        HomePageFragment f=new HomePageFragment();
        return f;
    }

}