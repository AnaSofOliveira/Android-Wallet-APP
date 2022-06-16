package com.a39275.wallet.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a39275.wallet.R;
import com.a39275.wallet.adapter.AdapterMovimentacao;
import com.a39275.wallet.config.ConfiguracaoFirebase;
import com.a39275.wallet.helper.UtilizadorFirebase;
import com.a39275.wallet.model.Movimentacao;
import com.a39275.wallet.model.Utilizador;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton btnDespesas, btnReceitas;
    private MaterialCalendarView calendarView;
    private TextView textoSaudacao, textoSaldo;
    private Double despesaTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double resumoUtilizador = 0.0;
    private boolean isPremium = false;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference refUtilizador;
    private ValueEventListener valueEventListenerUtilizador;
    private ValueEventListener valueEventListenerMovimentacoes;

    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacoes =  new ArrayList<>();
    private Movimentacao movimentacao;
    private DatabaseReference refMovimentacao;

    private String mesAnoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setElevation(0);

        btnDespesas = findViewById(R.id.menu_despesa);
        btnReceitas = findViewById(R.id.menu_receita);
        textoSaudacao = findViewById(R.id.textSaudacao);
        textoSaldo = findViewById(R.id.textSaldo);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerMovimentos);

        configuracaoCalendarView();
        swipe();

        btnDespesas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarDespesa(btnDespesas.getRootView());
            }
        });

        btnReceitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarReceita(btnDespesas.getRootView());
            }
        });

        adapterMovimentacao = new AdapterMovimentacao(movimentacoes, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMovimentacao);
    }

    public void swipe(){

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                // Desabilita drag and drop
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                // Habilita swipe start e end
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removerMovimentacao(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);

    }

    public void removerMovimentacao(RecyclerView.ViewHolder viewHolder){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Remover movimentação da conta");
        alertDialog.setMessage("Tem a certeza que pertende remover este movimento?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int position = viewHolder.getAdapterPosition();

                movimentacao = movimentacoes.get(position);

                FirebaseUser utilizador = UtilizadorFirebase.getUtilizadorAtual();
                String idUtilizador = utilizador.getUid();

                refMovimentacao = firebase.child("movimentacao")
                        .child(idUtilizador)
                        .child(mesAnoSelecionado);

                refMovimentacao.child(movimentacao.getId()).removeValue();
                adapterMovimentacao.notifyItemRemoved(position);
                atualizarSaldo();

                //Toast.makeText(HomeActivity.this, "Removido", Toast.LENGTH_SHORT).show();

            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(HomeActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();

    }

    public void atualizarSaldo(){
        if(movimentacao.getTipo().equals("r")){
            receitaTotal = receitaTotal - movimentacao.getValor();

            FirebaseUser utilizador = UtilizadorFirebase.getUtilizadorAtual();
            String idUtilizador = utilizador.getUid();

            refUtilizador = firebase.child("utilizadores").child(idUtilizador);

            refUtilizador.child("receitaTotal").setValue(receitaTotal);

        }

        if(movimentacao.getTipo().equals("d")){
            despesaTotal = despesaTotal - movimentacao.getValor();

            FirebaseUser utilizador = UtilizadorFirebase.getUtilizadorAtual();
            String idUtilizador = utilizador.getUid();

            refUtilizador = firebase.child("utilizadores").child(idUtilizador);

            refUtilizador.child("despesaTotal").setValue(despesaTotal);
        }
    }

    public void recuperarMovimentacoes(){

        FirebaseUser utilizador = UtilizadorFirebase.getUtilizadorAtual();
        String idUtilizador = utilizador.getUid();

        refMovimentacao = firebase.child("movimentacao")
                                .child(idUtilizador)
                                .child(mesAnoSelecionado);

        valueEventListenerMovimentacoes = refMovimentacao.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movimentacoes.clear();
                for(DataSnapshot dados: snapshot.getChildren()){

                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    movimentacao.setId(dados.getKey());
                    movimentacoes.add(movimentacao);

                    Log.i("dados", "retorno: " + movimentacao.getCategoria());
                }

                adapterMovimentacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void recuperarResumo(){

        FirebaseUser utilizador = UtilizadorFirebase.getUtilizadorAtual();
        String idUtilizador = utilizador.getUid();

        refUtilizador = firebase.child("utilizadores").child(idUtilizador);

        Log.i("Evento", "Evento foi adicionado");
        valueEventListenerUtilizador = refUtilizador.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Utilizador utilizador = snapshot.getValue(Utilizador.class);

                despesaTotal = utilizador.getDespesaTotal();
                receitaTotal = utilizador.getReceitaTotal();

                resumoUtilizador = receitaTotal - despesaTotal;
                DecimalFormat format = new DecimalFormat("0.##");
                String resumoFormatado = format.format(resumoUtilizador);

                textoSaudacao.setText("Olá, " + utilizador.getNome());
                textoSaldo.setText("€ " + resumoFormatado);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Trata Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Trata opções do menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sair:
                logout();
                break;
            case R.id.menu_config_notif:

                if(validarUserPremium()){
                    startActivity(new Intent(this, NotificacoesActivity.class));
                }else {
                    new AlertDialog.Builder(this)
                            .setTitle("Funcionalidade indisponível")
                            .setMessage("Não é um utilizador Premium!\n Subscreva para conseguir configurar notificações.")
                            .show();
                    }
                break;

            case R.id.menu_perfil:
                Toast.makeText(this, "Abri menu de perfil", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, PerfilActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validarUserPremium() {

        FirebaseUser utilizador = UtilizadorFirebase.getUtilizadorAtual();
        String idUtilizador = utilizador.getUid();

        DatabaseReference refUtilizador = firebase.child("utilizadores").child(idUtilizador);

        refUtilizador.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Utilizador utilizador = snapshot.getValue(Utilizador.class);
                isPremium = utilizador.isPremium();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return isPremium;

    }

    private void logout(){
        autenticacao.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void configuracaoCalendarView() {

        CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril",
                "Maio", "Junho", "Julho", "Agosto",
                "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths(meses);

        CharSequence semanas[] = {"Seg", "Ter", "Qua", "Qui",
                "Sex", "Sáb", "Dom"};
        calendarView.setWeekDayLabels(semanas);

        CalendarDay dataAtual = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", dataAtual.getMonth());
        mesAnoSelecionado = mesSelecionado + dataAtual.getYear();

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", date.getMonth());
                mesAnoSelecionado = mesSelecionado + date.getYear();

                refMovimentacao.removeEventListener(valueEventListenerMovimentacoes);
                recuperarMovimentacoes();
            }
        });
    }

    void adicionarReceita(View view){
//        Toast.makeText(HomeActivity.this, "Entra nas ReceitasActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    void adicionarDespesa(View view){
//        Toast.makeText(HomeActivity.this, "Entra nas DespesasActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, DespesasActivity.class));
    }


    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarMovimentacoes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Evento", "Evento foi removido");
        refUtilizador.removeEventListener(valueEventListenerUtilizador);
        refMovimentacao.removeEventListener(valueEventListenerMovimentacoes);
    }
}