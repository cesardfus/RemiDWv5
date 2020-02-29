package com.prontec.remidw;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prontec.remidw.secundarios.PassActivity;
import com.prontec.remidw.secundarios.Readme;
import com.prontec.remidw.secundarios.RegistroActivity;
import com.prontec.remidw.support.Funciones;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {

    public static int MILISEGUNDOS_ESPERA = 4000;
    private EditText etxuserlog, etxpasslog;
    private Button btningreso, btnsalirlog;
    private CheckBox recordarme, autologchk;
    private TextView txtvregistrar, txtresultadolog, txtvolvido;
    private String usuario, password, iduser, rol="admin";
    private Funciones funciones;
    private ProgressBar progressBar, pbcargauser;
    private ImageView imgok;
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "PrefsFile";
    private FirebaseAuth mAuth;// ...
    private Boolean readmeacept = false;
    private final int PHONE_SMS_READ = 100;
    private final int PHONE_SMS_SEND = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            if(getIntent().hasExtra("acepto")){
                readmeacept = getIntent().getExtras().getBoolean("acepto");
                if (readmeacept){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("pref_fuse", readmeacept);
                    editor.apply();
                }
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                versionesNuevas();
            } else {
                versionesViejas();
            }

            inicializarComponentes();

            mAuth = FirebaseAuth.getInstance();

            autologin();

            mostrarReadme();

            funciones = new Funciones();
            txtresultadolog.setTextColor(Color.RED);
            txtresultadolog.setVisibility(View.GONE);
            imgok.setVisibility(View.GONE);

            setearPreferencias();

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            inicio();

        }

    private void autologin() {
            if(preferences.contains("pref_autolog")){
                Boolean pautolog = preferences.getBoolean("pref_autolog", false);
                if(pautolog){
                    if(mAuth.getCurrentUser()!=null){
                        enviaraproxima();
                    }
                }
            }
        }

    private void mostrarReadme() {
            if(!preferences.contains("pref_fuse")){
                Intent n = new Intent(getApplicationContext(), Readme.class);
                n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(n);
            } else {
                Boolean pfuse = preferences.getBoolean("pref_fuse", true);
                if(!pfuse){
                    finish();
                }
            }
        }

    private void inicio() {
            seteoComponentes();
        }

    private void seteoComponentes(){

            btningreso.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginUserAccount();
                }
            });

            btnsalirlog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            txtvregistrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent n = new Intent(getApplicationContext(), RegistroActivity.class);
                    n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(n);
                }
            });

            txtvolvido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent n = new Intent(getApplicationContext(), PassActivity.class);
                    n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(n);
                }
            });

            autologchk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(autologchk.isChecked()) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("pref_autolog", true);
                        editor.apply();
                    }else{
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("pref_autolog", false);
                        editor.apply();
                    }
                }
            });
        }

        private void inicializarComponentes() {
            etxuserlog = findViewById(R.id.etxuserlog);
            etxpasslog = findViewById(R.id.etxpasslog);
            btningreso = findViewById(R.id.btningreso);
            btnsalirlog = findViewById(R.id.btnsalirlog);
            txtvregistrar = findViewById(R.id.txtvregistrar);
            txtvolvido = findViewById(R.id.txtvolvido);
            txtresultadolog = findViewById(R.id.txtresultadolog);
            //progressBar = findViewById(R.id.progressBar);
            imgok = findViewById(R.id.imgok);
            recordarme = findViewById(R.id.chkremember);
            autologchk = findViewById(R.id.chkingresoautomatico);
        }

    private void setearPreferencias(){
            preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            if(preferences.contains("pref_fuse")){
                Boolean pfuse = preferences.getBoolean("pref_fuse", false);
                if(pfuse){
                    if(preferences.contains("pref_user")){
                        String us = preferences.getString("pref_user", "No encontrado");
                        etxuserlog.setText(us);
                    }
                    if(preferences.contains("pref_pass")){
                        String pas = preferences.getString("pref_pass", "No encontrado");
                        etxpasslog.setText(pas);
                    }
                    if(preferences.contains("pref_check")){
                        Boolean chek = preferences.getBoolean("pref_check", false);
                        recordarme.setChecked(chek);
                    }
                    if(preferences.contains("pref_autolog")){
                        Boolean chek = preferences.getBoolean("pref_autolog", false);
                        autologchk.setChecked(chek);
                    }
                } else {
                    Intent n = new Intent(getApplicationContext(), Readme.class);
                    n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(n);
                }
            }
        }

    @Override
    public void finish() {
            super.finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

    @Override
    public void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            //updateUI(currentUser);
        }

    private void loginUserAccount() {
            usuario = etxuserlog.getText().toString();
            password = etxpasslog.getText().toString();
            usuario = usuario+"@prontec.com.ar";
            final ProgressDialog dialog=new ProgressDialog(this);
            dialog.setMessage("Ingresando...");
            dialog.show();
            if (TextUtils.isEmpty(usuario)) {
                Toast.makeText(getApplicationContext(), "Por favor ingrese un usuario ...", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Por favor ingrese una contraseña!", Toast.LENGTH_LONG).show();
                return;
            }

            if (recordarme.isChecked()){
                Boolean boolChek = recordarme.isChecked();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("pref_user", etxuserlog.getText().toString());
                editor.putString("pref_pass", etxpasslog.getText().toString());
                editor.putBoolean("pref_check", boolChek);
                editor.apply();
            }else{
                preferences.edit().clear().apply();
            }

            mAuth.signInWithEmailAndPassword(usuario, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Logueado correctamente!", Toast.LENGTH_LONG).show();
                                enviaraproxima();
                                dialog.dismiss();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Fallo! Por favor intentelo nuevamente", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }
                    });
        }

    private void enviaraproxima(){
            Intent n = new Intent(getApplicationContext(), MainActivity.class);
            n.putExtra("usuario", usuario);
            n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(n);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    private boolean CheckPermisos(String permission){
            int result = checkCallingOrSelfPermission(permission);
            return result == PackageManager.PERMISSION_GRANTED;
            /*
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_SMS)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            0);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                Toast.makeText(this, "SS", Toast.LENGTH_SHORT).show();
            }
            */
        }

    private void versionesViejas(){
        //Toast.makeText(this, "1", Toast.LENGTH_LONG).show();
        if(CheckPermisos(Manifest.permission.READ_SMS) && CheckPermisos(Manifest.permission.SEND_SMS)){

        } else {
            Toast.makeText(this, "Declinaste los permisos", Toast.LENGTH_LONG).show();
        }
    }

    private void versionesNuevas(){
        //Toast.makeText(this, "2", Toast.LENGTH_LONG).show();
        requestPermissions(new String[] {Manifest.permission.READ_SMS}, PHONE_SMS_READ);
        requestPermissions(new String[] {Manifest.permission.READ_SMS}, PHONE_SMS_SEND);
    }
}
