package com.prontec.remidw.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.format.DateFormat;
import android.util.Log;

import com.prontec.remidw.DatosActivity;
import com.prontec.remidw.secundarios.AlertActivity;

import java.util.Date;

public class RecivirSMS extends BroadcastReceiver {
    private String phone, message;
    private Context ctx;
    private Funciones funciones;
    private DatosActivity ma; //a reference to activity's context
    //private DBHelperSQLDatos dbd;
    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        funciones = new Funciones();
        final Bundle bundle = intent.getExtras();
        if (bundle != null) {
            final Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdusObj.length; i++) {
                SmsMessage MENSAJE = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                phone = MENSAJE.getDisplayOriginatingAddress();
                message = MENSAJE.getDisplayMessageBody();
                Log.i("RECEIVER", "numero:" + phone + " Mensaje:" + message);
                message = message.toUpperCase();
                if (message.contains("ALERTA")) {
                    Intent n = new Intent(context, AlertActivity.class);
                    n.putExtra("phone", phone);
                    n.putExtra("message", message);
                    n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(n);
                } else if (message.contains("TEMPERATURA")) {
                    StringWork("dat", message);
                    /* DESDE ACA LO MANDA A LA ACTIVIDAD PRINCIPAL */
                    Intent x = new Intent("broadCastName");
                    x.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    x.putExtra("message", phone);
                    context.sendBroadcast(x);
                } else if (message.contains("--")) {
                    StringWork("est", message);
                    /* DESDE ACA LO MANDA A LA ACTIVIDAD PRINCIPAL */
                    Intent x = new Intent("broadCastName");
                    x.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    x.putExtra("message", phone);
                    context.sendBroadcast(x);
                } else if (message.contains("TEMP:")) {
                    StringWork("temp", message);
                    /* DESDE ACA LO MANDA A LA ACTIVIDAD PRINCIPAL */
                    Intent x = new Intent("broadCastName");
                    x.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //x.putExtra("message", phone);
                    //x.putExtra("datos", message);
                    context.sendBroadcast(x);
                }
                //TEMPERATURA(C): G1  0.00,  G2   0.00,  G3   0.00,  G4 17.70,  G5   0.00  HUMEDAD(%): G1  0.00,  G2   0.00,  G3   0.00,  G4 76.70,  G5   0.00
            }
        }
    }

    private void StringWork(String action, String message){
        if (action=="dat"){
            String[] partst, partsh;
            String parttemp, parthum, temp, hum;
            Integer countt, counth;

            partst = message.split(":");
            parttemp = partst[1];
            parthum = partst[2];
            parttemp = parttemp.substring(1, parttemp.length()-12);
            parthum = parthum.substring(1, parthum.length()-1);
            partst = parttemp.split(",");
            partsh = parthum.split(",");

            for(Integer xy=0;xy<=partst.length-1;xy++){
                countt = partst[xy].length();
                counth = partsh[xy].length();
                temp = partst[xy].substring(countt-5,countt);
                hum = partsh[xy].substring(counth-5,counth);
                Integer NumG = xy+1;
                //if(dbu.agregarDatos(phone, obtenerFecha(), Float.parseFloat(temp), Float.parseFloat(hum), Float.parseFloat("0.00"), Float.parseFloat("0.00"), NumG)){
                    Log.d("MSG REC", phone + obtenerFecha() + temp + hum + "0.00"+ "0.00" + NumG);
                    //new SynDatosActivity(ctx, "INSERTARD").execute(phone, obtenerFecha().toString(), temp, hum, "0.00","0.00", NumG.toString());
                //}
            }
        } else if (action=="temp"){
            String[] partst;
            String parttemp;
            partst = message.split(":");
            parttemp = partst[1];
            partst = parttemp.split(",");

            String[] temperaturas = new String[partst.length];
            String[] humedades = new String[partst.length];
            String[] co2s = new String[partst.length];
            String[] amoniacos = new String[partst.length];

            for(Integer xy=0;xy<partst.length;xy++){
                partst[xy] = partst[xy].replaceAll(" ", "");
                Integer longitud = partst[xy].length();
                temperaturas[xy] = partst[xy].substring(2, longitud);
                humedades[xy] = "0.00";
                co2s[xy] = "0.00";
                amoniacos[xy] = "0.00";
            }
            funciones.escribirDatoFirebase(phone, obtenerFecha(), temperaturas, humedades, co2s, amoniacos, partst.length);
        } /* else if (action=="est"){
            dbd = new DBHelperSQLDatos(ctx);
            String[] parts, parts2;
            String part1, part2, temp, hum;
            Integer countt, counth, xy;
            parts = message.split(":");
            part1 = parts[1];
            part2 = parts[2];
            part1 = part1.substring(1, part1.length()-12);
            part2 = part2.substring(1, part2.length()-1);
            parts = part1.split(",");
            parts2 = part2.split(",");

            for(xy=0;xy<=parts.length-1;xy++){
                countt = parts[xy].length();
                counth = parts2[xy].length();
                temp = parts[xy].substring(countt-5,countt);
                hum = parts2[xy].substring(counth-5,counth);
                if(dbd.addDataD(phone, obtenerFecha(), Float.parseFloat(temp), Float.parseFloat(hum), Float.parseFloat("0.00"), Float.parseFloat("0.00"), xy)){
                    new SynDatosActivity(ctx, "INSERTARD").execute(phone, obtenerFecha().toString(), temp, hum, "0.00","0.00", xy.toString());
                }
            }
        }*/
        //dbu.close();
    }

    public String obtenerFecha(){
        Date d = new Date();
        //Long fechax = d.getTime();
        String fechax = DateFormat.format("dd-MM-yyyy HH:MM", d.getTime()).toString();
        return fechax;
    }
}