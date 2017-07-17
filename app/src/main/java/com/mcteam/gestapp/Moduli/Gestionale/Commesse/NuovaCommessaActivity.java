package com.mcteam.gestapp.Moduli.Gestionale.Commesse;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mcteam.gestapp.Fragments.DatePickerFragment;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Models.Rubrica.Nominativo;
import com.mcteam.gestapp.Models.Rubrica.Societa;
import com.mcteam.gestapp.Moduli.Gestionale.DateWatcher;
import com.mcteam.gestapp.Moduli.Gestionale.Nominativo.SocietaSpinnerAdapter;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.VolleyRequests;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import java.util.ArrayList;

public class NuovaCommessaActivity extends AppCompatActivity {

    EditText mCodiceCommessaView;
    EditText mNomeCommessaView;
    EditText mDataView;
    EditText mNoteView;

    Spinner mReferente1View;
    Spinner mReferente2View;
    Spinner mReferenteOfferta1View;
    Spinner mReferenteOfferta2View;
    Spinner mReferenteOfferta3View;
    Spinner mClienteView;
    Spinner mCommercialeView;

    RadioButton mMarketingRadio;
    RadioButton mOffertaRadio;
    RadioButton mOrdineRadio;
    RadioButton mSviluppoRadio;
    RadioButton mFatturaRadio;
    RadioButton mPagamentoRadio;

    Societa mSelectedCliente = null;
    Nominativo mSelectedCommerciale = null;
    Nominativo mSelectedReferente1 = null;
    Nominativo mSelectedReferente2 = null;
    Nominativo mSelectedReferenteOfferta1 = null;
    Nominativo mSelectedReferenteOfferta2 = null;
    Nominativo mSelectedReferenteOfferta3 = null;


    TextView mClienteErrorView;
    TextView mCommercialeErrorView;
    TextView mAvanzamentoErrorView;

    ArrayList<Nominativo> mNominativiList;
    ArrayList<Societa> mSocietaList;

    SocietaSpinnerAdapter mSocietaAdapter;
    NominativoSpinnerAdapter mNominativoAdapter;

    VolleyRequests mVolleyRequests;

    Boolean isModifica = false;
    Commessa mCommessaToModify = null;


