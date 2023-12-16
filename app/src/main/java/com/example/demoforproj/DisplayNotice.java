package com.example.demoforproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.adapters.ViewPagerAdapter;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

public class DisplayNotice extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView1,recyclerView;
    TextView imgt,pdft,duname,ddate,dtime,cap,depid,titid;
    View cust;
    ViewGroup pardispnot;
    Toolbar toolbar;
    NavigationView navigationview;
    DrawerLayout drawerLayout;
    private int previousSelectedItemId = -1;

    RecyclerAdapter1 adapter1;
    RecyclerAdapter adapter;
    LinearLayout progressdialog;
    List<SlideModel> slideModels;
    int itr = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_notice);
        Window window =getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.newpurp));
        pardispnot=findViewById(R.id.dispnot);
        progressdialog=findViewById(R.id.linv);
        //------toolbar--------------//
        toolbar=findViewById(R.id.toolbar);
        //---------navigation drawer---------//
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationview=findViewById(R.id.nav_view);
        Menu menu = navigationview.getMenu();
        MenuItem menuItem = menu.findItem(R.id.navDisplay_notice);
        menuItem.setChecked(true);
        navigationview.bringToFront();
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getColor(android.R.color.white));
        toggle.syncState();

        navigationview.setNavigationItemSelectedListener(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("notice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Dateobj> customlist = new ArrayList<>();
                List<DataSnapshot> datasd = new ArrayList<>();
                for (DataSnapshot checksnap : snapshot.getChildren()) {
                    String dateTimeString = checksnap.child("DateandTime").getValue(String.class);
                    if (dateTimeString != null) {
                        String[] check = dateTimeString.split("_");
                        if (check.length >= 4) {
                            String timec[] = check[3].split(":");
                            int si[] = new int[5];
                            int j = 0;
                            for (int i = 0; i < check.length - 1; i++, j++) {
                                si[j] = Integer.parseInt(check[i]);
                            }
                            for (int i = 0; i < timec.length; i++, j++) {
                                si[j] = Integer.parseInt(timec[i]);
                            }
                            customlist.add(new Dateobj(si[0], si[1], si[2], si[3], si[4], checksnap));
                        }
                    }
                }

                CustomComp customComp = new CustomComp();
                customlist.sort(customComp);
                for(Dateobj dasad:customlist){
                    datasd.add(dasad.snap);
                }
                for(DataSnapshot notsnap:datasd){
                    LayoutInflater layoutInflater =LayoutInflater.from(DisplayNotice.this);
                    cust=layoutInflater.inflate(R.layout.custom_for_dispnot,pardispnot,false);
                    ImageView im = cust.findViewById(R.id.dpic);
//                    recyclerView=cust.findViewById(R.id.recyclerview_gallery);
                    recyclerView1=cust.findViewById(R.id.recyclerview_gallery_pdf);
                    recyclerView1.setVisibility(View.VISIBLE);
                    imgt=findViewById(R.id.it);
                    pdft=findViewById(R.id.pt);
                    duname=cust.findViewById(R.id.duname);
                    ddate=cust.findViewById(R.id.ddate);
                    depid=cust.findViewById(R.id.depid);
                    dtime=cust.findViewById(R.id.dtime);
                    cap=cust.findViewById((R.id.subj));
                    titid=cust.findViewById(R.id.titid);
                    recyclerView=cust.findViewById(R.id.sliderr);
                    slideModels=new ArrayList<>();
                    ArrayList<Uri> pdfUris = new ArrayList<>();
                    ArrayList<Uri> imgUris = new ArrayList<>();
                    adapter=new RecyclerAdapter(slideModels,imgUris);
                    adapter1=new RecyclerAdapter1(pdfUris,pdft,"DisplayNotice",null);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(DisplayNotice.this));
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView1.setLayoutManager(new LinearLayoutManager(DisplayNotice.this));
                    recyclerView1.setAdapter(adapter1);
                    try{
                        Glide.with(DisplayNotice.this)
                                .load(notsnap.child("ProfPic").getValue(String.class))
                                .apply(RequestOptions.circleCropTransform())
                                .into(im);
                    }catch (Exception e){

                    }
                    try{
                        titid.setText(notsnap.child("Title").getValue(String.class));
                    }catch (Exception e){
                        titid.setVisibility(View.GONE);
                    }
                    try{
                        depid.setText(notsnap.child("Department").getValue(String.class));
                    }catch (Exception e){
                        depid.setVisibility(View.GONE);
                    }
                    duname.setText(notsnap.child("Username").getValue(String.class));
                    cap.setText(notsnap.child("Subject").getValue(String.class));
                    for(DataSnapshot childsnap:notsnap.child("img").getChildren()){
                        imgUris.add(Uri.parse(childsnap.child("FileUri").getValue(String.class)));
                        slideModels.add(new SlideModel(childsnap.child("FileUri").getValue(String.class), ScaleTypes.CENTER_CROP));
                    }
                    for(DataSnapshot childsnap1:notsnap.child("pdf").getChildren()){
                        pdfUris.add(Uri.parse(childsnap1.child("FileUri").getValue(String.class)));
                    }
                    if(!slideModels.isEmpty()){
                        recyclerView.setVisibility(View.VISIBLE);
                        Log.d("checkim",imgUris.toString());
                        adapter.notifyDataSetChanged();
                    }
                    adapter1.notifyDataSetChanged();
                    String parts[];
                    try{
                        parts=notsnap.child("DateandTime").getValue(String.class).split("_");
                    }catch (Exception e){
                        String date=new SimpleDateFormat("dd_MM_yyyy_HH:mm").format(new Date());
                        parts=date.split("_");
                    }
                    String dddate= parts[0]+"/"+parts[1]+"/"+parts[2];
                    ddate.setText(dddate);
                    dtime.setText("("+parts[3]+")");
                    pardispnot.addView(cust);
                    itr+=1;
                }
                if(itr==datasd.size()-1)
                    progressdialog.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        database.getReference().child("notice").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {;
//                for(DataSnapshot notsnap:snapshot.getChildren()){
//                    LayoutInflater layoutInflater =LayoutInflater.from(DisplayNotice.this);
//                    cust=layoutInflater.inflate(R.layout.custom_for_dispnot,pardispnot,false);
//                    ImageView im = cust.findViewById(R.id.dpic);
////                    recyclerView=cust.findViewById(R.id.recyclerview_gallery);
//                    recyclerView1=cust.findViewById(R.id.recyclerview_gallery_pdf);
//                    recyclerView1.setVisibility(View.VISIBLE);
//                    imgt=findViewById(R.id.it);
//                    pdft=findViewById(R.id.pt);
//                    duname=cust.findViewById(R.id.duname);
//                    ddate=cust.findViewById(R.id.ddate);
//                    depid=cust.findViewById(R.id.depid);
//                    dtime=cust.findViewById(R.id.dtime);
//                    cap=cust.findViewById((R.id.subj));
//                    titid=cust.findViewById(R.id.titid);
//                    recyclerView=cust.findViewById(R.id.sliderr);
//                    slideModels=new ArrayList<>();
//                    ArrayList<Uri> pdfUris = new ArrayList<>();
//                    ArrayList<Uri> imgUris = new ArrayList<>();
//                    adapter=new RecyclerAdapter(slideModels,imgUris);
//                    adapter1=new RecyclerAdapter1(pdfUris,pdft,"DisplayNotice");
//                    recyclerView.setAdapter(adapter);
//                    recyclerView.setLayoutManager(new LinearLayoutManager(DisplayNotice.this));
//                    recyclerView.setNestedScrollingEnabled(false);
//                    recyclerView1.setLayoutManager(new LinearLayoutManager(DisplayNotice.this));
//                    recyclerView1.setAdapter(adapter1);
//                    try{
//                        Glide.with(DisplayNotice.this)
//                                .load(notsnap.child("ProfPic").getValue(String.class))
//                                .apply(RequestOptions.circleCropTransform())
//                                .into(im);
//                    }catch (Exception e){
//
//                    }
//                    try{
//                        titid.setText(notsnap.child("Title").getValue(String.class));
//                    }catch (Exception e){
//                        titid.setVisibility(View.GONE);
//                    }
//                    try{
//                        depid.setText(notsnap.child("Department").getValue(String.class));
//                    }catch (Exception e){
//                        depid.setVisibility(View.GONE);
//                    }
//                    duname.setText(notsnap.child("Username").getValue(String.class));
//                    cap.setText(notsnap.child("Subject").getValue(String.class));
//                    for(DataSnapshot childsnap:notsnap.child("img").getChildren()){
//                        imgUris.add(Uri.parse(childsnap.child("FileUri").getValue(String.class)));
//                        slideModels.add(new SlideModel(childsnap.child("FileUri").getValue(String.class), ScaleTypes.CENTER_CROP));
//                    }
//                    for(DataSnapshot childsnap1:notsnap.child("pdf").getChildren()){
//                        pdfUris.add(Uri.parse(childsnap1.child("FileUri").getValue(String.class)));
//                    }
//                    if(!slideModels.isEmpty()){
//                        recyclerView.setVisibility(View.VISIBLE);
//                        Log.d("checkim",imgUris.toString());
//                        adapter.notifyDataSetChanged();
//                    }
//                    adapter1.notifyDataSetChanged();
//                    String parts[] = new String[4];
//                    try{
//                        parts=notsnap.child("DateandTime").getValue(String.class).split("_");
//                    }catch (Exception e){
//                        String date=new SimpleDateFormat("dd_MM_yyyy_HH:mm").format(new Date());
//                        parts=date.split("_");
//                    }
//                    String dddate= parts[0]+"/"+parts[1]+"/"+parts[2];
//                    ddate.setText(dddate);
//                    dtime.setText("("+parts[3]+")");
//                    pardispnot.addView(cust);
//                    progressDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {super.onBackPressed();
            startActivity(new Intent(DisplayNotice.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        switch (item.getItemId()){
            case R.id.classroom:
                startActivity(new Intent(DisplayNotice.this,Classroom.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            case R.id.navPost_notice:
                startActivity(new Intent(DisplayNotice.this,TestActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                break;

            case R.id.nav_home:
                startActivity(new Intent(DisplayNotice.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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
                    Intent intent = new Intent(DisplayNotice.this,LoginActivity.class);
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
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.profile) {
//            startActivity(new Intent(DisplayNotice.this,DashBoardActivity.class));
//
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
class Dateobj{
    int id1;
    int id2;
    int id3;
    int id4;
    int id5;
    DataSnapshot snap;
    public Dateobj(int id1, int id2, int id3, int id4,int id5, DataSnapshot snap){
        this.id1=id1;
        this.id2=id2;
        this.id3=id3;
        this.id4=id4;
        this.id5=id5;
        this.snap=snap;
    }
}
class CustomComp implements Comparator<Dateobj>{

    @Override
    public int compare(Dateobj o1, Dateobj o2) {
        if(o1.id3>o2.id3)
            return -1;
        else if(o1.id3==o2.id3 && o1.id2>o2.id2)
            return -1;
        else if (o1.id3==o2.id3 && o1.id2==o2.id2 && o1.id1>o2.id1)
            return -1;
        else if(o1.id3==o2.id3 && o1.id2==o2.id2 && o1.id1==o2.id1 && o1.id4>o2.id4)
            return -1;
        else if (o1.id3==o2.id3 && o1.id2==o2.id2 && o1.id1==o2.id1 && o1.id4==o2.id4 && o1.id5> o2.id5)
            return -1;
        else return 1;

    }
}