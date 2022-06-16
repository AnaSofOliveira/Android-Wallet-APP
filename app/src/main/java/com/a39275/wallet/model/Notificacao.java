package com.a39275.wallet.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.a39275.wallet.R;

public class Notificacao extends BroadcastReceiver {

    private static final int idNotificacao = 1;
    private static final String idCanal = "canal1";
    private static final String tituloTAG = "TituloExtra";
    private static final String mensagemTAG = "MensagemExtra";


    @Override
    public void onReceive(Context context, Intent intent) {

        Notification notificacao = new NotificationCompat.Builder(context, idCanal)
                .setSmallIcon(R.drawable.ic_baseline_attach_money_24)
                .setContentTitle(intent.getStringExtra(tituloTAG))
                .setContentText(intent.getStringExtra(mensagemTAG))
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(idNotificacao, notificacao);
        
    }
}
