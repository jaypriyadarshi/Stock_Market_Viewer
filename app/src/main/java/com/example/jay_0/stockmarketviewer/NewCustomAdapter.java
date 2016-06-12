package com.example.jay_0.stockmarketviewer;

import android.content.Context;
import android.content.Entity;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by jay-0 on 5/4/2016.
 */
public class NewCustomAdapter extends ArrayAdapter {
    public NewCustomAdapter(Context context, String[] data) {
        super(context, R.layout.news_custom_row,data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.news_custom_row,parent,false);

        String Entry = (String) getItem(position);
        TextView title = (TextView) customView.findViewById(R.id.title);
        TextView descp = (TextView) customView.findViewById(R.id.desc);
        TextView pub = (TextView) customView.findViewById(R.id.pub);
        TextView dat = (TextView) customView.findViewById(R.id.date);
        String[] elements = Entry.split("###");
        title.setText(Html.fromHtml(elements[0]));
        title.setMovementMethod(LinkMovementMethod.getInstance());
        descp.setText(elements[1]);
        pub.setText(elements[2]);
        String temp = elements[3];
        //"yyyy-MM-dd'T'HH:mm:ssZ" 2016-05-05T00:35:18Z
        String[] temp1 = temp.split("T");
        String[] dateArt = temp1[0].split("-");
        Log.i("TEMP1",temp1[1]);
        String month;
        if(dateArt[1].matches("01")){
            month = "January";
        }
        else if(dateArt[1].matches("02")){
            month = "February";
        }
        else if(dateArt[1].matches("03")){
            month = "March";
        }
        else if(dateArt[1].matches("04")){
            month = "April";
        }
        else if(dateArt[1].matches("05")){
            month = "May";
        }
        else if(dateArt[1].matches("06")){
            month = "June";
        }
        else if(dateArt[1].matches("07")){
            month = "July";
        }
        else if(dateArt[1].matches("08")){
            month = "August";
        }
        else if(dateArt[1].matches("09")){
            month = "Sempember";
        }
        else if(dateArt[1].matches("10")){
            month = "October";
        }
        else if(dateArt[1].matches("11")){
            month = "November";
        }
        else {
            month = "December";
        }

        String now = dateArt[2]+" "+month+" "+dateArt[0]+", ";
        String now1 = temp1[1].substring(0,temp1[1].length()-1);



        dat.setText(now+now1+" EST");

        return customView;
    }
}
