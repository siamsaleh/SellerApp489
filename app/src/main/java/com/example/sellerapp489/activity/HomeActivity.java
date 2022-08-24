package com.example.sellerapp489.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sellerapp489.R;
import com.example.sellerapp489.model.Seller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    //Initialize Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    //Initialize Variable
    RelativeLayout btOrders;
    ProgressBar progressBar;

    Seller seller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance("https://buyerapp489-default-rtdb.firebaseio.com/").getReference().child("Users");

        //Initialize variables
        btOrders = findViewById(R.id.btOrders);
        progressBar = findViewById(R.id.progressBar_pro);

        findViewById(R.id.btAddProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, AddProductActivity.class).putExtra("seller", (Parcelable) seller));
                if (seller != null){
                    startActivity(new Intent(HomeActivity.this, AddProductActivity.class).putExtra("name", seller.getStoreName())
                            .putExtra("SELLER", seller));
                }else {
                    Toast.makeText(HomeActivity.this, "Press Again", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, OrdersActivity.class));
            }
        });

        findViewById(R.id.btProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, AddProductActivity.class).putExtra("seller", (Parcelable) seller));
                if (seller != null){
                    startActivity(new Intent(HomeActivity.this, ProductListActivity.class).putExtra("name", seller.getStoreName())
                            .putExtra("SELLER", seller));
                }else {
                    Toast.makeText(HomeActivity.this, "Press Again", Toast.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.btProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, AddProductActivity.class).putExtra("seller", (Parcelable) seller));
                if (seller != null){
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class).putExtra("name", seller.getStoreName()));
                }else {
                    Toast.makeText(HomeActivity.this, "Press Again", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        //User Already Login or not
        if (currentUser == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
            checkUserExistence();
        }

    }

    private void checkUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot sellerSnapshot = snapshot.child(current_user_id);

                if (!sellerSnapshot.hasChild("Seller")){
                    Intent intent = new Intent(getApplicationContext(), SetupActivity.class).putExtra("INTENT_ID", -1);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else {

                    seller = sellerSnapshot.child("Seller").getValue(Seller.class);
                    if (seller.getIsVerified() == 1){
                        progressBar.setVisibility(View.GONE);
                        findViewById(R.id.topIcon).setVisibility(View.VISIBLE);
                    }else{
                        startActivity(new Intent(HomeActivity.this, RegisterActivity.class).putExtra("INTENT_ID", -1));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}