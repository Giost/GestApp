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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.util.HashMap;

public class NuovaSocietaActivity extends AppCompatActivity {

    Spinner mTipologiaChooser;
    Spinner mProvinceChooser;
    EditText mNomeSocieta;
    EditText mCodiceSocieta;
    EditText mIndirizzo;
    EditText mCap;
    EditText mCitta;
    EditText mStato;
    EditText mTelefono;
    EditText mPIva;
    EditText mCodiceFiscale;
    EditText mNote;
    EditText mSito;
    EditText mCellulare;
    String mTipologia;
    String mSelectedProvince;
    TextView mTiplogiaView;
    Boolean mTipologiaSelected = false;
    AlertDialog.Builder dialogBuilder;


    private RequestQueue mRequestQueue;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_societa);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        mTipologiaChooser = (Spinner) findViewById(R.id.choose_tipologia_societa);
        mProvinceChooser = (Spinner) findViewById(R.id.chooser_province);

        ArrayAdapter<CharSequence> tipologiaAdapter = ArrayAdapter.createFromResource(this,
                R.array.tipologia_cliente, android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> provinceAdapter = ArrayAdapter.createFromResource(this,
                R.array.province, android.R.layout.simple_spinner_item);

        mSelectedProvince = "";
        mTipologia = "";

        mTipologiaChooser.setAdapter(tipologiaAdapter);

        dialogBuilder = new AlertDialog.Builder(this);

        mRequestQueue = Volley.newRequestQueue(this);

        mProvinceChooser.setAdapter(provinceAdapter);

        mTipologiaChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTipologia = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "nessun elemento scelto", Toast.LENGTH_LONG).show();
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

        mNomeSocieta = (EditText) findViewById(R.id.nuova_societa_nome_societa);
        mCodiceSocieta = (EditText) findViewById(R.id.nuova_societa_codice_societa);
        mIndirizzo = (EditText) findViewById(R.id.nuova_societa_indirizzo_societa);
        mCap = (EditText) findViewById(R.id.nuova_societa_cap);
        mCitta = (EditText) findViewById(R.id.nuova_societa_citta);
        mStato = (EditText) findViewById(R.id.nuova_societa_stato);
        mTelefono = (EditText) findViewById(R.id.nuova_societa_telefono);
        mCellulare = (EditText) findViewById(R.id.nuova_societa_cellulare);
        mPIva = (EditText) findViewById(R.id.nuova_societa_p_iva);
        mCodiceFiscale = (EditText) findViewById(R.id.nuova_societa_codice_fiscale);
        mNote = (EditText) findViewById(R.id.nuova_societa_note);
        mSito = (EditText) findViewById(R.id.nuova_societa_sito_web);
        mTiplogiaView = (TextView) findViewById(R.id.nuova_societa_tipologia);

        Button creaButton = (Button) findViewById(R.id.nuova_societa_crea_nuovo);
        Button annullaButton = (Button) findViewById(R.id.nuova_societa_annulla);
        annullaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        creaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptCreation();
            }
        });
    }

    private void attemptCreation() {

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

        Societa nuovaSocieta = new Societa();

        if (!TextUtils.isEmpty(nomeSocieta)) {
            nuovaSocieta.setNomeSocietà(nomeSocieta);
        } else {
            mNomeSocieta.setError("Campo neccessario");
            focusView = mNomeSocieta;
            cancel = true;
        }

        if (!TextUtils.isEmpty(mTipologia)) {
            nuovaSocieta.setmTipologia(mTipologia);
        } else {
            mTiplogiaView.setText("Scegliere una tipologia");
            focusView = mTiplogiaView;
            cancel = true;
        }

        Toast.makeText(this, codiceSocieta, Toast.LENGTH_LONG).show();

        if (!TextUtils.isEmpty(codiceSocieta)) {
            Integer codiceInteger = Integer.parseInt(codiceSocieta);
            nuovaSocieta.setCodiceSocieta(codiceInteger);
        } else {
            mCodiceSocieta.setError("Campo neccessario");
            focusView = mCodiceSocieta;
            cancel = true;
        }

        /*
        if(!TextUtils.isEmpty(indirizzo)){
            nuovaSocieta.setIndirizzo(indirizzo);
        }else{
            mIndirizzo.setError("Campo neccessario");
            focusView = mIndirizzo;
            cancel = true;
        }

        if(!TextUtils.isEmpty(citta)){
            nuovaSocieta.setmCitta(citta);
        }else{
            mCitta.setError("Campo neccessario");
            focusView = mCitta;
            cancel = true;
        }

        if(!TextUtils.isEmpty(stato)){
            nuovaSocieta.setStato(stato);
        }else{
            mStato.setError("Campo neccessario");
            focusView = mStato;
            cancel = true;
        }

        if(!TextUtils.isEmpty(telefono)){
            nuovaSocieta.setmTelefono(telefono);
        }else{
            mTelefono.setError("Campo neccessario");
            focusView = mTelefono;
            cancel = true;
        }

        if(!TextUtils.isEmpty(cellulare)){
            nuovaSocieta.setmCellulare(cellulare);
        }else{
            mCellulare.setError("Campo neccessario");
            focusView = mCellulare;
            cancel = true;
        }

        if(!TextUtils.isEmpty(partitaIva)){
            nuovaSocieta.setPartitaIva(partitaIva);
        }else{
            mPIva.setError("Campo neccessario");
            focusView = mPIva;
            cancel = true;
        }

        if(!TextUtils.isEmpty(codiceFiscale)){
            nuovaSocieta.setCOD_FISCALE(codiceFiscale);
        }else{
            mCodiceFiscale.setError("Campo neccessario");
            focusView = mCodiceFiscale;
            cancel = true;
        }

        if(!TextUtils.isEmpty(sito)){
            nuovaSocieta.setSito(sito);
        }else{
            mSito.setError("Campo neccessario");
            focusView = mSito;
            cancel = true;
        }*/

        nuovaSocieta.setSito(sito);
        nuovaSocieta.setCOD_FISCALE(codiceFiscale);
        nuovaSocieta.setPartitaIva(partitaIva);
        nuovaSocieta.setmCellulare(cellulare);
        nuovaSocieta.setmTelefono(telefono);
        nuovaSocieta.setStato(stato);
        nuovaSocieta.setmCitta(citta);
        nuovaSocieta.setIndirizzo(indirizzo);

        nuovaSocieta.setmProvincia(mSelectedProvince);
        nuovaSocieta.setNote(note);
        nuovaSocieta.setCap(CAP);

        if (!cancel) {
            String json = nuovaSocieta.toJson();
            sendRequest(json);
        } else {
            focusView.requestFocus();
        }

    }


    private void sendRequest(String json) {
        String url = getString(R.string.mobile_url);
        url += "societa-nuovo";

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
                    finish();
                    return;
                }
            });
            AlertDialog dialog = dialogBuilder.create();
            dialog.setTitle("Successo!");
            dialog.setMessage("utente creato con successo");
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
