package com.example.mobilprogramlamaprojeodevi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mobilprogramlamaprojeodevi.databinding.ActivityDiaryBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DiaryActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Memory> memoryArrayList;
    DiaryAdapter diaryAdapter;
    private ActivityDiaryBinding binding;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.addMemory){
            Intent intentToCreateAMemory = new Intent(DiaryActivity.this,NewDiaryActivity.class);
            startActivity(intentToCreateAMemory);
        }else if(item.getItemId() == R.id.signOut){
            auth.signOut();
            Intent intentToSignOut = new Intent(DiaryActivity.this,MainActivity.class);
            startActivity(intentToSignOut);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiaryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        memoryArrayList = new ArrayList<>();

        getData();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        diaryAdapter = new DiaryAdapter(memoryArrayList);
        binding.recyclerView.setAdapter(diaryAdapter);

    }

    private void getData(){
        //DocumentReference documentReference = firebaseFirestore.collection("Title").document("asdasd")
        //CollectionReference documentReference = firebaseFirestore.collection("Title").document("asdasd")
        System.out.println("Current user: "+ auth.getCurrentUser().getEmail());
        firebaseFirestore.collection("memories").whereEqualTo("useremail",auth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    if(value !=null){
                        for(DocumentSnapshot documentSnapshot: value.getDocuments()){
                            System.out.println("Docid: "+ documentSnapshot.getId());
                            Map<String,Object> data = documentSnapshot.getData();
                            String id = documentSnapshot.getId();
                            String email = (String) data.get("useremail");
                            Timestamp timestamp = (Timestamp) data.get("date");
                            //System.out.println("TS " + timestamp);
                            String date = timestamp.toString();
                            //System.out.println("Date " + date);
                            String emoji = (String) data.get("emoji");
                            String password = (String) data.get("password");
                            String videoURL = (String) data.get("videourl");
                            Double longtitude = (Double) data.get("longitude");
                            Double latitude = (Double) data.get("latitude");
                            String title = (String) data.get("title");
                            String content = (String) data.get("content");
                            String downloadurl = (String) data.get("downloadurl");
                            Memory memory = new Memory(email,content,title,downloadurl,latitude,longtitude,password,date,id,emoji,videoURL);
                            memoryArrayList.add(memory);
                        }
                        diaryAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(DiaryActivity.this, "No memory has been created.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(DiaryActivity.this, "1" + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}