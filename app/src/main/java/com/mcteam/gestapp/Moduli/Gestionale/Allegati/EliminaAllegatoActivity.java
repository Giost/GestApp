package com.mcteam.gestapp.Moduli.Gestionale.Allegati;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mcteam.gestapp.Models.Allegato;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.VolleyRequests;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

public class EliminaAllegatoActivity extends AppCompatActivity {

    TextView mDescrizioneView;
    TextView mNomeView;
    Allegato mAllegatoAttuale;
    ImageView mLogo;
    BootstrapButton eliminaButton;
    BootstrapButton annullaButton;
    VolleyRequests mCustomRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elimina_allegato);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mDescrizioneView = (TextView) findViewById(R.id.elimina_allegato_descrizione);
        mNomeView = (TextView) findViewById(R.id.elimina_allegato_nome);
        mLogo = (ImageView) findViewById(R.id.elimina_allegato_logo);

        mAllegatoAttuale = getIntent().getParcelableExtra("allegatoToDelete");

        eliminaButton = (BootstrapButton) findViewById(R.id.elimina_allegato_bootstrap);
        annullaButton = (BootstrapButton) findViewById(R.id.elimina_allegato_annulla);

        mCustomRequests = new VolleyRequests(this, this);

        eliminaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomRequests.deleteElement("allegato/" + mAllegatoAttuale.getId());
            }
        });

        annullaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setupView(mAllegatoAttuale);

    }

    private void setupView(Allegato allegato) {
        mDescrizioneView.setText(allegato.getDescrizione());
        mNomeView.setText(allegato.getFile());
        Bitmap logo = AllegatiUtils.getAllegatoLogo(getResources(), allegato.getFile());
        mLogo.setImageBitmap(logo);
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
