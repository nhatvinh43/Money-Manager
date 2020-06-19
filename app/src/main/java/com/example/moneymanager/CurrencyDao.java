package com.example.moneymanager;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CurrencyDao {
    @Query("SELECT * FROM currency")
    List<Currency> getAllCurrency();

    @Insert
    void insertAll(Currency ... currencies);
}
