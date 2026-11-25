package com.example.quanlykhohang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.List;

/** Hiển thị mỗi sản phẩm thành một thẻ trong ListView. */
public class ProductAdapter extends ArrayAdapter<Product> {

    public ProductAdapter(Context context, List<Product> products) {
        super(context, 0, products);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView != null ? convertView
                : LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
        Product p = getItem(position);
        Context ctx = getContext();

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvQty = view.findViewById(R.id.tvQuantity);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        TextView tvCategory = view.findViewById(R.id.tvCategory);
        TextView tvLow = view.findViewById(R.id.tvLowStock);

        tvName.setText(p.getName());
        tvQty.setText(ctx.getString(R.string.item_qty, p.getQuantity()));
        tvPrice.setText(ctx.getString(R.string.item_price, Product.formatMoney(p.getPrice())));
        tvCategory.setText(p.getCategory());

        boolean low = p.isLowStock();
        tvQty.setTextColor(ContextCompat.getColor(ctx, low ? R.color.error : R.color.text_primary));
        tvLow.setVisibility(low ? View.VISIBLE : View.GONE);
        return view;
    }
}
