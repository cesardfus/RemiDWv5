package com.prontec.remidw;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.prontec.remidw.secundarios.OptionsActivity;

public class LogActivity extends AppCompatActivity {

    private String telefono, nombreg;
    private Toolbar toolbardatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        telefono = getIntent().getExtras().getString("telefono");
        nombreg = getIntent().getExtras().getString("nombre");

        toolbardatos = findViewById(R.id.toolbardatos);
        setSupportActionBar(toolbardatos);
        toolbardatos.setTitleTextColor(getResources().getColor(R.color.blackcolor));
        getSupportActionBar().setTitle("ACTIVIDAD");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
}
