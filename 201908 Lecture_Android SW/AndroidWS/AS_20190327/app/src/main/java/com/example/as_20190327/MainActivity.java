package com.example.as_20190327;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity
{
    private View hView;
    private View hView1;
    private ImageButton hImageButton;
    private ImageButton hImageButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hView = findViewById(R.id.background);
        hView1 = findViewById(R.id.background1);
        hImageButton = (ImageButton)findViewById(R.id.button);
        hImageButton1 = (ImageButton)findViewById(R.id.button1);

        hImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hView.setBackgroundResource(R.drawable.big);
            }
        });
        hImageButton1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hView1.setBackgroundResource(R.drawable.big);
            }
        });
    }
}
