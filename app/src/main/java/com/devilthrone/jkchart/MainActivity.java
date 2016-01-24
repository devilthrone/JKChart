package com.devilthrone.jkchart;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private JKLineChart mLineChart;
    private JKPieChart mPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPieChart = (JKPieChart) findViewById(R.id.pie_Chart);
        PieChartItem firstItem = new PieChartItem("first", 30, Color.parseColor("#BAF0A2"));
        PieChartItem secondItem = new PieChartItem("second", 10, Color.parseColor("#2F6994"));
        PieChartItem thirdItem = new PieChartItem("third", 20, Color.parseColor("#FF6600"));
        PieChartItem fourthItem = new PieChartItem("fourth", 20, Color.parseColor("#800080"));
        PieChartItem fifthItem = new PieChartItem("fifth", 20, Color.parseColor("#708090"));

        List<PieChartItem> chartItemsList = new ArrayList<PieChartItem>();
        chartItemsList.add(firstItem);
        chartItemsList.add(secondItem);
        chartItemsList.add(thirdItem);
        chartItemsList.add(fourthItem);
        chartItemsList.add(fifthItem);
        mPieChart.setChartItemsList(chartItemsList);
        mPieChart.setMaxValue(100);

        mLineChart = (JKLineChart) findViewById(R.id.line_Chart);
        LineChartItem item1 = new LineChartItem(Color.parseColor("#00ff00"), 12.5f, "正常");
        LineChartItem item2 = new LineChartItem(Color.parseColor("#0000ff"), 10.1f, "偏低");
        LineChartItem item3 = new LineChartItem(Color.parseColor("#FF0000"), 20.2f, "偏高");
        LineChartItem item4 = new LineChartItem(Color.parseColor("#FF0000"), 15.1f, "偏高");
        List<LineChartItem> list = new ArrayList<LineChartItem>();
        list.add(item1);
        list.add(item2);
        list.add(item3);
        list.add(item4);
        mLineChart.setItemList(list);
    }


}
