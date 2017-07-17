package com.mcteam.gestapp.Moduli.Gestionale.Nominativo;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.mcteam.gestapp.Models.Rubrica.Nominativo;
import com.mcteam.gestapp.Models.UserInfo;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NominativiAdvanceSearch extends AppCompatActivity {

    ListView mResulList;
    RubricaNominativaListAdapter mListAdapter;
    ArrayList<Nominativo> mNominativiList = null;
    ArrayList<Nominativo> mSearchList;
    EditText mNome;
    EditText mSocieta;
    EditText mCognome;
    Comparator<Nominativo> surnameSortingComparator;
    TextView mRicercaStatus;
    UserInfo actualUser;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nominativi_advance_search);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        Intent intent = getIntent();
        mNominativiList = intent.getParcelableArrayListExtra("listaNominativi");
        actualUser = intent.getParcelableExtra("actualUser");

        mResulList = (ListView) findViewById(R.id.nominativo_ricerca_avanzata_lista_risultato);
        //Comparatore per ordine alfabetico del cognome
        surnameSortingComparator = new Comparator<Nominativo>() {
            @Override
            public int compare(Nominativo lhs, Nominativo rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.getCognome(), rhs.getCognome());
            }
        };

        if (mNominativiList == null || mNominativiList.isEmpty()) {
            //finish();
        }

        mSearchList = new ArrayList<>(mNominativiList);

        mListAdapter = new RubricaNominativaListAdapter(this, mNominativiList, actualUser);

        mRicercaStatus = (TextView) findViewById(R.id.nominativo_ricerca_avanzata_status);
        mNome = (EditText) findViewById(R.id.nominativo_ricerca_avanzata_nome);
        mSocieta = (EditText) findViewById(R.id.nominativo_ricerca_avanzata_societa);
        mCognome = (EditText) findViewById(R.id.nominativo_ricerca_avanzata_cognome);


        ImageButton cercaButton = (ImageButton) findViewById(R.id.nominativo_ricerca_avanzata_cerca_button);

        cercaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerca();
            }
        });

        //
        mResulList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Nominativo selectedNominativo;
                selectedNominativo = (Nominativo) parent.getItemAtPosition(position);
                Intent visualizzaIntent = new Intent(getApplicationContext(), VisualizzaNominativoActivity.class);
                visualizzaIntent.putExtra("nominativoToView", selectedNominativo);
                startActivity(visualizzaIntent);
            }
        });//

        mResulList.setAdapter(mListAdapter);
    }
    /**
     * Metodo per la ricerca degli elementi
     */
    private void cerca() {

        String nomeSocieta = mSocieta.getText().toString();

        String nome = mNome.getText().toString();

        String cognome = mCognome.getText().toString();

        String[] dati = {nomeSocieta, cognome, nome};

        nomeSocieta = nomeSocieta.toUpperCase();
        nome = nome.toUpperCase();
        cognome = cognome.toUpperCase();


        ArrayList<Nominativo> matchingElement = new ArrayList<>();


        if (TextUtils.isEmpty(nome) && TextUtils.isEmpty(nomeSocieta) && TextUtils.isEmpty(cognome)) {
            updateList(mSearchList);
            return;
        }

        if (!TextUtils.isEmpty(nome)) {
            if (!TextUtils.isEmpty(nomeSocieta)) {
                if (!TextUtils.isEmpty(cognome)) { // Ricerca per cognome, nome e società
                    for (Nominativo nominativo : mSearchList) {
                        if (nominativo.getSocieta() == null || nominativo.getSocieta().getNomeSocietà() == null)
                            continue;
                        if ((nominativo.getSocieta().getNomeSocietà().toUpperCase().contains(nomeSocieta) && nominativo.getCognome().toUpperCase().contains(cognome) && nominativo.getNome().toUpperCase().contains(nome))) {
                            matchingElement.add(nominativo);
                        }
                    }
                }
                else { // Ricerca per nome e società
                    for (Nominativo nominativo : mSearchList) {
                        if (nominativo.getSocieta() == null || nominativo.getSocieta().getNomeSocietà() == null)
                            continue;
                        if ((nominativo.getSocieta().getNomeSocietà().toUpperCase().contains(nomeSocieta) && nominativo.getNome().toUpperCase().contains(nome))) {
                            matchingElement.add(nominativo);
                        }
                    }
                }
            }
            else {
                if (!TextUtils.isEmpty(cognome)) { // Ricerca per cognome, nome e società
                    for (Nominativo nominativo : mSearchList) {
                        if (nominativo.getSocieta() == null || nominativo.getSocieta().getNomeSocietà() == null)
                            continue;
                        if ((nominativo.getSocieta().getNomeSocietà().toUpperCase().contains(nomeSocieta) && nominativo.getCognome().toUpperCase().contains(cognome) && nominativo.getNome().toUpperCase().contains(nome))) {
                            matchingElement.add(nominativo);
                        }
                    }
                } else { // Ricerca per nome
                    for (Nominativo nominativo : mSearchList) {
                        if (nominativo.getNome().toUpperCase().contains(nome)) {
                            matchingElement.add(nominativo);
                        }
                    }
                }
            }

        } else if (!TextUtils.isEmpty(cognome)) {
            if (!TextUtils.isEmpty(nomeSocieta)) { // Ricerca per cognome e società
                for (Nominativo nominativo : mSearchList) {
                    if (nominativo.getSocieta() == null || nominativo.getSocieta().getNomeSocietà() == null)
                        continue;
                    if ((nominativo.getSocieta().getNomeSocietà().toUpperCase().contains(nomeSocieta) && nominativo.getCognome().toUpperCase().contains(cognome))) {
                        matchingElement.add(nominativo);
                    }
                }
            } else { // Ricerca per cognome
                for (Nominativo nominativo : mSearchList) {
                    if (nominativo.getCognome().toUpperCase().contains(cognome)) {
                        matchingElement.add(nominativo);
                    }
                }
            }
        } else if (!TextUtils.isEmpty(nomeSocieta)) { // Ricerca per società
            for (Nominativo nominativo : mSearchList) {
                if (nominativo.getSocieta() == null || nominativo.getSocieta().getNomeSocietà() == null)
                    continue;
                if ((nominativo.getSocieta().getNomeSocietà().toUpperCase().contains(nomeSocieta
                ))) {
                    matchingElement.add(nominativo);
                }
            }
        }

        updateList(matchingElement);

    }

    private void updateList(ArrayList<Nominativo> list, String... query) {
        mNominativiList.clear();
        mNominativiList.addAll(list);
        Collections.sort(list, surnameSortingComparator);
        String result = "";
        for (String data : query) {
            if (!TextUtils.isEmpty(data))
                result += "\"" + data + "\"";
        }

        mRicercaStatus.setText("Risultati per " + result + " : " + list.size());
        mListAdapter.cleanAlphabeticIndex();
        mListAdapter.notifyDataSetChanged();
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

