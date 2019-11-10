package com.example.keuanganq;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

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


        layoutManager = new LinearLayoutManager(ProfileActivity.this);

        rvHistory.setLayoutManager(layoutManager);
        rvHistory.setHasFixedSize(true);

        historylist = new ArrayList<HistoryList>();


        String Nama = getIntent().getStringExtra("nama");
        String Uang = getIntent().getStringExtra("uang");

        tvNama.setText(Nama);
        tvUang.setText(Uang);

        tampilMasukHistory();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
                finish();
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
}
