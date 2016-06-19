package com.brunosimplicio.topmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final String MOVIE = "movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();

        }
    }


    public static class DetailFragment extends Fragment {

        Movie mMovie;
        ImageView imageView;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();

            if(intent != null && intent.hasExtra(MOVIE)){
                mMovie = (Movie) intent.getSerializableExtra(MOVIE);
                ((TextView)rootView.findViewById(R.id.original_title_textview)).setText(mMovie.getOriginalTitle());
                ((TextView)rootView.findViewById(R.id.overview_textview)).setText(mMovie.getOverview());
                ((TextView)rootView.findViewById(R.id.vote_average_textview)).setText(String.valueOf(mMovie.getVoteAverage()));
                ((TextView)rootView.findViewById(R.id.release_date_textview)).setText(mMovie.getReleaseDate());
                imageView = (ImageView) rootView.findViewById(R.id.poster_image_view);
                Picasso.with(getActivity()).load(mMovie.getPosterPath()).into(imageView);
            }
            return rootView;
        }


    }


}
