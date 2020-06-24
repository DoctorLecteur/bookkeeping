package com.example.bookkeeping;

import android.app.Activity;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class ChartActivity extends Activity {

    String[] array_category;
    float[] array_sum;


    private float[] yData = {};
    private String[] xData = {};

    PieChart pieChart;
    private DbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();

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

        xData = array_category;

        array_sum = new float[11];


        pieChart = (PieChart) findViewById(R.id.idPieChart);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setDrawEntryLabels(true);
        
        addDataSet();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int pos1 = e.toString().indexOf("(sum): ");
                String sales = e.toString().substring(pos1 + 7);

                for (int i=0; i < yData.length; i++){
                    if(yData[i] == Float.parseFloat(sales)){
                        pos1 = i;
                        break;
                    }
                }
                String expenses = xData[pos1];
                Toast.makeText(ChartActivity.this, ("Категория: " + expenses + "\n" + "Потрачено: " + sales), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });


    }

    public void addDataSet() {
        ArrayList<PieEntry> yEntys = new ArrayList<>();
        ArrayList<String> xEntys = new ArrayList<>();


        long id;
        id = getIntent().getExtras().getLong(DbAdapter.KEY_ROWID);

        for (int i=0; i<11; i++){
            array_sum[i] = mDbHelper.ChartTotal(xData[i], id);
            Log.w("array_sum", "sum = " + array_sum[i]);
        }

        yData = array_sum;

        for (int i = 0; i < xData.length; i++){
            xEntys.add(xData[i]);
            Log.w("xData", "i = " + "xData = " + xData[i]);
        }

        for (int i=0; i < yData.length; i++){
            yEntys.add(new PieEntry(yData[i], i));
        }



        PieDataSet pieDataSet = new PieDataSet(yEntys, "Расходы");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        colors.add(Color.BLACK);
        colors.add(Color.DKGRAY);
        colors.add(Color.LTGRAY);
        colors.add(Color.WHITE+Color.RED);

        pieDataSet.setColors(colors);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
