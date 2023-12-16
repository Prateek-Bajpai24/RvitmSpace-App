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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;
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

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    ArrayList<String> sectionname;
    OnItemClick onItemClick;
    ArrayList<Integer> expcheck=new ArrayList<>();
    int expcheckchecker=0;
    ArrayList<ArrayList<String>> typeel;
    ArrayList<ArrayList<Uri>> ellink;
    SectionMaterialAdpter sectionMaterialAdpter;
    ArrayList<ArrayList<Uri>> bitrec;
    String foruser,stringr,editfield,subjcode;
    ArrayList<ArrayList<Uri>> bitinrec = new ArrayList<>();
    int initial = 0;
    public  interface OnItemClick{
        void oniteclicksection(int position, String datesend,String sectiontosend);
    }


    public SectionAdapter(ArrayList<String> sectionname,OnItemClick onItemClick,ArrayList<ArrayList<String>> typeel,ArrayList<ArrayList<Uri>> ellink,ArrayList<ArrayList<Uri>> bitrec) {
        this.typeel=typeel;
        this.ellink=ellink;
        this.sectionname=sectionname;
        this.onItemClick=onItemClick;
        this.bitrec=bitrec;
//        this.tempbit=tempbit;
    }

    @NonNull
    @Override
    public SectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.materialsdisplay, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionAdapter.ViewHolder holder, int position) {
        if(initial==0 && !typeel.isEmpty()){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            initial=1;
            int i;
            for(i=0;i<typeel.size();i++){
                ArrayList<String> checkref= new ArrayList<>();
                checkref=typeel.get(i);
                if(checkref.contains("uploaded")){
                    foruser=checkref.get(2);
                    stringr=checkref.get(3);
                    editfield=checkref.get(4);
                    subjcode=checkref.get(5);
                    break;
                }
            }
            final int ii=i;
            try {
                if((!foruser.isEmpty())&&(!stringr.isEmpty())&&(!editfield.isEmpty())){
                    database.getReference().child("Classroom").child(foruser).child(subjcode).child("Materials").child(stringr).child(editfield).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<Uri> tempuri = bitrec.get(ii);
                            tempuri.add(Uri.parse(snapshot.getValue(String.class)));
                            bitrec.add(tempuri);
                            Log.d("finbti",bitrec.toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }catch (Exception e){
                //
            }
        }

        if(typeel.get(holder.getAdapterPosition()).contains("uploaded")){
            holder.notext.setVisibility(View.GONE);
            holder.putmaterial.setVisibility(View.VISIBLE);
            sectionMaterialAdpter=new SectionMaterialAdpter(typeel.get(holder.getAdapterPosition()),ellink.get(holder.getAdapterPosition()),bitrec.get(holder.getAdapterPosition()));
            holder.putmaterial.setLayoutManager(new LinearLayoutManager(holder.putmaterial.getContext()));
            typeel.get(holder.getAdapterPosition()).remove("uploaded");
            typeel.get(holder.getAdapterPosition()).remove(foruser);
            typeel.get(holder.getAdapterPosition()).remove(stringr);
            typeel.get(holder.getAdapterPosition()).remove(subjcode);
            typeel.get(holder.getAdapterPosition()).remove(editfield);
            holder.putmaterial.setAdapter(sectionMaterialAdpter);

        }
        else if(typeel.get(holder.getAdapterPosition()).isEmpty()){
            holder.notext.setVisibility(View.VISIBLE);
            holder.putmaterial.setVisibility(View.GONE);
        }
        else {
            holder.notext.setVisibility(View.GONE);
            holder.putmaterial.setVisibility(View.VISIBLE);
            sectionMaterialAdpter=new SectionMaterialAdpter(typeel.get(holder.getAdapterPosition()),ellink.get(holder.getAdapterPosition()),bitrec.get(holder.getAdapterPosition()));
            holder.putmaterial.setLayoutManager(new LinearLayoutManager(holder.putmaterial.getContext()));
            holder.putmaterial.setAdapter(sectionMaterialAdpter);


        }
//        seccounter+=1;
//        if(seccounter==sectionname.size()){
//            seccounter=0;
////            holder.putmaterial.setAdapter(sectionMaterialAdpter);
//        }
        holder.secname.setText(sectionname.get(holder.getAdapterPosition()));
        if(sectionname.get(holder.getAdapterPosition()).equals("Textbook")||sectionname.get(holder.getAdapterPosition()).equals("CiePyq")||sectionname.get(holder.getAdapterPosition()).equals("UniversityPyq"))
            holder.delsec.setVisibility(View.GONE);
        if(expcheckchecker==0){
            for(int i=0;i<sectionname.size();i++)
                expcheck.add(i,0);
            expcheckchecker=1;
        }
//        if(typeuser.equals("student"))
//            holder.delsec.setVisibility(View.GONE);
//        holder.delsec.setOnClickListener(vdel->{
//            onItemClick.oniteclicksection(holder.getAdapterPosition(),sectionname.get(holder.getAdapterPosition()));
//        });
        holder.expless.setOnClickListener(vexp->{
            if(expcheck.get(holder.getAdapterPosition())==1){
                holder.expbutton.setImageResource(R.drawable.baseline_expand_less_24);
                if(typeel.get(holder.getAdapterPosition()).isEmpty()){
                    holder.putmaterial.setVisibility(View.GONE);
                    holder.notext.setVisibility(View.VISIBLE);
                }
                else {
                    holder.putmaterial.setVisibility(View.VISIBLE);
                    holder.notext.setVisibility(View.GONE);
                }
                expcheck.set(holder.getAdapterPosition(),0);
            }
            else if(expcheck.get(holder.getAdapterPosition())==0){
                holder.expbutton.setImageResource(R.drawable.baseline_expand_more_24);
                if(typeel.get(holder.getAdapterPosition()).isEmpty())
                    holder.notext.setVisibility(View.GONE);
                else
                    holder.putmaterial.setVisibility(View.GONE);
                expcheck.set(holder.getAdapterPosition(),1);
            }
        });
        holder.addsec.setOnClickListener(vadd->{
            onItemClick.oniteclicksection(holder.getAdapterPosition(),"addsection",sectionname.get(holder.getAdapterPosition()));
        });
    }


    @Override
    public int getItemCount() {
        try {
            return sectionname.size();
        }catch (Exception e){
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView secname,notext;
        ImageView delsec,expbutton,addsec;
        RelativeLayout expless;
        RecyclerView putmaterial;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            addsec=itemView.findViewById(R.id.addsection);
            putmaterial=itemView.findViewById(R.id.putmaterial);
            notext=itemView.findViewById(R.id.notext);
            expbutton=itemView.findViewById(R.id.expbutton);
            secname=itemView.findViewById(R.id.secname);
            delsec=itemView.findViewById(R.id.deletesection);
            expless=itemView.findViewById(R.id.expsection);
        }
    }



}