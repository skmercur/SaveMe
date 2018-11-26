package com.hackathon.saveme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView text = (TextView)findViewById(R.id.textView);
        Button btn = (Button)findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (i){

                    case 0:{
                        text.setText("Hello");
                        i++;
                        i = (i < 3) ? i: 0;
                        break;
                    }
                    case 1:{
                        text.setText("Champions");
                        i++;
                        i = (i < 3) ? i: 0;
                        break;
                    }
                    case 2:{
                        text.setText("Never look back");
                        i++;
                        i = (i < 3) ? i: 0;
                        break;
                    }
                }
            }
        });
    }
}
