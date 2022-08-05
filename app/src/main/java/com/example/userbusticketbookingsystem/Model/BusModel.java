package com.example.userbusticketbookingsystem.Model;

public class BusModel {
    String ArrivalTime,BusNo,BusType,Destination,Start,StartingTime,TicketPrice;

    public BusModel(String arrivalTime, String busNo, String busType, String destination, String numberOfSeat, String start, String startingTime, String ticketPrice) {
        ArrivalTime = arrivalTime;
        BusNo = busNo;
        BusType = busType;
        Destination = destination;
        Start = start;
        StartingTime = startingTime;
        TicketPrice = ticketPrice;
    }

    public BusModel() {
    }

    public String getArrivalTime() {
        return ArrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        ArrivalTime = arrivalTime;
    }

    public String getBusNo() {
        return BusNo;
    }

    public void setBusNo(String busNo) {
        BusNo = busNo;
    }

    public String getBusType() {
        return BusType;
    }

    public void setBusType(String busType) {
        BusType = busType;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public String getStart() {
        return Start;
    }

    public void setStart(String start) {
        Start = start;
    }

    public String getStartingTime() {
        return StartingTime;
    }

    public void setStartingTime(String startingTime) {
        StartingTime = startingTime;
    }

    public String getTicketPrice() {
        return TicketPrice;
    }

    public void setTicketPrice(String ticketPrice) {
        TicketPrice = ticketPrice;
    }
}
