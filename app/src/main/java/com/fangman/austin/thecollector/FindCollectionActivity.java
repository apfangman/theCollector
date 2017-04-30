package com.fangman.austin.thecollector;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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


public class FindCollectionActivity extends ActionBarActivity {

    static String userId = "";
    static String API_URL = "";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_collection);

        Intent intent = getIntent();
        userId = intent.getStringExtra("id");

        progressBar = (ProgressBar) findViewById(R.id.findCollectionsProgress);

        final EditText searchBar = (EditText)findViewById(R.id.findCollectionText);

        Button lSearchButton = (Button)findViewById(R.id.searchButton);
        lSearchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                API_URL = "http://104.236.238.213/api/findCollections/" + searchBar.getText();
                new Retriever().execute();
            }
        });
    }

    private void setCollectionList(List<CollectionData> dataList)
    {
        ListView collectionList = (ListView)findViewById(R.id.findCollectionsListView);

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
                CollectionData item = (CollectionData)parent.getItemAtPosition(position);
                Intent intent = getIntent();
                userId = intent.getStringExtra("id");
                goToItemsAfterSearch(item.getName(), item.getId(), userId);
            }
        });
    }

    private void goToItemsAfterSearch(String name, int collectionId, String userId)
    {
        Intent intent = new Intent(this, ItemsAfterSearchActivity.class);
        intent.putExtra("collectionName", name);
        intent.putExtra("collectionId", collectionId);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_collection, menu);
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
            if(response == null)
            {
                response = "THERE WAS AN ERROR";
            }
            if(response.trim() != "[]")
            {
                List<CollectionData> dataList = new Gson().fromJson(response, new TypeToken<List<CollectionData>>(){}.getType());
                setCollectionList(dataList);
            }
        }
    }

    class CollectionData
    {
        private Short id;
        private String name;
        private String picture;

        public String getName() { return name; }
        public String getPicture() { return picture; }
        public Short getId() { return id; }

        public String toString()
        {
            return name;
        }
    }
}
