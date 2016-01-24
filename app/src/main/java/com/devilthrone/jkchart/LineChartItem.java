package com.devilthrone.jkchart;

/**
 * Created by JK on 2016/1/18.
 * 点阵折线图的数据模型
 */
public class LineChartItem {
    public int color;
    public float value;
    public String title;

    public LineChartItem(int color, float value, String title) {
        this.color = color;
        this.value = value;
        this.title = title;

    }


}
