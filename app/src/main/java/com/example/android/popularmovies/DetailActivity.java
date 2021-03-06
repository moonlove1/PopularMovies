package com.example.android.popularmovies;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.popularmovies.APIUtils.movieApi;
import com.example.android.popularmovies.database.MovieContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
ImageView image,Trailer;
     ArrayList<Reviews> trailerArrayList;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
     ArrayList<Reviews> reviewsArrayList;
    TextView mdate,mplot;
    ObjectAnimator animator;
int identify;
    byte[] mArray;
    FloatingActionButton button;
    RecyclerView view;
    LinearLayoutManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        preferences=getSharedPreferences("movies",MODE_PRIVATE);
        editor=preferences.edit();
        final Bundle bundle=getIntent().getExtras();
        String date=bundle.getString("date");
        String plot=bundle.getString("plot");
        final String imageString=bundle.getString("ImageString");
     final   String id=bundle.getString("id");
        final String rating =bundle.getString("rating");
        view=(RecyclerView) findViewById(R.id.view);

        if(savedInstanceState!=null){
            reviewsArrayList=savedInstanceState.getParcelableArrayList("list");
            manager=new LinearLayoutManager(DetailActivity.this);
view.setAdapter(new ReviewAdapter(DetailActivity.this,reviewsArrayList));
            view.setLayoutManager(manager);
            manager.scrollToPosition(savedInstanceState.getInt("index"));
        }
        else{
            reviewsArrayList=new ArrayList<>();
            make_request(id);
        }

        trailerArrayList=new ArrayList<>();
        final String original=bundle.getString("original");
        String backdrop=bundle.getString("backdrop");
        Toolbar toolbar=(Toolbar) findViewById(R.id.beer);
        final AppBarLayout layout=(AppBarLayout) findViewById(R.id.appl);
        button=(FloatingActionButton) findViewById(R.id.fav);
        if(preferences.getString(original,"movies")!="movies"){
            button.setImageResource(R.drawable.ic_delete_black_24dp);
        }
        animator=ObjectAnimator.ofFloat(button,View.ROTATION_Y,360);

        //toolbar.setTitle("here toolbar");
        CollapsingToolbarLayout bar=(CollapsingToolbarLayout) findViewById(R.id.collap);
        bar.setTitle(original);
        bar.setExpandedTitleColor(getResources().getColor(R.color.white));
        manager=new LinearLayoutManager(this);
Trailer=(ImageView) findViewById(R.id.playtrailer);
        image=(ImageView) findViewById(R.id.thumbnail_detail);
        mdate=(TextView) findViewById(R.id.date_deatil);
        mplot=(TextView) findViewById(R.id.plot);
        Trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identify=1;
                get_trailer(id);
                Toast.makeText(DetailActivity.this,"trailer",Toast.LENGTH_SHORT).show();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

