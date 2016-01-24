package com.devilthrone.jkchart;

/**
 * Created by jikun on 16/1/16.
 * 饼状图中的一个数据模型
 * 包括文字 颜色 值
 */
public class PieChartItem {
    public int color;
    public int value;
    public String title;

    public PieChartItem(String title, int value, int color) {
        this.color = color;
        this.value = value;
        this.title = title;
    }
}
