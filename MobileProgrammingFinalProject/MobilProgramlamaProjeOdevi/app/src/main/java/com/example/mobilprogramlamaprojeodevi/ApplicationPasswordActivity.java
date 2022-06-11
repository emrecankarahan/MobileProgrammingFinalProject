package com.example.mobilprogramlamaprojeodevi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mobilprogramlamaprojeodevi.databinding.ActivityApplicationPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class ApplicationPasswordActivity extends AppCompatActivity {
    ActivityApplicationPasswordBinding  binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String userId;
    private String userPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApplicationPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = auth.getUid();


        firebaseFirestore.collection("applicationpassword").whereEqualTo("userid",userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(ApplicationPasswordActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                if(value != null){
                    for(DocumentSnapshot snapshot: value.getDocuments()){
                        Map<String,Object> data = snapshot.getData();
                        userId = (String) data.get("userid");
                        userPassword = (String) data.get("userpassword");
                        binding.buttonLogIn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(binding.applicationpassword.getText().toString().equals(userPassword)){
                                    Intent intent = new Intent(ApplicationPasswordActivity.this,DiaryActivity.class);
                                    Toast.makeText(ApplicationPasswordActivity.this, "Successfully Completed!", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);

                                }else{
                                    Toast.makeText(ApplicationPasswordActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

            }
        });




    }
}