package com.chang.am.app;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

/**
 * Created by benjachang on 6/27/2014.
 */
public class ImageAdapter extends BaseAdapter{

    Context mContext;
    LayoutInflater mInflater;
    //Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.pic_2, R.drawable.pic_2, R.drawable.pic_2, R.drawable.pic_2
    };

    // Constructor
    public ImageAdapter(Context c){
        mContext = c;
    }

    @Override
    public int getCount(){
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        // check if the view already exists
        // if so, no need to inflate and findViewById again!
        if (convertView == null) {

            // Inflate the custom row layout from your XML.
            //convertView = mInflater.inflate(R.layout.main_gridview, null);

            imageView = new ImageView(mContext);
            //imageView.setImageResource(mThumbIds[position]);
            imageView.setScaleType((ImageView.ScaleType.CENTER_INSIDE));
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 180));
            return imageView;

        } else {
            // skip all the expensive inflation/findViewById
            // and just get the holder you already made
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext).load("").into(imageView);

        return imageView;
        // inflation and finding by ID once ever per View
    }

    public void updateData(JSONArray photos) {
        //load url photos into Integer[]
        //Picasso.with(mContext).load("").into(imageView);
    }

    private static class ViewHolder {
        public ImageView thumbnailImageView;
    }
}
