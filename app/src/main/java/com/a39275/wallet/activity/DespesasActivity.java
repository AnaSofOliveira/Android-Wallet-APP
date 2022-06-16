package com.a39275.wallet.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.a39275.wallet.R;
import com.a39275.wallet.config.ConfiguracaoFirebase;
import com.a39275.wallet.helper.DateCustom;
import com.a39275.wallet.helper.UtilizadorFirebase;
import com.a39275.wallet.model.Movimentacao;
import com.a39275.wallet.model.Utilizador;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private FloatingActionButton btnGuardar;
    private Movimentacao movimentacao;
    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTotal;
    private Double valorDespesa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoValor = findViewById(R.id.editValor);
        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        btnGuardar = findViewById(R.id.fabGuardar);

        campoData.setText(DateCustom.dataAtual());

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarDespesa(btnGuardar.getRootView());
            }
        });

        obterDespesaTotal();

    }


    private void guardarDespesa(View view){

        if(validarCamposDespesa()) {
            String data = campoData.getText().toString();
            String mesAno = DateCustom.mesAnoData(data);
            valorDespesa = Double.parseDouble(campoValor.getText().toString());

            movimentacao = new Movimentacao();
            movimentacao.setValor(valorDespesa);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(data);
            movimentacao.setTipo("d");

            Double despesaAtualizada = despesaTotal + valorDespesa;

            atualizarDespesa(despesaAtualizada);

            movimentacao.guardar(mesAno);

            finish();

        }
    }

    private void atualizarDespesa(Double despesa) {

        FirebaseUser utilizador = UtilizadorFirebase.getUtilizadorAtual();
        String idUtilizador = utilizador.getUid();

        DatabaseReference refUtilizador = firebase.child("utilizadores").child(idUtilizador);

        refUtilizador.child("despesaTotal").setValue(despesa);

    }

    public boolean validarCamposDespesa(){

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();

        if(!textoValor.isEmpty()){
            if(!textoData.isEmpty()){
                if(!textoCategoria.isEmpty()){
                    if(!textoDescricao.isEmpty()){
                        return true;
                    }else{
                        Toast.makeText(DespesasActivity.this,
                                "Descrição não foi preenchida!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(DespesasActivity.this,
                            "Categoria não foi preenchida!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(DespesasActivity.this,
                        "Data não foi preenchida!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(DespesasActivity.this,
                    "Valor não foi preenchido!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void obterDespesaTotal(){

        FirebaseUser utilizador = UtilizadorFirebase.getUtilizadorAtual();
        String idUtilizador = utilizador.getUid();

        DatabaseReference refUtilizador = firebase.child("utilizadores").child(idUtilizador);

        refUtilizador.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Utilizador utilizador = snapshot.getValue(Utilizador.class);
                despesaTotal = utilizador.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}