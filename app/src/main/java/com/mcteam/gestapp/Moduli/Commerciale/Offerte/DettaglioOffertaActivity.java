package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DettaglioOffertaActivity extends AppCompatActivity {

    private ArrayList<Offerta> mOffArrayList;
    private ArrayList<Offerta> mSearchList;
    private RecyclerView mOffRecyclerView;
    private DettaglioOffertaAdapter mOffAdapter;
    private Commessa mCommessa;
    private View overlay;
    private FloatingActionsMenu fabMenu;
    private ProgressBar mProgressBar;
    private final Gson gson = new Gson();

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
                    OfferteUtils.esportaExcel(mOffArrayList,mCommessa,getApplicationContext());
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
                    Intent advancedSearch = new Intent(getApplicationContext(), DettaglioOffertaAdvanceSearch.class);
                    advancedSearch.putParcelableArrayListExtra("OFFERTE", mOffArrayList);
                    advancedSearch.putExtra("COMMESSA", mCommessa);
                    startActivity(advancedSearch);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.dettaglio_offerte_progress);

        mCommessa = getIntent().getParcelableExtra("COMMESSA");

        mOffArrayList = new ArrayList<>();
        mOffAdapter = new DettaglioOffertaAdapter(mOffArrayList, mCommessa);
        mOffRecyclerView = (RecyclerView) findViewById(R.id.offerte_recycler);
        mOffRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOffRecyclerView.setAdapter(mOffAdapter);

        getOfferte();

        setupHeaderCommessa(mCommessa);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_societa, menu);

        MenuItem searchItem = menu.findItem(R.id.action_ricerca_semplice);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                simpleSearch(newText);
                return true;
            }
        });

        return true;
    }

    private void simpleSearch(String query) {
        ArrayList<Offerta> matchingElement = new ArrayList<>();

        if (!TextUtils.isEmpty(query)) {
            //query = query.toUpperCase();

            for (Offerta offerta : mSearchList) {

                String dataOfferta = "";
                if (offerta.getDataOfferta() != null && !TextUtils.isEmpty(offerta.getDataOfferta())) {
                    dataOfferta = offerta.getDataOfferta();
                }

                String versione = String.valueOf(offerta.getVersione());

                String presentata = String.valueOf(offerta.getAccettata());

                if (dataOfferta.contains(query) || versione.contains(query) || presentata.contains(query)) {
                    matchingElement.add(offerta);
                }
            }
            updateList(matchingElement);
        } else
            updateList(mSearchList);

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

    /*private void logout() {
        ((MyApp) this.getApplication()).setCurrentUser(null); //Remove user in MyApp
        Intent goLogin = new Intent(this, LoginActivity.class);
        goLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goLogin);
        finish();
    }*/

    private void goHome() {
        Intent goHome = new Intent(this, HomeActivity.class);
        goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goHome);
        finish();
    }

    /**
     * Metodo richiamato per inizializzare la vista in base alla presenza di offerte
     * Se non ci sono offerte viene visualizzato un messaggio ed il FloatingButton permette di crearne una nuova
     * Se ci sono offerte, vengono visualizzate (riempiendo la lista mOffArrayList)
     * @param newList
     */
    public void initializeList(ArrayList<Offerta> newList) {
        showProgress(false);
        if (newList.isEmpty())
            emptyMode(true);
        else {
            emptyMode(false);
        }
        updateList(newList);
    }

    /**
     * Metodo richiamato ad ogni caricamento della ListView ed in ogni caso in cui la lista degli elementi da
     * visualizzare cambia
     */
    private void updateList(ArrayList<Offerta> list) {
        showProgress(false);
        if (!mOffArrayList.equals(list))
        {
            mOffArrayList.clear();
            mOffArrayList.addAll(list);
            mOffAdapter.notifyDataSetChanged();
            mOffRecyclerView.scrollToPosition(0);
        }
    }

    private void emptyMode(boolean enabled) {
        LinearLayout emptyLayout = (LinearLayout) findViewById(R.id.dettaglio_offerta_empty);
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_offerta_add);
        LinearLayout fieldsLayout = (LinearLayout) findViewById(R.id.dettaglio_offerta_fields);

        if (enabled) {
            fieldsLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            fabAdd.setVisibility(View.VISIBLE);
            fabMenu.setVisibility(View.GONE);
        } else {
            fieldsLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            fabAdd.setVisibility(View.GONE);
            fabMenu.setVisibility(View.VISIBLE);
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

    private void showProgress(boolean show) {
        if (show) {
            mOffRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mOffRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void getOfferte()
    {
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
                        mSearchList=newList;
                        initializeList(newList);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        fabMenu.collapse();
        getOfferte();
    }
}
