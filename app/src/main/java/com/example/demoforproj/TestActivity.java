package com.example.demoforproj;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ActionTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemChangeListener;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.interfaces.TouchListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String funame,profpic,titleid,titleofUser,titletopost;
    ProgressDialog progressDialog;
    private int previousSelectedItemId = -1;
    ImageSlider imgs;
    ArrayList<SlideModel> slideModels = new ArrayList<>();
    TextView imgt,pdft;
    View cust;
    ViewGroup pardispnot;
    RecyclerView recyclerView1;
    EditText caption;
    RelativeLayout postlayout;
    Button uploadNotice,delbut,expandbutton;
    RecyclerAdapter1 adapter1;
    TextView duname,ddate,dtime,depid,titid,tital;
    ImageView pdfselect,imgselect,title;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationview;
    ArrayList<String> selectedFiles = new ArrayList<>();
//    ViewPager2 viewPager2;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ArrayList<Uri> selectedUris = new ArrayList<>();
    ArrayList<Uri> selectedUripdf = new ArrayList<>();
    ArrayList<Uri> fileUris = new ArrayList<>();
    String fauth = firebaseAuth.getCurrentUser().getUid();
    public int itr = 0;
    public static final int Read_Permission = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        toolbar=findViewById(R.id.toolbar);
        //---------navigation drawer---------//
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationview=findViewById(R.id.nav_view);
        Menu menu = navigationview.getMenu();
        MenuItem menuItem = menu.findItem(R.id.navPost_notice);
        menuItem.setChecked(true);
        navigationview.bringToFront();
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(Color.BLACK);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getColor(android.R.color.white));
        toggle.syncState();
        navigationview.setNavigationItemSelectedListener(this);
        Window window =getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.newpurp));
        pardispnot=findViewById(R.id.dispnot);
        LayoutInflater layoutInflater = LayoutInflater.from(TestActivity.this);
        cust=layoutInflater.inflate(R.layout.custom_for_post_not,pardispnot,false);
        ImageView im = cust.findViewById(R.id.dpic);
        recyclerView1=cust.findViewById(R.id.recyclerview_gallery_pdf);
//        viewPager2=cust.findViewById(R.id.viewfordisp);
//        viewPager2.setVisibility(View.GONE);
        imgt=findViewById(R.id.it);
        pdft=findViewById(R.id.pt);
        duname=cust.findViewById(R.id.duname);
        ddate=cust.findViewById(R.id.ddate);
        depid=cust.findViewById(R.id.depid);
        dtime=cust.findViewById(R.id.dtime);
        titid=cust.findViewById(R.id.titid);
        tital=cust.findViewById(R.id.subj);
        title=findViewById(R.id.putcaption);
        caption=findViewById(R.id.caption);
//        viewPager2=cust.findViewById(R.id.viewfordisp);
        postlayout = cust.findViewById(R.id.postlayout);
        postlayout.setVisibility(View.VISIBLE);
        imgs = cust.findViewById(R.id.sliderr);
        delbut=cust.findViewById(R.id.removebutton);
        expandbutton=cust.findViewById(R.id.expandbutton);
//                    adapter=new RecyclerAdapter(imgUris,imgt,"DisplayNotice");
        adapter1=new RecyclerAdapter1(selectedUripdf,pdft,"TestActivity",null);
//                    recyclerView.setLayoutManager(new LinearLayoutManager(DisplayNotice.this));
        recyclerView1.setLayoutManager(new LinearLayoutManager(TestActivity.this));
