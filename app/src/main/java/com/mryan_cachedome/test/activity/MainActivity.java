package com.mryan_cachedome.test.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.mryan_cachedome.test.R;
import com.mryan_cachedome.test.adapter.MyAdapter;
import com.mryan_cachedome.test.application.MyImageLoader;
import com.mryan_cachedome.test.images.ImageUrls;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    //加载Veiw
    private void initView() {
        MyImageLoader instance = MyImageLoader.getInstance();
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new MyAdapter(MainActivity.this, ImageUrls.UrlImages, instance));
    }
}
