package com.example.bookkeeping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DbAdapter extends DatabaseUtils {

    public static final String DATABASE_NAME = "base0006";
    public static final String DATABASE_TABLE = "accounts";
    public static final String DATABASE_TABLE_EXPENSES = "expenses";
    public static final String DATABASE_TABLE_FINANCES = "finances";

    private static final int DATABASE_VERSION = 1;

    public static final String KEY_NAME_ACCOUNT = "name";
    public static final String KEY_SUM_OST = "sum_ost";
    public static final String KEY_ROWID = "_id";

    public static final String KEY_NAME_EXPENSE = "name_expense";
    public static final String KEY_CATEGORY_EXPENSE = "category_expense";
    public static final String KEY_DATE_TIME_EXPENSE = "rasxod_date_time_expense";
    public static final String KEY_PRICE_EXPENSE = "price_expense";
    public static final String KEY_EDIZM_EXPENSE = "edizm_expense";
    public static final String KEY_KOL_EXPENSE = "kol_expense";
    public static final String KEY_TOTAL_EXPENSE = "total_expense";
    public static final String KEY_ROWID_EXPENSE = "_id";
    public static final String KEY_ACCOUNT_ID = "account_id";

    public static final String KEY_ROWID_FINANCE = "_id";
    public static final String KEY_NAME_FINANCE = "name_finance";
    public static final String KEY_DATE_TIME_FINANCE = "date_time_finance";
    public static final String KEY_TOTAL_FINANCE = "total_finance";
    public static final String KEY_ACCOUNT_ID_FINANCE = "account_id";



    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " ("
            + KEY_ROWID + " integer primary key autoincrement, "
            + KEY_NAME_ACCOUNT + " text not null,"
            + KEY_SUM_OST + " text not null);";

    private static final String DATABASE_CREATE_EXPENSE =
            "create table " + DATABASE_TABLE_EXPENSES + " ("
                    + KEY_ROWID_EXPENSE + " integer primary key autoincrement, "
                    + KEY_NAME_EXPENSE + " text not null, "
                    + KEY_CATEGORY_EXPENSE + " text not null, "
                    + KEY_DATE_TIME_EXPENSE + " text not null, "
                    + KEY_PRICE_EXPENSE + " text not null, "
                    + KEY_EDIZM_EXPENSE + " text not null, "
                    + KEY_KOL_EXPENSE + " text not null, "
                    + KEY_TOTAL_EXPENSE + " text not null,"
                    + KEY_ACCOUNT_ID + " integer not null, foreign key (account_id) REFERENCES "
                    + DATABASE_TABLE +"(" + KEY_ROWID + ")" + ");";


    private static final String DATABASE_CREATE_FINANCE =
            "create table " + DATABASE_TABLE_FINANCES + " ("
            + KEY_ROWID_FINANCE + " integer primary key autoincrement, "
            + KEY_NAME_FINANCE + " text not null, "
            + KEY_DATE_TIME_FINANCE + " text not null, "
            + KEY_TOTAL_FINANCE + " text not null,"
            + KEY_ACCOUNT_ID_FINANCE + " integer not null, foreign key (account_id) REFERENCES "
                    + DATABASE_TABLE +"(" + KEY_ROWID + ")" + ");";

    private final Context mCtx;

    public DbAdapter(Context ctx) {
        this.mCtx = ctx;

    }

    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long createAccount(String name, String ost){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME_ACCOUNT, name);
        initialValues.put(KEY_SUM_OST, ost);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public long createExpense(String name, String category, String rasxodDateTime, String price, String edizm, String kol, String total, long account_id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME_EXPENSE, name);
        initialValues.put(KEY_CATEGORY_EXPENSE, category);
        initialValues.put(KEY_DATE_TIME_EXPENSE, rasxodDateTime);
        initialValues.put(KEY_PRICE_EXPENSE, price);
        initialValues.put(KEY_EDIZM_EXPENSE, edizm);
        initialValues.put(KEY_KOL_EXPENSE, kol);
        initialValues.put(KEY_TOTAL_EXPENSE, total);
        initialValues.put(KEY_ACCOUNT_ID, account_id);

        return mDb.insert(DATABASE_TABLE_EXPENSES, null, initialValues);

    }

    public long createFinance(String name, String DateTime, String total, long account_id){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME_FINANCE, name);
        initialValues.put(KEY_DATE_TIME_FINANCE, DateTime);
        initialValues.put(KEY_TOTAL_FINANCE, total);
        initialValues.put(KEY_ACCOUNT_ID_FINANCE, account_id);

        return mDb.insert(DATABASE_TABLE_FINANCES, null, initialValues);
    }

    public boolean deleteAccount(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteExpense(long rowId) {
        return mDb.delete(DATABASE_TABLE_EXPENSES, KEY_ROWID_EXPENSE + "=" + rowId, null) > 0;
    }

    public boolean deleteFinance(long rowId){
        return mDb.delete(DATABASE_TABLE_FINANCES, KEY_ROWID_FINANCE + "=" + rowId, null) > 0;
    }

    public boolean updateAccount(long rowId, String name){
        ContentValues args = new ContentValues();
        args.put(KEY_NAME_ACCOUNT, name);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null)>0;

    }

    public boolean updateExpense(long rowId, String name, String category, String rasxodDateTime, String price, String edizm, String kol, String total) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME_EXPENSE, name);
        args.put(KEY_CATEGORY_EXPENSE, category);
        args.put(KEY_DATE_TIME_EXPENSE, rasxodDateTime);
        args.put(KEY_PRICE_EXPENSE, price);
        args.put(KEY_EDIZM_EXPENSE, edizm);
        args.put(KEY_KOL_EXPENSE, kol);
        args.put(KEY_TOTAL_EXPENSE, total);

        return mDb.update(DATABASE_TABLE_EXPENSES, args, KEY_ROWID_EXPENSE + "=" + rowId, null) > 0;
    }

    public boolean updateFinance(long rowId, String name, String DateTime, String total){
        ContentValues args = new ContentValues();
        args.put(KEY_NAME_FINANCE, name);
        args.put(KEY_DATE_TIME_FINANCE, DateTime);
        args.put(KEY_TOTAL_FINANCE, total);

        return mDb.update(DATABASE_TABLE_FINANCES, args, KEY_ROWID_FINANCE + "=" + rowId, null) > 0;

    }

    public Cursor fetchAllAccounts() {
        return mDb.query(DATABASE_TABLE,
                new String[] {KEY_ROWID, KEY_NAME_ACCOUNT, KEY_SUM_OST},
                null, null, null, null, null);
    }

    public Cursor fetchAllExpense(long id) {
        return mDb.query(DATABASE_TABLE_EXPENSES,
                new String[] {KEY_ROWID_EXPENSE, KEY_NAME_EXPENSE, KEY_CATEGORY_EXPENSE,
                        KEY_DATE_TIME_EXPENSE, KEY_PRICE_EXPENSE, KEY_EDIZM_EXPENSE, KEY_KOL_EXPENSE, KEY_TOTAL_EXPENSE, KEY_ACCOUNT_ID},
                KEY_ACCOUNT_ID + "=" + id, null, null, null, KEY_DATE_TIME_EXPENSE);
    }

    public int fetchAllTotalExpense(long id) {
        final Cursor cursor = mDb.rawQuery("SELECT SUM(total_expense) as total FROM expenses WHERE account_id = "+ id + ";", null);
        int sum = 0;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    sum = cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        }

        return sum;
    }

    public int fetchAllTotalFinance(long id) {
        final Cursor cursor = mDb.rawQuery("SELECT SUM(total_finance) as total FROM finances WHERE account_id = "+ id + ";", null);
        int sum = 0;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    sum = cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        }

        return sum;
    }

    public float ChartTotal(String category, long id){
        final Cursor cursor = mDb.rawQuery("SELECT SUM(total_expense) as total FROM expenses WHERE account_id = " + id + " AND category_expense = " + "'" + category + "'" + ";", null);
        float sum = 0;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    sum = cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        }

        return sum;
    }

    public Cursor totalSumExpenseFinance(long id){
        int sum_total = 0;
        int a = 0, b = 0;
        if (id == 0) id = 1;
        a = fetchAllTotalFinance(id);
        b = fetchAllTotalExpense(id);
        sum_total = a - b;
        String sum_ost = String.valueOf(sum_total);
        Log.w("sum_ost = ", "id = " + id + " a = " + a + " b = " + " sum = " + sum_ost);
        ContentValues args = new ContentValues();
        args.put(KEY_SUM_OST, sum_ost);

        mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + id, null);

        return fetchAllAccounts();
    }

    public Cursor fetchAllFinance(long id){
        return mDb.query(DATABASE_TABLE_FINANCES,
                new String[] {KEY_ROWID_FINANCE, KEY_NAME_FINANCE,
                        KEY_DATE_TIME_FINANCE, KEY_TOTAL_FINANCE, KEY_ACCOUNT_ID_FINANCE},
                KEY_ACCOUNT_ID_FINANCE + "=" + id, null, null, null, KEY_DATE_TIME_FINANCE);
    }

    public Cursor fetchAccount(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE,
                new String[] {KEY_ROWID, KEY_NAME_ACCOUNT}, KEY_ROWID + "=" + rowId,
                null, null, null, null, null);

        if (mCursor!=null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchExpense(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE_EXPENSES,
                new String[] {KEY_ROWID_EXPENSE, KEY_NAME_EXPENSE, KEY_CATEGORY_EXPENSE,
                        KEY_DATE_TIME_EXPENSE, KEY_PRICE_EXPENSE, KEY_EDIZM_EXPENSE, KEY_KOL_EXPENSE, KEY_TOTAL_EXPENSE}, KEY_ROWID_EXPENSE + "=" + rowId,
                null, null, null, null, null);

        if (mCursor!=null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchFinance(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(DATABASE_TABLE_FINANCES,
                new String[] {KEY_ROWID_FINANCE, KEY_NAME_FINANCE,
                        KEY_DATE_TIME_FINANCE, KEY_TOTAL_FINANCE},
                KEY_ROWID_FINANCE + "=" + rowId, null, null, null, null);

        if (mCursor!=null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}

        @Override
        public void onCreate(SQLiteDatabase db) {
           db.execSQL(DATABASE_CREATE);
           db.execSQL(DATABASE_CREATE_EXPENSE);
           db.execSQL(DATABASE_CREATE_FINANCE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int Version){

        }
    }

}
