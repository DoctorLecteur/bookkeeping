package com.example.bookkeeping;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity {

    private DbAdapter mDbHelper;


    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        selectAccount();
        registerForContextMenu(getListView());
    }

    private void selectAccount() {
        Cursor accountCurcor = mDbHelper.fetchAllAccounts();
        startManagingCursor(accountCurcor);

        String[] from = new String[]{DbAdapter.KEY_NAME_ACCOUNT, DbAdapter.KEY_SUM_OST};

        int[] to = new int[]{R.id.text1, R.id.text2};

        SimpleCursorAdapter account = new SimpleCursorAdapter(this, R.layout.account_row, accountCurcor, from, to);
        setListAdapter(account);

    }

    public void addAccount(View v){
        createAccount();
    }

    private void createAccount() {
        Intent i = new Intent(this, activity_account.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v,
                                   int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, AccountExpensesActivity.class);
        i.putExtra(DbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        selectAccount();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, Menu.FIRST, Menu.NONE, "Удалить счет");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case Menu.FIRST:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteAccount(info.id);
                selectAccount();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public void exitClick(View v) {
        finish();
    }

}
