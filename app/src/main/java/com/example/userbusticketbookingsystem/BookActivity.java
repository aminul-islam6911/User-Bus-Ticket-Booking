package com.example.userbusticketbookingsystem;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userbusticketbookingsystem.Interface.IFirebaseLoadDone;
import com.example.userbusticketbookingsystem.Model.IDs;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class BookActivity extends AppCompatActivity implements IFirebaseLoadDone, DatePickerDialog.OnDateSetListener {
    SearchableSpinner searchableSpinnerdeparture;
    SearchableSpinner searchableSpinnerArrival;
    private DatabaseReference locationref;
    IFirebaseLoadDone iFirebaseLoadDone;
    List<IDs> iDs;

    private FirebaseUser currentuser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database, loadbusSp;
    private String UserId, name_set;
    private TextView Tx_Name;
    private Button MN_Select_date, Confirm;
    private ProgressDialog progressDialog;
    private String Finaldate, dbDate;
    int day, month, year, dayfinal, monthfinal, yearfinal;
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
                List<IDs> iDs = new ArrayList<>();

                for (DataSnapshot idSnapShot : dataSnapshot.getChildren()) {
                    iDs.add(idSnapShot.getValue(IDs.class));
                }
                iFirebaseLoadDone.onFirebaseLoadSuccess(iDs);
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
                IDs iD = iDs.get(position);
                DepartureAd = iD.getPlace();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchableSpinnerArrival.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                IDs iD = iDs.get(position);
                ArrivalAd = iD.getPlace();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onFirebaseLoadSuccess(List<IDs> Locationlist) {
        iDs = Locationlist;
        List<String> id_list = new ArrayList<>();
        for (IDs id : Locationlist) {
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
            loadbusSp = FirebaseDatabase.getInstance().getReference().child("Buses").child(dbDate);
            FirebaseRecyclerAdapter<BusModel, loadView> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BusModel, loadView>(
                    BusModel.class,
                    R.layout.bus_view,
                    loadView.class,
                    loadbusSp
            ) {
                @Override
                protected void populateViewHolder(final loadView viewHolder, BusModel model, int position) {
                    final String Blid = getRef(position).getKey();
                    loadbusSp.child(Blid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String Destination = dataSnapshot.child("Destination").getValue().toString();
                            final String ArrivalTime = dataSnapshot.child("ArrivalTime").getValue().toString();
                            final String Start = dataSnapshot.child("Start").getValue().toString();
                            final String BusNo = dataSnapshot.child("BusNo").getValue().toString();
                            final String StartingTime = dataSnapshot.child("StartingTime").getValue().toString();
                            final String BusType = dataSnapshot.child("BusType").getValue().toString();
                            final String NoOfSit = dataSnapshot.child("NumberOfSeat").getValue().toString();
                            final String Ticketprice = dataSnapshot.child("TicketPrice").getValue().toString();

                            viewHolder.settextBusno(BusNo);
                            viewHolder.settextLocationEnd(ArrivalAd);
                            viewHolder.settextLocationStart(DepartureAd);
                            viewHolder.settextSeatType(BusType);
                            viewHolder.settextTimeStart(StartingTime);
                            viewHolder.setTextTimeEnd(ArrivalTime);
                            viewHolder.settextPrice(Ticketprice);

                            viewHolder.blCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent in = new Intent(BookActivity.this, BookTicket.class);
                                    in.putExtra("ArrivalTime", ArrivalTime);
                                    in.putExtra("StartingTime", StartingTime);
                                    in.putExtra("BusNo", BusNo);
                                    in.putExtra("Date", Finaldate);
                                    in.putExtra("NumberOfSeat", NoOfSit);
                                    in.putExtra("BusType", BusType);
                                    in.putExtra("Starting", Start);
                                    in.putExtra("Destination", Destination);
                                    in.putExtra("Price", Ticketprice);
                                    startActivity(in);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            };
            MNblload.setAdapter(firebaseRecyclerAdapter);
            MNblload.setVisibility(View.VISIBLE);
        }
    }
}