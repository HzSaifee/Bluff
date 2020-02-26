package com.akatsuki.bluff;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class JoiningActivity extends Activity {

    public void joinMatch(View view) {
        ipAddress = ipEditText.getText() + "";
        name = nameEditText.getText() + "";

        Intent intent = new Intent(getApplicationContext(), GameClientActivity.class);
        intent.putExtra("IP", ipAddress);
        intent.putExtra("name", name);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joining);
        ipEditText = (EditText) findViewById(R.id.editText2);
        nameEditText = (EditText) findViewById(R.id.editText3);
    }

    EditText ipEditText;
    EditText nameEditText;
    String ipAddress;
    String name;
}