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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreateCollectionActivity extends ActionBarActivity {

    ProgressBar progressBar;

    static String API_URL = "";

    static String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_collection);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        progressBar = (ProgressBar) findViewById(R.id.createCollectionProgressBar);

        Button lSubmitCollectionButton = (Button)findViewById(R.id.submitCollectionButton);
        lSubmitCollectionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText collectionName = (EditText)findViewById(R.id.editTextCollectionName);
                EditText buttonOne = (EditText)findViewById(R.id.editTextButton1);
                EditText buttonTwo = (EditText)findViewById(R.id.editTextButton2);
                EditText buttonThree = (EditText)findViewById(R.id.editTextButton3);

                TextView nameAndButtonError = (TextView)findViewById(R.id.nameAndButtonError);

                String collectionNameText = collectionName.getText().toString().trim();
                String buttonOneText = buttonOne.getText().toString().trim();
                String buttonTwoText = buttonTwo.getText().toString().trim();
                String buttonThreeText = buttonThree.getText().toString().trim();

                if(!buttonThreeText.isEmpty() && buttonTwoText.isEmpty())
                {
                    buttonTwoText = buttonThreeText;
                    buttonThreeText = "";
                }

                if(!collectionNameText.isEmpty() && !buttonOneText.isEmpty())
                {
                    API_URL = "http://104.236.238.213/api/createCollection/" + collectionNameText + "/" + userId + "/" + buttonOneText;
                    if(!buttonTwoText.isEmpty())
                    {
                        API_URL = API_URL + "/" + buttonTwoText;
                        if(!buttonThreeText.isEmpty())
                        {
                            API_URL = API_URL + "/" + buttonThreeText;
                        }
                    }
                    nameAndButtonError.setVisibility(View.GONE);
                    new Retriever().execute();
                }
                else
                {
                    nameAndButtonError.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_collection, menu);
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
                CollectionData data = new Gson().fromJson(response, new TypeToken<CollectionData>(){}.getType());
                goToAddItems(data.getName(), data.getId(), data.getUserId());
                Log.i("INFO", response);
            }
        }
    }

    class CollectionData
    {
        private String id;
        private String name;
        private String userId;
        private String collectionId;

        public String getName() { return name; }
        public String getId() { return id; }
        public String getUserId() { return userId; }
        public String getCollectionId() { return collectionId; }

        public String toString()
        {
            return name;
        }
    }

    private void goToAddItems(String name, String collectionId, String userId)
    {
        Intent intent = new Intent(this, AddItemsActivity.class);
        intent.putExtra("collectionName", name);
        intent.putExtra("collectionId", collectionId);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}
