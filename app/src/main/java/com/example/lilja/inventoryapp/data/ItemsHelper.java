package com.example.lilja.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lilja.inventoryapp.data.ItemsContract.ItemsEntry;

/**
 * Created by lilja on 7/25/17.
 */


//Database helper

public class ItemsHelper extends SQLiteOpenHelper {

    // the database name
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    //to construct a new instance
    public ItemsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // String with SQL statement for creating the table
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemsEntry.TABLE_NAME + " ("
                + ItemsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemsEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ItemsEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + ItemsEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + ItemsEntry.COLUMN_ITEM_IMAGE + " TEXT, "
                + ItemsEntry.COLUMN_PRODUCT_DETAILS + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
