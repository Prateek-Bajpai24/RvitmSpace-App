package com.example.demoforproj;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class BaseActivity extends AppCompatActivity {

    protected StorageReference profilePicRef;
    String uid;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem profilePictureItem = menu.findItem(R.id.profile);
//        ImageView profileImageView = (ImageView) profilePictureItem.getActionView();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
//        db.collection("students").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    String fieldValue;
//                    if (document.contains("Profile Pic")) {
//                        fieldValue = document.getString("Profile Pic");
//                        Glide.with(BaseActivity.this)
//                                .load(fieldValue)
//                                .apply(RequestOptions.circleCropTransform())
//                                .error(R.drawable.baseline_account_circle_24)
//                                .into(profileImageView);
//                    } else {
//
//                    }
//                } else {
//                    Toast.makeText(BaseActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        return super.onPrepareOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int itemId = item.getItemId();
//
//        switch (itemId) {
//            case R.id.menu_profile:
//                // Handle profile menu item click
//                return true;
//            // Handle other menu items
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    // Add any other common functionality or methods here

}
