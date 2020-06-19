package com.example.moneymanager;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExpenditureDao {
    @Query("SELECT * FROM expenditure")
    List<Expenditure> getAllExpenditure();

    @Insert
    void insertAll(Expenditure ... expenditures);
}
