package com.asso.conference.mainPage;

import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;


public class HomePageFragment extends Fragment {

    TextView messageView;
    RelativeLayout devicesView;

    long MILISECONDS_WITH_NO_RESPONSE = 5000;

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

        // TODO keep list updated
        displayDevices();

        return view;
    }

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
            // check whether the last signal is acceptable to show a green or grey dot
            if( currentTime > btDevice.getLastSignal() + MILISECONDS_WITH_NO_RESPONSE || btDevice.getLastSignal() == 0){
                device.setImageResource(android.R.drawable.presence_offline);
            } else {
                device.setImageResource(android.R.drawable.presence_online);
            }

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.leftMargin = (int) (btDevice.getX() * devicesView.getWidth());
            layoutParams.topMargin = (int) (btDevice.getY() * devicesView.getHeight());
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
            rssi.setText(btDevice.getRssi()+" (" + btDevice.getLastSignal() + ")");
            RelativeLayout.LayoutParams rssiLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

            rssiLayoutParams.leftMargin = layoutParams.leftMargin + 13;
            rssiLayoutParams.topMargin = layoutParams.topMargin + 50;
            devicesView.addView(rssi, rssiLayoutParams);
        }
    }

    public static Fragment newInstance(){
        HomePageFragment f=new HomePageFragment();
        return f;
    }

}