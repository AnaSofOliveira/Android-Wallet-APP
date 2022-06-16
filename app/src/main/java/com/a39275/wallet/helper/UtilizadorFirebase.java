package com.a39275.wallet.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.a39275.wallet.config.ConfiguracaoFirebase;
import com.a39275.wallet.model.Utilizador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UtilizadorFirebase {


    public static FirebaseUser getUtilizadorAtual(){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return autenticacao.getCurrentUser();
    }

    public static String getIdentificadorUtilizador(){
        return getUtilizadorAtual().getUid();
    }

    public static void atualizarNomeUtilizador(String name){
        try {

            // Utilizador logado
            FirebaseUser user = getUtilizadorAtual();

            // Configurar alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar Perfil - Nome do utilizador.");
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void atualizarFotoUtilizador(Uri url){
        try {

            // Utilizador logado
            FirebaseUser user = getUtilizadorAtual();

            // Configurar alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar Perfil - Foto do utilizador.");
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Utilizador getDadosUtilizadorLogado(){

        FirebaseUser firebaseUser = getUtilizadorAtual();

        Utilizador utilizador = new Utilizador();
        utilizador.setEmail(firebaseUser.getEmail());
        utilizador.setNome(firebaseUser.getDisplayName());
        utilizador.setIdUtilizador(firebaseUser.getUid());

        if (firebaseUser.getPhotoUrl() == null){
            utilizador.setSrcFoto("");
        }else{
            utilizador.setSrcFoto( firebaseUser.getPhotoUrl().toString());
        }

        return utilizador;

    }
}
