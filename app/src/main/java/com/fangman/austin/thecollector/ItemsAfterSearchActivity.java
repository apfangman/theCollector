package com.fangman.austin.thecollector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class ItemsAfterSearchActivity extends ActionBarActivity {

    ProgressBar progressBar;

    //static String API_URL = "http://104.236.238.213/api/getItemsForCollection/";
    static String API_URL = "";
    static String userId = "";
    static String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_after_search);

        final Intent intent = getIntent();
        progressBar = (ProgressBar) findViewById(R.id.itemsProgressBar);
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
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
        API_URL = "http://104.236.238.213/api/getItemsForCollection/" + intent.getStringExtra("collectionId");
        new Retriever().execute();
    }

    private void setItemList(List<ItemData> dataList)
    {
        GridView itemList = (GridView)findViewById(R.id.itemGridView);

        ItemAdapter adapter = new ItemAdapter(this, dataList);
        itemList.setAdapter(adapter);
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
            goToMain();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToMain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    //This class came from http://stackoverflow.com/questions/5776851/load-image-from-url,
    //Kyle Clegg and King of Masses
    class ImageDownloader extends AsyncTask<String, Void, Bitmap>
    {
        ImageView image;

        public ImageDownloader(ImageView image)
        {
            this.image = image;
        }

        protected Bitmap doInBackground(String... urls)
        {
            String url = urls[0];
            Bitmap bitmap = null;
            try
            {
                InputStream is = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(is);
            }
            catch (Exception e)
            {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result)
        {
            image.setImageBitmap(result);
        }
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
            else if(response.equals("Collection Added!\n"))
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

    class ItemAdapter extends ArrayAdapter<ItemData>
    {
        public ItemAdapter(Context context, List<ItemData> items)
        {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            ItemData item = getItem(position);

            if(view == null)
            {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_for_grid, parent, false);
            }

            ImageView itemImage = (ImageView)view.findViewById(R.id.itemImage);
            TextView itemName = (TextView)view.findViewById(R.id.itemText);
            Button itemButton1 = (Button)view.findViewById(R.id.itemButton1);
            Button itemButton2 = (Button)view.findViewById(R.id.itemButton2);
            Button itemButton3 = (Button)view.findViewById(R.id.itemButton3);

            itemName.setText(item.getName());
            String button1text = item.getButton1Text();
            String button2text = item.getButton2Text();
            String button3text = item.getButton3Text();

            if(!item.getPicture().equals(""))
            {
                new ImageDownloader(itemImage)
                        .execute(item.getPicture());
            }

            //If a button has no text, make it invisible
            //If it is checked, set color to blue
            if(button1text.isEmpty())
            {
                itemButton1.setVisibility(View.GONE);
            }
            else
            {
                itemButton1.setText(item.getButton1Text());
            }

            if(button2text.isEmpty())
            {
                itemButton2.setVisibility(View.GONE);
            }
            else
            {
                itemButton2.setText(item.getButton2Text());
            }

            if(button3text.isEmpty())
            {
                itemButton3.setVisibility(View.GONE);
            }
            else
            {
                itemButton3.setText(item.getButton3Text());
            }

            return view;
        }
    }

    class ItemData
    {
        private Short id;
        private String name;
        private String picture;
        private String storeLink;
        private Short collectionId;
        private String buttonOne;
        private String buttonTwo;
        private String buttonThree;

        public String getName() { return name; }
        public String getPicture() { return picture; }
        public Short getCollectionId() { return collectionId; }
        public String getStoreLink() { return storeLink; }
        public String getButton1Text() { return buttonOne; }
        public String getButton2Text() { return buttonTwo; }
        public String getButton3Text() { return buttonThree; }

        public String toString()
        {
            return name;
        }
    }
}
