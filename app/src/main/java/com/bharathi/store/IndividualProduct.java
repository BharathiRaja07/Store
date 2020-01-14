package com.bharathi.store;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.bharathi.store.models.GenericProductModel;
import com.bharathi.store.models.SingleProductModel;
import com.bharathi.store.network.CheckInternetConnection;
import com.bharathi.store.usersession.UserSession;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class IndividualProduct extends AppCompatActivity {


    ImageView productimage;
    TextView productname;
    TextView productprice;
    TextView addToCart;
    TextView buyNow;
    TextView productdesc;
    EditText quantityProductPage;
    LottieAnimationView addToWishlist;
    EditText customheader;
    EditText custommessage;

    private String usermobile, useremail;
    private UserSession session;

    private int quantity = 1;
    private GenericProductModel model;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_product);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        initializeUI();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initialize();

    }

    private void initialize() {
        model = (GenericProductModel) getIntent().getSerializableExtra("product");

        productprice.setText("₹ " + Float.toString(model.getCardprice()));

        productname.setText(model.getCardname());
        productdesc.setText(model.getCarddiscription());
        quantityProductPage.setText("1");
        Picasso.with(IndividualProduct.this).load(model.getCardimage()).into(productimage);

        //SharedPreference for Cart Value
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();
        usermobile = session.getUserDetails().get(UserSession.KEY_MOBiLE);
        useremail = session.getUserDetails().get(UserSession.KEY_EMAIL);

        //setting textwatcher for no of items field
        quantityProductPage.addTextChangedListener(productcount);

        //get firebase instance
        //initializing database reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void Notifications(View view) {
        startActivity(new Intent(IndividualProduct.this, NotificationActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void shareProduct(View view) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Found amazing " + productname.getText().toString() + "on Magic Prints App";
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void similarProduct(View view) {
        finish();
    }

    private SingleProductModel getProductObject() {

        return new SingleProductModel(model.getCardid(), Integer.parseInt(quantityProductPage.getText().toString()), useremail, usermobile, model.getCardname(), Float.toString(model.getCardprice()), model.getCardimage(), model.carddiscription,customheader.getText().toString(),custommessage.getText().toString());

    }

    public void decrement(View view) {
        if (quantity > 1) {
            quantity--;
            quantityProductPage.setText(String.valueOf(quantity));
        }
    }

    public void increment(View view) {
        if (quantity < 500) {
            quantity++;
            quantityProductPage.setText(String.valueOf(quantity));
        } else {
            Toast.makeText(IndividualProduct.this, "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
        }
    }

    //check that product count must not exceed 500
    TextWatcher productcount = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (quantityProductPage.getText().toString().equals("")) {
                quantityProductPage.setText("0");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //none
            if (Integer.parseInt(quantityProductPage.getText().toString()) >= 500) {
                Toast.makeText(IndividualProduct.this, "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    public void addToCart(View view) {

        if ( customheader.getText().toString().length() == 0 ||  custommessage.getText().toString().length() ==0 ){

            Snackbar.make(view, "Header or Message Empty", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else{

            mDatabaseReference.child("cart").child(usermobile).push().setValue(getProductObject());
            session.increaseCartValue();
            //Log.e("Cart Value IP", session.getCartValue() + " ");
            Toast.makeText(IndividualProduct.this, "Added to Cart", Toast.LENGTH_SHORT).show();
        }
    }

    public void addToWishList(View view) {

        addToWishlist.playAnimation();
        mDatabaseReference.child("wishlist").child(usermobile).push().setValue(getProductObject());
        session.increaseWishlistValue();
    }

    public void goToCart(View view) {

        if ( customheader.getText().toString().length() == 0 ||  custommessage.getText().toString().length() ==0 ){

            Snackbar.make(view, "Header or Message Empty", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else {
            mDatabaseReference.child("cart").child(usermobile).push().setValue(getProductObject());
            session.increaseCartValue();
            startActivity(new Intent(IndividualProduct.this, Cart.class));
            finish();
        }
    }

    private void initializeUI(){
        productimage = findViewById(R.id.productimage);
        productname = findViewById((R.id.productname));
        addToCart = findViewById((R.id.add_to_cart));
        productdesc = findViewById((R.id.productdesc));
        buyNow = findViewById((R.id.buy_now));
        productprice = findViewById((R.id.productprice));
        quantityProductPage = findViewById((R.id.quantityProductPage));
        addToWishlist = findViewById((R.id.add_to_wishlist));
        customheader = findViewById((R.id.customheader));
        custommessage = findViewById((R.id.custommessage));


    }
}