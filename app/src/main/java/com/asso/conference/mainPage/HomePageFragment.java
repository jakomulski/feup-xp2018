package com.asso.conference.mainPage;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.asso.conference.HomeActivity;
import com.asso.conference.R;
import com.asso.conference.SignUpActivity;
import com.asso.conference.bluetooth.BluetoothDevice;
import com.asso.conference.webClient.BluetoothService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static android.content.Context.BIND_AUTO_CREATE;

public class HomePageFragment extends Fragment {

    TextView messageView;
    ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(
                R.layout.view_home,
                container,
                false);


        messageView = (TextView) view.findViewById(R.id.message);
        lv = (ListView) view.findViewById(R.id.deviceList);
        /*Button updateButton = (Button) view.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(e->{
            HashMap<String,BluetoothDevice> btDevices = ((HomeActivity)this.getContext()).bluetoothDevices;
            String[] btArr = new String[btDevices.size()];
            Iterator it = btDevices.entrySet().iterator();
            int i = 0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                btArr[i] = pair.getKey() + " = " + pair.getValue();
                it.remove();
                i++;
            }
            lv.setAdapter(new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, btArr));
        });*/

        // TODO keep list updated
        // Convert ArrayList to array
        HashMap<String,BluetoothDevice> btDevices = ((HomeActivity)this.getContext()).bluetoothDevices;
        String[] btArr = new String[btDevices.size()];
        Iterator it = btDevices.entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            btArr[i] = pair.getKey() + " = " + pair.getValue();
            it.remove();
            i++;
        }
        lv.setAdapter(new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, btArr));

        return view;
    }

    public static Fragment newInstance(){
        HomePageFragment f=new HomePageFragment();
        return f;
    }

}