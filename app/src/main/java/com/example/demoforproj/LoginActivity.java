package com.example.demoforproj;

import static com.example.demoforproj.SignUpActivity.SHARED_PREFS1;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEmail, loginPassword;
    String finalbranch;
    private Button loginbutton;
    ArrayList<String> dropdown = new ArrayList<>();
    AutoCompleteTextView autoCompleteTextView,usn;
    private TextView signupRedirectView;
    public static String SHARED_PREFS = "sharedPrefs";
    String USN;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPreferences sp;
    LinearLayout linv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Window window =getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.newpurp));
        linv=findViewById(R.id.linv);
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_psswd);
        loginbutton = findViewById(R.id.login_button);
        signupRedirectView=findViewById(R.id.signup_redirectText);
        autoCompleteTextView=findViewById(R.id.desig);
        usn=findViewById(R.id.usn);
        dropdown.add("Professor");
        dropdown.add("Assistant Professor");
        dropdown.add("Associate Professor");
        dropdown.add("Community/Club");
        String pattern = ".*(_cs|_is|_ec|_me|bis|bcs|bec|bms).*";
        Pattern regex= Pattern.compile(pattern);
        sp=getSharedPreferences("sharedPrefs",0);
        String emtocheck = sp.getString("emaildesig","");
        Matcher matcher1 = regex.matcher(emtocheck);
        if(!matcher1.matches()){
            usn.setVisibility(View.GONE);
            autoCompleteTextView.setVisibility(View.VISIBLE);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line,dropdown);
            autoCompleteTextView.setAdapter(arrayAdapter);
            autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoCompleteTextView.showDropDown();
                    autoCompleteTextView.setDropDownVerticalOffset(-40);
                }
            });
            autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        autoCompleteTextView.showDropDown();
                        autoCompleteTextView.setDropDownVerticalOffset(-40);
                    }
                }
            });
        }

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linv.setVisibility(View.VISIBLE);
                if(autoCompleteTextView.isShown()){
                    int flagtocheck=0;
                    String desigfordb=autoCompleteTextView.getText().toString();
                    for(String dropopt: dropdown)
                        if(desigfordb.equals(dropopt))
                            flagtocheck=1;
                    if(flagtocheck==0||desigfordb.isEmpty()){
                        Toast.makeText(LoginActivity.this, "Please select the designation from provided options", Toast.LENGTH_LONG).show();
                        linv.setVisibility(View.GONE);
                    }
                }else
                    USN = usn.getText().toString();
                String email=loginEmail.getText().toString();
                String psswd=loginPassword.getText().toString();
                Pattern patternbranch = Pattern.compile("_(\\w{2})(\\d{2})");
                Pattern patternbranch1 = Pattern.compile("_rvit(\\d{2})(\\w{3})");
                Matcher branchdecide = patternbranch.matcher(email);
                Matcher branchdecide1 = patternbranch1.matcher(email);
                while (branchdecide.find()) {
                    finalbranch = branchdecide.group(1).toUpperCase() + "E-" + (Integer.valueOf(branchdecide.group(2)) + 4);
                }

                while (branchdecide1.find()) {
                    finalbranch = branchdecide1.group(2).toUpperCase() + "E-" + (Integer.valueOf(branchdecide1.group(1)) + 4);
                }
                if(TextUtils.isEmpty(psswd)){
                    loginPassword.setError("Please enter your password");
                    linv.setVisibility(View.GONE);
                } else if (TextUtils.isEmpty(email)) {
                    loginEmail.setError("Please enter your email");
                    linv.setVisibility(View.GONE);
                }
                else if(usn.isShown() && usn.getText().toString().isEmpty()){
                    usn.setError("Please enter your usn");
                    linv.setVisibility(View.GONE);
                }
                else if(!email.isEmpty()) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(email, psswd).addOnSuccessListener(LoginActivity.this, new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    if(auth.getCurrentUser().isEmailVerified()){
                                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,0);
                                        SharedPreferences sharedPreferences1 = getSharedPreferences(SHARED_PREFS1,0);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("hasLogged",true);
                                        editor.commit();
                                        String semail = null;
                                        if(email.contains("_is")||email.contains("bis"))
                                            semail="Department of ISE";
                                        else if(email.contains("_cs")||email.contains("bcs"))
                                            semail="Department of CSE";
                                        else if(email.contains("_ec")||email.contains("bec"))
                                            semail="Department of ECE";
                                        else if(email.contains("_me")||email.contains("bme"))
                                            semail="Department of ME";
                                        String un = sharedPreferences1.getString("username","hello");
                                        String finalSemail = semail;
                                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task2 -> {
                                            database.getReference().child("Branchwise_batch").child(finalSemail).child(finalbranch).child(usn.getText().toString()).setValue(un);
                                            database.getReference().child("students").child(auth.getCurrentUser().getUid()).child("USN").setValue(USN);
                                            database.getReference().child("students").child(auth.getCurrentUser().getUid()).child("FCMtoken").setValue(task2.getResult());
                                            database.getReference().child("students").child(auth.getCurrentUser().getUid()).child("ProfPic").setValue(null);
                                            database.getReference().child("students").child(auth.getCurrentUser().getUid()).child("Department").setValue(finalSemail);
                                            database.getReference().child("students").child(auth.getCurrentUser().getUid()).child("Username").setValue(un).addOnCompleteListener(task3 ->{
                                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                Intent intentw = new Intent(LoginActivity.this, MainActivity.class);
                                                intentw.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                SharedPreferences.Editor editortorem = sp.edit();
                                                editortorem.remove("emaildesig");
                                                startActivity(intentw);
                                                finish();
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(LoginActivity.this, "Some error occured! Please contact the administrator", Toast.LENGTH_LONG).show();
                                                }
                                            });
////                                        db.collection("students").document(auth.getCurrentUser().getUid()).get()
////                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
////                                                    @Override
////                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
////                                                        if(task.getResult().exists()){}
////                                                        else{
//                                                            String semail = null;
//                                                            if(email.contains("_is")||email.contains("bis"))
//                                                                semail="Department of ISE";
//                                                            else if(email.contains("_cs")||email.contains("bcs"))
//                                                                semail="Department of CSE";
//                                                            else if(email.contains("_ec")||email.contains("bec"))
//                                                                semail="Department of ECE";
//                                                            else if(email.contains("_me")||email.contains("bme"))
//                                                                semail="Department of ME";
//                                                            String un = sharedPreferences1.getString("username","hello");
////                                                            Map<String,Object> students = new HashMap<>();
////                                                            students.put("Username",un);
////                                                            students.put("Department",semail);
//
//
////                                                                db.collection("students")
////                                                                    .document(auth.getCurrentUser().getUid()) // Use user's UID as document ID
////                                                                    .set(students)
////                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
////                                                                        @Override
////                                                                        public void onSuccess(Void unused) {
////                                                                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
////                                                                            Intent intentw = new Intent(LoginActivity.this, MainActivity.class);
////                                                                            intentw.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
////                                                                            startActivity(intentw);
////                                                                            finish();
////                                                                        }
////                                                                    })
////                                                                    .addOnFailureListener(new OnFailureListener() {
////                                                                        @Override
////                                                                        public void onFailure(@NonNull Exception e) {
////                                                                            Toast.makeText(LoginActivity.this, "Firestore error", Toast.LENGTH_SHORT).show();
////                                                                        }
////                                                                    });
//                                                            });
//                                                        }
//                                                    }
//                                                })
//                                                .addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        Toast.makeText(LoginActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                });
                                    });
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this, "Please verify email first", Toast.LENGTH_SHORT).show();
                                        linv.setVisibility(View.GONE);
                                    }
                                }
                            })
                            .addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "Some error occured! Please contact administrator", Toast.LENGTH_LONG).show();
                                }
                            });
                }
                else
                    Toast.makeText(LoginActivity.this, "Please enter details according to instruction", Toast.LENGTH_LONG).show();
                linv.setVisibility(View.GONE);
            }
        });
        signupRedirectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }
}