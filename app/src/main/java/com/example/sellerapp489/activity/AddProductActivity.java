package com.example.sellerapp489.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sellerapp489.R;
import com.example.sellerapp489.model.Location;
import com.example.sellerapp489.model.Product;
import com.example.sellerapp489.model.ProductImageArray;
import com.example.sellerapp489.model.Seller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    //Initialize variables
    private EditText etName, etDescription, etVideo, etPrice, etMinOrder, etPolicy, etMrp, etDiscount;
    private TextView txtAdd, txtTop;
    private ImageView productImage, productImage1, productImage2;
    private Button btSubmit;
    private ProgressDialog loadingBar;
    private ProgressBar progressBar;
    private Spinner spArea, spCategory, spSubCategory, spStock;
    private String stEmail = "";
    private String sellerShopName, productImageSaved;
    //    private List<String> imageBitmap;
//    private List<String> productImageSavedArray;
    ProductImageArray productImageArray;

    String KEY = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();;

    Product product;

    private Seller seller;

    //Initialize Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference productImageRef;
    private String currentUserId;
    private String currentUserEmail;

    final static int GALLERY_PICK = 1;
    final static int GALLERY_PICK1 = 2;
    final static int GALLERY_PICK2 = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        //Get Store Name
        Intent intent = getIntent();
        sellerShopName = intent.getStringExtra("name");

        //Initialize variable
        init();

        seller = (Seller) getIntent().getSerializableExtra("SELLER");

        //Initialize Firebase
        initFirebase();

        //Product from edit
        product = (Product) getIntent().getSerializableExtra("product");
        if (product!=null){

            KEY = product.getProduct_key();

            if (product.getProductImageArray()!=null) {
                Glide.with(getApplicationContext())
                        .load(product.getProductImageArray().img0)
                        .placeholder(R.drawable.img_loading)
                        .centerCrop()
                        .into(productImage);
            }

            etName.setText(product.getProduct_name());
            etPrice.setText(product.getProduct_price()+"");
            if (product.getMrp()!=null) {
                etMrp.setText(product.getMrp() + "");
            }
            etMinOrder.setText(product.getMinimum_order());
            etDiscount.setText(product.getDiscount());
            etDescription.setText(product.getProduct_description());
            etPolicy.setText(product.getPolicy());
            if (product.getProductVideo()!=null) {
                etVideo.setText(product.getProductVideo());
            }

            productImageArray = product.getProductImageArray();

            txtTop.setText("Edit Product");
            txtAdd.setText("Edit Product Image");

        }

        //Set up Spinner
        spArea = findViewById(R.id.spDivision);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.division_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spArea.setAdapter(adapter);

        spStock = findViewById(R.id.spStock);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.stock_array, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStock.setAdapter(adapter4);

        spCategory = findViewById(R.id.spCategory);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter2);
        //Spinner Selected Listener
        spCategorySelectedListener();

        spSubCategory = findViewById(R.id.spSubCategory);

        onClick();

    }

    private void onClick(){
        //Submit Data
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });

        //Choose Image
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK);
            }
        });
        productImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productImageArray.img0!=null) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_PICK1);
                }
            }
        });
        productImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productImageArray.img1!=null) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_PICK2);
                }
            }
        });
    }

    private void init(){
        txtAdd = findViewById(R.id.txtAdd);
        txtTop = findViewById(R.id.top_txt);

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etPolicy = findViewById(R.id.etPolicy);
        etMrp = findViewById(R.id.etMrp);
        etDiscount = findViewById(R.id.etDiscount);
        etVideo = findViewById(R.id.etVideo);
        btSubmit = findViewById(R.id.btSubmit);
        productImage = findViewById(R.id.product_image );
        productImage1 = findViewById(R.id.product_image1 );
        productImage2 = findViewById(R.id.product_image2 );
        etPrice = findViewById(R.id.etPrice );
        etMinOrder = findViewById(R.id.etMinimum );
        loadingBar = new ProgressDialog(this);
        loadingBar.setCancelable(false);
        progressBar = findViewById(R.id.progressbar);
//        imageBitmap = new ArrayList<>();
        productImageArray = new ProductImageArray();
    }

    private void initFirebase(){
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        currentUserEmail = mAuth.getCurrentUser().getEmail();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        productImageRef = FirebaseStorage.getInstance().getReference().child("product images").child(mAuth.getCurrentUser().getUid());
    }

    private void spCategorySelectedListener() {
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<CharSequence> adapter3;
                switch (position) {
                    case 1:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.apparel_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 2:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.bag_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 3:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.electronics_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 4:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.mobile_accessories_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 5:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.industrial_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 6:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.electrical_equipment_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 7:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.auto_transportation_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 8:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.gift_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 9:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.health_beauty_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 10:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.home_construction_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 11:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.lights_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 12:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.chemicals_rubber_plastics_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 13:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.packaging_office_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 14:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.medicine_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                    case 15:
                        findViewById(R.id.subCategoryLayout).setVisibility(View.VISIBLE);
                        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.fmcg_sub_category_array, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSubCategory.setAdapter(adapter3);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void saveUserInfo() {
        String name = etName.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String video = etVideo.getText().toString().trim();
        String minOrder = etMinOrder.getText().toString().trim();
        String area = spArea.getSelectedItem().toString().trim();
        String category = spCategory.getSelectedItem().toString().trim();
        String subCategory = spSubCategory.getSelectedItem().toString().trim();
        String stock = spStock.getSelectedItem().toString().trim();
        String policy = etPolicy.getText().toString().trim();
        String mrp = etMrp.getText().toString().trim();
        String discount = etDiscount.getText().toString().trim();


        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(minOrder) || TextUtils.isEmpty(price) || TextUtils.isEmpty(policy) || TextUtils.isEmpty(policy)){
            Toast.makeText(this, "Fill Up All", Toast.LENGTH_SHORT).show();
        }else if(area.equals("Select Area")){
            Toast.makeText(this, "Select Area", Toast.LENGTH_SHORT).show();
        }else if(category.equals("Select Category")){
            Toast.makeText(this, "Select Category", Toast.LENGTH_SHORT).show();
        }else if(subCategory.equals("Select Subcategory")){
            Toast.makeText(this, "Select Subcategory", Toast.LENGTH_SHORT).show();
        }else if(stock.equals("Select Stock")){
            Toast.makeText(this, "Select Stock", Toast.LENGTH_SHORT).show();
        }
        else if(productImageArray.img0==null){
            Toast.makeText(this, "Insert Image", Toast.LENGTH_SHORT).show();
        }
        else{
            //ProgressBar
            progressBar.setVisibility(View.VISIBLE);
            btSubmit.setVisibility(View.GONE);

            //Location Set
            String division = seller.getDivision();
            Location location = new Location();
            switch (division) {
                case "Dhaka":
                    location.setDhaka(true);
                    break;
                case "Chittagong":
                    location.setChittagong(true);
                    break;
                case "Rajshahi":
                    location.setRajshahi(true);
                    break;
                case "Khulna":
                    location.setKhulna(true);
                    break;
                case "Barisal":
                    location.setBarisal(true);
                    break;
                case "Sylhet":
                    location.setSylhet(true);
                    break;
                case "Rangpur":
                    location.setRangpur(true);
                    break;
                case "Mymensingh":
                    location.setMymensingh(true);
                    break;
            }

            //Save User Data
            HashMap userMap = new HashMap();
            userMap.put("product_name", name);
            userMap.put("product_price", price);
            userMap.put("product_quantity", "0");
            userMap.put("product_shop", sellerShopName);
            userMap.put("minimum_order", minOrder);
            //userMap.put("product_image", productImageSaved);
            userMap.put("productImageArray", productImageArray);
            userMap.put("product_category", category);
            userMap.put("product_description", description);
            userMap.put("isFeatured", false);
            userMap.put("product_key", KEY);
            userMap.put("discount", discount+"");
            userMap.put("location", location);
            //userMap.put("imageBitmap", imageBitmap);

            userMap.put("subCategory", subCategory);
            userMap.put("review", "0");
            userMap.put("policy", policy);
            userMap.put("view", "0");
            userMap.put("stock", stock);

            if (TextUtils.isEmpty(mrp)) {
                userMap.put("mrp", mrp);
            }

            userMap.put("sellerId", mAuth.getCurrentUser().getUid());
            if (TextUtils.isEmpty(video)){}else {userMap.put("productVideo", video);}
            userRef.child("Products").child(KEY).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){

                        //Save Data In Main Product
                        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
                        productRef.child(KEY).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    //Save Done
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    progressBar.setVisibility(View.GONE);
                                    btSubmit.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    }else {
                        Toast.makeText(AddProductActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        btSubmit.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image 0
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data!=null ){

//            Uri imageUri = data.getData();
//            InputStream inputStream = null;
//            try {
//                inputStream = getContentResolver().openInputStream(imageUri);
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                productImage.setImageBitmap(bitmap);
//                String encodedImage = encodeImage(bitmap);
//                imageBitmap.add(encodedImage);
//            } catch (FileNotFoundException exception) {
//                exception.printStackTrace();
//            }

            Uri imageUri = data.getData();

            loadingBar.setTitle("Profile Image");
            loadingBar.setMessage("Please wait, while we updating your product image...");
            loadingBar.show();

            final StorageReference filepath = productImageRef.child(KEY+".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){

                        //get Image Uri
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //get Image Uri
                                //productImage = uri.toString();
                                productImage.setImageURI(imageUri);
                                //Array
                                productImageArray.img0 = uri.toString();
                                //productImage1.setVisibility(View.VISIBLE);


//                                userRef.child("Products").child(KEY).child("product_image").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()){
//                                            productImage.setImageURI(imageUri);
//                                            loadingBar.dismiss();
//                                        }
//                                        else {
//                                            loadingBar.dismiss();
//                                        }
//                                    }
//                                });
                                loadingBar.dismiss();
                            }
                        });

                    }else{
                        loadingBar.dismiss();
                        Toast.makeText(AddProductActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //Image 1
        if (requestCode == GALLERY_PICK1 && resultCode == RESULT_OK && data!=null ) {
            Uri imageUri = data.getData();

            loadingBar.setTitle("Profile Image");
            loadingBar.setMessage("Please wait, while we updating your product image...");
            loadingBar.show();

            final StorageReference filepath = productImageRef.child(KEY+".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){

                        //get Image Uri
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //get Image Uri
                                //productImageSaved = uri.toString();
                                productImage1.setImageURI(imageUri);
                                //Array
                                productImageArray.img1 = uri.toString();
                                productImage2.setVisibility(View.VISIBLE);
                                loadingBar.dismiss();
                            }
                        });

                    }else{
                        loadingBar.dismiss();
                        Toast.makeText(AddProductActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //Image 2
        if (requestCode == GALLERY_PICK2 && resultCode == RESULT_OK && data!=null ) {
            Uri imageUri = data.getData();

            loadingBar.setTitle("Profile Image");
            loadingBar.setMessage("Please wait, while we updating your product image...");
            loadingBar.show();

            final StorageReference filepath = productImageRef.child(KEY+".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){

                        //get Image Uri
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //get Image Uri
                                //productImageSaved = uri.toString();
                                productImage2.setImageURI(imageUri);
                                //Array
                                productImageArray.img2 = uri.toString();
                                loadingBar.dismiss();
                            }
                        });

                    }else{
                        loadingBar.dismiss();
                        Toast.makeText(AddProductActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

//        if (requestCode == GALLERY_PICK1 && resultCode == RESULT_OK && data!=null ) {
//
//            Uri imageUri = data.getData();
//            InputStream inputStream = null;
//            try {
//                inputStream = getContentResolver().openInputStream(imageUri);
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                productImage1.setImageBitmap(bitmap);
//                String encodedImage = encodeImage(bitmap);
//                imageBitmap.add(encodedImage);
//            } catch (FileNotFoundException exception) {
//                exception.printStackTrace();
//            }
//        }
//
//        if (requestCode == GALLERY_PICK2 && resultCode == RESULT_OK && data!=null ) {
//
//            Uri imageUri = data.getData();
//            InputStream inputStream = null;
//            try {
//                inputStream = getContentResolver().openInputStream(imageUri);
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                productImage2.setImageBitmap(bitmap);
//                String encodedImage = encodeImage(bitmap);
//                imageBitmap.add(encodedImage);
//            } catch (FileNotFoundException exception) {
//                exception.printStackTrace();
//            }
//        }
    }


    ///////////////////////////////////////////////////////// Image Bitmap ///////////////////////////////////

//    private String encodeImage(Bitmap bitmap){
//        int previewWidth = 150;
//        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
//        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
//        byte[] bytes = byteArrayOutputStream.toByteArray();
//        return Base64.encodeToString(bytes, Base64.DEFAULT);
//    }



//    private  final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getData() != null){
//                    Uri imageUri = result.getData().getData();
//                    try{
//                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
//                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        binding.profileImage.setImageBitmap(bitmap);
//                        encodeImage = encodeImage(bitmap);
//                        updateProfile();
//                    }catch (FileNotFoundException e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//    );


}