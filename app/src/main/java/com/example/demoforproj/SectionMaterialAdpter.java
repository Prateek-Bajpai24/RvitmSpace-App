package com.example.demoforproj;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.content.ContentResolver;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SectionMaterialAdpter extends RecyclerView.Adapter<SectionMaterialAdpter.ViewHolder>{

    ArrayList<String> name;
    ArrayList<Uri> link;
    Context context;
    ArrayList<Uri> bitrec;
//    ArrayList<byte[]> tempbit;
//    ArrayList<Bitmap> loader=new ArrayList<>();
//    public  interface OnItemClick{
//        void oniteclicksection(int position, String datesend,String sectiontosend);
//    }


    public SectionMaterialAdpter(ArrayList<String> name, ArrayList<Uri> link,ArrayList<Uri> bitrec) {
        this.name=name;
        this.link=link;
        this.bitrec=bitrec;
//        this.tempbit=tempbit;
    }

    @NonNull
    @Override
    public SectionMaterialAdpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.displpartsofmaterialsdisplay, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionMaterialAdpter.ViewHolder holder, int position) {
        context=holder.pdfim.getContext();
        holder.textname.setText(name.get(holder.getAdapterPosition()));
//        new RetrievePdfIm(holder).execute(link.get(holder.getAdapterPosition()).toString());
//        if(!loader.isEmpty())
//            Glide.with(holder.pdfim.getContext())
//                .load(loader.get(holder.getAdapterPosition()))
//                .fitCenter()
//                .into(holder.pdfim);
//        if(bitrec.isEmpty()){
//            holder.viewim.setImageBitmap(BitmapFactory.decodeByteArray(tempbit.get(holder.getAdapterPosition()),0,tempbit.get(holder.getAdapterPosition()).length));
//        }
//        else{
//            Glide.with(holder.pdfim.getContext())
//                    .load(bitrec.get(holder.getAdapterPosition()))
//                    .fitCenter()
//                    .into(holder.pdfim);
//        }
        holder.pdfim.setImageURI(bitrec.get(holder.getAdapterPosition()));
        holder.viewim.setOnClickListener(vviewim->{
            Intent intent = new Intent(holder.pdfim.getContext(),PdfViewer.class);
            intent.putExtra("uri",link.get(holder.getAdapterPosition()).toString());
            holder.pdfim.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        try {
            return name.size();
        }catch (Exception e){
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textname;
        ImageView pdfim,viewim;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            textname=itemView.findViewById(R.id.te);
            pdfim=itemView.findViewById(R.id.pdfim);
            viewim=itemView.findViewById(R.id.viewim);
        }
    }
//    class RetrievePdfIm extends AsyncTask<String,Void,InputStream>{
//        private final ViewHolder viewHolder;
//
//        RetrievePdfIm(ViewHolder viewHolder) {
//            this.viewHolder = viewHolder;
//        }
//
//        @Override
//        protected InputStream doInBackground(String... strings) {
//            InputStream inputStream=null;
//            try{
//                URL url = new URL(strings[0]);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                if(urlConnection.getResponseCode()==200){
//                    inputStream=new BufferedInputStream(urlConnection.getInputStream());
//                }
//            }catch (IOException e){
//                e.printStackTrace();
//                return null;
//            }
//            return inputStream;
//        }
//        protected void onPostExecute(InputStream inputStream) {
//            pdfView.fromStream(inputStream)
//                    .defaultPage(0)
//                    .onLoad(new OnLoadCompleteListener() {
//                        @Override
//                        public void loadComplete(int nbPages) {
//                            pdfView.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Bitmap firstPageBitmap = pdfView.getDrawingCache();
//                                    // Now you have the first page as a Bitmap (firstPageBitmap)
//                                    // You can use it as needed
//                                    loader.add(firstPageBitmap);
//                                    notifyDataSetChanged();
//                                }
//                            }, 3000); // Adjust the delay as needed
//                            // Capture the first page immediately after loading
//                            // Now you have the first page as a Bitmap (firstPageBitmap)
//                            // You can use it as needed
//                        }
//                    })
//                    .load();
//        }
//    }


}