package com.example.userbusticketbookingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Tickets_booked extends AppCompatActivity {
    ArrayList<String> Tickets = new ArrayList<>();
    private ListView myListview;
    private String seatNoRef,stBusNo,stSeatAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticekets_booked);
        myListview = findViewById(R.id.TC_listView);

        seatNoRef = getIntent().getStringExtra("seatNoRef");
        stBusNo = getIntent().getStringExtra("stBusNo");
        stSeatAvailable = getIntent().getStringExtra("stSeatAvailable");

        FirebaseUser mauth = FirebaseAuth.getInstance().getCurrentUser();
        String User = mauth.getUid();

        final ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Tickets);
        myListview.setAdapter(myArrayAdapter);

        DatabaseReference User_HashCode = FirebaseDatabase.getInstance().getReference().child("Tickets").child("User_HashCode").child(User);
        User_HashCode.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String myChildViews = dataSnapshot.getValue(String.class);
                Tickets.add(myChildViews);
                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String date_ref = Tickets.get(i);
                Intent in = new Intent(Tickets_booked.this, Ticket_time.class);
                in.putExtra("date_ref", date_ref);
                in.putExtra("seatNoRef", seatNoRef);
                in.putExtra("stBusNo", stBusNo);
                in.putExtra("stSeatAvailable", stSeatAvailable);
                startActivity(in);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Tickets_booked.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}