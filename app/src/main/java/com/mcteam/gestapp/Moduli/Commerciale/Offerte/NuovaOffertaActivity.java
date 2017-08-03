package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mcteam.gestapp.Fragments.DatePickerFragment;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Models.Rubrica.Nominativo;
import com.mcteam.gestapp.Moduli.Gestionale.Allegati.AllegatiUtils;
import com.mcteam.gestapp.NetworkReq.VolleyRequests;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Created by Riccardo Rossi on 15/10/2016.
 */
public class NuovaOffertaActivity extends AppCompatActivity {

    private VolleyRequests mVolleyRequests;
    private EditText mData;
    private static final int FILE_CODE = 992;
    private File mChoosenFile;
    private DatePickerFragment mDateFragment;
    private ImageView mAllegatoLogo;
    private TextView mAllegatoNome;
    private TextView mAllegatoSize;
    private BootstrapButton mAllegato;
    private Commessa commessa;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_offerta);

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

        commessa = getIntent().getParcelableExtra("COMMESSA");

        EditText codCommessa = (EditText) findViewById(R.id.dett_off_new_codcomm);
        EditText cliente = (EditText) findViewById(R.id.dett_off_new_cliente);
        mData = (EditText) findViewById(R.id.dett_off_new_data);
        EditText oggetto = (EditText) findViewById(R.id.dett_off_new_obj);
        mAllegato = (BootstrapButton) findViewById(R.id.dett_off_new_alleg);
        Button creaButton = (Button) findViewById(R.id.bCrea);
        mAllegatoLogo = (ImageView) findViewById(R.id.dett_off_alleg_logo);
        mAllegatoNome = (TextView) findViewById(R.id.dett_off_alleg_nome);
        mAllegatoSize = (TextView) findViewById(R.id.dett_off_alleg_size);


        //Codice commessa
        codCommessa.setText(commessa.getCodice_commessa());
        codCommessa.setEnabled(false);

        //Cliente
        cliente.setText(commessa.getCliente().getNomeSocietà());
        cliente.setEnabled(false);

        //Inizializzazione mData
        mDateFragment = new DatePickerFragment(mData);
        mData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSelData();
            }
        });

        //Oggetto
        oggetto.setText(commessa.getNome_commessa());
        oggetto.setEnabled(false);

        mAllegato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //File chooser
                Intent i = new Intent(getApplicationContext(), FilePickerActivity.class);
                // This works if you defined the intent filter
                // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                // Set these depending on your use case. These are the defaults.
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                // Configure initial directory by specifying a String.
                // You could specify a String like "/storage/emulated/0/", but that can
                // dangerous. Always use Android's API calls to get paths to the SD-card or
                // internal memory.
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

                startActivityForResult(i, FILE_CODE);
            }
        });

        //Riempita nominativi list
        mVolleyRequests = new VolleyRequests(this, this);

        creaButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            mChoosenFile = new File(uri.getPath());
            if (mChoosenFile != null) {
                mAllegatoNome.setText(mChoosenFile.getName());
                Bitmap logo = AllegatiUtils.getAllegatoLogo(getResources(), mChoosenFile.getName());
                mAllegatoLogo.setImageBitmap(logo);
                mAllegatoSize.setText(mChoosenFile.length() + "Bytes");
            }
            // Do anything with file
        }
    }

    //Apertura date picker dopo click sul campo

    private void onClickSelData() {
        mDateFragment.show(getFragmentManager(), "datePicker");
    }


    public void onClickAnnulla(View view) {
        finish();
    }

    public void onClickCrea(View view) throws IOException {
        String nomeAllegatoSelected = mAllegatoNome.getText().toString();

        boolean cancel = false;
        View focusView = null;

        /* Controllo se la mData è stata selezionata */
        if (!mDateFragment.isDataSelected()) {
            Toast.makeText(getApplicationContext(), "La data non è stata selezionata: impossibile continuare", Toast.LENGTH_LONG).show();
            focusView = mData;
            cancel = true;
        }
        /* Controllo allegato scelto */
        else if (mChoosenFile == null) {
            Toast.makeText(getApplicationContext(), "File non selezionato: scegliere un file", Toast.LENGTH_LONG).show();
            focusView = mAllegato;
            cancel = true;
        }

        if (!cancel) {
            String data = null;
            try
            {
                data = Functions.fromDateToSql(mData.getText().toString());
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
            String json = "{\"allegato\":\""+mChoosenFile.getName()+"\",\"data_offerta\":\"" + data +"\",\"id_commessa\":" + commessa.getID() +",\"accettata\":0,\"versione\":0}";
            System.out.println(json);

            try {
                mVolleyRequests.addNewElementRequest(json,"offerta-nuovo/"+commessa.getID());
                mVolleyRequests.uploadFileOfferta(mChoosenFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (focusView != null)
                focusView.requestFocus();
        }

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
            case R.id.action_logout:
                Functions.logout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
