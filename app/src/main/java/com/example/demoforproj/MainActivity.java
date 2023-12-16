package com.example.demoforproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationview;
    Toolbar toolbar;
    ImageView map;
    FirebaseAuth auth;

    private int previousSelectedItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth=FirebaseAuth.getInstance();



        //-----for Image Slider------//
        ImageSlider imageSlider=findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.p, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.p1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.p2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.p3, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels,ScaleTypes.FIT);

        //------toolbar--------------//
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        //---------navigation drawer---------//
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationview=findViewById(R.id.nav_view);
        Menu menu = navigationview.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_home);
        menuItem.setChecked(true);
        Window window =getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.newpurp));
        navigationview.bringToFront();
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(Color.BLACK);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationview.setNavigationItemSelectedListener(this);


        //--------google map------------------//
        map= findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
//        database.getReference().child("notice").child(auth.getCurrentUser().getUid()).child("ProfPic").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Glide.with(MainActivity.this)
//                            .load(Uri.parse(snapshot.getValue().toString()))
//                            .apply(RequestOptions.circleCropTransform())
//                            .into(profpic);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        MenuItem profileMenuItem = toolbar.getMenu().findItem(R.id.profile);
//        profileMenuItem.setActionView(R.layout.menu_item_profile);
//        ImageView profileImageView = profileMenuItem.getActionView().findViewById(R.id.profileImageView);
//        CollectionReference collectionReference = db.collection("students");
//        collectionReference.document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot docproSnapshot = task.getResult();
//                    // Documents with the specified field value were found
//                    String dispnotprof = docproSnapshot.getString("Profile Pic");
//                    Glide.with(MainActivity.this)
//                            .load(dispnotprof)
//                            .apply(RequestOptions.circleCropTransform())
//                            .into(profpic);
//                } else {
//                    // An error occurred while executing the query
//                    Log.d("DisplayNotice", "Error executing query: " + task.getException());
//                    Toast.makeText(MainActivity.this, "Error loading image", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


    }

    private void openMap() {
        Uri uri = Uri.parse("geo:0, 0?q=RV institute of technology and management");
        Intent intent= new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);


    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        switch (item.getItemId()){
            case R.id.classroom:
                    startActivity(new Intent(MainActivity.this,Classroom.class));
                break;
            case R.id.navPost_notice:



                startActivity(new Intent(MainActivity.this,TestActivity.class));
                break;

            case R.id.navDisplay_notice:


                startActivity(new Intent(MainActivity.this,DisplayNotice.class));
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
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
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
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        // Get a reference to the menu item you want to customize
        MenuItem itemWithImage = menu.findItem(R.id.action_with_image);

        // Access the ImageView within your custom layout
        View actionView = itemWithImage.getActionView();
        ImageView imageView = actionView.findViewById(R.id.imageView);
        TextView slidingTextView = actionView.findViewById(R.id.slidingTextView);
        database.getReference().child("students").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    String imguri = snapshot.child("ProfPic").getValue(String.class);
                    Glide.with(MainActivity.this)
                            .load(Uri.parse(imguri))
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageView);
                    String[] profuri = snapshot.child("Username").getValue(String.class).split(" ");
                    slidingTextView.setText("Welcome, "+profuri[0]);
                }catch (Exception e){
                    String[] profuri = snapshot.child("Username").getValue(String.class).split(" ");
                    slidingTextView.setText("Welcome, "+profuri[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set a click listener for the ImageView if needed
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                startActivity(new Intent(MainActivity.this,DashBoardActivity.class));
            }
        });
        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                slidingTextView.setVisibility(View.VISIBLE);
                ValueAnimator animator = ValueAnimator.ofFloat(imageView.getLeft()-slidingTextView.getLeft(), imageView.getRight()-slidingTextView.getRight());
                animator.setDuration(1000); // Adjust the duration as needed
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        slidingTextView.setTranslationX(value);
                    }
                });

                animator.start();
            }
        }, 1000);
        return true;
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.profile) {
//            startActivity(new Intent(MainActivity.this,DashBoardActivity.class));
//
//        }
//        return super.onOptionsItemSelected(item);
//    }



}