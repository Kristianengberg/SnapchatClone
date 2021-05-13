package com.example.snapchat;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.snapchat.adapter.repo.Repo;
import com.example.snapchat.model.Snap;

public class DetailActivity extends AppCompatActivity implements TaskListener {


    private Snap currentSnap;
    ImageView snapImageView;
    Bitmap dataBaseImageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String snapID = getIntent().getStringExtra("snapid");
        currentSnap = (Repo.r().getSnapWithID(snapID));
        snapImageView = findViewById(R.id.snapView);
        Repo.r().downloadBitmap(snapID, this);

    }

    @Override
    public void receive(byte[] bytes) {
        dataBaseImageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        snapImageView.setImageBitmap(dataBaseImageBitmap);
    }

    // Kind of cool so when you press the back button it calls the delete function just like snapchat but pretty simple
    @Override
    protected void onPause() {
        super.onPause();
        Repo.r().deleteImageFromServer(currentSnap.getId());
        finish();
    }
}