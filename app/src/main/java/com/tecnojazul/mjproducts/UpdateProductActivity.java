package com.tecnojazul.mjproducts;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tecnojazul.mjproducts.Utilities.ClsBarcodeInfo;
import com.tecnojazul.mjproducts.Utilities.ClsSqLite;

public class UpdateProductActivity extends AppCompatActivity {

    public static final int UPDATE_REQUEST_CODE = 100;

    private TextView _txtBarcode, _txtDescription, _txtPrice;
    private SQLiteDatabase _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        _txtBarcode = findViewById(R.id.SpTxtBarcode);
        _txtDescription = findViewById(R.id.SpTxtDescription);
        _txtPrice = findViewById(R.id.SpTxtPrice);

        ClsSqLite sqLiteHelper = new ClsSqLite(this);
        _db = sqLiteHelper.getWritableDatabase();

        _txtBarcode.setText(ClsBarcodeInfo.Barcode);
        _txtDescription.setText(ClsBarcodeInfo.Description);
        _txtPrice.setText(ClsBarcodeInfo.Price);

        _txtBarcode.requestFocus(_txtBarcode.length());
    }

    public void ScanBarcode(View view) {
        startActivityForResult(new Intent(getApplicationContext(), ScanBarcodeActivity.class), UPDATE_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_new_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        final int itemId = item.getItemId();
        final String barcode = _txtBarcode.getText().toString();
        final String description = _txtDescription.getText().toString();
        final String price = _txtPrice.getText().toString();

        if (itemId == R.id.AnSave) {
            if (!barcode.isEmpty() && !description.isEmpty() && !price.isEmpty()) {
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Sure you want to update")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Define 'where' part of query.
                                String selection = "p_barcode LIKE ?";
                                // Specify arguments in placeholder order.
                                String[] selectionArgs = {ClsBarcodeInfo.Barcode};

                                ContentValues values = new ContentValues();
                                values.put("p_barcode", barcode);
                                values.put("p_description", description);
                                values.put("p_price", price);

                                ClsBarcodeInfo.Barcode = barcode;
                                ClsBarcodeInfo.Description = description;
                                ClsBarcodeInfo.Price = price;

                                // Issue SQL statement.
                                int updatedRow = _db.update("products", values, selection, selectionArgs);
                                _txtBarcode.requestFocus(_txtBarcode.length());
                                Toast.makeText(UpdateProductActivity.this, "Row updated: " + updatedRow, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                Toast.makeText(UpdateProductActivity.this, "Process canceled", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.show();
            } else {
                Toast.makeText(this, "Please complete all requirements", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ScanBarcodeActivity.GetBarcode = true;

        if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            _txtBarcode.setText(data.getStringExtra("ScanBarcode"));
            _txtBarcode.requestFocus(_txtBarcode.length());
        }
    }

    @Override
    protected void onDestroy() {
        if (_db != null) {
            _db.close();
        }
        super.onDestroy();
    }
}
