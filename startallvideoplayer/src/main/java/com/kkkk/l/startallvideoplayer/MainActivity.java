package com.kkkk.l.startallvideoplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public  void startAllVideo(View view){
        Intent intent =new Intent();
        intent.setDataAndType(Uri.parse("http://10.0.2.2:8080/rmvb.rmvbd"),"video/*");
        startActivity(intent);
    }
}
