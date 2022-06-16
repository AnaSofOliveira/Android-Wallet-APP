package com.a39275.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.a39275.wallet.R;
import com.a39275.wallet.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        setButtonBackVisible(false);
        setButtonNextVisible(false);

        addSlide(new FragmentSlide.Builder()
            .background(android.R.color.white)
            .fragment(R.layout.intro_1)
            .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_2)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_3)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_4)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_registo)
                .canGoForward(false)
                .build()
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "vou verificar se utilizador está logado");
        verificarUtilizadorLogado();
    }

    public void btnEntrar(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void btnRegistar(View view){
        startActivity(new Intent(this, RegistoActivity.class));
    }

    public void verificarUtilizadorLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();
        if(autenticacao.getCurrentUser() != null){
            abrirHomeActivity();
        }
    }


    public void abrirHomeActivity(){
        startActivity(new Intent(this, HomeActivity.class));
    }
}