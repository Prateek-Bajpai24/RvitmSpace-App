package com.example.demoforproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AttendanceSheet extends AppCompatActivity implements RecyclerAS.OnItemClick, EditAdapter.OnItemClick{
    RecyclerView recyclerView;
    ImageView bb;
    RecyclerAS adapter1;
    EditAdapter editAdapter;
    TextView Dateattend;
    String subjcode, foruser,togo,date,mode;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ArrayList<String> studlist = new ArrayList<>();
    ArrayList<String> statuslist = new ArrayList<>();
    Button savebutton;
    ArrayList<String> studentdat = new ArrayList<>();
    LinearLayout progressdialog;
    RelativeLayout abs,pres;
    FloatingActionButton expand;
    int checkexp=0;
    int checkforstuddat=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_sheet);
        Window window=getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.newpurp));
        expand=findViewById(R.id.expand);
        abs=findViewById(R.id.allabsent);
        pres=findViewById(R.id.allpresent);
        progressdialog=findViewById(R.id.linv);
        bb=findViewById(R.id.bb);
        savebutton=findViewById(R.id.savebutt);
        Intent intent = getIntent();
        subjcode = intent.getStringExtra("subjcode");
        date=intent.getStringExtra("date");
        mode=intent.getStringExtra("mode");
        Dateattend=findViewById(R.id.attendancedate);
        Dateattend.setText("Attendance - "+date);
//        pardispnot = findViewById(R.id.as);
        recyclerView = findViewById(R.id.studdata);
        editAdapter=new EditAdapter(this,studlist,statuslist,"attsheet");
        adapter1 = new RecyclerAS(studlist,"AttendanceSheet",statuslist,this); // Pass studlist to the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter1);
        database.getReference().child("teachers").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                database.getReference().child("teachers").child(auth.getCurrentUser().getUid()).child("check")
                        .setValue(snapshot.child("Username").getValue(String.class)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    foruser = snapshot.child("check").getValue(String.class);
                                    database.getReference().child("Classroom").child(foruser).child(subjcode)
                                            .child("Course_Students").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot ssnapshot) {
                                                    studlist.clear(); // Clear the list to avoid duplicates
                                                    for (DataSnapshot ch : ssnapshot.getChildren()) {
                                                        togo = ch.getKey() + " - " + ch.getValue(String.class);
                                                        studlist.add(togo);
                                                    }
                                                    if(!mode.equals("edit")){
                                                        progressdialog.setVisibility(View.GONE);
                                                        adapter1.notifyDataSetChanged();
                                                    }
                                                    else
                                                        recyclerView.setAdapter(editAdapter);
                                                    // Notify the adapter that the data has changed
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                    if(mode.equals("edit")){
                                        database.getReference().child("Classroom").child(foruser).child(subjcode).child("Attendance").child(date).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot datesnapshot) {
                                                for(DataSnapshot cds:datesnapshot.getChildren()){
                                                    Log.d("Studentstatus",cds.getValue(String.class));
                                                    statuslist.add(cds.getValue(String.class));
                                                }
                                                progressdialog.setVisibility(View.GONE);
                                                statuslist.add("modeedit");
                                                editAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        savebutton.setOnClickListener(v -> {
            if(mode.equals("edit"))
                studentdat=editAdapter.getStudlist();
            else
                studentdat = adapter1.getStudlist();
            if(studentdat.size()==(studlist.size()+1)){
            for(int i=0;i<studentdat.size();i++){
                if(studentdat.contains("checkover")){
                    if(studentdat.get(i+1).equals("checkover"))
                        studentdat.remove(i+1);
                    if(studentdat.get(i).equals("checkover")){
                        studentdat.remove(i);
                        checkforstuddat=1;
                    }
                    if(checkforstuddat==1){
                        checkforstuddat=0;
                        database.getReference().child("Classroom").child(foruser).child(subjcode).child("Attendance").child(date).child(studentdat.get(i)).setValue("A");
                    }
                    else
                        database.getReference().child("Classroom").child(foruser).child(subjcode).child("Attendance").child(date).child(studentdat.get(i)).setValue("P");
                }
                else
                    database.getReference().child("Classroom").child(foruser).child(subjcode).child("Attendance").child(date).child(studentdat.get(i)).setValue("A");
                if(i==studentdat.size()-1){
                    if(mode.equals("edit"))
                        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(this, "Attendance saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            }
            else {
                Toast.makeText(this, "Please select status of all children to save attendance", Toast.LENGTH_LONG).show();
            }

        });
        bb.setOnClickListener(v1 -> {
            finish();
        });
        expand.setOnClickListener(ve->{
            if(checkexp==0){
                expand.setImageResource(R.drawable.baseline_expand_more_24);
                pres.setVisibility(View.VISIBLE);
                abs.setVisibility(View.VISIBLE);
                checkexp=1;
            }
            else{
                expand.setImageResource(R.drawable.baseline_expand_less_24);
                pres.setVisibility(View.GONE);
                abs.setVisibility(View.GONE);
                checkexp=0;
            }
        });
        pres.setOnClickListener(vp->{
            statuslist.add("allpres");
            adapter1.notifyDataSetChanged();
        });
        abs.setOnClickListener(va->{
            statuslist.add("allabs");
            adapter1.notifyDataSetChanged();
        });
    }

    @Override
    public void oniteMclick(int position,String daterec) {

    }

    @Override
    public void onitemclick(int position) {

    }
}