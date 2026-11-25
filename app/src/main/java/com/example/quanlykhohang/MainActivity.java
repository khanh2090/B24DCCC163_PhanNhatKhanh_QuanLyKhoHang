package com.example.quanlykhohang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/** Màn hình 2: thống kê kho và danh sách sản phẩm. */
public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_PRODUCT_ID = "product_id";

    private DatabaseHelper db;
    private TextView tvTotal, tvValue, tvLow;
    private ListView lvProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        tvTotal = findViewById(R.id.tvTotal);
        tvValue = findViewById(R.id.tvValue);
        tvLow = findViewById(R.id.tvLow);
        lvProducts = findViewById(R.id.lvProducts);
        lvProducts.setEmptyView(findViewById(R.id.tvEmpty));

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product p = (Product) parent.getItemAtPosition(position);
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra(EXTRA_PRODUCT_ID, p.getId());
            startActivity(intent);
        });
        findViewById(R.id.btnAdd).setOnClickListener(v ->
                startActivity(new Intent(this, AddProductActivity.class)));
    }

    /** Tải lại mỗi khi quay về: sau khi thêm, bán, nhập hoặc xóa. */
    @Override
    protected void onResume() {
        super.onResume();
        List<Product> products = db.getAllProducts();
        lvProducts.setAdapter(new ProductAdapter(this, products));
        tvTotal.setText(String.valueOf(db.countProducts()));
        tvValue.setText(Product.formatMoney(db.getTotalValue()));
        tvLow.setText(String.valueOf(db.countLowStock()));
    }
}
