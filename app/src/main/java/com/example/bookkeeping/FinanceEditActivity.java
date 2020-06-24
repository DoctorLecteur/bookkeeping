package com.example.bookkeeping;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FinanceEditActivity extends Activity {

    private Button mAddButton;
    private Button mExitButton;

    private Button mDateButton;
    private Button mTimeButton;

    private static final int DATE_PICKER_DIALOG = 0;
    private static final int TIME_PICKER_DIALOG = 1;

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "kk:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";

    private DbAdapter mDbHelper;
    private Calendar mCalendar;

    private EditText mNameText;
    private EditText mTotalText;

    private Long mRowId;
    private Long mAccoutId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finance_edit);
        mDbHelper = new DbAdapter(this);


        mDateButton = (Button) findViewById(R.id.date);
        mCalendar = Calendar.getInstance();
        mTimeButton = (Button) findViewById(R.id.time);
        mNameText = (EditText) findViewById(R.id.name);
        mTotalText = (EditText) findViewById(R.id.total);



        mAddButton = (Button) findViewById(R.id.button1);
        mExitButton = (Button) findViewById(R.id.button2);

        mRowId = savedInstanceState !=null ? savedInstanceState.getLong(DbAdapter.KEY_ROWID_FINANCE): null;
        mAccoutId = savedInstanceState !=null ? savedInstanceState.getLong(DbAdapter.KEY_ACCOUNT_ID_FINANCE): null;

        registerButtonListenersAndSetDefaultText();

    }

    private void setRowIdFromIntent() {
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras !=null ? extras.getLong(DbAdapter.KEY_ROWID_FINANCE):null;
        }
        if (mAccoutId == null){
            Bundle extras = getIntent().getExtras();
            mAccoutId = extras !=null ? extras.getLong(DbAdapter.KEY_ACCOUNT_ID_FINANCE):null;
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
        populateFields();
    }

    private void registerButtonListenersAndSetDefaultText() {

        mDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_DIALOG);
            }
        });

        updateDateButtonText();
        mTimeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(TIME_PICKER_DIALOG);

            }
        });

        updateTimeButtonText();

        mAddButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setResult(RESULT_OK);
                Toast.makeText(FinanceEditActivity.this, getString(R.string.ras_add_message_finance), Toast.LENGTH_SHORT).show();
                saveState();
                mNameText.setText("");
                updateDateButtonText();
                updateTimeButtonText();
                mTotalText.setText("");

            }
        });

        mExitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void populateFields() {
        if (mRowId !=0 && mAccoutId != null) {
            Cursor finance = mDbHelper.fetchFinance(mRowId);
            startManagingCursor(finance);
            mNameText.setText(finance.getString(finance.getColumnIndexOrThrow(DbAdapter.KEY_NAME_FINANCE)));
            mTotalText.setText(finance.getString(finance.getColumnIndexOrThrow(DbAdapter.KEY_TOTAL_FINANCE)));

            mAddButton.setText("Изменить");

            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            Date date = null;
            try {
                String dateString = finance.getString(finance.getColumnIndexOrThrow(DbAdapter.KEY_DATE_TIME_FINANCE));
                date = dateTimeFormat.parse(dateString);
                mCalendar.setTime(date);
            }catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String defaultTitleKey = getString(R.string.pref_task_title_key);
            String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);
            String defaultTitle = prefs.getString(defaultTitleKey, "");
            String defaultTime = prefs.getString(defaultTimeKey, "");

            if ("".equals(defaultTitle) == false)
                mNameText.setText(defaultTitle);
            if ("".equals(defaultTime) == false)
                mCalendar.add(Calendar.MINUTE, Integer.parseInt(defaultTime));
        }
        updateDateButtonText();
        updateTimeButtonText();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(DbAdapter.KEY_ROWID_FINANCE, mRowId);
    }

    private void saveState() {
        String name = mNameText.getText().toString();
        String total = mTotalText.getText().toString();
        long id;

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        String DateTime = dateTimeFormat.format(mCalendar.getTime());
        if (mRowId == null || mRowId == 0)
        {
            id = mDbHelper.createFinance(name, DateTime, total, mAccoutId);

            if (id > 0) {
                mRowId = id;
                mDbHelper.totalSumExpenseFinance(mAccoutId);
                mRowId = null;
            }
        }
        else {
            mDbHelper.updateFinance(mRowId, name, DateTime, total);
            mDbHelper.totalSumExpenseFinance(mAccoutId);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case DATE_PICKER_DIALOG:
                return showDatePicker();
            case TIME_PICKER_DIALOG:
                return showTimePicker();
        }
        return super.onCreateDialog(id);
    }

    private DatePickerDialog showDatePicker() {
        DatePickerDialog datePicker =
                new DatePickerDialog(FinanceEditActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear,
                                                  int dayOfMonth) {
                                mCalendar.set(Calendar.YEAR, year);
                                mCalendar.set(Calendar.MONTH, monthOfYear);
                                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                updateDateButtonText();
                            }
                        }, mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
        return datePicker;
    }

    private TimePickerDialog showTimePicker() {
        TimePickerDialog timePicker =
                new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute){
                                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                mCalendar.set(Calendar.MINUTE, minute);
                                updateTimeButtonText();
                            }
                        }, mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE), true);
        return timePicker;
    }

    private void updateDateButtonText() {
        SimpleDateFormat dateFormat =
                new   SimpleDateFormat(DATE_FORMAT);
        String dateForButton =
                dateFormat.format(mCalendar.getTime());
        mDateButton.setText(dateForButton);
    }

    private void updateTimeButtonText() {
        SimpleDateFormat timeFormat =
                new SimpleDateFormat(TIME_FORMAT);
        String timeForButton =
                timeFormat.format(mCalendar.getTime());
        mTimeButton.setText(timeForButton);
    }

}
