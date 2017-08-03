package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mcteam.gestapp.Models.Commerciale.Offerta;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Moduli.Gestionale.Allegati.AllegatiUtils;
import com.mcteam.gestapp.NetworkReq.PUTRequest;
import com.mcteam.gestapp.NetworkReq.VolleyRequests;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Created by Lorenzo Sacchetti on 20/07/2017.
 */
public class EliminaOffertaActivity extends AppCompatActivity {

    TextView textOffertaCodComm;
    TextView textOffertaClient;
    TextView textOffertaRef1;
    TextView textOffertaRef2;
    TextView textOffertaRef3;
    TextView textOffertaDataOff;
    TextView textOffertaObj; //E' il nome della commessa o cosa?
    CheckBox textOffertaPresent;
    ImageView textOffertaAlleg;
    AlertDialog.Builder deleteDialogBuilder;
    Offerta offerta;
    Commessa commessa = null;
    VolleyRequests mMyRequests;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elimina_offerta);

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

        textOffertaCodComm = (TextView) findViewById(R.id.visual_offerta_codcomm);
        textOffertaClient = (TextView) findViewById(R.id.visual_offerta_cliente);
        textOffertaRef1 = (TextView) findViewById(R.id.visual_offerta_ref1);
        textOffertaRef2 = (TextView) findViewById(R.id.visual_offerta_ref2);
        textOffertaRef3 = (TextView) findViewById(R.id.visual_offerta_ref3);
        textOffertaDataOff = (TextView) findViewById(R.id.visual_offerta_dataoff);
        textOffertaObj = (TextView) findViewById(R.id.visual_offerta_oggetto);
        textOffertaPresent = (CheckBox) findViewById(R.id.visual_offerta_present);
        textOffertaAlleg = (ImageView) findViewById(R.id.visual_offerta_allegato);

        Button annulla = (Button) findViewById(R.id.elimina_offerta_annulla);

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button elimina = (Button) findViewById(R.id.elimina_offerta_elimina);

        elimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptDelete();
            }
        });


        textOffertaCodComm.setText(commessa.getCodice_commessa());
        textOffertaClient.setText(commessa.getCliente().getNomeSocietà());
        textOffertaRef1.setText(commessa.getCommerciale().getNome() + commessa.getCommerciale().getCognome());
        textOffertaRef2.setText(commessa.getReferente1().getNome() + commessa.getReferente1().getCognome());
        textOffertaRef3.setText(commessa.getReferente2().getNome() + commessa.getReferente2().getCognome());
        textOffertaDataOff.setText(offerta.getDataOfferta());
        textOffertaObj.setText(commessa.getNome_commessa()); //Non so principalmente che cosa settare, dal doc non si capisce
        textOffertaPresent.setChecked(offerta.getAccettata() == 1);
        textOffertaAlleg.setImageBitmap(AllegatiUtils.getAllegatoLogo(getResources(), offerta.getAllegato()));

    }

    private void attemptDelete()
    {
        mMyRequests.deleteElement("offerta-delete/"+offerta.getIdCommessa()+"/"+offerta.getVersione());
        deleteAllegato();
    }

    private void deleteAllegato()
    {
        String url = getString(R.string.mobile_url)+"offerta-allegato-delete/"+offerta.getIdCommessa()+"/"+offerta.getVersione();

        PUTRequest deleteRequestJson = new PUTRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean errore = response.getBoolean("error");
                    if (errore) {
                        try {
                            String error_type = response.getString("error_type");
                            System.out.println(error_type);

                        } catch (JSONException e) {
                            //showError(true);
                        }
                    } else {
                        //showError(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //showError(true);
            }
        });
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(deleteRequestJson);
    }

    /*public void showError(boolean error)
    {

        deleteDialogBuilder = new AlertDialog.Builder(this);
        if (error) {
            deleteDialogBuilder.setTitle("Errore eliminazione dati");
            deleteDialogBuilder.setMessage("Si è verificato un errore nella modifica dei dati");
            deleteDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog dialog = deleteDialogBuilder.create();
            dialog.show();
        } else {
            deleteDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    return;
                }
            });
            AlertDialog dialog = deleteDialogBuilder.create();
            dialog.setTitle("Successo!");
            dialog.setMessage("Eliminazione dati avvenuta con successo");
            dialog.show();
        }
    }*/

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
