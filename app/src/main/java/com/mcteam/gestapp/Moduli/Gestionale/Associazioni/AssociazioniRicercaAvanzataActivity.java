package com.mcteam.gestapp.Moduli.Gestionale.Associazioni;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mcteam.gestapp.Models.Associazione;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AssociazioniRicercaAvanzataActivity extends AppCompatActivity {
    EditText mClienteView;
    EditText mNomeCommessaView;
    EditText mCodiceCommessaView;
    EditText mReferente1View;
    EditText mConsulenteView;
    EditText mMeseInizioView;
    EditText mAnnoInizioView;
    TextView mStatus;
    LinearLayout mCercaForm;

    ArrayList<Associazione> mAssociazioniList;
    ArrayList<Associazione> mOriginalList;

    ListView mListResult;
    BootstrapButton mCercaButton;

    Comparator<Associazione> associazioniComparator = new Comparator<Associazione>() {
        @Override
        public int compare(Associazione lhs, Associazione rhs) {
            String rNomeSocieta = null;
            String lNomeSocieta = null;

            if (lhs.getCommessa() != null && lhs.getCommessa().getCliente() != null && lhs.getCommessa().getCliente().getNomeSocietà() != null)
                lNomeSocieta = lhs.getCommessa().getCliente().getNomeSocietà();
            if (rhs.getCommessa() != null && rhs.getCommessa().getCliente() != null && rhs.getCommessa().getCliente().getNomeSocietà() != null)
                rNomeSocieta = rhs.getCommessa().getCliente().getNomeSocietà();

            if (rNomeSocieta != null && lNomeSocieta != null)
                return String.CASE_INSENSITIVE_ORDER.compare(lNomeSocieta, rNomeSocieta);
            if (lNomeSocieta != null && rNomeSocieta == null) {
                return -1;
            } else
                return 2;
        }
    };

    AssociazioniListAdapter mListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associazioni_ricerca_avanzata);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            }
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        mClienteView = (EditText) findViewById(R.id.ricerca_associazioni_cliente);
        mNomeCommessaView = (EditText) findViewById(R.id.ricerca_associazioni_nome_commessa);
        mCodiceCommessaView = (EditText) findViewById(R.id.ricerca_associazioni_codice_commessa);
        mReferente1View = (EditText) findViewById(R.id.ricerca_associazioni_referente1);
        mConsulenteView = (EditText) findViewById(R.id.ricerca_associazioni_consulente);
        mMeseInizioView = (EditText) findViewById(R.id.ricerca_associazioni_mese_inizio);
        mAnnoInizioView = (EditText) findViewById(R.id.ricerca_associazioni_anno_inizio);
        mCercaForm = (LinearLayout) findViewById(R.id.ricerca_avanzata_associazioni_form);

        mListResult = (ListView) findViewById(R.id.ricerca_associazioni_list_result);

        mCercaButton = (BootstrapButton) findViewById(R.id.ricerca_associazioni_ricerca_button);

        mStatus = (TextView) findViewById(R.id.ricerca_associazioni_status_bar);

        mAssociazioniList = new ArrayList<>();

        mAssociazioniList = getIntent().getParcelableArrayListExtra("associazioniList");

        mOriginalList = new ArrayList<>(mAssociazioniList);

        mListAdapter = new AssociazioniListAdapter(this, mAssociazioniList);

        mListResult.setAdapter(mListAdapter);

        mCercaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCercaForm.getVisibility() == View.GONE) {
                    mCercaForm.setVisibility(View.VISIBLE);
                } else {
                    mCercaForm.setVisibility(View.GONE);
                    ricercaAvanzata();
                }
            }
        });

        mListResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Associazione ass = (Associazione) parent.getItemAtPosition(position);
                Intent visualizzaAss = new Intent(getApplicationContext(), VisualizzaAssociazioneActivity.class);
                visualizzaAss.putExtra("associazioneToView", ass);
                startActivity(visualizzaAss);
            }
        });
    }

    private void ricercaAvanzata() {

        String cliente = mClienteView.getText().toString();
        String nomeCommessa = mNomeCommessaView.getText().toString();
        String codiceCommessa = mCodiceCommessaView.getText().toString();
        String referente = mReferente1View.getText().toString();
        String consulente = mConsulenteView.getText().toString();
        String meseInizio = mMeseInizioView.getText().toString();
        String annoInizio = mAnnoInizioView.getText().toString();

        String data[] = {cliente, nomeCommessa, codiceCommessa, referente, consulente, meseInizio, annoInizio};


        if (TextUtils.isEmpty(cliente) && TextUtils.isEmpty(nomeCommessa) && TextUtils.isEmpty(codiceCommessa) && TextUtils.isEmpty(referente) && TextUtils.isEmpty(consulente) && TextUtils.isEmpty(meseInizio) && TextUtils.isEmpty(annoInizio)) {
            updateList(mOriginalList);
            return;
        }

        cliente = cliente.toUpperCase();
        nomeCommessa = nomeCommessa.toUpperCase();
        codiceCommessa = codiceCommessa.toUpperCase();
        referente = referente.toUpperCase();
        consulente = consulente.toUpperCase();

        ArrayList<Associazione> resultSet = mOriginalList;

        ArrayList<Associazione> tempResult;

        if (!TextUtils.isEmpty(cliente)) {
            tempResult = new ArrayList<>();
            String tmpCliente = "";
            for (Associazione associazione : mOriginalList) {
                tmpCliente = associazione.getCommessa().getCliente() != null ? associazione.getCommessa().getCliente().getNomeSocietà() : "";

                if (tmpCliente.toUpperCase().contains(cliente)) {
                    tempResult.add(associazione);
                }
            }
            resultSet = (ArrayList) Functions.union(resultSet, tempResult);
        }

        if (!TextUtils.isEmpty(nomeCommessa)) {

            tempResult = new ArrayList<>();

            for (Associazione associazione : mOriginalList) {
                String tmpCommessa = associazione.getCommessa() != null ? associazione.getCommessa().getNome_commessa() : "";

                if (tmpCommessa.toUpperCase().contains(nomeCommessa)) {
                    tempResult.add(associazione);
                }
            }
            resultSet = (ArrayList) Functions.union(resultSet, tempResult);
        }

        if (!TextUtils.isEmpty(codiceCommessa)) {

            tempResult = new ArrayList<>();

            for (Associazione associazione : mOriginalList) {
                String tmpCommessa = associazione.getCommessa() != null ? associazione.getCommessa().getCodice_commessa() : "";

                if (tmpCommessa.toUpperCase().contains(codiceCommessa)) {
                    tempResult.add(associazione);
                }
            }
            resultSet = (ArrayList) Functions.union(resultSet, tempResult);
        }

        if (!TextUtils.isEmpty(referente)) {

            tempResult = new ArrayList<>();

            for (Associazione associazione : mOriginalList) {
                String tmpReferente = associazione.getCommessa().getReferente1() != null ? associazione.getCommessa().getReferente1().getCognome() + " " + associazione.getCommessa().getReferente1().getNome() : "";

                if (tmpReferente.toUpperCase().contains(referente)) {
                    tempResult.add(associazione);
                }
            }
            resultSet = (ArrayList) Functions.union(resultSet, tempResult);
        }

        if (!TextUtils.isEmpty(consulente)) {

            tempResult = new ArrayList<>();

            for (Associazione associazione : mOriginalList) {
                String tmpConsulente = associazione.getRisorsa() != null ? associazione.getRisorsa().getCognome() + " " + associazione.getRisorsa().getNome() : "";

                if (tmpConsulente.toUpperCase().contains(consulente)) {
                    tempResult.add(associazione);
                }
            }
            resultSet = (ArrayList) Functions.union(resultSet, tempResult);
        }

        if (!TextUtils.isEmpty(meseInizio)) {

            tempResult = new ArrayList<>();
            String dataSplit[];
            for (Associazione associazione : mOriginalList) {
                dataSplit = associazione.getData_inizio().split("-");

                if (dataSplit[1].contains(meseInizio)) {
                    tempResult.add(associazione);
                }
            }
            resultSet = (ArrayList) Functions.union(resultSet, tempResult);
        }


        if (!TextUtils.isEmpty(annoInizio)) {

            tempResult = new ArrayList<>();

            for (Associazione associazione : mOriginalList) {
                String tmpAnnoInizio = associazione.getData_inizio().split("-")[0];

                if (tmpAnnoInizio.contains(annoInizio)) {
                    tempResult.add(associazione);
                }
            }
            resultSet = (ArrayList) Functions.union(resultSet, tempResult);
        }

        updateList(resultSet, data);
    }


    private void updateList(ArrayList<Associazione> list, String... query) {
        mAssociazioniList.clear();
        Collections.sort(list, associazioniComparator);
        mListAdapter.cleanAlphaIndex();
        mAssociazioniList.addAll(list);
        String result = "";
        for (String data : query) {
            if (!TextUtils.isEmpty(data))
                result += "\'" + data + "\'";
        }
        mStatus.setText("Risultati per " + result + " : " + list.size());
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
