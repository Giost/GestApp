package com.mcteam.gestapp.Moduli.Gestionale.Societa;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ModificaSocietaActivity extends AppCompatActivity {

    Spinner mTipologiaChooser;
    Spinner mProvinceChooser;
    EditText mNomeSocieta;
    EditText mCodiceSocieta;
    EditText mIndirizzo;
    EditText mCap;
    EditText mCitta;
    EditText mStato;
    EditText mTelefono;
    EditText mFax;
    EditText mPIva;
    EditText mCodiceFiscale;
    EditText mNote;
    EditText mSito;
    EditText mCellulare;
    String mTipologia;
    String mSelectedProvince;
    Boolean mTipologiaSelected = false;
    AlertDialog.Builder dialogBuilder;
    ArrayAdapter<CharSequence> mTipologiaAdapter;
    ArrayAdapter<CharSequence> mProvinceAdapter;
    Societa mCurrentSocieta;
    List<String> allProvince;

    private RequestQueue mRequestQueue;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_societa_modifica);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        Intent intent = getIntent();
        final Societa currentSocieta = intent.getParcelableExtra("societaToModify");
        mCurrentSocieta = currentSocieta;

        mTipologiaChooser = (Spinner) findViewById(R.id.modifica_societa_tipologia);
        mProvinceChooser = (Spinner) findViewById(R.id.modifica_societa_provincia);

        mTipologiaAdapter = ArrayAdapter.createFromResource(this,
                R.array.tipologia_cliente, android.R.layout.simple_spinner_item);

        mProvinceAdapter = ArrayAdapter.createFromResource(this,
                R.array.province, android.R.layout.simple_spinner_item);

        allProvince = Arrays.asList(getResources().getStringArray(R.array.province));


        mSelectedProvince = "";
        mTipologia = "";

        mTipologiaChooser.setAdapter(mTipologiaAdapter);

        dialogBuilder = new AlertDialog.Builder(this);

        mRequestQueue = Volley.newRequestQueue(this);

        mProvinceChooser.setAdapter(mProvinceAdapter);

        mTipologiaChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTipologia = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "nessun elemento scelto", Toast.LENGTH_LONG).show();
                mTipologia = "";
            }
        });

        mProvinceChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedProvince = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mTipologiaSelected = false;
            }
        });

        System.out.println(currentSocieta.toJson());

        Button modifica = (Button) findViewById(R.id.modifica_societa_ok);
        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptModifica();
            }
        });
        mNomeSocieta = (EditText) findViewById(R.id.modifica_societa_nome);
        mCodiceSocieta = (EditText) findViewById(R.id.modifica_societa_codice);
        mIndirizzo = (EditText) findViewById(R.id.modifica_societa_indirizzo);
        mCap = (EditText) findViewById(R.id.modifica_societa_cap);
        mCitta = (EditText) findViewById(R.id.modifica_societa_citta);
        mStato = (EditText) findViewById(R.id.modifica_societa_stato);
        mTelefono = (EditText) findViewById(R.id.modifica_societa_telefono);
        mCellulare = (EditText) findViewById(R.id.modifica_societa_cellulare);
        mFax = (EditText) findViewById(R.id.modifica_societa_fax);
        mPIva = (EditText) findViewById(R.id.modifica_societa_partita_iva);
        mCodiceFiscale = (EditText) findViewById(R.id.modifica_societa_codice_fiscale);
        mNote = (EditText) findViewById(R.id.modifica_societa_note);
        mSito = (EditText) findViewById(R.id.modifica_societa_sito);

        setupView();

    }

    private void attemptModifica() {
        boolean cancel = false;
        View focusView = null;

        mNomeSocieta.setError(null);
        mCodiceSocieta.setError(null);

        String nomeSocieta = mNomeSocieta.getText().toString();
        String codiceSocieta = mCodiceSocieta.getText().toString();
        String indirizzo = mIndirizzo.getText().toString();
        String CAP = mCap.getText().toString();
        String citta = mCitta.getText().toString();
        String stato = mStato.getText().toString();
        String telefono = mTelefono.getText().toString();
        String partitaIva = mPIva.getText().toString();
        String codiceFiscale = mCodiceFiscale.getText().toString();
        String note = mNote.getText().toString();
        String sito = mSito.getText().toString();
        String cellulare = mCellulare.getText().toString();
        String fax = mFax.getText().toString();

        if (!TextUtils.isEmpty(nomeSocieta)) {
            mCurrentSocieta.setNomeSocietà(nomeSocieta);
        } else {
            mNomeSocieta.setError("Campo neccessario");
            focusView = mNomeSocieta;
            cancel = true;
        }

        mCurrentSocieta.setmTipologia(mTipologia);

        if (!TextUtils.isEmpty(codiceSocieta)) {
            Integer codiceInteger = Integer.parseInt(codiceSocieta);
            mCurrentSocieta.setCodiceSocieta(codiceInteger);
        } else {
            mCodiceSocieta.setError("Campo neccessario");
            focusView = mCodiceSocieta;
            cancel = true;
        }

        mCurrentSocieta.setSito(sito);
        mCurrentSocieta.setCOD_FISCALE(codiceFiscale);
        mCurrentSocieta.setPartitaIva(partitaIva);
        mCurrentSocieta.setmCellulare(cellulare);
        mCurrentSocieta.setmTelefono(telefono);
        mCurrentSocieta.setStato(stato);
        mCurrentSocieta.setmCitta(citta);
        mCurrentSocieta.setIndirizzo(indirizzo);
        mCurrentSocieta.setmFax(fax);

        mCurrentSocieta.setmProvincia(mSelectedProvince);
        mCurrentSocieta.setNote(note);
        mCurrentSocieta.setCap(CAP);

        if (!cancel) {
            String json = mCurrentSocieta.toJson();
            sendRequest(json);
        } else {
            focusView.requestFocus();
        }
    }

    private void setupView() {

        String tipologia = mCurrentSocieta.getmTipologia();

        if (tipologia.equals("F")) {
            mTipologiaChooser.setSelection(1);
        } else if (tipologia.equals("C")) {
            mTipologiaChooser.setSelection(0);
        } else {
            mTipologiaChooser.setSelection(2);
        }

        mNomeSocieta.setText(mCurrentSocieta.getNomeSocietà());
        mCodiceSocieta.setText("" + mCurrentSocieta.getCodiceSocieta());
        mCellulare.setText(mCurrentSocieta.getmCellulare());
        mIndirizzo.setText(mCurrentSocieta.getIndirizzo());
        mTelefono.setText(mCurrentSocieta.getmTelefono());
        mCap.setText(mCurrentSocieta.getCap());
        mCitta.setText(mCurrentSocieta.getmCitta());

        int index = allProvince.indexOf(mCurrentSocieta.getmProvincia());

        mProvinceChooser.setSelection(index);

        mNote.setText(mCurrentSocieta.getNote());
        mCodiceFiscale.setText(mCurrentSocieta.getCOD_FISCALE());
        mPIva.setText(mCurrentSocieta.getPartitaIva());
        mStato.setText(mCurrentSocieta.getStato());
        mSito.setText(mCurrentSocieta.getSito());
        mFax.setText(mCurrentSocieta.getmFax() == null ? "" : mCurrentSocieta.getmFax().equals("null" + "") ? "" : mCurrentSocieta.getmFax());
    }

    private void sendRequest(String json) {
        String url = getString(R.string.mobile_url);
        url += "societa/" + mCurrentSocieta.getID();

        HashMap params = new HashMap();
        params.put("data", json);

        System.out.println(json);

        dialogBuilder.setTitle("Creazione nuova società");

        PUTRequest modifyRequestJson = new PUTRequest(Request.Method.PUT, url, params, new Response.Listener<JSONObject>() {
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

        mRequestQueue.add(modifyRequestJson);
    }

    public void showError(boolean error) {
        if (error) {
            dialogBuilder.setTitle("Errore modifica dati");
            dialogBuilder.setMessage("Si è verificato un errore nella modifica dei dati");
            dialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        } else {
            dialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent data = new Intent();
                    data.putExtra("modifiedSocieta", mCurrentSocieta);

                    if (getParent() == null) {
                        setResult(Activity.RESULT_OK, data);
                    } else {
                        getParent().setResult(Activity.RESULT_OK, data);
                    }
                    finish();
                    return;
                }
            });
            AlertDialog dialog = dialogBuilder.create();
            dialog.setTitle("Successo!");
            dialog.setMessage("Società modificata con successo");
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
