package com.example.quanlykhohang;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Một sản phẩm trong kho, ánh xạ một dòng của bảng products. */
public class Product {
    /** Ngưỡng số lượng: dưới mức này coi là sắp hết hàng. */
    public static final int LOW_STOCK_LIMIT = 5;

    private int id;
    private String name;
    private long price;
    private int quantity;
    private String category;
    private boolean active;
    private String description;

    public Product(int id, String name, long price, int quantity,
                   String category, boolean active, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    /** Dùng khi tạo sản phẩm mới, id do SQLite tự sinh. */
    public Product(String name, long price, int quantity,
                   String category, boolean active, String description) {
        this(0, name, price, quantity, category, active, description);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public long getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getCategory() { return category; }
    public boolean isActive() { return active; }
    public String getDescription() { return description; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isLowStock() { return quantity < LOW_STOCK_LIMIT; }

    public long getStockValue() { return price * quantity; }

    /** Định dạng tiền kiểu Việt Nam: 1.350.000 đ */
    public static String formatMoney(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');
        return new DecimalFormat("#,###", symbols).format(amount) + " đ";
    }
}
