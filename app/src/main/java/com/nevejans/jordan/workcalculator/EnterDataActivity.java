package com.nevejans.jordan.workcalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.GregorianCalendar;
import java.util.Date;
import java.util.List;

public class EnterDataActivity extends Activity {

    final String TAG = ".EnterDataActivity";

    Double bT;//busboy tips
    Double sT;//server tips
    GregorianCalendar date;
    int bN;//# of busboys
    int sN;//# of servers




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data);

        /*
        Double bT;//busboy tips
        Double sT;//server tips
        GregorianCalendar date;
        int bN;//# of busboys
        int sN;//# of servers
        */

        Button eraseDataButton = (Button) findViewById(R.id.eraseDataButton);
        Button enterDataButton = (Button) findViewById(R.id.submitButton);

        List<WorkDay> workDayList = WorkDay.listAll(WorkDay.class);
        TableLayout dataPointsTableLayout = (TableLayout) findViewById(R.id.dataPointsTableLayout);
        populateTable(dataPointsTableLayout, workDayList);

        enterDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Init EditText variables");
                EditText yourTipsEditText = (EditText) findViewById(R.id.yourTipsEditText);
                EditText noBusboysEditText = (EditText) findViewById(R.id.noBusboysEditText);
                EditText noServersEditText = (EditText) findViewById(R.id.noServersEditText);
                EditText hoursWorkedEditText = (EditText) findViewById(R.id.hoursWorkedEditText);
                EditText barTipsEditText = (EditText) findViewById(R.id.barTipsEditText);
                Log.d(TAG, "Create WorkDay");
                //DatePicker datePicker = (DatePicker) findViewById(R.id.submitDataDatePicker);

                try {
                    WorkDay workDay = new WorkDay(
                            Integer.parseInt(yourTipsEditText.getText().toString()),
                            Integer.parseInt(barTipsEditText.getText().toString()),
                            Integer.parseInt(noBusboysEditText.getText().toString()),
                            Integer.parseInt(noServersEditText.getText().toString()),
                            new Date(), //Integer.parseInt(""+datePicker.getMonth()+datePicker.getDayOfMonth()+datePicker.getYear()),//Integer.parseInt(dateEditText.getText().toString() ),
                            Double.parseDouble(hoursWorkedEditText.getText().toString())
                    );

                    Log.d(TAG, "Saving WorkDay");
                    workDay.save();//saves to SQL using SugarORM

                    Log.d(TAG, "Making toast");
                    //
                    //should add a toast and an intent to go back to the main menu
                    //


                    Toast toast = Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT);
                    toast.show();

                    Log.d(TAG, "Starting new intent");

                    Intent intent = new Intent(EnterDataActivity.this, MainMenuActivity.class);
                    startActivity(intent);

                }catch(NumberFormatException e){
                    Log.d(TAG, "WorkDay Failed to Save");
                    Toast toast = Toast.makeText(getApplicationContext(), "Missing required fields", Toast.LENGTH_SHORT);
                    toast.show();
                }




            }
        });

        eraseDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EnterDataActivity.this)
                        .setTitle("Title")
                        .setMessage("Do you really want to delete all the data?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                WorkDay.deleteAll(WorkDay.class);
                                Toast.makeText(EnterDataActivity.this, "Deleted.", Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });
        Log.d(TAG, "OnCreate Finished Loading");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter_data, menu);
        return true;
    }

    public void populateTable(TableLayout t, List<WorkDay> workDayList){
            //tableRow

        for(WorkDay w: workDayList) {
            TableRow tr = (TableRow) findViewById(R.id.dataPointTableRow);
            //dollarsEarned
            TextView de = (TextView) tr.findViewById(R.id.dataPointTableRowDollarsEarnedTextView);
            //serverTips
            TextView st = (TextView) tr.findViewById(R.id.dataPointTableRowServerTipsTextView);
            //dollarsPerHour
            TextView dph = (TextView) tr.findViewById(R.id.dataPointTableRowDollarsPHourTextView);

            de.setText(String.valueOf(w.getMoneyMade()));
            st.setText(String.valueOf(w.getServerTips()));
            dph.setText(String.valueOf(w.getMoneyMade()/w.getHoursWorked()));//$Made/Hrs.Worked
            t.addView(tr);
        }
    }
}
