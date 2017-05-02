package com.mryan_cachedome.test.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mryan_cachedome.test.R;
import com.mryan_cachedome.test.application.MyImageLoader;

/**
 * name:Mr.Yan or Mr.TianChen
 * Data: 2017/4/10
 * 备注
 */

public class MyAdapter extends BaseAdapter {

    private final Context context;
    private final String[] urlImages;
    private final MyImageLoader instance;

    public MyAdapter(Context context, String[] urlImages, MyImageLoader instance) {
        this.context = context;
        this.urlImages = urlImages;
        this.instance = instance;
    }

    @Override
    public int getCount() {
        return urlImages.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoulder houlder;
        if(convertView==null){
            convertView = View.inflate(context, R.layout.listview_item,null);
            houlder = new ViewHoulder();
            houlder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(houlder);
        }else {
            houlder = (ViewHoulder) convertView.getTag();
        }
        //给图片赋值
        /**
         * 如果要是单纯的做网络请求图片，那么我们直接在这里写异步就可以了
         * 但是我们这么多张图片，就有可能需要做缓存了，
         * 那我们就写一个工具类，专门去做图片缓存
         */
            instance.display(houlder.img,urlImages[position]);

        return convertView;
    }
    class ViewHoulder {
        ImageView img;
    }
}
