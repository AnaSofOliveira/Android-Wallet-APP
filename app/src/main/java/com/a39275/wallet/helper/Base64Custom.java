package com.a39275.wallet.helper;

import android.util.Base64;

public class Base64Custom {

    public static String codificaBase64(String texto){
        //Codifica para Base64 e remove espaços em branco
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static String descodificaBase64(String textoCodificado){
        return new String(Base64.decode(textoCodificado, Base64.DEFAULT));
    }

}
