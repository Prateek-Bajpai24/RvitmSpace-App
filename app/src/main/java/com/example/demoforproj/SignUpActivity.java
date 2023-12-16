package com.example.demoforproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {
    private EditText signupEmail,username;

    private TextInputLayout textinplay,textinplayc;
    private TextInputEditText signupPassword, signupConfirmPsswd;
    private Button button;
    private TextView RedirectView;
    public static String SHARED_PREFS1  = "sharedPrefs";
    LinearLayout linv;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Window window =getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.newpurp));
        textinplayc=findViewById(R.id.inplayc);
        textinplay=findViewById(R.id.inplay);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_psswd);
        signupConfirmPsswd = findViewById(R.id.signup_confirmpsswd);
        button = findViewById(R.id.signup_button);
        RedirectView=findViewById(R.id.loginRedir);
        signupConfirmPsswd.setEnabled(false);
        username = findViewById(R.id.user_name);
        linv=findViewById(R.id.linv);
        signupPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pinput = s.toString();
                String cpsswd = signupConfirmPsswd.getText().toString();
                if (pinput.length() >= 8) {
                    Pattern p = Pattern.compile("[^a-zA-Z0-9]");
                    Matcher m = p.matcher(pinput);
                    boolean pm = m.find();
                    if (pm) {
                        textinplay.setHelperText("Password Strong");
                        textinplay.setError("");
                        signupConfirmPsswd.setEnabled(true);
                        if (!cpsswd.isEmpty() && (!pinput.equals(cpsswd)))
                            textinplayc.setError("Passwords do not match");
                        else if (pinput.equals(cpsswd)) {
                            textinplayc.setHelperText("Passwords matched succesfully");
                            textinplayc.setError("");
                        }

                    } else {
                        textinplay.setError("Password too weak");
                        textinplay.setErrorIconDrawable(null);
                        signupConfirmPsswd.setEnabled(false);
                        if (!cpsswd.isEmpty() && (!pinput.equals(cpsswd)))
                            textinplayc.setError("Passwords do not match");
                    }

                } else if (pinput.length() == 0 || pinput.isEmpty()) {
                    textinplay.setHelperText("Enter Password");
                    if (cpsswd.isEmpty() && pinput.isEmpty())
                        textinplayc.setHelperText("Re-enter Password");
                    else if ((!cpsswd.isEmpty()) && pinput.isEmpty())
                        textinplayc.setError("Please enter password above");
                    signupConfirmPsswd.setEnabled(false);
                } else {
                    textinplay.setError("Password should be 8 character long");
                    textinplay.setErrorIconDrawable(null);
                    signupConfirmPsswd.setEnabled(false);
                    if (!cpsswd.isEmpty() && (!pinput.equals(cpsswd)))
                        textinplayc.setError("Passwords do not match");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        signupConfirmPsswd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cinp = s.toString();
                String pswd = signupPassword.getText().toString();
                String cpswd = signupConfirmPsswd.getText().toString();
                if(cinp.length()==0)
                    textinplayc.setHelperText("Re-enter Password");
                else if(cpswd.equals(pswd)){
                    textinplayc.setHelperText("Passwords matched successfully");
                    textinplayc.setError("");
                }else if(!cpswd.isEmpty()&&(!pswd.equals(cpswd))){
                    textinplayc.setError("Passwords do not match");
                    textinplayc.setErrorIconDrawable(null);
                }

            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        button.setOnClickListener(v -> {
            linv.setVisibility(View.VISIBLE);
            String email = signupEmail.getText().toString().trim();
            String psswd = signupPassword.getText().toString().trim();
            String cpsswd = signupConfirmPsswd.getText().toString().trim();
            String usft = username.getText().toString();
            if (email.isEmpty()||psswd.isEmpty()||cpsswd.isEmpty()||textinplay.getError()!=null||textinplayc.getError()!=null||usft.isEmpty()){
                Toast.makeText(SignUpActivity.this, "Please enter credentials as per instruction", Toast.LENGTH_LONG).show();
                linv.setVisibility(View.GONE);
            }
            if(email.endsWith(".rvitm@rvei.edu.in")){
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email, psswd)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(SignUpActivity.this, new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    String uname=username.getText().toString();
                                                    SharedPreferences preferences = getSharedPreferences(SHARED_PREFS1,0);
                                                    SharedPreferences.Editor editor1 = preferences.edit();
                                                    editor1.putString("username",uname);
                                                    editor1.putString("emaildesig",email);
                                                    editor1.commit();
                                                    Toast.makeText(SignUpActivity.this, "Email verification link sent successfully", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(SignUpActivity.this, new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SignUpActivity.this,"Enter a valid college email id",Toast.LENGTH_LONG).show();
                                                    linv.setVisibility(View.GONE);
                                                }
                                            });

                                }else{
                                    Toast.makeText(SignUpActivity.this,"Please enter correct details",Toast.LENGTH_LONG).show();
                                    linv.setVisibility(View.GONE);
                                }
                            }
                        })
                        .addOnFailureListener(SignUpActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this,"Signup failed as the account is already in use",Toast.LENGTH_LONG).show();
                            }
                        });

            }
            else {
                Toast.makeText(SignUpActivity.this, "Enter College Email!", Toast.LENGTH_LONG).show();
                linv.setVisibility(View.GONE);
            }

        });

        RedirectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }
}