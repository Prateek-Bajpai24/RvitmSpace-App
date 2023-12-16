package com.example.demoforproj;


import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class DashBoardActivity extends AppCompatActivity {
    FloatingActionButton fb;
    TextView dep, tes, cou;
    Button cuname,cunames;
    ImageView img;
    RelativeLayout gosync,unsync;
    public String fieldValue;
    GoogleSignInClient mgsic;
    String uid;
    GoogleSignInAccount account;
    SharedPreferences sp;
    EditText namedy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        Window window =getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.newpurp));
        if(account!=null)
            changelayout();
        fb = findViewById(R.id.floatingActionButton);
        img = findViewById(R.id.profpic);
        dep = findViewById(R.id.dept);
        namedy = findViewById(R.id.named);
        gosync = findViewById(R.id.gsync);
        unsync=findViewById(R.id.usc);
        unsync.setVisibility(View.GONE);
        tes = findViewById(R.id.downloads);
        cou = gosync.findViewById(R.id.choup);
        cuname=findViewById(R.id.cuname);
        cunames=findViewById(R.id.cunames);
        cunames.setVisibility(View.GONE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        ProgressDialog progressDialog = new ProgressDialog(DashBoardActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        db.collection("students").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.contains("Profile Pic")) {
                        fieldValue = document.getString("Profile Pic");
                        Glide.with(DashBoardActivity.this)
                                .load(fieldValue)
                                .apply(RequestOptions.circleCropTransform())
                                .error(R.drawable.baseline_account_circle_24)
                                .into(img);
                    } else {
                        img.setImageResource(R.drawable.baseline_account_circle_24);
                        fieldValue=null;
                    }
                    dep.setText(document.getString("Department"));
                    namedy.setText(document.getString("Username"));
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(DashBoardActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sp = getSharedPreferences("Googleacc", MODE_PRIVATE);
        if(sp.getBoolean("googlogged",false))
            changelayout();
        cuname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namedy.setEnabled(true);
                cuname.setVisibility(View.GONE);
                cunames.setVisibility(View.VISIBLE);
                cunames.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cunames.setVisibility(View.GONE);
                        cuname.setVisibility(View.VISIBLE);
                        namedy.setEnabled(false);
                        if(!namedy.getText().toString().isEmpty()){
                            Map<String,Object> students = new HashMap<>();
                            students.put("Username",namedy.getText().toString());
                            db.collection("students").document(uid).update(students);
                        }
                        else{
                            Toast.makeText(DashBoardActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, Fullscreendisp.class);
                intent.putExtra("imageUri", fieldValue);
                intent.putExtra("stat","fupload");// Pass the image URL to the FullScreenActivity
                launcher.launch(intent);
            }
        });
        gosync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mgsic = GoogleSignIn.getClient(DashBoardActivity.this, gso);
                Intent signint = mgsic.getSignInIntent();
                signInLauncher.launch(signint);
            }
        });
        unsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgsic.signOut();
                sp=getSharedPreferences("Googleacc",MODE_PRIVATE);
                sp.edit().putBoolean("googlogged",false).apply();
                recreate();
            }
        });
    }
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String stat = result.getData().getStringExtra("updates");
                    if ("image_updated".equals(stat)) {
                        String updatedImage = result.getData().getStringExtra("updatedI");
                        Glide.with(DashBoardActivity.this)
                                .load(updatedImage)
                                .apply(RequestOptions.circleCropTransform())
                                .error(R.drawable.baseline_account_circle_24)
                                .into(img);
                        fieldValue = updatedImage;
                    } else if ("image_deleted".equals(stat)) {
                        Glide.with(DashBoardActivity.this)
                                .load(R.drawable.baseline_account_circle_24)
                                .apply(RequestOptions.circleCropTransform())
                                .into(img);
                        fieldValue = null;
                    }
                    if(result.getData().getParcelableExtra("savedornot")!=null){
                        sp = getSharedPreferences("Googleacc",MODE_PRIVATE);
                        sp.edit().putBoolean("googlogged",true).apply();
                        namedy.setText(result.getData().getStringExtra("chuname"));
                        changelayout();
                    }
                }
            }
    );
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            account = task.getResult(ApiException.class);
                            if (account != null) {
                                String acce= account.getEmail();
                                if(acce.endsWith(".rvitm@rvei.edu.in")){
                                    Intent intent1 = new Intent(DashBoardActivity.this,Fullscreendisp.class);
                                    intent1.putExtra("stat","gupload");
                                    intent1.putExtra("gogacc",account);
                                    launcher.launch(intent1);
                                }
                                else{
                                    Toast.makeText(this, "Please select college email to continue syncing", Toast.LENGTH_LONG).show();
                                    mgsic.signOut();
                                }

                            }
                        } catch (ApiException e) {
                            Toast.makeText(DashBoardActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                }
            }
    );
    private void changelayout(){
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) gosync.getLayoutParams();
        layoutParams.leftMargin=(100);
        layoutParams.rightMargin=(250);
        gosync.setLayoutParams(layoutParams);
        unsync.setVisibility(View.VISIBLE);
        cou.setText("Google account synced");
        gosync.setClickable(false);
    }

}