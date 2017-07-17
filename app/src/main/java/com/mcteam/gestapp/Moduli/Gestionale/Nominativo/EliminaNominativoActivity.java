package com.mcteam.gestapp.Moduli.Gestionale.Nominativo;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mcteam.gestapp.Models.Rubrica.Nominativo;
import com.mcteam.gestapp.Models.UserInfo;
import com.mcteam.gestapp.Moduli.Gestionale.Societa.VisualizzaSocietaActivity;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.PUTRequest;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import org.json.JSONException;
import org.json.JSONObject;

public class EliminaNominativoActivity extends AppCompatActivity {

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
    UserInfo mActualUser = null;
    AlertDialog.Builder deleteDialogBuilder;
    AlertDialog mDeleteDialog;
    RequestQueue mRequestQueue;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elimina_nominativo);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        Intent intent = getIntent();
        mNominativoAttuale = intent.getParcelableExtra("nominativoToDelete");

        mActualUser = intent.getParcelableExtra("actualUser");


        if (mNominativoAttuale == null) {
            finish();
        }


        mTitoloView = (TextView) findViewById(R.id.elimina_nominativo_titolo);
        mCognomeView = (TextView) findViewById(R.id.elimina_nominativo_cognome);
        mNomeView = (TextView) findViewById(R.id.elimina_nominativo_nome);
        mIndirizzoView = (TextView) findViewById(R.id.elimina_nominativo_indirizzo);
        mCapView = (TextView) findViewById(R.id.elimina_nominativo_cap);
        mProvinciaView = (TextView) findViewById(R.id.elimina_nominativo_provincia);
        mCittaView = (TextView) findViewById(R.id.elimina_nominativo_citta);
        mDataNascitaView = (TextView) findViewById(R.id.elimina_nominativo_data_nascita);
        mLuogoNascitaView = (TextView) findViewById(R.id.elimina_nominativo_luogo_nascita);
        mPIVAView = (TextView) findViewById(R.id.elimina_nominativo_partita_iva);
        mCod_FiscaleView = (TextView) findViewById(R.id.elimina_nominativo_codice_fiscale);
        mCartaIDView = (TextView) findViewById(R.id.elimina_nominativo_carta_identita);
        mPatenteView = (TextView) findViewById(R.id.elimina_nominativo_patente);
        mTesseraSanitariaView = (TextView) findViewById(R.id.elimina_nominativo_tessera_sanitaria);
        mSitoWebView = (TextView) findViewById(R.id.elimina_nominativo_sito_web);
        mNomeBancaView = (TextView) findViewById(R.id.elimina_nominativo_nome_banca);
        mIndirizzoBancaView = (TextView) findViewById(R.id.elimina_nominativo_indirizzo_banca);
        mIBANView = (TextView) findViewById(R.id.elimina_nominativo_iban);
        mNazionalitaView = (TextView) findViewById(R.id.elimina_nominativo_nazionalita);
        mEmailView = (TextView) findViewById(R.id.elimina_nominativo_email);
        mNoteView = (TextView) findViewById(R.id.elimina_nominativo_note);
        mNoteDettView = (TextView) findViewById(R.id.elimina_nominativo_note_dettagli);
        mSocietaView = (TextView) findViewById(R.id.elimina_nominativo_societa);
        mCellulareView = (TextView) findViewById(R.id.elimina_nominativo_cellulare);

        mViewSocieta = (Button) findViewById(R.id.elimina_nominativo_societa_visualizza_button);

        if (mNominativoAttuale.getSocieta() != null) {
            mViewSocieta.setVisibility(View.VISIBLE);
        }


        mViewSocieta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modificaIntent = new Intent(getApplicationContext(), VisualizzaSocietaActivity.class);
                modificaIntent.putExtra("societaToView", mNominativoAttuale.getSocieta());
                startActivity(modificaIntent);
            }
        });

        Button annulla = (Button) findViewById(R.id.elimina_nominativo_annulla_button);

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button elimina = (Button) findViewById(R.id.elimina_nominativo_conferma_button);

        elimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAlert();
            }
        });

        Button moreDettagli = (Button) findViewById(R.id.elimina_nominativo_mostra_dettagli_button);

        if (!mActualUser.isAmministratore()) {
            moreDettagli.setVisibility(View.GONE);
        }


        mDettagliView = (LinearLayout) findViewById(R.id.elimina_nominativo_visualizza_dettagli);

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

        mRequestQueue = Volley.newRequestQueue(this);

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
        mDataNascitaView.setText(nominativo.getDataNascita());
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

    private void attemptDelete() {
        String url = getString(R.string.mobile_url);
        url += "nominativo/" + mNominativoAttuale.getID();

        PUTRequest deleteRequestJson = new PUTRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean errore = response.getBoolean("error");
                    if (errore) {
                        showError(true);
                    } else {
                        showError(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showError(true);
            }
        });

        mRequestQueue.add(deleteRequestJson);
    }

    private void showDeleteAlert() {
        deleteDialogBuilder = new AlertDialog.Builder(this);
        deleteDialogBuilder.setTitle("Sicuro di voler eliminare?");
        deleteDialogBuilder.setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                attemptDelete();
            }
        });

        deleteDialogBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        mDeleteDialog = deleteDialogBuilder.create();
        mDeleteDialog.show();
    }


    public void showError(boolean error) {

        deleteDialogBuilder = new AlertDialog.Builder(this);
        if (error) {
            deleteDialogBuilder.setTitle("Errore eliminazione dati");
            deleteDialogBuilder.setMessage("Si è verificato un errore nella modifica dei dati");
            deleteDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog dialog = deleteDialogBuilder.create();
            dialog.show();
        } else {
            deleteDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    return;
                }
            });
            AlertDialog dialog = deleteDialogBuilder.create();
            dialog.setTitle("Successo!");
            dialog.setMessage("Eliminazione dati avvenuta con successo");
            dialog.show();
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
