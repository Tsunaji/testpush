package com.digitopolis.testpush;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by User on 11/2/2015.
 */
public class OpenByPush extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_by_push);

        String text = getIntent().getExtras().getString("parseValue");

        TextView textView = (TextView) findViewById(R.id.open_by_push_textView);
        textView.setText(text);
    }
}
