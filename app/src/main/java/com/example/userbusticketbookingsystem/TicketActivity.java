package com.example.userbusticketbookingsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userbusticketbookingsystem.Model.TicketModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TicketActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private String User, stGetSeats, stDateId, stBusNo, stTravellerNo, stStart, stDestination, stDate, stTime;
    private TextView start, des, busNo, traveller, date;
    private FirebaseRecyclerAdapter<TicketModel, TicketAdapter> firebaseRecyclerAdapter;
    private DatabaseReference seatUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        User = firebaseUser.getUid();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference query = FirebaseDatabase.getInstance().getReference().child("Tickets").child("Users").child(User);
        FirebaseRecyclerOptions<TicketModel> options = new FirebaseRecyclerOptions.Builder<TicketModel>()
                .setQuery(query, TicketModel.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TicketModel, TicketAdapter>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull TicketAdapter holder, @SuppressLint("RecyclerView") final int position, @NonNull TicketModel model) {
                holder.txtArrivalTime.setText("Arrival Time :" + model.getArrivalTime());
                holder.txtBusNo.setText("Bus No :" + model.getBusNo());
                holder.txtDate.setText("Date :" + model.getDate());
                holder.txtBusType.setText("Bus Type :" + model.getBusType());
                holder.txtDestination.setText("Destination :" + model.getDestination());
                holder.txtStart.setText("Start :" + model.getStart());
                holder.txtStartingTime.setText("Start Time:" + model.getStartingTime());
                holder.txtTicketPrice.setText("Tk :" + model.getTicketPrice());
                holder.txtTravellerNo.setText("Total Traveller :" + model.getNoOfTraveller());

                holder.tCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DialogPlus dialogPlus = DialogPlus.newDialog(holder.tCancel.getContext())
                                .setContentHolder(new ViewHolder(R.layout.ticket_cancel_view))
                                .setExpanded(true, 650).create();

                        View view = dialogPlus.getHolderView();
                        start = view.findViewById(R.id.cStart);
                        des = view.findViewById(R.id.cDestination);
                        busNo = view.findViewById(R.id.cBusNo);
                        traveller = view.findViewById(R.id.cTraveller);
                        date = view.findViewById(R.id.cDate);
                        Button cancel = view.findViewById(R.id.btnCancel);

                        start.setText(model.getStart());
                        des.setText(model.getDestination());
                        busNo.setText(model.getBusNo());
                        traveller.setText(model.getNoOfTraveller());
                        date.setText(model.getDate());

                        stStart = start.getText().toString();
                        stDestination = des.getText().toString();
                        stBusNo = busNo.getText().toString();
                        stTravellerNo = traveller.getText().toString();
                        stDate = date.getText().toString();

                        stDateId = (stStart + " " + stDestination);

                        seatUp = FirebaseDatabase.getInstance().getReference().child("Schedule")
                                .child(stDateId).child(stBusNo);

                        seatUp.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                stGetSeats = Objects.requireNonNull(snapshot.child("NumberOfSeat").getValue()).toString();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        dialogPlus.show();

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseDatabase.getInstance().getReference().child("Tickets").child("Users").child(User)
                                        .child(Objects.requireNonNull(getRef(position).getKey())).removeValue();

                                FirebaseDatabase.getInstance().getReference().child("Tickets").child("Admin").child("On " + stDate + " by bus no " + stBusNo)
                                        .child(Objects.requireNonNull(getRef(position).getKey())).removeValue();

                                seatUpdate();
                                Toast.makeText(TicketActivity.this, "Your ticket canceled successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(TicketActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                dialogPlus.dismiss();
                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public TicketAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_view, parent, false);
                return new TicketAdapter(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void seatUpdate() {
        int i = Integer.parseInt(stGetSeats) + Integer.parseInt(stTravellerNo);
        String s = Integer.toString(i);
        Map<String, Object> upSeats = new HashMap<>();
        upSeats.put("NumberOfSeat", s);
        seatUp.updateChildren(upSeats);
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}