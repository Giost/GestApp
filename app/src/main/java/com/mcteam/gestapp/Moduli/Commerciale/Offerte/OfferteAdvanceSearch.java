package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import java.util.ArrayList;

public class OfferteAdvanceSearch extends AppCompatActivity {

    private ArrayList<Commessa> mCommArrayList;
    private ArrayList<Commessa> mSearchList;
    private RecyclerView mOffRecyclerView;
    private OfferteAdapter mAdapter;
    private EditText mCliente;
    private EditText mCodice;
    private EditText mNome;
    private TextView mResultStatus;
    private ImageButton cercaButton;
    private LinearLayout mInputs;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offerte_advance_search);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.commerciale_home_background);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        //get list from intent
        mCommArrayList = getIntent().getParcelableArrayListExtra("COMMESSE");

        mSearchList = new ArrayList<>(mCommArrayList);

        // inizializzazione view
        mAdapter = new OfferteAdapter(mSearchList, new OfferteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Commessa item) {
                Intent i = new Intent(getApplicationContext(), DettaglioOffertaActivity.class);
                i.putExtra("COMMESSA", item);
                startActivity(i);
            }
        });
        mOffRecyclerView = (RecyclerView) findViewById(R.id.offerte_ricerca_avanzata_recycler);
        mOffRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOffRecyclerView.setAdapter(mAdapter);

        mCliente = (EditText) findViewById(R.id.offerte_ricerca_avanzata_cliente);
        mCodice = (EditText) findViewById(R.id.offerte_ricerca_avanzata_codice);
        mNome = (EditText) findViewById(R.id.offerte_ricerca_avanzata_nome);

        mResultStatus = (TextView) findViewById(R.id.offerte_ricerca_avanzata_status);
        cercaButton = (ImageButton) findViewById(R.id.offerte_ricerca_avanzata_cerca_button);
        mInputs = (LinearLayout) findViewById(R.id.offerte_ricerca_avanzata_inputs);

        cercaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInputs.getVisibility() == View.VISIBLE) {
                    advanceSearch();
                    mInputs.setVisibility(View.GONE);
                } else if (mInputs.getVisibility() == View.GONE) {
                    mInputs.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Metodo per la ricerca degli elementi
     */
    private void advanceSearch()
    {
        ArrayList<Commessa> commesseSetResult = new ArrayList<>(mCommArrayList);

        String cliente = mCliente.getText().toString();
        String codice = mCodice.getText().toString();
        String nomeCommessa = mNome.getText().toString();

        String data[] = {cliente, codice, nomeCommessa};

        codice = codice.toUpperCase();
        nomeCommessa = nomeCommessa.toUpperCase();
        cliente = cliente.toUpperCase();

        if (TextUtils.isEmpty(codice) && TextUtils.isEmpty(nomeCommessa) && TextUtils.isEmpty(cliente)) {
            updateList(mCommArrayList);
            return;
        }

        ArrayList<Commessa> tempResult;

        if (!TextUtils.isEmpty(codice)) { // Ricerca per codice
            tempResult = new ArrayList<>();
            for (Commessa commessa : mCommArrayList) {
                if (commessa.getCodice_commessa().toUpperCase().contains(codice)) {
                    tempResult.add(commessa);
                }
            }

            commesseSetResult = (ArrayList) Functions.union(commesseSetResult, tempResult);

        }

        if (!TextUtils.isEmpty(nomeCommessa)) { // Ricerca per nome commessa
            tempResult = new ArrayList<>();
            for (Commessa commessa : mCommArrayList) {

                if (commessa.getNome_commessa() != null) {
                    if (commessa.getNome_commessa().toUpperCase().contains(nomeCommessa)) {
                        tempResult.add(commessa);
                    }
                }
            }
            commesseSetResult = (ArrayList) Functions.union(commesseSetResult, tempResult);
        }

        if (!TextUtils.isEmpty(cliente)) { // Ricerca per nome cliente
            tempResult = new ArrayList<>();
            String nomeSocieta = "";
            for (Commessa commessa : mCommArrayList) {

                if (commessa.getCliente() != null && !TextUtils.isEmpty(commessa.getCliente().getNomeSocietà())) {
                    nomeSocieta = commessa.getCliente().getNomeSocietà().toUpperCase();
                }

                if (nomeSocieta.contains(cliente)) {
                    tempResult.add(commessa);
                }
            }
            commesseSetResult = (ArrayList) Functions.union(commesseSetResult, tempResult);
        }
        updateList(commesseSetResult,data);
    }

    private void updateList(ArrayList<Commessa> list, String... query) {
        mSearchList.clear();
        mSearchList.addAll(list);
        String result = "";
        for (String data : query) {
            if (!TextUtils.isEmpty(data))
                result += "\"" + data + "\"";
        }

        mResultStatus.setText("Risultati per " + result + " : " + list.size());
        mAdapter.notifyDataSetChanged();
    }

}
