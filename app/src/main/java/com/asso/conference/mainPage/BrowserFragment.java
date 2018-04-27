package com.asso.conference.mainPage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.asso.conference.R;

public class BrowserFragment extends Fragment {

    WebView browser;
    String url;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(
                R.layout.view_schedule,
                container,
                false);

        browser=(WebView)view.findViewById(R.id.webview);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setWebViewClient(new WebViewClient());
        browser.loadUrl(url);
        // Just load whatever URL this fragment is
        // created with.
        return view;
    }

    // This is the method the pager adapter will use
    // to create a new fragment
    public static Fragment newInstance(String url){
        BrowserFragment f=new BrowserFragment();
        f.url=url;
        return f;
    }

}