    Gson gson;
    private Commessa upNominativoSpinner;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_commessa);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mCommessaToModify = getIntent().getParcelableExtra("commessaToModify");

        //***********************************************************************
        //Cambiare colore alla actionBar
        //************************************************************************
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        //***************************************************************************
        //Inizializzazione elementi view
        //***************************************************************************

        mCodiceCommessaView = (EditText) findViewById(R.id.nuova_commessa_codice_commessa);
        mNomeCommessaView = (EditText) findViewById(R.id.nuova_commessa_nome_commessa);
        mClienteView = (Spinner) findViewById(R.id.nuova_commessa_cliente_spinner);
        mCommercialeView = (Spinner) findViewById(R.id.nuova_commessa_commerciale_spinner);
        mNoteView = (EditText) findViewById(R.id.nuova_commessa_note);
        mDataView = (EditText) findViewById(R.id.nuova_commessa_data);
        mReferente1View = (Spinner) findViewById(R.id.nuova_commessa_referente_1);
        mReferente2View = (Spinner) findViewById(R.id.nuova_commessa_referente_2);
        mReferenteOfferta1View = (Spinner) findViewById(R.id.nuova_commessa_offerta_1);
        mReferenteOfferta2View = (Spinner) findViewById(R.id.nuova_commessa_offerta_2);
        mReferenteOfferta3View = (Spinner) findViewById(R.id.nuova_commessa_offeta_3);

        mMarketingRadio = (RadioButton) findViewById(R.id.nuova_commessa_marketing);
        mOffertaRadio = (RadioButton) findViewById(R.id.nuova_commessa_offerta);
        mOrdineRadio = (RadioButton) findViewById(R.id.nuova_commessa_ordine);
        mSviluppoRadio = (RadioButton) findViewById(R.id.nuova_commessa_sviluppo);
        mFatturaRadio = (RadioButton) findViewById(R.id.nuova_commessa_fattura);
        mPagamentoRadio = (RadioButton) findViewById(R.id.nuova_commessa_pagamento);


        mClienteErrorView = (TextView) findViewById(R.id.nuova_commessa_cliente_error);
        mCommercialeErrorView = (TextView) findViewById(R.id.nuova_commessa_commerciale_error);
        mAvanzamentoErrorView = (TextView) findViewById(R.id.nuova_commessa_avanzamento_error);

        //***************************************************************************
        //Inizializzazione variabili
        //***************************************************************************

        mNominativiList = new ArrayList<>();
        mSocietaList = new ArrayList<>();
        mVolleyRequests = new VolleyRequests(this, this);
        gson = new Gson();
        mDataView.addTextChangedListener(new DateWatcher(mDataView));

        //***************************************************************************
        //Inizializzazione button
        //***************************************************************************
        Button creaNuova = (Button) findViewById(R.id.nuova_commessa_crea_nuovo);
        Button annulla = (Button) findViewById(R.id.nuova_commessa_annulla);

        creaNuova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddNew();
            }
        });

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //***************************************************************************
        //Inizializzazione spinner adapter
        //***************************************************************************
        mSocietaAdapter = new SocietaSpinnerAdapter(this, R.layout.nominativo_societa_spinner_row, mSocietaList);
        mNominativoAdapter = new NominativoSpinnerAdapter(this, R.layout.nominativo_societa_spinner_row, mNominativiList);

        mNominativoAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (isModifica) {
                    setupNominativoSpinner(mCommessaToModify);
                }
            }

        });

        mSocietaAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (isModifica)
                    setupSocietàSpinners(mCommessaToModify);
            }
        });

        //***************************************************************************
        //inizializzazione adapter spinner
        //***************************************************************************
        mClienteView.setAdapter(mSocietaAdapter);
        mCommercialeView.setAdapter(mNominativoAdapter);
        mReferente1View.setAdapter(mNominativoAdapter);
        mReferente2View.setAdapter(mNominativoAdapter);
        mReferenteOfferta1View.setAdapter(mNominativoAdapter);
        mReferenteOfferta2View.setAdapter(mNominativoAdapter);
        mReferenteOfferta3View.setAdapter(mNominativoAdapter);

        //***************************************************************************
        //richiesta dati dal server
        //***************************************************************************
        mVolleyRequests.getNominativiList(mNominativiList, mNominativoAdapter);
        mVolleyRequests.getSocietaList(mSocietaList, mSocietaAdapter);

        //***************************************************************************
        //Inizializzazione listener per i spinner
        //***************************************************************************
        mReferente1View.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    mSelectedReferente1 = (Nominativo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mReferente2View.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    mSelectedReferente2 = (Nominativo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mReferenteOfferta1View.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    mSelectedReferenteOfferta1 = (Nominativo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mReferenteOfferta2View.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    mSelectedReferenteOfferta2 = (Nominativo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mReferenteOfferta3View.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    mSelectedReferenteOfferta3 = (Nominativo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mClienteView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    mSelectedCliente = (Societa) parent.getItemAtPosition(position);
                    clearError(mClienteErrorView);
                } else
                    mSelectedCliente = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSelectedCliente = null;
            }
        });

        mCommercialeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    mSelectedCommerciale = (Nominativo) parent.getItemAtPosition(position);
                    clearError(mCommercialeErrorView);
                } else
                    mSelectedCommerciale = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSelectedCommerciale = null;
            }
        });

        mDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSelData();
            }
        });


        //***************************************************************************
        //Inizializzazione buttons
        //***************************************************************************

        if (mCommessaToModify != null && mCommessaToModify.isValid()) {
            isModifica = true;
            setupView(mCommessaToModify);
        }

        if (isModifica) {
            getSupportActionBar().setTitle("Modifica Commessa");
            creaNuova.setText("Modifica");
        }


    }

    public void attemptAddNew() {

        boolean cancel = false;
        View focusView = null;

        mCodiceCommessaView.setError(null);
        mNomeCommessaView.setError(null);
        mDataView.setError(null);

        String codiceCommessa = mCodiceCommessaView.getText().toString();
        String nomeCommessa = mNomeCommessaView.getText().toString();
        String data = mDataView.getText().toString();
        String note = mNoteView.getText().toString();
        Commessa commessa;

        if (!isModifica) {
            commessa = new Commessa();
        } else {
            commessa = mCommessaToModify;
        }

        if (!TextUtils.isEmpty(codiceCommessa)) {
            commessa.setCodice_commessa(codiceCommessa);
        } else {
            mCodiceCommessaView.setError("Campo neccessario");
            focusView = mCodiceCommessaView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(nomeCommessa)) {
            commessa.setNome_commessa(nomeCommessa);
        } else {
            mNomeCommessaView.setError("Campo neccessario");
            focusView = mNomeCommessaView;
            cancel = true;
        }

        //********************* CONTROLLO DATA *****************************//

        if (!TextUtils.isEmpty(data)) {
            if (Functions.validateNormalDate(data)) {
                try {
                    data = Functions.fromDateToSql(data);
                    commessa.setData(data);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            } else {
                mDataView.setError("Campo neccessario");
                focusView = mDataView;
                cancel = true;
            }
        } else {
            mDataView.setError("Campo neccessario");
            focusView = mDataView;
            cancel = true;
        }

        //***************************************************************//

        if (mSelectedCliente != null) {
            commessa.setId_cliente(mSelectedCliente.getID());
        } else {
            mClienteErrorView.setError("Scegliere un cliente");
            focusView = mClienteErrorView;
            cancel = true;
        }

        if (mSelectedCommerciale != null) {
            commessa.setId_commerciale(mSelectedCommerciale.getID());
        } else {
            mCommercialeErrorView.setError("Scegliere un commerciale");
            focusView = mCommercialeErrorView;
            cancel = true;
        }

        if (mSelectedReferente1 != null) {
            commessa.setId_referente_1(mSelectedReferente1.getID());
        }

        if (mSelectedReferente2 != null) {
            commessa.setId_referente_2(mSelectedReferente2.getID());
        }

        if (mSelectedReferenteOfferta1 != null) {
            commessa.setOff1(mSelectedReferenteOfferta1.getID());
        }

        if (mSelectedReferenteOfferta2 != null) {
            commessa.setOff2(mSelectedReferenteOfferta2.getID());
        }

        if (mSelectedReferenteOfferta3 != null) {
            commessa.setOff3(mSelectedReferenteOfferta3.getID());
        }

        String avanzamento = "";

        if (mMarketingRadio.isChecked())
            avanzamento = "marketing";
        else if (mOffertaRadio.isChecked())
            avanzamento = "offerta";
        else if (mOrdineRadio.isChecked())
            avanzamento = "ordine";
        else if (mSviluppoRadio.isChecked())
            avanzamento = "sviluppo";
        else if (mFatturaRadio.isChecked())
            avanzamento = "fattura";
        else if (mPagamentoRadio.isChecked())
            avanzamento = "pagamento";

        if (!TextUtils.isEmpty(avanzamento)) {
            commessa.setAvanzamento(avanzamento);
            mAvanzamentoErrorView.setError(null);
            mAvanzamentoErrorView.clearFocus();
        } else {
            mAvanzamentoErrorView.setError("Scegliere un avanzamento");
            focusView = mAvanzamentoErrorView;
            cancel = true;
        }

        commessa.setNote(note);

        if (!cancel) {

            String json = gson.toJson(commessa);

            System.out.println(json);

            if (!isModifica) {
                mVolleyRequests.addNewElementRequest(json, "commessa-nuovo");
            } else {
                mVolleyRequests.addNewElementRequest(json, "commessa/" + commessa.getID());
            }
        } else {
            focusView.requestFocus();
        }
    }

    public void setupView(Commessa commessa) {

        mCodiceCommessaView.setText(commessa.getCodice_commessa() + "");
        mNomeCommessaView.setText(commessa.getNome_commessa());

        if (commessa.getData() != null)
            try {
                mDataView.setText(Functions.getFormattedDate(commessa.getData()));
            } catch (Exception e) {
                mDataView.setText(commessa.getData());
            }

        mNoteView.setText(commessa.getNote());

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

    }


    public void setupSocietàSpinners(Commessa commessa) {

        int indexCliente;


        if (commessa.getCliente() != null) {
            indexCliente = mSocietaList.indexOf(commessa.getCliente());
            System.out.println("index cliente " + indexCliente);
            mClienteView.setSelection(indexCliente);
        }
    }

    public void setupNominativoSpinner(Commessa commessa) {
        int indexReferente1;
        int indexReferente2;
        int indexReferenteOfferta1;
        int indexReferenteOfferta2;
        int indexReferenteOfferta3;
        int indexCommerciale;

        if (commessa.getReferente1() != null) {
            indexReferente1 = mNominativiList.indexOf(commessa.getReferente1());
            System.out.println("index referente " + indexReferente1);
            mReferente1View.setSelection(indexReferente1);
        }

        if (commessa.getReferente2() != null) {
            indexReferente2 = mNominativiList.indexOf(commessa.getReferente2());
            System.out.println("index referente 2 " + indexReferente2);
            mReferente2View.setSelection(indexReferente2);
        }

        if (commessa.getReferente_offerta1() != null) {
            indexReferenteOfferta1 = mNominativiList.indexOf(commessa.getReferente_offerta1());
            System.out.println("index referente off 1 " + indexReferenteOfferta1);
            mReferenteOfferta1View.setSelection(indexReferenteOfferta1);
        }

        if (commessa.getReferente_offerta2() != null) {
            indexReferenteOfferta2 = mNominativiList.indexOf(commessa.getReferente_offerta2());
            System.out.println("index referente off 2 " + indexReferenteOfferta2);
            mReferenteOfferta2View.setSelection(indexReferenteOfferta2);
        }

        if (commessa.getReferente_offerta3() != null) {
            indexReferenteOfferta3 = mNominativiList.indexOf(commessa.getReferente_offerta3());
            mReferenteOfferta3View.setSelection(indexReferenteOfferta3);
            System.out.println("index referente off 3 " + indexReferenteOfferta3);

        }

        if (commessa.getCommerciale() != null) {
            indexCommerciale = mNominativiList.indexOf(commessa.getCommerciale());
            System.out.println("index commerciale " + indexCommerciale);
            mCommercialeView.setSelection(indexCommerciale);
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

    public void onClickSelData() {
        DialogFragment dialogFragment = new DatePickerFragment(mDataView);
        dialogFragment.show(getFragmentManager(), "datePicker");
    }

    private void goHome() {
        Intent goHome = new Intent(this, HomeActivity.class);
        goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goHome);
        finish();
    }

    private void clearError(TextView view) {
        view.setError(null);
        view.clearFocus();
    }

}
