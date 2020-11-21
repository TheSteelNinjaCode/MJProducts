package com.tecnojazul.mjproducts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tecnojazul.mjproducts.Utilities.ClsBarcodeInfo;
import com.tecnojazul.mjproducts.Utilities.ClsSqLite;

import java.util.ArrayList;

public class ViewProductActivity extends AppCompatActivity {

    private SQLiteDatabase _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        ClsSqLite sqLiteHelper = new ClsSqLite(this);
        _db = sqLiteHelper.getWritableDatabase();

        LoadProductInfo();
    }

    private void LoadProductInfo() {
        String productInfo =
                "Barcode: " + ClsBarcodeInfo.Barcode +
                        "\nDescription: " + ClsBarcodeInfo.Description +
                        "\nPrice: " + ClsBarcodeInfo.Price;
        ((TextView) findViewById(R.id.VpTxtView)).setText(productInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        final int itemId = item.getItemId();

        if (itemId == R.id.sm_delete) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Sure you want to delete")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Define 'where' part of query.
                            String selection = "p_barcode LIKE ?";
                            // Specify arguments in placeholder order.
                            String[] selectionArgs = {ClsBarcodeInfo.Barcode};
                            // Issue SQL statement.
                            int deletedRow = _db.delete("products", selection, selectionArgs);
                            Toast.makeText(ViewProductActivity.this, "Deleted row: " + deletedRow, Toast.LENGTH_SHORT).show();
                            ((TextView) findViewById(R.id.VpTxtView)).setText("");
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            Toast.makeText(ViewProductActivity.this, "Process canceled", Toast.LENGTH_SHORT).show();
                        }
                    });
            builder.show();

        } else if (itemId == R.id.sm_update) {
            Intent intent = new Intent(getApplicationContext(), UpdateProductActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadProductInfo();
    }
}