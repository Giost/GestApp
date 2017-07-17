package com.mcteam.gestapp.Moduli.Amministrazione.RubricaBanche;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mcteam.gestapp.Models.Rubrica.Banca;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.VolleyRequests;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

public class VisualizzaBancaActivity extends AppCompatActivity {

    private static final int MODIFY_REQUEST = 432;
    TextView mNominativoView;
    TextView mIbanView;
    TextView mIndirizzoView;
    TextView mReferenteView;
    Banca mBancaAttuale;
    VolleyRequests mVolleyRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_banca);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                actionBarBack = getDrawable(R.drawable.actionbar_amministrazione);
            } else {
                actionBarBack = getResources().getDrawable(R.drawable.actionbar_amministrazione);
            }
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        mVolleyRequest = new VolleyRequests(this, this);

        mBancaAttuale = getIntent().getParcelableExtra("bancaToView");

        mNominativoView = (TextView) findViewById(R.id.visualizza_banca_nominativo);
        mIbanView = (TextView) findViewById(R.id.visualizza_banca_iban);
        mIndirizzoView = (TextView) findViewById(R.id.visualizza_banca_indirizzo);
        mReferenteView = (TextView) findViewById(R.id.visualizza_banca_referente);


        BootstrapButton modifica = (BootstrapButton) findViewById(R.id.visualizza_banca_modifica_button);
        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modificaIntent = new Intent(getApplicationContext(), NuovaBancaActivity.class);
                modificaIntent.putExtra("bancaToModify", mBancaAttuale);
                startActivityForResult(modificaIntent, MODIFY_REQUEST);
            }
        });


        BootstrapButton elimina = (BootstrapButton) findViewById(R.id.visualizza_banca_elimina_button);
        elimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVolleyRequest.deleteElement("banca/" + mBancaAttuale.getId_banca());
            }
        });

        BootstrapButton stampa = (BootstrapButton) findViewById(R.id.visualizza_banca_stampa_button);
        stampa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BancaUtils.print(mBancaAttuale, getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        setupView(mBancaAttuale);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MODIFY_REQUEST) {
            if (RESULT_OK == resultCode) {
                finish();
            }
        }
    }

    void setupView(Banca banca) {

        mNominativoView.setText(banca.getNome());
        mIbanView.setText(banca.getIban());
        mIndirizzoView.setText(banca.getIndirizzo());
        mReferenteView.setText(banca.getReferente());

    }

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
