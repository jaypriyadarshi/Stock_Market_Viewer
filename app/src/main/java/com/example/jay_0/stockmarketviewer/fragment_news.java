package com.example.jay_0.stockmarketviewer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jay-0 on 5/3/2016.
 */
public class fragment_news extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String news_data = getArguments().getString("news");
        //Log.i("received data",news_data);
        View view =  inflater.inflate(R.layout.frag_news,container,false);

        try {
            JSONObject nwsobj = new JSONObject(news_data);
            JSONObject branch1 = nwsobj.getJSONObject("d");
            JSONArray json_results = branch1.getJSONArray("results");
            String[] arrayData = new String[json_results.length()];
            for (int i=0;i<json_results.length();++i){
                JSONObject tempObj = json_results.getJSONObject(i);
                String newsUrl = tempObj.getString("Url");
                String Title = tempObj.getString("Title");
                String desc = tempObj.getString("Description");
                String pub = tempObj.getString("Source");
                String date = tempObj.getString("Date");
                Title = "<a href='"+newsUrl+"'>"+Title+"</a>";
                String stringData = Title+"###"+desc+"###"+pub+"###"+date;
                arrayData[i] = stringData;
                Log.i("Data",stringData);
                ListAdapter currentAdapter1 = new NewCustomAdapter(getActivity(),arrayData);
                ListView newsListView = (ListView) view.findViewById(R.id.newsView);
                newsListView.setAdapter(currentAdapter1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
