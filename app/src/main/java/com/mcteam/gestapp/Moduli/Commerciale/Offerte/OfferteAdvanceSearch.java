package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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

import com.mcteam.gestapp.Models.Commerciale.Offerta;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.R;

import java.util.ArrayList;
import java.util.Collections;

public class OfferteAdvanceSearch extends AppCompatActivity {

    private ArrayList<Offerta> mOffArrayList;
    private ArrayList<Offerta> mSearchList;
    private RecyclerView mOffRecyclerView;
    private DettaglioOffertaAdapter mOffAdapter;
    private Commessa mCommessa;
    private EditText mVersione;
    private EditText mDataOfferta;
    private RadioButton mPresentata;
    private RadioButton mNonPresentata;
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
        mOffArrayList = getIntent().getParcelableArrayListExtra("OFFERTE");
        mCommessa = getIntent().getParcelableExtra("COMMESSA");

        mSearchList = new ArrayList<>(mOffArrayList);

        // inizializzazione view
        mOffAdapter = new DettaglioOffertaAdapter(mOffArrayList, mCommessa);
        mOffRecyclerView = (RecyclerView) findViewById(R.id.offerte_ricerca_avanzata_recycler);
        mOffRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOffRecyclerView.setAdapter(mOffAdapter);

        mVersione = (EditText) findViewById(R.id.offerte_ricerca_avanzata_versione);
        mDataOfferta = (EditText) findViewById(R.id.offerte_ricerca_avanzata_data);
        mPresentata = (RadioButton) findViewById(R.id.offerte_presentata_si);
        mNonPresentata = (RadioButton) findViewById(R.id.offerte_presentata_no);
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
        ArrayList<Offerta> matchingElement = new ArrayList<>();

        String versione = mVersione.getText().toString();
        String dataOfferta = mDataOfferta.getText().toString();
        int presentata = mPresentata.isChecked() ? 1 : 0 ;

        String data[] = {versione, dataOfferta, String.valueOf(presentata)};

        versione.toUpperCase();
        dataOfferta.toUpperCase();

        System.out.println(versione+" data:"+dataOfferta+"presentata:"+presentata);
    }

    private void updateList(ArrayList<Offerta> list, String... query) {
        mSearchList.clear();
        mSearchList.addAll(list);
        String result = "";
        for (String data : query) {
            if (!TextUtils.isEmpty(data))
                result += "\"" + data + "\"";
        }

        mResultStatus.setText("Risultati per " + result + " : " + list.size());
        mOffAdapter.notifyDataSetChanged();
    }
}
