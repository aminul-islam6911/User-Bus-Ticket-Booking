package com.example.userbusticketbookingsystem;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class BookActivity extends AppCompatActivity implements IFirebaseLoadDone, DatePickerDialog.OnDateSetListener {
    SearchableSpinner searchableSpinnerdeparture;
    SearchableSpinner searchableSpinnerArrival;
    IFirebaseLoadDone iFirebaseLoadDone;
    List<LocationModel> place;
    int day, month, year, dayfinal, monthfinal, yearfinal;
    private DatabaseReference locationref;
    private FirebaseUser currentuser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database, loadbusSp;
    private String UserId, name_set;
    private TextView Tx_Name;
    private Button MN_Select_date, Confirm;
    private ProgressDialog progressDialog;
    private String Finaldate, dbDate;
    private RecyclerView MNblload;
    private GridLayoutManager gridLayoutManager;
    private String ArrivalAd, DepartureAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        Initialize();
        TextView_Name();
        FirebaseDataRetrieve();
        SpinnerGetText();
        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ArrivalAd.equals(DepartureAd)) {
                    Toast.makeText(BookActivity.this, "Location is repeated", Toast.LENGTH_SHORT).show();
                    MNblload.setVisibility(View.GONE);
                } else {
                    Searching();
                }
            }
        });
        MN_Select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(BookActivity.this,
                        BookActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Finaldate = "Empty";
    }

    private void Initialize() {
        Tx_Name = (TextView) findViewById(R.id.MN_Name);
        searchableSpinnerdeparture = (SearchableSpinner) findViewById(R.id.MN_Location_Place_Start);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Configuring Environment");
        progressDialog.show();

        //firebase
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        UserId = currentuser.getUid();
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(UserId);
        searchableSpinnerArrival = findViewById(R.id.MN_Location_Place_Destination);

        MN_Select_date = findViewById(R.id.MN_Select_date);
        Confirm = findViewById(R.id.MN_Search);

        gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);

        MNblload = findViewById(R.id.MNblload);
        MNblload.setHasFixedSize(false);
        MNblload.setLayoutManager(gridLayoutManager);
    }

    private void TextView_Name() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name_set = dataSnapshot.child("Name").getValue().toString();
                Tx_Name.setText(name_set);
                progressDialog.cancel();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void FirebaseDataRetrieve() {
        locationref = FirebaseDatabase.getInstance().getReference("Locations");
        iFirebaseLoadDone = this;
        locationref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<LocationModel> place = new ArrayList<>();

                for (DataSnapshot idSnapShot : dataSnapshot.getChildren()) {
                    place.add(idSnapShot.getValue(LocationModel.class));
                }
                iFirebaseLoadDone.onFirebaseLoadSuccess(place);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearfinal = year;
        monthfinal = month + 1;
        dayfinal = dayOfMonth;
        Finaldate = (dayfinal + "-" + monthfinal + "-" + yearfinal);
        MN_Select_date.setText(Finaldate);
    }

    public void Searching() {
        if (Finaldate.equals("Empty")) {
            Toast.makeText(this, "Please Select Date", Toast.LENGTH_SHORT).show();
        } else {
            dbDate = DepartureAd + " " + ArrivalAd;
            DatabaseReference query = FirebaseDatabase.getInstance().getReference().child("Schedule").child(dbDate);
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
                                Intent in = new Intent(BookActivity.this, BookTicket.class);
                                in.putExtra("ArrivalTime", ArrivalTime);
                                in.putExtra("StartingTime", StartingTime);
                                in.putExtra("BusNo", BusNo);
                                in.putExtra("Date", Finaldate);
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
}