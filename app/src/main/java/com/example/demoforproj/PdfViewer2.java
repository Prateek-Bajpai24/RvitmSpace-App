package com.example.demoforproj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PdfViewer2 extends AppCompatActivity {
    PDFView pdfView;
    TextView textView;
    Button bb;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window =getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        textView = findViewById(R.id.proftext);
        bb=findViewById(R.id.bb);
        linearLayout=findViewById(R.id.linv);
        Intent intent = getIntent();
        String uri = intent.getStringExtra("uri");
        pdfView=findViewById(R.id.pdfview);
        pdfView.fromUri(Uri.parse(uri))
                .defaultPage(0)
                .scrollHandle(new DefaultScrollHandle(PdfViewer2.this))
                .spacing(10)
                .nightMode(false)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        linearLayout.setVisibility(View.GONE);
                    }
                })
                .load();
        ContentResolver contentResolver = textView.getContext().getContentResolver();
        textView.setText(getFileName(Uri.parse(uri),contentResolver));
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