package com.mcteam.gestapp.Moduli.Gestionale.Societa;

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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mcteam.gestapp.Models.Rubrica.Societa;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.PUTRequest;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import org.json.JSONException;
import org.json.JSONObject;

public class EliminaSocietaActivity extends AppCompatActivity {

    TextView mNomeSocieta;
    TextView mTipologia;
    TextView mCodiceSocietaView;
    TextView mIndirizzo;
    TextView mTelefono;
    TextView mCap;
    TextView mProvincia;
    TextView mNote;
    TextView mCOD_FISCALE;
    TextView mPartita_iva;
    TextView mStato;
    TextView mFax;
    TextView mSito;
    TextView mCitta;
    TextView mCellulare;

    AlertDialog.Builder deleteDialogBuilder;
    RequestQueue mRequestQueue;
    Societa mCurrentSocieta;

    AlertDialog mDeleteDialog;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elimina_societa);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        mNomeSocieta = (TextView) findViewById(R.id.elimina_societa_nome);
        mTipologia = (TextView) findViewById(R.id.elimina_societa_tipologia);
        mCodiceSocietaView = (TextView) findViewById(R.id.elimina_societa_codice_societa);
        mIndirizzo = (TextView) findViewById(R.id.elimina_societa_indirizzo);
        mCap = (TextView) findViewById(R.id.elimina_societa_cap);
        mProvincia = (TextView) findViewById(R.id.elimina_societa_provincia);
        mNote = (TextView) findViewById(R.id.elimina_societa_note);
        mCellulare = (TextView) findViewById(R.id.elimina_societa_cellulare);
        mTelefono = (TextView) findViewById(R.id.elimina_societa_telefono);
        mCOD_FISCALE = (TextView) findViewById(R.id.elimina_societa_codice_fiscale);
        mPartita_iva = (TextView) findViewById(R.id.elimina_societa_partita_iva);
        mStato = (TextView) findViewById(R.id.elimina_societa_stato);
        mSito = (TextView) findViewById(R.id.elimina_societa_sito);
        mCitta = (TextView) findViewById(R.id.elimina_societa_citta);
        mFax = (TextView) findViewById(R.id.elimina_societa_fax);

        Button modificaSocieta = (Button) findViewById(R.id.elimina_societa_procedi);

        modificaSocieta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAlert();
            }
        });

        Intent intent = getIntent();
        final Societa currentSocieta = intent.getParcelableExtra("societaToDelete");
        mCurrentSocieta = currentSocieta;

        deleteDialogBuilder = new AlertDialog.Builder(this);


        mRequestQueue = Volley.newRequestQueue(this);


        setupView(currentSocieta);

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


    private void attemptDelete() {
        String url = getString(R.string.mobile_url);
        url += "societa/" + mCurrentSocieta.getID();

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
