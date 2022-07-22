package com.example.userbusticketbookingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Payment extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private DatabaseReference confirmTicket;
    private Button button;
    private String stBusNo, stDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        stBusNo = getIntent().getStringExtra("BusNo");
        stDate = getIntent().getStringExtra("Date");

        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookTickets();
                BookTicket.TimerCancel();
                Intent intent = new Intent(Payment.this, Tickets_booked.class);
                startActivity(intent);
            }
        });
    }

    private void BookTickets() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String User = firebaseUser.getUid();
        confirmTicket = FirebaseDatabase.getInstance().getReference().
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
}