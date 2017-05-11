package com.fangman.austin.thecollector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.LinearLayout;
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


public class ItemsActivity extends ActionBarActivity {

    ProgressBar progressBar;

    static String API_URL = "";

    static String userId = "";
    static String userName = "";
    static String collectionId = "";
    static String collectionName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        Intent intent = getIntent();
        progressBar = (ProgressBar) findViewById(R.id.itemsProgressBar);
        collectionId = intent.getStringExtra("collectionId");
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        collectionName = intent.getStringExtra("collectionName");

        TextView textView = (TextView)findViewById(R.id.itemHeading);
        textView.setText(collectionName);
        API_URL = "http://104.236.238.213/api/getItemsForSingleUserCollection/" + collectionId + "/" + intent.getStringExtra("userId");
        new Retriever().execute();

        Button addUserItemButton = (Button)findViewById(R.id.addUserItemButton);
        addUserItemButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToAddItems();
            }
        });
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
        getMenuInflater().inflate(R.menu.menu_items, menu);
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

    private void goToAddItems()
    {
        Intent intent = new Intent(this, AddItemsActivity.class);
        intent.putExtra("collectionName", collectionName);
        intent.putExtra("collectionId", collectionId);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        intent.putExtra("userSpecific", true);
        startActivity(intent);
    }

    private void goToMain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    private void goToStore(String itemName)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ebay.com/sch/i.html?_nkw=" + itemName.replace(" ", "%20")));
        startActivity(intent);
    }

    //Modified from http://www.androidauthority.com/use-remote-web-api-within-android-app-617869/
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
                response = "THERE WAS AN ERROR";
            }
            if(response.equals("Item Updated!\n") || response.equals("Item Deleted!\n"))
            {
                return;
            }
            else
            {
                progressBar.setVisibility(View.GONE);
                Log.i("INFO", response);
                List<ItemData> dataList = new Gson().fromJson(response, new TypeToken<List<ItemData>>(){}.getType());
                setItemList(dataList);
            }
        }
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

    class ItemAdapter extends ArrayAdapter<ItemData>
    {
        public ItemAdapter(Context context, List<ItemData> items)
        {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            final ItemData item = getItem(position);

            if(view == null)
            {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_for_grid, parent, false);
            }

            final LinearLayout itemLayout = (LinearLayout)view.findViewById(R.id.itemLayout);
            final ImageView itemImage = (ImageView)view.findViewById(R.id.itemImage);
            final TextView itemName = (TextView)view.findViewById(R.id.itemText);
            final Button itemButton1 = (Button)view.findViewById(R.id.itemButton1);
            final Button itemButton2 = (Button)view.findViewById(R.id.itemButton2);
            final Button itemButton3 = (Button)view.findViewById(R.id.itemButton3);
            final Button itemButton4 = (Button)view.findViewById(R.id.itemButton4);
            final Button itemButton5 = (Button)view.findViewById(R.id.itemButton5);

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
                if(item.getButton1Checked())
                {
                    itemButton1.setBackgroundColor(Color.GREEN);
                    itemButton1.setText(itemButton1.getText() + " \u2713");
                }
                itemButton1.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        switchColorAndText(itemButton1);
                        API_URL = "http://104.236.238.213/api/updateItem/" + item.getId() + "/" + userId + "/" + "1";
                        new Retriever().execute();
                    }
                });
            }

            if(button2text.isEmpty())
            {
                itemButton2.setVisibility(View.GONE);
            }
            else
            {
                itemButton2.setText(item.getButton2Text());
                if(item.getButton2Checked())
                {
                    itemButton2.setBackgroundColor(Color.GREEN);
                    itemButton2.setText(itemButton2.getText() + " \u2713");
                }
                itemButton2.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        switchColorAndText(itemButton2);
                        API_URL = "http://104.236.238.213/api/updateItem/" + item.getId() + "/" + userId + "/" + "2";
                        new Retriever().execute();
                    }
                });
            }

            if(button3text.isEmpty())
            {
                itemButton3.setVisibility(View.GONE);
            }
            else
            {
                itemButton3.setText(item.getButton3Text());
                if(item.getButton3Checked())
                {
                    itemButton3.setBackgroundColor(Color.GREEN);
                    itemButton3.setText(itemButton3.getText() + " \u2713");
                }
                itemButton3.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        switchColorAndText(itemButton3);
                        API_URL = "http://104.236.238.213/api/updateItem/" + item.getId() + "/" + userId + "/" + "3";
                        new Retriever().execute();
                    }
                });
            }

            itemButton4.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    API_URL = "http://104.236.238.213/api/deleteItemForCollectionForUser/" + item.getId() + "/" + userId;
                    new Retriever().execute();
                    /*itemButton1.setVisibility(View.GONE);
                    itemButton2.setVisibility(View.GONE);
                    itemButton3.setVisibility(View.GONE);
                    itemImage.setVisibility(View.GONE);
                    itemName.setVisibility(View.GONE);*/
                    itemLayout.setVisibility(View.GONE);
                }
            });

            itemButton5.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    goToStore(item.getName());
                }
            });
            return view;
        }

        private void switchColorAndText(Button itemButton)
        {
            ColorDrawable itemButtonColor = (ColorDrawable)itemButton.getBackground();
            if(itemButtonColor.getColor() == Color.GRAY)
            {
                itemButton.setBackgroundColor(Color.GREEN);
                itemButton.setText(itemButton.getText() + " \u2713");
            }
            else
            {
                itemButton.setBackgroundColor(Color.GRAY);
                itemButton.setText(itemButton.getText().subSequence(0, itemButton.getText().length() - 2));
            }
        }
    }

    class ItemData
    {
        private String id;
        private String name;
        private String picture;
        private Short userId;
        private Short collectionId;
        private String buttonOne;
        private String buttonTwo;
        private String buttonThree;
        private Boolean buttonOneChecked;
        private Boolean buttonTwoChecked;
        private Boolean buttonThreeChecked;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getPicture() { return picture; }
        public Short getUserId() { return userId; }
        public Short getCollectionId() { return collectionId; }
        public String getButton1Text() { return buttonOne; }
        public String getButton2Text() { return buttonTwo; }
        public String getButton3Text() { return buttonThree; }
        public Boolean getButton1Checked() { return buttonOneChecked; }
        public Boolean getButton2Checked() { return buttonTwoChecked; }
        public Boolean getButton3Checked() { return buttonThreeChecked; }

        public String toString()
        {
            return name;
        }
    }
}
