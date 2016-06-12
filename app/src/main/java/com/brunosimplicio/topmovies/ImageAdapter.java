package com.brunosimplicio.topmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by brunosimplicio on 12/06/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<com.brunosimplicio.topmovies.Movie> mListMovies;
    private ImageView mImageView;

    public ImageAdapter(Context mContext, ArrayList<com.brunosimplicio.topmovies.Movie> mListMovies, ImageView mImageView) {
        this.mContext = mContext;
        this.mListMovies = mListMovies;
        this.mImageView = mImageView;
    }

    @Override
    public int getCount() {
        return mListMovies.size();
    }

    @Override
    public Object getItem(int position) {
        return mListMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            mImageView = new ImageView(mContext);
            mImageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.setPadding(8, 8, 8, 8);
        } else {
            mImageView = (ImageView) convertView;
        }

        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w92/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg").into(mImageView);


        return mImageView;
    }
}
