# JKChart
this is a simple Chart library for Android !
这是一个简单方便的android图表框架,目前包括点阵图和饼图两种图，使用起来灰常灰常简单。


点阵图 JKLineChart:

![这里写图片描述](http://img.blog.csdn.net/20160125013917407)

在xml文件中声明：
```
<com.devilthrone.jkchart.JKLineChart
        android:id="@+id/line_Chart"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerHorizontal="true"
        android:layout_gravity="center"
        app:indicatorType="rectangle"
        app:showHintBlock="true" />
```
在activity文件中使用
```
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
```

具体实现过程请看我的博客：[http://blog.csdn.net/a253664942/article/details/50576360](http://blog.csdn.net/a253664942/article/details/50576360)  中有详细的描述。

饼图 JKPieChart:(尚需完善)
![这里写图片描述](http://img.blog.csdn.net/20160125014253304)

xml布局文件中

```
 <com.devilthrone.jkchart.JKPieChart
        android:id="@+id/pie_Chart"
        android:layout_marginTop="50dp"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:layout_centerHorizontal="true"
        android:layout_gravity="center"
        app:hintTitleSize="14dp"
        app:hintTitleColor="#8d8d8d"
        app:isRound="true"
        app:showTitle="true"
         />
```

activity中

```
 mPieChart = (JKPieChart) findViewById(R.id.pie_Chart);
        PieChartItem firstItem = new  PieChartItem("first", 30, Color.parseColor("#BAF0A2"));
        PieChartItem secondItem = new  PieChartItem("second", 10, Color.parseColor("#2F6994"));
        PieChartItem thirdItem = new  PieChartItem("third", 20, Color.parseColor("#FF6600"));
        PieChartItem fourthItem = new  PieChartItem("fourth", 20, Color.parseColor("#800080"));
        PieChartItem fifthItem = new  PieChartItem("fifth", 20, Color.parseColor("#708090"));

        List<PieChartItem> chartItemsList = new ArrayList<PieChartItem>();
        chartItemsList.add(firstItem);
        chartItemsList.add(secondItem);
        chartItemsList.add(thirdItem);
        chartItemsList.add(fourthItem);
        chartItemsList.add(fifthItem);
        mPieChart.setChartItemsList(chartItemsList);
        mPieChart.setMaxValue(100);
```

