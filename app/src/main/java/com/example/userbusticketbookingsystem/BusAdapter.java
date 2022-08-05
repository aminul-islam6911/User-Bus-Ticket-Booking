package com.example.userbusticketbookingsystem;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class BusAdapter extends RecyclerView.ViewHolder {
    TextView txtArrivalTime, txtBusNo, txtBusType, txtDestination,
            txtStart, txtStartingTime, txtTicketPrice;
    CardView cardView;

    public BusAdapter(@NonNull View itemView) {
        super(itemView);
        txtArrivalTime = itemView.findViewById(R.id.arrivalTime);
        txtBusNo = itemView.findViewById(R.id.busNo);
        txtBusType = itemView.findViewById(R.id.busType);
        txtDestination = itemView.findViewById(R.id.destination);
        txtStart = itemView.findViewById(R.id.starting);
        txtStartingTime = itemView.findViewById(R.id.startingTime);
        txtTicketPrice = itemView.findViewById(R.id.ticketPrice);
        cardView = itemView.findViewById(R.id.cardView);
    }
}
