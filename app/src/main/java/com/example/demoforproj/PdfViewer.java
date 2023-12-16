package com.example.demoforproj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.Loader;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.ybq.android.spinkit.SpinKitView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PdfViewer extends AppCompatActivity {
    PDFView pdfView;
    TextView load;
    TextView textView;
    LinearLayout relativeLayout;
    Button bb;
    SwitchCompat switchCompat,switchCompat1;
    int view=0,mode=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window =getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        textView = findViewById(R.id.proftext);
        bb=findViewById(R.id.bb);
        switchCompat=findViewById(R.id.sc);
        switchCompat1=findViewById(R.id.sc1);
        Intent intent = getIntent();
        String uri = intent.getStringExtra("uri");
        pdfView=findViewById(R.id.pdfview);
        load=findViewById(R.id.load);
        new RetrievePdf(view,mode).execute(uri);
        ContentResolver contentResolver = textView.getContext().getContentResolver();
        textView.setText(getFileName(Uri.parse(uri),contentResolver));
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                relativeLayout.setVisibility(View.VISIBLE);
                if(isChecked){
                    mode=1;
                    new RetrievePdf(view,mode).execute(uri);
                }
                else{
                    mode=0;
                    new RetrievePdf(view,mode).execute(uri);
                }

            }
        });
        switchCompat1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                relativeLayout.setVisibility(View.VISIBLE);
                if(isChecked){
                    view=1;
                    new RetrievePdf(view,mode).execute(uri);
                }
                else {
                    view=0;
                    new RetrievePdf(view,mode).execute(uri);
                }
            }
        });


    }
    class RetrievePdf extends AsyncTask<String,Void, InputStream>{
        int v,m;
        protected RetrievePdf(int v,int m){
            this.v=v;
            this.m=m;
        }
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream=null;
            try{
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if(urlConnection.getResponseCode()==200){
                    inputStream=new BufferedInputStream(urlConnection.getInputStream());
                }
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }
        protected void onPostExecute(InputStream inputStream){
            relativeLayout=findViewById(R.id.linv);
            if(v==0 && m==0){
                load.setTextColor(getColor(R.color.black));
                pdfView.fromStream(inputStream)
                        .defaultPage(0)
                        .scrollHandle(new DefaultScrollHandle(PdfViewer.this))
                        .spacing(10)
                        .nightMode(false)
                        .onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                relativeLayout.setVisibility(View.GONE);
                            }
                        })
                        .load();
            }
            else if(v==1 && m==0) {
                load.setTextColor(getColor(R.color.black));
                pdfView.fromStream(inputStream)
                        .defaultPage(0)
                        .scrollHandle(new DefaultScrollHandle(PdfViewer.this))
                        .swipeHorizontal(true)
                        .spacing(10)
                        .nightMode(false)
                        .onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                relativeLayout.setVisibility(View.GONE);
                            }
                        })
                        .load();
            }
            else if(v==1 && m==1){
                load.setTextColor(getColor(R.color.white));
                pdfView.fromStream(inputStream)
                        .defaultPage(0)
                        .scrollHandle(new DefaultScrollHandle(PdfViewer.this))
                        .swipeHorizontal(true)
                        .spacing(10)
                        .nightMode(true)
                        .onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                relativeLayout.setVisibility(View.GONE);
                            }
                        })
                        .load();
            }

            else if(v==0 && m==1){
                load.setTextColor(getColor(R.color.white));
                    pdfView.fromStream(inputStream)
                            .defaultPage(0)
                            .scrollHandle(new DefaultScrollHandle(PdfViewer.this))
                            .spacing(10)
                            .nightMode(true)
                            .onLoad(new OnLoadCompleteListener() {
                                @Override
                                public void loadComplete(int nbPages) {
                                    relativeLayout.setVisibility(View.GONE);
                                }
                            })
                            .load();
            }
        }

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