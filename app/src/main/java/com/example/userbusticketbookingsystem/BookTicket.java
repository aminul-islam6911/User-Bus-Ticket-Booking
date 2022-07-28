package com.example.userbusticketbookingsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BookTicket extends AppCompatActivity {
    private TextView tvBusNo, tvDate, tvStarting, tvDestination, tvStartingTime, tvArrivalTime,
            tvSeatAvailable, tvBusType;
    private Button btnConfirmBooking, btnAddTraveller, btnSeatReload;
    private String stBusNo, stDate, stStarting, stDestination, stStartingTime, stArrivalTime,
            stSeatAvailable, stBusType, stTicketPrice;
    private FirebaseUser firebaseUser;
    private DatabaseReference seatsNo, confirmTicket;
    private String seatNoRef;
    int noOfSeats;
    EditText edtTraveller;
    ListView listView;
    public static ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    Timer timer;
    public static TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ticket);
        Initialize();
        GetStringFromIntent();
        ProgressDialog configure = new ProgressDialog(this);
        configure.setCancelable(false);
        configure.setTitle("Configuring Environment");
        configure.show();
        SetStringToTextView(configure);

        //Arraylist to call data
        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(BookTicket.this, android.R.layout
                .simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        onButtonClickAdd();

        seatsNo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String v = snapshot.child("NumberOfSeat").getValue(String.class);
                tvSeatAvailable.setText(v);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookTicket.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!arrayList.isEmpty()) {
                    TimerStart();
                    Intent in = new Intent(BookTicket.this, Payment.class);
                    in.putExtra("Date", stDate);
                    in.putExtra("BusNo", stBusNo);
                    in.putExtra("Starting", stStarting);
                    in.putExtra("Destination", stDestination);
                    in.putExtra("StartingTime", stStartingTime);
                    in.putExtra("ArrivalTime", stArrivalTime);
                    in.putExtra("BusType", stBusType);
                    in.putExtra("Price", stTicketPrice);
                    startActivity(in);
                } else {
                    Toast.makeText(BookTicket.this, "Please add passenger name", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void Initialize() {
        tvBusNo = findViewById(R.id.tvBusNo);
        tvDate = findViewById(R.id.tvDate);
        tvStarting = findViewById(R.id.tvStarting);
        tvDestination = findViewById(R.id.tvDestination);
        tvStartingTime = findViewById(R.id.tvStartingTime);
        tvArrivalTime = findViewById(R.id.tvArrivalTime);
        tvSeatAvailable = findViewById(R.id.tvSeatAvailable);
        tvBusType = findViewById(R.id.tvBusType);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        btnAddTraveller = findViewById(R.id.btnAddTraveller);
        edtTraveller = findViewById(R.id.edtTraveller);
        listView = findViewById(R.id.lvTraveller);
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
        seatNoRef = stStarting + " " + stDestination;
        seatsNo = FirebaseDatabase.getInstance().getReference().child("Schedule")
                .child(seatNoRef).child(stBusNo);
    }

    private void SetStringToTextView(ProgressDialog configure) {
        tvBusNo.setText("Bus No :" + stBusNo);
        tvDate.setText("Date :" + stDate);
        tvStarting.setText("From :" + stStarting);
        tvDestination.setText("To :" + stDestination);
        tvStartingTime.setText("Starting Time :" + stStartingTime);
        tvArrivalTime.setText("Arrival Time :" + stArrivalTime);
        //tvSeatAvailable.setText(stSeatAvailable);
        tvBusType.setText("Bus Type :" + stBusType);
        btnConfirmBooking.setText("Confirm(" + stTicketPrice + " Tk)");
        configure.dismiss();
    }

    public void TimerStart() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                int seat = Integer.parseInt(tvSeatAvailable.getText().toString()) + arrayList.size();
                String st = Integer.toString(seat);
                Map<String, Object> upSeats = new HashMap<>();
                upSeats.put("NumberOfSeat", st);
                seatsNo.updateChildren(upSeats).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            tvSeatAvailable.setText(st);
                        }
                    }
                });
                Intent intent = new Intent(BookTicket.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        timer.schedule(timerTask, 60000);
    }

    public static void TimerCancel() {
        timerTask.cancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int seat = Integer.parseInt(tvSeatAvailable.getText().toString()) + arrayList.size();
        String st = Integer.toString(seat);
        Map<String, Object> upSeats = new HashMap<>();
        upSeats.put("NumberOfSeat", st);
        seatsNo.updateChildren(upSeats);
        Intent intent = new Intent(BookTicket.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onButtonClickAdd() {
        btnAddTraveller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = edtTraveller.getText().toString();
                noOfSeats = Integer.parseInt(tvSeatAvailable.getText().toString());
                if (noOfSeats <= 0) {
                    edtTraveller.setText("");
                    Toast.makeText(BookTicket.this, "No seats availble", Toast.LENGTH_SHORT).show();
//                    btnAddTraveller.setClickable(false);
                } else if (result.isEmpty()) {
                    Toast.makeText(BookTicket.this, "Add Passenger name", Toast.LENGTH_SHORT).show();
                } else {
                    arrayList.add(result);
                    adapter.notifyDataSetChanged();
                    edtTraveller.setText("");
                    calculate_price();
                    GetNoOfPSeat();
                }
            }
        });
    }

    private void calculate_price() {
        int number_of_traveller = arrayList.size();
        int Price = Integer.parseInt(stTicketPrice);
        int total_price = Price * number_of_traveller;
        String str_total_price = Integer.toString(total_price);
        btnConfirmBooking.setText("Confirm(" + str_total_price + " Tk)");
    }

    private void GetNoOfPSeat() {
        int new_val = Integer.parseInt(tvSeatAvailable.getText().toString()) - 1;
        String st = Integer.toString(new_val);
        Map<String, Object> upSeats = new HashMap<>();
        upSeats.put("NumberOfSeat", st);
        seatsNo.updateChildren(upSeats).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    tvSeatAvailable.setText(st);
                }
            }
        });
    }
}