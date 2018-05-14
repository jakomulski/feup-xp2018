package com.asso.conference.mainPage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.asso.conference.R;

public class HomePageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(
                R.layout.view_home,
                container,
                false);

        return view;
    }

    // This is the method the pager adapter will use
    // to create a new fragment
    public static Fragment newInstance(){
        HomePageFragment f=new HomePageFragment();
        return f;
    }

}