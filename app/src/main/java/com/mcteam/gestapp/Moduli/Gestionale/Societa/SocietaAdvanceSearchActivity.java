package com.mcteam.gestapp.Moduli.Gestionale.Societa;

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

import com.mcteam.gestapp.Models.Rubrica.Societa;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SocietaAdvanceSearchActivity extends AppCompatActivity {

    ListView mResulList;
    RubricaSocietaListAdapter mListAdapter;
    ArrayList<Societa> mSocietaList = null;
    ArrayList<Societa> mSearchList;
    EditText mCitta;
    EditText mSocieta;
    Comparator<Societa> surnameSortingComparator;
    TextView mRicercaStatus;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_societa_advance_search);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
            getSupportActionBar().setIcon(R.drawable.ic_accessibility_white_24dp);
        }

        mResulList = (ListView) findViewById(R.id.societa_ricerca_avanzata_lista);

        surnameSortingComparator =
                new Comparator<Societa>() {
                    @Override
                    public int compare(Societa lhs, Societa rhs) {
                        return String.CASE_INSENSITIVE_ORDER.compare(lhs.getNomeSocietà(), rhs.getNomeSocietà());
                    }
                };

        Intent intent = getIntent();
        mSocietaList = intent.getParcelableArrayListExtra("listaSocieta");

        if (mSocietaList == null || mSocietaList.isEmpty()) {
            finish();
        }

        mSearchList = new ArrayList<>(mSocietaList);

        mRicercaStatus = (TextView) findViewById(R.id.societa_ricerca_avanzata_status);

        mListAdapter = new RubricaSocietaListAdapter(this, mSocietaList);

        mCitta = (EditText) findViewById(R.id.societa_ricerca_avanzata_citta);
        mSocieta = (EditText) findViewById(R.id.societa_ricerca_avanzata_societa);

        ImageButton cercaButton = (ImageButton) findViewById(R.id.societa_ricerca_avanzata_button);

        cercaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerca();
            }
        });

        mResulList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Societa selectedSociety;
                selectedSociety = (Societa) parent.getItemAtPosition(position);
                Intent visualizzaIntent = new Intent(getApplicationContext(), VisualizzaSocietaActivity.class);
                visualizzaIntent.putExtra("societaToView", selectedSociety);
                startActivity(visualizzaIntent);
            }
        });

        mResulList.setAdapter(mListAdapter);
    }
    /**
    * Metodo per la ricerca degli elementi
    */
    private void cerca() {

        String nomeSocieta = mSocieta.getText().toString();

        String citta = mCitta.getText().toString();

        ArrayList<Societa> matchingElement = new ArrayList<>();
        // Se è stata inserita sia una città che un nome nella ricerca, il confronto viene fatto per tutti e due i campi
        if (!TextUtils.isEmpty(citta) && !TextUtils.isEmpty(nomeSocieta)) {
            for (Societa societa : mSearchList) {
                if ((societa.getNomeSocietà().toUpperCase().contains(nomeSocieta.toUpperCase()) && societa.getmCitta().toUpperCase().contains(citta.toUpperCase()))) {
                    matchingElement.add(societa);
                }
            }
            String[] dati = {nomeSocieta, citta};
            updateList(matchingElement, dati);
        } else if (!TextUtils.isEmpty(citta)) { // Confronta solo la città
            for (Societa societa : mSearchList) {
                if (societa.getmCitta().toUpperCase().contains(citta.toUpperCase())) {
                    matchingElement.add(societa);
                }
            }
            updateList(matchingElement, citta);
        } else if (!TextUtils.isEmpty(nomeSocieta)) { // Confronta solo il nome della società
            for (Societa societa : mSearchList) {
                if (societa.getNomeSocietà().toUpperCase().contains(nomeSocieta.toUpperCase())) {
                    matchingElement.add(societa);
                }
            }
            updateList(matchingElement, nomeSocieta);
        } else if (TextUtils.isEmpty(citta) && TextUtils.isEmpty(nomeSocieta)) { // Ripristina tutti gli elementi nella visualizzazione
            updateList(mSearchList);
        }
    }

    private void updateList(ArrayList<Societa> list, String... query) {
        mSocietaList.clear();
        mSocietaList.addAll(list);
        Collections.sort(list, surnameSortingComparator);
        String result = "";
        for (String data : query) {
            result += "\"" + data + "\"";
        }

        mRicercaStatus.setText("Risultati per " + result + " : " + list.size());
        mListAdapter.clearAlphabeticIndex();
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
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        Intent goLogin = new Intent(this, LoginActivity.class);
        goLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goLogin);
        finish();
    }
}
