package com.prontec.remidw;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prontec.remidw.entidades.Datos;
import com.prontec.remidw.entidades.Granja;
import com.prontec.remidw.secundarios.OptionsActivity;
import com.prontec.remidw.support.Funciones;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//import androidx.annotation.RequiresApi;

import static com.prontec.remidw.support.Funciones.ParseFecha;
@SuppressWarnings("unchecked")
public class GraficaActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {

    private LineChart mChart;
    private BarChart mChartB;
    private LinearLayout lyporfecha, lyxsemana;
    private String telefono, fechainicio, fechafin, telefonosc, posfiltro="all", usuario, iduser, nombreg;
    private Funciones funciones;
    private Integer cantgalpones, galponselect;
    private LineDataSet ldsTemperatura, ldsHumedad, ldsCo2, ldsAmoniaco;
    private ArrayList<ILineDataSet> dataSets;
    private LineData data;
    private ArrayList fechasChart;
    private Toolbar toolbargrafica;
    private Button etxfechaini, etxfechafin, etxfechainisem;
    private DatePickerDialog datePickerDialogIni, datePickerDialogFin, datePickerDialogIniSem;
    private Button btnfiltrofecha;
    private Spinner spngalpongraf, spinnerfilt, spfiltrox;
    private Typeface tfavv;
    private SeekBar skbsemana;
    private DatabaseReference mDatabase, myRef;
    private Granja granja;
    private Datos dato;
    List<String> valuesfilt,spfiltroxval,values;
    LimitLine limitLine, limitLineb;
    YAxis ylAxis;
    XAxis xAxis;
    TextView nombregranja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        System.gc();

