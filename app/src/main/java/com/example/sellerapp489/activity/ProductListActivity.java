package com.example.sellerapp489.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.sellerapp489.R;
import com.example.sellerapp489.adapter.ProductAdapter;
import com.example.sellerapp489.model.Location;
import com.example.sellerapp489.model.Product;
import com.example.sellerapp489.model.Seller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    //Product RecyclerView /
    private RecyclerView productRecyclerView;
    private List<Product> productList;
    private ProductAdapter productAdapter;

    //Initialize Variables
    private ProgressBar progressBar;

    //database
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference productRef;

    Seller seller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize Variables
        progressBar = findViewById(R.id.progressBar_cat);

        //initializing firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        productRef = firebaseDatabase.getReference("Products");

        seller = (Seller) getIntent().getSerializableExtra("SELLER");

        //Product RecyclerView
        productRecyclerView = findViewById(R.id.product_recyclerview);
        productRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );
        productList = new ArrayList<>();
        //getting products from firebase
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot npsnapshot : snapshot.getChildren()){
                        Product l = npsnapshot.getValue(Product.class);

                        if (npsnapshot.hasChild("location")) {
                            DataSnapshot locationS = npsnapshot.child("location");

                            Location location = locationS.getValue(Location.class);
                            l.setLocation(location);

                            Log.d("LOCATION1", "onDataChange: " + l.getLocation().isMirpur());

                            if (l.getSellerId().equals(mAuth.getCurrentUser().getUid())) {
                                productList.add(l);
//                                Log.d("TESTING", "onDataChange: "+l.getProductImageArray().img0);
                            }
                            //Progress Bar
                            if (progressBar.getVisibility() == View.VISIBLE) {
                                progressBar.setVisibility(View.GONE);

                            }
                        }
                    }
                    productAdapter = new ProductAdapter(productList, getApplicationContext(), seller);
                    productRecyclerView.setAdapter(productAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    //For Back Button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}