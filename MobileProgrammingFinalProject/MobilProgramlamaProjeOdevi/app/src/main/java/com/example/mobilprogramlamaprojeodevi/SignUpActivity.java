package com.example.mobilprogramlamaprojeodevi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mobilprogramlamaprojeodevi.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editTextLogInEmail.getText().toString();
                String password = binding.editTextLogInPassword.getText().toString();
                String applicationPassword = binding.editTextLogInPassword2.getText().toString();
                HashMap<String,Object> applicationInformation = new HashMap<>();
                if( email.equals("") || password.equals("") ||applicationPassword.equals("")){
                    Toast.makeText(SignUpActivity.this, "All information must be written", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(SignUpActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setTitle("Sign Up");
                    builder.setMessage("Are you sure to sign up?");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(SignUpActivity.this, "The user is saved.", Toast.LENGTH_SHORT).show();
                                    //System.out.println("created userid: "+ auth.getUid());
                                    applicationInformation.put("userid",auth.getUid());
                                    applicationInformation.put("userpassword",applicationPassword);
                                    firebaseFirestore.collection("applicationpassword").add(applicationInformation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Intent intentToDiary = new Intent(SignUpActivity.this,DiaryActivity.class);
                                            startActivity(intentToDiary);
                                            intentToDiary.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }
}