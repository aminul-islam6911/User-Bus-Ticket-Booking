package com.example.userbusticketbookingsystem.Interface;

import com.example.userbusticketbookingsystem.Model.IDs;

import java.util.List;

public interface IFirebaseLoadDone {

    void onFirebaseLoadSuccess(List<IDs> Locationlist);
    void onFirebaseLoadFailed(String Message);

}
