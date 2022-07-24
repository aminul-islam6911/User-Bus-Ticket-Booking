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
    private String stBusNo, stDate, stStarting, stDestination, stStartingTime, stArrivalTime,
            stSeatAvailable, stBusType, stTicketPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        GetStringFromIntent();

        button = findViewById(R.id.button);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User = firebaseUser.getUid();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookTickets();
                CreatePDF();
                BookTicket.TimerCancel();
                Intent intent = new Intent(Payment.this, Tickets_booked.class);
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
        stSeatAvailable = getIntent().getStringExtra("NumberOfSeat");
        stBusType = getIntent().getStringExtra("BusType");
        stTicketPrice = getIntent().getStringExtra("Price");
    }

    private void BookTickets() {
        DatabaseReference confirmTicket = FirebaseDatabase.getInstance().getReference().
                child("Tickets").child(stDate).child(stBusNo).child(User);

        DatabaseReference TicketID = FirebaseDatabase.getInstance().getReference().
                child("Tickets").child("TicketID").child(stDate);
        //Here is the code to store the ticket in TicketID node
        HashMap<String, Object> H_TicketID = new HashMap<>();
        H_TicketID.put(stDate + " " + stBusNo, "On " + stDate + " by bus no " + stBusNo);
        TicketID.updateChildren(H_TicketID);

        DatabaseReference Ticket_HashCode = FirebaseDatabase.getInstance().getReference()
                .child("Tickets").child("Ticket_HashCode").child(User);
        //Ticket_HashCode is stored
        HashMap<String, Object> H_Ticket_HashCode = new HashMap<>();
        H_Ticket_HashCode.put(stDate + " " + stBusNo, "On " + stDate + " by bus no " + stBusNo);
        Ticket_HashCode.updateChildren(H_Ticket_HashCode);

        DatabaseReference Ticket_Check_User_Admin = FirebaseDatabase.getInstance().
                getReference().child("Tickets").child("Ticket_Check_User_Admin")
                .child("On " + stDate + " by bus no " + stBusNo);
        //Here is the code to store Ticket_Check_User_Admin
        int Length = BookTicket.arrayList.size();//Data stored in list
        HashMap<String, Object> H_Ticket_Check_User_Admin = new HashMap<>();//Data stored in list are accessed one by one
        for (int a = 0; a < Length; a++) {
            H_Ticket_Check_User_Admin.put(User + "_Traveller_" + a, BookTicket.arrayList.get(a));
        }
        Ticket_Check_User_Admin.updateChildren(H_Ticket_Check_User_Admin);

        DatabaseReference Ticket_User_Search = FirebaseDatabase.getInstance().
                getReference().child("Tickets").child("Tickets_User_Search").child(User)
                .child("On " + stDate + " by bus no " + stBusNo);
        //Here is the code to store Ticket_User_Search node it will have same hashmap as Ticket_Check_User_Admin
        int l = BookTicket.arrayList.size();//Data stored in list
        HashMap<String, Object> H_Ticket_User_Search = new HashMap<>();//Data stored in list are accessed one by one
        for (int a = 0; a < l; a++) {
            H_Ticket_User_Search.put(User + "_Traveller_" + a, BookTicket.arrayList.get(a));
        }
        Ticket_User_Search.updateChildren(H_Ticket_User_Search);
    }

    private void CreatePDF() {
        String name = String.valueOf(BookTicket.arrayList);
        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Tickets");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
        }
        //Create time stamp
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(date);
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