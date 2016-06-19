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

    public void clear(){
        mListMovies.clear();
        notifyDataSetChanged();
    }

    public void add(Movie movie){
        mListMovies.add(movie);
        notifyDataSetChanged();
    }

    @Override
    public Movie getItem(int position) {
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
            mImageView.setLayoutParams(new GridView.LayoutParams((int)mContext.getResources().getDimension(R.dimen.width),
                    (int)mContext.getResources().getDimension(R.dimen.height)));
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.setPadding(8, 8, 8, 8);
        } else {
            mImageView = (ImageView) convertView;
        }

        Picasso.with(mContext).load(mListMovies.get(position).getPosterPath()).into(mImageView);

        return mImageView;
    }
}
