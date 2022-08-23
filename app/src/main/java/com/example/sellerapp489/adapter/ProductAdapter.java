package com.example.sellerapp489.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sellerapp489.R;
import com.example.sellerapp489.activity.AddProductActivity;
import com.example.sellerapp489.model.Product;
import com.example.sellerapp489.model.Seller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    //Variables
    Dialog editDialog;

    List<Product> productList;
    FragmentActivity fragmentActivity;
    Context context;

    Seller seller;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference favouriteRef = FirebaseDatabase.getInstance("https://buyerapp489-default-rtdb.firebaseio.com/").getReference().child("Users");

    public ProductAdapter(List<Product> productList, FragmentActivity fragmentActivity) {
        this.productList = productList;
        this.fragmentActivity = fragmentActivity;
    }

    public ProductAdapter(List<Product> productList, Context context, Seller seller) {
        this.productList = productList;
        this.context = context;
        this.seller = seller;
    }

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.product_view_container,parent,false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(productList.get(position).getProduct_name());
        holder.price.setText(productList.get(position).getProduct_price());
        holder.minimumOrder.setText(productList.get(position).getMinimum_order());
        // holder.orginalPrice.setText(productList.get(position).getDiscount());
//         Glide.with(context)
//                 .load(productList.get(position).getProduct_image())
//                 .placeholder(R.drawable.img_loading)
//                 .centerCrop()
//                 .into(holder.picture);

        //Image
//        if ()
//        List<String> imageBitmap = productList.get(position).getImageBitmap();
        if (productList.get(position).getProductImageArray()!=null) {
            Glide.with(context)
                    .load(productList.get(position).getProductImageArray().img0)
                    .placeholder(R.drawable.img_loading)
                    .centerCrop()
                    .into(holder.picture);
            Log.d("TESTING", "onDataChange: "+productList.get(position).getProductImageArray().img0);
        }

        //On Click For edit
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(v.getRootView().getContext());
                editDialog.setMessage("You can edit product.");
                editDialog.setTitle("Edit product?");
                editDialog.setIcon(R.drawable.img_edit_product);

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                editDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, AddProductActivity.class);
//                         List<Product> product = new ArrayList<>();
//                         product.add(productList.get(position));
                        intent.putExtra("product", productList.get(position)).putExtra("SELLER", seller);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                editDialog.show();

//                 Intent intent = new Intent(context, ProductDescriptionActivity.class);
//                 intent.putExtra("product", productList.get(position));
//                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                 context.startActivity(intent);
            }
        });

        //Shop Name
        holder.shop_name_text.setText(productList.get(position).getProduct_shop());

        //Discount
        if (productList.get(position).getDiscount().equals("0")){
            holder.discountLayout.setVisibility(View.GONE);
        }else {
            holder.discountLayout.setVisibility(View.VISIBLE);
            holder.discountText.setText(productList.get(position).getDiscount()+"% OFF");
        }

        //Rating
        String rating = productList.get(position).getReview();
        //holder.rating_txt.setText("Rating : " + rating + "");
        if (Integer.parseInt(rating)==5){
            holder.star1.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star2.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star3.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star4.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star5.setImageResource(R.drawable.ic_baseline_star_green);
        }else if (Integer.parseInt(rating)>=4){
            holder.star1.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star2.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star3.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star4.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star5.setImageResource(R.drawable.baseline_star_24);
        }else if (Integer.parseInt(rating)>=3){
            holder.star1.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star2.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star3.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star4.setImageResource(R.drawable.baseline_star_24);
            holder.star5.setImageResource(R.drawable.baseline_star_24);
        }else if (Integer.parseInt(rating)>=2){
            holder.star1.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star2.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star3.setImageResource(R.drawable.baseline_star_24);
            holder.star4.setImageResource(R.drawable.baseline_star_24);
            holder.star5.setImageResource(R.drawable.baseline_star_24);
        }else if (Integer.parseInt(rating)>=1){
            holder.star1.setImageResource(R.drawable.ic_baseline_star_green);
            holder.star2.setImageResource(R.drawable.baseline_star_24);
            holder.star3.setImageResource(R.drawable.baseline_star_24);
            holder.star4.setImageResource(R.drawable.baseline_star_24);
            holder.star5.setImageResource(R.drawable.baseline_star_24);
        }else {
            holder.star1.setImageResource(R.drawable.baseline_star_24);
            holder.star2.setImageResource(R.drawable.baseline_star_24);
            holder.star3.setImageResource(R.drawable.baseline_star_24);
            holder.star4.setImageResource(R.drawable.baseline_star_24);
            holder.star5.setImageResource(R.drawable.baseline_star_24);
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView picture, favorite, star1, star2, star3, star4, star5;
        TextView name,price,orginalPrice, discountText, minimumOrder, shop_name_text, rating_txt;
        ConstraintLayout discountLayout;
        ConstraintLayout constraintLayout;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.product_image);
            name = itemView.findViewById(R.id.product_text_name);
            price = itemView.findViewById(R.id.product_text_price);
            orginalPrice = itemView.findViewById(R.id.product_text_original_price);
            discountText = itemView.findViewById(R.id.discount_text_id);
//            favorite = itemView.findViewById(R.id.favorite_button);
            minimumOrder = itemView.findViewById(R.id.minimum_order_text);
            constraintLayout = itemView.findViewById(R.id.constantLayout);
            shop_name_text = itemView.findViewById(R.id.shop_name_text);
            rating_txt = itemView.findViewById(R.id.rating_txt);
            star1 = itemView.findViewById(R.id.star1);
            star2 = itemView.findViewById(R.id.star2);
            star3 = itemView.findViewById(R.id.star3);
            star4 = itemView.findViewById(R.id.star4);
            star5 = itemView.findViewById(R.id.star5);

            //When Discount is Available... [Visibility "VISIBLE"] ... [Default Visibility "GONE"]
            discountLayout = itemView.findViewById(R.id.discount_layout);
        }
    }


}
