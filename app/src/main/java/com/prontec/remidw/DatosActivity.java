package com.prontec.remidw;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prontec.remidw.entidades.Datos;
import com.prontec.remidw.entidades.Granja;
import com.prontec.remidw.interfaces.GetInt;
import com.prontec.remidw.interfaces.GetString;
import com.prontec.remidw.secundarios.OptionsActivity;
import com.prontec.remidw.support.Funciones;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DatosActivity extends AppCompatActivity {
    //3442664840
    private Button btntraerdatos, btngrafica, btnlog;
    private Toolbar toolbardatos;
    private String telefono, temperatura, humedad, co2, amoniaco, usuario, nombreGranja, fechalastdato;
    private Funciones funciones;
    private TextView txtvalor, txtmaxval, txtminval, txtgalponview, txtactivated;
    private Integer intpres;
    public Integer cantgalpones = 1, intgalpon;
    private Typeface tfavv;
    private Animation myAnim;
    private DatabaseReference mDatabase, myRef;
    private Granja granja;
    private Datos dato;
    private List<String> values;
    private String temparray[];
    private ProgressDialog dialog;

    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);

        telefono = getIntent().getExtras().getString("telefono");
        nombreGranja = getIntent().getExtras().getString("nombre");
        usuario = getIntent().getExtras().getString("usuario");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        values = new ArrayList<String>();
        values.add("GALPON 1");

        funciones = new Funciones();

        inicio();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("PLAYGROUND", "Permission is not granted, requesting");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 123);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 123);
            Log.d("PLAYGROUND", "Permission is granted");
        }

        /*-- ACA OBTENGO LA CANTIDAD DE GALPONES --*/
        myRef = mDatabase.child("granjas").orderByChild("telefono").equalTo(telefono).getRef();
        //Query phoneQuery = myRef.orderByChild();
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

        registerReceiver(broadcastReceiver, new IntentFilter("broadCastName"));

        btngrafica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //unregisterReceiver(broadcastReceiver);
                Intent n = new Intent(getApplicationContext(), GraficaActivity.class);
                n.putExtra("telefono", telefono);
                n.putExtra("nombre", nombreGranja);
                n.putExtra("usuario", usuario);
                n.putExtra("cantidadgalpones", cantgalpones);
                n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(n);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                //unregisterReceiver(broadcastReceiver);
                Intent n = new Intent(getApplicationContext(), GraficaActivity2.class);
                n.putExtra("telefono", telefono);
                n.putExtra("nombre", nombreGranja);
                n.putExtra("usuario", usuario);
                n.putExtra("cantidadgalpones", cantgalpones);
                n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(n);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                /*
                //unregisterReceiver(broadcastReceiver);
                Intent n = new Intent(getApplicationContext(), LogActivity.class);
                n.putExtra("telefono", telefono);
                n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(n);
                */
            }
        });

        btntraerdatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.enviarSMS(telefono, "#tg");
                dialog=new ProgressDialog(v.getContext());
                dialog.setMessage("Esperando mensaje de respuesta. Por favor espere...");
                dialog.show();
                timer = new Timer();
                timer.schedule(new firstTask(), 0,500);
            }
        });

        btntraerdatos.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                btntraerdatos.startAnimation(myAnim);
            }
        });

        toolbardatos = findViewById(R.id.toolbardatos);
        setSupportActionBar(toolbardatos);
        toolbardatos.setTitleTextColor(getResources().getColor(R.color.blackcolor));
        getSupportActionBar().setTitle("DATOS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        poneracero();
        tomardata();

        //para llenar el spinner
        Spinner spgalpones = findViewById(R.id.spgalponesdisp);
        spgalpones.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id){
                intgalpon=position+1;
                CambiarGalpon();
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        Spinner spmenudatos = findViewById(R.id.spmenudatos);
        spmenudatos.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id){
                if(position==0){
                    CambioUnidad("temp");
                } else if(position==1){
                    CambioUnidad("hum");
                } else if(position==2){
                    CambioUnidad("amon");
                } else {
                    CambioUnidad("co2");
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        List<String> menudatos = new ArrayList<String>();
        menudatos.add("TEMPERATURA");
        menudatos.add("HUMEDAD");
        menudatos.add("AMONIACO");
        menudatos.add("CO2");

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
        spgalpones.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdaptermenu = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, menudatos){
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
                //view.setBackgroundColor();
                return v;
            }
        };
        dataAdaptermenu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spmenudatos.setAdapter(dataAdaptermenu);
    }

    private void pasarvalorInt(Integer cs) {
        cantgalpones = cs;
    }

    private void CambioUnidad(String option){
        if(option == "co2"){
            intpres=3;
            txtvalor.setText("0ppm");
            txtmaxval.setText("0ppm");
            txtminval.setText("0ppm");
        } else if(option == "temp"){
            intpres=1;
            txtminval.setText("0ºC");
            txtmaxval.setText("0ºC");
            txtvalor.setText("0ºC");
        } else if(option == "amon"){
            intpres=4;
            txtminval.setText("0ppm");
            txtmaxval.setText("0ppm");
            txtvalor.setText("0ppm");
        } else if(option == "hum"){
            intpres=2;
            txtvalor.setText("0%");
            txtmaxval.setText("0%");
            txtminval.setText("0%");
        }
        tomardata();
    }

    public void CambiarGalpon(){
        tomardata();
    }

    public void inicio(){

        /*-- ACA INICIALIZO LAS VARIABLES --*/
        intpres = 1;
        intgalpon = 1;
        co2 = "0";
        amoniaco = "0";

        txtvalor = findViewById(R.id.txtvalor);
        txtgalponview = findViewById(R.id.txtgalponview);
        txtmaxval = findViewById(R.id.txtmaxval);
        txtminval = findViewById(R.id.txtminval);
        btngrafica = findViewById(R.id.btngrafica);
        btnlog = findViewById(R.id.btnlog);
        btntraerdatos = findViewById(R.id.btntraerdatos);
        txtactivated = findViewById(R.id.txtactivated);
        txtgalponview.setText(nombreGranja);

        myAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
    }

    BroadcastReceiver broadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            //telefono = b.getString("datos");
            //Toast.makeText(getApplicationContext(), telefono, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            timer.cancel();
            timer.purge();
            tomardata();
        }
    };


    //tells handler to send a message
    class firstTask extends TimerTask {
        @Override
        public void run() {
            h.sendEmptyMessage(0);
        }
    }

    final Handler h = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            long millis = System.currentTimeMillis();
            int seconds = (int) (millis / 1000);
            seconds     = seconds % 60;
            if(seconds>=60) quitarDialog();
            return false;
        }
    });

    public void quitarDialog(){
        Toast.makeText(this, "No se pudo obtener datos", Toast.LENGTH_LONG).show();
        dialog.dismiss();
        timer.cancel();
        timer.purge();
    }

    public void obtenerFecha(){
        Date d = new Date();
        String fecha = String.valueOf(d.getTime());
        Toast.makeText(this, fecha, Toast.LENGTH_SHORT).show();
        Long fechaint = Long.parseLong(fecha);
        Toast.makeText(this, fechaint.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                unregisterReceiver(broadcastReceiver);
                finish();
                return true;
            case R.id.action_help: //hago un case por si en un futuro agrego mas opciones
                return true;
            case R.id.action_settings: //hago un case por si en un futuro agrego mas opciones
                Intent n = new Intent(this, OptionsActivity.class);
                n.putExtra("telefono", telefono);
                n.putExtra("nombregranja", nombreGranja);
                n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(n);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void poneracero(){
        temperatura = "0°C";
        humedad = "0%";
        co2 = "0ppm";
        amoniaco = "0ppm";
    }

    public void tomardata(){
        myRef = mDatabase.child("datos").orderByChild("telefono").equalTo(telefono).getRef();
        //Query phoneQuery = myRef.orderByChild();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> arrayData = new ArrayList<>();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    dato = singleSnapshot.getValue(Datos.class);
                    txtactivated.setText("");
                    txtactivated.setText(dato.getFecha());
                    if(dato.getGalpon().equals(intgalpon)){
                        switch (intpres){
                            case 1:
                                txtvalor.setText(dato.getTemperatura()+"°C");
                                break;
                            case 2:
                                txtvalor.setText(dato.getHumedad()+"%");
                                break;
                            case 3:
                                txtvalor.setText(dato.getAmoniaco()+"ppm");
                                break;
                            case 4:
                                txtvalor.setText(dato.getCo2()+"ppm");
                                break;
                        }
                        //arrayData.add(dato.getFecha()+"/"+dato.getTemperatura()+"°C"+"/"+dato.getHumedad()+"%"+"/"+dato.getAmoniaco()+"ppm"+"/"+dato.getCo2()+"ppm"+"/");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERRORFIREBASE", "onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PLAYGROUND", "Permission has been granted");
            } else {
                Log.d("PLAYGROUND", "Permission has been denied or request cancelled");
            }
        }
    }

}
