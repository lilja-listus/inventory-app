package com.example.lilja.inventoryapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lilja.inventoryapp.data.ItemsContract.ItemsEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.Integer.parseInt;

/**
 * Created by lilja on 7/25/17.
 */

//allows to create a new item and edit existing

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final int ITEMS_LOADER = 0;
    private static final int PICTURE_GALLERY_REQUEST = 5;
    /**
     * Identifier for the record album image URI loader
     */
    private static final String STATE_PICTURE_URI = "STATE_PICTURE_URI";
    final Context context = this;
    private Uri currentItemUri;
    // EditText field to enter the product name
    private EditText mNameEditText;
    //quantity
    private EditText mQuantitytEditText;
    // price
    private EditText mPricetEditText;
    //details
    private EditText mDetailsEditText;
    // quantity
    private int quantity;
    private ImageView mAddImage;
    private String picturePath;
    private Bitmap picture;
    private Uri pictureUri;

    private Button buttonPlus;
    private Button buttonMinus;
    private Button orderButton;

    private String supplierEmail = "name.lastname@gmail.com";

    // to check whether the item was changed
    private boolean mItemHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (pictureUri != null)
            outState.putString(STATE_PICTURE_URI, pictureUri.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(STATE_PICTURE_URI) &&
                !savedInstanceState.getString(STATE_PICTURE_URI).equals("")) {
            pictureUri = Uri.parse(savedInstanceState.getString(STATE_PICTURE_URI));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        currentItemUri = intent.getData();

        if (currentItemUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));

            // Initialize a loader to read the data from the database
            // and display the current values in the editor

            getLoaderManager().initLoader(ITEMS_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.name_of_product);
        mQuantitytEditText = (EditText) findViewById(R.id.quantity_of_product);
        mPricetEditText = (EditText) findViewById(R.id.price_of_product);
        mDetailsEditText = (EditText) findViewById(R.id.details_of_product);
        mAddImage = (ImageView) findViewById(R.id.edit_item_image_upload);

        //  OnTouchListeners on all the input fields
        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantitytEditText.setOnTouchListener(mTouchListener);
        mPricetEditText.setOnTouchListener(mTouchListener);
        mDetailsEditText.setOnTouchListener(mTouchListener);
        orderButton = (Button) findViewById(R.id.order);
        buttonMinus = (Button) findViewById(R.id.minus);
        buttonPlus = (Button) findViewById(R.id.plus);
        mAddImage.setOnTouchListener(mTouchListener);
        //add image
        mAddImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent openPictureGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();
                Uri data = Uri.parse(pictureDirectoryPath);
                openPictureGallery.setDataAndType(data, "image/*");
                startActivityForResult(openPictureGallery, PICTURE_GALLERY_REQUEST);
            }
        });

        //Order button
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String orderQuantity = mQuantitytEditText.getText().toString().trim();
                if (orderQuantity.length() != 0) {
                    String productName = mNameEditText.getText().toString().trim();

                    String emailAddress = "mailto:" + supplierEmail.toString().trim();
                    String subjectHeader = "Order For: " + productName;
                    String orderMessage = "Please send " + orderQuantity + " units of " + productName + ". " + " \n\n" + "Thank you.";
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse(emailAddress));
                    intent.putExtra(Intent.EXTRA_SUBJECT, subjectHeader);
                    intent.putExtra(Intent.EXTRA_TEXT, orderMessage);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }

                } else {
                    String toastMessage = "Order quantity required";
                    Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastMessage;
                int currentQuantity = 0;
                if (!(mQuantitytEditText.getText().toString()).isEmpty()) {
                    currentQuantity = parseInt(mQuantitytEditText.getText().toString());
                }
                if (currentQuantity >= 1) {
                    currentQuantity--;
                    mQuantitytEditText.setText(String.valueOf(currentQuantity));
                } else {
                    toastMessage = "The amount of items cant be less than one!";
                    mQuantitytEditText.setText(String.valueOf(currentQuantity));
                    Toast.makeText(v.getContext(), toastMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
        buttonPlus.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                int currentQuantity = 0;
                if (!(mQuantitytEditText.getText().toString()).isEmpty()) {
                    currentQuantity = parseInt(mQuantitytEditText.getText().toString());
                }
                currentQuantity++;
                mQuantitytEditText.setText(String.valueOf(currentQuantity));
            }
        });

    }

    /**
     * Get user input from editor and save item into database.
     */
    private void saveItem() {
        if (pictureUri != null) {
            String nameString = mNameEditText.getText().toString().trim();
            String quantityString = mQuantitytEditText.getText().toString().trim();
            String priceString = mPricetEditText.getText().toString().trim();
            String detailsString = mDetailsEditText.getText().toString().trim();

            if (!nameString.isEmpty() && !quantityString.isEmpty() &&
                    !priceString.isEmpty() && !detailsString.isEmpty()) {

                // Create a ContentValues object
                ContentValues values = new ContentValues();
                values.put(ItemsEntry.COLUMN_PRODUCT_NAME, nameString);
                int quantity = 0;
                if (!TextUtils.isEmpty(quantityString)) {
                    quantity = parseInt(quantityString);
                }
                values.put(ItemsEntry.COLUMN_PRODUCT_QUANTITY, quantity);

                int price = 0;
                if (!TextUtils.isEmpty(priceString)) {
                    price = parseInt(priceString);
                }
                values.put(ItemsEntry.COLUMN_PRODUCT_PRICE, price);
                values.put(ItemsEntry.COLUMN_ITEM_IMAGE, picturePath);
                values.put(ItemsEntry.COLUMN_PRODUCT_DETAILS, detailsString);


                // to determine if this is a new or existing item
                if (currentItemUri == null) {
                    Uri newUri = getContentResolver().insert(ItemsEntry.CONTENT_URI, values);

                    if (newUri == null) {
                        Toast.makeText(this, getString(R.string.editor_insert_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.editor_insert_successful),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {

                    int rowsAffected = getContentResolver().update(currentItemUri, values, null, null);

                    //to show the toast message whether it was successful
                    if (rowsAffected == 0) {
                        Toast.makeText(this, getString(R.string.editor_update_failed),
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(this, getString(R.string.editor_update_successful), Toast.LENGTH_SHORT);
                    }
                }
            } else {
                Toast.makeText(context, R.string.give_full_info, Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(context, R.string.give_full_info, Toast.LENGTH_SHORT).show();
            return;

        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //to upload picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == PICTURE_GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                try {
                    pictureUri = resultData.getData();
                    int takeFlags = resultData.getFlags();
                    takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    picturePath = pictureUri.toString();
                    InputStream inputStream;
                    inputStream = getContentResolver().openInputStream(pictureUri);
                    picture = BitmapFactory.decodeStream(inputStream);
                    mAddImage.setImageBitmap(picture);
                    picturePath = pictureUri.toString();
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            getContentResolver().takePersistableUriPermission(pictureUri, takeFlags);
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    mAddImage.setImageBitmap(getBitmapFromUri(pictureUri, context, mAddImage));

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(EditorActivity.this, "Can not open this image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public Bitmap getBitmapFromUri(Uri uri, Context context, ImageView imageView) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            if (input != null)
                input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            Bitmap.createScaledBitmap(bitmap, 88, 88, false);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }


    /**
     * method when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // Show a dialog that warns the user there are unsaved changes that will be lost
    //if they continue leaving the editor.

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this item.
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the item in the database.
     */
    private void deleteItem() {
        if (currentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(currentItemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ItemsEntry._ID,
                ItemsEntry.COLUMN_PRODUCT_NAME,
                ItemsEntry.COLUMN_PRODUCT_QUANTITY,
                ItemsEntry.COLUMN_PRODUCT_PRICE,
                ItemsEntry.COLUMN_PRODUCT_DETAILS,
                ItemsEntry.COLUMN_ITEM_IMAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                currentItemUri,         // Query the content URI for the current item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        ViewTreeObserver viewTreeObserver = mAddImage.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mAddImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mAddImage.setImageBitmap(getBitmapFromUri(pictureUri, context, mAddImage));
                }
            }
        });

        // Proceed with moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ItemsEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ItemsEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ItemsEntry.COLUMN_PRODUCT_PRICE);
            int pictureColumnIndex = cursor.getColumnIndex(ItemsEntry.COLUMN_ITEM_IMAGE);
            int detailsColumnIndex = cursor.getColumnIndex(ItemsEntry.COLUMN_PRODUCT_DETAILS);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String details = cursor.getString(detailsColumnIndex);
            String stringUri = cursor.getString(pictureColumnIndex);
            Uri uriData = Uri.parse(stringUri);


            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mQuantitytEditText.setText(Integer.toString(quantity));
            mPricetEditText.setText(Integer.toString(price));
            mDetailsEditText.setText(details);
            pictureUri = uriData;
            picturePath = stringUri;
            if (pictureUri.toString().contains("drawable"))
                mAddImage.setImageURI(uriData);
            else {
                Bitmap bM = getBitmapFromUri(pictureUri, context, mAddImage);
                mAddImage.setImageBitmap(bM);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mQuantitytEditText.setText("0");
        mPricetEditText.setText("0");
        mDetailsEditText.setText("");
        mAddImage.setImageResource(R.drawable.ic_action_name);
    }
}