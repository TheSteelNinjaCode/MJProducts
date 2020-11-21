package com.tecnojazul.mjproducts;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tecnojazul.mjproducts.Utilities.ClsSqLite;

public class AddNewActivity extends AppCompatActivity {

    public static final int ADD_NEW_REQUEST_CODE = 200;
    private TextView _txtBarcode, _txtDescription, _txtPrice;
    private SQLiteDatabase _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        _txtBarcode = findViewById(R.id.TxtBarcode);
        _txtDescription = findViewById(R.id.TxtDescription);
        _txtPrice = findViewById(R.id.TxtPrice);

        _txtBarcode.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_new_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.AnSave) {
            String barcode = _txtBarcode.getText().toString();
            String description = _txtDescription.getText().toString();
            String price = _txtPrice.getText().toString();

            if (!barcode.isEmpty() && !description.isEmpty() && !price.isEmpty()) {

                // Gets the data repository in write mode
                ClsSqLite sqLiteHelper = new ClsSqLite(this);
                _db = sqLiteHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put("p_barcode", barcode);
                values.put("p_description", description);
                values.put("p_price", price);

                _db.insert("products", null, values);

                _txtBarcode.setText("");
                _txtDescription.setText("");
                _txtPrice.setText("");

                Toast.makeText(this, "Save success", Toast.LENGTH_SHORT).show();
                _txtBarcode.requestFocus();
            } else {
                Toast.makeText(this, "Complete all inputs it's a requirement", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void ScanBarcode(View view) {
        ScanBarcodeActivity.GetBarcode = true;
        startActivityForResult(new Intent(getApplicationContext(), ScanBarcodeActivity.class), ADD_NEW_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NEW_REQUEST_CODE && resultCode == RESULT_OK) {
            _txtBarcode.setText(data.getStringExtra("Barcode"));
            _txtDescription.requestFocus();
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
