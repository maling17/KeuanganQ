package com.example.keuanganq;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";
    DatabaseReference reference, reference4;
    ArrayList<HistoryList> historylist;

    private HistoryAdapter adapter;

    private FirebaseRecyclerAdapter<HistoryList, HistoryAdapter> mAdapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView rvHistory;
    private LinearLayout btnLl_all, btn_masuk, btn_keluar;
    private TextView tvNama;
    private TextView tvUang;
    private SwipeRefreshLayout srlProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getUsernameLocal();

        rvHistory = findViewById(R.id.rv_history_profile);
        btnLl_all = findViewById(R.id.btn_all_profil);
        btn_masuk = findViewById(R.id.btn_masuk_profil);
        btn_keluar = findViewById(R.id.btn_keluar_profil);

        Button btnLogout = findViewById(R.id.btn_logout);
        tvNama = findViewById(R.id.tv_nama_user_profile);
        tvUang = findViewById(R.id.tv_uang_skrg_profile);
        Button btnReset = findViewById(R.id.btn_reset);
        srlProfil = findViewById(R.id.srl_profile);

        layoutManager = new LinearLayoutManager(ProfileActivity.this);

        rvHistory.setLayoutManager(layoutManager);
        rvHistory.setHasFixedSize(true);

        historylist = new ArrayList<HistoryList>();


       /* String Nama = getIntent().getStringExtra("nama");
        String Uang = getIntent().getStringExtra("uang");

        tvNama.setText(Nama);
        tvUang.setText(Uang);*/

       UserKeuanganQ();

        srlProfil.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UserKeuanganQ();
                        if (srlProfil.isRefreshing()){
                            srlProfil.setRefreshing(false);
                        }
                    }
                },2000);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);

                // set title dialog
                alertDialogBuilder.setTitle("Apakah yakin ingin Log Out?");

                // set pesan dari dialog
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // jika tombol diklik, maka akan menutup activity ini
                                logOut();
                                finish();

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

        });





        btnLl_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "INI BUTTON ALL", Toast.LENGTH_LONG).show();
            }
        });

        btn_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ProfileActivity.this, "INI BUTTON MASUK", Toast.LENGTH_LONG).show();

            }
        });

        btn_keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ProfileActivity.this, "INI BUTTON KELUAR", Toast.LENGTH_LONG).show();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);

                // set title dialog
                alertDialogBuilder.setTitle("Yakin ingin Menghapus History dan Uang?");

                // set pesan dari dialog
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // jika tombol diklik, maka akan menutup activity ini
                                ResetData();
                                ResetUang();

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

    public void tampilMasukHistory() {
        reference = FirebaseDatabase.getInstance().getReference();

        // Set up FirebaseRecyclerAdapter with the Query [Start]
        Query query = getMasukQuery(reference);

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

    public void tampilKeluarHistory() {
        reference = FirebaseDatabase.getInstance().getReference();

        // Set up FirebaseRecyclerAdapter with the Query [Start]
        Query query = getKeluarQuery(reference);

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


    // untuk mengambil table di firebase
    private Query getQuery(DatabaseReference reference4) {
        return reference4.child("Users").child(username_key_new).child("history");
    }


    //untuk memfilter arus uang menjadi masuk
    private Query getMasukQuery(DatabaseReference reference) {
        return reference.child("Users").child(username_key_new).child("history").orderByChild("arus_uang").equalTo("Masuk");
    }


    //untuk memfilter arus uang menjadi keluar
    private Query getKeluarQuery(DatabaseReference reference2) {
        return reference2.child("Users").child(username_key_new).child("history").orderByChild("arus_uang").equalTo("Keluar");
    }


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


    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }

    public void logOut() {
        //simpan ke local storage dulu
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(username_key, null);
        editor.apply();

        Intent Login = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(Login);

    }

    public void ResetData() {
        reference4 = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(username_key_new).child("history");
        reference4.setValue(null);

    }

    public void ResetUang() {
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new).child("uang_skrg");
        reference.setValue("0");
    }

    public void UserKeuanganQ() {
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvNama.setText("Keuangan " + Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString());
                tvUang.setText(Objects.requireNonNull(dataSnapshot.child("uang_skrg").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
