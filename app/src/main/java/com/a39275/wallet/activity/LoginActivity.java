package com.a39275.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.a39275.wallet.R;
import com.a39275.wallet.config.ConfiguracaoFirebase;
import com.a39275.wallet.model.Utilizador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button bntEntrar;
    private Utilizador utilizador;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        bntEntrar = findViewById(R.id.btnEntrar);

        bntEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if(!textoEmail.isEmpty()){
                    if(!textoSenha.isEmpty()){
                        utilizador = new Utilizador();
                        utilizador.setEmail(textoEmail);
                        utilizador.setPassword(textoSenha);
                        utilizador.setPremium(false);
                        validarLogin();

                    }else{
                        Toast.makeText(LoginActivity.this,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,
                            "Preencha o e-mail!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                utilizador.getEmail(),
                utilizador.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    abrirHomeActivity();

                }else{
                    String excepcao = "";

                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        excepcao = "O utilizador não está registado!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excepcao = "E-mail e senha não correspondem a um utilizador registado!";
                    }catch (Exception e){
                        excepcao = "Erro ao iniciar sessão: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this,
                            excepcao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void abrirHomeActivity(){
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

}