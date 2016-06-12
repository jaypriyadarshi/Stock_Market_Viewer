package com.example.jay_0.stockmarketviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class CustomAdapter extends ArrayAdapter {

    public CustomAdapter(Context context, String[] data) {
        super(context, R.layout.custom_row,data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.custom_row,parent,false);

        String singleEntry = (String) getItem(position);
        TextView head = (TextView) customView.findViewById(R.id.head);
        TextView content = (TextView) customView.findViewById(R.id.content);

        if(position == 0){
            head.setText("Name");
            content.setText(singleEntry);
        }
        else if(position == 1){
            head.setText("Symbol");
            content.setText(singleEntry);
        }
        else if(position == 2){
            head.setText("Last Price ");
            content.setText("$"+singleEntry);
        }
        else if(position == 3){
            String temp = singleEntry;
            String[] elements = temp.split(" ");
            String first = elements[0];
            String second = elements[2];
            double num1 = Double.valueOf(first);
            double num2 = Double.valueOf(second);

            //DecimalFormat df = new DecimalFormat("#.##");
            //df.setRoundingMode(RoundingMode.CEILING);
            num1 = (double)Math.round(num1*100d)/100d;
            first = Double.toString(num1);
            num2 = (double)Math.round(num2*100d)/100d;
            second = Double.toString(num2);
            ImageView img = (ImageView) customView.findViewById(R.id.arrow);
            if(num2<0){
                img.setImageResource(R.drawable.down);
            }
            else if(num2>0){
                second = "+"+second;
                img.setImageResource(R.drawable.up);
            }
            head.setText("Change");
            String newContent = first+"("+second+"%)";
            content.setText(newContent);
        }
        else if(position == 4){
            head.setText("Timestamp");
            String[] components = singleEntry.split(" ");
            String day = components[2];
            if(components[2].length() == 1){
                day = "0"+day;

            }
            String outp = day+" "+components[1]+" "+components[5]+", "+components[3];
            content.setText(outp);

        }
        else if(position == 5){
            head.setText("Market Cap");
            double num3 = Double.valueOf(singleEntry);
            String out;
            if ((num3/1000000000)>1){
                num3 = (num3/1000000000);
                num3 = (double)Math.round(num3*100d)/100d;
                out = num3+" Billions";
            }
            else if((num3/1000000)>1){
                num3 = (num3/1000000);
                num3 = (double)Math.round(num3*100d)/100d;
                out = num3+" Millions";
            }
            else{
                out = singleEntry;
            }
            content.setText(out);
        }
        else if(position == 6){
            head.setText("Volume");
            content.setText(singleEntry);
        }
        else if(position == 7){
            head.setText("Change YTD");

            String temp = singleEntry;
            String[] elements = temp.split(" ");
            String first = elements[0];
            String second = elements[2];
            double num1 = Double.valueOf(first);
            double num2 = Double.valueOf(second);

            //DecimalFormat df = new DecimalFormat("#.##");
            //df.setRoundingMode(RoundingMode.CEILING);
            num1 = (double)Math.round(num1*100d)/100d;
            first = Double.toString(num1);
            num2 = (double)Math.round(num2*100d)/100d;
            second = Double.toString(num2);
            ImageView img = (ImageView) customView.findViewById(R.id.arrow);
            if(num2<0){
                img.setImageResource(R.drawable.down);
            }
            else if(num2>0){
                second = "+"+second;
                img.setImageResource(R.drawable.up);
            }
            String newContent = first+"("+second+"%)";
            content.setText(newContent);
        }
        else if(position == 8){
            head.setText("High");
            content.setText("$"+singleEntry);
        }
        else if(position == 9){
            head.setText("Low");
            content.setText("$"+singleEntry);
        }
        else if(position == 10){
            head.setText("Open");
            content.setText("$"+singleEntry);
        }
        else if(position == 11){
            head.setText("Today's Stock Activity");
            try {
                String url_yahoo = "http://chart.finance.yahoo.com/t?s="+singleEntry+"&lang=en-US&width=200&height=150'";
                Log.i("URL:",url_yahoo);
                //URL url = new URL(url_yahoo);
                //Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                content.setText("");
                //content.removeView();
                TouchImageView img = (TouchImageView) customView.findViewById(R.id.yahooImg);
                //img.setImageBitmap(bmp);
                new DownloadImageTask(img).execute(url_yahoo);
                //img.setImageResource();

                img.getLayoutParams().height = 700;
                img.getLayoutParams().width = 900;
                //RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(img.getLayoutParams());
                //layout.setMargins(-100, 0, 0, 0);//left,right,top,bottom
                //img.setLayoutParams(layout);
                //img.getLayoutParams().margin

                //img.requestLayout();

            } catch (Exception e) {
                return null;
            }
        }
        else{
            head.setText("");
            content.setText(singleEntry);
        }



        return customView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        TouchImageView bmImage;

        public DownloadImageTask(TouchImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
