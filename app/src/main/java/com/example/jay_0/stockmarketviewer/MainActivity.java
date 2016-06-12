package com.example.jay_0.stockmarketviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ImageButton;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.support.v4.view.GestureDetectorCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String DEBUG_TAG = "REQUEST";
    //private TextView t1;
    //private TextView t2;
    private AutoCompleteTextView input;
    private boolean flag = false;
    private int total = 0;

    private List<String> favDetails = new ArrayList<>();

    public void refresh(){

        Toast.makeText(getApplicationContext(),"Refreshing...",Toast.LENGTH_SHORT).show();
        String name;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //SharedPreferences.Editor editor = preferences.edit();
        //editor.remove("Data");
        //editor.apply();
        try{
            name = preferences.getString("Name", "");
        }catch (Exception e){
            name = "";
        }


        if(!name.matches("")) {
            Log.i("FAV_DATA",name);
            String[] favs = name.split(" ");
            total = favs.length;
            for (int i = 0; i < favs.length; i++) {
                String stringUrl = "http://cs571homework8-env.us-west-1.elasticbeanstalk.com/?symbol=" + favs[i];
                Log.i("GENERATED_URL",stringUrl);
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    //t1.setText("Connection available");
                    new DownloadWebpageTask().execute(stringUrl);
                } else {
                    //t1.setText("No network connection available.");
                }

            }

            //Log.i("FAVDETAILS",favDetails.get(0));
            //String[] dataFav = new String[favDetails.size()];
            //int k=0;
            //for (int j = 0;j < favDetails.size();j++){
              //  dataFav[j] = favDetails.get(k)+"###"+favDetails.get(k+1)+"###"+favDetails.get(k+2)+"###"+favDetails.get(k+3)+"###"+favDetails.get(k+4);
               // Log.i("DATA",favDetails.get(k)+"###"+favDetails.get(k+1)+"###"+favDetails.get(k+2)+"###"+favDetails.get(k+3)+"###"+favDetails.get(k+4));
                //k = k+5;

            //}

            String data;
            try{
                data = preferences.getString("Data", "");
            }catch (Exception e){
                data = "";
            }
            String[] dataFav = new String[favs.length];
            for(int i = 0; i<favs.length;i++) {
                dataFav[i] = preferences.getString(favs[i],"");
                //String[] dataFav = data.split(":::");
                Log.i("DATA", dataFav[i]);
            }
            //ListAdapter currentAdapter1 = new favAdapter(getApplicationContext(),dataFav);
            //ListView favListView = (ListView) findViewById(R.id.faView);
            //favListView.setAdapter(currentAdapter1);
            ListAdapter currentAdapter = new favAdapter(this, dataFav);
            ListView currentListView = (ListView) findViewById(R.id.faView);
            currentListView.setAdapter(currentAdapter);

            currentListView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            String name = (String) arg0.getItemAtPosition(position);
                            name = name.split("###")[1];
                            //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                            callQuote(name);

                        }
                    }
            );

        }
    }

    public void callQuote(String name){
        String stringUrl = "http://cs571homework8-env.us-west-1.elasticbeanstalk.com/?symbol=" + name;
        flag = true;
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //t1.setText("Connection available");
            Toast.makeText(this, "Getting Data...", Toast.LENGTH_SHORT).show();
            new DownloadWebpageTasknew().execute(stringUrl);
        } else {
            //t1.setText("No network connection available.");
        }
    }

    public void Refresh_once(View view){
        refresh();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //t1 = (TextView) findViewById(R.id.test1);
        //t2 = (TextView) findViewById(R.id.test2);
        //ListView currentListView1 = (ListView) findViewById(R.id.faView);
        //currentListView1.setAdapter(null);

        SharedPreferences.Editor editor = preferences.edit();
       /*editor.remove("Name");
        editor.apply();
        editor.remove("MSFT");
        editor.remove("AAP");
        editor.remove("FB");
        editor.remove("GOOGL");
        editor.remove("MS");
        editor.remove("Name");
        editor.apply();*/
        mHandler = new Handler();
        //startRepeatingTask();

        //ImageButton act = (ImageButton) findViewById(R.id.ActivityButton);
        //act.setVisibility(View.INVISIBLE);
        //etContentView();
        refresh();

        input = (AutoCompleteTextView) findViewById(R.id.Company);
        input.setAdapter(new SymbolAutocompleteAdapter(this, R.layout.list_item));
        input.setOnItemClickListener(this);
    }

    public void clearStuff(View v) {
        //TextView temp = (TextView) findViewById(R.id.tempText);
        //temp.setText("Refresh Button Pressed");
        //Intent i = new Intent(this, ResultActivity.class);
        //startActivity(i);
        input.setText("");
        //ImageButton act = (ImageButton) findViewById(R.id.ActivityButton);
        //act.setVisibility(View.VISIBLE);
    }

    public void gotoActivity(View v){
        Intent j = new Intent(getApplicationContext(), ResultActivity.class);
        startActivity(j);

    }
    public void getInfo(View v) {

        String val =   input.getText().toString();

        if (val.matches("")) {
            Log.i("ERROR","empty field");
            Toast.makeText(getApplicationContext(),"Input field cannot be empty",Toast.LENGTH_SHORT).show();

        }
        else{
            Log.i("val",val);
            String stringUrl = "http://cs571homework8-env.us-west-1.elasticbeanstalk.com/?symbol=" + val;
            flag = true;
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                //t1.setText("Connection available");
                Toast.makeText(this, "Getting Data...", Toast.LENGTH_SHORT).show();
                new DownloadWebpageTasknew().execute(stringUrl);
            } else {
                //t1.setText("No network connection available.");
            }
        }
    }
 ////////////

    public static ArrayList autocomplete(String input1) {
        ArrayList resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            URL url = new URL("http://cs571homework8-env.us-west-1.elasticbeanstalk.com/?find="+input1);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            //Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            //Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            Log.i("JSON",jsonResults.toString());
            // Create a JSON object hierarchy from the results
            JSONArray jsonArray = new JSONArray(jsonResults.toString());
            //JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.i("Name",jsonArray.getJSONObject(i).getString("Name"));
                resultList.add(jsonArray.getJSONObject(i).getString("Symbol")+" - "+jsonArray.getJSONObject(i).getString("Name")+" ( "+jsonArray.getJSONObject(i).getString("Exchange")+" )");

            }
        } catch (JSONException e) {
            //Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }




    @Override
    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        String[] strnew = str.split(" ");
        input.setText(strnew[0]);
    }


    class SymbolAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;
        public SymbolAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }
        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        //ProgressBar p = (ProgressBar) findViewById(R.id.p1);
                        //p.setVisibility(View.VISIBLE);
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());
                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                        //p.setVisibility(View.INVISIBLE);
                    }
                    return filterResults;

                }
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    ////////////


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
            Log.i("result",result);
            try {
                //total = total+1;

                JSONObject jobject = new JSONObject(result);
                String Name = jobject.getString("Name");

                //if(!flag){
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String temp;
                    try{
                        temp = preferences.getString(jobject.getString("Symbol"), "");
                    }catch (Exception e){
                        temp = "";
                    }
                    SharedPreferences.Editor editor = preferences.edit();
                    /*
                    if(!temp.matches("")) {

                        temp = temp+":::"+Name + "###" + jobject.getString("Symbol") + "###" + jobject.getString("LastPrice") + "###" + jobject.getString("ChangePercent") + "###" + jobject.getString("MarketCap");
                        editor.putString("Data", temp);
                        editor.apply();
                    }
                    else{
                        temp = Name + "###" + jobject.getString("Symbol") + "###" + jobject.getString("LastPrice") + "###" + jobject.getString("ChangePercent") + "###" + jobject.getString("MarketCap");
                        editor.putString("Data", temp);
                        editor.apply();
                    }*/


                    temp =  Name + "###" + jobject.getString("Symbol") + "###" + jobject.getString("LastPrice") + "###" + jobject.getString("ChangePercent") + "###" + jobject.getString("MarketCap");
                    editor.putString(jobject.getString("Symbol"), temp);
                    editor.apply();

                    //Log.i("DATA_CONTENTS",Name + "###" + jobject.getString("Symbol") + "###" + jobject.getString("LastPrice") + "###" + jobject.getString("ChangePercent") + "###" + jobject.getString("MarketCap"));
                    //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Log.i("DATA_EDITOR", preferences.getString(jobject.getString("Symbol"),""));
                //}
                //Log.i("FAV_BUTTON",(total+"")+" "+(len+""));
                /*
                if(flag && total == 0) {
                    total = 1 ;
                    Intent i = new Intent(getApplicationContext(), ResultActivity.class);
                    i.putExtra("quote_data",result);
                    i.putExtra("symbol",input.getText().toString());
                    if (jobject.getString("Status").matches("SUCCESS")) {
                        //ImageButton act = (ImageButton) findViewById(R.id.ActivityButton);
                        //act.setVisibility(View.VISIBLE);
                        startActivity(i);
                    } else {
                        Toast.makeText(getApplicationContext(), "The Status of the reply is not SUCCESS", Toast.LENGTH_SHORT).show();
                    }
                }*/




            } catch (JSONException e) {
                //Toast.makeText(getApplicationContext(),"Please Enter a Valid Entry",Toast.LENGTH_SHORT).show();
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
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            //String contentAsString = readIt(is, len);
            //return contentAsString;

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String inputLine;
            String responseData = "";
            while((inputLine = in.readLine())!= null)
                responseData += inputLine;
            in.close();

            return responseData;

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




    /////////////////

    private int mInterval = 10000; // 5 seconds by default, can be changed later
    private Handler mHandler;



    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                refresh(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask(View v) {
        Switch s = (Switch) v.findViewById(R.id.autorefresh);
        if (s.isChecked()) {
            mStatusChecker.run();
        }
        else{
            stopRepeatingTask();
        }

    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

///////////////////new request



private class DownloadWebpageTasknew extends AsyncTask<String, Void, String> {
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
        Log.i("result",result);
        try {
            //total = total+1;

            JSONObject jobject = new JSONObject(result);
            String Name = jobject.getString("Name");
                Intent i = new Intent(getApplicationContext(), ResultActivity.class);
                i.putExtra("quote_data",result);
                i.putExtra("symbol",input.getText().toString());
                if (jobject.getString("Status").matches("SUCCESS")) {
                    //ImageButton act = (ImageButton) findViewById(R.id.ActivityButton);
                    //act.setVisibility(View.VISIBLE);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "The Status of the reply is not SUCCESS", Toast.LENGTH_SHORT).show();
                }
            }
        catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"Please Enter a Valid Entry",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        //GOOGOt1.setText(result);


    }
}
    private String downloadUrlnew(String myurl) throws IOException {
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
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            //String contentAsString = readIt(is, len);
            //return contentAsString;

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String inputLine;
            String responseData = "";
            while((inputLine = in.readLine())!= null)
                responseData += inputLine;
            in.close();

            return responseData;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    public String readItnew(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }







}
