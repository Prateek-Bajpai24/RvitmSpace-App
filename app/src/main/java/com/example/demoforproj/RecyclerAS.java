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
import android.net.Uri;
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class RecyclerAS extends RecyclerView.Adapter<RecyclerAS.ViewHolder> {

    ArrayList<String> studlist;
    ArrayList<String> present=new ArrayList<>();
    ArrayList<String> absent=new ArrayList<>();
    ArrayList<String> finstatus=new ArrayList<>();
    ArrayList<String> statusofstud;
    OnItemClick onItemClick;
    String activity;
    public  interface OnItemClick{
        void oniteMclick(int position, String datesend);
    }


    public RecyclerAS(ArrayList<String> studlist,String activity,ArrayList<String> statusofstud,OnItemClick onItemClick) {
        this.studlist=studlist;
        this.activity=activity;
        this.statusofstud=statusofstud;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public RecyclerAS.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_for_pdf, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAS.ViewHolder holder, int position) {
        holder.delete.setVisibility(View.GONE);
        holder.pdflog.setImageResource(R.drawable.baseline_calendar_month_24);
        holder.title.setText(studlist.get(position));
        String[] actver=activity.split("_");
        if(actver[0].equals("AllClass")){
            holder.deletedate.setVisibility(View.VISIBLE);
            holder.editbut.setVisibility(View.VISIBLE);
        }
        if(!statusofstud.isEmpty())
            Log.d("status",statusofstud.get(0));
        if(statusofstud.contains("allpres")){
            absent=new ArrayList<>();
            present=new ArrayList<>();
            statusofstud.remove("allpres");
            for(int i=0;i<studlist.size();i++)
                present.add(studlist.get(i).split(" ")[0]);
        }
        else if(statusofstud.contains("allabs")){
            present=new ArrayList<>();
            absent=new ArrayList<>();
            statusofstud.remove("allabs");
            for(int i=0;i<studlist.size();i++)
                absent.add(studlist.get(i).split(" ")[0]);
        }
        if(present.contains(studlist.get(holder.getAdapterPosition()).split(" ")[0])) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.presentgreencon));
            if (holder.pdflog.getVisibility() == View.VISIBLE)
                holder.pdflog.setVisibility(View.INVISIBLE);
            if (holder.absstatus.getVisibility() == View.VISIBLE)
                holder.absstatus.setVisibility(View.GONE);
            holder.presstatus.setVisibility(View.VISIBLE);
        }

        else if(absent.contains(studlist.get(holder.getAdapterPosition()).split(" ")[0])){
            if(holder.pdflog.getVisibility()==View.VISIBLE)
                holder.pdflog.setVisibility(View.INVISIBLE);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(),R.color.absentredcon));
            holder.presstatus.setVisibility(View.GONE);
            holder.absstatus.setVisibility(View.VISIBLE);
            }
        else {
            if(holder.pdflog.getVisibility()==View.INVISIBLE)
                holder.pdflog.setVisibility(View.VISIBLE);
            if(holder.absstatus.getVisibility()==View.VISIBLE)
                holder.absstatus.setVisibility(View.GONE);
            if(holder.presstatus.getVisibility()==View.VISIBLE)
                holder.presstatus.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(),R.color.white));
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.equals("AttendanceSheet")){
                    String[] studusnp = studlist.get(holder.getAdapterPosition()).split(" ");
                    if(present.contains(studusnp[0])){
                        absent.add(studusnp[0]);
                        present.remove(studusnp[0]);
                    }
                    else if(absent.contains(studusnp[0])){
                        absent.remove(studusnp[0]);
                        present.add(studusnp[0]);
                    }
                    else
                        present.add(studusnp[0]);
                    notifyDataSetChanged();
                }
                else
                    onItemClick.oniteMclick(holder.getAdapterPosition(),"buttonactive");

            }
        });
        holder.editbut.setOnClickListener(v -> {
            Intent intent=new Intent(holder.itemView.getContext(),AttendanceSheet.class);
            intent.putExtra("subjcode",actver[1]);
            intent.putExtra("date",holder.title.getText());
            intent.putExtra("mode","edit");
            holder.itemView.getContext().startActivity(intent);
        });
        holder.deletedate.setOnClickListener(vd->{
            Dialog dialog = new Dialog(holder.itemView.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.confirmdel);
            Button yes = dialog.findViewById(R.id.yes);
            Button no = dialog.findViewById(R.id.no);
            yes.setOnClickListener(vy->{
                onItemClick.oniteMclick(holder.getAdapterPosition(),holder.title.getText().toString());
                dialog.dismiss();
            });
            no.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);

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

}