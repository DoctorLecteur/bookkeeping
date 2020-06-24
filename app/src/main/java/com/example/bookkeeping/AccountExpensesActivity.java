package com.example.bookkeeping;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Toast;

public class AccountExpensesActivity extends TabActivity {

    private DbAdapter mDbHelper;
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_expenses);


        Resources res = getResources();
        String tab1expense = res.getString(R.string.expense);
        String tab2finance = res.getString(R.string.finance);
        String tab3 = "График";

        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        long id;
        long id2;
        long id3;

        final Intent i = new Intent(this, ExpensesActivity.class);
        id = getIntent().getExtras().getLong(DbAdapter.KEY_ROWID);
        i.putExtra(DbAdapter.KEY_ROWID, id);

        spec = tabHost.newTabSpec("Расходы").setIndicator(tab1expense).setContent(i);
        tabHost.addTab(spec);

        final Intent i2 = new Intent(this, FinanceActivity.class);
        id2 = getIntent().getExtras().getLong(DbAdapter.KEY_ROWID);
        i2.putExtra(DbAdapter.KEY_ROWID, id2);

        spec = tabHost.newTabSpec("Доходы").setIndicator(tab2finance).setContent(i2);
        tabHost.addTab(spec);

        final Intent i3 = new Intent(this, ChartActivity.class);
        id3 = getIntent().getExtras().getLong(DbAdapter.KEY_ROWID);
        i3.putExtra(DbAdapter.KEY_ROWID, id3);

        spec = tabHost.newTabSpec("График").setIndicator(tab3).setContent(i3);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(1);

        String f_key = String.valueOf(getIntent().getExtras().getLong(DbAdapter.KEY_ROWID));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

            }
        });
    }
}
