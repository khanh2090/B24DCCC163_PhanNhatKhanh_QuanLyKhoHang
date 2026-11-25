package com.example.quanlykhohang;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

/** Màn hình 3: nhập và lưu sản phẩm mới vào SQLite. */
public class AddProductActivity extends AppCompatActivity {
    private EditText edtName, edtPrice, edtQuantity, edtDescription;
    private Spinner spCategory;
    private SwitchCompat swActive;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = new DatabaseHelper(this);
        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtDescription = findViewById(R.id.edtDescription);
        spCategory = findViewById(R.id.spCategory);
        swActive = findViewById(R.id.swActive);

        findViewById(R.id.btnBackTop).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        if (name.isEmpty()) {
            edtName.setError(getString(R.string.error_name_empty));
            edtName.requestFocus();
            return;
        }
        long price = parsePositiveOrNegativeOne(edtPrice.getText().toString().trim());
        if (price <= 0) {
            edtPrice.setError(getString(R.string.error_price_invalid));
            edtPrice.requestFocus();
            return;
        }
        long quantity = parsePositiveOrNegativeOne(edtQuantity.getText().toString().trim());
        if (quantity < 0 || quantity > Integer.MAX_VALUE) {
            edtQuantity.setError(getString(R.string.error_quantity_invalid));
            edtQuantity.requestFocus();
            return;
        }

        Product p = new Product(name, price, (int) quantity,
                spCategory.getSelectedItem().toString(), swActive.isChecked(),
                edtDescription.getText().toString().trim());
        if (db.insertProduct(p) == -1) {
            Toast.makeText(this, R.string.error_save_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, getString(R.string.toast_saved, name), Toast.LENGTH_SHORT).show();
        finish(); // MainActivity.onResume tải lại danh sách và thống kê
    }

    /** Chuỗi số nguyên không âm; trả về -1 nếu rỗng hoặc không hợp lệ (kể cả quá lớn). */
    private static long parsePositiveOrNegativeOne(String s) {
        if (s.isEmpty()) return -1;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
