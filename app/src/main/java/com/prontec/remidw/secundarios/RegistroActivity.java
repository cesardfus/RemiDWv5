package com.prontec.remidw.secundarios;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.prontec.remidw.LoginActivity;
import com.prontec.remidw.R;

public class RegistroActivity extends AppCompatActivity {

    private EditText etxemail, etxpass;
    private String correo, contrasena;
    private Button btnregistro;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        inicio();

        btnregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegistroActivity.this, "CLCIK!", Toast.LENGTH_SHORT).show();
                correo = etxemail.getText().toString().trim();
                contrasena = etxpass.getText().toString().trim();
                correo=correo+"@prontec.com.ar";

                firebaseAuth.createUserWithEmailAndPassword(correo,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegistroActivity.this, "El usuario se registro con éxito", Toast.LENGTH_SHORT).show();
                            Intent n = new Intent(getApplicationContext(), LoginActivity.class);
                            n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(n);
                            RegistroActivity.this.finish();
                        }else{
                            Toast.makeText(RegistroActivity.this, "El usuario no se registro.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void inicio() {
        etxemail = findViewById(R.id.etxemail);
        etxpass = findViewById(R.id.etxpass);
        btnregistro = findViewById(R.id.btnregistro);
        firebaseAuth = FirebaseAuth.getInstance();
    }

}

/*
        telefono = getIntent().getExtras().getString("telefono");

        btnreg = findViewById(R.id.btnregistro);
        btnatras = findViewById(R.id.btnatras);
        etxdirecciongranja = findViewById(R.id.etxdirecciongranja);
        edtnombregranja = findViewById(R.id.edtnombregranja);
        etxpropietariogranja = findViewById(R.id.etxpropietariogranja);
        etxintegraciongranja = findViewById(R.id.etxintegraciongranja);
        txtresultado = findViewById(R.id.txtresultado);
        ndegranjas = findViewById(R.id.edtnumgalp);
        etxtel = findViewById(R.id.etxtel);
        chktemp = findViewById(R.id.chktemp);
        chkhum = findViewById(R.id.chkhum);
        chkamon = findViewById(R.id.chkamon);
        chkco2 = findViewById(R.id.chkco2);
        etxtel.setText(telefono);

        dbu = new ConexionSQLiteHelper(this);

        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbu.agregarGranjasSQLite(
                        etxtel.getText().toString(),
                        edtnombregranja.getText().toString(),
                        etxdirecciongranja.getText().toString(),
                        etxpropietariogranja.getText().toString(),
                        etxintegraciongranja.getText().toString(),
                        //tomardatoslect(),
                        Integer.parseInt(ndegranjas.getText().toString()))){
                    new SyncSQLActivity(getApplicationContext(), "INSERTARG").execute(
                            etxtel.getText().toString(),
                            edtnombregranja.getText().toString(),
                            etxdirecciongranja.getText().toString(),
                            etxpropietariogranja.getText().toString(),
                            etxintegraciongranja.getText().toString(),
                            ndegranjas.getText().toString());
                    Toast.makeText(getApplicationContext(), "CORRECTO", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "NO CARGADO", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String tomardatoslect() {
        String val="";
        if(chktemp.isChecked())val+="T";
        if(chkamon.isChecked())val+="A";
        if(chkhum.isChecked())val+="H";
        if(chkco2.isChecked())val+="C";
        Toast.makeText(this, val, Toast.LENGTH_SHORT).show();
        return val;
    }

    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    */
