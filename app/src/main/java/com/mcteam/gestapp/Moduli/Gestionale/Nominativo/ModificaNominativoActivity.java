package com.mcteam.gestapp.Moduli.Gestionale.Nominativo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mcteam.gestapp.Application.MyApp;
import com.mcteam.gestapp.Fragments.DatePickerFragment;
import com.mcteam.gestapp.Models.Rubrica.Nominativo;
import com.mcteam.gestapp.Models.Rubrica.Societa;
import com.mcteam.gestapp.Models.UserInfo;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.CustomRequest;
import com.mcteam.gestapp.NetworkReq.PUTRequest;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ModificaNominativoActivity extends AppCompatActivity {

    Spinner mTitoloView;
    EditText mCognomeView;
    EditText mNomeView;
    EditText mIndirizzoView;
    EditText mCapView;
    Spinner mProvinciaView;
    EditText mCittaView;
    EditText mDataNascitaView;
    EditText mLuogoNascitaView;
    EditText mPIVAView;
    EditText mCod_FiscaleView;
    EditText mCartaIDView;
    EditText mPatenteView;
    EditText mTesseraSanitariaView;
    EditText mSitoWebView;
    EditText mNomeBancaView;
    EditText mIndirizzoBancaView;
    EditText mIBANView;
    EditText mNazionalitaView;
    EditText mEmailView;
    EditText mNoteView;
    EditText mNoteDettView;
    EditText mTelefono;
    EditText mFaxView;
    EditText mSocietaView;
    EditText mCellulareView;
    ArrayAdapter<String> mTitoloAdapter;
    ArrayAdapter<String> mProvinceAdapter;
    SocietaSpinnerAdapter mSocietaAdapter;
    LinearLayout mDettagliView;
    String selectedTitle = null;
    String selectedProvince = null;

    List<String> allProvince;
    List<String> mTitoli;

    Gson gson;

    Spinner mSocietaSpinner;

    ArrayList<Societa> mAllSocieta;

    AlertDialog.Builder dialogBuilder;

    RequestQueue mRequestQueue;

    Societa mSelectedSocieta = null;
    UserInfo mActualUser = null;

    Nominativo mNominativoAttuale = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_nominativo);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mAllSocieta = new ArrayList<>();

        //Set liste della provincia e dei titoli prelevando dalle risorse
        allProvince = Arrays.asList(getResources().getStringArray(R.array.province));
        mTitoli = Arrays.asList(getResources().getStringArray(R.array.titoli));

        mNominativoAttuale = getIntent().getParcelableExtra("nominativoToModify");
        mActualUser = getIntent().getParcelableExtra("actualUser");

        mTitoloView = (Spinner) findViewById(R.id.modifica_nominativo_titolo_spinner);
        mSocietaSpinner = (Spinner) findViewById(R.id.modifica_nominativo_societa);
        mProvinciaView = (Spinner) findViewById(R.id.modifica_nominativo_provincia_spinner);

        mTitoloView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTitle = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mProvinciaView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProvince = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gson = new Gson();

        /* Set spinners adapter */
        mTitoloAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mTitoli);
        mTitoloView.setAdapter(mTitoloAdapter);

        mProvinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allProvince);
        mProvinciaView.setAdapter(mProvinceAdapter);

        mSocietaAdapter = new SocietaSpinnerAdapter(this, R.layout.nominativo_societa_spinner_row, mAllSocieta);
        mSocietaSpinner.setAdapter(mSocietaAdapter);

        mDettagliView = (LinearLayout) findViewById(R.id.modifica_nominativo_dettagli);

        Button btnShowDettagli = (Button) findViewById(R.id.modifica_nominativo_modifica_dettagli);

        UserInfo mCurrentUser = ((MyApp) getApplication()).getCurrentUser();

        if (mCurrentUser != null && !mCurrentUser.isAmministratore()) {
            btnShowDettagli.setVisibility(View.GONE);
        }

        btnShowDettagli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDettagliView.getVisibility() == View.VISIBLE) {
                    mDettagliView.setVisibility(View.GONE);
                } else {
                    mDettagliView.setVisibility(View.VISIBLE);
                }
            }
        });


        mCognomeView = (EditText) findViewById(R.id.modifica_nominativo_cognome);
        mNomeView = (EditText) findViewById(R.id.modifica_nominativo_nome);
        mIndirizzoView = (EditText) findViewById(R.id.modifica_nominativo_indirizzo);
        mCapView = (EditText) findViewById(R.id.modifica_nominativo_cap);
        mCittaView = (EditText) findViewById(R.id.modifica_nominativo_citta);
        mDataNascitaView = (EditText) findViewById(R.id.modifica_nominativo_data_nascita);
        mLuogoNascitaView = (EditText) findViewById(R.id.modifica_nominativo_luogo_nascita);
        mPIVAView = (EditText) findViewById(R.id.modifica_nominativo_partita_iva);
        mCod_FiscaleView = (EditText) findViewById(R.id.modifica_nominativo_codice_fiscale);
        mCartaIDView = (EditText) findViewById(R.id.modifica_nominativo_carta_identita);
        mPatenteView = (EditText) findViewById(R.id.modifica_nominativo_patente);
        mTesseraSanitariaView = (EditText) findViewById(R.id.modifica_nominativo_tessera_sanitaria);
        mSitoWebView = (EditText) findViewById(R.id.modifica_nominativo_sito_web);
        mNomeBancaView = (EditText) findViewById(R.id.modifica_nominativo_nome_banca);
        mIndirizzoBancaView = (EditText) findViewById(R.id.modifica_nominativo_indirizzo_banca);
        mIBANView = (EditText) findViewById(R.id.modifica_nominativo_iban);
        mNazionalitaView = (EditText) findViewById(R.id.modifica_nominativo_nazionalita);
        mEmailView = (EditText) findViewById(R.id.modifica_nominativo_email);
        mFaxView = (EditText) findViewById(R.id.modifica_nominativo_fax);
        mNoteView = (EditText) findViewById(R.id.modifica_nominativo_note);
        mNoteDettView = (EditText) findViewById(R.id.modifica_nominativo_note_dettaglio);
        mCellulareView = (EditText) findViewById(R.id.modifica_nominativo_cellulare);
        mTelefono = (EditText) findViewById(R.id.modifica_nominativo_telefono);


        mRequestQueue = Volley.newRequestQueue(this);

        mSocietaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    mSelectedSocieta = (Societa) parent.getItemAtPosition(position);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDataNascitaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSelData();
            }
        });


        Button modifica = (Button) findViewById(R.id.modifica_nominativo_conferma_btn);

        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptModifica();
            }
        });

        Button annulla = (Button) findViewById(R.id.modifica_nominativo_annulla_btn);

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialogBuilder = new AlertDialog.Builder(this);

        getRubricaList();

        setupView(mNominativoAttuale);

    }

    private void attemptModifica() {
        boolean cancel = false;
        View focusView = null;

        Nominativo nominativo = new Nominativo();

        int ID;
        int IDSocieta;
        String Cognome = mCognomeView.getText().toString();
        String Nome = mNomeView.getText().toString();
        String Indirizzo = mIndirizzoView.getText().toString();
        String Cap = mCapView.getText().toString();
        String Citta = mCittaView.getText().toString();
        String DataNascita = mDataNascitaView.getText().toString();
        String LuogoNascita = mLuogoNascitaView.getText().toString();
        String PIVA = mPIVAView.getText().toString();
        String Fax = mFaxView.getText().toString();
        String Cod_Fiscale = mCod_FiscaleView.getText().toString();
        String CartaID = mCartaIDView.getText().toString();
        String Patente = mPatenteView.getText().toString();
        String TesseraSanitaria = mTesseraSanitariaView.getText().toString();
        String SitoWeb = mSitoWebView.getText().toString();
        String NomeBanca = mNomeBancaView.getText().toString();
        String IndirizzoBanca = mIndirizzoBancaView.getText().toString();
        String IBAN = mIBANView.getText().toString();
        String Nazionalita = mNazionalitaView.getText().toString();
        String Email = mEmailView.getText().toString();
        String Note = mNoteView.getText().toString();
        String NoteDett = mNoteDettView.getText().toString();
        String Cellulare = mCellulareView.getText().toString();
        String telefono = mTelefono.getText().toString();

        mCognomeView.setError(null);

        if (!TextUtils.isEmpty(Cognome)) {
            nominativo.setCognome(Cognome);
        } else {
            mCognomeView.setError("Campo neccessario");
            focusView = mCognomeView;
            cancel = true;
        }

        nominativo.setNome(Nome);
        nominativo.setIndirizzo(Indirizzo);
        nominativo.setCap(Cap);
        nominativo.setCitta(Citta);

        if (!TextUtils.isEmpty(DataNascita) && !Functions.validateDate(DataNascita)) {
            mDataNascitaView.setError("Data mancante o errata: rispettare il formato 01-01-1900");
            focusView = mDataNascitaView;
            cancel = true;
        } else {
            try {
                nominativo.setDataNascita(Functions.fromDateToSql(DataNascita));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        nominativo.setLuogoNascita(LuogoNascita);
        nominativo.setPIVA(PIVA);
        nominativo.setFax(Fax);
        nominativo.setCod_Fiscale(Cod_Fiscale);
        nominativo.setCartaID(CartaID);
        nominativo.setPatente(Patente);
        nominativo.setTesseraSanitaria(TesseraSanitaria);
        nominativo.setSitoWeb(SitoWeb);
        nominativo.setNomeBanca(NomeBanca);
        nominativo.setIndirizzoBanca(IndirizzoBanca);
        nominativo.setIBAN(IBAN);
        nominativo.setNazionalita(Nazionalita);
        nominativo.setEmail(Email);
        nominativo.setNote(Note);
        nominativo.setNoteDett(NoteDett);
        nominativo.setCellulare(Cellulare);
        nominativo.setTelefono(telefono);
        nominativo.setProvincia(selectedProvince);
        nominativo.setTitolo(selectedTitle);
        if (mSelectedSocieta == null)
            cancel = true;
        else
            nominativo.setIDSocieta(mSelectedSocieta.getID());

        if (!cancel) {
            String json = gson.toJson(nominativo);
            System.out.println(json);
            sendRequest(json);
        } else {
            focusView.requestFocus();
        }
    }

    private void setupView(Nominativo nominativo) {

        int provinceIndex = allProvince.indexOf(nominativo.getProvincia());

        int titoloIndex = mTitoli.indexOf(nominativo.getTitolo());

        mTitoloView.setSelection(titoloIndex);

        mProvinciaView.setSelection(provinceIndex);

        mCognomeView.setText(nominativo.getCognome());
        mNomeView.setText(nominativo.getNome());
        mIndirizzoView.setText(nominativo.getIndirizzo());
        mCapView.setText(nominativo.getCap());
        mCittaView.setText(nominativo.getCitta());
        mDataNascitaView.setText(nominativo.getDataNascita() == null ? " " : TextUtils.isEmpty(nominativo.getDataNascita()) ? " " : !Functions.validateReverseDate(nominativo.getDataNascita()) ? "" : Functions.getFormattedDate(nominativo.getDataNascita()));
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
        mCellulareView.setText(nominativo.getCellulare());
        mTelefono.setText(nominativo.getTelefono());
        mFaxView.setText(nominativo.getFax());
    }


    private void sendRequest(String json) {
        String url = getString(R.string.mobile_url);
        url += "nominativo/" + mNominativoAttuale.getID();

        HashMap params = new HashMap();
        params.put("data", json);

        System.out.println(json);

        dialogBuilder.setTitle("Modifica nominativo");

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
                    data.putExtra("modifiedSocieta", mNominativoAttuale);

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
            dialog.setMessage("Utente modificato con successo");
            dialog.show();
        }
    }

    /**
     * Metodo che fa il SELECT di tutti i record della tabella "rubrica societa", perchè devono
     * essere mostrate nel menù a tendina
     */
    public void getRubricaList() {
        String url = getString(R.string.mobile_url);
        url += "rubrica-societa";

        CustomRequest accessiRequest = new CustomRequest(url, null, new RubricaResponse(), new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Errore richiesta Volley", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        });

        mRequestQueue.add(accessiRequest);
    }

    public class RubricaResponse implements Response.Listener<JSONArray> {

        Gson gson = new Gson();

        @Override
        public void onResponse(JSONArray responseArray) {
            try {
                Log.i("SistemiActivity.class", " " + responseArray.length());
                // Parsing json object response
                // response will be a json object
                ArrayList<Societa> societas = new ArrayList<>();
                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject response = responseArray.getJSONObject(i);
                    Societa societa = gson.fromJson(response.toString(), Societa.class);
                    societas.add(societa);
                }
                //Aggiorna spinner delle società
                mAllSocieta.addAll(societas);
                mSocietaAdapter.notifyDataSetChanged();
                updateSocietaView();
            } catch (JSONException e) {

                e.printStackTrace();
                Log.i("LoginResponse", e.getMessage());

                Toast.makeText(getApplicationContext(),
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    //Set società nello spinner
    private void updateSocietaView() {

        int indexSocieta = mAllSocieta.indexOf(mNominativoAttuale.getSocieta());
        mSocietaSpinner.setSelection(indexSocieta);
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
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickSelData() {
        DialogFragment dialogFragment = new DatePickerFragment(mDataNascitaView);
        dialogFragment.show(getFragmentManager(), "datePicker");
    }

    private void logout() {
        Intent goLogin = new Intent(this, LoginActivity.class);
        goLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goLogin);
        finish();
    }

    private void goHome() {
        Intent goHome = new Intent(this, HomeActivity.class);
        goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goHome);
        finish();
    }


}
