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
import java.util.List;


public class RegisterUserActivity extends ActionBarActivity {

    ProgressBar progressBar;

    static String API_URL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Button submitButton = (Button)findViewById(R.id.registerUserSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText name = (EditText)findViewById(R.id.editTextRegisterName);
                EditText email = (EditText)findViewById(R.id.editTextRegisterEmail);
                EditText password = (EditText)findViewById(R.id.editTextRegisterPassword);
                EditText reenterPassword = (EditText)findViewById(R.id.editTextRegisterReenterPassword);

                TextView registerError = (TextView)findViewById(R.id.registerUserPasswordError);
                TextView fieldsError = (TextView)findViewById(R.id.registerUserFieldsError);

                String nameText = name.getText().toString().trim();
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();
                String reenterPasswordText = reenterPassword.getText().toString().trim();

                if(!nameText.isEmpty() && !emailText.isEmpty() && !passwordText.isEmpty() && !reenterPasswordText.isEmpty())
                {
                    if(reenterPasswordText.equals(passwordText))
                    {
                        registerError.setVisibility(View.GONE);
                        fieldsError.setVisibility(View.GONE);
                        API_URL = "http://104.236.238.213/api/registerUser/" + nameText + "/" + emailText + "/" + passwordText;
                        progressBar = (ProgressBar)findViewById(R.id.registerUserProgressBar);
                        progressBar.setVisibility(View.VISIBLE);
                        new Retriever().execute();
                    }
                    else
                    {
                        fieldsError.setVisibility(View.GONE);
                        registerError.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    fieldsError.setVisibility(View.VISIBLE);
                    registerError.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_user, menu);
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

    private void goToMain(String name, String id)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("id", id);
        startActivity(intent);
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
            else if(response.isEmpty())
            {
                TextView takenError = (TextView)findViewById(R.id.registerUserTakenError);
                takenError.setVisibility(View.VISIBLE);
            }
            else
            {
                progressBar.setVisibility(View.GONE);
                UserData data = new Gson().fromJson(response, new TypeToken<UserData>(){}.getType());
                goToMain(data.getName(), data.getId());
                Log.i("INFO", response);
            }
        }
    }

    class UserData
    {
        private String id;
        private String name;

        public String getName() { return name; }
        public String getId() { return id; }

        public String toString()
        {
            return name;
        }
    }
}
