package com.example.keuanganq;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.ViewHolder {


    private final TextView tvTanggal;
    private final TextView tvUang;
    private final TextView tvKeterangan;
    private final TextView tvArusUang;

    public HistoryAdapter(@NonNull View itemView) {
        super(itemView);

        tvArusUang = itemView.findViewById(R.id.tv_arus_uang);
        tvKeterangan = itemView.findViewById(R.id.tv_keterangan);
        tvUang = itemView.findViewById(R.id.tv_uang);
        tvTanggal = itemView.findViewById(R.id.tv_tanggal_item);
    }
    public void bindToHistory(HistoryList historyList){
        tvArusUang.setText(historyList.arus_uang);
        tvTanggal.setText(historyList.tanggal);
        tvKeterangan.setText(historyList.ket);
        tvUang.setText(historyList.uang);
    }

}
