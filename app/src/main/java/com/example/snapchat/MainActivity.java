package com.example.snapchat;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.snapchat.adapter.MyAdapter;
import com.example.snapchat.adapter.repo.Repo;
import com.example.snapchat.model.Snap;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskListener, Updatable {


    static final int REQUEST_IMAGE_CAPTURE = 2;

    List<Snap> snaps = new ArrayList<>();
    ImageView imageView;
    Bitmap currentWorkingBitmap;
    Bitmap dataBaseImageBitmap;
    ListView listView;
    MyAdapter myAdapter;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupListView();
        Repo.r().setup(this, snaps);
        editText = findViewById(R.id.addTextToSnap);
        imageView = findViewById(R.id.imageView);
    }

    public void GoToSnapButton(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    public void addTextToSnapButton(View view) {
        currentWorkingBitmap = drawTextToBitmap(currentWorkingBitmap, editText.getText().toString());
    }

    public void saveToAppFolder(View view) {
        Repo.r().addSnap(currentWorkingBitmap);
        myAdapter.notifyDataSetChanged(); // not sure if this is needed but it works and i dont want to mess anything up :)
    }

    public Bitmap drawTextToBitmap(Bitmap image, String gText) {
        Bitmap.Config bitmapConfig = image.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        image = image.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);// new antialised Paint
        paint.setColor(Color.rgb(161, 161, 161));
        paint.setTextSize((int) (20)); // text size in pixels
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // text shadow
        canvas.drawText(gText, 10, 100, paint);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == -1) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                currentWorkingBitmap = bitmap;
            }
        }
    }

    private void setupListView() {
        listView = findViewById(R.id.snapListView);
        myAdapter = new MyAdapter(snaps, this);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            System.out.println("click on row: " + position);
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("snapid", snaps.get(position).getId());
            startActivity(intent);
        });
    }

    @Override
    public void receive(byte[] bytes) {
        dataBaseImageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(dataBaseImageBitmap);

    }

    @Override
    public void update(Object o) {
        myAdapter.notifyDataSetChanged();
    }
}