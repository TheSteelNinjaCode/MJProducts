package com.tecnojazul.mjproducts;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tecnojazul.mjproducts.Utilities.ClsBarcodeInfo;
import com.tecnojazul.mjproducts.Utilities.ClsSqLite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase _db;
    private ListView _productsListView;
    private ArrayAdapter<String> _adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _productsListView = findViewById(R.id.ProductsListView);
        LoadProducts();
    }

    private void LoadProducts() {

        ClsSqLite sqLiteHelper = new ClsSqLite(this);
        _db = sqLiteHelper.getWritableDatabase();

        String sql = "SELECT * FROM products";
        Cursor cursor = _db.rawQuery(sql, null);

        ArrayList<String> productsList = new ArrayList<>();
        final ArrayList<String> codeList = new ArrayList<>();
        final Map<String, String[]> productInfoMap = new HashMap<>();

        while (cursor.moveToNext()) {
            String barcode = cursor.getString(0);
            String description = cursor.getString(1);
            String price = cursor.getString(2);

            String descriptionSub;
            if (description.length() > 20) {
                descriptionSub = description.substring(0, 20) + "...";
            } else {
                descriptionSub = description;
            }

            String formattedString = "Barcode: " + barcode + "\n" +
                    "Description: " + descriptionSub + "\n" +
                    "Price: " + price;

            String[] productInfo = {barcode, description, price};

            productsList.add(formattedString);
            codeList.add(barcode);
            productInfoMap.put(barcode, productInfo);
        }

        cursor.close();
        _db.close();

        _adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productsList);
        _productsListView.setAdapter(_adapter);
        _productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ClsBarcodeInfo.Barcode = productInfoMap.get(codeList.get(position))[0];
                ClsBarcodeInfo.Description = productInfoMap.get(codeList.get(position))[1];
                ClsBarcodeInfo.Price = productInfoMap.get(codeList.get(position))[2];

                Intent intent = new Intent(getApplicationContext(), ViewProductActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.MmSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                _adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        Log.i("MenuItem", "onOptionsItemSelected: " + item);

        if (itemId == R.id.AddNew) {
            startActivity(new Intent(getApplicationContext(), AddNewActivity.class));
        } else if (itemId == R.id.Search) {
            startActivity(new Intent(getApplicationContext(), ScanBarcodeActivity.class));
        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        LoadProducts();
    }

    @Override
    protected void onDestroy() {
        if (_db != null) {
            _db.close();
        }
        super.onDestroy();
    }
}
