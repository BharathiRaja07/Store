package com.bharathi.store;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bharathi.store.network.CheckInternetConnection;


public class OrderPlaced extends AppCompatActivity {

    TextView orderidview;
    private String orderid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        orderidview = findViewById(R.id.orderid);
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        initialize();
    }

    private void initialize() {
        orderid = getIntent().getStringExtra("orderid");
        orderidview.setText(orderid);
    }

    public void finishActivity(View view) {
        finish();
    }
}
