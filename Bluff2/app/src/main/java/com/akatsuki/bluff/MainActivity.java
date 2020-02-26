package com.akatsuki.bluff;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    public void toHostingActivity(View view){
        Intent intent = new Intent(this , HostingActivity.class);
        startActivity(intent);
    }

    public  void toJoinActivity(View view){
        Intent intent = new Intent(this , JoiningActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}