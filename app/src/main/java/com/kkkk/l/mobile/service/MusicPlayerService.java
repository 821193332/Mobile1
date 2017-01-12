package com.kkkk.l.mobile.service;


import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.kkkk.l.mobile.IMusicPlayerService;
import com.kkkk.l.mobile.bean.MediaItem;

import java.io.IOException;
import java.util.ArrayList;




/**
 * Created by KangYueLong on 2017/1/12.
 */

public class MusicPlayerService extends Service {
       public  static final String OPEN_COMPLETE="open_complete";

        IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {
        MusicPlayerService service =MusicPlayerService.this;
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
                service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();

        }

        @Override
        public void pause() throws RemoteException {
            service.pause();

        }

        @Override
        public String getAudioName() throws RemoteException {
            return service.getAudioName();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return service.getArtistName();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public void next() throws RemoteException {
           service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public void setPlayMode(int mode) throws RemoteException {
                  service.setPlayMode(mode);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mediaPlayer.isPlaying();
        }

        @Override
        public void seekTo(int postion) throws RemoteException {
           mediaPlayer.seekTo(postion);
        }
    };
    private ArrayList<MediaItem> mediaItems;
    private boolean isLoaded =false;
    private  MediaItem mediaItem;
    private int position;
    private  MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getDataFromLocal();
    }

    private void  getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST
                };
                Cursor cusor = resolver.query(uri, objs, null, null, null);
                if (cusor != null) {

                    while (cusor.moveToNext()) {

                        MediaItem mediaItem = new MediaItem();

                        //添加到集合中
                        mediaItems.add(mediaItem);//可以

                        String name = cusor.getString(0);
                        mediaItem.setName(name);
                        long duration = cusor.getLong(1);
                        mediaItem.setDuration(duration);
                        long size = cusor.getLong(2);
                        mediaItem.setSize(size);
                        String data = cusor.getString(3);//播放地址
                        mediaItem.setData(data);
                        String artist = cusor.getString(4);//艺术家
                        mediaItem.setArtist(artist);
                    }
                    cusor.close();
                }
                isLoaded = true;
            }
        }.start();
    }
    /**
     * 根据位置打开一个音频并且播放
     *
     * @param position
     */
    void openAudio(int position) {
        if (mediaItems != null && mediaItems.size() > 0) {
            mediaItem = mediaItems.get(position);
            this.position = position;

            //MediaPlayer
            if (mediaPlayer != null) {
                mediaPlayer.reset();//上一曲重置
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();
            //设置三个监听
            mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
            mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
            mediaPlayer.setOnErrorListener(new MyOnErrorListener());

            //设置播放地址
            try {
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (!isLoaded) {
            Toast.makeText(this, "没有加载完成", Toast.LENGTH_SHORT).show();
        }

    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }


    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            notifyChange(OPEN_COMPLETE);
            start();
        }
    }

    private void notifyChange(String action) {
        Intent intent = new Intent();
        intent.setAction(action);

        //发广播
        sendBroadcast(intent);
    }

    /**
     * 开始播放音频
     */
    void start() {
        mediaPlayer.start();

    }

    /**
     * 暂停
     */
    void pause() {
        mediaPlayer.pause();
    }

    /**
     * 得到歌曲的名称
     */
    String getAudioName() {
        if (mediaItem != null) {
            return mediaItem.getName();
        }
        return "";
    }

    /**
     * 得到歌曲演唱者的名字
     */
    String getArtistName() {
        if (mediaItem != null) {
            return mediaItem.getArtist();
        }
        return "";

    }

    /**
     * 得到歌曲的当前播放进度
     */
    int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 得到歌曲的当前总进度
     */
    int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 播放下一首歌曲
     */
    void next() {

    }

    /**
     * 播放上一首歌曲
     */
    void pre() {

    }

    /**
     * 得到播放模式
     */
    int getPlayMode() {
        return 0;
    }

    /**
     * 设置播放模式
     */
    void setPlayMode(int mode) {

    }


}

