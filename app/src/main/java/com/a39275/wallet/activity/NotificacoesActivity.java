package com.a39275.wallet.activity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.a39275.wallet.databinding.ActivityNotificacoesBinding;
import com.a39275.wallet.model.Notificacao;

import java.util.Calendar;
import java.util.Date;

public class NotificacoesActivity extends AppCompatActivity {

    private static final int idNotificacao = 1;
    private static final String idCanal = "canal1";
    private static final String tituloTAG = "TituloExtra";
    private static final String mensagemTAG = "MensagemExtra";

    private ActivityNotificacoesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificacoesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        createNotificationChannel();

        binding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String categoria = binding.categoria.getText().toString();
                String descricao = binding.descricao.getText().toString();

                //Validar se os campos foram preenchidos
                if(!categoria.isEmpty()){
                    if(!descricao.isEmpty()){
                        scheduleNotification();
                    }else{
                        Toast.makeText(NotificacoesActivity.this,
                                "Preencha a descrição!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(NotificacoesActivity.this,
                            "Preencha a categoria!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void scheduleNotification() {

        Intent intent = new Intent(this, Notificacao.class);
        String title = binding.categoria.getText().toString();
        String message = binding.descricao.getText().toString();
        intent.putExtra(tituloTAG, title);
        intent.putExtra(mensagemTAG, message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, idNotificacao,
                intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long time = getTime();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    pendingIntent
            );
        }

        showAlert(time, title, message);
    }

    private void showAlert(long time, String title, String message) {

        Date date = new Date(time);
        java.text.DateFormat dateFormat = DateFormat.getLongDateFormat(this);
        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(this);

        new AlertDialog.Builder(this)
                .setTitle("Notificação Agendada")
                .setMessage(
                        "Categoria: " + title +
                        "\nDescrição: " + message +
                        "\nData Alerta: " + dateFormat.format(date) + " " + timeFormat.format(date))
                .show();
    }

    private long getTime() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int minute = binding.timePicker.getMinute();
            int hour = binding.timePicker.getHour();
            int day = binding.datePicker.getDayOfMonth();
            int month = binding.datePicker.getMonth();
            int year = binding.datePicker.getYear();
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute);

            return calendar.getTimeInMillis();
        }

        return 0;
    }


    private void createNotificationChannel() {

        String name = "Notification Channel";
        String desc = "A Description of the Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(idCanal, name, importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }



    }
}