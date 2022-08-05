package com.example.userbusticketbookingsystem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Payment extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private Button button;
    private String User;
    private String timeStamp;
    private String arrTostr, noOfbookedSeats;
    private Date date;
    private String stBusNo, stDate, stStarting, stDestination, stStartingTime, stArrivalTime,
            stSeatAvailable, stBusType, stTicketPrice, road_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        GetStringFromIntent();

        date = new Date();
        timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);

        arrTostr = BookTicket.arrayList.toString();
        int noOfSeat = BookTicket.arrayList.size();
        noOfbookedSeats = Integer.toString(noOfSeat);


        button = findViewById(R.id.button);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User = firebaseUser.getUid();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookTickets();
                CreatePDF();
                BookTicket.TimerCancel();
                Intent intent = new Intent(Payment.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void GetStringFromIntent() {
        stBusNo = getIntent().getStringExtra("BusNo");
        stDate = getIntent().getStringExtra("Date");
        stStarting = getIntent().getStringExtra("Starting");
        stDestination = getIntent().getStringExtra("Destination");
        stStartingTime = getIntent().getStringExtra("StartingTime");
        stArrivalTime = getIntent().getStringExtra("ArrivalTime");
        stBusType = getIntent().getStringExtra("BusType");
        stTicketPrice = getIntent().getStringExtra("Price");
        road_ref = getIntent().getStringExtra("road_ref");
        stSeatAvailable = getIntent().getStringExtra("stSeatAvailable");
    }

    private void BookTickets() {
//create date_bus_ref in Tickets --> Admin_HashCode--> date
        DatabaseReference Admin_HashCode = FirebaseDatabase.getInstance().getReference().
                child("Tickets").child("Admin_HashCode").child(stDate);

        HashMap<String, Object> hashCode = new HashMap<>();
        hashCode.put(stDate + " " + stBusNo, "On " + stDate + " by bus no " + stBusNo);
        Admin_HashCode.updateChildren(hashCode);

//create Admin Ticket in Tickets --> Admin --> date_bus_ref
        DatabaseReference Admin_Ticket = FirebaseDatabase.getInstance().getReference()
                .child("Tickets").child("Admin").child("On " + stDate + " by bus no " + stBusNo);
        String a_key = Admin_Ticket.push().getKey();

        HashMap<String, Object> a_map = new HashMap<>();
        a_map.put("Start", stStarting);
        a_map.put("Destination", stDestination);
        a_map.put("Date", stDate);
        a_map.put("StartingTime", stStartingTime);
        a_map.put("ArrivalTime", stArrivalTime);
        a_map.put("BusType", stBusType.toLowerCase());//Converting text to lower case
        a_map.put("BusNo", stBusNo);
        a_map.put("TicketPrice", stTicketPrice);
        a_map.put("Name", arrTostr);
        a_map.put("NoOfTraveller", noOfbookedSeats);
        Admin_Ticket.child(a_key).setValue(a_map);


//create User Ticket in Tickets --> Users --> current_user
        DatabaseReference User_Ticket = FirebaseDatabase.getInstance().getReference()
                .child("Tickets").child("Users").child(User);
        String u_key = User_Ticket.push().getKey();

        HashMap<String, Object> u_map = new HashMap<>();
        u_map.put("Start", stStarting);
        u_map.put("Destination", stDestination);
        u_map.put("Date", stDate);
        u_map.put("StartingTime", stStartingTime);
        u_map.put("ArrivalTime", stArrivalTime);
        u_map.put("BusType", stBusType.toLowerCase());//Converting text to lower case
        u_map.put("BusNo", stBusNo);
        u_map.put("TicketPrice", stTicketPrice);
        u_map.put("Name", arrTostr);
        u_map.put("NoOfTraveller", noOfbookedSeats);
        User_Ticket.child(a_key).setValue(u_map);
    }

    private void CreatePDF() {
        String name = String.valueOf(BookTicket.arrayList);
        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Tickets");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
        }
        File file = new File(pdfFolder + " " + timeStamp + ".pdf");

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(250, 400, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(15f);
        paint.setColor(Color.rgb(255, 0, 0));
        canvas.drawText("Bus Ticket Booking System", pageInfo.getPageWidth() / 2, 30, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(10f);
        paint.setColor(Color.rgb(51, 0, 0));
        canvas.drawText("Bus No : " + stBusNo, 40, 50, paint);
        canvas.drawText("Date : " + stDate, 40, 60, paint);
        canvas.drawText("Starting Place : " + stStarting, 40, 70, paint);
        canvas.drawText("Destination : " + stDestination, 40, 80, paint);
        canvas.drawText("Starting Time : " + stStartingTime, 40, 90, paint);
        canvas.drawText("Arrival Time : " + stArrivalTime, 40, 100, paint);
        canvas.drawText("Bus Type : " + stBusType, 40, 110, paint);
        canvas.drawText("Price : " + stTicketPrice, 40, 120, paint);
        canvas.drawText("Passengers Name -", 40, 150, paint);
        canvas.drawText(name, 40, 160, paint);

        pdfDocument.finishPage(page);
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "PDF created", Toast.LENGTH_SHORT).show();
    }
}