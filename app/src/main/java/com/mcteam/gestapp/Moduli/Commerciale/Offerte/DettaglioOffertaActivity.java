package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.mcteam.gestapp.Models.Commerciale.Offerta;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DettaglioOffertaActivity extends AppCompatActivity {

    private ArrayList<Offerta> mOffArrayList;
    private RecyclerView mOffRecyclerView;
    private DettaglioOffertaAdapter mOffAdapter;
    private Commessa mCommessa;
    private View overlay;
    private FloatingActionsMenu fabMenu;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio_offerta);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.commerciale_home_background);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }


        overlay = findViewById(R.id.offerta_overlay);

        fabMenu = (FloatingActionsMenu) findViewById(R.id.fabmenu_offerta);
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                overlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                overlay.setVisibility(View.GONE);
            }
        });

        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.collapse();
            }
        });

        FloatingActionButton exportaExcel = (FloatingActionButton) findViewById(R.id.fab_offerta_excel);
        exportaExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //esportaExcel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        FloatingActionButton printAll = (FloatingActionButton) findViewById(R.id.fab_offerta_print);
        printAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OfferteUtils.printAll(mOffArrayList,mCommessa,getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton ricercaAvanzata = (FloatingActionButton) findViewById(R.id.fab_offerta_search);
        ricercaAvanzata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    /*Intent advancedSearch = new Intent(getApplicationContext(), SocietaAdvanceSearchActivity.class);
                    advancedSearch.putParcelableArrayListExtra("listaSocieta", mRubricaSocieta);
                    startActivity(advancedSearch);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        mCommessa = getIntent().getParcelableExtra("COMMESSA");

        final Gson gson = new Gson();
        mOffArrayList = new ArrayList<>();
        mOffAdapter = new DettaglioOffertaAdapter(mOffArrayList, mCommessa);
        mOffRecyclerView = (RecyclerView) findViewById(R.id.offerte_recycler);
        mOffRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOffRecyclerView.setAdapter(mOffAdapter);

        String url = getString(R.string.mobile_url) + "offerte-list/" + mCommessa.getID();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Offerta> newList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Offerta offerta = gson.fromJson(obj.toString(), Offerta.class);
                                //System.out.println(offerta);
                                newList.add(offerta);
                            } catch (JSONException e) {
                                System.out.println("Something went wrong during deserialization!");
                                e.printStackTrace();
                            }
                        }
                        updateList(newList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Something went wrong!");
                        error.printStackTrace();
                    }
                }
        );

        Volley.newRequestQueue(this).add(jsonObjectRequest);

        setupHeaderCommessa(mCommessa);
    }

    public void updateList(ArrayList<Offerta> newList) {
        if (newList.isEmpty())
            emptyMode(true);
        else {
            emptyMode(false);
            mOffArrayList.addAll(newList);
            mOffAdapter.notifyDataSetChanged();
        }
    }

    private void emptyMode(boolean enabled) {
        LinearLayout emptyLayout = (LinearLayout) findViewById(R.id.dettaglio_offerta_empty);
        /*FloatingActionButton fabSearch = (FloatingActionButton) findViewById(R.id.fab_offerta_search);
        FloatingActionButton fabPrint = (FloatingActionButton) findViewById(R.id.fab_offerta_print);
        FloatingActionButton fabExcel = (FloatingActionButton) findViewById(R.id.fab_offerta_excel);*/
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_offerta_add);
        LinearLayout fieldsLayout = (LinearLayout) findViewById(R.id.dettaglio_offerta_fields);

        if (enabled) {
            emptyLayout.setVisibility(View.VISIBLE);
            fabAdd.setVisibility(View.VISIBLE);
        } else {
            fabMenu.setVisibility(View.VISIBLE);
            /*fabSearch.setVisibility(View.VISIBLE);
            fabPrint.setVisibility(View.VISIBLE);
            fabExcel.setVisibility(View.VISIBLE);*/
        }

    }

    private void setupHeaderCommessa(Commessa commessa) {
        TextView codCommessa = (TextView) findViewById(R.id.dett_off_head_codcomm);
        TextView nomeComm = (TextView) findViewById(R.id.dett_off_head_nomecomm);
        TextView cliente = (TextView) findViewById(R.id.dett_off_head_cliente);
        TextView refCommessa = (TextView) findViewById(R.id.dett_off_head_refcomm);
        TextView consulente = (TextView) findViewById(R.id.dett_off_head_consul);

        codCommessa.setText(commessa.getCodice_commessa());
        nomeComm.setText(commessa.getNome_commessa());
        cliente.setText(commessa.getCliente().getNomeSocietà());

        String nomeReferente = commessa.getReferente1() == null ? "" : commessa.getReferente1().getNome();
        String cognomeReferente = commessa.getReferente1() == null ? "" : commessa.getReferente1().getCognome();
        refCommessa.setText(nomeReferente + " " + cognomeReferente);

        String nomeConsulente = commessa.getCommerciale() == null ? "" : commessa.getCommerciale().getNome();
        String cognomeConsulente = commessa.getCommerciale() == null ? "" : commessa.getCommerciale().getCognome();
        consulente.setText(nomeConsulente + " " + cognomeConsulente);
    }

    public void onClickAddOfferta(View view) {
        Intent intent = new Intent(getApplicationContext(), NuovaOffertaActivity.class);
        intent.putExtra("COMMESSA", mCommessa);
        startActivity(intent);
    }
}
