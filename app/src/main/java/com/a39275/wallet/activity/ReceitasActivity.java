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

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private FloatingActionButton btnGuardar;
    private Movimentacao movimentacao;
    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotal;
    private Double valorReceita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoValor = findViewById(R.id.editValor);
        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        btnGuardar = findViewById(R.id.fabGuardar);

        campoData.setText(DateCustom.dataAtual());

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarReceita(btnGuardar.getRootView());
            }
        });

        obterReceitaTotal();
    }

    private void salvarReceita(View view){

        if(validarCamposReceita()) {
            String data = campoData.getText().toString();
            String mesAno = DateCustom.mesAnoData(data);
            valorReceita = Double.parseDouble(campoValor.getText().toString());

            movimentacao = new Movimentacao();
            movimentacao.setValor(valorReceita);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(data);
            movimentacao.setTipo("r");

            Double receitaAtualizada = receitaTotal + valorReceita;

            atualizarReceita(receitaAtualizada);

            movimentacao.guardar(mesAno);

            finish();
        }
    }

    private void atualizarReceita(Double receita) {

        FirebaseUser utilizador = UtilizadorFirebase.getUtilizadorAtual();
        String idUtilizador = utilizador.getUid();

        DatabaseReference refUtilizador = firebase.child("utilizadores").child(idUtilizador);

        refUtilizador.child("receitaTotal").setValue(receita);

    }

    public boolean validarCamposReceita(){

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
                        Toast.makeText(ReceitasActivity.this,
                                "Descrição não foi preenchida!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(ReceitasActivity.this,
                            "Categoria não foi preenchida!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(ReceitasActivity.this,
                        "Data não foi preenchida!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(ReceitasActivity.this,
                    "Valor não foi preenchido!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void obterReceitaTotal(){

        FirebaseUser utilizador = UtilizadorFirebase.getUtilizadorAtual();
        String idUtilizador = utilizador.getUid();

        DatabaseReference refUtilizador = firebase.child("utilizadores").child(idUtilizador);

        refUtilizador.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Utilizador utilizador = snapshot.getValue(Utilizador.class);
                receitaTotal = utilizador.getReceitaTotal();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}