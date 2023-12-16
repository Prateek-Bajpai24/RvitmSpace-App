package com.example.demoforproj;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import kotlin.Unit;

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.ViewHolder> {

    ArrayList<String> studlist;
    ArrayList<String> present=new ArrayList<>();
    ArrayList<String> absent=new ArrayList<>();
    ArrayList<String> finstatus=new ArrayList<>();
    ArrayList<String> statusofstud;
    String activity,monthtosend,datetosend;
    OnItemClick onItemClick;
    public  interface OnItemClick{
        void onitemclick(int position);
    }

    public EditAdapter(OnItemClick onItemClick,ArrayList<String> studlist,ArrayList<String> statusofstud,String activity) {
        this.studlist=studlist;
        this.statusofstud=statusofstud;
        this.activity=activity;
        this.onItemClick=onItemClick;
    }

    @NonNull
    @Override
    public EditAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_for_pdf, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditAdapter.ViewHolder holder, int position) {
        if(statusofstud.contains("modeedit")){
            statusofstud.remove("modeedit");
            for(int i=0;i<studlist.size();i++){
                if(statusofstud.get(i).equals("A"))
                    absent.add(studlist.get(i).split(" ")[0]);
                else if(statusofstud.get(i).equals("P"))
                    present.add(studlist.get(i).split(" ")[0]);
            }
        }
        if(present.contains(studlist.get(holder.getAdapterPosition()).split(" ")[0])){
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(),R.color.presentgreencon));
            holder.presstatus.setVisibility(View.VISIBLE);
            holder.absstatus.setVisibility(View.GONE);
        }
        else if(absent.contains(studlist.get(holder.getAdapterPosition()).split(" ")[0])){
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(),R.color.absentredcon));
            holder.presstatus.setVisibility(View.GONE);
            holder.absstatus.setVisibility(View.VISIBLE);
        }
        if(activity.equals("attsheet")){
            holder.pdflog.setVisibility(View.INVISIBLE);
            holder.delete.setVisibility(View.GONE);
            holder.title.setText(studlist.get(holder.getAdapterPosition()));
        }
        else {
            holder.delete.setVisibility(View.GONE);
            holder.pdflog.setImageResource(R.drawable.baseline_calendar_month_24);
            holder.title.setText(studlist.get(position));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.equals("attsheet")){
                    String[] studusnp = studlist.get(holder.getAdapterPosition()).split(" ");
                    if(present.contains(studusnp[0])){
                        absent.add(studusnp[0]);
                        present.remove(studusnp[0]);
                    }
                    else{
                        absent.remove(studusnp[0]);
                        present.add(studusnp[0]);
                    }
                    notifyDataSetChanged();
                }
                else{
                    datetosend=holder.title.getText().toString();
                    monthtosend=statusofstud.get(holder.getAdapterPosition());
                    Log.d("monthcheck",statusofstud.toString());
                    onItemClick.onitemclick(holder.getAdapterPosition());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        try {
            return studlist.size();
        }catch (Exception e){
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView pdflog,delete,deletedate;
        RelativeLayout presstatus,absstatus;
        TextView title,editbut;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            delete=itemView.findViewById(R.id.delete1);
            presstatus=itemView.findViewById(R.id.presstatus);
            absstatus=itemView.findViewById(R.id.absstatus);
            title = itemView.findViewById(R.id.title);
            pdflog=itemView.findViewById(R.id.pdflog);
            cardView=itemView.findViewById(R.id.cardpdf);
            deletedate=itemView.findViewById(R.id.deletedate);
            editbut=itemView.findViewById(R.id.editbut);
        }
    }
    public ArrayList<String> getStudlist(){
        finstatus = present;
        finstatus.add("checkover");
        finstatus.addAll(absent);
        return finstatus;
    }
    public ArrayList<String> getDatefromrecycler(){
        finstatus=new ArrayList<>();
        finstatus.add(datetosend);
        finstatus.add(monthtosend);
        return finstatus;
    }

}