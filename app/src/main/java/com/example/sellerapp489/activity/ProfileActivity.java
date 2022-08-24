package com.example.sellerapp489.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sellerapp489.R;
import com.example.sellerapp489.model.Seller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    //Initialize variables
    private TextView etName, etPhone, etEmail, etOwnerName, etAddress, etAddressCity, etAddressState, etPostalCode, txtDivision, editProfile, logout;
    private ImageView profileImage;

    private Seller seller;

    //Initialize Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference userStoreImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Initialize variable
        etName = findViewById(R.id.store_name);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etOwnerName = findViewById(R.id.etOwnerName);
        etAddress = findViewById(R.id.etAddress);
        etAddressCity = findViewById(R.id.etAddressCity);
        etAddressState = findViewById(R.id.etAddressState);
        etPostalCode = findViewById(R.id.etPostalCode);
        profileImage = findViewById(R.id.profile_image);
        txtDivision = findViewById(R.id.spDivision);
        editProfile = findViewById(R.id.edit_button);
        logout = findViewById(R.id.log_out_button);


        //Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance("https://buyerapp489-default-rtdb.firebaseio.com/").getReference().child("Users");
        userStoreImageRef = FirebaseStorage.getInstance().getReference().child("store images");

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetupActivity.class).putExtra("SELLER", seller).putExtra("INTENT_ID", 1);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot newSnapshot = snapshot.child(mAuth.getCurrentUser().getUid());
                seller = newSnapshot.child("Seller").getValue(Seller.class);

                etName.setText(seller.getStoreName()+"");
                etEmail.setText(seller.getEmail()+"");
                etPhone.setText(seller.getPhone()+"");
                etOwnerName.setText(seller.getOwnerName()+"");
                etAddress.setText(seller.getAddress()+"");
                etAddressCity.setText(seller.getCity()+"");
                etAddressState.setText(seller.getState()+"");
                etPostalCode.setText(seller.getPostalCode()+"");
                txtDivision.setText(seller.getDivision()+"");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}