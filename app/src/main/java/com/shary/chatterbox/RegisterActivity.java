package com.shary.chatterbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shary.chatterbox.util.MyConstant;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText email , password, username;
    Button regbtn;
    DatabaseReference ref;
    FirebaseAuth auth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen);




        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        regbtn = findViewById(R.id.signinbtn);

        auth = FirebaseAuth.getInstance();

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     String txt_username = username.getText().toString();
                     String txt_email = email.getText().toString();
                     String txt_password = password.getText().toString();

                     if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                         Toast.makeText(RegisterActivity.this, "All the Fields are Required",Toast.LENGTH_SHORT).show();
                     } else if(txt_password.length() < 6){
                         Toast.makeText(RegisterActivity.this, "The password should be more than 6 characters",Toast.LENGTH_SHORT).show();
                } else{
                         signup(txt_username , txt_email , txt_password);
                     }
            }
        });

    }
    private void signup(final String username, String email , String password  ){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            ref = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String , String> hashMap = new HashMap<>();
                            hashMap.put(MyConstant.ID, userid );
                            hashMap.put(MyConstant.NAME , username);
                            hashMap.put("imageURL", MyConstant.DEFAULT);

                            ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
