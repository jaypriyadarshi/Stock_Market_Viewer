package com.example.jay_0.stockmarketviewer;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.jay_0.stockmarketviewer.CustomViewPager;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResultActivity extends AppCompatActivity {
    //private static final String DEBUG_TAG = "HttpExample";
    Bundle bundle = new Bundle();
    TabLayout tabLayout;
    ViewPager viewPager;
    private String cname;
    private String fullName;
    private String stockPrice;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    //private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        setContentView(R.layout.activity_result);
        //TextView temp = (TextView)findViewById(R.id.intentData);
        ImageButton btn = (ImageButton) findViewById(R.id.favButton);
        btn.setTag("notFav");
        Bundle quote = getIntent().getExtras();
        if (quote == null) {
            return;
        }
        String quoteData = quote.getString("quote_data");
        //temp.setText(quoteData);
        bundle.putString("json", quoteData);
        bundle.putString("symbol", quote.getString("symbol"));
        cname = quote.getString("symbol");
        try {
            JSONObject j = new JSONObject(quoteData);
            fullName = j.getString("Name");
            stockPrice = j.getString("LastPrice");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager(), getApplicationContext()));

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

        //////
        String stringUrl = "http://cs571homework8-env.us-west-1.elasticbeanstalk.com/?news=" + cname;

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //t1.setText("Connection available");
            new DownloadWebpageTask().execute(stringUrl);
            //count = 1;
            //stringUrl = "http://cs571homework8-env.us-west-1.elasticbeanstalk.com/?company=" + cname;
            //new DownloadWebpageTask().execute(stringUrl);
        } else {
            //t1.setText("No network connection available.");
        }


        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                Log.i("FACEBOOK","Success");
                Toast.makeText(getApplicationContext(),"Post Successful",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"Post Canceled",Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });
    }
        //////////
        @Override
        protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    public void goBack(View v){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }


    public void fbShare(View v){
        String postTitle = "Current Stock Price of "+fullName+", $"+stockPrice;
        String postDesc = "Stock information of "+fullName;
        String shareUrl = "http://chart.finance.yahoo.com/t?s="+cname+"&lang=en-US&width=600&height=600'";
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(postTitle)
                    .setContentDescription(postDesc)
                    .setImageUrl(Uri.parse(shareUrl))
                    .setContentUrl(Uri.parse(shareUrl))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    public void favFunc(View v){
        ImageButton btn = (ImageButton) findViewById(R.id.favButton);
        if(btn.getTag()=="notFav") {
            btn.setImageResource(android.R.drawable.btn_star_big_on);
            btn.setTag("Fav");
            Toast.makeText(getApplicationContext(),"Added to Favorites!",Toast.LENGTH_SHORT).show();
            String name;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            try{
                name = preferences.getString("Name", "");
            }catch (Exception e){
                name = "";
            }
            SharedPreferences.Editor editor = preferences.edit();
            if(name.matches("")){
                name = name+cname;
            }
            else {
                name = name + " " + cname;
            }
            editor.putString("Name",name);
            editor.apply();
        }
        else{
            Toast.makeText(getApplicationContext(),"Removed from Favorites",Toast.LENGTH_SHORT).show();
            btn.setImageResource(android.R.drawable.btn_star_big_off);
            btn.setTag("notFav");

        }
    }
        /////////

        private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {

                // params comes from the execute() call: params[0] is the url.
                try {
                    Log.i("UrlFetch",urls[0]);
                    return downloadUrl(urls[0]);
                } catch (IOException e) {
                    return "Unable to retrieve web page. URL may be invalid.";
                }
            }
            // onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(String result) {
               // int count = 0;
                try {
                    JSONObject jobject = new JSONObject(result);
                    try{
                        JSONObject tempor = jobject.getJSONObject("d");
                        Log.i("NEWS JSON DATA:", result);
                        bundle.putString("news", result);
                    }
                    catch (Exception e){
                        Log.i("Historic JSON DATA:", result);
                        bundle.putString("historic", result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //GOOGOt1.setText(result);


            }
        }
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("HTTPexample", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                String responseData = "";
                while((inputLine = in.readLine())!= null)
                    responseData += inputLine;
                in.close();

                return responseData;
                //String contentAsString = readIt(is, len);
                //return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
    ///////





    private class CustomAdapter extends FragmentPagerAdapter{
        private String fragments [] = {"Current","Historic","News"};

        public CustomAdapter(FragmentManager supportFragmentManager, Context applicationContext){
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position){
            /*
            switch(position){
                case 0:
                    return new fragment_current();
                case 1:
                    return new fragment_historic();
                case 2:
                    return new fragment_news();
                default:
                    return null;
            }*/
            if(position == 0){
                Fragment fragment1 = new fragment_current();
                fragment1.setArguments(bundle);
                return fragment1;
            }
            else if(position == 1){
                Fragment fragment2 = new fragment_historic();
                fragment2.setArguments(bundle);
                return fragment2;
            }
            else if(position == 2){
                Fragment fragment3 = new fragment_news();
                fragment3.setArguments(bundle);
                return fragment3;
            }
            else{
                return null;
            }

        }

        @Override
        public int getCount(){
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position];
        }
    }

    //////////////////


}