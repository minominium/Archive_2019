package com.example.as_20190422;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.as_20190422.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private ArrayList<PhoneInfo> phones = new ArrayList<PhoneInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView hListView = (ListView)findViewById(R.id.list);

        for(int i = 0; i < 20; i++)
        {
            phones.add(new PhoneInfo(R.drawable.girl1, "태연"));
            phones.add(new PhoneInfo(R.drawable.girl2, "제시카"));
            phones.add(new PhoneInfo(R.drawable.girl3, "써니"));
            phones.add(new PhoneInfo(R.drawable.girl4, "티파니"));
            phones.add(new PhoneInfo(R.drawable.girl5, "효연"));
            phones.add(new PhoneInfo(R.drawable.girl6, "유리"));
            phones.add(new PhoneInfo(R.drawable.girl7, "수영"));
            phones.add(new PhoneInfo(R.drawable.girl8, "윤아"));
            phones.add(new PhoneInfo(R.drawable.girl9, "서현"));
        }

        hListView.setAdapter(new PhoneListAdapter(this, phones));
    }

    private class PhoneInfo
    {
        int icon;
        String name;

        PhoneInfo(int icon, String name)
        {
            this.icon = icon;
            this.name = name;
        }
    }

    private class PhoneListAdapter extends ArrayAdapter<PhoneInfo>
    {
        private final LayoutInflater inflater;

        PhoneListAdapter(Context context, ArrayList<PhoneInfo> list)
        {
            super(context, 0, list);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if(convertView == null)
            {
                convertView = inflater.inflate(R.layout.item, null);
                holder = new ViewHolder();
                holder.icon = (ImageView)convertView.findViewById(R.id.icon);
                holder.name = (TextView)convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }

            final PhoneInfo pi = getItem(position);
            holder.icon.setImageResource(pi.icon);
            holder.name.setText(pi.name);

            return convertView;
        }

        private class ViewHolder
        {
            ImageView icon;
            TextView name;
        }
    }
}
