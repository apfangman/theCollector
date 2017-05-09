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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends ActionBarActivity {

    static String API_URL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button)findViewById(R.id.signInButton);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText email = (EditText)findViewById(R.id.editTextEmail);
                EditText password = (EditText)findViewById(R.id.editTextPassword);

                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                if(!emailText.isEmpty() && !passwordText.isEmpty())
                {
                    API_URL = "http://104.236.238.213/api/checkLogin/" + emailText + "/" + passwordText;
                    new Retriever().execute();
                }
            }
        });
        Button registerButton = (Button)findViewById(R.id.registerUserButton);
        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToRegister();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private void goToRegister()
    {
        Intent intent = new Intent(this, RegisterUserActivity.class);
        startActivity(intent);
    }

    class Retriever extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {}

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

            TextView signInError = (TextView)findViewById(R.id.loginErrorText);

            if(response != "")
            {
                signInError.setVisibility(View.GONE);
                UserData data = new Gson().fromJson(response, new TypeToken<UserData>(){}.getType());
                goToMain(data.getName(), data.getId());
            }
            else
            {
                signInError.setVisibility(View.VISIBLE);
            }
        }
    }

    class UserData
    {
        private Boolean valid;
        private String id;
        private String name;

        public Boolean isValid() { return valid; }
        public String getId() { return id; }
        public String getName() { return name; }

        public String toString()
        {
            return name;
        }
    }
}
