package com.example.lilja.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.lilja.inventoryapp.data.ItemsContract.ItemsEntry;


/**
 * Created by lilja on 7/25/17.
 */

public class ItemsCursorAdapter extends CursorAdapter {

    public ItemsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        Button saleButton = (Button) view.findViewById(R.id.sell_button);

        // Find the columns of attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ItemsEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ItemsEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ItemsEntry.COLUMN_PRODUCT_QUANTITY);
        int idColumnIndex = cursor.getColumnIndex(ItemsEntry._ID);

        // Read the  attributes from the Cursor for the current item
        String itemName = cursor.getString(nameColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        final int itemQuantity = cursor.getInt(quantityColumnIndex);
        final int itemId = cursor.getInt(idColumnIndex);

        // Update the TextViews with the attributes for the current item
        nameTextView.setText(itemName);
        priceTextView.setText(itemPrice);
        quantityTextView.setText(context.getString(R.string.quantity_setting_text) + itemQuantity);

        saleButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (itemQuantity > 0) {
                    int tempQuantity = itemQuantity;
                    tempQuantity--;
                    ContentValues values = new ContentValues();
                    values.put(ItemsEntry.COLUMN_PRODUCT_QUANTITY, tempQuantity);
                    Uri currentItemUri = ContentUris.withAppendedId(ItemsEntry.CONTENT_URI, itemId);

                    // create the Uri of the current product
                    context.getContentResolver().update(currentItemUri, values, null, null);
                }
            }
        });
    }
}
