package com.prontec.remidw.secundarios;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.prontec.remidw.LoginActivity;
import com.prontec.remidw.R;

public class Readme extends AppCompatActivity {

    private SharedPreferences preferences;
    private static final String PREFS_NAME = "PrefsFile";
    private Button btnaceptar, btncancelar;
    private CheckBox chekpermision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readme);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        btnaceptar = findViewById(R.id.btnaceptar);
        btncancelar = findViewById(R.id.btncancelarreadme);
        chekpermision = findViewById(R.id.chkpermiso);

        chekpermision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chekpermision.isChecked()){
                    btnaceptar.setEnabled(true);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("pref_fuse", true);
                    editor.apply();
                } else{
                    btnaceptar.setEnabled(false);
                }
            }
        });

        btnaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n = new Intent(getApplicationContext(), LoginActivity.class);
                n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                n.putExtra("acepto", true);
                getApplicationContext().startActivity(n);
            }
        });

        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

    }


}
