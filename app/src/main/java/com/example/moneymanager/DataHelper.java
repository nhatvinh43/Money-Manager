package com.example.moneymanager;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ComponentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.google.firebase.firestore.Query.Direction.ASCENDING;
import static com.google.firebase.firestore.Query.Direction.DESCENDING;

public class DataHelper {
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    FirebaseStorage storage;

    public DataHelper() {
        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // User model (lấy ID tự gen từ Authentication)
    public void setUsersCollection(String uID, String fullName, String email, String avatarURL) {
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("email", email);
        user.put("avatarUrl", avatarURL);

        db.collection("users").document(uID).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    //// Not complete (dùng thì edit lại)
    public User getUsers(String uID) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return null;
    }

    // Moneysource model (lấy ID tự gen trên CLoud Firestore)
    public String setMoneySource(final MoneySourceCallBack callBack, String uID, String name, double amount, double limit, String curID, String curName) {
        String msId =  db.collection("moneySources").document().getId();
        Map<String, Object> moneySource = new HashMap<>();
        moneySource.put("userId", uID);
        moneySource.put("moneySourceName", name);
        moneySource.put("amount", amount);
        moneySource.put("limit", limit);
        moneySource.put("currencyId", curID);
        moneySource.put("currencyName", curName);

        final MoneySource ms = new MoneySource(amount, curID, curName, limit, msId, name, uID);
        db.collection("moneySources").document(msId).set(moneySource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ArrayList<MoneySource> arrayList = new ArrayList<>();
                        arrayList.add(ms);
                        callBack.onCallBack(arrayList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callBack.onCallBackFail(e.getMessage());
                    }
                });

        return msId;
    }

