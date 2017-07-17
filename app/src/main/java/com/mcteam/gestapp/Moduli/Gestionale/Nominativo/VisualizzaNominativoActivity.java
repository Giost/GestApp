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
import android.text.TextUtils;
import android.util.Log;
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
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mcteam.gestapp.Application.MyApp;
import com.mcteam.gestapp.Models.Rubrica.Nominativo;
import com.mcteam.gestapp.Models.UserInfo;
import com.mcteam.gestapp.Moduli.Gestionale.Societa.VisualizzaSocietaActivity;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.PUTRequest;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Constants;
import com.mcteam.gestapp.Utils.Functions;

import org.json.JSONException;
import org.json.JSONObject;

public class VisualizzaNominativoActivity extends AppCompatActivity {

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
    TextView mFaxView;
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
    TextView mTelefonoView;
    LinearLayout mDettagliView;
    Nominativo mNominativoAttuale = null;
    UserInfo mActualUser = null;
    RequestQueue mRequestQueue;
    AlertDialog.Builder deleteDialogBuilder;
    AlertDialog mDeleteDialog;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_nominativo);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        //Get data from intent
        mNominativoAttuale = getIntent().getParcelableExtra("nominativoToView");
        mActualUser = getIntent().getParcelableExtra("actualUser");

        if (mActualUser == null)
            mActualUser = ((MyApp) this.getApplication()).getCurrentUser();

        if (mNominativoAttuale == null) {
            finish();
        }

        mTitoloView = (TextView) findViewById(R.id.visualizza_nominativo_titolo);
        mCognomeView = (TextView) findViewById(R.id.visualizza_nominativo_cognome);
        mNomeView = (TextView) findViewById(R.id.visualizza_nominativo_nome);
        mIndirizzoView = (TextView) findViewById(R.id.visualizza_nominativo_indirizzo);
        mCapView = (TextView) findViewById(R.id.visualizza_nominativo_cap);
        mProvinciaView = (TextView) findViewById(R.id.visualizza_nominativo_provincia);
        mCittaView = (TextView) findViewById(R.id.visualizza_nominativo_citta);
        mDataNascitaView = (TextView) findViewById(R.id.visualizza_nominativo_data_nascita);
        mLuogoNascitaView = (TextView) findViewById(R.id.visualizza_nominativo_luogo_nascita);
        mPIVAView = (TextView) findViewById(R.id.visualizza_nominativo_partita_iva);
        mFaxView = (TextView) findViewById(R.id.visualizza_nominativo_fax);
        mCod_FiscaleView = (TextView) findViewById(R.id.visualizza_nominativo_codice_fiscale);
        mCartaIDView = (TextView) findViewById(R.id.visualizza_nominativo_carta_identita);
        mPatenteView = (TextView) findViewById(R.id.visualizza_nominativo_patente);
        mTesseraSanitariaView = (TextView) findViewById(R.id.visualizza_nominativo_tessera_sanitaria);
        mSitoWebView = (TextView) findViewById(R.id.visualizza_nominativo_sito_web);
        mNomeBancaView = (TextView) findViewById(R.id.visualizza_nominativo_nome_banca);
        mIndirizzoBancaView = (TextView) findViewById(R.id.visualizza_nominativo_indirizzo_banca);
        mIBANView = (TextView) findViewById(R.id.visualizza_nominativo_iban);
        mNazionalitaView = (TextView) findViewById(R.id.visualizza_nominativo_nazionalita);
        mEmailView = (TextView) findViewById(R.id.visualizza_nominativo_email);
        mNoteView = (TextView) findViewById(R.id.visualizza_nominativo_note);
        mNoteDettView = (TextView) findViewById(R.id.visualizza_nominativo_note_dett);
        mSocietaView = (TextView) findViewById(R.id.visualizza_nominativo_societa);
        mCellulareView = (TextView) findViewById(R.id.visualizza_nominativo_cellulare);
        mTelefonoView = (TextView) findViewById(R.id.visualizza_nominativo_telefono);

        mViewSocieta = (Button) findViewById(R.id.nominativo_societa_view);

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

        final BootstrapButton modifica = (BootstrapButton) findViewById(R.id.visualizza_nominativo_modifica_button);

        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modificaIntent = new Intent(getApplicationContext(), ModificaNominativoActivity.class);
                modificaIntent.putExtra("nominativoToModify", mNominativoAttuale);
                modificaIntent.putExtra("actualUser", mActualUser);
                startActivityForResult(modificaIntent, Constants.REQUEST_MODIFICA);
            }
        });

        Button moreDettagli = (Button) findViewById(R.id.visualizza_nominativo_mostra_dettagli_button);

        if (!mActualUser.isAmministratore()) {
            moreDettagli.setVisibility(View.GONE);
        }

        BootstrapButton stampaSemplice = (BootstrapButton) findViewById(R.id.visualizza_nominativo_stampa_button);

        stampaSemplice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    NominativoUtils.printSimple(mNominativoAttuale, getApplicationContext(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        Button stampaDettagli = (Button) findViewById(R.id.visualizza_nominativo_stampa_con_dettagli);

        if (!mActualUser.isAmministratore()) {
            stampaDettagli.setVisibility(View.GONE);
        }
        stampaDettagli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    NominativoUtils.printSimple(mNominativoAttuale, getApplicationContext(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        BootstrapButton deleteElement = (BootstrapButton) findViewById(R.id.visualizza_nominativo_elimina_button);

        deleteElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptDelete();
            }
        });


        mDettagliView = (LinearLayout) findViewById(R.id.visualizza_nominativo_visualizza_dettagli);

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
        deleteDialogBuilder = new AlertDialog.Builder(this);

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
        mDataNascitaView.setText(nominativo.getDataNascita() == null ? " " : TextUtils.isEmpty(nominativo.getDataNascita()) ? " " : !Functions.validateReverseDate(nominativo.getDataNascita()) ? "" : Functions.getFormattedDate(nominativo.getDataNascita()));
        mLuogoNascitaView.setText(nominativo.getLuogoNascita());
        mPIVAView.setText(nominativo.getPIVA());
        mFaxView.setText(nominativo.getFax());
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
        mNoteDettView.setText(nominativo.getNoteDett() == null ? "" : nominativo.getNoteDett().equals("null") ? "" : nominativo.getNoteDett());
        if (nominativo.getSocieta() != null)
            mSocietaView.setText(nominativo.getSocieta().getNomeSocietà());
        mCellulareView.setText(nominativo.getCellulare());
        mTelefonoView.setText(nominativo.getTelefono());
        //Log.d("Tel: ",nominativo.getTelefono());
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

    /*private void showDeleteAlert() {
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
    }*/


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_MODIFICA) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

}
