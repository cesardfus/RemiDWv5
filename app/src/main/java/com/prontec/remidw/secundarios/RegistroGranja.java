package com.prontec.remidw.secundarios;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.prontec.remidw.LogActivity;
import com.prontec.remidw.MainActivity;
import com.prontec.remidw.R;
import com.prontec.remidw.support.Funciones;

public class RegistroGranja extends AppCompatActivity {

    private Button btncargar;
    private EditText etxnombre, etxdireccion, etxpropietario, etxintegracion, etxcantgalp;
    private TextView txttelefono;
    private Funciones fnx;
    private String telefono, nombre, direccion, propietario, integracion;
    private Integer cantgalpones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_granja);
        telefono = getIntent().getExtras().getString("telefono");
        inicio();

        btncargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre = etxnombre.getText().toString().toUpperCase();
                direccion = etxdireccion.getText().toString();
                propietario = etxpropietario.getText().toString();
                integracion = etxintegracion.getText().toString().toUpperCase();
                cantgalpones = Integer.parseInt(etxcantgalp.getText().toString());
                fnx.escribirGranjaFirebase(telefono,nombre,direccion,propietario,integracion,cantgalpones);
                Intent n = new Intent(getApplicationContext(), MainActivity.class);
                n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(n);

            }
        });
    }

    private void inicio() {
        fnx = new Funciones();
        btncargar = findViewById(R.id.btncargar);
        etxnombre = findViewById(R.id.lblnombre);
        etxdireccion = findViewById(R.id.lbldireccion);
        etxpropietario = findViewById(R.id.lblpropietario);
        etxintegracion = findViewById(R.id.lblintegracion);
        etxcantgalp = findViewById(R.id.lblcantgalp);
        txttelefono = findViewById(R.id.lbltelefono);
        txttelefono.setText(telefono);
    }
}
