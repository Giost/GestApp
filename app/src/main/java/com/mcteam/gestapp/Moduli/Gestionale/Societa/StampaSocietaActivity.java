package com.mcteam.gestapp.Moduli.Gestionale.Societa;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mcteam.gestapp.Models.Rubrica.Societa;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

public class StampaSocietaActivity extends AppCompatActivity {

    TextView mNomeSocieta;
    TextView mTipologia;
    TextView mCodiceSocietaView;
    TextView mIndirizzo;
    TextView mTelefono;
    TextView mFax;
    TextView mCap;
    TextView mProvincia;
    TextView mNote;
    TextView mCOD_FISCALE;
    TextView mPartita_iva;
    TextView mStato;
    TextView mSito;
    TextView mCitta;
    TextView mCellulare;

    Societa mCurrentSocieta;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stampa_societa);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        mNomeSocieta = (TextView) findViewById(R.id.stampa_societa_nome);
        mTipologia = (TextView) findViewById(R.id.stampa_societa_tipologia);
        mCodiceSocietaView = (TextView) findViewById(R.id.stampa_societa_codice_societa);
        mIndirizzo = (TextView) findViewById(R.id.stampa_societa_indirizzo);
        mCap = (TextView) findViewById(R.id.stampa_societa_cap);
        mProvincia = (TextView) findViewById(R.id.stampa_societa_provincia);
        mNote = (TextView) findViewById(R.id.stampa_societa_note);
        mCellulare = (TextView) findViewById(R.id.stampa_societa_cellulare);
        mTelefono = (TextView) findViewById(R.id.stampa_societa_telefono);
        mFax = (TextView) findViewById(R.id.stampa_societa_fax);
        mCOD_FISCALE = (TextView) findViewById(R.id.stampa_societa_codice_fiscale);
        mPartita_iva = (TextView) findViewById(R.id.stampa_societa_partita_iva);
        mStato = (TextView) findViewById(R.id.stampa_societa_stato);
        mSito = (TextView) findViewById(R.id.stampa_societa_sito);
        mCitta = (TextView) findViewById(R.id.stampa_societa_citta);

        Button printButton = (Button) findViewById(R.id.stampa_societa_stampa);


        Intent intent = getIntent();
        final Societa currentSocieta = intent.getParcelableExtra("societaToPrint");
        mCurrentSocieta = currentSocieta;

        setupView(currentSocieta);

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    print(currentSocieta);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void print(Societa currentSocieta) throws Exception {
        SocietaUtils.print(currentSocieta, getApplicationContext());
    }


    private void setupView(Societa currentSocieta) {
        mNomeSocieta.setText(currentSocieta.getNomeSocietà());

        String tipologia = currentSocieta.getmTipologia();
        if (tipologia.equals("C")) {
            mTipologia.setText("Cliente");
        } else if (tipologia.equals("F")) {
            mTipologia.setText("Forintore");
        } else if (tipologia.equals("P"))
            mTipologia.setText("Personale");

        mCodiceSocietaView.setText("" + currentSocieta.getCodiceSocieta());
        mCellulare.setText(currentSocieta.getmCellulare().equals("0") ? "" : currentSocieta.getmCellulare());
        mTelefono.setText(currentSocieta.getmTelefono());
        mIndirizzo.setText(currentSocieta.getIndirizzo());
        mCap.setText(currentSocieta.getCap());
        mCitta.setText(currentSocieta.getmCitta());
        mProvincia.setText(currentSocieta.getmProvincia());
        mNote.setText(currentSocieta.getNote());
        mCOD_FISCALE.setText(currentSocieta.getCOD_FISCALE());
        mPartita_iva.setText(currentSocieta.getPartitaIva());
        mStato.setText(currentSocieta.getStato());
        mSito.setText(currentSocieta.getSito());
        mFax.setText(currentSocieta.getmFax() == null ? " " : currentSocieta.getmFax().equals("null") ? " " : currentSocieta.getmFax());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                goHome();
                return true;
            case R.id.action_logout:
                Functions.logout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goHome() {
        Intent goHome = new Intent(this, HomeActivity.class);
        goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goHome);
        finish();
    }


}
