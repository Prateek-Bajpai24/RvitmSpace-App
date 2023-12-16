package com.example.demoforproj;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.content.ContentResolver;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter1 extends RecyclerView.Adapter<RecyclerAdapter1.ViewHolder> {

    private ArrayList<Uri> uriArrayList;
    ArrayList<String> studlist;
    private TextView pdft;
    String activity;

    public RecyclerAdapter1(ArrayList<Uri> uriArrayList, TextView pdft, String activity, ArrayList<String> studlist) {
        this.uriArrayList = uriArrayList;
        this.pdft=pdft;
        this.activity=activity;
        this.studlist=studlist;
    }

    @NonNull
    @Override
    public RecyclerAdapter1.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_for_pdf, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter1.ViewHolder holder, int position) {
        Uri uri = uriArrayList.get(position);
        ContentResolver contentResolver = holder.itemView.getContext().getContentResolver();
        String[] test = activity.split(",");
        if(activity=="DisplayNotice"){
            String fileName = getFileName(uri, contentResolver);
            holder.title.setText(fileName);
            holder.delete.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.title.getLayoutParams();
            layoutParams.leftMargin = 30; // Adjust this value to move the TextView right
            holder.title.setLayoutParams(layoutParams);
        }
        else{
            String fileName = getFileName(uri, contentResolver);
            holder.title.setText(fileName);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.title.getLayoutParams();
            layoutParams.width = 580; // Adjust this value to move the TextView right
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                uriArrayList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, getItemCount());
                if(uriArrayList.isEmpty())
                    pdft.setVisibility(View.GONE);
                Toast.makeText(holder.delete.getContext(), "Pdf removed", Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                if(activity=="DisplayNotice"){
                    Intent intent = new Intent(context, PdfViewer.class);
                    // Add data as extras to the Intent
                    intent.putExtra("uri",uri.toString());

                    // Start the new activity
                    context.startActivity(intent);
                }
                else{
                    Intent intent = new Intent(context, PdfViewer2.class);
                    // Add data as extras to the Intent
                    intent.putExtra("uri",uri.toString());

                    // Start the new activity
                    context.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        try {
            return uriArrayList.size();
        }catch (Exception e){
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView delete,pdflog;
        TextView title,pt,at;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            title = itemView.findViewById(R.id.title);
            delete = itemView.findViewById(R.id.delete1);
            pdflog=itemView.findViewById(R.id.pdflog);
            pt=itemView.findViewById(R.id.prestext);
            at=itemView.findViewById(R.id.abstext);
        }
    }
    private String getFileName(Uri uri,ContentResolver contentResolver) {
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