package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.mcteam.gestapp.Fragments.DatePickerFragment;
import com.mcteam.gestapp.Models.Commerciale.Offerta;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Models.Rubrica.Nominativo;
import com.mcteam.gestapp.Moduli.Gestionale.Commesse.NominativoSpinnerAdapter;
import com.mcteam.gestapp.NetworkReq.VolleyRequests;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import java.util.ArrayList;

public class ModificaOffertaActivity extends AppCompatActivity {

    EditText mOffertaCodComm;
    EditText mOffertaClient;
    Spinner mOffertaRef1;
    Spinner mOffertaRef2;
    Spinner mOffertaRef3;
    EditText mOffertaDataOff;
    EditText mOffertaObj;
    CheckBox mOffertaPresent;
    RadioGroup rdgp;
    LinearLayout allegato;
    Offerta offerta;
    Commessa commessa = null;
    VolleyRequests mMyRequests;
    DatePickerFragment mDateFragment;
    ArrayList<Nominativo> mNominativiList;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_offerta);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //***********************************************************************
        //Cambiare colore alla actionBar
        //************************************************************************
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.commerciale_home_background);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        offerta = getIntent().getParcelableExtra("OFFERTA");
        commessa = getIntent().getParcelableExtra("COMMESSA");

        mMyRequests = new VolleyRequests(this, this);

        allegato = (LinearLayout) findViewById(R.id.modifica_offerta_layout_allegato);
        mOffertaCodComm = (EditText) findViewById(R.id.modifica_offerta_codcom);
        mOffertaClient = (EditText) findViewById(R.id.modifica_offerta_cliente);
        mOffertaRef1 = (Spinner) findViewById(R.id.modifica_offerta_ref1);
        mOffertaRef2 = (Spinner) findViewById(R.id.modifica_offerta_ref2);
        mOffertaRef3 = (Spinner) findViewById(R.id.modifica_offerta_ref3);
        mOffertaDataOff = (EditText) findViewById(R.id.modifica_offerta_dataoff);
        mOffertaObj = (EditText) findViewById(R.id.modifica_offerta_oggetto);
        mOffertaPresent = (CheckBox) findViewById(R.id.modifica_offerta_present);
        //mOffertaAlleg = (ImageView) findViewById(R.id.modifica_offerta_allegato);

        Button annulla = (Button) findViewById(R.id.modifica_offerta_annulla_btn);

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button elimina = (Button) findViewById(R.id.modifica_offerta_conferma_btn);

        elimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //inserire il metotodo per la modifica
            }
        });

        mOffertaCodComm.setText(commessa.getCodice_commessa());
        mOffertaCodComm.setEnabled(false);

        mOffertaClient.setText(commessa.getCliente().getNomeSocietà());
        mOffertaClient.setEnabled(false);

        //Lista nominativi
        mNominativiList = new ArrayList<>();
        NominativoSpinnerAdapter adapter = new NominativoSpinnerAdapter(this, R.layout.nominativo_societa_spinner_row, mNominativiList);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setupNominativoSpinner(commessa);
            }

        });

        //Riferenti 1, 2 e 3
        mOffertaRef1.setAdapter(adapter);
        mOffertaRef2.setAdapter(adapter);
        mOffertaRef3.setAdapter(adapter);

        mMyRequests.getNominativiList(mNominativiList, adapter);

        //mOffertaRef2.setText(commessa.getReferente1().getNome() + commessa.getReferente1().getCognome());
        //mOffertaRef3.setText(commessa.getReferente2().getNome() + commessa.getReferente2().getCognome());

        //mOffertaDataOff.setText(offerta.getDataOfferta());
        mDateFragment = new DatePickerFragment(mOffertaDataOff);
        mOffertaDataOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateFragment.show(getFragmentManager(), "datePicker");
            }
        });
        mOffertaDataOff.setText(Functions.getFormattedDate(commessa.getData()));

        mOffertaObj.setText(commessa.getNome_commessa());
        mOffertaObj.setEnabled(false);

        mOffertaPresent.setChecked(offerta.getAccettata() == 1);
        //mOffertaAlleg.setImageBitmap(AllegatiUtils.getAllegatoLogo(getResources(), offerta.getAllegato()));

        rdgp = (RadioGroup) findViewById(R.id.modifica_offerta_group_allegato);
        rdgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i)
            {
                if (i==R.id.modifica_offerta_si)
                {
                    allegato.setVisibility(View.VISIBLE);
                }
                else
                {
                    allegato.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setupNominativoSpinner(Commessa commessa) {
        int indexReferenteOfferta1;
        int indexReferenteOfferta2;
        int indexReferenteOfferta3;

        if (commessa.getReferente_offerta1() != null) {
            indexReferenteOfferta1 = mNominativiList.indexOf(commessa.getReferente_offerta1());
            mOffertaRef1.setSelection(indexReferenteOfferta1);
        }

        if (commessa.getReferente_offerta2() != null) {
            indexReferenteOfferta2 = mNominativiList.indexOf(commessa.getReferente_offerta2());
            mOffertaRef2.setSelection(indexReferenteOfferta2);
        }

        if (commessa.getReferente_offerta3() != null) {
            indexReferenteOfferta3 = mNominativiList.indexOf(commessa.getReferente_offerta3());
            mOffertaRef3.setSelection(indexReferenteOfferta3);
        }
    }
}
