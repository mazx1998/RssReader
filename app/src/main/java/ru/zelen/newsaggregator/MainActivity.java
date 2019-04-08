package ru.zelen.newsaggregator;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//RSS chanels that works:
//http://ua-daily.com/all_news/feed
//http://mir24.net/index.php?option=com_sdrsssyndicator&feed_id=1&format=raw
//http://rf-lenta.ru/engine/rss.php
//https://fishnews.ru/rss_full
//http://uanews.pp.ua/feed

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnReadNews;
    EditText editRssChanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnReadNews = findViewById(R.id.btnReadNews);
        btnReadNews.setOnClickListener(this);

        editRssChanel = findViewById(R.id.editRssChanel);
        editRssChanel.setText("http://mir24.net/index.php?option=com_sdrsssyndicator&feed_id=1&format=raw");
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        String rssChanel;

        intent = new Intent(MainActivity.this, ReadActivity.class);
        rssChanel = editRssChanel.getText().toString();

        if (!rssChanel.equals("")) {
            intent.putExtra("urlAddress", editRssChanel.getText().toString());
            startActivityForResult(intent, 1);
        }
        else {
            Toast.makeText(this, "Please, enter url of chanel", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;
        String intentMessage = data.getStringExtra("errorMessage");
        if (!intentMessage.equals("")){
            Toast.makeText(this, intentMessage, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
        }

    }
}
