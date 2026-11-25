package com.example.quanlykhohang;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/** Màn hình 4: chi tiết sản phẩm, nhập thêm / bán / xóa. */
public class ProductDetailActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private Product product;
    private TextView tvName, tvCategory, tvPrice, tvQuantity, tvLowStock, tvStockValue,
            tvStatus, tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = new DatabaseHelper(this);
        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvLowStock = findViewById(R.id.tvLowStock);
        tvStockValue = findViewById(R.id.tvStockValue);
        tvStatus = findViewById(R.id.tvStatus);
        tvDescription = findViewById(R.id.tvDescription);

        // Nhận id qua Intent rồi truy vấn lại cơ sở dữ liệu
        int id = getIntent().getIntExtra(MainActivity.EXTRA_PRODUCT_ID, -1);
        product = db.getProductById(id);
        if (product == null) {
            Toast.makeText(this, R.string.error_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        findViewById(R.id.btnBackTop).setOnClickListener(v -> finish());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnImport).setOnClickListener(v -> changeQuantity(+1));
        findViewById(R.id.btnSell).setOnClickListener(v -> {
            if (product.getQuantity() == 0) {
                Toast.makeText(this, R.string.toast_out_of_stock, Toast.LENGTH_SHORT).show();
            } else {
                changeQuantity(-1);
            }
        });
        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            db.deleteProduct(product.getId());
            Toast.makeText(this, getString(R.string.toast_deleted, product.getName()),
                    Toast.LENGTH_SHORT).show();
            finish();
        });
        showProduct();
    }

    private void changeQuantity(int delta) {
        int newQuantity = product.getQuantity() + delta;
        db.updateQuantity(product.getId(), newQuantity);
        product = db.getProductById(product.getId());
        showProduct();
    }

    private void showProduct() {
        tvName.setText(product.getName());
        tvCategory.setText(product.getCategory());
        tvPrice.setText(Product.formatMoney(product.getPrice()));
        tvQuantity.setText(String.valueOf(product.getQuantity()));
        tvLowStock.setVisibility(product.isLowStock() ? View.VISIBLE : View.GONE);
        tvStockValue.setText(Product.formatMoney(product.getStockValue()));
        tvStatus.setText(product.isActive() ? R.string.status_active : R.string.status_inactive);
        String desc = product.getDescription();
        tvDescription.setText(desc == null || desc.isEmpty() ? getString(R.string.no_description) : desc);
    }
}
