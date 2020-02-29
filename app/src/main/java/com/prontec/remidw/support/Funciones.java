package com.prontec.remidw.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prontec.remidw.entidades.Datos;
import com.prontec.remidw.entidades.Granja;
import com.prontec.remidw.entidades.GranjaUser;
import com.prontec.remidw.entidades.Usuarios;
import com.prontec.remidw.interfaces.GetInt;
import com.prontec.remidw.interfaces.GetLong;
import com.prontec.remidw.interfaces.GetBoolean;
import com.prontec.remidw.interfaces.GetString;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Funciones {
    private Activity activity;
    private Context ctx;
    private DatabaseReference mDatabase, myRef;
    private Datos dato;
    private Granja granja;
    private GranjaUser granjauser;
    private Usuarios user;

    public Funciones(){
        //this.activity = activity;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
    }

    public Long obtenerFecha(){
        Date d = new Date();
        Long fechax = d.getTime();
        //String fechax = DateFormat.format("dd-MM-yyyy HH:MM", d.getTime()).toString();
        return fechax;
    }

    public void escribirDatoFirebase(final String telefono, final String fecha, final String[] temperatura, final String[] humedad, final String[] co2, final String[] amoniaco, final Integer galponA) {
        numeroIDsFirebase(new GetLong() {
            @Override
            public void LongValor(Long value) {
                for(int i=0; i<galponA; i++) {
                    Datos dato = new Datos(telefono, fecha, temperatura[i], humedad[i], co2[i], amoniaco[i], i+1);
                    Log.d("DATOREC", temperatura[i]);
                    Log.d("DATOREC", humedad[i]);
                    Log.d("DATOREC", co2[i]);
                    Log.d("DATOREC", amoniaco[i]);
                    myRef = mDatabase.child("datos/" + value);
                    myRef.setValue(dato);
                    value++;
                }
            }
        }, "datos");
    }

    /* ------------------- FUNCIONES PARA GRANJAS -------------------------------- */

    public void escribirGranjaFirebase(final String tel, final String nom, final String dir, final String prop, final String inte, final Integer cantgalp) {
        Granja granja = new Granja(nom, dir, prop, inte, cantgalp);
        myRef = mDatabase.child("granjas/"+tel);
        myRef.setValue(granja);
        /*
        numeroIDsFirebase(new GetLong() {
            @Override
            public void LongValor(Long value) {
                Granja granja = new Granja(tel, nom, dir, prop, inte, cantgalp);
                myRef = mDatabase.child("granjas/" + value);
                myRef.setValue(granja);
            }
        }, "granjas");
        */
    }

    /* ------------------------------------ Granjas de Usuarios ------------------------------------------------ */

    public void granjaUser(final String usuario, final String granjanombre, final String telefono) {
        numeroIDsFirebase(new GetLong() {
            @Override
            public void LongValor(Long value) {
                GranjaUser gu = new GranjaUser(telefono, granjanombre);
                myRef = mDatabase.child("granjausers" + "/" + usuario + "/" + value );
                myRef.setValue(gu);
            }
        }, "granjausers/"+usuario);
    }

    public void numeroIDsFirebase(final GetLong intefazUID, final String nodo) {
        myRef = mDatabase.child(nodo).getRef();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long numHijos = dataSnapshot.getChildrenCount();
                intefazUID.LongValor(numHijos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }

    public String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

    }

    public void enviarSMS(String phone, String message){
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, message, null, null);
    }

    protected void sendEmail(Context ctx) {
        String[] TO = {"contacto@seogalicia.es"}; //aquí pon tu correo
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        // Esto podrás modificarlo si quieres, el asunto y el cuerpo del mensaje
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Asunto");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Escribe aquí tu mensaje");

        try {
            ctx.startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ctx,"No tienes clientes de email instalados.", Toast.LENGTH_SHORT).show();
        }
    }

    public static Date ParseFecha(String fecha){
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaDate = null;
        try {
            fechaDate = formato.parse(fecha);
        }
        catch (ParseException ex)
        {
            System.out.println(ex);
        }
        return fechaDate;
    }
}
