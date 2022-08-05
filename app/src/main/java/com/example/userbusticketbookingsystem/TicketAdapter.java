package com.example.userbusticketbookingsystem;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TicketAdapter extends RecyclerView.ViewHolder {
    TextView txtArrivalTime, txtBusNo, txtBusType, txtDestination,
            txtStart, txtStartingTime, txtTicketPrice, txtTravellerNo, txtDate;
    Button tView,tPrint,tCancel;

    public TicketAdapter(@NonNull View itemView) {
        super(itemView);

        txtArrivalTime = itemView.findViewById(R.id.tArrivalTime);
        txtBusNo = itemView.findViewById(R.id.tBusNo);
        txtDate = itemView.findViewById(R.id.tDate);
        txtBusType = itemView.findViewById(R.id.tBusType);
        txtDestination = itemView.findViewById(R.id.tDestination);
        txtStart = itemView.findViewById(R.id.tStarting);
        txtStartingTime = itemView.findViewById(R.id.tStartingTime);
        txtTicketPrice = itemView.findViewById(R.id.tTicketPrice);
        txtTravellerNo = itemView.findViewById(R.id.tTraveller);
        tView = itemView.findViewById(R.id.tView);
        tPrint = itemView.findViewById(R.id.tPrint);
        tCancel = itemView.findViewById(R.id.tCancel);

    }
}
