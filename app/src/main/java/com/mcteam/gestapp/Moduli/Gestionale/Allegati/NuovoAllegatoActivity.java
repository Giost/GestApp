package com.mcteam.gestapp.Moduli.Gestionale.Allegati;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.VolleyRequests;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.io.IOException;

public class NuovoAllegatoActivity extends AppCompatActivity {

    private static final int FILE_CODE = 992;
    EditText mDescrizioneView;
    BootstrapButton mFileChooserButton;
    BootstrapButton mCreaButton;
    BootstrapButton mAnnullaButton;
    TextView mFileNameView;
    TextView mFileSizeView;
    ImageView mLogoView;
    VolleyRequests mMyRequests;
    File choosenFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovo_allegato);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mDescrizioneView = (EditText) findViewById(R.id.nuovo_allegato_descrizione);
        mFileChooserButton = (BootstrapButton) findViewById(R.id.nuovo_allegato_scegli_file);
        mCreaButton = (BootstrapButton) findViewById(R.id.nuovo_allegato_crea_nuovo);
        mAnnullaButton = (BootstrapButton) findViewById(R.id.nuovo_allegato_annulla);
        mFileNameView = (TextView) findViewById(R.id.nuovo_allegato_nome_file);
        mFileSizeView = (TextView) findViewById(R.id.nuovo_allegato_dimensione);
        mLogoView = (ImageView) findViewById(R.id.nuovo_allegato_logo);
        mMyRequests = new VolleyRequests(this, this);

        mFileChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        mCreaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        mAnnullaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void uploadFile() {

        String descrizione = mDescrizioneView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(descrizione)) {
            mDescrizioneView.setError("Campo richiesto");
            focusView = mDescrizioneView;
            cancel = true;
        }
        if (choosenFile == null) {
            Toast.makeText(getApplicationContext(), "File non selezionato: scegliere un file", Toast.LENGTH_LONG).show();
            cancel = true;
        }

        if (!cancel) {
            try {
                mMyRequests.uploadFile(choosenFile, descrizione);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            focusView.requestFocus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                choosenFile = new File(uri.getPath());
                if (choosenFile != null) {
                    mFileNameView.setText(choosenFile.getName());
                    Bitmap logo = AllegatiUtils.getAllegatoLogo(getResources(), choosenFile.getName());
                    mLogoView.setImageBitmap(logo);
                    mFileSizeView.setText(choosenFile.length() + " Bytes");
                }
            }
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
