package com.mcteam.gestapp.Moduli.Gestionale.Commesse;

import android.annotation.TargetApi;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RicercaAvanzataCommesseActivity extends AppCompatActivity {


    Spinner mAvanzamentoSpinner;
    EditText mCodiceTv;
    EditText mNomeCommessaTv;
    EditText mClienteTv;
    EditText mReferente1Tv;
    EditText mReferente2Tv;
    ListView mListView;
    BootstrapButton mCercaButton;
    CommesseListAdapter mCommesseListAdapter;
    String selectedAvanzamento = "";
    LinearLayout mInputs;

    Comparator<Commessa> commessaComparator = new Comparator<Commessa>() {
        @Override
        public int compare(Commessa lhs, Commessa rhs) {
            String lhsNomeSocieta = lhs.getCliente().getNomeSocietà();
            String rhsNomeSocieta = rhs.getCliente().getNomeSocietà();
            return String.CASE_INSENSITIVE_ORDER.compare(lhsNomeSocieta, rhsNomeSocieta);
        }
    };


    ArrayList<Commessa> mCommesseList = null;
    ArrayList<Commessa> mOriginalList = null;

    TextView mRicercaStatus;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_avanzata_commesse);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        //***********************************************************************************
        //  get the list form the intent
        //***********************************************************************************

        mCommesseList = getIntent().getParcelableArrayListExtra("commesseList");

        mOriginalList = new ArrayList<>(mCommesseList);

        //***********************************************************************************
        //  Inizializzazione view
        //***********************************************************************************

        mAvanzamentoSpinner = (Spinner) findViewById(R.id.commessa_ricerca_avanzata_avanzamento);
        mCodiceTv = (EditText) findViewById(R.id.commesse_ricerca_avanzata_codice);
        mNomeCommessaTv = (EditText) findViewById(R.id.commesse_ricerca_avanzata_nome);
        mClienteTv = (EditText) findViewById(R.id.commesse_ricerca_avanzata_cliente);
        mReferente1Tv = (EditText) findViewById(R.id.commesse_ricerca_avanzata_referente_1);
        mReferente2Tv = (EditText) findViewById(R.id.commesse_ricerca_avanzata_referente_2);
        mListView = (ListView) findViewById(R.id.commesse_ricerca_avanzata_list_result);
        mRicercaStatus = (TextView) findViewById(R.id.commesse_ricerca_avanzata_status);
        mCercaButton = (BootstrapButton) findViewById(R.id.ricerca_avanzata_commesse_cerca_button);
        mInputs = (LinearLayout) findViewById(R.id.commesse_ricerca_avanzata_inputs);

        mCommesseListAdapter = new CommesseListAdapter(this, mCommesseList, false);

        mListView.setAdapter(mCommesseListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Commessa commessa = (Commessa) parent.getItemAtPosition(position);
                Intent visualizzaCommessa = new Intent(getApplicationContext(), VisualizzaCommessaActivity.class);
                visualizzaCommessa.putExtra("commessaToView", commessa);
                startActivity(visualizzaCommessa);
            }
        });

        //Adapter spinner
        ArrayAdapter<CharSequence> avanzamentoAdapter = ArrayAdapter.createFromResource(this,
                R.array.avanzamento, android.R.layout.simple_expandable_list_item_1);

        mAvanzamentoSpinner.setAdapter(avanzamentoAdapter);

        mAvanzamentoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    selectedAvanzamento = (String) parent.getItemAtPosition(position);
                else
                    selectedAvanzamento = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedAvanzamento = "";

            }
        });

        mCercaButton.setOnClickListener(new View.OnClickListener() {
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
    private void advanceSearch() {
        String codice = null,
                nomeCommessa = null,
                cliente = null,
                inputReferente1 = null,
                inputReferente2 = null;

        codice = mCodiceTv.getText().toString();
        nomeCommessa = mNomeCommessaTv.getText().toString();
        cliente = mClienteTv.getText().toString();
        inputReferente1 = mReferente1Tv.getText().toString();
        inputReferente2 = mReferente2Tv.getText().toString();

        String data[] = {codice, nomeCommessa, cliente, inputReferente1, inputReferente2, selectedAvanzamento};

        codice = codice.toUpperCase();
        nomeCommessa = nomeCommessa.toUpperCase();
        cliente = cliente.toUpperCase();
        inputReferente1 = inputReferente1.toUpperCase();
        inputReferente2 = inputReferente2.toUpperCase();


        if (TextUtils.isEmpty(codice) && TextUtils.isEmpty(nomeCommessa) && TextUtils.isEmpty(cliente) && TextUtils.isEmpty(inputReferente1) && TextUtils.isEmpty(inputReferente2) && TextUtils.isEmpty(selectedAvanzamento)) {
            updateList(mOriginalList);
            return;
        }
        String nomeSocieta = "";
        String ref1 = "";
        String ref2 = "";

        ArrayList<Commessa> commesseSetResult = new ArrayList<>();

        commesseSetResult = mOriginalList;

        ArrayList<Commessa> tempResult;

        if (!TextUtils.isEmpty(codice)) { // Ricerca per codice
            tempResult = new ArrayList<>();
            for (Commessa commessa : mOriginalList) {
                if (commessa.getCodice_commessa().toUpperCase().contains(codice)) {
                    tempResult.add(commessa);
                }
            }

            commesseSetResult = (ArrayList) Functions.union(commesseSetResult, tempResult);

        }

        if (!TextUtils.isEmpty(nomeCommessa)) { // Ricerca per nome commessa
            tempResult = new ArrayList<>();
            for (Commessa commessa : mOriginalList) {

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
            for (Commessa commessa : mOriginalList) {

                if (commessa.getCliente() != null && !TextUtils.isEmpty(commessa.getCliente().getNomeSocietà())) {
                    nomeSocieta = commessa.getCliente().getNomeSocietà().toUpperCase();
                }

                if (nomeSocieta.contains(cliente)) {
                    tempResult.add(commessa);
                }
            }
            commesseSetResult = (ArrayList) Functions.union(commesseSetResult, tempResult);
        }


        if (!TextUtils.isEmpty(inputReferente1)) { // Ricerca per nome e cognome referente 1
            tempResult = new ArrayList<>();
            for (Commessa commessa : mOriginalList) {
                if (commessa.getReferente1() != null) {
                    ref1 = commessa.getReferente1().getNome() + " " + commessa.getReferente1().getCognome();
                    if (!TextUtils.isEmpty(ref1)) {
                        ref1 = ref1.toUpperCase();
                        if (ref1.contains(inputReferente1)) {
                            tempResult.add(commessa);
                        }
                    }
                }

            }
            System.out.println(" referente 1 ---> " + tempResult.size());

            commesseSetResult = (ArrayList) Functions.union(commesseSetResult, tempResult);
        }


        if (!TextUtils.isEmpty(inputReferente2)) { // Ricerca per cognome e nome referente 1
            tempResult = new ArrayList<>();
            for (Commessa commessa : mOriginalList) {

                if (commessa.getReferente2() != null) {
                    ref2 = commessa.getReferente2().getNome() + " " + commessa.getReferente2().getCognome();
                    if (!TextUtils.isEmpty(ref2)) {
                        ref2 = ref2.toUpperCase();
                        if (ref2.contains(inputReferente2)) {
                            tempResult.add(commessa);
                        }
                    }
                }

            }

            commesseSetResult = (ArrayList) Functions.union(commesseSetResult, tempResult);
        }


        if (!TextUtils.isEmpty(selectedAvanzamento)) { // Ricerca per avanzamento
            tempResult = new ArrayList<>();
            for (Commessa commessa : mOriginalList) {

                if (commessa.getAvanzamento().toUpperCase().contains(selectedAvanzamento.toUpperCase())) {
                    tempResult.add(commessa);
                }
            }


            commesseSetResult = (ArrayList) Functions.union(commesseSetResult, tempResult);
        }


        updateList(commesseSetResult, data);
    }

    private void updateList(ArrayList<Commessa> list, String... query) {
        mCommesseList.clear();
        Collections.sort(list, commessaComparator);
        mCommesseList.addAll(list);
        String result = "";
        for (String data : query) {
            if (!TextUtils.isEmpty(data))
                result += "\"" + data + "\"";
        }
        mRicercaStatus.setText("Risultati per " + result + " : " + list.size());
        mCommesseListAdapter.notifyDataSetChanged();
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
