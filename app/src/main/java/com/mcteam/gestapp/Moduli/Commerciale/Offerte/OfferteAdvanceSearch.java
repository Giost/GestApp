package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mcteam.gestapp.Models.Commerciale.Offerta;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import android.app.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

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
    private DatePickerDialog DatePickerDialog;

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
        mOffAdapter = new DettaglioOffertaAdapter(mSearchList, mCommessa);
        mOffRecyclerView = (RecyclerView) findViewById(R.id.offerte_ricerca_avanzata_recycler);
        mOffRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOffRecyclerView.setAdapter(mOffAdapter);

        mVersione = (EditText) findViewById(R.id.offerte_ricerca_avanzata_versione);

        mDataOfferta = (EditText) findViewById(R.id.offerte_ricerca_avanzata_data);
        mDataOfferta.setInputType(InputType.TYPE_NULL);

        mDataOfferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.show();
            }
        });
        mDataOfferta.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v,boolean isFocused) {
                if (isFocused) {
                    DatePickerDialog.show();
                }
            }
        });
//http://androidopentutorials.com/android-datepickerdialog-on-edittext-click-event/
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ITALY);
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog = new DatePickerDialog(this, new android.app.DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mDataOfferta.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

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
        ArrayList<Offerta> tempResult;

        String versione = mVersione.getText().toString();
        String dataOfferta = mDataOfferta.getText().toString();
        int presentata = mPresentata.isChecked() ? 1 : 0 ;

        String data[] = {versione, dataOfferta, presentata == 1 ? "SI" : "NO"};

        if(!TextUtils.isEmpty(versione))
        {
            if (!TextUtils.isEmpty(dataOfferta)) // Ricerca per versione, data e presentata
            {
                for (Offerta off : mOffArrayList)
                {
                    if (versione.contains(String.valueOf(off.getVersione())) && dataOfferta.contains(Functions.getFormattedDate(off.getDataOfferta())) && presentata==off.getAccettata())
                    {
                        matchingElement.add(off);
                    }
                }
            }
            else // Ricerca per versione e presentata
            {
                for (Offerta off : mOffArrayList)
                {
                    if (versione.contains(String.valueOf(off.getVersione())) && presentata==off.getAccettata())
                    {
                        matchingElement.add(off);
                    }
                }
            }
        }
        else if (!TextUtils.isEmpty(dataOfferta)) // Ricerca per data e presentata
        {
            for (Offerta off : mOffArrayList)
            {
                if (dataOfferta.contains(Functions.getFormattedDate(off.getDataOfferta())) && presentata==off.getAccettata())
                {
                    matchingElement.add(off);
                }
            }
        }
        else // Ricerca per presentata
        {
            for (Offerta off : mOffArrayList)
            {
                if (presentata==off.getAccettata())
                {
                    matchingElement.add(off);
                }
            }
        }
        updateList(matchingElement,data);
        System.out.println(mOffArrayList.get(0).getDataOfferta()+" data:"+dataOfferta+"presentata:"+presentata);
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
