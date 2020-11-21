package com.tecnojazul.mjproducts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;
import com.tecnojazul.mjproducts.Utilities.ClsSqLite;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanBarcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public static Boolean GetBarcode;

    private ZXingScannerView _scannerView;
    private String _barcodeResult;
    private SQLiteDatabase _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        _scannerView = new ZXingScannerView(this);
        setContentView(_scannerView);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, 1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    @Override
    public void handleResult(Result result) {
        _barcodeResult = result.getText();

        ClsSqLite sqLiteHelper = new ClsSqLite(this);
        _db = sqLiteHelper.getWritableDatabase();

        String sql = "SELECT * FROM products WHERE p_barcode LIKE '" + _barcodeResult + "'";
        Cursor row = _db.rawQuery(sql, null);

        if (row.moveToNext()) {
            Intent intent = new Intent(getApplicationContext(), ViewProductActivity.class);
            intent.putExtra("Barcode", _barcodeResult);
            intent.putExtra("Description", row.getString(1));
            intent.putExtra("Price", row.getString(2));
            startActivity(intent);
        } else {
            if (GetBarcode) {
                GetBarcode = false;
                Intent intent = new Intent();
                intent.putExtra("Barcode", _barcodeResult);
                setResult(RESULT_OK, intent);
                onBackPressed();
            } else {
                Toast.makeText(this, "Barcode don't exist", Toast.LENGTH_SHORT).show();
                _scannerView.setResultHandler(this);
                _scannerView.startCamera();
            }
        }

        row.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        _scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        _scannerView.setResultHandler(this);
        _scannerView.startCamera();
    }
}
