package com.tecnojazul.mjproducts.Utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ClsSqLite extends SQLiteOpenHelper {

    public ClsSqLite(@Nullable Context context) {
        super(context, "mj_products", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE products(p_barcode text PRIMARY KEY, p_description text, p_price real)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
