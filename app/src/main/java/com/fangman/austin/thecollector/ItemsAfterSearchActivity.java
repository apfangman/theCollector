package com.fangman.austin.thecollector;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class ItemsAfterSearchActivity extends ActionBarActivity {

    ProgressBar progressBar;

    //static String API_URL = "http://104.236.238.213/api/getItemsForCollection/";
    static String API_URL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_after_search);

        final Intent intent = getIntent();
        progressBar = (ProgressBar) findViewById(R.id.itemsProgressBar);
        TextView textView = (TextView)findViewById(R.id.itemHeading);
        textView.setText(intent.getStringExtra("collectionName"));

        Button addCollectionButton = (Button)findViewById(R.id.addCollectionButton);
        addCollectionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                API_URL = "http://104.236.238.213/api/addCollection/" + intent.getStringExtra("collectionId") + "/" + intent.getStringExtra("userId");
                new Retriever().execute();
            }
        });
        API_URL = "http://104.236.238.213/api/getItemsForCollection/" + intent.getIntExtra("collectionId", -1);

    }

    private void setItemList(List<ItemData> dataList)
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items_after_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class Retriever extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            progressBar.setVisibility(View.GONE);
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            if(response == "Collection Added!")
            {
                TextView collectionAdded = (TextView)findViewById(R.id.collectionAddedText);
                collectionAdded.setVisibility(View.VISIBLE);
            }
            else
            {
                Log.i("INFO", response);
                List<ItemData> dataList = new Gson().fromJson(response, new TypeToken<List<ItemData>>(){}.getType());
                setItemList(dataList);
            }
        }
    }

    class ItemData
    {
        private Short id;
        private String name;
        private String picture;
        private Short userId;
        private Short collectionId;

        public String getName() { return name; }
        public String getPicture() { return picture; }
        public Short getUserId() { return userId; }
        public Short getCollectionId() { return collectionId; }

        public String toString()
        {
            return name;
        }
    }
}
