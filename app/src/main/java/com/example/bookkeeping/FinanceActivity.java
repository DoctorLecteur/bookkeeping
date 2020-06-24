package com.example.bookkeeping;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FinanceActivity extends ListActivity {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private DbAdapter mDbHelper;
    private TextView totalSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finance);
        totalSum = (TextView) findViewById(R.id.totalsum);
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        fillData();
        sumData();
        registerForContextMenu(getListView());
    }

    private void fillData() {
        long id;
        id = getIntent().getExtras().getLong(DbAdapter.KEY_ROWID);
        Cursor financeCursor = mDbHelper.fetchAllFinance(id);
        startManagingCursor(financeCursor);

        String[] from = new String[]{DbAdapter.KEY_NAME_FINANCE, DbAdapter.KEY_TOTAL_FINANCE};

        int[] to = new int[]{R.id.text1, R.id.text2};

        SimpleCursorAdapter finance = new SimpleCursorAdapter(this, R.layout.account_row, financeCursor, from, to);
        setListAdapter(finance);
    }

    private void sumData() {
        int sum = 0;
        long id;
        id = getIntent().getExtras().getLong(DbAdapter.KEY_ROWID);
        sum = mDbHelper.fetchAllTotalFinance(id);
        String tsum = new String().valueOf(sum);
        totalSum.setText(tsum);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
        sumData();
    }

    @Override
    protected void onListItemClick(ListView l, View v,
                                   int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, FinanceEditActivity.class);
        i.putExtra(DbAdapter.KEY_ROWID_FINANCE, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_delete:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteFinance(info.id);
                fillData();
                sumData();
                long id;
                id = getIntent().getExtras().getLong(DbAdapter.KEY_ROWID);
                mDbHelper.totalSumExpenseFinance(id);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,
                                    View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.list_menu_item_longpress, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_insert:
                createFinance();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void add_finance(View v){
        createFinance();
    }

    private void createFinance(){
        Intent i = new Intent(this, FinanceEditActivity.class);
        long id = getIntent().getExtras().getLong(DbAdapter.KEY_ROWID);
        i.putExtra(DbAdapter.KEY_ACCOUNT_ID_FINANCE, id);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    public void exit_finance(View v){
        finish();
    }
}
