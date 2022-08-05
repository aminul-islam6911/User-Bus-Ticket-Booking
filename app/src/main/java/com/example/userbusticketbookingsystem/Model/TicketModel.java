package com.example.userbusticketbookingsystem.Model;

public class TicketModel {
    String ArrivalTime,BusNo,BusType,Destination,Date,Start,StartingTime,TicketPrice,NoOfTraveller;

    public TicketModel() {
    }

    public TicketModel(String arrivalTime, String busNo, String busType, String destination,
                       String numberOfSeat, String start, String startingTime, String ticketPrice, String noOfTraveller) {
        ArrivalTime = arrivalTime;
        BusNo = busNo;
        BusType = busType;
        Destination = destination;
        Date = numberOfSeat;
        Start = start;
        StartingTime = startingTime;
        TicketPrice = ticketPrice;
        NoOfTraveller = noOfTraveller;
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

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
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

    public String getNoOfTraveller() {
        return NoOfTraveller;
    }

    public void setNoOfTraveller(String noOfTraveller) {
        NoOfTraveller = noOfTraveller;
    }
}
