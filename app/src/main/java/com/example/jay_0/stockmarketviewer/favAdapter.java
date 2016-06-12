package com.example.jay_0.stockmarketviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by jay-0 on 5/5/2016.
 */
public class favAdapter extends ArrayAdapter{
    public favAdapter(Context context, String[] data) {
        super(context, R.layout.fav_layout,data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.fav_layout,parent,false);

        String Entry = (String) getItem(position);
        Log.i("IndexError",Entry);
        TextView symbol = (TextView) customView.findViewById(R.id.symbol);
        TextView name = (TextView) customView.findViewById(R.id.name);
        TextView price = (TextView) customView.findViewById(R.id.price);
        TextView change = (TextView) customView.findViewById(R.id.change);
        TextView mcap = (TextView) customView.findViewById(R.id.mcap);
        try {
            String[] elements = Entry.split("###");
            double num1 = Double.valueOf(elements[2]);
            double num2 = Double.valueOf(elements[3]);

            //DecimalFormat df = new DecimalFormat("#.##");
            //df.setRoundingMode(RoundingMode.CEILING);
            num1 = (double) Math.round(num1 * 100d) / 100d;
            String first = Double.toString(num1);
            num2 = (double) Math.round(num2 * 100d) / 100d;
            String second;
            if (num2 > 0) {
                change.setTextColor(Color.GREEN);
                second = "+" + Double.toString(num2) + "%";
            } else if (num2 < 0) {
                change.setTextColor(Color.RED);
                second = Double.toString(num2) + "%";
            } else {
                second = Double.toString(num2) + "%";
            }
            name.setText(elements[0]);
            symbol.setText(elements[1]);
            price.setText("$" + first);
            change.setText(second);
            //mcap.setText(elements[4]);
            String t = elements[4];
            double num3 = Double.valueOf(t);
            String out;
            if ((num3 / 1000000000) > 1) {
                num3 = (num3 / 1000000000);
                num3 = (double) Math.round(num3 * 100d) / 100d;
                out = num3 + " Billions";
            } else if ((num3 / 1000000) > 1) {
                num3 = (num3 / 1000000);
                num3 = (double) Math.round(num3 * 100d) / 100d;
                out = num3 + " Millions";
            } else {
                out = t;
            }
            mcap.setText("Market Cap: "+out);
        }catch (Exception e){
            e.printStackTrace();
        }

        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        //SharedPreferences.Editor editor = preferences.edit();
        //editor.remove("Data");
        //editor.apply();

        return customView;
    }
}
