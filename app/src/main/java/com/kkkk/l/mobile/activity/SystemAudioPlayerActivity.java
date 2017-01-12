package com.kkkk.l.mobile.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kkkk.l.mobile.IMusicPlayerService;
import com.kkkk.l.mobile.R;
import com.kkkk.l.mobile.service.MusicPlayerService;
import com.kkkk.l.mobile.utils.Utils;

public class SystemAudioPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnSwichLyric;
    private int position;
    private MyReceiver receiver;
    private static final int PROGRESS = 1;
    private Utils utils;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-01-12 11:41:04 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_audio_player);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnAudioPlaymode = (Button) findViewById(R.id.btn_audio_playmode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnSwichLyric = (Button) findViewById(R.id.btn_swich_lyric);
        //动画
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable drawable = (AnimationDrawable) ivIcon.getBackground();
        drawable.start();

        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnSwichLyric.setOnClickListener(this);
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnAudioPlaymode) {
            // Handle clicks for btnAudioPlaymode
        } else if (v == btnAudioPre) {
            // Handle clicks for btnAudioPre
        } else if (v == btnAudioStartPause) {
            // Handle clicks for btnAudioStartPause

            try {
                if(service.isPlaying()){
                    //暂停
                    service.pause();
                    //按钮状态-设置播放
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                }else{
                    //播放
                    service.start();
                    //按钮状态-设置暂停
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }


        } else if (v == btnAudioNext) {
            // Handle clicks for btnAudioNext
        } else if (v == btnSwichLyric) {
            // Handle clicks for btnSwichLyric
        }
    }

    private IMusicPlayerService service;

    private ServiceConnection conn = new ServiceConnection() {
        /**
         * 当连接服务成功后回调
         * @param name
         * @param iBdinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBdinder) {
            service = IMusicPlayerService.Stub.asInterface(iBdinder);

            if (service != null) {
                try {
                    //开始播放
                    service.openAudio(position);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 当断开的时候回调
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:

                    try {
                        int currentPosition = service.getCurrentPosition();
                        tvTime.setText(utils.stringForTime(currentPosition)+"/"+utils.stringForTime(service.getDuration()));


                        //SeekBar进度更新
                        seekbarAudio.setProgress(currentPosition);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS,1000);

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        //绑定方式启动服务
        startAndBindServide();


    }

    /**
     * 接收广播
     */
    private void initData() {
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPEN_COMPLETE);
        registerReceiver(receiver,intentFilter);

        utils = new Utils();

    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(MusicPlayerService.OPEN_COMPLETE.equals(intent.getAction())){

                showViewData();
            }

        }
    }

    /**
     * 显示视图的数据
     */
    private void showViewData() {
        try {
            tvArtist.setText(service.getArtistName());
            tvName.setText(service.getAudioName());

            //得到总时长
            int duration = service.getDuration();
            seekbarAudio.setMax(duration);

            //更新进度
            handler.sendEmptyMessage(PROGRESS);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {

        if(conn != null){
            unbindService(conn);
            conn = null;
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void startAndBindServide() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        //绑定服务
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        //启动服务
        startService(intent);//防止服务多次创建
    }

    private void getData() {
        /**
         * 得到播放位置
         */
        position = getIntent().getIntExtra("position", 0);
    }
}
