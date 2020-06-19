package com.example.moneymanager;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Currency.class, Expenditure.class, MoneySource.class, Transaction.class},
                        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract CurrencyDao currencyDao();
    public abstract ExpenditureDao expenditureDao();
    public abstract MoneySourceDao moneySourceDao();
    public abstract TransactionDao transactionDao();
}
