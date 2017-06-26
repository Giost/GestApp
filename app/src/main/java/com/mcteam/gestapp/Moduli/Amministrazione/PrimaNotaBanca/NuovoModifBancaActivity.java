package com.mcteam.gestapp.Moduli.Amministrazione.PrimaNotaBanca;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.ParseException;

import com.mcteam.gestapp.Utils.Constants;
import com.mcteam.gestapp.Fragments.DatePickerFragment;
import com.mcteam.gestapp.Models.PrimaNota.NotaBanca;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;
import com.mcteam.gestapp.NetworkReq.VolleyRequests;

/**
 * @author Created by Riccardo Rossi on 17/05/2016.
 * Prova
 */

public class NuovoModifBancaActivity extends AppCompatActivity {

    private Spinner mGruppo;
    private TextView mDataOperazione;
    private TextView mDataValuta;
    private EditText mDescrizioneMovimenti;
    private EditText mProtocollo;
    private EditText mDare;
    private EditText mAvere;
    private Button mButtonCrea;
    private Button mButtonModifica;
    private VolleyRequests mVolleyRequest;
    private NotaBanca notaBancaEdit;
    private Gson gson = new Gson();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_edit_nota_banca);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //Set colore action bar
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_amministrazione);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
            getSupportActionBar().setIcon(R.drawable.ic_accessibility_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mGruppo = (Spinner) findViewById(R.id.spinner_gruppo_banca);
        mDataOperazione = (TextView) findViewById(R.id.banca_dataop);
        mDataValuta = (TextView) findViewById(R.id.banca_dataval);
        mDescrizioneMovimenti = (EditText) findViewById(R.id.banca_descr);
        mProtocollo = (EditText) findViewById(R.id.banca_prot);
        mDare = (EditText) findViewById(R.id.banca_dare);
        mAvere = (EditText) findViewById(R.id.banca_avere);
        mButtonCrea = (Button) findViewById(R.id.banca_b_crea);
        mButtonModifica = (Button) findViewById(R.id.banca_b_modifica);
        mVolleyRequest = new VolleyRequests(this, this);

        notaBancaEdit = getIntent().getParcelableExtra(Constants.NOTA_BANCA);
        System.out.println(notaBancaEdit);
        if (notaBancaEdit != null) {
            DecimalFormat df = new DecimalFormat();
            df.setMinimumFractionDigits(2);

            //Non mostra la tastiera in apertura
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            setTitle("Modifica nota banca");
            mGruppo.setSelection(notaBancaEdit.getGruppo());
            mDataOperazione.setText(notaBancaEdit.getDataPagamento());
            mDataValuta.setText(notaBancaEdit.getDataPagamento());
            mDescrizioneMovimenti.setText(notaBancaEdit.getDescrizione());
            if (notaBancaEdit.getNumeroProtocollo() != 0)
                mProtocollo.setText(notaBancaEdit.getNumeroProtocollo() + "");
            mDare.setText(notaBancaEdit.getDare() + "");
            mAvere.setText(notaBancaEdit.getAvere() + "");
            mButtonModifica.setVisibility(View.VISIBLE);
        } else {
            mButtonCrea.setVisibility(View.VISIBLE);
            //Set data del mese selezionato in caso di nota nuova
            DecimalFormat mFormat = new DecimalFormat("00");

            int month = getIntent().getIntExtra("MONTH", 1);
            String year = getIntent().getStringExtra("YEAR");
            mDataOperazione.setText("01-" + mFormat.format(month + 1) + "-" + year);
            mDataValuta.setText("01-" + mFormat.format(month + 1) + "-" + year);
        }

        mDataOperazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSelData(mDataOperazione);
            }
        });

        mDataValuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSelData(mDataValuta);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    public void onClickModifica(View view) {
        if (checkFields())
            try {
                attemptEdit();
            } catch (ParseException e) {
                e.printStackTrace();
            }
    }

    public void onClickSelData(TextView view) {
        DatePickerFragment dpf = new DatePickerFragment(view);
        dpf.show(getFragmentManager(), "datePicker");
    }

    private void attemptEdit() throws ParseException {
        NotaBanca notaBanca = notaBancaToEncode();
        int ID = notaBancaEdit.getID();
        mVolleyRequest.addNewElementRequest(gson.toJson(notaBanca), "nota-banca-edit/" + ID);
    }

    public void onClickCrea(View view) {
        if (checkFields())
            try {
                attemptCreate();
            } catch (ParseException e) {
                e.printStackTrace();
            }
    }

    private void attemptCreate() throws ParseException {
        NotaBanca notaBanca = notaBancaToEncode();
        mVolleyRequest.addNewElementRequest(gson.toJson(notaBanca), "nota-banca-nuovo");
    }

    public void onClickAnnulla(View view) {
        finish();
    }

    private boolean checkFields() {

        String dataOperazione = mDataOperazione.getText().toString();
        String dataValuta = mDataValuta.getText().toString();
        String descrizione = mDescrizioneMovimenti.getText().toString();
        String dare = mDare.getText().toString();
        String avere = mAvere.getText().toString();

        if (TextUtils.isEmpty(dataOperazione) || !Functions.validateDate(dataOperazione)) {
            mDataOperazione.setError("Data mancante o errata: rispettare il formato gg-mm-aaaa");
            mDataOperazione.requestFocus();
            return false;
        } else {
            try {
                Functions.fromDateToSql(dataOperazione);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(dataValuta) || !Functions.validateDate(dataOperazione) || !Functions.strDateGreaterThan(dataOperazione, dataValuta)) {
            mDataValuta.setError("Data errata: rispettare il formato gg-mm-aaaa e DATA VALUTA maggiore di DATA OPERAZIONE");
            mDataValuta.requestFocus();
            return false;
        } else {
            try {
                Functions.fromDateToSql(dataValuta);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(descrizione)) {
            mDescrizioneMovimenti.setError("Inserire una descrizione");
            mDescrizioneMovimenti.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(dare) && TextUtils.isEmpty(avere)) {
            mDare.setError("Inserire almeno un importo tra dare e avere");
            mDare.requestFocus();
            return false;
        }

        return true;
    }

    private NotaBanca notaBancaToEncode() throws ParseException {

        NotaBanca notaBanca = new NotaBanca();

        int gruppo = mGruppo.getSelectedItemPosition();
        String dataOperazione = mDataOperazione.getText().toString();
        String dataValuta = mDataValuta.getText().toString();
        String descrizione = mDescrizioneMovimenti.getText().toString();
        Integer protocollo;
        try {
            protocollo = Integer.parseInt(mProtocollo.getText().toString()); //protocollo puo essere lasciato vuoto
        } catch (NumberFormatException ex) {
            protocollo = 0; //Se vuoto viene settato a 0 per convenzione
        }

        float dare;
        try {
            dare = Float.parseFloat((mDare.getText().toString()));
        } catch (NumberFormatException exception) {
            dare = 0;
        }

        float avere;
        try {
            avere = Float.parseFloat((mAvere.getText().toString()));
        } catch (NumberFormatException exception) {
            avere = 0;
        }

        notaBanca.setGruppo(gruppo);
        notaBanca.setDataPagamento(Functions.fromDateToSql(dataOperazione));
        notaBanca.setDataValuta(Functions.fromDateToSql(dataValuta));
        notaBanca.setDescrizione(descrizione);
        notaBanca.setNumeroProtocollo(protocollo);
        notaBanca.setDareDb(Functions.format(dare));
        notaBanca.setAvereDb(Functions.format(avere));

        return notaBanca;
    }
}
