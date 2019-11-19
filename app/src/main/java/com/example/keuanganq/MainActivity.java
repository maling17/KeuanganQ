package com.example.keuanganq;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";
    DatabaseReference reference, reference4;
    ArrayList<HistoryList> historylist;
    private RecyclerView rvHistory;
    private TextView tvUangSkrg;
    private EditText etUangMasuk, etUangkeluar, tanggal_masuk, ket_masuk, tanggal_keluar, ket_keluar;
    private TextView tvNama;
    private SwipeRefreshLayout srlMain;
    private HistoryAdapter adapter;
    SimpleDateFormat dateFormatter;

    private FirebaseRecyclerAdapter<HistoryList, HistoryAdapter> mAdapter;
    private LinearLayoutManager layoutManager;
    private LinearLayout llDompet;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mengambil username local
        getUsernameLocal();

        rvHistory = findViewById(R.id.rv_history);
        tvUangSkrg = findViewById(R.id.tv_uang_skrg);
        tvNama = findViewById(R.id.tv_nama_user);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        srlMain = findViewById(R.id.srl_main);


        Button btnUangMasuk = findViewById(R.id.btn_uang_masuk);
        Button btnUangKeluar = findViewById(R.id.btn_uang_keluar);

        llDompet = findViewById(R.id.ll_dompet);


        layoutManager = new LinearLayoutManager(MainActivity.this);
        rvHistory.setLayoutManager(layoutManager);


        historylist = new ArrayList<HistoryList>();


        srlMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UserKeuanganQ();
                        if (srlMain.isRefreshing()){
                            srlMain.setRefreshing(false);
                        }
                    }
                },2000);
            }
        });


        // untuk tampil recylcer view
        tampilHistory();

        //menampilkan User dan Uangnya sekarang
        UserKeuanganQ();

        //Untuk dialog form uang keluar
        btnUangKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //menampilkan popUp dialog
                PopDialogKeluar();
            }
        });

        //Untuk dialog form uang masuk
        btnUangMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //menampilkan popUp dialog
                PopDialogMasuk();
            }
        });

        //Pindah ke Profile Activity
        llDompet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Nama=tvNama.getText().toString();
                String Uang=tvUangSkrg.getText().toString();
                Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
                profile.putExtra("nama",Nama);
                profile.putExtra("uang",Uang);
                startActivity(profile);
            }
        });


    }


    private void PopDialogMasuk() {
        final Dialog dialog = new Dialog(MainActivity.this);


        dialog.setContentView(R.layout.popup_masuk);
        dialog.setCancelable(true);

        etUangMasuk = dialog.findViewById(R.id.et_uang_masuk);
        tanggal_masuk = dialog.findViewById(R.id.et_tanggal_masuk);
        ket_masuk = dialog.findViewById(R.id.et_keterangan_masuk);
        Button btnPop = dialog.findViewById(R.id.btn_tambah_masuk);
        Button btnTglMsk=dialog.findViewById(R.id.btn_tanggal_masuk);

        tanggal_masuk.setEnabled(false);

        btnTglMsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });



        btnPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpanArusMasuk();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void PopDialogKeluar() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.popup_keluar);
        dialog.setCancelable(true);

        tanggal_keluar = dialog.findViewById(R.id.et_tanggal_keluar);
        ket_keluar = dialog.findViewById(R.id.et_keterangan_keluar);
        etUangkeluar = dialog.findViewById(R.id.et_uang_keluar);
        Button btnTanggalKeluar=dialog.findViewById(R.id.btn_tanggal_keluar);
        Button btnPop = dialog.findViewById(R.id.btn_tambah_keluar);

        tanggal_keluar.setEnabled(false);

        btnTanggalKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialogKeluar();
            }
        });
        btnPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpanArusKeluar();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            popUpExit();
        }
        return super.onKeyDown(keycode, event);
    }

    public void popUpExit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title dialog
        alertDialogBuilder.setTitle("Keluar dari aplikasi?");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Klik Ya untuk keluar!")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // jika tombol diklik, maka akan menutup activity ini
                        moveTaskToBack(true);

                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // jika tombol ini diklik, akan menutup dialog
                        // dan tidak terjadi apa2
                        dialog.cancel();
                    }
                });

        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
    }

    public void SimpanArusMasuk() {
        int uangSkrg = Integer.parseInt(tvUangSkrg.getText().toString());
        final String tanggalMasuk = tanggal_masuk.getText().toString();
        final String ketMasuk = ket_masuk.getText().toString();
        final int uangMasuk = Integer.parseInt(String.valueOf(etUangMasuk.getText()));


        uangSkrg = uangSkrg + uangMasuk;
        final String Uang = String.valueOf(uangSkrg);
        final String UangString = String.valueOf(uangMasuk);

        tvUangSkrg.setText(Uang);

        final String UangSkrg = tvUangSkrg.getText().toString();

        reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(username_key_new)
                .child("history")
                .child(tanggalMasuk);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("tanggal").setValue(tanggalMasuk);
                dataSnapshot.getRef().child("arus_uang").setValue("Masuk");
                dataSnapshot.getRef().child("ket").setValue(ketMasuk);
                dataSnapshot.getRef().child("uang").setValue(UangString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        reference2.child("Users").child(username_key_new).child("uang_skrg").setValue(UangSkrg);
    }

    public void SimpanArusKeluar() {

        int uangSkrg = Integer.parseInt(tvUangSkrg.getText().toString());
        final String tanggalKeluar = tanggal_keluar.getText().toString();
        final String ketKeluar = ket_keluar.getText().toString();
        final int uangKeluar = Integer.parseInt(String.valueOf(etUangkeluar.getText()));

        uangSkrg = uangSkrg - uangKeluar;
        final String Uang = String.valueOf(uangSkrg);
        final String UangString = String.valueOf(uangKeluar);
        tvUangSkrg.setText(Uang);

        final String UangSkrg = tvUangSkrg.getText().toString();


        reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(username_key_new)
                .child("history")
                .child(tanggalKeluar);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("tanggal").setValue(tanggalKeluar);
                dataSnapshot.getRef().child("arus_uang").setValue("Keluar");
                dataSnapshot.getRef().child("ket").setValue(ketKeluar);
                dataSnapshot.getRef().child("uang").setValue(UangString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference();
        reference3.child("Users").child(username_key_new).child("uang_skrg").setValue(UangSkrg);
    }

    public void UserKeuanganQ() {
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvNama.setText("Keuangan " + dataSnapshot.child("username").getValue());
                tvUangSkrg.setText(""+dataSnapshot.child("uang_skrg").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void tampilHistory() {
        reference = FirebaseDatabase.getInstance().getReference();

        // Set up FirebaseRecyclerAdapter with the Query [Start]
        Query query = getQuery(reference);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<HistoryList>()
                .setQuery(query, HistoryList.class)
                .build();
        // [End]

        //Adapter untuk Recycler view nya
        mAdapter = new FirebaseRecyclerAdapter<HistoryList, HistoryAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HistoryAdapter historyAdapter, int i, @NonNull HistoryList historyList) {

                historyAdapter.bindToHistory(historyList);

            }

            @NonNull
            @Override
            public HistoryAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());

                return new HistoryAdapter(inflater.inflate(R.layout.history_item, parent, false));
            }
        };
        mAdapter.notifyDataSetChanged();
        rvHistory.setAdapter(mAdapter);
    }


    // untuk mengambil table di firebase [Start]
    private Query getQuery(DatabaseReference reference4) {
        Query query = reference4.child("Users").child(username_key_new).child("history");
        return query;
    }
    //[End]

    //untuk refresh tampilan recyclerview jika ada data baru masuk [Start]
    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
    //[End]

    private void showDateDialogKeluar(){

        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Initiate DatePicker dialog
         */
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */

                tanggal_keluar.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }

    private void showDateDialog(){

        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Initiate DatePicker dialog
         */
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */

                tanggal_masuk.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }
}

