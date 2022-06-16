package com.a39275.wallet.model;

import com.a39275.wallet.config.ConfiguracaoFirebase;
import com.a39275.wallet.helper.UtilizadorFirebase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class Movimentacao {

    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    private double valor;
    private String id;

    public Movimentacao() {
    }

    public void guardar(String mesAno){
        FirebaseUser utilizador = UtilizadorFirebase.getUtilizadorAtual();
        String idUtilizador = utilizador.getUid();

        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("movimentacao")
                .child(idUtilizador)
                .child(mesAno)
                .push()
                .setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