        Log.d("DEBUG", "ENTRANDO A GRAFICA");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica);

        telefono = getIntent().getExtras().getString("telefono");
        nombreg = getIntent().getExtras().getString("nombre");
        usuario = getIntent().getExtras().getString("usuario");
        iduser = getIntent().getExtras().getString("iduser");
        cantgalpones = getIntent().getExtras().getInt("cantidadgalpones");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        inicio();

        btnfiltrofecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fechainicio = etxfechaini.getText().toString();
                fechafin = etxfechafin.getText().toString();
                Log.d("FECHAS", fechainicio+" "+fechafin);
                if((fechainicio!="") || (fechafin!="")){
                    filtrarxfecha(posfiltro);
                } else {
                    Toast.makeText(getApplicationContext(), "INGRESAR UNA FECHA DE INICIO Y FIN PARA FILTRAR", Toast.LENGTH_SHORT).show();
                }
            }
        });

        datePickerLlenado();

        DibujarChart(galponselect, "temp");

        spngalpongraf.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id){
                galponselect = position+1;
                DibujarChart(galponselect, posfiltro);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spinnerfilt.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id){
                switch (position){
                    case 0:
                        posfiltro = "all";
                        DibujarChart(galponselect, posfiltro);
                        break;
                    case 1:
                        posfiltro="temp";
                        DibujarChart(galponselect, posfiltro);
                        break;
                    case 2:
                        posfiltro="hum";
                        DibujarChart(galponselect, posfiltro);
                        break;
                    case 3:
                        posfiltro="amon";
                        DibujarChart(galponselect, posfiltro);
                        break;
                    case 4:
                        posfiltro="co2";
                        DibujarChart(galponselect, posfiltro);
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spfiltrox.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id){
                switch (position){
                    case 0:
                        showViewFiltro("xfecha");
                        break;
                    case 1:
                        showViewFiltro("xsemana");
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // Elementos en Spinner
        values.add("GALPON 1");
        for(int w=2; w<cantgalpones+1; w++){
            values.add("GALPON " + w);
        }

        // Elementos en Spinner

        valuesfilt.add("TODOS");
        valuesfilt.add("TEMPERATURA");
        valuesfilt.add("HUMEDAD");
        valuesfilt.add("CO2");
        valuesfilt.add("AMONIACO");

        spfiltroxval.add("FILTRAR POR FECHA");
        spfiltroxval.add("FILTRAR POR SEMANA");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values) {
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(tfavv);
                return v;
            }

            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(tfavv);
                v.setTextColor(Color.BLACK);
                return v;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spngalpongraf.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapterfilt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valuesfilt){
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(tfavv);
                return v;
            }

            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(tfavv);
                v.setTextColor(Color.BLACK);
                return v;
            }
        };
        dataAdapterfilt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerfilt.setAdapter(dataAdapterfilt);
        spinnerfilt.setSelection(1);

        ArrayAdapter<String> dataAdapterfiltx = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spfiltroxval){
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(tfavv);
                return v;
            }

            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(tfavv);
                v.setTextColor(Color.BLACK);
                return v;
            }
        };
        dataAdapterfiltx.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spfiltrox.setAdapter(dataAdapterfiltx);

        skbsemana.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filtrarxsemana(progress, posfiltro);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void datePickerLlenado() {
        etxfechaini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialogIni = new DatePickerDialog(GraficaActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            //@RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String fecha = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                etxfechaini.setText(fecha);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogIni.show();
            }
        });

        etxfechafin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialogFin = new DatePickerDialog(GraficaActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String fecha = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                etxfechafin.setText(fecha);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogFin.show();
            }
        });

        etxfechainisem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialogIniSem = new DatePickerDialog(GraficaActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String fecha = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                etxfechainisem.setText(fecha);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogIniSem.show();
            }
        });
    }

    private void inicio() {
        btnfiltrofecha = findViewById(R.id.btnfiltrofecha);
        //btnactualizar = findViewById(R.id.btnactualizar);
        etxfechaini = findViewById(R.id.etxfechaini);
        etxfechafin = findViewById(R.id.etxfechafin);
        etxfechainisem = findViewById(R.id.etxfechainisem);
        mChart = findViewById(R.id.lineChartData);
        spngalpongraf = findViewById(R.id.spngalpongraf);
        spinnerfilt = findViewById(R.id.spfiltrodatos);
        spfiltrox = findViewById(R.id.spfiltrox);
        spinnerfilt = findViewById(R.id.spfiltrodatos);
        spfiltrox = findViewById(R.id.spfiltrox);
        lyxsemana = findViewById(R.id.lyxsemana);
        lyporfecha = findViewById(R.id.lyporfecha);
        lyporfecha.setVisibility(View.VISIBLE);
        lyxsemana.setVisibility(View.GONE);
        skbsemana = findViewById(R.id.skbsemana);
        nombregranja = findViewById(R.id.nombregranja);

        tfavv = ResourcesCompat.getFont(this, R.font.gothica1black);
        toolbargrafica = findViewById(R.id.toolbargrafica);
        setSupportActionBar(toolbargrafica);
        toolbargrafica.setTitleTextColor(getResources().getColor(R.color.blackcolor));
        getSupportActionBar().setTitle("GRÁFICA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        nombregranja.setText(nombreg);

        galponselect = 1;
        fechasChart = new ArrayList<Long>();
        dataSets = new ArrayList<>();
        valuesfilt=new ArrayList<>();
        spfiltroxval=new ArrayList<>();
        values=new ArrayList<>();

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setDrawGridBackground(false);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getAxisRight().setDrawGridLines(false);
        mChart.getDescription().setEnabled(false);

        limitLine = new LimitLine(40f, "PELIGRO");
        limitLine.setLineColor(Color.RED);
        limitLine.setLineWidth(2f);
        limitLine.enableDashedLine(5f, 5f, 0);
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLine.setTextSize(15f);

        limitLineb = new LimitLine(10f, "MUY BAJO");
        limitLineb.setLineWidth(2f);
        limitLineb.setLineColor(Color.BLUE);
        limitLineb.enableDashedLine(5f, 5f, 0);
        limitLineb.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        limitLineb.setTextSize(15f);
    }

    private void DibujarChart(final Integer galponn, final String rd){
        System.gc();
        /* ---------------- ACA RELLENO EL ARRAY DE FECHAS -------------------------------- */
        myRef = mDatabase.child("datos").orderByChild("telefono").equalTo(telefono).getRef();
        //Query phoneQuery = myRef.orderByChild();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(fechasChart!=null) fechasChart.clear();
                ldsTemperatura = null;
                ldsHumedad = null;
                ldsCo2 = null;
                ldsAmoniaco = null;

                if(ldsTemperatura!=null) ldsTemperatura.clear();
                if(ldsHumedad!=null) ldsHumedad.clear();
                if(ldsCo2!=null) ldsCo2.clear();
                if(ldsAmoniaco!=null) ldsAmoniaco.clear();

                mChart.invalidate();
                dataSets.clear();
                ArrayList<String> arrayDataF = new ArrayList<>();
                ArrayList<Entry> arrayDataT = new ArrayList<>();
                ArrayList<Entry> arrayDataH = new ArrayList<>();
                ArrayList<Entry> arrayDataA = new ArrayList<>();
                ArrayList<Entry> arrayDataC = new ArrayList<>();
                float i =0;
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    dato = singleSnapshot.getValue(Datos.class);
                    if(dato.getGalpon().equals(galponn)){
                        i=i+1;
                        arrayDataF.add(dato.getFecha());
                        arrayDataT.add(new Entry(i, Float.parseFloat(dato.getTemperatura())));
                        arrayDataH.add(new Entry(i, Float.parseFloat(dato.getHumedad())));
                        arrayDataA.add(new Entry(i, Float.parseFloat(dato.getAmoniaco())));
                        arrayDataC.add(new Entry(i, Float.parseFloat(dato.getCo2())));
                    }
                }

                fechasChart.addAll(arrayDataF);
                ldsTemperatura = new LineDataSet(arrayDataT, "Temperaturas en °C");
                ldsHumedad = new LineDataSet(arrayDataH, "Humedades");
                ldsCo2 = new LineDataSet(arrayDataA, "CO2");
                ldsAmoniaco = new LineDataSet(arrayDataC, "Amoniaco");

                propiedadeslined(ldsTemperatura,"#FF3300");
                propiedadeslined(ldsHumedad,"#3399FF");
                propiedadeslined(ldsCo2,"#99FF66");
                propiedadeslined(ldsAmoniaco,"#77DD66");

                switch(rd){
                    case "all":
                        posfiltro="all";
                        dataSets.add(ldsTemperatura);
                        dataSets.add(ldsHumedad);
                        dataSets.add(ldsCo2);
                        dataSets.add(ldsAmoniaco);
                        break;
                    case "temp":
                        posfiltro="temp";
                        dataSets.add(ldsTemperatura);
                        dataSets.remove(ldsHumedad);
                        dataSets.remove(ldsCo2);
                        dataSets.remove(ldsAmoniaco);
                        break;
                    case "hum":
                        posfiltro="hum";
                        dataSets.remove(ldsTemperatura);
                        dataSets.add(ldsHumedad);
                        dataSets.remove(ldsCo2);
                        dataSets.remove(ldsAmoniaco);
                        break;
                    case "co2":
                        posfiltro="co2";
                        dataSets.remove(ldsTemperatura);
                        dataSets.remove(ldsHumedad);
                        dataSets.add(ldsCo2);
                        dataSets.remove(ldsAmoniaco);
                        break;
                    case "amon":
                        posfiltro="amon";
                        dataSets.remove(ldsTemperatura);
                        dataSets.remove(ldsHumedad);
                        dataSets.remove(ldsCo2);
                        dataSets.add(ldsAmoniaco);
                        break;
                }

                xAxis = mChart.getXAxis();
                PropiedadesXAxis(fechasChart);

                ylAxis = mChart.getAxisLeft();
                PropiedadesYAxis(50f, -20f);

                data = new LineData(dataSets);
                mChart.setData(data);
                mChart.animateY(1000);
                mChart.setVisibleXRangeMaximum(5);
                mChart.moveViewToX(mChart.getWidth());
                mChart.animate();
                mChart.invalidate();
                Log.d("DEBUG", "Salir Chart");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERRORFIREBASE", "onCancelled", databaseError.toException());
            }
        });

        Log.d("DEBUG", "SELECT SPINNER: "+galponn);

    }

    private void filtrarxfecha(final String rd) {
        System.gc();
        galponselect = spngalpongraf.getSelectedItemPosition() + 1;
        fechainicio = etxfechaini.getText().toString();
        fechafin = etxfechafin.getText().toString();

        if (fechainicio != null && fechainicio != "" && fechafin != null && fechafin != "") {
            /* ---------------- ACA RELLENO EL ARRAY DE FECHAS -------------------------------- */
            myRef = mDatabase.child("datos").orderByChild("telefono").equalTo(telefono).getRef();
            //Query phoneQuery = myRef.orderByChild();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Date dateinicio=new Date(),datefin=new Date(),datehoy=new Date();
                    if (fechasChart != null) fechasChart.clear();
                    ldsTemperatura = null;
                    ldsHumedad = null;
                    ldsCo2 = null;
                    ldsAmoniaco = null;

                    if (ldsTemperatura != null) ldsTemperatura.clear();
                    if (ldsHumedad != null) ldsHumedad.clear();
                    if (ldsCo2 != null) ldsCo2.clear();
                    if (ldsAmoniaco != null) ldsAmoniaco.clear();

                    mChart.invalidate();
                    dataSets.clear();
                    ArrayList<String> arrayDataF = new ArrayList<>();
                    ArrayList<Entry> arrayDataT = new ArrayList<>();
                    ArrayList<Entry> arrayDataH = new ArrayList<>();
                    ArrayList<Entry> arrayDataA = new ArrayList<>();
                    ArrayList<Entry> arrayDataC = new ArrayList<>();

                    float i = 0;
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        dato = singleSnapshot.getValue(Datos.class);
                        if (dato.getGalpon().equals(galponselect)) {

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                            try {
                                dateinicio = new Date(sdf.parse(fechainicio).getTime());
                                datefin = new Date(sdf.parse(fechafin).getTime());
                                String[] datetemp = dato.getFecha().split(" ");
                                datehoy = new Date(sdf.parse(datetemp[0]).getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(datehoy.after(dateinicio) && datehoy.before(datefin) || datehoy.compareTo(datefin)==0){
                                Log.d("FECHAS", dateinicio+","+datefin+","+datehoy);
                                i = i + 1;
                                arrayDataF.add(dato.getFecha());
                                arrayDataT.add(new Entry(i, Float.parseFloat(dato.getTemperatura())));
                                arrayDataH.add(new Entry(i, Float.parseFloat(dato.getHumedad())));
                                arrayDataA.add(new Entry(i, Float.parseFloat(dato.getAmoniaco())));
                                arrayDataC.add(new Entry(i, Float.parseFloat(dato.getCo2())));
                            }
                        }
                    }

                    fechasChart.addAll(arrayDataF);

                    ldsTemperatura = new LineDataSet(arrayDataT, "Temperaturas");
                    ldsHumedad = new LineDataSet(arrayDataH, "Humedades");
                    ldsCo2 = new LineDataSet(arrayDataA, "CO2");
                    ldsAmoniaco = new LineDataSet(arrayDataC, "Amoniaco");

                    propiedadeslined(ldsTemperatura, "#FF3300");
                    propiedadeslined(ldsHumedad, "#3399FF");
                    propiedadeslined(ldsCo2, "#99FF66");
                    propiedadeslined(ldsAmoniaco, "#77DD66");

                    switch (rd) {
                        case "all":
                            posfiltro = "all";
                            dataSets.add(ldsTemperatura);
                            dataSets.add(ldsHumedad);
                            dataSets.add(ldsCo2);
                            dataSets.add(ldsAmoniaco);
                            break;
                        case "temp":
                            posfiltro = "temp";
                            dataSets.add(ldsTemperatura);
                            dataSets.remove(ldsHumedad);
                            dataSets.remove(ldsCo2);
                            dataSets.remove(ldsAmoniaco);
                            break;
                        case "hum":
                            posfiltro = "hum";
                            dataSets.remove(ldsTemperatura);
                            dataSets.add(ldsHumedad);
                            dataSets.remove(ldsCo2);
                            dataSets.remove(ldsAmoniaco);
                            break;
                        case "co2":
                            posfiltro = "co2";
                            dataSets.remove(ldsTemperatura);
                            dataSets.remove(ldsHumedad);
                            dataSets.add(ldsCo2);
                            dataSets.remove(ldsAmoniaco);
                            break;
                        case "amon":
                            posfiltro = "amon";
                            dataSets.remove(ldsTemperatura);
                            dataSets.remove(ldsHumedad);
                            dataSets.remove(ldsCo2);
                            dataSets.add(ldsAmoniaco);
                            break;
                    }

                    xAxis = mChart.getXAxis();
                    PropiedadesXAxis(fechasChart);

                    ylAxis = mChart.getAxisLeft();
                    PropiedadesYAxis(50f, -20f);

                    data = new LineData(dataSets);
                    mChart.setData(data);
                    mChart.animateY(1000);
                    mChart.setVisibleXRangeMaximum(5);
                    mChart.moveViewToX(mChart.getWidth());
                    mChart.invalidate();
                    Log.d("DEBUG", "Salir Chart");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ERRORFIREBASE", "onCancelled", databaseError.toException());
                }
            });
        }
    }

    private void filtrarxsemana(Integer semana, final String rd){

        fechainicio = etxfechainisem.getText().toString();
        Date m = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(m);
        cal.add(Calendar.DATE, semana*7); // 10 is the days you want to add or subtract
        int mYear = cal.get(Calendar.YEAR); // current year
        int mMonth = cal.get(Calendar.MONTH); // current month
        int mDay = cal.get(Calendar.DAY_OF_MONTH); // current day
        fechafin = mDay + "-" + (mMonth + 1) + "-" + mYear;

        if(fechainicio!=null&&fechainicio!=""){

            /* ---------------- ACA RELLENO EL ARRAY DE FECHAS -------------------------------- */
            myRef = mDatabase.child("datos").orderByChild("telefono").equalTo(telefono).getRef();
            //Query phoneQuery = myRef.orderByChild();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Date dateinicio=new Date(),datefin=new Date(),datehoy=new Date();
                    if (fechasChart != null) fechasChart.clear();
                    ldsTemperatura = null;
                    ldsHumedad = null;
                    ldsCo2 = null;
                    ldsAmoniaco = null;

                    if (ldsTemperatura != null) ldsTemperatura.clear();
                    if (ldsHumedad != null) ldsHumedad.clear();
                    if (ldsCo2 != null) ldsCo2.clear();
                    if (ldsAmoniaco != null) ldsAmoniaco.clear();

                    mChart.invalidate();
                    dataSets.clear();
                    ArrayList<String> arrayDataF = new ArrayList<>();
                    ArrayList<Entry> arrayDataT = new ArrayList<>();
                    ArrayList<Entry> arrayDataH = new ArrayList<>();
                    ArrayList<Entry> arrayDataA = new ArrayList<>();
                    ArrayList<Entry> arrayDataC = new ArrayList<>();

                    float i = 0;
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        dato = singleSnapshot.getValue(Datos.class);
                        if (dato.getGalpon().equals(galponselect)) {

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                            try {
                                dateinicio = new Date(sdf.parse(fechainicio).getTime());
                                datefin = new Date(sdf.parse(fechafin).getTime());
                                String[] datetemp = dato.getFecha().split(" ");
                                datehoy = new Date(sdf.parse(datetemp[0]).getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(datehoy.after(dateinicio) && datehoy.before(datefin) || datehoy.compareTo(datefin)==0){
                                Log.d("FECHAS", dateinicio+","+datefin+","+datehoy);
                                i = i + 1;
                                arrayDataF.add(dato.getFecha());
                                arrayDataT.add(new Entry(i, Float.parseFloat(dato.getTemperatura())));
                                arrayDataH.add(new Entry(i, Float.parseFloat(dato.getHumedad())));
                                arrayDataA.add(new Entry(i, Float.parseFloat(dato.getAmoniaco())));
                                arrayDataC.add(new Entry(i, Float.parseFloat(dato.getCo2())));
                            }
                        }
                    }

                    fechasChart.addAll(arrayDataF);

                    ldsTemperatura = new LineDataSet(arrayDataT, "Temperaturas");
                    ldsHumedad = new LineDataSet(arrayDataH, "Humedades");
                    ldsCo2 = new LineDataSet(arrayDataA, "CO2");
                    ldsAmoniaco = new LineDataSet(arrayDataC, "Amoniaco");

                    propiedadeslined(ldsTemperatura, "#FF3300");
                    propiedadeslined(ldsHumedad, "#3399FF");
                    propiedadeslined(ldsCo2, "#99FF66");
                    propiedadeslined(ldsAmoniaco, "#77DD66");

                    switch (rd) {
                        case "all":
                            posfiltro = "all";
                            dataSets.add(ldsTemperatura);
                            dataSets.add(ldsHumedad);
                            dataSets.add(ldsCo2);
                            dataSets.add(ldsAmoniaco);
                            break;
                        case "temp":
                            posfiltro = "temp";
                            dataSets.add(ldsTemperatura);
                            dataSets.remove(ldsHumedad);
                            dataSets.remove(ldsCo2);
                            dataSets.remove(ldsAmoniaco);
                            break;
                        case "hum":
                            posfiltro = "hum";
                            dataSets.remove(ldsTemperatura);
                            dataSets.add(ldsHumedad);
                            dataSets.remove(ldsCo2);
                            dataSets.remove(ldsAmoniaco);
                            break;
                        case "co2":
                            posfiltro = "co2";
                            dataSets.remove(ldsTemperatura);
                            dataSets.remove(ldsHumedad);
                            dataSets.add(ldsCo2);
                            dataSets.remove(ldsAmoniaco);
                            break;
                        case "amon":
                            posfiltro = "amon";
                            dataSets.remove(ldsTemperatura);
                            dataSets.remove(ldsHumedad);
                            dataSets.remove(ldsCo2);
                            dataSets.add(ldsAmoniaco);
                            break;
                    }

                    xAxis = mChart.getXAxis();
                    PropiedadesXAxis(fechasChart);

                    ylAxis = mChart.getAxisLeft();
                    PropiedadesYAxis(50f, -20f);

                    data = new LineData(dataSets);
                    mChart.setData(data);
                    mChart.animateY(1000);
                    mChart.setVisibleXRangeMaximum(5);
                    mChart.moveViewToX(mChart.getWidth());
                    mChart.invalidate();
                    Log.d("DEBUG", "Salir Chart");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ERRORFIREBASE", "onCancelled", databaseError.toException());
                }
            });
        }

    }

    private void showViewFiltro(@org.jetbrains.annotations.NotNull String optionfiltro){
        switch (optionfiltro){
            case "xfecha":
                lyxsemana.setVisibility(View.GONE);
                lyporfecha.setVisibility(View.VISIBLE);
                break;
            case "xsemana":
                lyxsemana.setVisibility(View.VISIBLE);
                lyporfecha.setVisibility(View.GONE);
                break;
        }
    }

    private void propiedadeslined(LineDataSet lineDataSet, String Colorx){
        //lineDataSet.setFillColor(Color.parseColor(Colorx));
        lineDataSet.setColor(Color.parseColor(Colorx));
        lineDataSet.setCircleColor(Color.parseColor(Colorx));
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setValueTextSize(11f);
        //lineDataSet.setDrawFilled(true);
        lineDataSet.setValueTypeface(tfavv);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //lineDataSet.setFillAlpha(110);
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
                finish();
                return true;
            case R.id.action_help: //hago un case por si en un futuro agrego mas opciones
                return true;
            case R.id.action_settings: //hago un case por si en un futuro agrego mas opciones
                Intent n = new Intent(this, OptionsActivity.class);
                n.putExtra("telefono", telefono);
                n.putExtra("nombregranja", nombreg);
                n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(n);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Date variarFecha(Date fecha, int campo, int valor){
        if (valor==0) return fecha;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(campo, valor);
        return calendar.getTime();
    }

    public void PropiedadesYAxis(Float Xmax, Float Xmin){
        ylAxis.removeAllLimitLines();
        ylAxis.addLimitLine(limitLine);
        ylAxis.addLimitLine(limitLineb);
        ylAxis.setAxisMaximum(Xmax);
        ylAxis.setAxisMinimum(Xmin);
        ylAxis.enableGridDashedLine(5f,5f,0);
        ylAxis.setDrawGridLinesBehindData(true);
    }

    public void PropiedadesXAxis(ArrayList fChart){
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(fChart));
    }

    // ---------------------------------------------------------------------------//

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        //Toast.makeText(this, "Start"+me.getX()+me.getY(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        //Toast.makeText(this, "End"+lastPerformedGesture, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        //Toast.makeText(this, "LongPressed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        //Toast.makeText(this, "Double", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        //Toast.makeText(this, "SingleTaped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        //Toast.makeText(this, "CF"+velocityX+"--"+velocityY, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        //Toast.makeText(this, "CS"+scaleX+"--"+scaleY, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        // Toast.makeText(this, "CT"+dX+"--"+dY, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        String dat, datx, daty;
        dat = e.toString().trim();
        datx = dat.substring(10, 12);
        daty = dat.substring(17, dat.length());
        if(datx.contains(".")) {
            datx = dat.substring(10, 11);
            daty = dat.substring(17, dat.length());
        }
        Toast toast = Toast.makeText(this, "Fecha de medicion\n" + fechasChart.get(Integer.parseInt(datx)-1)
                + "\nTemperatura: " + daty + "°C", Toast.LENGTH_LONG);
        //Entry, x: 23.0 y: 29.69
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    @Override
    public void onNothingSelected() {

    }

}