//                    recyclerView.setAdapter(adapter);
        recyclerView1.setAdapter(adapter1);
        database.getReference().child("students").child(fauth).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                funame=snapshot.child("Username").getValue(String.class);
                titleid=snapshot.child("Department").getValue(String.class);
                profpic=snapshot.child("ProfPic").getValue(String.class);
                try{
                    titleofUser=snapshot.child("Title").getValue(String.class);
                }catch (Exception e){
                    titleofUser="";
                }
                try{
                    Glide.with(TestActivity.this)
                            .load(profpic)
                            .apply(RequestOptions.circleCropTransform())
                            .into(im);
                }catch (Exception e) {
                }
                try{
                    titid.setText(titleofUser);
                }catch (Exception e){
                    titid.setVisibility(View.GONE);
                }
                try{
                    depid.setText(titleid);
                }catch (Exception e){
                    depid.setVisibility(View.GONE);
                }
                duname.setText(funame);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String date=new SimpleDateFormat("dd_MM_yyyy_HH:mm").format(new Date());
        String[] parts;
        parts=date.split("_");
        String dddate= parts[0]+"/"+parts[1]+"/"+parts[2];
        ddate.setText(dddate);
        dtime.setText("("+parts[3]+")");
        pardispnot.addView(cust);
        pdfselect=findViewById(R.id.pdfselect);
        imgselect=findViewById(R.id.imgselect);
        uploadNotice=cust.findViewById(R.id.button);
        pdfselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(TestActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(TestActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Read_Permission);

                    return;
                }

                openGallery(1);

            }
        });
        imgselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(0);
            }
        });
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titletopost=caption.getText().toString();
                if(titletopost.isEmpty())
                    Toast.makeText(TestActivity.this, "Please enter the text first", Toast.LENGTH_LONG).show();
                else
                    tital.setText(caption.getText().toString());
            }
        });

        uploadNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectedUris.isEmpty()||!selectedUripdf.isEmpty()){
                    titletopost = tital.getText().toString();
                    if (titletopost.isEmpty())
                        Toast.makeText(TestActivity.this, "Please enter the subject to continue", Toast.LENGTH_LONG).show();
                    else
                        uploadFilesToFirebaseStorage(fileUris);
                }
                else{
                    Toast.makeText(TestActivity.this, "Select files to upload!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgs.setItemChangeListener(new ItemChangeListener() {
            @Override
            public void onItemChanged(int i) {
                itr=i;
            }
        });
        delbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideModels.remove(itr);
                selectedUris.remove(itr);
                imgs.setImageList(slideModels);
                if(slideModels.isEmpty()){
                    imgs.setVisibility(View.GONE);
                    delbut.setVisibility(View.GONE);
                    expandbutton.setVisibility(View.GONE);
                }
                Toast.makeText(TestActivity.this, "Image removed", Toast.LENGTH_SHORT).show();
            }
        });
        expandbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selstr = new ArrayList<>();
                for(int l=0;l<selectedUris.size();l++)
                    selstr.add(selectedUris.get(l).toString());
                Intent full = new Intent(TestActivity.this,FullDisp.class);
                full.putStringArrayListExtra("uri",selstr);
                full.putExtra("position",itr);
                try{
                    startActivity(full);
                }catch (Exception e){
                    Log.d("error",e.getMessage());
                }
            }
        });

    }
    private void openGallery(int a) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (a==0){
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        else{
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/pdf"});
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        launcher.launch(Intent.createChooser(intent, "Select Files"));
    }
    public ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ClipData clipData = result.getData().getClipData();

                    if (clipData != null) {
                        // Multiple files selected
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri fileUri = clipData.getItemAt(i).getUri();
                            String mimeType = getContentResolver().getType(fileUri);
                            if (mimeType.startsWith("image/")){
                                selectedUris.add(fileUri);
                                imgs.setVisibility(View.VISIBLE);
                                delbut.setVisibility(View.VISIBLE);
                                expandbutton.setVisibility(View.VISIBLE);
                                slideModels.add(new SlideModel(String.valueOf(fileUri), ScaleTypes.CENTER_CROP));
                            }
                            else if (mimeType.equals("application/pdf")) {
                                selectedUripdf.add(fileUri);
                                recyclerView1.setVisibility(View.VISIBLE);
                            }
                        }
                        imgs.setImageList(slideModels);
                        adapter1.notifyDataSetChanged();

                    }else {
                        // Single file selected
                        Uri fileUri = result.getData().getData();
                        String mimeType = getContentResolver().getType(fileUri);
                        if (mimeType.startsWith("image/")){
                            selectedUris.add(fileUri);
                            imgs.setVisibility(View.VISIBLE);
                            delbut.setVisibility(View.VISIBLE);
                            expandbutton.setVisibility(View.VISIBLE);
                            slideModels.add(new SlideModel(String.valueOf(fileUri), ScaleTypes.CENTER_CROP));
                        }
                        else if (mimeType.equals("application/pdf")) {
                            // Display PDF
                            selectedUripdf.add(fileUri);
                            recyclerView1.setVisibility(View.VISIBLE);
                        }
                        imgs.setImageList(slideModels);
                        adapter1.notifyDataSetChanged();
                    }
                }
            }
    );
    private void uploadFilesToFirebaseStorage(List<Uri> fileUris) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String tof = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss").format(new Date());
        database.getReference().child("notice").child(fauth+ tof).child("ProfPic").setValue(profpic);
        database.getReference().child("notice").child(fauth+ tof).child("Username").setValue(funame);
        database.getReference().child("notice").child(fauth+ tof).child("Title").setValue(titleofUser);
        database.getReference().child("notice").child(fauth+ tof).child("Department").setValue(titleid);
        database.getReference().child("notice").child(fauth+ tof).child("Subject").setValue(titletopost);
        database.getReference().child("notice").child(fauth+ tof).child("UserId").setValue(fauth);
        database.getReference().child("notice").child(fauth+ tof).child("DateandTime").setValue(new SimpleDateFormat("dd_MM_yyyy_HH:mm").format(new Date()));

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Files");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(fileUris.size());
        progressDialog.setCancelable(false);
        progressDialog.show();
        for(Uri uri:selectedUris){
            fileUris.add(uri);
        }
        for(Uri uri:selectedUripdf){
            fileUris.add(uri);
        }

        int totalFiles = fileUris.size();
        AtomicInteger uploadedFiles = new AtomicInteger(0);
        int size = fileUris.size();
        final int[] i = {0};
        for (int k=0;k<size;k++) {
            Uri fileUri = fileUris.get(k);
            String name = getFileName(fileUri);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("notice").child(name);
            UploadTask uploadTask = storageReference.putFile(fileUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Pattern pattern = Pattern.compile("^(.+?)\\.(?:jpg|jpeg|png)$");
                        // Match the pattern against the input string
                        Matcher matcher = pattern.matcher(name);
                        if(matcher.matches()){
                            i[0] = i[0] +1;
                            database.getReference().child("notice").child(fauth+ tof).child("img").child(String.valueOf(i[0])).child("FileUri").setValue(uri.toString());
                            database.getReference().child("notice").child(fauth+ tof).child("img").child(String.valueOf(i[0])).child("FileName").setValue(name);
                        }
                        else{
                            i[0] = i[0] +1;
                            database.getReference().child("notice").child(fauth+ tof).child("pdf").child(String.valueOf(i[0])).child("FileUri").setValue(uri.toString());
                            database.getReference().child("notice").child(fauth+ tof).child("pdf").child(String.valueOf(i[0])).child("FileName").setValue(name);
                        }
                    }
                });

                int count = uploadedFiles.incrementAndGet();
                progressDialog.setProgress(count);
                if (count == totalFiles) {
                    progressDialog.dismiss();
                    selectedFiles.clear();
                    selectedUris.clear();
                    fileUris.clear();
                    selectedUripdf.clear();
                    tital.setText(null);
                    caption.setText(null);
                    imgs.setVisibility(View.GONE);
                    delbut.setVisibility(View.GONE);
                    expandbutton.setVisibility(View.GONE);
                    recyclerView1.setVisibility(View.GONE);
                    adapter1.notifyDataSetChanged();
                    Toast.makeText(this, "Notice updated successfully", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(e -> {
                // Handle upload failure

                // Check if all files are uploaded, even in case of failure
                int count = uploadedFiles.incrementAndGet();
                progressDialog.setProgress(count);

                if (count == totalFiles) {
                    progressDialog.dismiss();
                    // All files uploaded (including the failed one)
                    // Perform any necessary actions
                }
            });
        }
        database.getReference().child("students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childnotsnap:snapshot.getChildren()){
                    String passing = childnotsnap.child("FCMtoken").getValue(String.class);
                    String message = "Published by @"+funame+" regarding "+titletopost;
                    try {
                        sendNotif(message,passing);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if((requestCode==1) && (grantResults.length>0) && (grantResults[0]==PackageManager.PERMISSION_GRANTED)){
            openGallery(1);

        }
        else{
            Toast.makeText(getApplicationContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
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


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {super.onBackPressed();
            startActivity(new Intent(TestActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    void sendNotif(String message,String passing) throws JSONException {
        JSONObject jsonObject=new JSONObject();
        JSONObject notific = new JSONObject();
        notific.put("title","New Notice");
        notific.put("body",message);
        JSONObject dataobj = new JSONObject();
        dataobj.put("userID",fauth);

        jsonObject.put("notification",notific);
        jsonObject.put("data",dataobj);
        jsonObject.put("to",passing);
        callapi(jsonObject);
    }
    void callapi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Authorization","Bearer AAAApPFm0b8:APA91bFlyaY5GzCW4Uba7y398zkmMAHv9mCPa_Yn2x0jUtkBdv7EoQEEeeEMv7_lWLN85wLYWGqQ4RjON1y9VgkwsvArQSbjM-gaPT0EumgDG7o2nC-zJCM27fp2q1NzMzqsftxDnoUR")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        switch (item.getItemId()){

            case R.id.classroom:
                startActivity(new Intent(TestActivity.this,Classroom.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                break;

            case R.id.navDisplay_notice:

                startActivity(new Intent(TestActivity.this,DisplayNotice.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                break;

            case R.id.nav_home:

                startActivity(new Intent(TestActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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
                    Intent intent = new Intent(TestActivity.this,LoginActivity.class);
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

}