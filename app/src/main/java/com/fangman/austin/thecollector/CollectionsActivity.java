package com.fangman.austin.thecollector;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class CollectionsActivity extends ActionBarActivity {

    ProgressBar progressBar;

    static String API_URL = "";

    static String userId = "";

    static String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        Intent intent = getIntent();
        API_URL = "http://104.236.238.213/api/getCollections/" + intent.getStringExtra("userId");

        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        new Retriever().execute();
    }

    private void setCollectionList(List<CollectionData> dataList)
    {
        ListView collectionList = (ListView)findViewById(R.id.listView);

        ArrayAdapter<CollectionData> adapter = new ArrayAdapter<CollectionData>(
                this,
                android.R.layout.simple_list_item_1,
                dataList);
        collectionList.setAdapter(adapter);
        collectionList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = getIntent();

                CollectionData item = (CollectionData)parent.getItemAtPosition(position);
                goToItems(item.getName(), item.getCollectionId());
            }
        });
    }

    private void goToItems(String name, String collectionId)
    {
        Intent intent = new Intent(this, ItemsActivity.class);
        intent.putExtra("collectionName", name);
        intent.putExtra("collectionId", collectionId);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    private void goToMain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collections, menu);
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
            goToMain();
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
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            List<CollectionData> dataList = new Gson().fromJson(response, new TypeToken<List<CollectionData>>(){}.getType());
            setCollectionList(dataList);
        }
    }

    class CollectionData
    {
        private String id;
        private String name;
        private String picture;
        private String userId;
        private String collectionId;

        public String getName() { return name; }
        public String getPicture() { return picture; }
        public String getUserId() { return userId; }
        public String getCollectionId() { return collectionId; }

        public String toString()
        {
            return name;
        }
    }
}
