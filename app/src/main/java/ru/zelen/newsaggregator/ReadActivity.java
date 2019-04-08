package ru.zelen.newsaggregator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReadActivity extends AppCompatActivity {

    String urlAddress;
    ListView listView;
    ArrayList<Map<String, String>> data;
    Map<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        Intent intent = getIntent();
        urlAddress = intent.getStringExtra("urlAddress");

        listView = findViewById(R.id.listVIew);

        data = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                map = new HashMap<>();
                map = data.get(position);
                Uri uri = Uri.parse(map.get("link"));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        new readRssChanel().execute();
    }

    public InputStream getInputStream(URL url)
    {
        try
        {
            return url.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public void showErrorAndFinishActivity(String errorMessage)
    {
        Intent intent;
        intent = new Intent();
        intent.putExtra("errorMessage", errorMessage);
        setResult(RESULT_OK, intent);
        finish();
    }

    public class readRssChanel extends AsyncTask<Integer, Void, Exception>
    {
        ProgressDialog progressDialog = new ProgressDialog(ReadActivity.this);

        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading rss news, please wait...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... integers) {
            try
            {
                URL url = new URL(urlAddress);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();

                InputStream inputStream = getInputStream(url);
                if (inputStream == null){
                    showErrorAndFinishActivity("No connection to this chanel");
                }
                else {
                    xpp.setInput(inputStream, "UTF_8");
                }

                boolean insideItem = false;

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if (eventType == XmlPullParser.START_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            insideItem = true;
                            map = new HashMap<>();
                        }
                        else if (xpp.getName().equalsIgnoreCase("title"))
                        {
                            if (insideItem)
                            {
                                map.put("title", xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("link"))
                        {
                            if (insideItem)
                            {
                                map.put("link", xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("description"))
                        {
                            if (insideItem)
                            {
                                map.put("description", xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("pubDate"))
                        {
                            if (insideItem)
                            {
                                map.put("pubDate", xpp.nextText());
                            }
                        }
                    }
                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item"))
                    {
                        insideItem = false;
                        data.add(map);
                    }
                    eventType = xpp.next();
                }
            }
            catch (MalformedURLException e)
            {
                exception = e;
            }
            catch (XmlPullParserException e)
            {
                exception = e;
            }
            catch (IOException e)
            {
                exception = e;
            }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);

            String[] from = { "title", "description", "pubDate" };
            int[] to = { R.id.tvTitle, R.id.tvDescription, R.id.tvPubDate };

            if (data.isEmpty()){
                showErrorAndFinishActivity("Can't read this chanel ");
            }

            SimpleAdapter sAdapter = new SimpleAdapter(ReadActivity.this, data, R.layout.raw_instance, from, to);
            listView.setAdapter(sAdapter);

            progressDialog.dismiss();
        }
    }
}
