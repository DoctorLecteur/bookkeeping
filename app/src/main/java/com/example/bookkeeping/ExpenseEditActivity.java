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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExpenseEditActivity extends Activity {


    private Button mDateButton;
    private Button mTimeButton;

    private static final int DATE_PICKER_DIALOG = 0;
    private static final int TIME_PICKER_DIALOG = 1;

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "kk:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";

    private DbAdapter mDbHelper;
    private Calendar mCalendar;
    private ChartActivity chart;

    private EditText mNameText;
    private Spinner mCategoryText;
    private EditText mPriceText;
    private Spinner mEdIzmText;
    private EditText mKolText;
    private EditText mTotalText;

    private Button mAddButton;
    private Button mExitButton;
    private Long mRowId;
    private Long mAccoutId;

    String[] array_category;
    String[] array_izm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new DbAdapter(this);
        setContentView(R.layout.expense_edit);

        array_category = new String[11];
        array_category[0] = "Продукты";
        array_category[1] = "Транспорт";
        array_category[2] = "Коммунальные услуги";
        array_category[3] = "Одежда и обувь";
        array_category[4] = "Товары для дома";
        array_category[5] = "Интернет и мобильная связь";
        array_category[6] = "Косметология";
        array_category[7] = "Техника";
        array_category[8] = "Здоровье";
        array_category[9] = "Развлечения";
        array_category[10] = "Прочее";

        array_izm = new String[4];
        array_izm[0] = "штука";
        array_izm[1] = "килограмм";
        array_izm[2] = "литр";
        array_izm[3] = "упаковка";

        mDateButton = (Button) findViewById(R.id.date);
        mCalendar = Calendar.getInstance();
        mTimeButton = (Button) findViewById(R.id.time);

        mNameText = (EditText) findViewById(R.id.name);
        mCategoryText = (Spinner) findViewById(R.id.category);
        mPriceText = (EditText) findViewById(R.id.price);
        mEdIzmText = (Spinner) findViewById(R.id.edizm);

        mKolText = (EditText) findViewById(R.id.kol);
        mTotalText = (EditText) findViewById(R.id.total);

        mAddButton = (Button) findViewById(R.id.button1);
        mExitButton = (Button) findViewById(R.id.button2);
        mRowId = savedInstanceState !=null ? savedInstanceState.getLong(DbAdapter.KEY_ROWID_EXPENSE): null;
        mAccoutId = savedInstanceState !=null ? savedInstanceState.getLong(DbAdapter.KEY_ACCOUNT_ID): null;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, array_category);
        mCategoryText.setAdapter(adapter);

        ArrayAdapter<String> adapter_izm = new ArrayAdapter<String>(this, R.layout.spinner_item, array_izm);
        mEdIzmText.setAdapter(adapter_izm);

        registerButtonListenersAndSetDefaultText();

    }

    private void setRowIdFromIntent() {
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras !=null ? extras.getLong(DbAdapter.KEY_ROWID_EXPENSE):null;
        }
        if (mAccoutId == null){
            Bundle extras = getIntent().getExtras();
            mAccoutId = extras !=null ? extras.getLong(DbAdapter.KEY_ACCOUNT_ID):null;
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
                Toast.makeText(ExpenseEditActivity.this, getString(R.string.ras_add_message), Toast.LENGTH_SHORT).show();
                saveState();
                mNameText.setText("");
                mCategoryText.setSelection(0);
                updateDateButtonText();
                updateTimeButtonText();
                mPriceText.setText("");
                mKolText.setText("");
                mTotalText.setText("");

            }
        });

        mExitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPriceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (mPriceText!=null &&  mKolText!=null) {
                        String pr = mPriceText.getText().toString();
                        String kl = mKolText.getText().toString();
                        if (pr!="" && kl!="") {
                            float price = new Float(pr).floatValue();
                            float kol = new Float(kl).floatValue();
                            float total = price * kol;
                            String tl = new String().valueOf(total);
                            mTotalText.setText(tl);
                        }
                    }
                } catch (NumberFormatException e) {
                    mTotalText.setText("");
                }
            }
        });

        mKolText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (mPriceText!=null &&  mKolText!=null) {
                        String pr = mPriceText.getText().toString();
                        String kl = mKolText.getText().toString();
                        if (pr!="" && kl!="") {
                            float price = new Float(pr).floatValue();
                            float kol = new Float(kl).floatValue();
                            float total = price * kol;
                            String tl = new String().valueOf(total);
                            mTotalText.setText(tl);
                        }
                    }
                } catch (NumberFormatException e) {
                    mTotalText.setText("");
                }
            }
        });

    }

    private void populateFields() {
        if (mRowId != 0 && mAccoutId != null) {
            Cursor rasxod = mDbHelper.fetchExpense(mRowId);
            startManagingCursor(rasxod);
            mNameText.setText(rasxod.getString(rasxod.getColumnIndexOrThrow(DbAdapter.KEY_NAME_EXPENSE)));
            String Category = rasxod.getString(rasxod.getColumnIndexOrThrow(DbAdapter.KEY_CATEGORY_EXPENSE));

            String ed_izm = rasxod.getString(rasxod.getColumnIndexOrThrow(DbAdapter.KEY_EDIZM_EXPENSE));

            ArrayAdapter adapter = (ArrayAdapter) mCategoryText.getAdapter();
            int position = adapter.getPosition(Category);
            mCategoryText.setSelection(position);
            mPriceText.setText(rasxod.getString(rasxod.getColumnIndexOrThrow(DbAdapter.KEY_PRICE_EXPENSE)));
            ArrayAdapter adapter1 = (ArrayAdapter) mEdIzmText.getAdapter();
            int position1 = adapter1.getPosition(ed_izm);
            mEdIzmText.setSelection(position1);
            mKolText.setText(rasxod.getString(rasxod.getColumnIndexOrThrow(DbAdapter.KEY_KOL_EXPENSE)));
            mTotalText.setText(rasxod.getString(rasxod.getColumnIndexOrThrow(DbAdapter.KEY_TOTAL_EXPENSE)));

            mAddButton.setText("Изменить");


            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            Date date = null;
            try {
                String dateString = rasxod.getString(rasxod.getColumnIndexOrThrow(DbAdapter.KEY_DATE_TIME_EXPENSE));
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
        outState.putLong(DbAdapter.KEY_ROWID_EXPENSE, mRowId);
    }

    private void saveState() {
        String name = mNameText.getText().toString();
        String category = mCategoryText.getSelectedItem().toString();
        String price = mPriceText.getText().toString();
        String izm = mEdIzmText.getSelectedItem().toString();
        String kol = mKolText.getText().toString();
        String total = mTotalText.getText().toString();

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        String rasxodDateTime = dateTimeFormat.format(mCalendar.getTime());
        if (mRowId == null || mRowId == 0)
        {
            long id = mDbHelper.createExpense(name, category, rasxodDateTime, price, izm, kol, total, mAccoutId);
            //chart.addDataSet();
            if (id > 0) {
                mRowId = id;
                mDbHelper.totalSumExpenseFinance(mAccoutId);
                //chart.addDataSet();
                mRowId = null;
            }
        }
        else {
            mDbHelper.updateExpense(mRowId, name, category, rasxodDateTime, price, izm, kol, total);
            mDbHelper.totalSumExpenseFinance(mAccoutId);
            //chart.addDataSet();

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
                new DatePickerDialog(ExpenseEditActivity.this,
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
