package com.asso.conference;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.asso.conference.bluetooth.BluetoothDevice;
import com.asso.conference.common.Consumer;
import com.asso.conference.common.Wrapper;
import com.asso.conference.db.AuthDBModel;
import com.asso.conference.mainPage.BrowserFragment;
import com.asso.conference.mainPage.HomePageFragment;
import com.asso.conference.mainPage.LoginFragment;
import com.asso.conference.mainPage.UserPageFragment;
import com.asso.conference.webClient.BluetoothService;
import com.asso.conference.webClient.BookmarkCallback;
import com.asso.conference.webClient.NotificationService;
import com.asso.conference.webClient.UserService;
import com.asso.conference.webClient.models.UserModel;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class HomeActivity extends AppCompatActivity {
    private TextView mTextMessage;
    Toolbar toolbar;

    public static boolean loggedIn = true;

    protected ServiceConnection serviceConnection;
    private BluetoothService service;
    private Disposable disposable;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public HashMap<String, BluetoothDevice> bluetoothDevices = new HashMap<String, BluetoothDevice>();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_schedule:
                    viewPager.setCurrentItem(1);
                    mTextMessage.setText(R.string.title_schedule);
                    return true;
                case R.id.navigation_user:
                    viewPager.setCurrentItem(2);
                    mTextMessage.setText(R.string.title_user);
                    return true;
            }
            return false;
        }
    };
    private ViewPager viewPager;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //ActiveAndroid.initialize(this);

        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);

        BluetoothManager btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }
        // Make sure we have access coarse location enabled, if not, prompt the user to enable it

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect peripherals.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                        }
                    }
                });
                builder.show();
            }
        }


        Intent intent2 = new Intent(this, BluetoothService.class);
        startService(intent2);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d("Homepage","BlueMAX service bound");
                service = ((BluetoothService.LocalBinder)iBinder).getService();
                disposable = service.observeDevices()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(devices -> {
                            toolbar.setSubtitle(devices.get("C4:BE:84:49:DD:7E").getRssi()+"");
                            bluetoothDevices = devices;
                        });
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d("Homepage","Service disconnected");
            }
        };
        bindService(new Intent( this, BluetoothService.class), serviceConnection, BIND_AUTO_CREATE);


        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        setContentView(R.layout.activity_home);

        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("XP 2018");
        toolbar.setSubtitle("");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.LTGRAY);
        //toolbar.
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            MenuItem prevMenuItem = null;
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                // Check if this is the page you want.

                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    navigation.getMenu().getItem(0).setChecked(false);
                }

                // close the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(HomeActivity.this.viewPager.getWindowToken(), 0);

                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager)
    {
        FragmentStatePagerAdapter adapter=new FragmentStatePagerAdapter(
                getSupportFragmentManager()
        ){

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {

                Wrapper<Consumer<UserModel>> userModelConsumer = new Wrapper<>();



                if(position == 1){
                    return BrowserFragment.newInstance("https://xp2018.sched.com/mobile/");
                } else{


                    if(loggedIn && UserService.INSTANCE.isAuthenticated(new BookmarkCallback<UserModel>() {
                        @Override
                        public void onSuccess(UserModel userModel) {
                            //userModelConsumer.ifPresent(c->c.consume(userModel));
                        }

                        @Override
                        public void onError(String message) {
                            finish();
                            startActivity(getIntent());
                            loggedIn = false;
                        }
                    }))
                    {
                        if(position == 0)
                            return HomePageFragment.newInstance();
                        else
                            return UserPageFragment.newInstance();
                    }

                    return LoginFragment.newInstance();
                }
            }
        };
        viewPager.setAdapter(adapter);
    }

    private void updateData(AuthDBModel authDBModel){
        toolbar.setSubtitle("Welcome, "+ authDBModel.username);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.microbit:
                // By clicking the microbit button in the upper right corner it opens the microbit app
                // to flash the microbit device or in case the user doesn't have it installed it
                // redirects to the play store.
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.samsung.microbit");
                if (intent != null) {
                    // We found the activity now start the activity
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // We didn't find the activity and bring user to the market and let them download the app
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("market://details?id=" + "com.samsung.microbit"));
                    startActivity(intent);
                }
                Toast.makeText(this, "Use it to flash your microbit and have fun!", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(disposable != null)
            disposable.dispose();
        unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

}
