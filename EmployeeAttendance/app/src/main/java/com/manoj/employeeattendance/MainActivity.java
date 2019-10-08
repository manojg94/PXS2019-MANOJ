package com.manoj.employeeattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.manoj.employeeattendance.pojo.api;
import com.manoj.employeeattendance.pojo.employeelist;

import java.time.Month;
import java.time.MonthDay;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner sp_employee, sp_year, sp_month;
    ArrayList<Integer> years = new ArrayList<Integer>();
    ArrayList<String> employees = new ArrayList<String>();
    ArrayList<String> months = new ArrayList<String>();
    Map<String,Integer> myMap_Months = new LinkedHashMap<>();
    Map<String,String> myMap_employees = new LinkedHashMap<>();
    Button submit;
    String employeename,date_year,date_month,date_days,emp_id,date_from,date_to,monthname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp_employee = findViewById(R.id.employee);
        sp_year = findViewById(R.id.year);
        sp_month = findViewById(R.id.month);
        submit = findViewById(R.id.submit);

        assigningMonths();
        employees.add("Select employee");

        sp_employee.setOnItemSelectedListener(this);
        sp_year.setOnItemSelectedListener(this);
        sp_month.setOnItemSelectedListener(this);
        submit.setOnClickListener(this);

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1900; i--)
        {
            years.add(i);
        }

        Log.d("thisMonth",String.valueOf(numberOfDaysInMonth(9,2019)));

        employeeList();
        //Creating the ArrayAdapter instance having the employee list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, employees);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_employee.setAdapter(aa);

        //Creating the ArrayAdapter instance having the year list
        ArrayAdapter yy = new ArrayAdapter(this, android.R.layout.simple_spinner_item, years);
        yy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_year.setAdapter(yy);

        //Creating the ArrayAdapter instance having the month list
        ArrayAdapter mm = new ArrayAdapter(this, android.R.layout.simple_spinner_item, months);
        mm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_month.setAdapter(mm);



    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.submit:

                if (!emp_id.equals("null") && !employeename.isEmpty() &&
                        !emp_id.isEmpty() && !employeename.equals("Select employee")){
                    Log.d("submit_empname",employeename);
                    Log.d("submit_empid",emp_id);
                    Log.d("submit_year",date_year);
                    Log.d("submit_month",date_month);
                    date_days = String.valueOf(numberOfDaysInMonth(
                            Integer.parseInt(date_month),Integer.parseInt(date_year)));

                    Log.d("submit_day",date_days);

                    date_from = date_year+"-"+date_month+"-1";
                    date_to = date_year+"-"+date_month+"-"+date_days;

                    Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
                    intent.putExtra("empname",employeename);
                    intent.putExtra("empid",emp_id);
                    intent.putExtra("date_to",date_to);
                    intent.putExtra("date_from",date_from);
                    intent.putExtra("monthname",monthname);
                    intent.putExtra("year",date_year);
                    intent.putExtra("noofdays",date_days);
                    startActivity(intent);

                }else {
                    Toast.makeText(this, "Please select the name of the Employee..", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


        switch (adapterView.getId()) {
            case R.id.employee:
                Toast.makeText(getApplicationContext(), employees.get(i), Toast.LENGTH_LONG).show();
                //getting the key of the on item select of the spinner.
                employeename=employees.get(i);
                //getting the value by key in the Map..and we get the key as in the above line.
                emp_id=String.valueOf(myMap_employees.get(employees.get(i)));
                break;
            case R.id.year:
                Toast.makeText(getApplicationContext(), String.valueOf(years.get(i)), Toast.LENGTH_LONG).show();
                date_year = String.valueOf(years.get(i));
                break;
            case R.id.month:
                Toast.makeText(getApplicationContext(), String.valueOf(myMap_Months.get(months.get(i))), Toast.LENGTH_LONG).show();
                date_month = String.valueOf(myMap_Months.get(months.get(i)));
                monthname = months.get(i);
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void employeeList() {
        Retrofit retro = new Retrofit.Builder()
                .baseUrl(api.baseurl)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();
        api retrfit = retro.create(api.class);
        Call<List<employeelist>> call;
        call = retrfit.getempList();

        call.enqueue(new Callback<List<employeelist>>() {
            @Override
            public void onResponse(Call<List<employeelist>> call, retrofit2.Response<List<employeelist>> response) {

                if (response.isSuccessful()) {
                    for(int i=0;i<response.body().size();i++){
                        Log.d("results", response.body().get(i).getName());
                        //adding  name and id as key value pairs..
                        myMap_employees.put(response.body().get(i).getName(),response.body().get(i).getEmpId());

                    }
                    //getting only the keys of the employee that we added into the map.becoz for the spinner.
                    for ( String key : myMap_employees.keySet() ) {
                        employees.add(key);
                    }
                } else {

                    Toast.makeText(MainActivity.this, "Failed ", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<employeelist>> call, Throwable t) {
                Log.d("errorin:", t.getMessage());
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }




    public static int numberOfDaysInMonth(int month, int year) {
        Calendar monthStart = new GregorianCalendar(year, month, 1);
        return monthStart.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


    private void assigningMonths() {

        myMap_Months.put("January",0);
        myMap_Months.put("February",1);
        myMap_Months.put("March",2);
        myMap_Months.put("April",3);
        myMap_Months.put("May",4);
        myMap_Months.put("June",5);
        myMap_Months.put("July",6);
        myMap_Months.put("August",7);
        myMap_Months.put("September",8);
        myMap_Months.put("October",9);
        myMap_Months.put("November",10);
        myMap_Months.put("December",11);

        for ( String key : myMap_Months.keySet() ) {
            months.add(key);
        }
    }


}
