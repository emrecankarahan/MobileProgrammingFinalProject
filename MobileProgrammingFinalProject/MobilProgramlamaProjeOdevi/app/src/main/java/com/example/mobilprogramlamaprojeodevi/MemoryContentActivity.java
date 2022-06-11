package com.example.mobilprogramlamaprojeodevi;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mobilprogramlamaprojeodevi.databinding.ActivityMemoryContentBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class MemoryContentActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Memory memory;
    ActivityMemoryContentBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    private GoogleMap mMap;
    private LocationListener locationListener;
    ActivityResultLauncher<String> permissionLauncher;
    private LocationManager locationManager;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu2,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemoryContentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        memory = (Memory) intent.getSerializableExtra("memory");
        System.out.println("id - name vb." + memory.getMemoryId()+ memory.getTitle());
        Picasso.get().load(memory.getDownloadUrl()).into(binding.imageView);
        binding.textViewContent.setText(memory.getContent());
        binding.textViewTitle.setText(memory.getTitle());
        binding.textViewVideoUri.setText(memory.getVideoURL());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(MemoryContentActivity.this);
        int happy = R.drawable.happy;
        int unhappy = R.drawable.unhappy;
        int emotionless = R.drawable.emotionless;
        int angry = R.drawable.angry;
        if(memory.getEmoji().equals("happy")){
           binding.emojiView.setImageResource(happy);
        }else if(memory.getEmoji().equals("unhappy")){
            binding.emojiView.setImageResource(unhappy);
        }else if(memory.getEmoji().equals("emotionless")){
            binding.emojiView.setImageResource(emotionless);
        }else if(memory.getEmoji().equals("angry")){
            binding.emojiView.setImageResource(angry);
        }
        Double latitude = memory.getLatitude();
        Double longitude = memory.getLongitude();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.updateMemory){
            // Updating
            firebaseFirestore.collection("memories").document(memory.getMemoryId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Intent intentToCreateAMemory = new Intent(MemoryContentActivity.this,NewDiaryActivity.class);
                    intentToCreateAMemory.putExtra("update",memory);
                    System.out.println("Memory Content Acticity: "+ memory.getEmoji() + memory.getMemoryId());
                    startActivity(intentToCreateAMemory);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MemoryContentActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });



        }else if(item.getItemId() == R.id.signOut2){
            auth.signOut();
            Intent intentToSignOut = new Intent(MemoryContentActivity.this,MainActivity.class);
            startActivity(intentToSignOut);
            finish();
        }else if(item.getItemId() == R.id.shareMemory){
            String sharedText = "Look at my memory!\nTitle: "+memory.getTitle()+"\nContent: "+ memory.getContent();
            Intent intentToShare = new Intent(Intent.ACTION_SEND);
            intentToShare.setType("text/plain");
            intentToShare.putExtra(Intent.EXTRA_TEXT,sharedText);
            startActivity(Intent.createChooser(intentToShare,null));
        }else if(item.getItemId() == R.id.createPDF){

            AlertDialog.Builder builder = new AlertDialog.Builder(MemoryContentActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Creating PDF Document");
            builder.setMessage("Are you sure to create the PDF Document?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PdfDocument pdfDocument = new PdfDocument();
                    Paint title = new Paint();
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1100, 800, 1).create();
                    PdfDocument.Page Page = pdfDocument.startPage(pageInfo);
                    Canvas canvas = Page.getCanvas();
                    title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                    title.setTextSize(15);
                    title.setColor(ContextCompat.getColor(MemoryContentActivity.this, R.color.black));
                    canvas.drawText(memory.getTitle() + "\n"+ memory.getContent(), 209, 100, title);
                    title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    pdfDocument.finishPage(Page);
                    pdfDocument.close();
                    Toast.makeText(MemoryContentActivity.this, "PDF Document is created!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MemoryContentActivity.this,DiaryActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else if(item.getItemId() == R.id.deleteMemory){
            // Deleting
            AlertDialog.Builder builder = new AlertDialog.Builder(MemoryContentActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Deleting A New Memory");
            builder.setMessage("Are you sure to delete the new memory?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    firebaseFirestore.collection("memories").document(memory.getMemoryId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MemoryContentActivity.this, "The memory is deleted.", Toast.LENGTH_SHORT).show();
                            Intent intentFromDeleting = new Intent(MemoryContentActivity.this,DiaryActivity.class);
                            startActivity(intentFromDeleting);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MemoryContentActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intentAfterDelete = new Intent(MemoryContentActivity.this,DiaryActivity.class);
                    startActivity(intentAfterDelete);
                    finish();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng turkey = new LatLng(memory.getLatitude(),memory.getLongitude()); // location with latitude and longitude
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(turkey,13)); // zoomed maps focus on the location
        mMap.addMarker(new MarkerOptions().position(turkey));
    }
}