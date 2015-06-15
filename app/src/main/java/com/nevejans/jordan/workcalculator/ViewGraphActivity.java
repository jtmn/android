package com.nevejans.jordan.workcalculator;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
//TODO
//make todo list

public class ViewGraphActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_graph);

        final String TAG = ".ViewGraphActivity";
        final double HOURLY_PAY = 5.4;
        double restaurantSalesPerNight;
        double avgHourlyPay;
        double totalHours=0;
        double totalTips=0;//just tips
        double totalPay=0;//tips+bar+hourly
        int totalRestaurantSales=0;
        int totalDaysWorked=0;
        double avgShiftLength;
        double avgPayPerDay;
        double avgTipsPerDay;

        int totalServerTips=0;
        double avgTipsPerServer=0;


        TextView avgPayNumTextView = (TextView) findViewById(R.id.avg_pay_num_text_view);
        TextView avgPayPerHourNumTextView = (TextView) findViewById(R.id.avg_pay_per_hour_num_text_view);
        TextView avgShiftLengthNumTextView = (TextView) findViewById(R.id.avg_shift_length_num_text_view);
        TextView avgServerPayNumTextView = (TextView) findViewById(R.id.avg_server_day_num_text_view);
        TextView avgRestaurantSalesNumTextView = (TextView) findViewById(R.id.avg_restaurant_sales_num_text_view);
        TextView avgTipsNumTextView = (TextView) findViewById(R.id.avg_tips_num_text_view);


        List<WorkDay> workDayList = WorkDay.listAll(WorkDay.class);//gets WorkDays from sql database
        LineGraphSeries<DataPoint> serverTips = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> busboyTips = new LineGraphSeries<DataPoint>();
        GraphView graph = (GraphView) findViewById(R.id.lineGraphView);

        //totally not copy/pasted code to populate daySpinner
        Spinner spinner = (Spinner) findViewById(R.id.daySpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.days_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //blank for now
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

            }
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){

            }
        });

        Log.d(TAG, "Calculating stats");

        //populates the two lists to be graphed
        //adds up totals used to calc avgs
        Log.d(TAG, "size of WorkDayList"+workDayList.toArray().length);
        for(WorkDay d: workDayList ){
            String day = d.getDayOfWeek();
            Date x = d.getDate();//x-value on graph
            double yS = d.getServerTips();
            double yB = d.getMoneyMade();

            if(day.equals(spinner.getSelectedItem().toString())||spinner.getSelectedItem().toString().equals("All")) {//if day==spinnerDay or spinnerDay=='All'

                Log.d(TAG, "Adding day to list: "+day);
                //should change this
                serverTips.appendData(new DataPoint(x, yS), true, 30);
                busboyTips.appendData(new DataPoint(x, yB), true, 30);

                totalServerTips += d.getServerTips();

                totalPay += d.getMoneyMade();
                totalHours += d.getHoursWorked();
                totalTips += d.getTips();
                totalRestaurantSales += d.getRestarauntSales();
                totalDaysWorked++;
            }else{
                Log.d(TAG, "NOT adding day to list: "+day);
            }
            Log.d(TAG, x.toString());
        }

        //
        //should have if debug==true populate list with test points
        //
        Log.d(TAG, "customizing graphs");

        busboyTips.setTitle("bT");
        busboyTips.setColor(Color.BLUE);
        busboyTips.setDrawDataPoints(true);

        serverTips.setTitle("sT");
        serverTips.setColor(Color.RED);
        serverTips.setDrawDataPoints(true);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext()));//to get the dates on the xaxis to show up
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);//maybe change number

        /*graph.getViewport().setMinX(.getTime());
        graph.getViewport().setMaxX(.getTime());
        graph.getViewport().setXAxisBoundsManual(true);*/


        Log.d(TAG, "adding data point functionality");

        final DecimalFormat D = new DecimalFormat("#0.00");
        //makes data points clickable
        //and displays (x,y) values in a toast
        busboyTips.setOnDataPointTapListener(new OnDataPointTapListener() {
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(ViewGraphActivity.this, new DateAsXAxisLabelFormatter(getApplicationContext()).formatLabel(dataPoint.getX(), true) + " - Busboys: $" + D.format(dataPoint.getY()), Toast.LENGTH_SHORT).show();
            }
        });

        serverTips.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(ViewGraphActivity.this, "Servers: $"+D.format(dataPoint.getY()), Toast.LENGTH_SHORT).show();
            }
        });

        if(!busboyTips.isEmpty() && !serverTips.isEmpty() && totalDaysWorked > 1) {
            graph.addSeries(busboyTips);
            graph.addSeries(serverTips);
        }

        Log.d(TAG, "Rendering Statistics");
        try {
            avgTipsPerServer = totalServerTips / totalDaysWorked;

            avgTipsPerDay = totalTips / totalDaysWorked;//does not include barTips
            avgHourlyPay = totalPay / totalHours;
            avgShiftLength = totalHours / totalDaysWorked;
            avgPayPerDay = totalPay / totalDaysWorked;
            restaurantSalesPerNight = totalRestaurantSales / totalDaysWorked;


            avgPayNumTextView.setText("$" + (int) avgPayPerDay);
            avgTipsNumTextView.setText("$" + (int) avgTipsPerDay);
            avgRestaurantSalesNumTextView.setText("$" + (int) restaurantSalesPerNight);
            avgServerPayNumTextView.setText("$" + (int) avgTipsPerServer);
            avgShiftLengthNumTextView.setText(new DecimalFormat("#0.0").format(avgShiftLength) + "hrs.");//output X.X hrs
            avgPayPerHourNumTextView.setText("$" + new DecimalFormat("#0.00").format(avgHourlyPay));//output: $X.XX
        }catch(ArithmeticException e){
            //
        }


    }

    //given avg earnings to plot and list of work days
    //returns series of DataPoint(d.date(),avg)
    private LineGraphSeries<DataPoint> plotAvg(int avgEarnings, List<WorkDay> workDayList){
        LineGraphSeries<DataPoint> avgSeries = new LineGraphSeries<>();

        for(WorkDay d: workDayList)
            avgSeries.appendData(new DataPoint(d.getDate(), avgEarnings), true, 30);


        return avgSeries;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_graph, menu);
        return true;
    }


}
