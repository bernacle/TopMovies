package com.brunosimplicio.topmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MoviesFragment extends Fragment {

    private ImageAdapter mMoviesAdapter;
    private GridView mGridView;
    private ImageView mImageView;
    private ArrayList<Movie> arrayMovie;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        mImageView = (ImageView) rootView.findViewById(R.id.grid_item_movies_imageview);
        mGridView  = (GridView) rootView.findViewById(R.id.gridview_movies);
        arrayMovie = new ArrayList<>();
        Movie movie = new Movie(1);

        String rootUrl = "http://image.tmdb.org/t/p/";
        String size = "w185";
        String posterPath = "/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg";
        String fullUrl    = rootUrl + size + posterPath;

        movie.setPosterPath(fullUrl);
        arrayMovie.add(movie);

        mMoviesAdapter = new ImageAdapter(getActivity(), arrayMovie, mImageView);

        mGridView.setAdapter(mMoviesAdapter);

        return rootView;
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>>{

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            // If there's no sort_by params, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try  {
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, params[0])
                        .appendQueryParameter(API_KEY_PARAM, "4043c8fc1b4aa2f9230bf75988cd81de")
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to MoviesDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                movieJsonStr = buffer.toString();

            }catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;

            }  finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movies.
            return null;
        }

        private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr) {
            return null;
        }


    }
}
