package com.akatsuki.bluff;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class HostingActivity extends Activity {

    public void toGameActivity(View view){
        Intent intent = new Intent(this , GameHostActivity.class);
        intent.putExtra("gameName", gameIp.getText().toString());
        intent.putExtra("inGameName",inGameName.getText().toString());
        rb = (RadioButton)findViewById(rg.getCheckedRadioButtonId());
        intent.putExtra("playersNumber", Integer.parseInt(rb.getText().toString()));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosting);
        gameIp = (TextView) findViewById(R.id.game_ip);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        inGameName = (EditText) findViewById(R.id.in_game_name);

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());

        gameIp.setText(ip);
    }

    TextView gameIp;
    EditText inGameName;
    RadioGroup rg;
    RadioButton rb;
}