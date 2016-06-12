package com.example.jay_0.stockmarketviewer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * Created by jay-0 on 5/3/2016.
 */
public class fragment_historic extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.frag_hist,container,false);
        final String symbol = getArguments().getString("symbol");
        final String history_data = getArguments().getString("historic");

        View view = inflater.inflate(R.layout.frag_hist,container,false);
        //TextView temp = (TextView) view.findViewById(R.id.histText);
        //temp.setText(symbol);

        final WebView webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/renderHighcharts.html");
        Log.i("Progress","html loaded");
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                Log.i("Progress","You reached here");
                webView.loadUrl("javascript:render_chart('"+symbol+"')");
            }

        });
        return view;
    }
}
