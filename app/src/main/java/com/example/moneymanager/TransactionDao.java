package com.example.moneymanager;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM `transaction`")
    List<Transaction> getAllTransaction();

    @Insert
    void insertAll(Transaction ... transactions);
}
