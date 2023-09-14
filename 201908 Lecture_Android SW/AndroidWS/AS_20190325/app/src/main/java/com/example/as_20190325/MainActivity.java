package com.example.as_20190325;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity
{
    private View hView;
    private ImageButton hImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hView = findViewById(R.id.background);
        hImageButton = (ImageButton)findViewById(R.id.button);

        hImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hView.setBackgroundResource(R.drawable.big);
            }
        });
    }
}
