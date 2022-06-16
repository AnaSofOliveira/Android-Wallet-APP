package com.a39275.wallet.helper;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateCustom {

    public static String dataAtual(){
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = simpleDateFormat.format(date);

        return dataString;
    }

    public static String mesAnoData(String data){
        String[] dataSeparada = data.split("/");
        String mes = dataSeparada[1];
        String ano = dataSeparada[2];

        return mes + ano;

    }

    public static long dataMilisegundos(String data){

        long millis = 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            Date date = sdf.parse(data);
            millis = date.getTime();
        } catch (ParseException e) {
            Log.d("Data", "Erro na obtenção da data em milisegundos.");
        }

        return millis;
    }
}
