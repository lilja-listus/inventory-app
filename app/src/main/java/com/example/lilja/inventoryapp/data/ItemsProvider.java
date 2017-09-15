package com.example.lilja.inventoryapp.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.lilja.inventoryapp.data.ItemsContract.ItemsEntry;


/**
 * Created by lilja on 7/25/17.
 */

//Content provider for the app
public class ItemsProvider extends ContentProvider {

    public static final String LOG_TAG = ItemsHelper.class.getSimpleName();

    // URI matcher code for the content URI for the whole table
    private static final int ITEMS = 100;

    // URI matcher code for the content URI for one item
    private static final int ITEMS_ID = 101;

    //UriMatcher object to match a content URI to a corresponding code.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer
    static {
        sUriMatcher.addURI(ItemsContract.CONTENT_AUTHORITY, ItemsContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(ItemsContract.CONTENT_AUTHORITY, ItemsContract.PATH_ITEMS + "/#", ITEMS_ID);
    }

    // Database helper object
    private ItemsHelper helper;

    @Override
    public boolean onCreate() {
        helper = new ItemsHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = helper.getReadableDatabase();

        // Cursor to hold the result of the query
        Cursor cursor;


        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                cursor = database.query(ItemsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ITEMS_ID:
                selection = ItemsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ItemsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //  notification URI on the Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    //Insert a new product item and check whether data is valid
    private Uri insertItem(Uri uri, ContentValues values) {
        String name = values.getAsString(ItemsEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Insert the product name ");
        }

        Integer quantity = values.getAsInteger(ItemsEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Insert valid quantity");
        }

        Integer price = values.getAsInteger(ItemsEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Insert valid price");
        }
        String image = values.getAsString(ItemsEntry.COLUMN_ITEM_IMAGE);

        String details = values.getAsString(ItemsEntry.COLUMN_PRODUCT_DETAILS);
        if (details == null) {
            throw new IllegalArgumentException("Insert details ");
        }

        // Get writeable database
        SQLiteDatabase database = helper.getWritableDatabase();

        // Insert the new product with the given values
        long id = database.insert(ItemsEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // to notify  listeners that the data has changed
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEMS_ID:
                selection = ItemsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    // to update items in the database with the given content values.

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ItemsEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ItemsEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Insert a valid name ");
            }
        }

        if (values.containsKey(ItemsEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ItemsEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Insert valid quantity");
            }
        }

        if (values.containsKey(ItemsEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(ItemsEntry.COLUMN_PRODUCT_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Insert valid price");
            }
        }

        if (values.containsKey(ItemsEntry.COLUMN_PRODUCT_DETAILS)) {
            String details = values.getAsString(ItemsEntry.COLUMN_PRODUCT_DETAILS);
            if (details == null) {
                throw new IllegalArgumentException("Insert valid details ");
            }
        }

        //if there is no new values, no update needed
        if (values.size() == 0) {
            return 0;
        }

        //to update the database getting writable database
        SQLiteDatabase database = helper.getWritableDatabase();

        // update and shows the number of rows updated
        int rowsUpdated = database.update(ItemsEntry.TABLE_NAME, values, selection, selectionArgs);

        // to notify if at least one row is updated
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = helper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ItemsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEMS_ID:
                // Delete a single row given by the ID in the URI
                selection = ItemsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ItemsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        //notify if at least one row is deleted
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return ItemsEntry.CONTENT_LIST_TYPE;
            case ITEMS_ID:
                return ItemsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}