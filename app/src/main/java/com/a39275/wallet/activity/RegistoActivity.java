package com.a39275.wallet.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.a39275.wallet.R;
import com.a39275.wallet.config.ConfiguracaoFirebase;
import com.a39275.wallet.helper.UtilizadorFirebase;
import com.a39275.wallet.model.Utilizador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegistoActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button btnRegisto;
    private FirebaseAuth autenticacao;
    private Utilizador utilizador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registo);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        btnRegisto = findViewById(R.id.btnRegistar);

        btnRegisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                //Validar se os campos foram preenchidos
                if(!textoNome.isEmpty()){
                    if(!textoEmail.isEmpty()){
                        if(!textoSenha.isEmpty()){
                            utilizador = new Utilizador();
                            utilizador.setNome(textoNome);
                            utilizador.setEmail(textoEmail);
                            utilizador.setPassword(textoSenha);
                            registarUtilizador();
                        }else{
                            Toast.makeText(RegistoActivity.this,
                                    "Preencha a senha!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RegistoActivity.this,
                                "Preencha o e-mail!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegistoActivity.this,
                            "Preencha o nome!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registarUtilizador(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
            utilizador.getEmail(), utilizador.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    UtilizadorFirebase.atualizarNomeUtilizador(utilizador.getNome());

                    utilizador.setPremium(false);
                    utilizador.setIdUtilizador(autenticacao.getUid());
                    utilizador.guardar();
                    finish();

                }else {
                    String excepcao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excepcao = "Insira uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excepcao = "Por favor, insira um e-mail válido!";
                    }catch (FirebaseAuthUserCollisionException e){
                        excepcao = "Conta já registada!";
                    }catch (Exception e){
                        excepcao = "Erro ao registar o utilizador: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(RegistoActivity.this,
                            excepcao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}