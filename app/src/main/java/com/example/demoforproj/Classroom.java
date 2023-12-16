package com.example.demoforproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class Classroom extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    private int previousSelectedItemId = -1;
    NavigationView navigationview;
    Toolbar toolbar;
    FloatingActionButton addclass;
    int color=0;
    LinearLayout progressdialog;
    RecyclerView recyclerViewclassroom;
    ClassroomRecycler classadapter;
    RelativeLayout rl;
    String foruser;
//    View cust;
    FirebaseAuth auth;
    TextView ctext;
    ArrayList<Integer> colorvalue=new ArrayList<>();
    String path;
    ArrayList<String> coursetosend,batchtosend,codetosend,credittosend,typetosend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        //------toolbar--------------//
        toolbar=findViewById(R.id.toolbar);
        //---------navigation drawer---------//
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationview=findViewById(R.id.nav_view);
        Menu menu = navigationview.getMenu();
        MenuItem menuItem = menu.findItem(R.id.classroom);
        menuItem.setChecked(true);
        navigationview.bringToFront();
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getColor(android.R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationview.setNavigationItemSelectedListener(this);
        Window window =getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.newpurp));
        addclass=findViewById(R.id.fab);
        progressdialog=findViewById(R.id.linv);
        rl=findViewById(R.id.relpar);
        recyclerViewclassroom=findViewById(R.id.classroom_recycler);
        coursetosend=new ArrayList<>();
        batchtosend=new ArrayList<>();
        codetosend=new ArrayList<>();
        credittosend=new ArrayList<>();
        typetosend=new ArrayList<>();
        ctext=findViewById(R.id.chtex);
        recyclerViewclassroom.setLayoutManager(new LinearLayoutManager(Classroom.this));
        Resources res = getResources();

        // Get the resource ID of the colors array
        int colorsArrayResourceId = res.getIdentifier("colors", "array", getPackageName());

        // Retrieve the array of color resource IDs
        TypedArray colorArray = res.obtainTypedArray(colorsArrayResourceId);

        // Loop through each color resource ID
        for (int i = 0; i < colorArray.length(); i++) {
            int colorResourceId = colorArray.getResourceId(i, -1);

            // Check if the resource ID is valid
            if (colorResourceId != -1) {
                // Get the color value using the resource ID
                colorvalue.add(res.getColor(colorResourceId));
                // You can now use the colorValue as needed
                // For example, you can set it as the background color of a view
            }
        }

        // Don't forget to recycle the TypedArray
        colorArray.recycle();
        classadapter = new ClassroomRecycler(coursetosend,batchtosend,codetosend,credittosend,typetosend,colorvalue,foruser);

        recyclerViewclassroom.setAdapter(classadapter);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        try{
            Log.d("auth",auth.getCurrentUser().getUid());
            database.getReference().child("teachers").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    database.getReference().child("teachers").child(auth.getCurrentUser().getUid()).child("check").setValue(snapshot.child("Username").getValue(String.class)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            foruser=snapshot.child("Username").getValue(String.class);
                            database.getReference().child("Classroom").child(foruser).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot childclass:snapshot.getChildren()){
                                        String[] coderef = childclass.child("Credits").getRef().getParent().toString().split("/");
                                        codetosend.add(coderef[5]);
                                        coursetosend.add(childclass.child("Course").getValue(String.class));
                                        batchtosend.add(childclass.child("Batch").getValue(String.class));
                                        typetosend.add(childclass.child("Type").getValue(String.class));
                                        credittosend.add(childclass.child("Credits").getValue(String.class));
                                        database.getReference().child("Classroom").child(foruser).removeEventListener(this);
                                    }
                                    addclass.setVisibility(View.VISIBLE);
                                    progressdialog.setVisibility(View.GONE);
                                    classadapter.notifyDataSetChanged();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }catch (Exception e){
            Log.d("Classroom",e.getMessage());
        }
        addclass.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.alertdialogtoselectclass);
            AutoCompleteTextView sem =dialog.findViewById(R.id.sem);
            AutoCompleteTextView dept = dialog.findViewById(R.id.dept);
            EditText coursecode = dialog.findViewById(R.id.coursecode);
            EditText course = dialog.findViewById(R.id.course);
            EditText credittext = dialog.findViewById(R.id.credittext);
            AutoCompleteTextView typetext = dialog.findViewById(R.id.typetext);
            AutoCompleteTextView batch = dialog.findViewById(R.id.batch);
            LinearLayout progressdialogalert = dialog.findViewById(R.id.linv);
            Button add = dialog.findViewById(R.id.add);
            sem.setInputType(InputType.TYPE_NULL);
            batch.setInputType(InputType.TYPE_NULL);
            dept.setInputType(InputType.TYPE_NULL);
            typetext.setInputType(InputType.TYPE_NULL);
            progressdialogalert.setVisibility(View.GONE);
            ArrayList<String> deplist = new ArrayList<>();
            deplist.add("CSE");
            deplist.add("ISE");
            deplist.add("ECE");
            deplist.add("ME");
            ArrayList<String> semlist  =new ArrayList<>();
            semlist.add("1");
            semlist.add("2");
            semlist.add("3");
            semlist.add("4");
            semlist.add("5");
            semlist.add("6");
            semlist.add("7");
            semlist.add("8");
            ArrayList<String> typelist = new ArrayList<>();
            typelist.add("Laboratory");
            typelist.add("Core Subject");
            typelist.add("Professional Elective");
            typelist.add("Open Elective");
            ArrayList<String> branchlist = new ArrayList<>();
            ArrayAdapter<String> typeretiever = new ArrayAdapter<>(Classroom.this, android.R.layout.simple_dropdown_item_1line,typelist);
            ArrayAdapter<String> deptretreiver = new ArrayAdapter<>(Classroom.this, android.R.layout.simple_dropdown_item_1line,deplist);
            ArrayAdapter<String> semretreiver = new ArrayAdapter<>(Classroom.this, android.R.layout.simple_dropdown_item_1line,semlist);
            //change to view only two items and rest by scrolling and label to be added
            dept.setAdapter(deptretreiver);
            sem.setAdapter(semretreiver);
            typetext.setAdapter(typeretiever);
            typetext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    typetext.setDropDownHeight(350);
                    typetext.setDropDownVerticalOffset(-typetext.getDropDownHeight()-130);
                    typetext.showDropDown();
                }
            });
            typetext.setOnClickListener(vt->{
                typetext.showDropDown();
            });
            dept.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        dept.setDropDownHeight(350);
                        dept.showDropDown();
                    }
                }
            });
            dept.setOnClickListener(vd->{
                dept.showDropDown();
            });
            sem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        sem.setDropDownHeight(500);
                        sem.showDropDown();
                    }
                }
            });
            sem.setOnClickListener(vs->{
                sem.showDropDown();
            });
            dept.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    progressdialogalert.setVisibility(View.VISIBLE);
                    if(!branchlist.isEmpty())
                        for(int bi=0;bi<branchlist.size();bi++)
                            branchlist.remove(bi);
                    database.getReference().child("Branchwise_batch").child("Department of "+dept.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot childbranch:snapshot.getChildren())
                                for(DataSnapshot keyextractor: childbranch.getChildren()){
                                    String[] ref= keyextractor.getRef().getParent().toString().split("/");
                                    branchlist.add(ref[5]);
                                    break;
                                }
                            if(branchlist.isEmpty())
                                Toast.makeText(Classroom.this, "No data for this semester, please ask students to register in app", Toast.LENGTH_LONG).show();
                            progressdialogalert.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            ArrayAdapter<String> branchretriever = new ArrayAdapter<>(Classroom.this, android.R.layout.simple_dropdown_item_1line,branchlist);
            batch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    batch.setDropDownHeight(350);
                    batch.showDropDown();
                }
            });
            batch.setAdapter(branchretriever);
            batch.setOnClickListener(vb->{
                batch.setDropDownHeight(350);
                batch.showDropDown();
            });
            add.setOnClickListener(v1 -> {
                color+=1;
                Log.d("ctest",String.valueOf(color));
                final int temp = color;
                if(sem.getText().toString().isEmpty()||dept.getText().toString().isEmpty()||coursecode.getText().toString().isEmpty()||course.getText().toString().isEmpty()||batch.getText().toString().isEmpty()||credittext.getText().toString().isEmpty()||typetext.getText().toString().isEmpty())
                    Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                else{
                    database.getReference().child("teachers").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                        path = snapshot.child("Username").getValue(String.class);
                                        database.getReference().child("Classroom").child(path).child(coursecode.getText().toString().toUpperCase()).child("Credits").setValue(credittext.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                database.getReference().child("Classroom").child(path).child(coursecode.getText().toString().toUpperCase()).child("Type").setValue(typetext.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        database.getReference().child("Branchwise_batch").child("Department of "+dept.getText().toString()).child(batch.getText().toString()).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for(DataSnapshot studentdnap: snapshot.getChildren()){
                                                                    String sname=studentdnap.getValue(String.class);
                                                                    String susn = studentdnap.getKey();
                                                                    database.getReference().child("Classroom").child(path).child(coursecode.getText().toString().toUpperCase()).child("Course_Students").child(susn).setValue(sname).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            database.getReference().child("Classroom").child(path).child(coursecode.getText().toString().toUpperCase()).child("Sem").setValue(sem.getText().toString());
                                                                            database.getReference().child("Classroom").child(path).child(coursecode.getText().toString().toUpperCase()).child("Course").setValue(course.getText().toString());
                                                                            database.getReference().child("Classroom").child(path).child(coursecode.getText().toString().toUpperCase()).child("Batch").setValue(batch.getText().toString());
                                                                            database.getReference().child("Classroom").child(path).child(coursecode.getText().toString().toUpperCase()).child("Materials").child("Textbook").setValue("none");
                                                                            database.getReference().child("Classroom").child(path).child(coursecode.getText().toString().toUpperCase()).child("Materials").child("CiePyq").setValue("none");
                                                                            database.getReference().child("Classroom").child(path).child(coursecode.getText().toString().toUpperCase()).child("Materials").child("UniversityPyq").setValue("none");
                                                                        }
                                                                    });
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                            }catch (Exception e){
                                Log.d("error",e.getMessage());
                                path="error";
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//                    LayoutInflater layoutInflater =LayoutInflater.from(Classroom.this);
//                    cust=layoutInflater.inflate(R.layout.custom_for_classroom_display,pardispnot,false);
//                    codetosend=cust.findViewById(R.id.code);
//                    coursetosend=cust.findViewById(R.id.course);
//                    batchtosend=cust.findViewById(R.id.batch);
//                    typetosend=cust.findViewById(R.id.type);
//                    credittosend=cust.findViewById(R.id.credit);
//                    cardView=cust.findViewById(R.id.cardview);
                    coursetosend.add(course.getText().toString());
                    codetosend.add(coursecode.getText().toString());
                    batchtosend.add(batch.getText().toString());
                    typetosend.add(typetext.getText().toString());
                    credittosend.add(credittext.getText().toString());
                    try{
//                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{colorvalue.get(temp-1), colorvalue.get(temp)});
//                        gradientDrawable.setCornerRadius(100);
//                        cardView.setCardElevation(50);
//                        cardView.setBackground(gradientDrawable);
//                        pardispnot.addView(cust);
                        classadapter.notifyDataSetChanged();
                        dialog.dismiss();
                        Toast.makeText(this, "New classroom added", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Log.d("ctest",String.valueOf(color));
                        dialog.dismiss();
                    }
                }

            });
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        switch (item.getItemId()){
            case  R.id.nav_home:
                startActivity(new Intent(Classroom.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case R.id.navPost_notice:
                startActivity(new Intent(Classroom.this,TestActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                break;

            case R.id.navDisplay_notice:
                startActivity(new Intent(Classroom.this,DisplayNotice.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                break;

            case R.id.nav_logout:
                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.confirmdel);
                TextView confdel = dialog.findViewById(R.id.conformtext);
                confdel.setText("Confirm Logout?");
                Button yes = dialog.findViewById(R.id.yes);
                Button no =dialog.findViewById(R.id.no);
                yes.setOnClickListener(v -> {
                    auth.signOut();
                    SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS,0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("hasLogged",false);
                    editor.commit();
                    Intent intent = new Intent(Classroom.this,LoginActivity.class);
                    startActivity(intent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    dialog.dismiss();
                });
                no.setOnClickListener(v1 -> {
                    dialog.dismiss();
                });
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setGravity(Gravity.CENTER);
                break;
        }


        // highlight selected item
        item.setChecked(true);
        //get previous selected item id
        previousSelectedItemId = item.getItemId();

        return true;
    }

    //onResume method in your activity to clear the selection state of the previous item
    @Override
    protected void onResume() {
        super.onResume();
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (previousSelectedItemId != -1) {
            // Clear the selection state of the previous item
            navigationView.getMenu().findItem(previousSelectedItemId).setChecked(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {super.onBackPressed();
            startActivity(new Intent(Classroom.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

}