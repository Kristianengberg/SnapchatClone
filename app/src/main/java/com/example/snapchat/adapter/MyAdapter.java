package com.example.snapchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.snapchat.R;
import com.example.snapchat.model.Snap;

import java.util.List;

public class MyAdapter extends BaseAdapter {

    private List<Snap> snaps; // will hold data
    private LayoutInflater layoutInflater; // can "inflate" layout files

    public MyAdapter(List<Snap> snaps, Context context) {
        this.snaps = snaps;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return snaps.size();
    }

    @Override
    public Object getItem(int i) {
        return snaps.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // make layout .xml file first...
        if (view == null) {
            view = layoutInflater.inflate(R.layout.myrow, null);
        }
        //LinearLayout linearLayout = (LinearLayout)view;
        TextView textView = view.findViewById(R.id.textView1);
        if (textView != null) {
            textView.setText(snaps.get(i).getId()); // later I will connect to the items list
        }
        return textView;
    }


}
