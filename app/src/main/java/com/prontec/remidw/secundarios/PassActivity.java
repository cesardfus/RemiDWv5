package com.prontec.remidw.secundarios;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.prontec.remidw.R;
import com.prontec.remidw.support.MailJob;

public class PassActivity  extends AppCompatActivity {

    private EditText etxmailpass, etxmailreg;
    private Button btnsolicitarolv, btnsolicitarreg;
    private TextView txtregistro, txtolvido, txtreg;
    private LinearLayout lyolvido, lyregistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

        etxmailpass = findViewById(R.id.etxmailpass);
        etxmailreg = findViewById(R.id.etxmailreg);
        btnsolicitarolv = findViewById(R.id.btnsolicitarolv);
        btnsolicitarreg = findViewById(R.id.btnsolicitarreg);
        txtregistro = findViewById(R.id.txtregistro);
        txtolvido = findViewById(R.id.txtolvido);
        lyolvido = findViewById(R.id.lyolvido);
        lyregistro = findViewById(R.id.lyregistro);
        txtreg = findViewById(R.id.txtreg);

        btnsolicitarolv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MailJob("cesardfus@gmail.com", "C3s4rdfus").execute(
                        new MailJob.Mail("App RemiD", "cesardfus@gmail.com", "Solicitar Contraseña", "Solicito la contraseña de mi usuario o  email: " + etxmailpass.getText().toString() + " para la app")
                );
            }
        });

        btnsolicitarreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MailJob("cesardfus@gmail.com", "C3s4rdfus").execute(
                        new MailJob.Mail("App RemiD", "cesardfus@gmail.com", "Solicitar Registro", "Solicito el registro de mi usuario o  email: " + etxmailreg.getText().toString() + " para la app")
                );
            }
        });


        txtregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lyolvido.setVisibility(View.GONE);
                lyregistro.setVisibility(View.VISIBLE);
            }
        });

        txtolvido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lyolvido.setVisibility(View.VISIBLE);
                lyregistro.setVisibility(View.GONE);
            }
        });

        txtreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dbu.agregarUsuario("admin","admin", "admin");
            }
        });
    }
}
