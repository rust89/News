package ru.sike.lada;

import android.app.Application;

import ru.sike.lada.utils.GlobalValues;

public class ApplicationController extends Application {

    private GlobalValues mGlobalValues = new GlobalValues();

    public synchronized GlobalValues getGlobalValues() {
        return mGlobalValues;
    }
}
