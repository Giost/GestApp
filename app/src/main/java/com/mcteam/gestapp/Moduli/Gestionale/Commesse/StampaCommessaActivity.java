package com.mcteam.gestapp.Moduli.Gestionale.Commesse;

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
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

public class StampaCommessaActivity extends AppCompatActivity {

    TextView mCodiceCommessa;
    TextView mNomeCommessa;
    TextView mCliente;
    TextView mCommerciale;
    TextView mData;
    TextView mReferente1;
    TextView mReferente2;
    TextView mReferenteOfferta1;
    TextView mReferenteOfferta2;
    TextView mReferenteOfferta3;
    TextView mNote;
    RadioButton mMarketingRadio;
    RadioButton mOffertaRadio;
    RadioButton mOrdineRadio;
    RadioButton mSviluppoRadio;
    RadioButton mFatturaRadio;
    RadioButton mPagamentoRadio;
    Commessa mCommessaAttuale = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elimina_commessa);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //***********************************************************************
        //Cambiare colore alla actionBar
        //************************************************************************
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            }
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        //**************************************
        //Get parcellable data from intent
        //**************************************

        mCommessaAttuale = getIntent().getParcelableExtra("commessaToPrint");

        //**************************************
        //Inizializza la view
        //**************************************

        mCodiceCommessa = (TextView) findViewById(R.id.elimina_commessa_codice_commessa);
        mNomeCommessa = (TextView) findViewById(R.id.elimina_commessa_nome);
        mCliente = (TextView) findViewById(R.id.elimina_commessa_cliente);
        mCommerciale = (TextView) findViewById(R.id.elimina_commessa_commerciale);
        mData = (TextView) findViewById(R.id.elimina_commessa_data);
        mReferente1 = (TextView) findViewById(R.id.elimina_commessa_referente_1);
        mReferente2 = (TextView) findViewById(R.id.elimina_commessa_referente_2);
        mReferenteOfferta1 = (TextView) findViewById(R.id.elimina_commessa_referente_offerta_1);
        mReferenteOfferta2 = (TextView) findViewById(R.id.elimina_commessa_referente_offerta_2);
        mReferenteOfferta3 = (TextView) findViewById(R.id.elimina_commessa_referente_offerta_3);
        mNote = (TextView) findViewById(R.id.elimina_commessa_note);

        mMarketingRadio = (RadioButton) findViewById(R.id.elimina_commessa_marketing);
        mOffertaRadio = (RadioButton) findViewById(R.id.elimina_commessa_offerta);
        mOrdineRadio = (RadioButton) findViewById(R.id.elimina_commessa_ordine);
        mSviluppoRadio = (RadioButton) findViewById(R.id.elimina_commessa_sviluppo);
        mFatturaRadio = (RadioButton) findViewById(R.id.elimina_commessa_fattura);
        mPagamentoRadio = (RadioButton) findViewById(R.id.elimina_commessa_pagamento);

        //**************************************
        //setup buttoni
        //**************************************

        Button modifica = (Button) findViewById(R.id.elimina_commessa_elimina);
        modifica.setText("STAMPA");
        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button annulla = (Button) findViewById(R.id.elimina_commessa_annulla);
        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupView(mCommessaAttuale);

    }

    void setupView(Commessa commessa) {

        Gson gson = new Gson();
        System.out.println(gson.toJson(commessa));

        mCodiceCommessa.setText(commessa.getCodice_commessa());

        mNomeCommessa.setText(commessa.getNome_commessa());

        mNote.setText(commessa.getNote());

        if (commessa.getCliente() != null)
            mCliente.setText(commessa.getCliente().getNomeSocietà());

        if (commessa.getCommerciale() != null)
            mCommerciale.setText(commessa.getCommerciale().getCognome() + " " + commessa.getCommerciale().getNome());

        mData.setText(Functions.validateReverseDate(commessa.getData()) ? Functions.getFormattedDate(commessa.getData()) : "");

        if (commessa.getReferente1() != null)
            mReferente1.setText(commessa.getReferente1().getCognome() + " " + commessa.getReferente1().getNome());

        if (commessa.getReferente2() != null)
            mReferente2.setText(commessa.getReferente2().getCognome() + " " + commessa.getReferente2().getNome());

        if (commessa.getAvanzamento().equals("marketing")) {
            mMarketingRadio.setChecked(true);
        } else if (commessa.getAvanzamento().equals("offerta")) {
            mOffertaRadio.setChecked(true);
        } else if (commessa.getAvanzamento().equals("ordine")) {
            mOrdineRadio.setChecked(true);
        } else if (commessa.getAvanzamento().equals("sviluppo")) {
            mSviluppoRadio.setChecked(true);
        } else if (commessa.getAvanzamento().equals("fattura")) {
            mFatturaRadio.setChecked(true);
        } else if (commessa.getAvanzamento().equals("pagamento")) {
            mPagamentoRadio.setChecked(true);
        }

        if (commessa.getReferente_offerta1() != null) {
            mReferenteOfferta1.setText(commessa.getReferente_offerta1().getCognome() + " " + commessa.getReferente_offerta1().getNome());
        }
        if (commessa.getReferente_offerta2() != null) {
            mReferenteOfferta2.setText(commessa.getReferente_offerta2().getCognome() + " " + commessa.getReferente_offerta1().getNome());
        }
        if (commessa.getReferente_offerta3() != null) {
            mReferenteOfferta3.setText(commessa.getReferente_offerta3().getCognome() + " " + commessa.getReferente_offerta1().getNome());
        }
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
