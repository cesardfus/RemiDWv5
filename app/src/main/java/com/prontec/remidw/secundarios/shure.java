package com.prontec.remidw.secundarios;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.prontec.remidw.R;

public class shure extends Activity {

    private Button btncancelar, btnagregar;
    private String telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shure);

        telefono = getIntent().getExtras().getString("telefono");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.9), (int) (height*.4));

        btnagregar = findViewById(R.id.btnagregar);
        btncancelar = findViewById(R.id.btncancelar);

        btnagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent pop = new Intent(getApplicationContext(), RegistroGranja.class);
                pop.putExtra("telefono", telefono);
                startActivity(pop);
            }
        });

        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
