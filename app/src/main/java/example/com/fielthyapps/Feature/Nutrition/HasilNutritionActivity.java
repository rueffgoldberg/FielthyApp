package example.com.fielthyapps.Feature.Nutrition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import example.com.fielthyapps.Feature.History.HistoryActivity;
import example.com.fielthyapps.R;

public class HasilNutritionActivity extends AppCompatActivity {
    private TextView tV_pagi,tV_siang,tV_malam;
    private String pagilauk,sianglauk,malamlauk,pagibuah,siangbuah,malambuah,pagisayur,siangsayur,malamsayur,pagimakan,siangmakan,malammakan;
    private BigDecimal intpagilauk,intsianglauk,intmalamlauk,intpagibuah,intsiangbuah,intmalambuah,intpagisayur,intsiangsayur,intmalamsayur,intpagimakan,intsiangmakan,intmalammakan;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore fStore;
    private String id,status;
    String formattedDate;
    BigDecimal hasilpagi,hasilsiang,hasilmalam;
    String ketpagi,ketsiang,ketmalam;
    private Button btn_back;
    private int dataLoadCounter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_nutrition);
        tV_pagi = findViewById(R.id.tV_pagi);
        tV_siang = findViewById(R.id.tV_siang);
        tV_malam = findViewById(R.id.tV_malam);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        btn_back = findViewById(R.id.btn_back_nutrition);
        Intent iin = getIntent();
        final Bundle b = iin.getExtras();

        if (b != null) {
            id = (String) b.get("id");
            status = (String) b.get("status");
        }

        // Mendapatkan tanggal saat ini
        java.time.LocalDateTime currentDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDateTime = java.time.LocalDateTime.now();
        }

        // Mengatur format tanggal
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        }

        // Mengonversi LocalDate ke String
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDate= currentDateTime.format(formatter);
        }




        checktestbuah();
        checktestlauk();
        checktestsayur();
        checktestmakan();








        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("testnutrition")){
                    Intent intent = new Intent(HasilNutritionActivity.this,NutritionActivity.class);
                    intent.putExtra("id",id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else if (status.equals("historynutrition")){
                    Intent intent = new Intent(HasilNutritionActivity.this, HistoryActivity.class);
//                    intent.putExtra("id",id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

            }
        });




    }

    private void checktestlauk(){
        DocumentReference checkdata = fStore.collection("lauk").document(id);
        checkdata.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Dokumen ditemukan, Anda dapat mengakses nilai-nilainya di sini
                    pagilauk = documentSnapshot.getString("porsisatu");
                    sianglauk = documentSnapshot.getString("porsidua");
                    malamlauk= documentSnapshot.getString("porsitiga");
                    // Lakukan apa pun yang perlu Anda lakukan dengan nilai-nilai tersebut
                    if (pagilauk.equals("1/2 Piring")){
                        intpagilauk = BigDecimal.ONE.divide(BigDecimal.valueOf(2), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (pagilauk.equals("1/3 Piring")) {
                        intpagilauk = BigDecimal.ONE.divide(BigDecimal.valueOf(3), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (pagilauk.equals("1/4 Piring")) {
                        intpagilauk = BigDecimal.ONE.divide(BigDecimal.valueOf(4), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (pagilauk.equals("1/6 Piring")) {
                        intpagilauk = BigDecimal.ONE.divide(BigDecimal.valueOf(6), 10, BigDecimal.ROUND_HALF_UP);
                    }else if (pagilauk.equals("1 Piring")){
                        intpagilauk = BigDecimal.ONE;
                    }

                    if (sianglauk.equals("1/2 Piring")){
                        intsianglauk = BigDecimal.ONE.divide(BigDecimal.valueOf(2), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (sianglauk.equals("1/3 Piring")) {
                        intsianglauk = BigDecimal.ONE.divide(BigDecimal.valueOf(3), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (sianglauk.equals("1/4 Piring")) {
                        intsianglauk = BigDecimal.ONE.divide(BigDecimal.valueOf(4), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (sianglauk.equals("1/6 Piring")) {
                        intsianglauk = BigDecimal.ONE.divide(BigDecimal.valueOf(6), 10, BigDecimal.ROUND_HALF_UP);
                    }else if (pagilauk.equals("1 Piring")){
                        intsianglauk = BigDecimal.ONE;
                    }

                    if (malamlauk.equals("1/2 Piring")){
                        intmalamlauk = BigDecimal.ONE.divide(BigDecimal.valueOf(2), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (malamlauk.equals("1/3 Piring")) {
                        intmalamlauk = BigDecimal.ONE.divide(BigDecimal.valueOf(3), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (malamlauk.equals("1/4 Piring")) {
                        intmalamlauk = BigDecimal.ONE.divide(BigDecimal.valueOf(4), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (malamlauk.equals("1/6 Piring")) {
                        intmalamlauk = BigDecimal.ONE.divide(BigDecimal.valueOf(6), 10, BigDecimal.ROUND_HALF_UP);
                    }else if (malamlauk.equals("1 Piring")){
                        intmalamlauk = BigDecimal.ONE;
                    }

                    dataLoadComplete();

                } else {
                    // Dokumen tidak ditemukan
                    Log.d("Firestore", "Dokumen tidak ditemukan.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Penanganan kesalahan jika gagal mengambil data dari Firestore
                Log.e("Firestore", "Gagal mengambil data: " + e.getMessage());
            }
        });





    }

    private void checktestsayur() {
        DocumentReference checkdata = fStore.collection("sayur").document(id);
        checkdata.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Dokumen ditemukan, Anda dapat mengakses nilai-nilainya di sini
                    pagisayur = documentSnapshot.getString("porsisatu");
                    siangsayur = documentSnapshot.getString("porsidua");
                    malamsayur= documentSnapshot.getString("porsitiga");
                    if (pagisayur.equals("1/2 Piring")){
                        intpagisayur = BigDecimal.ONE.divide(BigDecimal.valueOf(2), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (pagisayur.equals("1/3 Piring")) {
                        intpagisayur = BigDecimal.ONE.divide(BigDecimal.valueOf(3), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (pagisayur.equals("1/4 Piring")) {
                        intpagisayur = BigDecimal.ONE.divide(BigDecimal.valueOf(4), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (pagisayur.equals("1/6 Piring")) {
                        intpagisayur = BigDecimal.ONE.divide(BigDecimal.valueOf(6), 10, BigDecimal.ROUND_HALF_UP);
                    }else if (pagisayur.equals("1 Piring")){
                        intpagisayur = BigDecimal.ONE;
                    }

                    if (siangsayur.equals("1/2 Piring")){
                        intsiangsayur = BigDecimal.ONE.divide(BigDecimal.valueOf(2), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (siangsayur.equals("1/3 Piring")) {
                        intsiangsayur = BigDecimal.ONE.divide(BigDecimal.valueOf(3), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (siangsayur.equals("1/4 Piring")) {
                        intsiangsayur = BigDecimal.ONE.divide(BigDecimal.valueOf(4), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (siangsayur.equals("1/6 Piring")) {
                        intsiangsayur = BigDecimal.ONE.divide(BigDecimal.valueOf(6), 10, BigDecimal.ROUND_HALF_UP);
                    }else if (siangsayur.equals("1 Piring")){
                        intsiangsayur = BigDecimal.ONE;
                    }

                    if (malamsayur.equals("1/2 Piring")){
                        intmalamsayur = BigDecimal.ONE.divide(BigDecimal.valueOf(2), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (malamsayur.equals("1/3 Piring")) {
                        intmalamsayur = BigDecimal.ONE.divide(BigDecimal.valueOf(3), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (malamsayur.equals("1/4 Piring")) {
                        intmalamsayur = BigDecimal.ONE.divide(BigDecimal.valueOf(4), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (malamsayur.equals("1/6 Piring")) {
                        intmalamsayur = BigDecimal.ONE.divide(BigDecimal.valueOf(6), 10, BigDecimal.ROUND_HALF_UP);
                    }else if (malamsayur.equals("1 Piring")){
                        intmalamsayur = BigDecimal.ONE;
                    }

                    dataLoadComplete();

                    // Lakukan apa pun yang perlu Anda lakukan dengan nilai-nilai tersebut
                } else {
                    // Dokumen tidak ditemukan
                    Log.d("Firestore", "Dokumen tidak ditemukan.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Penanganan kesalahan jika gagal mengambil data dari Firestore
                Log.e("Firestore", "Gagal mengambil data: " + e.getMessage());
            }
        });
    }
    //
    private void checktestbuah() {
        DocumentReference checkdata = fStore.collection("buah").document(id);
        checkdata.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Dokumen ditemukan, Anda dapat mengakses nilai-nilainya di sini
                    pagibuah = documentSnapshot.getString("porsisatu");
                    siangbuah = documentSnapshot.getString("porsidua");
                    malambuah= documentSnapshot.getString("porsitiga");

                    if (pagibuah.equals("1/2 Piring")){
                        intpagibuah = BigDecimal.ONE.divide(BigDecimal.valueOf(2), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (pagibuah.equals("1/3 Piring")) {
                        intpagibuah = BigDecimal.ONE.divide(BigDecimal.valueOf(3), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (pagibuah.equals("1/4 Piring")) {
                        intpagibuah = BigDecimal.ONE.divide(BigDecimal.valueOf(4), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (pagibuah.equals("1/6 Piring")) {
                        intpagibuah = BigDecimal.ONE.divide(BigDecimal.valueOf(6), 10, BigDecimal.ROUND_HALF_UP);
                    }else if (pagibuah.equals("1 Piring")){
                        intpagibuah = BigDecimal.ONE;
                    }

                    if (siangbuah.equals("1/2 Piring")){
                        intsiangbuah = BigDecimal.ONE.divide(BigDecimal.valueOf(2), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (siangbuah.equals("1/3 Piring")) {
                        intsiangbuah = BigDecimal.ONE.divide(BigDecimal.valueOf(3), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (siangbuah.equals("1/4 Piring")) {
                        intsiangbuah = BigDecimal.ONE.divide(BigDecimal.valueOf(4), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (siangbuah.equals("1/6 Piring")) {
                        intsiangbuah = BigDecimal.ONE.divide(BigDecimal.valueOf(6), 10, BigDecimal.ROUND_HALF_UP);
                    }else if (siangbuah.equals("1 Piring")){
                        intsiangbuah = BigDecimal.ONE;
                    }

                    if (malambuah.equals("1/2 Piring")){
                        intmalambuah = BigDecimal.ONE.divide(BigDecimal.valueOf(2), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (malambuah.equals("1/3 Piring")) {
                        intmalambuah = BigDecimal.ONE.divide(BigDecimal.valueOf(3), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (malambuah.equals("1/4 Piring")) {
                        intmalambuah = BigDecimal.ONE.divide(BigDecimal.valueOf(4), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (malambuah.equals("1/6 Piring")) {
                        intmalambuah = BigDecimal.ONE.divide(BigDecimal.valueOf(6), 10, BigDecimal.ROUND_HALF_UP);
                    }else if (malambuah.equals("1 Piring")){
                        intmalambuah = BigDecimal.ONE;
                    }

                    dataLoadComplete();
                    // Lakukan apa pun yang perlu Anda lakukan dengan nilai-nilai tersebut
                } else {
                    // Dokumen tidak ditemukan
                    Log.d("Firestore", "Dokumen tidak ditemukan.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Penanganan kesalahan jika gagal mengambil data dari Firestore
                Log.e("Firestore", "Gagal mengambil data: " + e.getMessage());
            }
        });
    }
    //
    private void checktestmakan() {
        DocumentReference checkdata = fStore.collection("makanan").document(id);
        checkdata.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Dokumen ditemukan, Anda dapat mengakses nilai-nilainya di sini
                    pagimakan = documentSnapshot.getString("porsisatu");
                    siangmakan = documentSnapshot.getString("porsidua");
                    malammakan= documentSnapshot.getString("porsitiga");

                    if (pagimakan.equals("1/2 Piring")){
                        intpagimakan = BigDecimal.ONE.divide(BigDecimal.valueOf(2), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (pagimakan.equals("1/3 Piring")) {
                        intpagimakan = BigDecimal.ONE.divide(BigDecimal.valueOf(3), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (pagimakan.equals("1/4 Piring")) {
                        intpagimakan = BigDecimal.ONE.divide(BigDecimal.valueOf(4), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (pagimakan.equals("1/6 Piring")) {
                        intpagimakan = BigDecimal.ONE.divide(BigDecimal.valueOf(6), 10, BigDecimal.ROUND_HALF_UP);
                    }else if (pagimakan.equals("1 Piring")){
                        intpagimakan = BigDecimal.ONE;
                    }

                    if (siangmakan.equals("1/2 Piring")){
                        intsiangmakan = BigDecimal.ONE.divide(BigDecimal.valueOf(2), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (siangmakan.equals("1/3 Piring")) {
                        intsiangmakan = BigDecimal.ONE.divide(BigDecimal.valueOf(3), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (siangmakan.equals("1/4 Piring")) {
                        intsiangmakan = BigDecimal.ONE.divide(BigDecimal.valueOf(4), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (siangmakan.equals("1/6 Piring")) {
                        intsiangmakan = BigDecimal.ONE.divide(BigDecimal.valueOf(6), 10, BigDecimal.ROUND_HALF_UP);
                    }else if (siangmakan.equals("1 Piring")){
                        intsiangmakan = BigDecimal.ONE;
                    }

                    if (malammakan.equals("1/2 Piring")){
                        intmalammakan = BigDecimal.ONE.divide(BigDecimal.valueOf(2), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (malammakan.equals("1/3 Piring")) {
                        intmalammakan = BigDecimal.ONE.divide(BigDecimal.valueOf(3), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (malammakan.equals("1/4 Piring")) {
                        intmalammakan = BigDecimal.ONE.divide(BigDecimal.valueOf(4), 10, BigDecimal.ROUND_HALF_UP);
                    } else if (malammakan.equals("1/6 Piring")) {
                        intmalammakan = BigDecimal.ONE.divide(BigDecimal.valueOf(6), 10, BigDecimal.ROUND_HALF_UP);
                    }else if (malammakan.equals("1 Piring")){
                        intmalammakan = BigDecimal.ONE;
                    }

                    dataLoadComplete();
                    // Lakukan apa pun yang perlu Anda lakukan dengan nilai-nilai tersebut
                } else {
                    // Dokumen tidak ditemukan
                    Log.d("Firestore", "Dokumen tidak ditemukan.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Penanganan kesalahan jika gagal mengambil data dari Firestore
                Log.e("Firestore", "Gagal mengambil data: " + e.getMessage());
            }
        });
    }

    // Metode yang akan dipanggil setiap kali data berhasil dimuat
    private void dataLoadComplete() {
        dataLoadCounter++;
        if (dataLoadCounter == 4) {
            calculate();
        }
    }

    private void calculate(){


        hasilpagi = intpagibuah.add(intpagilauk).add(intpagimakan).add(intpagisayur);
        hasilsiang = intsiangbuah.add(intsianglauk).add(intsiangmakan).add(intsiangsayur);
        hasilmalam = intmalambuah.add(intmalamlauk).add(intmalammakan).add(intmalamsayur);


        // Kondisi untuk makanan pagi
        if (hasilpagi.compareTo(BigDecimal.ONE) == 0) {
            ketpagi = "Selamat makan pagi, Anda memenuhi gizi seimbang";
        } else if (hasilpagi.compareTo(BigDecimal.ONE) < 0) {
            ketpagi = "Maaf, Anda kekurangan. Makan pagi Anda tidak memenuhi gizi seimbang";
        } else {
            ketpagi = "Maaf, Anda kelebihan. Makan pagi Anda tidak memenuhi gizi seimbang";
        }

        // Kondisi untuk makanan siang
        if (hasilsiang.compareTo(BigDecimal.ONE) == 0) {
            ketsiang = "Selamat makan siang, Anda memenuhi gizi seimbang";
        } else if (hasilsiang.compareTo(BigDecimal.ONE) < 0) {
            ketsiang = "Maaf, Anda kekurangan. Makan siang Anda tidak memenuhi gizi seimbang";
        } else {
            ketsiang = "Maaf, Anda kelebihan. Makan siang Anda tidak memenuhi gizi seimbang";
        }

        // Kondisi untuk makanan malam
        if (hasilmalam.compareTo(BigDecimal.ONE) == 0) {
            ketmalam = "Selamat makan malam, Anda memenuhi gizi seimbang";
        } else if (hasilmalam.compareTo(BigDecimal.ONE) < 0) {
            ketmalam = "Maaf, Anda kekurangan. Makan malam Anda tidak memenuhi gizi seimbang";
        } else {
            ketmalam = "Maaf, Anda kelebihan. Makan malam Anda tidak memenuhi gizi seimbang";
        }
        tV_pagi.setText(ketpagi);
        tV_siang.setText(ketsiang);
        tV_malam.setText(ketmalam);


    }
}