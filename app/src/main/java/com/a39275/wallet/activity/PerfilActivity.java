package com.a39275.wallet.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.a39275.wallet.R;
import com.a39275.wallet.config.ConfiguracaoFirebase;
import com.a39275.wallet.helper.UtilizadorFirebase;
import com.a39275.wallet.model.Utilizador;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    private static final int SELECAO_GALERIA = 200;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();

    private DatabaseReference refUtilizador;
    private ValueEventListener valueEventListenerUtilizador;

    private String idUtilizador;

    TextView logout;
    TextInputEditText textoNome, textoEmail;
    TextView alterarFoto;
    CircleImageView imagemPerfil;
    Button guardarAlteracoes;
    SwitchMaterial isPremiumSwitch;
    private StorageReference storageRef;
    private Utilizador utilizador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();

        setContentView(R.layout.activity_perfil);

        utilizador = UtilizadorFirebase.getDadosUtilizadorLogado();
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        idUtilizador = UtilizadorFirebase.getIdentificadorUtilizador();

        alterarFoto = findViewById(R.id.alterarFoto);
        isPremiumSwitch = findViewById(R.id.switchIsPremium);
        textoNome = findViewById(R.id.editNome);
        textoEmail = findViewById(R.id.editEmail);
        logout = findViewById(R.id.logout);
        imagemPerfil = findViewById(R.id.imagemPerfil);
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();

        guardarAlteracoes = findViewById(R.id.buttonGuardar);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        recuperarDadosUtilizador();

        // Guardar alterações nome
        guardarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nomeAtualizado = textoNome.getText().toString();

                UtilizadorFirebase.atualizarNomeUtilizador(nomeAtualizado);

                utilizador.setNome(nomeAtualizado);
                utilizador.guardar();

                Boolean estado = isPremiumSwitch.isChecked();

                guardarEstadoPremium(estado);

                Toast.makeText(PerfilActivity.this,
                        "Dados alterados com sucesso! Premium" + estado ,
                        Toast.LENGTH_SHORT).show();
            }
        });

        alterarFoto();
    }

    private void guardarEstadoPremium(boolean estado) {

        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        FirebaseUser user = UtilizadorFirebase.getUtilizadorAtual();
        String idUtilizador = user.getUid();

        database.child("utilizadores").child(idUtilizador).child("premium").setValue(estado);

    }

    private void alterarFoto() {

        alterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Valida se é possível abrir a galeria de fotos
                if ( intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                // Selecionar da galeria de fotos
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }

                // Caso tenha escolhido uma imagem
                if(imagem != null){

                    // Coloca imagem no Perfil Wallet
                    imagemPerfil.setImageBitmap(imagem);

                    // Guarda imagem no firebase

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    byte[] dadosImagem = stream.toByteArray();

                    idUtilizador = autenticacao.getCurrentUser().getUid();

                    final StorageReference imagemRef = storageRef.child("imagens")
                            .child("perfil")
                            .child(idUtilizador + ".jpeg");
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PerfilActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(PerfilActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(PerfilActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();

                            // Recuperar URL da foto
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();

                                    atualizarFotoUtilizador(url);
                                }
                            });

                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void atualizarFotoUtilizador(Uri url) {

        FirebaseUser user = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser();

        UserProfileChangeRequest profile = new UserProfileChangeRequest
                .Builder()
                .setPhotoUri(url)
                .build();

        user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Log.d("Perfil", "Erro ao atualizar a foto do utilizador");
                }
            }
        });

        // Atualizar foto no perfil
        UtilizadorFirebase.atualizarFotoUtilizador(url);

        // Atualizar foto no firebase
        utilizador.setSrcFoto(url.toString());
        utilizador.guardar();

    }

    private void recuperarDadosUtilizador() {

        FirebaseUser perfilUser = UtilizadorFirebase.getUtilizadorAtual();

        String nome = perfilUser.getDisplayName();
        String email = perfilUser.getEmail();

        System.out.println("nome: " + nome + "| email: " + email);

        textoNome.setText(nome);
        textoEmail.setText(email);

        Uri url = perfilUser.getPhotoUrl();
        if(url != null){
            Glide.with(PerfilActivity.this)
            .load(url)
                    .into(imagemPerfil);
        }else{
            imagemPerfil.setImageResource(R.drawable.ic_avatar);
        }


    }

    private void logout(){
        autenticacao.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}