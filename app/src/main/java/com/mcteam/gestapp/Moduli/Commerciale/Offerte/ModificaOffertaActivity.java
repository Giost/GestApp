package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.Gson;
import com.mcteam.gestapp.Fragments.DatePickerFragment;
import com.mcteam.gestapp.Models.Commerciale.Offerta;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Models.Rubrica.Nominativo;
import com.mcteam.gestapp.Moduli.Gestionale.Allegati.AllegatiUtils;
import com.mcteam.gestapp.NetworkReq.VolleyRequests;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;

public class ModificaOffertaActivity extends AppCompatActivity {

    EditText mOffertaCodComm;
    EditText mOffertaClient;
    EditText mOffertaDataOff;
    EditText mOffertaObj;
    CheckBox mOffertaPresent;
    RadioGroup rdgpModAllegato;
    RadioButton rdModAllegato;
    RadioButton rdSovrascrivi;
    ImageView mAllegatoLogo;
    TextView mAllegatoNome;
    TextView mAllegatoSize;
    BootstrapButton mAllegato;
    LinearLayout allegato;
    Offerta offerta;
    Commessa commessa = null;
    VolleyRequests mMyRequests;
    DatePickerFragment mDateFragment;
    static final int FILE_CODE = 995;
    File mChoosenFile;
    Gson gson;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_offerta);

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

        offerta = getIntent().getParcelableExtra("OFFERTA");
        commessa = getIntent().getParcelableExtra("COMMESSA");

        mMyRequests = new VolleyRequests(this, this);
        gson = new Gson();

        allegato = (LinearLayout) findViewById(R.id.modifica_offerta_layout_allegato);
        mOffertaCodComm = (EditText) findViewById(R.id.modifica_offerta_codcom);
        mOffertaClient = (EditText) findViewById(R.id.modifica_offerta_cliente);
        mOffertaDataOff = (EditText) findViewById(R.id.modifica_offerta_dataoff);
        mOffertaObj = (EditText) findViewById(R.id.modifica_offerta_oggetto);
        mOffertaPresent = (CheckBox) findViewById(R.id.modifica_offerta_present);
        rdModAllegato = (RadioButton) findViewById(R.id.modifica_offerta_si);
        rdSovrascrivi = (RadioButton) findViewById(R.id.modifica_offerta_sovrascivi);
        mAllegato = (BootstrapButton) findViewById(R.id.modifica_offerta_allegato);
        mAllegatoLogo = (ImageView) findViewById(R.id.mod_off_alleg_logo);
        mAllegatoNome = (TextView) findViewById(R.id.mod_off_alleg_nome);
        mAllegatoSize = (TextView) findViewById(R.id.mod_off_alleg_size);

        Button annulla = (Button) findViewById(R.id.modifica_offerta_annulla_btn);

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button modifica = (Button) findViewById(R.id.modifica_offerta_conferma_btn);

        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptModifica();
            }
        });

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

        mOffertaCodComm.setText(commessa.getCodice_commessa());
        mOffertaCodComm.setEnabled(false);

        mOffertaClient.setText(commessa.getCliente().getNomeSocietà());
        mOffertaClient.setEnabled(false);

        mDateFragment = new DatePickerFragment(mOffertaDataOff);
        mOffertaDataOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateFragment.show(getFragmentManager(), "datePicker");
            }
        });
        mOffertaDataOff.setText(Functions.getFormattedDate(offerta.getDataOfferta()));

        mOffertaObj.setText(commessa.getNome_commessa());
        mOffertaObj.setEnabled(false);

        mOffertaPresent.setChecked(offerta.getAccettata() == 1);
        //mOffertaAlleg.setImageBitmap(AllegatiUtils.getAllegatoLogo(getResources(), offerta.getAllegato()));

        rdgpModAllegato = (RadioGroup) findViewById(R.id.modifica_offerta_group_allegato);
        rdgpModAllegato.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i)
            {
                if (i==R.id.modifica_offerta_si)
                {
                    allegato.setVisibility(View.VISIBLE);
                }
                else
                {
                    allegato.setVisibility(View.GONE);
                }
            }
        });
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
                mAllegatoSize.setText(mChoosenFile.length() + " Bytes");
            }
        }
    }

    private void attemptModifica() {
        boolean cancel = false;
        View focusView = null;

        /* Controllo se la mData è stata selezionata */
        if (TextUtils.isEmpty(mOffertaDataOff.getText().toString())) {
            Toast.makeText(getApplicationContext(), "La data non è stata selezionata: impossibile continuare", Toast.LENGTH_LONG).show();
            focusView = mOffertaDataOff;
            cancel = true;
        }
        /* Controllo allegato scelto */
        else if (rdModAllegato.isChecked()) {
            if (mChoosenFile == null) {
                Toast.makeText(getApplicationContext(), "File non selezionato: scegliere un file", Toast.LENGTH_LONG).show();
                focusView = mAllegato;
                cancel = true;
            }
        }

        if (!cancel) {
            String data = null;
            try
            {
                data = Functions.fromDateToSql(mOffertaDataOff.getText().toString());
            }
            catch (Exception e)
            {
                System.out.println(e);
            }

            String json = "{\"allegato\":\""+(rdModAllegato.isChecked() ? mChoosenFile.getName() : offerta.getAllegato())+"\",\"data_offerta\":\"" + data +"\",\"id_commessa\":" + offerta.getIdCommessa() +",\"accettata\":" + (mOffertaPresent.isChecked() ? 1 : 0) +",\"versione\":" + offerta.getVersione() +
                    ",\"new_version\":" +  (rdSovrascrivi.isChecked() ? 0 : 1)+",\"edit_offerta\":" +  (rdModAllegato.isChecked() ? 1 : 0)+"}";
            System.out.println(json);

            try {
                mMyRequests.addNewElementRequest(json,"offerta-edit");
                if (rdModAllegato.isChecked()) {
                    mMyRequests.uploadFileOfferta(mChoosenFile);
                }
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
