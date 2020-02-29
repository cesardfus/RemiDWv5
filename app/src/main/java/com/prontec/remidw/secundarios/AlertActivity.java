package com.prontec.remidw.secundarios;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.prontec.remidw.R;
import com.prontec.remidw.support.Funciones;

public class AlertActivity extends AppCompatActivity {

    private Button btnpararalerta, btndescartar, btnsalir;
    private TextView lblnumtel, txtalerta;
    private String telefono, mensaje;
    private Funciones funciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        telefono = getIntent().getExtras().getString("phone");
        mensaje = getIntent().getExtras().getString("message");

        lblnumtel = findViewById(R.id.lblnumtel);
        txtalerta = findViewById(R.id.txtalerta);
        btnpararalerta = findViewById(R.id.btnpararalerta);
        btndescartar = findViewById(R.id.btndescartar);
        btnsalir = findViewById(R.id.btnsalir);

        lblnumtel.setText(telefono);

        if (mensaje.contains("TEMPERATURA")){
            txtalerta.setText("ALERTA DE TEMPERATURA");
        }

        btnpararalerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //funciones.enviarSMS(telefono, "#tg");
            }
        });

        btnpararalerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //funciones.enviarSMS(telefono, "#tg");
            }
        });

        btnpararalerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
