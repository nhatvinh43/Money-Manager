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
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DataHelper {
    FirebaseFirestore db;

    public DataHelper() {
        db = FirebaseFirestore.getInstance();
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

        db.collection("moneySources").document(msId).set(moneySource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callBack.onCallBack(new ArrayList<MoneySource>());
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
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()){

                        }
                        if (task.isSuccessful()) {
                            ArrayList<MoneySource> moneySourceList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final MoneySource ms = new MoneySource();
                                ms.setUserId((String) document.getData().get("userId"));
                                ms.setMoneySourceId(document.getId());
                                ms.setMoneySourceName((String) document.getData().get("moneySourceName"));
                                ms.setAmount(document.getDouble("amount"));
                                ms.setLimit(document.getDouble("limit"));
                                ms.setCurrencyId((String) document.getData().get("currencyId"));
                                ms.setCurrencyName((String) document.getData().get("currencyName"));
                                ms.setTransactionsList(getTransaction(document.getId()));

                                moneySourceList.add(ms);
                            }

                            callBack.onCallBack(moneySourceList);
                        } else {
                            callBack.onCallBackFail(task.getException().getMessage());
                        }
                    }
                });
    }

    // Transaction model
    public void setTransaction(String msID, double amount, boolean isIncome, String description, String expenditureId, String expenditureName, Timestamp transTime) {
        String tsId =  db.collection("transaction").document().getId();
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("moneySourceId", msID);
        transaction.put("transactionAmount", amount);
        transaction.put("transactionIsIncome", isIncome);
        transaction.put("description", description);
        transaction.put("expenditureId", expenditureId);
        transaction.put("expenditureName", expenditureName);
        transaction.put("transactionTime", transTime);

        db.collection("transactions").document(tsId).set(transaction)
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

    public ArrayList<Transaction> getTransaction(String msID) {
        final ArrayList<Transaction> transactionList = new ArrayList<>();

        db.collection("transactions").whereEqualTo("moneySourceId", msID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Transaction ts = new Transaction();
                                ts.setMoneySourceId((String) document.getData().get("moneySourceId"));
                                ts.setTransactionId(document.getId());
                                ts.setTransactionAmount((double) document.getData().get("transactionAmount"));
                                ts.setTransactionIsIncome((boolean) document.getData().get("transactionIsIncome"));
                                ts.setDescription((String) document.getData().get("Description"));
                                ts.setExpenditureId((String) document.getData().get("expenditureId"));
                                ts.setExpenditureName((String) document.getData().get("expenditureName"));
                                ts.setTransactionTime(new Timestamp(((Date) document.getDate("transactionTime")).getTime()));

                                transactionList.add(ts);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return transactionList;
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
    //get List MoneySource
    public ArrayList<MoneySource> getListMoneySouce(String uID){
        final ArrayList<MoneySource> moneySources = new ArrayList<>();
        db.collection("moneySources").whereEqualTo("userId", uID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //if !task.isComplete ....
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                MoneySource moneySource = new MoneySource();
                                moneySource.setUserId(document.get("userId").toString());
                                moneySource.setMoneySourceId(document.getId().toString());
                                try {
                                    moneySource.setLimit(NumberFormat.getInstance().parse(document.get("amount").toString()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    moneySource.setLimit(NumberFormat.getInstance().parse(document.get("limit").toString()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                moneySource.setMoneySourceName(document.get("moneySourceName").toString());
                                moneySource.setCurrencyId(document.get("currencyId").toString());
                                moneySource.setCurrencyName(document.get("currencyName").toString());
                                moneySources.add(moneySource);
                            }
                        }
                        else {
                            Log.d("GetListMoneySource", "Error getting documents: " + task.getException());
                        }
                    }
                });
        return moneySources;
    }
    public String createMoneySouce(String uId, String moneySourceName, Number amount, Number limit,
                                   String currencyId, String currencyName){
        String newMoneySourceId = db.collection("moneySources").document().getId();
        Map<String, Object> moneySource = new HashMap<>();
        moneySource.put("amount", amount);
        moneySource.put("currencyId", currencyId);
        moneySource.put("currencyName", currencyName);
        moneySource.put("limit", limit);
        moneySource.put("moneySourceName", moneySourceName);
        moneySource.put("userId", uId);

        db.collection("moneySources").document(newMoneySourceId).set(moneySource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("CreateNewMoneySource", "DocumentSnapshot successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("CreateNewMoneySource", "Error writing document", e);
                    }
        });
        return newMoneySourceId;
    }
    public MoneySource getMoneySourceById(String MSId){
        MoneySource moneySource = new MoneySource();
        db.collection("moneySources").document(MSId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<MoneySource> moneySources = new ArrayList<>();

                        }
                    }
                });
        return  moneySource;
    }

    // Currency model
    // Expenditure model
}
