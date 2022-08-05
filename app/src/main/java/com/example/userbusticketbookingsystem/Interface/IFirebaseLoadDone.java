package com.example.userbusticketbookingsystem.Interface;

import com.example.userbusticketbookingsystem.Model.LocationModel;

import java.util.List;

public interface IFirebaseLoadDone {

    void onFirebaseLoadSuccess(List<LocationModel> Locationlist);
    void onFirebaseLoadFailed(String Message);

}
