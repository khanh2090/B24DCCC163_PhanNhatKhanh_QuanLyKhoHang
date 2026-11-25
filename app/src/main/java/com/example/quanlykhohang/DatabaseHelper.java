package com.example.quanlykhohang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/** Quản lý cơ sở dữ liệu warehouse.db gồm bảng users và products. */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "warehouse.db";
    private static final int DB_VERSION = 1;

    private static final String T_USERS = "users";
    private static final String T_PRODUCTS = "products";

    private static final String C_ID = "id";
    private static final String C_USERNAME = "username";
    private static final String C_PASSWORD = "password";
    private static final String C_NAME = "name";
    private static final String C_PRICE = "price";
    private static final String C_QUANTITY = "quantity";
    private static final String C_CATEGORY = "category";
    private static final String C_ACTIVE = "active";
    private static final String C_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_USERS + " ("
                + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_USERNAME + " TEXT NOT NULL UNIQUE, "
                + C_PASSWORD + " TEXT NOT NULL)");
        db.execSQL("CREATE TABLE " + T_PRODUCTS + " ("
                + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_NAME + " TEXT NOT NULL, "
                + C_PRICE + " INTEGER NOT NULL, "
                + C_QUANTITY + " INTEGER NOT NULL, "
                + C_CATEGORY + " TEXT NOT NULL, "
                + C_ACTIVE + " INTEGER NOT NULL, "
                + C_DESCRIPTION + " TEXT)");

        // Dữ liệu mẫu: chèn qua chính db đang tạo (không gọi getWritableDatabase trong onCreate)
        insertUser(db, "admin", "123456");
        insertProduct(db, new Product("Chuột không dây", 150000, 20, "Điện tử", true,
                "Chuột không dây kết nối USB 2.4GHz"));
        insertProduct(db, new Product("Bàn phím cơ", 450000, 3, "Điện tử", true,
                "Bàn phím cơ switch xanh, có đèn nền"));
        insertProduct(db, new Product("Sổ tay A5", 25000, 40, "Văn phòng phẩm", true,
                "Sổ tay khổ A5, 200 trang"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + T_PRODUCTS);
        onCreate(db);
    }

    // ---------- users ----------

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(T_USERS, new String[]{C_ID},
                C_USERNAME + " = ? AND " + C_PASSWORD + " = ?",
                new String[]{username, password}, null, null, null)) {
            return c.moveToFirst();
        }
    }

    public long insertUser(String username, String password) {
        return insertUser(getWritableDatabase(), username, password);
    }

    private long insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues v = new ContentValues();
        v.put(C_USERNAME, username);
        v.put(C_PASSWORD, password);
        return db.insert(T_USERS, null, v);
    }

    // ---------- products ----------

    /** Thêm sản phẩm, trả về id vừa thêm (-1 nếu lỗi). */
    public long insertProduct(Product p) {
        return insertProduct(getWritableDatabase(), p);
    }

    private long insertProduct(SQLiteDatabase db, Product p) {
        ContentValues v = new ContentValues();
        v.put(C_NAME, p.getName());
        v.put(C_PRICE, p.getPrice());
        v.put(C_QUANTITY, p.getQuantity());
        v.put(C_CATEGORY, p.getCategory());
        v.put(C_ACTIVE, p.isActive() ? 1 : 0);
        v.put(C_DESCRIPTION, p.getDescription());
        return db.insert(T_PRODUCTS, null, v);
    }

    /** Lấy toàn bộ sản phẩm; danh sách rỗng nếu kho trống. */
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(T_PRODUCTS, null, null, null, null, null, C_ID + " ASC")) {
            while (c.moveToNext()) {
                list.add(readProduct(c));
            }
        }
        return list;
    }

    /** Lấy một sản phẩm theo id, null nếu không có. */
    public Product getProductById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(T_PRODUCTS, null, C_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? readProduct(c) : null;
        }
    }

    public int updateQuantity(int id, int quantity) {
        ContentValues v = new ContentValues();
        v.put(C_QUANTITY, quantity);
        return getWritableDatabase().update(T_PRODUCTS, v, C_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public int deleteProduct(int id) {
        return getWritableDatabase().delete(T_PRODUCTS, C_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public int countProducts() {
        return queryLong("SELECT COUNT(*) FROM " + T_PRODUCTS, null).intValue();
    }

    /** Tổng giá trị kho: SUM(price * quantity). */
    public long getTotalValue() {
        return queryLong("SELECT IFNULL(SUM(" + C_PRICE + " * " + C_QUANTITY + "), 0) FROM "
                + T_PRODUCTS, null);
    }

    /** Số sản phẩm sắp hết hàng (quantity < 5). */
    public int countLowStock() {
        return queryLong("SELECT COUNT(*) FROM " + T_PRODUCTS + " WHERE " + C_QUANTITY + " < ?",
                new String[]{String.valueOf(Product.LOW_STOCK_LIMIT)}).intValue();
    }

    private Long queryLong(String sql, String[] args) {
        try (Cursor c = getReadableDatabase().rawQuery(sql, args)) {
            return c.moveToFirst() ? c.getLong(0) : 0L;
        }
    }

    private Product readProduct(Cursor c) {
        return new Product(
                c.getInt(c.getColumnIndexOrThrow(C_ID)),
                c.getString(c.getColumnIndexOrThrow(C_NAME)),
                c.getLong(c.getColumnIndexOrThrow(C_PRICE)),
                c.getInt(c.getColumnIndexOrThrow(C_QUANTITY)),
                c.getString(c.getColumnIndexOrThrow(C_CATEGORY)),
                c.getInt(c.getColumnIndexOrThrow(C_ACTIVE)) == 1,
                c.getString(c.getColumnIndexOrThrow(C_DESCRIPTION)));
    }
}
