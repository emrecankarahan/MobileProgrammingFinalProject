package com.example.mobilprogramlamaprojeodevi;

import androidx.activity.result.ActivityResult;
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
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.mobilprogramlamaprojeodevi.databinding.ActivityNewDiaryBinding;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

public class NewDiaryActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    Uri imageData;
    Uri videoData;
    private boolean intent;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private GoogleMap mMap;
    private String selectedEmoji;
    private LatLng selectedLatLng;
    private LocationManager locationManager;
    private LocationListener locationListener;
    ActivityNewDiaryBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    ActivityResultLauncher<Intent> activityResultLauncher2;
    ActivityResultLauncher<String> permissionLauncher2;
    private Memory memory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewDiaryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        intent = false;
        registerLauncher();
        videoData = null;
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        registerLauncher();
        registerLauncher2();
        registerLauncher3();

        if(getIntent().hasExtra("update")){
            System.out.println("girdi");
            intent = true;
            memory = (Memory) getIntent().getSerializableExtra("update");
            binding.textViewVideoUri.setText(memory.getVideoURL());
            //Picasso.get().load(memory.getDownloadUrl()).into(binding.imageView);
            binding.editTextTitle.setText(memory.getTitle());
            binding.editTextContent.setText(memory.getContent());
            binding.memoryPassword.setText(memory.getPassword());
            //LatLng point = new LatLng(memory.getLatitude(),memory.getLongitude()); // location with latitude and longitude
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,13)); // zoomed maps focus on the location
            //mMap.addMarker(new MarkerOptions().position(point));
            if(memory.getEmoji().equals("happy")){
                binding.checkboxhappy.setChecked(true);
            }else if(memory.getEmoji().equals("unhappy")){
                binding.checkboxunhappy.setChecked(true);
            }else if(memory.getEmoji().equals("emotionless")){
                binding.checkboxemotionless.setChecked(true);
            }else if(memory.getEmoji().equals("angry")){
                binding.checkboxangry.setChecked(true);
            }
            Toast.makeText(this, memory.getTitle() + memory.getEmoji(), Toast.LENGTH_SHORT).show();
        }else{
            System.out.println("girmedi");
        }


        binding.checkboxangry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.checkboxangry.isChecked()){
                    binding.checkboxhappy.setChecked(false);
                    binding.checkboxunhappy.setChecked(false);
                    binding.checkboxemotionless.setChecked(false);
                    selectedEmoji = "angry";
                }else{
                    selectedEmoji = null;
                }
            }
        });
        binding.checkboxhappy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.checkboxhappy.isChecked()){
                    selectedEmoji = "happy";
                    binding.checkboxemotionless.setChecked(false);
                    binding.checkboxunhappy.setChecked(false);
                    binding.checkboxangry.setChecked(false);
                }else{
                    selectedEmoji = null;
                }
            }
        });
        binding.checkboxunhappy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.checkboxunhappy.isChecked()){
                    selectedEmoji = "unhappy";
                    binding.checkboxemotionless.setChecked(false);
                    binding.checkboxhappy.setChecked(false);
                    binding.checkboxangry.setChecked(false);
                }else{
                    selectedEmoji = null;
                }
            }
        });
        binding.checkboxemotionless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.checkboxemotionless.isChecked()){
                    selectedEmoji = "emotionless";
                    binding.checkboxhappy.setChecked(false);
                    binding.checkboxunhappy.setChecked(false);
                    binding.checkboxangry.setChecked(false);
                }else{
                    selectedEmoji = null;
                }
            }
        });
        binding.buttonCreateNewMemory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(imageData != null && videoData !=null && !binding.editTextContent.equals("") && !binding.editTextTitle.equals("") && !selectedEmoji.equals("") ){
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewDiaryActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Creating A New Memory");
                    builder.setMessage("Are you sure to create a new memory?");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Creating a new memory;
                            UUID uuid = UUID.randomUUID();
                            String imageName = "images/" + uuid + ".jpg";
                            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    StorageReference storageReference1 = firebaseStorage.getReference(imageName);
                                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();
                                            String title = binding.editTextTitle.getText().toString();
                                            String content = binding.editTextContent.getText().toString();
                                            FirebaseUser firebaseUser =  firebaseAuth.getCurrentUser();
                                            String videoUrl = videoData.toString();
                                            Double latitude = selectedLatLng.latitude;
                                            Double longitude = selectedLatLng.longitude;
                                            String memoryPassword = binding.memoryPassword.getText().toString();
                                            String email = firebaseUser.getEmail();
                                            HashMap<String,Object> memory = new HashMap<>();
                                            memory.put("longitude",longitude);
                                            memory.put("latitude",latitude);
                                            memory.put("videourl",videoUrl);
                                            memory.put("useremail",email);
                                            memory.put("downloadurl",downloadUrl);
                                            memory.put("password",memoryPassword);
                                            memory.put("title",title);
                                            memory.put("content",content);
                                            memory.put("emoji",selectedEmoji);
                                            memory.put("date", FieldValue.serverTimestamp());
                                            firebaseFirestore.collection("memories").add(memory).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    // back to diaryActivity (Intent or Finish)
                                                    if(intent){
                                                        Toast.makeText(getApplicationContext(),"The memory is updated.",Toast.LENGTH_SHORT).show();

                                                    }else{
                                                        Toast.makeText(getApplicationContext(),"The memory is created.",Toast.LENGTH_SHORT).show();

                                                    }
                                                    Intent intent = new Intent(NewDiaryActivity.this,DiaryActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // All activities are closed.
                                                    startActivity(intent);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(NewDiaryActivity.this, "1:" +e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(NewDiaryActivity.this,"2:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(NewDiaryActivity.this, "3:" +e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    Toast.makeText(NewDiaryActivity.this, "All information must be written!", Toast.LENGTH_SHORT).show();
                }
            }

        });

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(NewDiaryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(NewDiaryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Snackbar.make(v, "If you add a photo/video, the permission must be granted.", Snackbar.LENGTH_INDEFINITE).setAction("Give the permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // ask the permission from the snackbar
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }).show();
                    }else{
                        // ask permission normally
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }else {
                    // intent to gallery - the permission is OK
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // Go and pick
                    activityResultLauncher.launch(intentToGallery);
                }
            }
        });
        binding.buttonAddAVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(NewDiaryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(NewDiaryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Snackbar.make(v, "If you add a photo/video, the permission must be granted.", Snackbar.LENGTH_INDEFINITE).setAction("Give the permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // ask the permission from the snackbar
                                permissionLauncher2.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }).show();
                    }else{
                        // ask permission normally
                        permissionLauncher2.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }else {
                    // intent to gallery - the permission is OK
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI); // Go and pick
                    activityResultLauncher2.launch(intentToGallery);
                }
            }
        });
    }
    private void registerLauncher(){
        // Resulting and Callbacks
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    // There is no problem = Result OK
                    Intent intentFromResult = result.getData();
                    if(intentFromResult != null){
                        imageData = intentFromResult.getData();
                        binding.imageView.setImageURI(imageData);

                    }
                }
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    // permission is OK
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else{
                    // permission is not OK
                }
            }
        });
    }
    private void registerLauncher3(){
        // Resulting and Callbacks
        activityResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    // There is no problem = Result OK
                    Intent intentFromResult = result.getData();
                    if(intentFromResult != null){
                        videoData = intentFromResult.getData();
                        binding.textViewVideoUri.setText("Video Url has been copied!");

                    }
                }
            }
        });
        permissionLauncher2 = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    // permission is OK
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher2.launch(intentToGallery);
                }else{
                    // permission is not OK
                }
            }
        });
    }

    private void registerLauncher2(){
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //permission granted
                    if(ContextCompat.checkSelfPermission(NewDiaryActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                }else{
                    //permission denied.
                    Toast.makeText(NewDiaryActivity.this, "The maps must be used!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        selectedLatLng = latLng;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                //LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
            }

        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // request permission
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.getRoot(),"Maps must be used!",Snackbar.LENGTH_INDEFINITE).setAction("Give the permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // request permission
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).show();

            }else{

            }
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1000,locationListener);

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1000,locationListener);

        LatLng turkey = new LatLng(41.014916,28.993032); // location with latitude and longitude
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(turkey,13)); // zoomed maps focus on the location

    }
}