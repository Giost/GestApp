package com.mcteam.gestapp.Moduli.Gestionale.Nominativo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcteam.gestapp.Models.Rubrica.Nominativo;
import com.mcteam.gestapp.Models.UserInfo;
import com.mcteam.gestapp.Moduli.Gestionale.Societa.VisualizzaSocietaActivity;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

public class StampaNominativoActivity extends AppCompatActivity {

    TextView mTitoloView;
    TextView mCognomeView;
    TextView mNomeView;
    TextView mIndirizzoView;
    TextView mCapView;
    TextView mProvinciaView;
    TextView mCittaView;
    TextView mDataNascitaView;
    TextView mLuogoNascitaView;
    TextView mPIVAView;
    TextView mCod_FiscaleView;
    TextView mCartaIDView;
    TextView mPatenteView;
    TextView mTesseraSanitariaView;
    TextView mSitoWebView;
    TextView mNomeBancaView;
    TextView mIndirizzoBancaView;
    TextView mIBANView;
    TextView mNazionalitaView;
    TextView mEmailView;
    TextView mNoteView;
    TextView mNoteDettView;
    Button mViewSocieta;
    TextView mSocietaView;
    TextView mCellulareView;
    LinearLayout mDettagliView;
    Nominativo mNominativoAttuale = null;
    UserInfo mActualUser;

    {
        mActualUser = null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stampa_nominativo);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        Intent intent = getIntent();
        mNominativoAttuale = intent.getParcelableExtra("nominativoToPrint");

        mActualUser = intent.getParcelableExtra("actualUser");


        if (mNominativoAttuale == null) {
            finish();
        }


        mTitoloView = (TextView) findViewById(R.id.stampa_nominativo_titolo);
        mCognomeView = (TextView) findViewById(R.id.stampa_nominativo_cognome);
        mNomeView = (TextView) findViewById(R.id.stampa_nominativo_nome);
        mIndirizzoView = (TextView) findViewById(R.id.stampa_nominativo_indirizzo);
        mCapView = (TextView) findViewById(R.id.stampa_nominativo_cap);
        mProvinciaView = (TextView) findViewById(R.id.stampa_nominativo_provincia);
        mCittaView = (TextView) findViewById(R.id.stampa_nominativo_citta);
        mDataNascitaView = (TextView) findViewById(R.id.stampa_nominativo_data_nascita);
        mLuogoNascitaView = (TextView) findViewById(R.id.stampa_nominativo_luogo_nascita);
        mPIVAView = (TextView) findViewById(R.id.stampa_nominativo_partita_iva);
        mCod_FiscaleView = (TextView) findViewById(R.id.stampa_nominativo_codice_fiscale);
        mCartaIDView = (TextView) findViewById(R.id.stampa_nominativo_carta_identita);
        mPatenteView = (TextView) findViewById(R.id.stampa_nominativo_patente);
        mTesseraSanitariaView = (TextView) findViewById(R.id.stampa_nominativo_tessera_sanitaria);
        mSitoWebView = (TextView) findViewById(R.id.stampa_nominativo_sito_web);
        mNomeBancaView = (TextView) findViewById(R.id.stampa_nominativo_nome_banca);
        mIndirizzoBancaView = (TextView) findViewById(R.id.stampa_nominativo_indirizzo_banca);
        mIBANView = (TextView) findViewById(R.id.stampa_nominativo_iban);
        mNazionalitaView = (TextView) findViewById(R.id.stampa_nominativo_nazionalita);
        mEmailView = (TextView) findViewById(R.id.stampa_nominativo_email);
        mNoteView = (TextView) findViewById(R.id.stampa_nominativo_note);
        mNoteDettView = (TextView) findViewById(R.id.stampa_nominativo_note_dettaglio);
        mSocietaView = (TextView) findViewById(R.id.stampa_nominativo_societa);
        mCellulareView = (TextView) findViewById(R.id.stampa_nominativo_cellulare);

        mViewSocieta = (Button) findViewById(R.id.stampa_nominativo_societa_visualizza_button);


        mViewSocieta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modificaIntent = new Intent(getApplicationContext(), VisualizzaSocietaActivity.class);
                modificaIntent.putExtra("societaToView", mNominativoAttuale.getSocieta());
                startActivity(modificaIntent);
            }
        });

        Button stampa = (Button) findViewById(R.id.stampa_nominativo_stampa);

        stampa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    NominativoUtils.printSimple(mNominativoAttuale, getApplicationContext(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button stampaDett = (Button) findViewById(R.id.stampa_nominativo_stampa_dettagli);

        stampaDett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    NominativoUtils.printSimple(mNominativoAttuale, getApplicationContext(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button moreDettagli = (Button) findViewById(R.id.stampa_nominativo_mostra_dettagli_button);

        if (!mActualUser.isAmministratore()) {
            moreDettagli.setVisibility(View.GONE);
            stampaDett.setVisibility(View.GONE);

        }

        if (mNominativoAttuale.getSocieta() != null) {
            mViewSocieta.setVisibility(View.VISIBLE);
        }

        mDettagliView = (LinearLayout) findViewById(R.id.stampa_nominativo_dettagli_view);

        moreDettagli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDettagliView.getVisibility() == View.GONE) {
                    mDettagliView.setVisibility(View.VISIBLE);
                } else {
                    mDettagliView.setVisibility(View.GONE);
                }
            }
        });

        viewSetup(mNominativoAttuale);

    }

    void viewSetup(Nominativo nominativo) {

        mTitoloView.setText(nominativo.getTitolo());
        mCognomeView.setText(nominativo.getCognome());
        mNomeView.setText(nominativo.getNome());
        mIndirizzoView.setText(nominativo.getIndirizzo());
        mCapView.setText(nominativo.getCap());
        mProvinciaView.setText(nominativo.getProvincia());
        mCittaView.setText(nominativo.getCitta());
        mDataNascitaView.setText(nominativo.getDataNascita() == null ? " " : TextUtils.isEmpty(nominativo.getDataNascita()) ? " " : nominativo.getDataNascita());
        mLuogoNascitaView.setText(nominativo.getLuogoNascita());
        mPIVAView.setText(nominativo.getPIVA());
        mCod_FiscaleView.setText(nominativo.getCod_Fiscale());
        mCartaIDView.setText(nominativo.getCartaID());
        mPatenteView.setText(nominativo.getPatente());
        mTesseraSanitariaView.setText(nominativo.getTesseraSanitaria());
        mSitoWebView.setText(nominativo.getSitoWeb());
        mNomeBancaView.setText(nominativo.getNomeBanca());
        mIndirizzoBancaView.setText(nominativo.getIndirizzoBanca());
        mIBANView.setText(nominativo.getIBAN());
        mNazionalitaView.setText(nominativo.getNazionalita());
        mEmailView.setText(nominativo.getEmail());
        mNoteView.setText(nominativo.getNote());
        mNoteDettView.setText(nominativo.getNoteDett());
        if (nominativo.getSocieta() != null)
            mSocietaView.setText(nominativo.getSocieta().getNomeSocietà());
        mCellulareView.setText(nominativo.getCellulare());

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
