package com.manoj.employeeattendance;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.manoj.employeeattendance.pojo.api;
import com.manoj.employeeattendance.pojo.employeeDetails;
import com.manoj.employeeattendance.pojo.employeelist;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity {

    String emp_name,emp_id,date_from,date_to,monthname,year,noofdays;
    TextView title;
    TableLayout mTableLayout,totaltable;
    ArrayList<String> entrytime = new ArrayList<String>();
    ArrayList<String> exittime = new ArrayList<String>();
    ArrayList<String> day = new ArrayList<String>();
    ArrayList<String> Arr_hours = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        title = findViewById(R.id.title);
        mTableLayout = (TableLayout) findViewById(R.id.tablelog);
        totaltable = (TableLayout) findViewById(R.id.total);


        Intent intent= getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            emp_name = extras.getString("empname");
            emp_id = extras.getString("empid");
            date_to = extras.getString("date_to");
            date_from = extras.getString("date_from");
            monthname = extras.getString("monthname");
            year = extras.getString("year");
            noofdays = extras.getString("noofdays");
        }
        Log.d("details_empname",emp_name);
        Log.d("details_empid",emp_id);
        Log.d("details_date_to",date_to);
        Log.d("details_date_from",date_from);
        Log.d("details_monthname",monthname);

        title.setText(emp_name+"'s Attendence Report for "+monthname+" "+year);
        employeeDetails();
    }



    private void employeeDetails() {
        Retrofit retro = new Retrofit.Builder()
                .baseUrl(api.baseurl)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();
        api retrfit = retro.create(api.class);
        Call<List<employeeDetails>> call;
        call = retrfit.getempDetails(emp_id,date_from,date_to);

        call.enqueue(new Callback<List<employeeDetails>>() {
            @Override
            public void onResponse(Call<List<employeeDetails>> call, retrofit2.Response<List<employeeDetails>> response) {

                if (response.isSuccessful()) {
                    for(int i=0;i<response.body().size();i++){
//                        Log.d("resultsdet", response.body().get(i).getEntryAt());
//                        Log.d("resultsdet", response.body().get(i).getExitAt());
                        //adding  name and id as key value pairs..
                    if (response.body().get(i).getEntryAt()!=null && response.body().get(i).getExitAt()!=null) {
                        entrytime.add(response.body().get(i).getEntryAt());
                        exittime.add(response.body().get(i).getExitAt());
                    }else {
                        Toast.makeText(DetailsActivity.this, "Some of the exit values missing", Toast.LENGTH_SHORT).show();

                        break;
                    }

                    }
                    //difference btn entery and exit time
                    Timedifference(entrytime,exittime);

                } else {

                    Toast.makeText(DetailsActivity.this, "Failed ", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<employeeDetails>> call, Throwable t) {
                Log.d("errorin:", t.getMessage());
                Toast.makeText(DetailsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void Timedifference(ArrayList<String> entry, ArrayList<String> exit) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parsedDate_entry = null;
        Date parsedDate_exit = null;

        for (int i=0;i<entrytime.size();i++) {
            try {
                parsedDate_entry = dateFormat.parse(entry.get(i));
                parsedDate_exit = dateFormat.parse(exit.get(i));
                day.add(entry.get(i).substring(8,10));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            Timestamp timeStampDate_entry = new Timestamp(parsedDate_entry.getTime());
            Timestamp timeStampDate_exit = new Timestamp(parsedDate_exit.getTime());

            Timestamp timestamp1 = new Timestamp(timeStampDate_entry.getTime());
            Timestamp timestamp2 = new Timestamp(timeStampDate_exit.getTime());
            // get time difference in seconds
            long milliseconds = timestamp2.getTime() - timestamp1.getTime();
            int seconds = (int) milliseconds / 1000;

            // calculate hours minutes and seconds
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            Arr_hours.add(hours + "." + minutes);
            Log.d("resultsdettimedidd", String.valueOf("hours: ->"+Arr_hours));
            Log.d("resultsdettimedidd", String.valueOf("day :-> "+day));

        }
        //since it need needs duplicated valued to be merged i am not using this its commented
        //the same lines is used in totalTable method.

       // addTable(Arr_hours,day);
        totalTable(Arr_hours,day);
        addDubTab(Arr_hours,day);
    }

    private void addDubTab(ArrayList<String> arr_hours, ArrayList<String> day) {
        //this method merges the dublicate values
        ArrayList<String> good_day = new ArrayList<>();
        ArrayList<String> good_hrs = new ArrayList<>();
        ArrayList<String> good_day1 = new ArrayList<>();
        ArrayList<String> good_hrs1 = new ArrayList<>();


        for (int i=0;i<day.size();i++) {

               if (i<day.size()-1) {
                   if (day.get(i).equals(day.get(i + 1))) {
                    good_day.add(day.get(i));
                    good_hrs.add(
                            String.valueOf(Double.parseDouble(arr_hours.get(i))+
                                    Double.parseDouble(arr_hours.get(i+1))));
                    i=i+1;
                   } else {
                       good_day.add(day.get(i));
                       good_hrs.add(arr_hours.get(i));
                   }
               }
        }

        for (int j = 0; j < good_day.size(); j++) {
            if (j < good_day.size() - 1) {
                    if (good_day.get(j).equals(good_day.get(j + 1))) {
                        good_day1.add(good_day.get(j));
                        good_hrs1.add(
                                String.valueOf(Double.parseDouble(good_hrs.get(j)) +
                                        Double.parseDouble(good_hrs.get(j + 1))));
                        j = j + 1;
                    } else {
                        good_day1.add(good_day.get(j));
                        good_hrs1.add(good_hrs.get(j));
                    }
                }
            }


        for (int i=0;i<good_hrs1.size();i++){
            Log.d("good", String.valueOf("hours: ->"+good_hrs1.get(i)));
            Log.d("good", String.valueOf("day :-> "+good_day1.get(i)));
        }
        addTable(good_hrs1,good_day1);
    }

    private void totalTable(ArrayList<String> arr_hours, ArrayList<String> day) {
        //this method is for totaling the log
        Double tthrs = 0.0;

        for (int i=0;i<arr_hours.size();i++){
            tthrs=tthrs+Double.parseDouble(arr_hours.get(i));
        }
        Set<String> set = new HashSet<String>(arr_hours);


        TableRow tableRow = new TableRow(DetailsActivity.this);


        TableRow.LayoutParams fieldparams = new TableRow.LayoutParams(10, TableRow.LayoutParams.MATCH_PARENT, 1.0f);
        tableRow.setLayoutParams(fieldparams);
        tableRow.setBackgroundResource(R.drawable.divider);


        // total hours
        TextView textView = new TextView(DetailsActivity.this);
        textView.setTextSize(20);
        textView.setPadding(0,20,0,20);
        textView.setTextColor(getColor(R.color.colorWhite));
        textView.setGravity(Gravity.CENTER);
        tableRow.addView(textView, fieldparams);
        textView.setText(String.valueOf(new DecimalFormat("#0.00").format(tthrs)));

        // No of days present
        TextView textView2 = new TextView(DetailsActivity.this);
        textView2.setTextSize(20);
        textView2.setPadding(0,20,0,20);
        textView2.setTextColor(getColor(R.color.colorWhite));
        textView2.setGravity(Gravity.CENTER);
        tableRow.addView(textView2, fieldparams);
        textView2.setText(String.valueOf(set.size()));

        // No of days absent
        TextView textView3 = new TextView(DetailsActivity.this);
        textView3.setTextSize(20);
        textView3.setPadding(0,20,0,20);
        textView3.setTextColor(getColor(R.color.colorWhite));
        textView3.setGravity(Gravity.CENTER);
        tableRow.addView(textView3, fieldparams);
        textView3.setText(String.valueOf(Integer.parseInt(noofdays)-set.size()));


        totaltable.addView(tableRow);
    }

    private void addTable(ArrayList<String> arr_hours, ArrayList<String> day) {
        for (int i = 0; i < arr_hours.size(); i++) {
            TableRow tableRow = new TableRow(DetailsActivity.this);

            // Set new table row layout parameters.
//                        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
//                        tableRow.setLayoutParams(layoutParams);

            TableRow.LayoutParams fieldparams = new TableRow.LayoutParams(10, TableRow.LayoutParams.MATCH_PARENT, 1.0f);
            tableRow.setLayoutParams(fieldparams);
            tableRow.setDividerDrawable(getResources().getDrawable(R.drawable.divider));
            tableRow.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING|LinearLayout.SHOW_DIVIDER_MIDDLE|LinearLayout.SHOW_DIVIDER_END);
            tableRow.setBackgroundResource(R.drawable.divider);

            // Add a TextView in the first column.
            TextView textView = new TextView(DetailsActivity.this);
            textView.setTextSize(20);
            textView.setPadding(0,20,0,20);
            textView.setTextColor(getColor(R.color.colorWhite));
            textView.setGravity(Gravity.CENTER);
            tableRow.addView(textView, fieldparams);
            textView.setText(day.get(i));



            // Add a text in the second column
            TextView textView2 = new TextView(DetailsActivity.this);
            textView2.setTextSize(20);
            textView2.setPadding(0,20,0,20);
            textView2.setTextColor(getColor(R.color.colorWhite));
            textView2.setGravity(Gravity.CENTER);
            tableRow.addView(textView2, fieldparams);
            textView2.setText(arr_hours.get(i));


            mTableLayout.addView(tableRow);
        }
    }



}


