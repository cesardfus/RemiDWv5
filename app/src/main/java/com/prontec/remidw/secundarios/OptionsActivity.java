package com.prontec.remidw.secundarios;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prontec.remidw.R;
import com.prontec.remidw.entidades.Datos;
import com.prontec.remidw.entidades.Granja;
import com.prontec.remidw.support.AutoSMS;
import com.prontec.remidw.support.Funciones;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OptionsActivity extends AppCompatActivity {

    private Button btnsalir, btnsetmax, btnsetmin, btntraerconfig, btnsetautomensaje;
    private String telefono, tempenviar, tempenviars, nombreg;
    private Integer cantgalpones, galponselect;
    private Funciones funciones;
    private TextView etxmax, etxmin, txttelgranja;
    private Toolbar toolbaropciones;
    private Switch swtautoinicio, swautomensaje;
    private LinearLayout layoutmensaje;
    private Typeface tfavv;
    private DatabaseReference mDatabase, myRef;
    private Granja granja;
    private List<String> values;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        telefono = getIntent().getExtras().getString("telefono");
        nombreg = getIntent().getExtras().getString("nombregranja");

        inicio();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        setListeners();

        // Elementos en Spinner
        values = new ArrayList<String>();
        values.add("GALPON 1");
        /*-- ACA OBTENGO LA CANTIDAD DE GALPONES --*/
        myRef = mDatabase.child("granjas").orderByChild("telefono").equalTo(telefono).getRef();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    granja = singleSnapshot.getValue(Granja.class);
                    Log.d("CANTGALP", granja.getCantgalpones().toString());

                    cantgalpones = granja.getCantgalpones();

                    for(int w=2; w<cantgalpones+1; w++){
                        values.add("GALPON " + w);
                    }

                    pasarvalorInt(cantgalpones);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERRORFIREBASE", "onCancelled", databaseError.toException());
            }
        });

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values){
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                tfavv = ResourcesCompat.getFont(getContext(), R.font.gothica1black);
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(tfavv);
                return v;
            }

            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView v = (TextView) super.getView(position, convertView, parent);
                tfavv = ResourcesCompat.getFont(getContext(), R.font.gothica1black);
                view.setBackgroundColor(Color.parseColor("#FFA000"));
                v.setTypeface(tfavv);
                v.setTextColor(Color.BLACK);
                return v;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void setListeners() {
        btnsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnsetautomensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnsetautomensaje.isActivated()){
                    Toast.makeText(v.getContext(), "ACTIVADO", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "DESACTIVADO", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnsetmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempenviar = "#settmi="+etxmin.getText();
                if(!telefono.contains("+549")){telefono = "+549" + telefono;}
                funciones.enviarSMS(telefono, tempenviar);
            }
        });

        btnsetmax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempenviar = "#settma="+etxmin.getText();
                if(!telefono.contains("+549")){telefono = "+549" + telefono;}
                funciones.enviarSMS(telefono, tempenviar);
            }
        });

        btntraerconfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempenviars = "#estado";
                if(!telefono.contains("+549")){telefono = "+549" + telefono;}
                funciones.enviarSMS(telefono, tempenviars);
            }
        });

        swautomensaje.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    layoutmensaje.setVisibility(View.VISIBLE);
                    startService(new Intent(getApplicationContext(), AutoSMS.class));
                } else {
                    layoutmensaje.setVisibility(View.INVISIBLE);
                    stopService(new Intent(getApplicationContext(), AutoSMS.class));
                }
            }
        });

        // Spinner click listener
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id){
                galponselect = position+1;
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    private void inicio() {
        toolbaropciones = findViewById(R.id.toolbaropciones);
        setSupportActionBar(toolbaropciones);
        toolbaropciones.setTitleTextColor(getResources().getColor(R.color.blackcolor));
        getSupportActionBar().setTitle("Opciones");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        layoutmensaje = findViewById(R.id.layoutmensaje);
        btnsalir = findViewById(R.id.btnsalir);
        btnsetmin = findViewById(R.id.btnsetmin);
        btnsetmax = findViewById(R.id.btnsetmax);
        swtautoinicio = findViewById(R.id.swtautoinicio);
        swautomensaje = findViewById(R.id.swautomensaje);
        etxmax = findViewById(R.id.etxmax);
        etxmin = findViewById(R.id.etxmin);
        txttelgranja = findViewById(R.id.txttelgranja);
        btntraerconfig = findViewById(R.id.btntraerconfig);
        btnsetautomensaje = findViewById(R.id.btnsetautomensaje);
        spinner = findViewById(R.id.spngalpon);

        txttelgranja.setText(nombreg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_opciones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                finish();
                return true;
            case R.id.action_help: //hago un case por si en un futuro agrego mas opciones
                return true;
            case R.id.action_user: //hago un case por si en un futuro agrego mas opciones
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void pasarvalorInt(Integer cs) {
        cantgalpones = cs;
    }
}
