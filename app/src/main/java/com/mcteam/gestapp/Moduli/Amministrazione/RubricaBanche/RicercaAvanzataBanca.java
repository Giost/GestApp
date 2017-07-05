package com.mcteam.gestapp.Moduli.Amministrazione.RubricaBanche;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.mcteam.gestapp.Models.Rubrica.Banca;
import com.mcteam.gestapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RicercaAvanzataBanca extends AppCompatActivity {

    TextView mNominativoView;
    TextView mIndirizzoView;
    TextView mIbanView;
    TextView mStatus;
    ListView mBancheListView;
    ImageButton mSearchButton;
    ArrayList<Banca> mBancheList;
    ArrayList<Banca> mOriginalList;
    RubricaBancaListAdapter mBancheListAdapter;
    Comparator<Banca> bancaComparator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_avanzata_banca);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //***********************************************************************
        //Cambiare colore alla actionBar
        //************************************************************************
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                actionBarBack = getDrawable(R.drawable.actionbar_amministrazione);
            }
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        mNominativoView = (TextView) findViewById(R.id.ricerca_avanzata_banca_nome);
        mIndirizzoView = (TextView) findViewById(R.id.ricerca_avanzata_banca_indirizzo);
        mIbanView = (TextView) findViewById(R.id.ricerca_avanzata_banca_IBAN);
        mSearchButton = (ImageButton) findViewById(R.id.ricerca_avanzata_banca_button);
        mBancheListView = (ListView) findViewById(R.id.ricerca_avanzata_banca_result_list);
        mStatus = (TextView) findViewById(R.id.ricerca_avanzata_banca_status);

        mBancheList = getIntent().getParcelableArrayListExtra("listaBanche");

        mOriginalList = new ArrayList<>(mBancheList);

        mBancheListAdapter = new RubricaBancaListAdapter(getApplicationContext(), mBancheList);

        mBancheListView.setAdapter(mBancheListAdapter);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        //Comparatore per ordine alfabetico del nome
        bancaComparator = new Comparator<Banca>() {
            @Override
            public int compare(Banca lhs, Banca rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.getNome(), rhs.getNome());
            }
        };

        mBancheListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Banca bancaSelected = (Banca) parent.getItemAtPosition(position);
                Intent apriBanca = new Intent(getApplicationContext(), VisualizzaBancaActivity.class);
                apriBanca.putExtra("bancaToView", bancaSelected);
                startActivity(apriBanca);
            }
        });
    }

    void search() {
        String nome = mNominativoView.getText().toString();
        String indirizzo = mIndirizzoView.getText().toString();
        String iban = mIbanView.getText().toString();

        String[] dati = {nome, indirizzo, iban};

        nome = nome.toUpperCase();
        indirizzo = indirizzo.toUpperCase();
        iban = iban.toUpperCase();


        ArrayList<Banca> matchingElement = new ArrayList<>();


        if (!TextUtils.isEmpty(nome)) {
            if (!TextUtils.isEmpty(indirizzo)) {
                if (!TextUtils.isEmpty(iban)) { // Ricerca per nome, indirizzo e iban
                    for (Banca banca : mOriginalList) {
                        if ((banca.getNome().toUpperCase().contains(nome) && banca.getIndirizzo().toUpperCase().contains(indirizzo) && banca.getIban().toUpperCase().contains(iban))) {
                            matchingElement.add(banca);
                        }
                    }
                } else { // Ricerca per nome e indirizzo
                    for (Banca banca : mOriginalList) {
                        if ((banca.getNome().toUpperCase().contains(nome) && banca.getIndirizzo().toUpperCase().contains(indirizzo))) {
                            matchingElement.add(banca);
                        }
                    }

                }
            } else { // Ricerca per nome
                for (Banca banca : mOriginalList) {
                    if ((banca.getNome().toUpperCase().contains(nome))) {
                        matchingElement.add(banca);
                    }
                }
            }
        } else if (!TextUtils.isEmpty(indirizzo)) {
            if (!TextUtils.isEmpty(iban)) { // Ricerca per indirizzo e iban
                for (Banca banca : mOriginalList) {
                    if ((banca.getIndirizzo().toUpperCase().contains(indirizzo) && banca.getIban().toUpperCase().contains(iban))) {
                        matchingElement.add(banca);
                    }
                }
            } else { // Ricerca per indirizzo
                for (Banca banca : mOriginalList) {
                    if ((banca.getIndirizzo().toUpperCase().contains(indirizzo))) {
                        matchingElement.add(banca);
                    }
                }
            }
        } else if (!TextUtils.isEmpty(iban)) { // Ricerca per indirizzo
            for (Banca banca : mOriginalList) {
                if ((banca.getIndirizzo().toUpperCase().contains(indirizzo))) {
                    matchingElement.add(banca);
                }
            }
        } else {
            System.out.println("TUTTO SOTTO VUOTO " + mOriginalList.size());
            updateList(mOriginalList, dati);
            return;
        }

        updateList(matchingElement, dati);

    }

    private void updateList(ArrayList<Banca> list, String... query) {
        Collections.sort(list, bancaComparator);
        mBancheListAdapter.clear();
        mBancheListAdapter.addAll(list);
        mBancheListAdapter.cleanAlphabeticIndex();
        mBancheListAdapter.notifyDataSetChanged();
        String result = "";
        for (String data : query) {
            if (!TextUtils.isEmpty(data))
                result += "\"" + data + "\"";
        }

        mStatus.setText("Risultati per " + result + " : " + list.size());
    }
}
