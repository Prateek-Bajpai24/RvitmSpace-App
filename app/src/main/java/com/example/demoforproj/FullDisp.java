package com.example.demoforproj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;

public class FullDisp extends AppCompatActivity {
    TextView textView;
    Button bb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_disp);
        bb=findViewById(R.id.bb);
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.newpurp));
        textView = findViewById(R.id.proftext);
        Intent intent = getIntent();
        ArrayList<String> data1 = intent.getStringArrayListExtra("uri");
        int pos = intent.getIntExtra("position", 0);
        ViewPager2 viewPager = findViewById(R.id.fullScreenImageView);

        // Preload one page ahead
        viewPager.setOffscreenPageLimit(1);

        ArrayList<Uri> uriArrayList = new ArrayList<>();
        for (int i = 0; i < data1.size(); i++) {
            uriArrayList.add(Uri.parse(data1.get(i)));
        }

        RecyclerForImage adapter = new RecyclerForImage(uriArrayList);

        // Set the adapter to your ViewPager2
        viewPager.setAdapter(adapter);

        // Load the image at the specified position upfront
        ContentResolver contentResolver = textView.getContext().getContentResolver();
        viewPager.setCurrentItem(pos, false);
        textView.setText(getFileName(Uri.parse(data1.get(pos)), contentResolver));

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // This method is called when the page (image) changes
                // Compare the new position with the last position
                textView.setText(getFileName(Uri.parse(data1.get(position)), contentResolver));
            }
        });

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private String getFileName(Uri uri, ContentResolver contentResolver) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                result = cursor.getString(columnIndex);
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}