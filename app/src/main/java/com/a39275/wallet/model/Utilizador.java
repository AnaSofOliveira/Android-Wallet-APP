package com.a39275.wallet.model;

import com.a39275.wallet.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Utilizador {

    private String nome;
    private String email;
    private String password;
    private String idUtilizador;
    private String srcFoto = "";
    private boolean isPremium;
    private Double receitaTotal = 0.00;
    private Double despesaTotal = 0.00;


    public Utilizador() {
    }

    public void guardar(){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("utilizadores")
                .child(this.idUtilizador)
                .setValue(this);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Exclude
    public String getIdUtilizador() {
        return idUtilizador;
    }

    public void setIdUtilizador(String idUtilizador) {
        this.idUtilizador = idUtilizador;
    }

    public String getSrcFoto() {
        return srcFoto;
    }

    public void setSrcFoto(String srcFoto) {
        this.srcFoto = srcFoto;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public Double getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(Double receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public Double getDespesaTotal() {
        return despesaTotal;
    }

    public void setDespesaTotal(Double despesaTotal) {
        this.despesaTotal = despesaTotal;
    }
}
