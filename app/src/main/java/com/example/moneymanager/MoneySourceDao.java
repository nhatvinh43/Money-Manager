package com.example.moneymanager;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MoneySourceDao {
    @Query("SELECT * FROM moneysource")
    List<MoneySource> getAllMoneySource();

    @Insert
    void insertAll(MoneySource ... moneySources);
}
