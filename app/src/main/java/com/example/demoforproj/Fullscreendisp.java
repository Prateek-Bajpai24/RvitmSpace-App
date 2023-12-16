package com.example.demoforproj;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Fullscreendisp extends AppCompatActivity {
    Button gb, camr, del, saes,gueb,saveches;
    public String fieldValue;
    ImageView fullscreen;
    EditText guub;
    Intent revert = new Intent(Fullscreendisp.this, DashBoardActivity.class);
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(),imageu;
    GoogleSignInAccount tga;
    GoogleSignInOptions gso;
    GoogleSignInClient googleSignInClient;
    int cufg=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreendisp);
        Window window =getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.newpurp));
        fullscreen = findViewById(R.id.fullScreenImageView);
        imageu = getIntent().getStringExtra("imageUri");
        String utype = getIntent().getStringExtra("stat");
        saes=findViewById(R.id.sc);
        saes.setVisibility(View.GONE);
        gb = findViewById(R.id.bb);
        camr = findViewById(R.id.cam);
        del = findViewById((R.id.delet));
        fullscreen.setDrawingCacheEnabled(true);
        fullscreen.buildDrawingCache();
        guub=findViewById(R.id.gu);
        gueb=findViewById(R.id.gebfu);
        guub.setVisibility(View.GONE);
        gueb.setVisibility(View.GONE);
        saveches=findViewById(R.id.savech);
        saveches.setVisibility(View.GONE);
        if("fupload".equals(utype)){
            if (imageu != null) {
                Glide.with(Fullscreendisp.this)
                        .load(imageu)
                        .into(fullscreen);
                fieldValue=imageu;
            } else {
                fullscreen.setImageResource(R.drawable.baseline_account_circle_24);
                fieldValue=null;
            }

        } else if ("gupload".equals(utype)) {
            tga = getIntent().getParcelableExtra("gogacc");
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .setAccountName(tga.getEmail())
                    .build();
            googleSignInClient = GoogleSignIn.getClient(Fullscreendisp.this, gso);
            camr.setVisibility(View.GONE);
            del.setVisibility(View.GONE);
            saes.setVisibility(View.VISIBLE);
            guub.setVisibility(View.VISIBLE);
            gueb.setVisibility(View.VISIBLE);
            guub.setEnabled(false);
            fieldValue = tga.getPhotoUrl().toString();
            Glide.with(Fullscreendisp.this)
                    .load(fieldValue)
                    .into(fullscreen);
            guub.setText(tga.getDisplayName());
            saes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cufg++;
                    Bitmap b2 = fullscreen.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    b2.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    byte[] dta1 = baos.toByteArray();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference imageRef = storageRef.child("students/" + uid + "/profile_pic.jpg");
                    Map<String,Object> students = new HashMap<>();
                    students.put("Username",guub.getText().toString());
                    revert.putExtra("chuname",guub.getText().toString());
                    db.collection("students").document(uid).update(students);
                    UploadTask uploadTask = imageRef.putBytes(dta1);
                    uploadtheaction(uploadTask,imageRef,db);
                }
            });
            gueb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gueb.setVisibility(View.GONE);
                    saveches.setVisibility(View.VISIBLE);
                    guub.setEnabled(true);
                    saveches.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            guub.setEnabled(false);
                            saveches.setVisibility(View.GONE);
                            gueb.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });

        }
        camr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(Fullscreendisp.this)
                        .cropSquare()                    //Crop image(Optional), Check Customization for more option
                        .compress(800)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
        gb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tga!=null){
                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Fullscreendisp.this, "Google sync aborted", Toast.LENGTH_SHORT).show();
                            revert.putExtra("savedornot",tga);
                            setResult(RESULT_OK,revert);
                        }
                    });
                }

                finish();
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable imageViewDrawable = fullscreen.getDrawable();
                Drawable desiredDrawable = ContextCompat.getDrawable(Fullscreendisp.this, R.drawable.baseline_account_circle_24);
                if (imageViewDrawable.getConstantState().equals(desiredDrawable.getConstantState())) {
                    Toast.makeText(Fullscreendisp.this, "No Profile Picture", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference documentRef = FirebaseFirestore.getInstance().collection("students").document(uid);
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("Profile Pic", FieldValue.delete());
                    documentRef.update(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    String filePath = "students/" + uid + "/profile_pic.jpg"; // Path to the file you want to delete
                                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                    StorageReference fileRef = storageRef.child(filePath);

                                    fileRef.delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(Fullscreendisp.this, "Profile Pic deleted", Toast.LENGTH_SHORT).show();
                                                    Glide.with(Fullscreendisp.this)
                                                            .load(R.drawable.baseline_account_circle_24)
                                                            .into(fullscreen);
                                                    revert.putExtra("updates", "image_deleted");
                                                    setResult(RESULT_OK, revert);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Failed to delete the file
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to delete the field
                                }
                            });

                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            Uri uri = data.getData();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("students/" + uid + "/profile_pic.jpg");
            UploadTask uploadTask = imageRef.putFile(uri);
            uploadtheaction(uploadTask,imageRef,db);
        }
    }

    private void uploadtheaction(UploadTask uploadTask, StorageReference imageRef, FirebaseFirestore db) {
        Map<String,Object> students = new HashMap<>();
        ProgressDialog progressDialog = new ProgressDialog(Fullscreendisp.this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setProgress((int) progress);
            }
        });

// Listen for upload success or failure
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image upload successful
                progressDialog.dismiss();
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        String imageUrl = downloadUri.toString();
                        fieldValue = imageUrl;
                        students.put("Profile Pic", imageUrl);
                        db.collection("students")
                                .document(uid) // Use user's UID as document ID
                                .update(students)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Fullscreendisp.this, "Firestore error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        database.getReference().child("students").child(uid).child("ProfPic").setValue(fieldValue);
                        Glide.with(Fullscreendisp.this)
                                .load(fieldValue)
                                .into(fullscreen);
                        if(cufg>0)
                            revert.putExtra("savedornot",tga);
                        revert.putExtra("updatedI", fieldValue);
                        revert.putExtra("updates", "image_updated");
                        setResult(RESULT_OK, revert);
                        if(cufg>0){
                            Toast.makeText(Fullscreendisp.this, "Google account sync successfull", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Image upload failed
                progressDialog.dismiss();
            }
        });
    }
}