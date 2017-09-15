package com.example.lilja.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Inverory app
 */
public final class ItemsContract {

    // uri
    public static final String CONTENT_AUTHORITY = "com.example.lilja.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";
    // the empty constructor
    private ItemsContract() {
    }

    /**
     * Inner class to define values for the database table.
     */
    public static final class ItemsEntry implements BaseColumns {

        // The content URI to access the data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);


        // The MIME type of for a list of items
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        // The MIME type of for one item
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        // Name of database table
        public final static String TABLE_NAME = "items";


        // Unique ID number for one item in the table
        public final static String _ID = BaseColumns._ID;

        // the product name
        public final static String COLUMN_PRODUCT_NAME = "name";

        // Current quantity
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        // Price of the product
        public final static String COLUMN_PRODUCT_PRICE = "price";

        // Details
        public final static String COLUMN_PRODUCT_DETAILS = "details";

        //Image

        public static final String COLUMN_ITEM_IMAGE = "image";

    }

}
