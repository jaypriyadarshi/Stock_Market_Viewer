package com.example.jay_0.stockmarketviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.security.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by jay-0 on 5/3/2016.
 */

public class fragment_current extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String json_data = getArguments().getString("json");

        View view = inflater.inflate(R.layout.frag_curr, container, false);
        //TextView temp = (TextView) view.findViewById(R.id.currentText);
        try {
            JSONObject jobject = new JSONObject(json_data);
            //SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");
            //String time = format.format(jobject.getString("Timestamp"));
            String[] data = {jobject.getString("Name"),jobject.getString("Symbol"),jobject.getString("LastPrice"),jobject.getString("Change")+" ( "+jobject.getString("ChangePercent")+" %)",jobject.getString("Timestamp"),jobject.getString("MarketCap"),jobject.getString("Volume"),jobject.getString("ChangeYTD")+" ( "+jobject.getString("ChangePercentYTD")+" %) ",jobject.getString("High"),jobject.getString("Low"),jobject.getString("Open"),jobject.getString("Symbol")};
            Log.i("JSON Data",json_data);
            //ListAdapter currentAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,data);
            ListAdapter currentAdapter = new CustomAdapter(getActivity(),data);
            ListView currentListView = (ListView) view.findViewById(R.id.currentView);
            currentListView.setAdapter(currentAdapter);
        } catch (JSONException e) {
            Log.i("JSON","ERROR");
            e.printStackTrace();
            //temp.setText(json_data);

        }
        return view;
    }
}
