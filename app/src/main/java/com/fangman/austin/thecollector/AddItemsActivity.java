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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class AddItemsActivity extends ActionBarActivity {

    ProgressBar progressBar;

    static String API_URL = "";

    static String userId = "";
    static String userName = "";
    static String collectionId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        collectionId = intent.getStringExtra("collectionId");

        progressBar = (ProgressBar) findViewById(R.id.addItemProgress);

        final boolean userSpecific = intent.getBooleanExtra("userSpecific", true);

        TextView textView = (TextView)findViewById(R.id.addItemsHeader);
        textView.setText("Add Items to " + intent.getStringExtra("collectionName"));

        final TextView emptyNameError = (TextView)findViewById(R.id.emptyNameError);

        Button lAddItemsButton = (Button)findViewById(R.id.addItemsButton);
        lAddItemsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText itemName = (EditText)findViewById(R.id.editTextItemName);
                String itemNameText = itemName.getText().toString().trim();

                if(!itemNameText.isEmpty())
                {
                    emptyNameError.setVisibility(View.GONE);
                    if(userSpecific)
                    {
                        API_URL = "http://104.236.238.213/api/addItemToCollectionForUser/" + itemNameText + "/" + collectionId + "/" + userId;
                        new Retriever().execute();
                    }
                    else
                    {
                        API_URL = "http://104.236.238.213/api/addItemToCollection/" + itemNameText + "/" + collectionId + "/" + userId;
                        new Retriever().execute();
                    }
                }
                else
                {
                    emptyNameError.setVisibility(View.VISIBLE);
                }
            }
        });

        Button lAddItemsHomeButton = (Button)findViewById(R.id.addItemsHomeButton);
        lAddItemsHomeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToCollections();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_items, menu);
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
            if(response == null)
            {
                Log.i("INFO", "THERE WAS AN ERROR");
            }
            else
            {
                progressBar.setVisibility(View.GONE);
                EditText itemName = (EditText)findViewById(R.id.editTextItemName);
                itemName.setText("");
                final TextView addItemMessage = (TextView)findViewById(R.id.addItemMessage);
                addItemMessage.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        addItemMessage.setVisibility(View.GONE);
                    }
                }, 3000);
                Log.i("INFO", response);
            }
        }
    }

    private void goToCollections()
    {
        Intent intent = new Intent(this, CollectionsActivity.class);
        intent.putExtra("userName", userName);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}
