package com.brunosimplicio.topmovies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        mMoviesAdapter = new ImageAdapter(getActivity(), new ArrayList<Movie>(), mImageView);

        mGridView.setAdapter(mMoviesAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        FetchMoviesTask task = new FetchMoviesTask();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = sharedPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_default));
        task.execute(sort_by);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>>{

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private Movie movie;
        ArrayList<Movie> listaMovie;

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

                Log.v(LOG_TAG, builtUri.toString());

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

        @Override
        protected void onPostExecute(ArrayList<Movie> listaMovie) {
            super.onPostExecute(listaMovie);
            if (listaMovie != null){
                mMoviesAdapter.clear();
                for (Movie movie : listaMovie ){
                    mMoviesAdapter.add(movie);
                }
            }
        }

        private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr) throws JSONException {

            final String OWM_RESULTS = "results";
            final String OWM_ID = "id";
            final String OWM_OVERVIEW = "overview";
            final String OWM_RELEASE_DATE = "release_date";
            final String OWM_POSTER_PATH = "poster_path";
            final String OWM_ORIGINAL_TITLE = "original_title";
            final String OWM_VOTE_AVERAGE = "vote_average";

            Log.v(LOG_TAG, movieJsonStr);
            JSONObject moviesJson = new JSONObject(movieJsonStr);
            JSONArray  moviesArray = moviesJson.getJSONArray(OWM_RESULTS);
            listaMovie = new ArrayList<>();

            for (int i = 0; i < moviesArray.length(); i++){
                long id;
                String originalTitle;
                String posterPath;
                String overview;
                double voteAverage;
                String releaseDate;

                id = moviesArray.getJSONObject(i).getLong(OWM_ID);
                overview = moviesArray.getJSONObject(i).getString(OWM_OVERVIEW);
                posterPath = moviesArray.getJSONObject(i).getString(OWM_POSTER_PATH);
                originalTitle = moviesArray.getJSONObject(i).getString(OWM_ORIGINAL_TITLE);
                voteAverage = moviesArray.getJSONObject(i).getDouble(OWM_VOTE_AVERAGE);
                releaseDate = moviesArray.getJSONObject(i).getString(OWM_RELEASE_DATE);

                String posterFullPath = getFullImagePath(posterPath);

                movie = new Movie();
                movie.setId(id);
                movie.setOverview(overview);
                movie.setPosterPath(posterFullPath);
                movie.setOriginalTitle(originalTitle);
                movie.setVoteAverage(voteAverage);
                movie.setReleaseDate(releaseDate);

                listaMovie.add(movie);


            }
            return listaMovie;
        }

        private String getFullImagePath(String posterPath) {
            String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
            String SIZE = "w185";

            Uri builtImageUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                    .appendPath(SIZE)
                    .appendEncodedPath(posterPath)
                    .build();

            return builtImageUri.toString();
        }


    }
}