    public void getMoneySource(final MoneySourceCallBack callBack, String uID) {
        db.collection("moneySources").whereEqualTo("userId", uID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        if (task.isComplete()){

                        }
                        if (task.isSuccessful()) {
                            final ArrayList<MoneySource> moneySourceList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final MoneySource ms = new MoneySource();
                                ms.setUserId((String) document.getData().get("userId"));
                                ms.setMoneySourceId(document.getId());
                                ms.setMoneySourceName((String) document.getData().get("moneySourceName"));
                                ms.setAmount(document.getDouble("amount"));
                                ms.setLimit(document.getDouble("limit"));
                                ms.setCurrencyId((String) document.getData().get("currencyId"));
                                ms.setCurrencyName((String) document.getData().get("currencyName"));

                                getTransaction(new TransactionCallBack() {
                                    @Override
                                    public void onCallBack(ArrayList<Transaction> list) {
                                        ms.setTransactionsList(list);
                                        moneySourceList.add(ms);

                                        Log.d("------------------------ Size", moneySourceList.size() + "  ---");
                                        if(moneySourceList.size() == task.getResult().size()) {
                                            callBack.onCallBack(moneySourceList);
                                        }
                                    }

                                    @Override
                                    public void onCallBackFail(String message) {

                                    }
                                }, document.getId());
                            }

                        } else {
                            callBack.onCallBackFail(task.getException().getMessage());
                        }
                    }
                });
    }

    public void getMoneySourceWithoutTransactionList(final MoneySourceCallBack callBack, String uID) {
        db.collection("moneySources").whereEqualTo("userId", uID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        if (task.isComplete()){

                        }
                        if (task.isSuccessful()) {
                            final ArrayList<MoneySource> moneySourceList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final MoneySource ms = new MoneySource();
                                ms.setUserId((String) document.getData().get("userId"));
                                ms.setMoneySourceId(document.getId());
                                ms.setMoneySourceName((String) document.getData().get("moneySourceName"));
                                ms.setAmount(document.getDouble("amount"));
                                ms.setLimit(document.getDouble("limit"));
                                ms.setCurrencyId((String) document.getData().get("currencyId"));
                                ms.setCurrencyName((String) document.getData().get("currencyName"));
                                moneySourceList.add(ms);
                            }
                            callBack.onCallBack(moneySourceList);

                        } else {
                            callBack.onCallBackFail(task.getException().getMessage());
                        }
                    }
                });
    }

    public void getMoneySourceById(final SingleMoneySourceCallBack callBack, String MSId){
        final MoneySource moneySource = new MoneySource();
        db.collection("moneySources").document(MSId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            moneySource.setUserId(task.getResult().get("userId").toString());
                            moneySource.setLimit(task.getResult().getDouble("limit"));
                            moneySource.setAmount(task.getResult().getDouble("amount"));
                            moneySource.setMoneySourceName(task.getResult().get("moneySourceName").toString());
                            moneySource.setMoneySourceId(task.getResult().getId());
                            moneySource.setCurrencyName(task.getResult().get("currencyName").toString());
                            moneySource.setCurrencyId(task.getResult().get("currencyId").toString());
                            callBack.onCallBack(moneySource);
                        }else {
                            callBack.onCallBackFailed(task.getException().getMessage());
                        }
                    }
                });
    }

    public void updateMoneySource(MoneySource ms) {
        Map<String, Object> moneySource = new HashMap<>();
        moneySource.put("userId", ms.getUserId());
        moneySource.put("moneySourceName", ms.getMoneySourceName());
        moneySource.put("amount", ms.getAmount());
        moneySource.put("limit", ms.getLimit());
        moneySource.put("currencyId", ms.getCurrencyId());
        moneySource.put("currencyName", ms.getCurrencyName());

        db.collection("moneySources").document(ms.getMoneySourceId()).set(moneySource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("-------- Update moneysource --------", "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("-------- Update moneysource --------", "Fail");
                    }
                });
    }

    public void deleteMoneySource(MoneySource ms){
        db.collection("moneySources").document(ms.getMoneySourceId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("-------- Delete moneysource --------", "Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("-------- Delete moneysource --------", "Fail");
                    }
                });

        for(Transaction trans : ms.getTransactionsList()) {
            deleteTransaction(trans);
        }
    }

    // Transaction model
    public String setTransaction(final TransactionCallBack transCallBack, String description, String expenditureId, String expenditureName, Number transactionAmount, String moneySourceId,
                                    boolean transactionIsIncome, Timestamp transactionTime, boolean isPeriodic){
        String newTransactionId = db.collection("transactions").document().getId();
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("moneySourceId", moneySourceId);
        transaction.put("transactionAmount", transactionAmount);
        transaction.put("transactionIsIncome", transactionIsIncome);
        transaction.put("description", description);
        transaction.put("expenditureId", expenditureId);
        transaction.put("expenditureName", expenditureName);
        transaction.put("transactionTime", transactionTime);
        transaction.put("isPeriodic", isPeriodic);

        final Transaction trans = new Transaction(description, expenditureId, expenditureName, transactionAmount, newTransactionId, moneySourceId, transactionIsIncome, transactionTime, isPeriodic);
        db.collection("transactions").document(newTransactionId).set(transaction)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ArrayList<Transaction> arrayList = new ArrayList<>();
                        arrayList.add(trans);
                        transCallBack.onCallBack(arrayList);
                        Log.d("AddTran", "Successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        transCallBack.onCallBackFail(e.getMessage());
                    }
                });
        return newTransactionId;
    }

    public void setTransactionFromPeriodicTransaction(PeriodicTransaction periodicTransaction) {
        String newTransactionId = db.collection("transactions").document().getId();
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("moneySourceId", periodicTransaction.getMoneySourceId());
        transaction.put("transactionAmount", periodicTransaction.getTransactionAmount().doubleValue());
        transaction.put("transactionIsIncome", periodicTransaction.getTransactionIsIncome());
        transaction.put("description", periodicTransaction.getDescription());
        transaction.put("expenditureId", periodicTransaction.getExpenditureId());
        transaction.put("expenditureName", periodicTransaction.getExpenditureName());
        transaction.put("transactionTime", periodicTransaction.getTransactionTime());
        transaction.put("isPeriodic", true);

//        final Transaction trans = new Transaction();
//        trans.setDescription(periodicTransaction.getDescription());
//        trans.setExpenditureId(periodicTransaction.getExpenditureId());
//        trans.setExpenditureName(periodicTransaction.getExpenditureName());
//        trans.setTransactionAmount(periodicTransaction.getTransactionAmount());
//        trans.setTransactionId(newTransactionId);
//        trans.setMoneySourceId(periodicTransaction.getMoneySourceId());
//        trans.setTransactionIsIncome(periodicTransaction.getTransactionIsIncome());
//        trans.setTransactionTime(periodicTransaction.getTransactionTime());
        db.collection("transactions").document(newTransactionId).set(transaction)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public void getTransaction(final TransactionCallBack transCallBack, String msID) {

        db.collection("transactions").orderBy("transactionTime", DESCENDING).whereEqualTo("moneySourceId", msID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Transaction> transactionList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Transaction ts = new Transaction();
                                ts.setMoneySourceId((String) document.getData().get("moneySourceId"));
                                ts.setTransactionId(document.getId());
                                ts.setTransactionAmount(document.getDouble("transactionAmount"));
                                ts.setTransactionIsIncome((boolean) document.getData().get("transactionIsIncome"));
                                ts.setDescription((String) document.getData().get("description"));
                                ts.setExpenditureId((String) document.getData().get("expenditureId"));
                                ts.setExpenditureName((String) document.getData().get("expenditureName"));
                                ts.setTransactionTime(new Timestamp((document.getDate("transactionTime")).getTime()));
                                ts.setIsPeriodic((boolean) document.getData().get("isPeriodic"));

                                transactionList.add(ts);
                            }

                            transCallBack.onCallBack(transactionList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getTransactionById(final SingleTransactionCallBack singleTransCallBack, String transID) {
        db.collection("transactions").document(transID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            Transaction ts = new Transaction();
                            DocumentSnapshot document = task.getResult();
                            ts.setMoneySourceId((String) document.getData().get("moneySourceId"));
                            ts.setTransactionId(document.getId());
                            ts.setTransactionAmount(document.getDouble("transactionAmount"));
                            ts.setTransactionIsIncome((boolean) document.getData().get("transactionIsIncome"));
                            ts.setDescription((String) document.getData().get("description"));
                            ts.setExpenditureId((String) document.getData().get("expenditureId"));
                            ts.setExpenditureName((String) document.getData().get("expenditureName"));
                            ts.setTransactionTime(new Timestamp((document.getDate("transactionTime")).getTime()));
                            ts.setIsPeriodic((boolean) document.getData().get("isPeriodic"));

                            singleTransCallBack.onCallBack(ts);
                        } else {
                            singleTransCallBack.onCallBackFailed(task.getException().getMessage());
                        }
                    }
                });
    }

    public void updateTransaction(Transaction trans) {
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("moneySourceId", trans.getMoneySourceId());
        transaction.put("transactionAmount", trans.getTransactionAmount());
        transaction.put("transactionIsIncome", trans.getTransactionIsIncome());
        transaction.put("description", trans.getDescription());
        transaction.put("expenditureId", trans.getExpenditureId());
        transaction.put("expenditureName", trans.getExpenditureName());
        transaction.put("transactionTime", trans.getTransactionTime());
        transaction.put("isPeriodic", trans.getIsPeriodic());

        db.collection("transactions").document(trans.getTransactionId()).set(transaction)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("-------- Update transaction --------", "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("-------- Update transacion --------", "Fail");
                    }
                });
    }

    public void deleteTransaction(Transaction trans) {
        db.collection("transactions").document(trans.getTransactionId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("-------- Delete transaction --------", "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("-------- Delete transacion --------", "Fail");
                    }
                });
    }

    // PeriodicTransaction Model
    public String setPeriodicTransaction(final PeriodicTransactionCallBack transCallBack, String description, String expenditureId, String expenditureName, Number transactionAmount, String moneySourceId,
                                 boolean transactionIsIncome, Timestamp transactionTime, String periodicType){
        String newTransactionId = db.collection("periodicTransactions").document().getId();
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("moneySourceId", moneySourceId);
        transaction.put("transactionAmount", transactionAmount);
        transaction.put("transactionIsIncome", transactionIsIncome);
        transaction.put("description", description);
        transaction.put("expenditureId", expenditureId);
        transaction.put("expenditureName", expenditureName);
        transaction.put("transactionTime", transactionTime);
        transaction.put("periodicType", periodicType);

        final PeriodicTransaction trans = new PeriodicTransaction(description, expenditureId, expenditureName, transactionAmount, newTransactionId, moneySourceId, transactionIsIncome, transactionTime, periodicType);
        db.collection("periodicTransactions").document(newTransactionId).set(transaction)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ArrayList<PeriodicTransaction> arrayList = new ArrayList<>();
                        arrayList.add(trans);
                        transCallBack.onCallBack(arrayList);
                        Log.d("AddTran", "Successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        transCallBack.onCallBackFail(e.getMessage());
                    }
                });
        return newTransactionId;
    }

    public void getPeriodicTransaction(final PeriodicTransactionCallBack transCallBack, String userID) {
        getMoneySourceWithoutTransactionList(new MoneySourceCallBack() {
            @Override
            public void onCallBack(final ArrayList<MoneySource> list) {
                db.collection("periodicTransactions").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                ArrayList<PeriodicTransaction> transactionList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String msId = (String) document.getData().get("moneySourceId");

                                    for(MoneySource ms : list) {
                                        if (msId.equals(ms.getMoneySourceId())) {
                                            PeriodicTransaction ts = new PeriodicTransaction();
                                            ts.setMoneySourceId((String) document.getData().get("moneySourceId"));
                                            ts.setTransactionId(document.getId());
                                            ts.setTransactionAmount(document.getDouble("transactionAmount"));
                                            ts.setTransactionIsIncome((boolean) document.getData().get("transactionIsIncome"));
                                            ts.setDescription((String) document.getData().get("description"));
                                            ts.setExpenditureId((String) document.getData().get("expenditureId"));
                                            ts.setExpenditureName((String) document.getData().get("expenditureName"));
                                            ts.setTransactionTime(new Timestamp((document.getDate("transactionTime")).getTime()));
                                            ts.setPeriodicType((String) document.getData().get("periodicType"));

                                            transactionList.add(ts);
                                            break;
                                        }
                                    }
                                }

                                transCallBack.onCallBack(transactionList);
                            }
                        });
            }

            @Override
            public void onCallBackFail(String message) {

            }
        }, userID);
    }

    public void updatePeriodicTransaction(PeriodicTransaction trans) {
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("moneySourceId", trans.getMoneySourceId());
        transaction.put("transactionAmount", trans.getTransactionAmount());
        transaction.put("transactionIsIncome", trans.getTransactionIsIncome());
        transaction.put("description", trans.getDescription());
        transaction.put("expenditureId", trans.getExpenditureId());
        transaction.put("expenditureName", trans.getExpenditureName());
        transaction.put("transactionTime", trans.getTransactionTime());
        transaction.put("periodicType", trans.getPeriodicType());

        db.collection("periodicTransactions").document(trans.getTransactionId()).set(transaction)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("-------- Update periodic transaction --------", "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("-------- Update periodic transacion --------", "Fail");
                    }
                });
    }

    public void deletePeriodicTransaction(PeriodicTransaction trans) {
        db.collection("periodicTransactions").document(trans.getTransactionId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("-------- Delete periodic transaction --------", "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("-------- Delete periodic transacion --------", "Fail");
                    }
                });
    }

    // Avatar model
    public void uploadAvatar(final UserAvatarCallBack userAvatarCallBack, Uri pickedImgUri) {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("userAvatar");
        final StorageReference imageFilePath = mStorage.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            userAvatarCallBack.onCallBack(uri);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            userAvatarCallBack.onCallBackFail(e.toString());
                        }
                    });
            }
        });
    }

    // Currency model
    // Expenditure model
}
