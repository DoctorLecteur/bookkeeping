package com.example.bookkeeping;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class activity_account extends AppCompatActivity {


    private EditText name_account;
    private DbAdapter mDbHelper;
    private Long mRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mDbHelper = new DbAdapter(this);
        name_account = (EditText) findViewById(R.id.account);
        mRowId = savedInstanceState !=null ? savedInstanceState.getLong(DbAdapter.KEY_ROWID): null;

    }


    private void populateFields() {
        if (mRowId != null) {
            Cursor account = mDbHelper.fetchAccount(mRowId);
            startManagingCursor(account);

            name_account.setText(account.getString(account.getColumnIndexOrThrow(DbAdapter.KEY_NAME_ACCOUNT)));
        }
    }

    private void setRowIdFromIntent() {
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras !=null ? extras.getLong(DbAdapter.KEY_ROWID):null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDbHelper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDbHelper.open();
        setRowIdFromIntent();
        //populateFields();
    }

    public void add(View v) {
        saveState();
        exit(v);
    }

    private void saveState() {
        String name = name_account.getText().toString();

        if (mRowId == null || mRowId == 0)
        {
            long id = mDbHelper.createAccount(name, "0");
            if (id > 0) {
                mRowId = id;
                mRowId = null;
            }
        }
        else {
            mDbHelper.updateAccount(mRowId, name);
        }
    }

    public void exit(View v){
        finish();
    }
}
