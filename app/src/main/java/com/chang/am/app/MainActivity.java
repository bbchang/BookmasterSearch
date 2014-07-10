package com.chang.am.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    ImageButton mainButton;
    EditText mainEditText;
    GridView mainGridView;
    ImageAdapter mImageAdapter;
    ArrayList mNameList = new ArrayList();
    ShareActionProvider mShareActionProvider;
    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    SharedPreferences mSharedPreferences;

    private static String api_key = "api_key=OOslfso5nAGPXxWH8bXp0PxNt4grG6MybaItGSLWyXyRK9Gsmc";
    private static String BLOG_QUERY_URL = "http://api.tumblr.com/v2/blog/koreanmodel.tumblr.com/posts/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 11. Add a spinning progress bar (and make sure it's off)
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);

        setContentView(R.layout.activity_main);
        // 1. Access the TextView defined in layout XML
        // and then set its text
        //mainTextView = (TextView) findViewById(R.id.main_textview);

        // 2. Access the Button defined in layout XML
        // and listen for it here
        mainButton = (ImageButton) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        // 3. Access the EditText defined in layout XML
        mainEditText = (EditText) findViewById(R.id.main_edittext);

        // 4. Access the GridView
        mainGridView = (GridView) findViewById(R.id.main_gridview);

        // 5. Set this activity to react to list items being pressed
        mainGridView.setOnItemClickListener(this);

        // 7. Greet the user, or ask for their name if new
        displayWelcome();

        // 10. Create a ImageAdapter for the GridView
        mImageAdapter = new ImageAdapter(this);

        // Set the GridView to use the ImageAdapter
        mainGridView.setAdapter(mImageAdapter);

    }


    @Override
    @TargetApi(14)
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu.
        // Adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Access the Share Item defined in menu XML
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        // Access the object responsible for
        // putting together the sharing submenu
        if (shareItem != null) {
            mShareActionProvider = (ShareActionProvider)shareItem.getActionProvider();
        }

        // Create an Intent to share your content
        setShareIntent();

        return true;
    }

    @TargetApi(14)
    private void setShareIntent() {

        if (mShareActionProvider != null) {

            // create an Intent with the contents of the TextView
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Frashion");
            //shareIntent.putExtra(Intent.EXTRA_TEXT, mainTextView.getText());

            // Make sure the provider knows
            // it should work with that Intent
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void onClick(View view) {
        /** Take what was typed into the EditText
        // and use in TextView
        mainTextView.setText(mainEditText.getText().toString()
                + " is learning Android development!");

        // Also add that value to the list shown in the ListView
        //mNameList.add(mainEditText.getText().toString());
        //Collections.reverse(mNameList);
        //mArrayAdapter.notifyDataSetChanged();
        mArrayAdapter.insert(mainEditText.getText().toString(), 0);

        // 6. The text you'd like to share has changed,
        // and you need to update
        setShareIntent();**/

        // 9. Take what was typed into the EditText and use in search
        queryName(mainEditText.getText().toString());
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        // 12. Now that the user's chosen a book, grab the cover data
        JSONObject jsonObject = (JSONObject) mImageAdapter.getItem(position);
        String coverID = jsonObject.optString("cover_i", "");
        //String titleText = jsonObject.optString("title", "title");
        String titleText = mainEditText.getText().toString().toUpperCase();
        String authorText = "";
        try {
            JSONArray authors = jsonObject.getJSONArray("author_name");
            for (int i=1;i< authors.length();i++) {
                authorText += authors.getString(i) + " ";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //create an Intent to take you over to a new Detailactivity
        Intent detailIntent = new Intent(this, DetailActivity.class);

        //pack away the data about the cover
        //into your Intent before you head out
        detailIntent.putExtra("coverID", coverID);
        detailIntent.putExtra("author_name", authorText);
        detailIntent.putExtra("title", titleText);

        //Start the next activity using your preparedIntent
        startActivity(detailIntent);
    }

    public void displayWelcome() {

        //Access the device's key-value storage
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        //Read the user's name,
        //or an empty string if nothing found
        String name = mSharedPreferences.getString(PREF_NAME, "");

        if (name.length() > 0) {

            //If the name is valid, display a Toast welcoming them
            Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_LONG).show();
        } else {
            // otherwise, show a dialog to ask for their name
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Hello!");
            alert.setMessage("What is your name?");

            //Create EditText for entry
            final EditText input = new EditText(this);
            alert.setView(input);

            //Make an "OK" button to save the name
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton){
                    //Grab the EditText's input
                    String inputName = input.getText().toString();

                    //Put it into memory (don't forget to commit!)
                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putString(PREF_NAME, inputName);
                    e.commit();

                    //Welcome the new user
                    Toast.makeText(getApplicationContext(), "Welcome, " + inputName + "!", Toast.LENGTH_LONG).show();
                    }
                });

            // Make a "Cancel" button
            // that simply dismisses the alert
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton){}
            });

            alert.show();
        }
    }

    private void queryName(String searchString) {

        // Prepare your search string to be put in a URL
        // It might have reserved characters or something
        String urlString = "";
        try {
            urlString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException e) {

            // if this fails for some reason, let the user know why
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Create a client to perform networking
        AsyncHttpClient client = new AsyncHttpClient();
        // 11. start progress bar
        setProgressBarIndeterminateVisibility(true);
        // Have the client get a JSONArray of data
        // and define how to respond
        String photoURLString = "photo?tag=";
        //TO-DO: Create enum for other choices text, etc.
        String fullURL = BLOG_QUERY_URL + photoURLString + urlString + "&" + api_key;

        client.get(fullURL, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject jsonObject){
                // 11. stop progress bar
                setProgressBarIndeterminateVisibility(false);
                // Display a "Toast" message
                // to announce your success
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();

                try {
                    // update the data in your custom method.
                    JSONObject res = jsonObject.getJSONObject("response");
                    System.out.println(res);
                    JSONObject posts = res.optJSONObject("posts");
                    JSONArray photos = res.optJSONArray("posts");
                    mImageAdapter.updateData(photos);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                // 11. stop progress bar
                setProgressBarIndeterminateVisibility(false);
                // Display a "Toast" message
                // to announce the failure
                Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                // Log error message
                // to help solve any problems
                Log.e("omg android", statusCode + " " + throwable.getMessage());
            }
        });

    }
}
