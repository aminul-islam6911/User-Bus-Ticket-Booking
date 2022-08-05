package com.example.userbusticketbookingsystem;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userbusticketbookingsystem.Interface.IFirebaseLoadDone;
import com.example.userbusticketbookingsystem.Model.BusModel;
import com.example.userbusticketbookingsystem.Model.LocationModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class BuyActivity extends AppCompatActivity implements IFirebaseLoadDone {
    public static String currentDate;
    SearchableSpinner searchableSpinnerdeparture;
    SearchableSpinner searchableSpinnerArrival;
    IFirebaseLoadDone iFirebaseLoadDone;
    List<LocationModel> place;

    private DatabaseReference database;
    private String name_set;
    private TextView Tx_Name, MN_Select_date;
    private Button Confirm;
    private ProgressDialog progressDialog;
    private RecyclerView MNblload;
    private String ArrivalAd, DepartureAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testsearch);
        Initialize();
        TextView_Name();
        FirebaseDataRetrieve();
        SpinnerGetText();
        Confirm.setOnClickListener(v -> {
            if (ArrivalAd.equals(DepartureAd)) {
                Toast.makeText(BuyActivity.this, "Location is repeated", Toast.LENGTH_SHORT).show();
                MNblload.setVisibility(View.GONE);
            } else {
                Searching();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        currentDate = day + "-" + (month + 1) + "-" + year;
        MN_Select_date.setText(currentDate);
    }

    private void Initialize() {
        Tx_Name = findViewById(R.id.MN_Name);
        MN_Select_date = findViewById(R.id.MN_Select_date);
        searchableSpinnerdeparture = findViewById(R.id.MN_Location_Place_Start);
        searchableSpinnerArrival = findViewById(R.id.MN_Location_Place_Destination);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Configuring Environment");
        progressDialog.show();

        //firebase
        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentuser != null;
        String userId = currentuser.getUid();
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        Confirm = findViewById(R.id.MN_Search);

        MNblload = findViewById(R.id.MNblload);
        MNblload.setLayoutManager(new LinearLayoutManager(this));
    }

    private void TextView_Name() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name_set = Objects.requireNonNull(snapshot.child("Name").getValue()).toString();
                Tx_Name.setText(name_set);
                progressDialog.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FirebaseDataRetrieve() {
        DatabaseReference locationref = FirebaseDatabase.getInstance().getReference("Locations");
        iFirebaseLoadDone = this;
        locationref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<LocationModel> place = new ArrayList<>();

                for (DataSnapshot idSnapShot : snapshot.getChildren()) {
                    place.add(idSnapShot.getValue(LocationModel.class));
                }
                iFirebaseLoadDone.onFirebaseLoadSuccess(place);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });
    }

    private void SpinnerGetText() {
        searchableSpinnerdeparture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LocationModel iD = place.get(position);
                DepartureAd = iD.getPlace();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchableSpinnerArrival.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LocationModel iD = place.get(position);
                ArrivalAd = iD.getPlace();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onFirebaseLoadSuccess(List<LocationModel> Locationlist) {
        place = Locationlist;
        List<String> id_list = new ArrayList<>();
        for (LocationModel id : Locationlist) {
            id_list.add(id.getPlace());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, id_list);
            searchableSpinnerdeparture.setAdapter(adapter);
            searchableSpinnerArrival.setAdapter(adapter);
        }
    }

    @Override
    public void onFirebaseLoadFailed(String Message) {

    }

    private void Searching() {
        String dbEveryday = DepartureAd + " " + ArrivalAd;
        DatabaseReference query = FirebaseDatabase.getInstance().getReference().child("Schedule").child(dbEveryday);
        FirebaseRecyclerOptions<BusModel> options = new FirebaseRecyclerOptions.Builder<BusModel>()
                .setQuery(query, BusModel.class)
                .build();

        FirebaseRecyclerAdapter<BusModel, BusAdapter> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BusModel, BusAdapter>(options) {
            @NonNull
            @Override
            public BusAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_view, parent, false);
                return new BusAdapter(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull BusAdapter holder, int position, @NonNull BusModel model) {
                final String pos = getRef(position).getKey();
                query.child(pos).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String Destination = Objects.requireNonNull(snapshot.child("Destination").getValue()).toString();
                        final String ArrivalTime = Objects.requireNonNull(snapshot.child("ArrivalTime").getValue()).toString();
                        final String Start = Objects.requireNonNull(snapshot.child("Start").getValue()).toString();
                        final String BusNo = Objects.requireNonNull(snapshot.child("BusNo").getValue()).toString();
                        final String StartingTime = Objects.requireNonNull(snapshot.child("StartingTime").getValue()).toString();
                        final String BusType = Objects.requireNonNull(snapshot.child("BusType").getValue()).toString();
                        final String TicketPrice = Objects.requireNonNull(snapshot.child("TicketPrice").getValue()).toString();

                        holder.txtArrivalTime.setText("Arrival Time :" + ArrivalTime);
                        holder.txtBusNo.setText("Bus No :" + BusNo);
                        holder.txtBusType.setText("Bus Type :" + BusType);
                        holder.txtDestination.setText("Destination :" + Destination);
                        holder.txtStart.setText("Start :" + Start);
                        holder.txtStartingTime.setText("Start Time:" + StartingTime);
                        holder.txtTicketPrice.setText("Tk :" + TicketPrice);

                        holder.cardView.setOnClickListener(v -> {
                            Intent in = new Intent(BuyActivity.this, BookTicket.class);
                            in.putExtra("ArrivalTime", ArrivalTime);
                            in.putExtra("StartingTime", StartingTime);
                            in.putExtra("BusNo", BusNo);
                            in.putExtra("Date", currentDate);
                            in.putExtra("BusType", BusType);
                            in.putExtra("Starting", Start);
                            in.putExtra("Destination", Destination);
                            in.putExtra("Price", TicketPrice);
                            startActivity(in);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };
        MNblload.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        MNblload.setVisibility(View.VISIBLE);
    }
}