if(preferences.getString(original,"movies")=="movies"){
    Picasso.with(DetailActivity.this).load(movieApi.THUMBNAIL+imageString).into(new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            mArray = stream.toByteArray();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    });
    ContentValues values=new ContentValues();
    values.put(MovieContract.movietable.MOVIE_IMAGE,mArray);
    values.put(MovieContract.movietable.MOVIE_RATING,rating);
    Uri uri=getContentResolver().insert(MovieContract.movietable.CONTENT_URI,values);
    if(uri!=null){
        editor.putString(original,uri.getLastPathSegment());
        editor.apply();
        Toast.makeText(DetailActivity.this,"Added to Favourites" +uri.getLastPathSegment(),Toast.LENGTH_SHORT).show();
    }

    //identify=0;
    button.setImageResource(R.drawable.ic_delete_black_24dp);
    //      Toast.makeText(DetailActivity.this,"trailer",Toast.LENGTH_SHORT).show();
    animator.start();

}
else {
    int row=getContentResolver().delete(MovieContract.movietable.CONTENT_URI.buildUpon().appendPath(preferences.getString(original,"movies")).build(),null,null);
    editor.remove(original);
    editor.apply();
    button.setImageResource(R.drawable.ic_star_rate_black_18dp);
    animator.start();
    Toast.makeText(DetailActivity.this,"Deleted from Favourites",Toast.LENGTH_SHORT).show();
}


                                //button.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });
        //mtitle=(TextView) findViewById(R.id.name_deatil);
        //poster=(ImageView) findViewById(R.id.poster_image);
        Picasso.with(this).load(movieApi.THUMBNAIL+backdrop).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                layout.setBackground(new BitmapDrawable(bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

        Picasso.with(this).load(movieApi.THUMBNAIL+imageString).into(image);
        mdate.setText(date);
        mplot.setText(plot);
        //mtitle.setText(original);

    }

    public void make_request(String id){


       final RequestQueue queue= Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest mrequest=new JsonObjectRequest(Request.Method.GET, movieApi.MOVIE_REVIEWS_1+id+movieApi.MOVIE_REVIEW_2+getString(R.string.movie_key), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
      //          errortext.setVisibility(View.INVISIBLE);
                //        Toast.makeText(MainActivity.this,"swing",Toast.LENGTH_SHORT).show();
        //        bar.setVisibility(View.INVISIBLE);
                try {
                    JSONArray results=response.getJSONArray("results");
                    for(int i=0;i<results.length();i++){
                        JSONObject re=results.getJSONObject(i);
                        String name=re.getString("author");
                        String content=re.getString("content");

                        Reviews m=new Reviews(content,name);
                        reviewsArrayList.add(m);
                    }
    //                adapter=new MovieAdapter(MainActivity.this,movie_list);
                    //adapter.swap(movie_list);
                    queue.stop();
                    view.setAdapter(new ReviewAdapter(DetailActivity.this,reviewsArrayList));
                    view.setLayoutManager(manager);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.getMessage());
//                bar.setVisibility(View.INVISIBLE);
  //              errortext.setVisibility(View.INVISIBLE);
            }
        });

        queue.add(mrequest);

    }

    public void get_trailer(String id){
        final RequestQueue queue= Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest mrequest=new JsonObjectRequest(Request.Method.GET, movieApi.MOVIE_REVIEWS_1+id+movieApi.MOVIE_TRAILER_2+getString(R.string.movie_key), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //          errortext.setVisibility(View.INVISIBLE);
                //        Toast.makeText(MainActivity.this,"swing",Toast.LENGTH_SHORT).show();
                //        bar.setVisibility(View.INVISIBLE);
                try {
                    JSONArray results=response.getJSONArray("youtube");
                    for(int i=0;i<results.length();i++){
                        JSONObject re=results.getJSONObject(i);
                        String name=re.getString("name");
                        String source=re.getString("source");

                        Reviews m=new Reviews(source,name);
                        trailerArrayList.add(m);
                    }
                    make_alert_dialog(trailerArrayList);


                    //                adapter=new MovieAdapter(MainActivity.this,movie_list);
                    //adapter.swap(movie_list);
                    queue.stop();
                  //  view.setAdapter(new ReviewAdapter(DetailActivity.this,reviewsArrayList));
                   // view.setLayoutManager(manager);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            private void make_alert_dialog(final ArrayList<Reviews> trailerArrayList) {
                String array[] =new String[trailerArrayList.size()];
                for(int i=0;i<trailerArrayList.size();i++){
                    array[i]=trailerArrayList.get(i).getReviewer();
                }

                AlertDialog.Builder builder=new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("TRAILER");
                builder.setItems(array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
trailerArrayList.get(which).getReview();
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.youtube.com/watch?v=" +trailerArrayList.get(which).getReview()));
                        startActivity(intent);
                    }
                });

builder.create();
                builder.show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.getMessage());
//                bar.setVisibility(View.INVISIBLE);
                //              errortext.setVisibility(View.INVISIBLE);
            }
        });

        queue.add(mrequest);



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list",reviewsArrayList);
        outState.putInt("index",manager.findFirstVisibleItemPosition());
    }
}
