package com.example.demoforproj;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.content.ContentResolver;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClassroomRecycler extends RecyclerView.Adapter<ClassroomRecycler.ViewHolder> {

    private ArrayList<String> course,batch,code,credit,type;
    private ArrayList<Integer> colorlist;
    private String foruser;
    public ClassroomRecycler(ArrayList<String> course, ArrayList<String> batch, ArrayList<String> code, ArrayList<String> credit, ArrayList<String> type,ArrayList<Integer> colorlist,String foruser) {
        this.course=course;
        this.batch=batch;
        this.code=code;
        this.credit=credit;
        this.type=type;
        this.colorlist=colorlist;
        this.foruser=foruser;
    }

    @NonNull
    @Override
    public ClassroomRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_for_classroom_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomRecycler.ViewHolder holder, int position) {
        int color=(position%2==0) ? position+1:position+4;
        holder.co.setText(course.get(position));
        holder.bat.setText(batch.get(position));
        holder.cod.setText(code.get(position));
        holder.cre.setText(credit.get(position));
        holder.typ.setText(type.get(position));
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{colorlist.get(color),colorlist.get(color+1)});
        gradientDrawable.setCornerRadius(100);
        holder.cardView.setCardElevation(50);
        holder.del.setColorFilter(colorlist.get(color));
        holder.del.setOnClickListener(v -> {
            Dialog dialog = new Dialog(holder.itemView.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.confirmdel);
            Button yes = dialog.findViewById(R.id.yes);
            Button no = dialog.findViewById(R.id.no);
            yes.setOnClickListener(v1 -> {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                int cpos=holder.getAdapterPosition();
                database.getReference().child("teachers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        database.getReference().child("teachers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("check").setValue(snapshot.child("Username").getValue(String.class)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("Classroom").child(snapshot.child("Username").getValue(String.class)).child(holder.cod.getText().toString()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(holder.itemView.getContext(), "Classroom deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                course.remove(cpos);
                batch.remove(cpos);
                code.remove(cpos);
                type.remove(cpos);
                credit.remove(cpos);
                notifyItemRemoved(cpos);
                notifyItemRangeChanged(cpos,getItemCount());
                dialog.dismiss();
            });
            no.setOnClickListener(v2 -> {
                dialog.dismiss();
            });
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);

        });

        holder.cardView.setBackground(gradientDrawable);
        holder.itemView.setOnClickListener(vz -> {
            Intent intent = new Intent(holder.itemView.getContext(),AllClassroomActivity.class);
            intent.putExtra("subjcode",code.get(holder.getAdapterPosition()));
            intent.putExtra("subjname",course.get(holder.getAdapterPosition()));
            intent.putExtra("batch",batch.get(holder.getAdapterPosition()));
            holder.itemView.getContext().startActivity(intent);

        });
    }


    @Override
    public int getItemCount() {
        return code.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView co,bat,cod,cre,typ;
        ImageView del;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            cardView=itemView.findViewById(R.id.cardview);
            co=itemView.findViewById(R.id.course);
            bat=itemView.findViewById(R.id.batch);
            cod=itemView.findViewById(R.id.code);
            cre=itemView.findViewById(R.id.credit);
            typ=itemView.findViewById(R.id.type);
            del=itemView.findViewById(R.id.delview);
        }
    }
}
