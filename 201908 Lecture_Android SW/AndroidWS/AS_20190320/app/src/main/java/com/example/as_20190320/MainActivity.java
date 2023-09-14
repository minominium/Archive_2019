package com.example.as_20190320;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private ImageView hImageView;
    private TextView hTextView;
    private Button hButton0;
    private Button hButton1;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hImageView = (ImageView)findViewById(R.id.img);
        hTextView = (TextView)findViewById(R.id.text);
        hButton0 = (Button)findViewById(R.id.buttonPrev);
        hButton1 = (Button)findViewById(R.id.buttonNext);

        hButton0.setOnClickListener(this);
        hButton1.setOnClickListener(this);
        page = 1;
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.buttonPrev:
                switch (page)
                {
                    case 1:
                        break;
                    case 2:
                        page--;
                        hTextView.setText(R.string.page1);
                        hImageView.setImageResource(R.drawable.p01);
                        break;
                    case 3:
                        page--;
                        hTextView.setText(R.string.page2);
                        hImageView.setImageResource(R.drawable.p02);
                        break;
                    case 4:
                        page--;
                        hTextView.setText(R.string.page3);
                        hImageView.setImageResource(R.drawable.p03);
                        break;
                    case 5:
                        page--;
                        hTextView.setText(R.string.page4);
                        hImageView.setImageResource(R.drawable.p04);
                        break;
                }
                break;
            case R.id.buttonNext:
                switch (page)
                {
                    case 1:
                        page++;
                        hTextView.setText(R.string.page2);
                        hImageView.setImageResource(R.drawable.p02);
                        break;
                    case 2:
                        page++;
                        hTextView.setText(R.string.page3);
                        hImageView.setImageResource(R.drawable.p03);
                        break;
                    case 3:
                        page++;
                        hTextView.setText(R.string.page4);
                        hImageView.setImageResource(R.drawable.p04);
                        break;
                    case 4:
                        page++;
                        hTextView.setText(R.string.page5);
                        hImageView.setImageResource(R.drawable.p05);
                        break;
                    case 5:
                        break;
                }
                break;
        }
    }
}