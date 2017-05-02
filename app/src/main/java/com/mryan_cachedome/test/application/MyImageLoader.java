package com.mryan_cachedome.test.application;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.mryan_cachedome.test.utils.MD5Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * name:Mr.Yan or Mr.TianChen
 * Data: 2017/4/10
 * 备注
 */

public class MyImageLoader {
    private final LruCache<String, Bitmap> mLruCache;
    private final File mDirectory;
    private final ExecutorService mNewFixedThreadPool;
    private final Handler mHandler;
    private FileOutputStream mFileOutputStream;
    //在这里做网络请求
    /**
     * 写成单例形式
     * 单例又分恶汉式和懒汉式
     * 我们写一个懒汉式的单例
     */
    ////////////////////////////
    /**
     * 先看下内存有没有图片 LruCaChe
     * 再看下磁盘有没有图片
     * 最后才去网络获取
     * 在这里做他们的初始化操作
     */
    private MyImageLoader(){
        //创建一个handle
        mHandler = new Handler() {};

        /**
         * 为了避免内存溢出
         * 设置大小一般只占八分之一
         * 获取当前应用运行时maxMemory  设置最大值,如果超过这个值就销毁它
         */
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        /**
         *  内存处理 比如内存可以是 10M 有10个1M的  正好是10M  现在我们想要加载一个4M的高清图片
         *  那我们怎么知道图片到底有多大呢
         *  指定它的键值对
         *  键是String放的是路径,值是BitMap放的是图片
         *  计算图片的大小
         *  返回值是拿到图片的高度乘上它的像素点getRowBytes()
         */
        mLruCache = new LruCache<String, Bitmap>(maxSize){
            //获取单张图片大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //计算图片的大小
                //返回值是拿到图片的高度乘上它的像素点getRowBytes()
                return value.getHeight()*value.getRowBytes();
            }
        };
        /**
         * 接下来准备磁盘缓存
         */
        //磁环缓存的路径
        mDirectory = new File(Environment.getExternalStorageDirectory(),"imageLoader");
        //判断这个文件夹是否存在   如果不存在的话  就创建出来
        if(!mDirectory.exists()){
             mDirectory.mkdirs();
        }

        /**
         * 在有就是网络请求
         * 当前图片有很多  每一张都开启一个线程,我们知道网络请求的操作都要写到子线程中,
         * 都开启一个线程的话就会开启很多个线程 非常的浪费内存,消耗资源,
         * 所以我们利用一个线程池的操作,
         * 在这里一个非常简单的API Executors
         */
        //设置线程池大小
        mNewFixedThreadPool = Executors.newFixedThreadPool(5);


    }
    //定义一个对象//静态的对象保证唯一//开始时 不创建这个对象
    private static MyImageLoader myImageLoader = null;
    //在这里就开始创建了
    public static MyImageLoader getInstance(){
        if(myImageLoader==null){
            //这是一个懒汉式
            myImageLoader = new MyImageLoader();
        }
        return myImageLoader;
    }
    //接下来就写一些逻辑，做一下获取图片的操作
    //给这里一个控件，这个方法就给这个控件设置图片
    public void display(ImageView imageView,String path){
        //先从内存中获取图片
        Bitmap bitmap = mLruCache.get(path);
        //判断内存有没有图片
        if(bitmap!=null){
            //如果不为空的话就显示
            imageView.setImageBitmap(bitmap);
            Log.e("Message===========>","从内存中获取图片");
            return;
        }
        //如果能够继续往前走的话就代表内存中没有我们要取得图片
        //那我们就去磁盘里去获取资源
        bitmap = getBitmaoFromLacal(path);
        //判断内存有没有图片-----到磁盘中去
        if(bitmap!=null){
            //如果不为空的话就显示
            imageView.setImageBitmap(bitmap);
            mLruCache.put(path,bitmap);
            Log.e("Message===========>","从磁盘中获取图片");
            return;
        }
        //如果磁盘中没有图片,那我们就去请求网络了
        //创建一个请求网络的方法
        getBitmapNet(imageView,path);
    }
    //网络请求 获取图片资源  这里就不要返回值了,因为这里用的是异步
    private void getBitmapNet(final ImageView imageView, final String path) {
        mNewFixedThreadPool.execute(new Runnable() {
            //到子线程中
            @Override
            public void run() {
                //请求网络
                try {
                    //这里设置防止图片闪烁的标记
                    imageView.setTag(path);
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000);
                    int responseCode = connection.getResponseCode();
                    if(responseCode==200){
                        InputStream inputStream = connection.getInputStream();
                        //将这个流解析成图片
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //如果我们直接展示图片可能会产生图片的闪烁,所以我们要给它设置一个标记来避免
                                if(path.equals(imageView.getTag())){
                                    imageView.setImageBitmap(bitmap);
                                    Log.e("Message===========>",bitmap+"");
                                    //当我们请求到图片后,根据逻辑  我们还要给内存和磁盘存一份
                                    //存入磁盘中一份  写个方法
                                    writeBitmapToLocal(bitmap,path);
                                    //保存到内存一份
                                    if(path!=null&&bitmap!=null){
                                        mLruCache.put(path,bitmap);
                                    }
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //写到本地
    private void writeBitmapToLocal(Bitmap bitmap, String path) {
        String md5 = MD5Utils.MD5(path,false);
        try {
            mFileOutputStream = new FileOutputStream(new File(mDirectory, md5));
            //有一个方便的API
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,mFileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从磁盘获取图片
     * @param path
     * @return
     */
    private Bitmap getBitmaoFromLacal(String path) {
        try {
            String md5 = MD5Utils.MD5(path,false);
            FileInputStream fileInputStream = new FileInputStream(new File(mDirectory,md5));
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

