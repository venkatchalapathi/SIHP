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

public class RegisterActivity extends AppCompatActivity {

    EditText email, password, re_enterPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_password);
        re_enterPassword = findViewById(R.id.reenter_password);
    }

    public void registerButton(View view) {
        if (Utils.isNetworkAvailable(this)) {
            if (!TextUtils.isEmpty(email.getText().toString())) {
                if (!TextUtils.isEmpty(password.getText().toString())) {
                    if (!TextUtils.isEmpty(re_enterPassword.getText().toString())) {
                        if (password.getText().toString().equals(re_enterPassword.getText().toString())) {
                            registerUser(email.getText().toString(), password.getText().toString());
                        }
                    } else {
                        re_enterPassword.setError("Please fill all fields..");
                    }
                } else {
                    password.setError("Please enter password");
                }
            } else {
                email.setError("Please enter Email Id");
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
    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
