package com.asso.conference;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.asso.conference.common.Consumer;
import com.asso.conference.common.Wrapper;
import com.asso.conference.db.AuthDBModel;
import com.asso.conference.mainPage.BrowserFragment;
import com.asso.conference.mainPage.HomePageFragment;
import com.asso.conference.mainPage.LoginFragment;
import com.asso.conference.mainPage.UserPageFragment;
import com.asso.conference.ui.MainActivity;
import com.asso.conference.webClient.BeaconQueue;
import com.asso.conference.webClient.BookmarkCallback;
import com.asso.conference.webClient.UserService;
import com.asso.conference.webClient.models.BeaconModel;
import com.asso.conference.webClient.models.UserModel;


public class HomeActivity extends AppCompatActivity {
    private TextView mTextMessage;
    Toolbar toolbar;

    public  static boolean loggedIn = true;

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
                // This makes sure getItem doesn't use a position
                // that is out of bounds of our array of URLs
                //return toVisit.length;
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                // Here is where all the magic of the adapter happens
                // As you can see, this is really simple.


                Wrapper<Consumer<UserModel>> userModelConsumer = new Wrapper<>();
                
                if(position == 1){
                    return BrowserFragment.newInstance("https://xp2018.sched.com/mobile/");
                } else{


                    if(loggedIn && UserService.INSTANCE.isAuthenticated(new BookmarkCallback<UserModel>() {
                        @Override
                        public void onSuccess(UserModel userModel) {
                            userModelConsumer.ifPresent(c->c.consume(userModel));
                        }

                        @Override
                        public void onError() {
                            finish();
                            startActivity(getIntent());
                            loggedIn = false;
                        }
                    }))
                    {
                        if(position == 0)
                            return HomePageFragment.newInstance();
                        else
                            return UserPageFragment.newInstance(userModelConsumer);
                    }

                    return LoginFragment.newInstance();
                }

                //return HomePageFragment.newInstance();
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
                Toast.makeText(this, "clicked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
