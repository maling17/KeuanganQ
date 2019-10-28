package com.example.keuanganq;

public class HistoryList {


    String uang;
    String tanggal;
    String ket;
    String arus_uang;

    public HistoryList() {
    }

    public HistoryList(String uang, String tanggal, String ket, String arus_uang) {
        this.uang = uang;
        this.tanggal = tanggal;
        this.ket = ket;
        this.arus_uang = arus_uang;
    }

    public String getArus_uang() {
        return arus_uang;
    }

    public void setArus_uang(String arus_uang) {
        this.arus_uang = arus_uang;
    }

    public String getUang() {
        return uang;
    }

    public void setUang(String uang) {
        this.uang = uang;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }


}
