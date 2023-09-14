package com.example.as_20190424;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "Example";
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                button.setText(R.string.clicked);
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        Log.v(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Log.v(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStart()
    {
        Log.v(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        Log.v(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart()
    {
        Log.v(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.v(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        String text = button.getText().toString();
        outState.putString("ButtonText", text);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.v(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        button.setText(savedInstanceState.getString("ButtonText"));
    }

    @Override
    public void onBackPressed()
    {
        Log.v(TAG, "onBackPressed");
        super.onBackPressed();
    }

    @Override
    public void finish()
    {
        Log.v(TAG, "finish");
        super.finish();
    }
}
