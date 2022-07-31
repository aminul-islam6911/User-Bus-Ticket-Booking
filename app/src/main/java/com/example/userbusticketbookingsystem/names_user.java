package com.example.userbusticketbookingsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class names_user extends AppCompatActivity {
    ArrayList<String> arrayList = new ArrayList<>();
    DatabaseReference User_Tickets_Search, remove_user_time, remove_admin_time, remove_admin_Tickets_Search, seat_update;
    private ListView listView;
    private String date_ref, time_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_names_user);

        listView = findViewById(R.id.NM_listView);
        Button button = findViewById(R.id.btnTicDelete);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String User = firebaseUser.getUid();

        date_ref = getIntent().getStringExtra("date_ref");
        time_ref = getIntent().getStringExtra("time_ref");

        User_Tickets_Search = FirebaseDatabase.getInstance().getReference().child("Tickets").child("User_Tickets_Search").child(User).child(date_ref).child(time_ref);
        remove_user_time = FirebaseDatabase.getInstance().getReference().child("Tickets").child("User_Time").child(User).child(date_ref).child(time_ref);
        remove_admin_time = FirebaseDatabase.getInstance().getReference().child("Tickets").child("Admin_Time").child(date_ref).child(time_ref);
        remove_admin_Tickets_Search = FirebaseDatabase.getInstance().getReference().child("Tickets").child("Admin_Ticket_Search").child(date_ref).child(time_ref);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);


        User_Tickets_Search.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.getValue(String.class);
                arrayList.add(value);
                arrayAdapter.notifyDataSetChanged();
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(names_user.this);
                builder.setMessage("Are you sure you want to Delete?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                User_Tickets_Search.removeValue();
                                remove_user_time.removeValue();
                                remove_admin_Tickets_Search();
                                remove_admin_time();
                                Intent intent = new Intent(names_user.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void remove_admin_time() {
        remove_admin_time.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                remove_admin_time.removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void remove_admin_Tickets_Search() {
        remove_admin_Tickets_Search.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                remove_admin_Tickets_Search.removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}