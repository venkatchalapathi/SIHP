package com.example.sihp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sihp.R;
import com.example.sihp.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText email, password, re_enterPassword,mobile,u_name;
    private FirebaseAuth mAuth;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("SIHP");
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_password);
        re_enterPassword = findViewById(R.id.reenter_password);
        mobile = findViewById(R.id.reg_phone);
        u_name = findViewById(R.id.reg_name);

    }

    public void registerButton(View view) {
        if (Utils.isNetworkAvailable(this)) {
            if (!TextUtils.isEmpty(u_name.getText().toString())) {
                if (!TextUtils.isEmpty(email.getText().toString())) {
                    if (!TextUtils.isEmpty(mobile.getText().toString())) {
                        if (password.getText().toString().equals(re_enterPassword.getText().toString())) {
                            if (!TextUtils.isEmpty(password.getText().toString())){
                                registerUser(u_name.getText().toString(),email.getText().toString(),mobile.getText().toString(),password.getText().toString());
                            }else {
                                password.setError("Enter Password");
                            }
                            //registerUser(email.getText().toString(), password.getText().toString());
                        }
                    } else {
                        re_enterPassword.setError("password doesn't match");
                    }
                } else {
                    password.setError("Please E-mail id");
                }
            } else {
                email.setError("Please enter User name");
            }
        }else {
            showAlert();
        }
    }
    public void showAlert() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Please check your Internet connection...");
        builder.setTitle("No internet Access,Connection Failed");
        builder.setPositiveButton("Goto Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(
                        Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        setContentView(R.layout.error_loading_screen);
        ImageView imageView = findViewById(R.id.errorimg);
        Glide.with(RegisterActivity.this)
                .load(R.drawable.anim).into(imageView);
        builder.show();
    }
    private void registerUser(final String name, final String email, final String mobile, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            saveUserDataToDB(name,email,mobile);
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }
                    }
                });

    }

    private void saveUserDataToDB(String name, String email, String mobile) {
        Map<String,Object> user = new HashMap<>();
        user.put("name",name);
        user.put("email",email);
        user.put("mobile",mobile);

        reference.child("Users").push().setValue(user);
        Toast.makeText(this, "Data Saved!", Toast.LENGTH_SHORT).show();

    }
    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
