package techhavoc.currencyconverter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/* in brief: this class manages the database of the app. it creates, edits and deletes the currencies. all methods used herein can be reused */

class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "currency_converter.db";
    private static final String TABLE_NAME = "Currencies";
    private static final String TAB_NAME_ONE = "USD";
    private static final String TAB_NAME_ONE_COUNTRY = "United States - Dollar";
    private static final String TAB_NAME_TWO = "EURO";
    private static final String TAB_NAME_TWO_COUNTRY = "Europe - Euro";
    private static final String TAB_NAME_THREE = "BPD";
    private static final String TAB_NAME_THREE_COUNTRY = "United Kingdom - Pound";
    private static final String TAB_NAME_FOUR = "UGX";
    private static final String TAB_NAME_FOUR_COUNTRY = "Uganda - Shilling";
    private static final String TAB_NAME_FIVE = "RWF";
    private static final String TAB_NAME_FIVE_COUNTRY = "Rwanda - Franc";
    private static final String TAB_NAME_SIX = "KSH";
    private static final String TAB_NAME_SIX_COUNTRY = "Kenya - Shilling";
    private static final String TAB_NAME_SEVEN = "TSH";
    private static final String TAB_NAME_SEVEN_COUNTRY = "Tanzania - Shilling";
    private static final String TAB_NAME_EIGHT = "MXN";
    private static final String TAB_NAME_EIGHT_COUNTRY = "Mexico - Peso";
    private static final String TAB_NAME_NINE = "KWD";
    private static final String TAB_NAME_NINE_COUNTRY = "Kuwait - Dinar";
    private static final int DATABASE_VERSION = 1;
    private static final String NAME_COL = "CurrencyName";
    private static final String ID_COL = "_id";
    private static final String CurrencyName = "CurrencyName";
    private static final String toUSD = "toUSD";
    private static final String symbol = "symbol";

    private String[] tab_names = {TAB_NAME_ONE, TAB_NAME_TWO, TAB_NAME_THREE, TAB_NAME_FOUR, TAB_NAME_FIVE, TAB_NAME_SIX, TAB_NAME_SEVEN, TAB_NAME_EIGHT, TAB_NAME_NINE};
    private String[] tab_country_names = {TAB_NAME_ONE_COUNTRY, TAB_NAME_TWO_COUNTRY, TAB_NAME_THREE_COUNTRY, TAB_NAME_FOUR_COUNTRY, TAB_NAME_FIVE_COUNTRY, TAB_NAME_SIX_COUNTRY, TAB_NAME_SEVEN_COUNTRY, TAB_NAME_EIGHT_COUNTRY, TAB_NAME_NINE_COUNTRY};
    private double[] tab_usd_vals = {1, 0.87, 0.77, 3599, 845, 103, 2239, 17, 0.30};

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // insert the currencies when the first is first launched using a for loop.
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_ONE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + ID_COL + " INTEGER PRIMARY KEY," + NAME_COL + " TEXT, " + toUSD + " TEXT," + symbol + ")";
        db.execSQL(CREATE_TABLE_ONE);

        for (int i = 0; i != tab_country_names.length; i++){

            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME_COL, tab_country_names[i]);
            contentValues.put(toUSD, tab_usd_vals[i]);
            contentValues.put(symbol, tab_names[i]);
            db.insert("Currencies", null, contentValues);

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //method to insert the new currency in the database
    void insertCurrency(String name, String tousd) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COL, name);
        contentValues.put(toUSD, tousd);
        db.insert("Currencies", null, contentValues);
    }

    // string method to return the rate to USD of a given currency
    String getCurrencyValue(String name){

        SQLiteDatabase db = this.getReadableDatabase();

        String returnedValue = "";

        String selectQuery = "SELECT toUSD FROM Currencies WHERE CurrencyName = '" + name + "'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                returnedValue = (cursor.getString(cursor.getColumnIndex("toUSD")));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return returnedValue;

    }

    //method to delete the currency. gets passed the currency name to delete as an argument
    void deleteCurrency(String currencyName) {
        SQLiteDatabase db = getWritableDatabase();
        String where = CurrencyName + " = ?";
        String[] whereArgs = { currencyName };
        db.delete("Currencies", where, whereArgs);
    }

    // method to return the currency names and rate to USD (used in the settings activity)
    HashMap<String, String> currencies(){

        HashMap<String, String> toReturn = new HashMap<>();

        String selectQuery = "SELECT * FROM Currencies ORDER BY _id ASC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                toReturn.put(cursor.getString(cursor.getColumnIndex("CurrencyName")), cursor.getString(cursor.getColumnIndex("toUSD")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return toReturn;
    }

    // method to return all currencies row ids
    ArrayList<String> getCurrencyIds(){

        ArrayList<String> toReturn = new ArrayList<>();

        String selectQuery = "SELECT * FROM Currencies ORDER BY _id ASC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                toReturn.add(cursor.getString(cursor.getColumnIndex("_id")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return toReturn;
    }

    // method to fetch all currencies and return them in a string array
    ArrayList<String> currenciesSymbol(){

        ArrayList<String> toReturn = new ArrayList<>();

        String selectQuery = "SELECT * FROM Currencies ORDER BY CurrencyName ASC";
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                toReturn.add(cursor.getString(cursor.getColumnIndex("CurrencyName")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return toReturn;
    }

    // method to update the currency, gets passed the old currency name, the new name and the values.
    void updateCurrency(String oldName, String name, String values){

        SQLiteDatabase db = this.getReadableDatabase();

        String returnedID = "";

        String selectQuery = "SELECT _id FROM Currencies WHERE CurrencyName = '" + oldName + "'";
        Cursor cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                returnedID = (cursor.getString(cursor.getColumnIndex("_id")));
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.execSQL("UPDATE Currencies SET CurrencyName='" + name + "', toUSD='" + values + "' WHERE _id=" + returnedID + "");
        db.close();
    }

}

