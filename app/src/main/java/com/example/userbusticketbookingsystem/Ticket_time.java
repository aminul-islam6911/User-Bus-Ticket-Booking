package com.example.userbusticketbookingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Ticket_time extends AppCompatActivity {
    ArrayList<String> Tickets_time = new ArrayList<>();
    private String date_ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_time);
        ListView listView = findViewById(R.id.TCT_listView);

        date_ref = getIntent().getStringExtra("date_ref");

        FirebaseUser mauth = FirebaseAuth.getInstance().getCurrentUser();
        String User = mauth.getUid();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Tickets_time);
        listView.setAdapter(adapter);

        DatabaseReference User_Time = FirebaseDatabase.getInstance().getReference().child("Tickets").child("User_Time").child(User).child(date_ref);
        User_Time.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String myChildViews = snapshot.getValue(String.class);
                Tickets_time.add(myChildViews);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String time_ref = Tickets_time.get(position);
                Intent in = new Intent(Ticket_time.this, names_user.class);
                in.putExtra("time_ref", time_ref);
                in.putExtra("date_ref", date_ref);
                startActivity(in);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Ticket_time.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}