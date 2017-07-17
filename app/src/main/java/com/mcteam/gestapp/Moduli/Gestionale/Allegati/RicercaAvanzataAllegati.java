package com.mcteam.gestapp.Moduli.Gestionale.Allegati;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mcteam.gestapp.Models.Allegato;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RicercaAvanzataAllegati extends AppCompatActivity {
    TextView mDescrizioneView;
    TextView mDataInserimento;
    TextView mTipoFile;
    TextView mStatus;
    BootstrapButton mCercaButton;
    ListView resultList;
    AllegatiListAdapter mAllegatiAdapter;
    ArrayList<Allegato> mAllegatiList;
    ArrayList<Allegato> mOriginalList;
    Comparator<Allegato> allegatoComparator = new Comparator<Allegato>() {
        @Override
        public int compare(Allegato lhs, Allegato rhs) {
            return String.CASE_INSENSITIVE_ORDER.compare(lhs.getDescrizione(), rhs.getDescrizione());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_avanzata_allegati);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mDescrizioneView = (TextView) findViewById(R.id.ricerca_avanzata_allegati_descrizione);
        mDataInserimento = (TextView) findViewById(R.id.ricerca_avanzata_allegati_data_inserimento);
        mTipoFile = (TextView) findViewById(R.id.ricerca_avanzata_allegati_tipo_file);
        mCercaButton = (BootstrapButton) findViewById(R.id.ricerca_avanzata_allegati_cerca_button);
        resultList = (ListView) findViewById(R.id.ricerca_avanzata_allegati_list_result);
        mStatus = (TextView) findViewById(R.id.ricerca_avanzata_allegati_status_bar);

        mAllegatiList = getIntent().getParcelableArrayListExtra("listaAllegati");

        mOriginalList = new ArrayList<>();
        mOriginalList.addAll(mAllegatiList);

        mAllegatiAdapter = new AllegatiListAdapter(this, mAllegatiList);

        resultList.setAdapter(mAllegatiAdapter);

        mCercaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ricercaAvanzata();
            }
        });
    }

    private void ricercaAvanzata() {
        String descrizione = mDescrizioneView.getText().toString();
        String data = mDataInserimento.getText().toString();
        String tipoFile = mTipoFile.getText().toString();

        String[] dati = {descrizione, data, tipoFile};

        descrizione = descrizione.toUpperCase();
        data = data.toUpperCase();
        tipoFile = tipoFile.toUpperCase();

        ArrayList<Allegato> matchingElement = new ArrayList<>();

        if (TextUtils.isEmpty(descrizione) && TextUtils.isEmpty(data) && TextUtils.isEmpty(tipoFile)) {
            updateList(mOriginalList);
            return;
        }
        if (!TextUtils.isEmpty(descrizione)) {
            if (!TextUtils.isEmpty(data)) {
                if (!TextUtils.isEmpty(tipoFile)) {
                    for (Allegato allegato : mOriginalList) {
                        if (allegato.getDescrizione().toUpperCase().contains(descrizione) && allegato.getFile().toUpperCase().contains(tipoFile) && allegato.getUpload().contains(data)) {
                            matchingElement.add(allegato);
                        }
                    }
                } else {
                    for (Allegato allegato : mOriginalList) {
                        if (allegato.getDescrizione().toUpperCase().contains(descrizione) && allegato.getUpload().contains(data)) {
                            matchingElement.add(allegato);
                        }
                    }
                }
            } else {
                if (!TextUtils.isEmpty(tipoFile)) {
                    for (Allegato allegato : mOriginalList) {
                        if (allegato.getDescrizione().toUpperCase().contains(descrizione) && allegato.getFile().toUpperCase().contains(tipoFile)) {
                            matchingElement.add(allegato);
                        }
                    }
                } else {
                    for (Allegato allegato : mOriginalList) {
                        if (allegato.getDescrizione().toUpperCase().contains(descrizione)) {
                            matchingElement.add(allegato);
                        }
                    }
                }
            }
        } else if (!TextUtils.isEmpty(data)) {
            if (!TextUtils.isEmpty(tipoFile)) {
                for (Allegato allegato : mOriginalList) {
                    if (allegato.getFile().toUpperCase().contains(tipoFile) && allegato.getUpload().contains(data)) {
                        matchingElement.add(allegato);
                    }
                }
            } else {
                for (Allegato allegato : mOriginalList) {
                    if (allegato.getUpload().contains(data)) {
                        matchingElement.add(allegato);
                    }
                }

            }
        } else if (!TextUtils.isEmpty(tipoFile)) {
            for (Allegato allegato : mOriginalList) {
                if (allegato.getFile().toUpperCase().contains(tipoFile)) {
                    matchingElement.add(allegato);
                }
            }
        }
        updateList(matchingElement, dati);

    }

    private void updateList(ArrayList<Allegato> list, String... query) {
        Collections.sort(list, allegatoComparator);
        mAllegatiAdapter.clear();
        mAllegatiAdapter.addAll(list);
        mAllegatiAdapter.cleanAlphabeticIndex();
        mAllegatiAdapter.notifyDataSetChanged();
        String result = "";
        for (String data : query) {
            if (!TextUtils.isEmpty(data))
                result += "\"" + data + "\"";
        }

        mStatus.setText("Risultati per " + result + " : " + list.size());
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
