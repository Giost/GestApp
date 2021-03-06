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
import android.widget.ProgressBar;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.VolleyRequests;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.ComparatorPool;
import com.mcteam.gestapp.Utils.Functions;

import java.util.ArrayList;
import java.util.Collections;

public class OfferteActivity extends AppCompatActivity {

    private ArrayList<Commessa> mCommArrList;
    private ArrayList<Commessa> mCommArrListO;
    private RecyclerView mOffRecyclerView;
    private OfferteAdapter mAdapter;
    private VolleyRequests mVolleyRequests;
    private ProgressBar mProgressBar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offerte);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.commerciale_home_background);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        mOffRecyclerView = (RecyclerView) findViewById(R.id.offerte_comm_recycler);
        mOffRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mVolleyRequests = new VolleyRequests(this, this);
        mCommArrList = new ArrayList<>();
        mCommArrListO = new ArrayList<>();
        mAdapter = new OfferteAdapter(mCommArrList, new OfferteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Commessa item) {
                Intent i = new Intent(getApplicationContext(), DettaglioOffertaActivity.class);
                i.putExtra("COMMESSA", item);
                startActivity(i);
            }
        });

        mOffRecyclerView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.offerte_progress);

        FloatingActionButton ricercaAvanzata = (FloatingActionButton) findViewById(R.id.fab_offerta_ricerca_avanzata);
        ricercaAvanzata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent advancedSearch = new Intent(getApplicationContext(), OfferteAdvanceSearch.class);
                    advancedSearch.putParcelableArrayListExtra("COMMESSE", mCommArrList);
                    startActivity(advancedSearch);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mVolleyRequests.getCommesseList();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mVolleyRequests.getCommesseList();
    }

    // FromVolleyRequest indica se la richiesta di aggiornamento lista è stata fatta dopo una richiesta al db (Volley)
    public void updateList(ArrayList<Commessa> list, boolean fromVolleyRequest) {
        showProgress(false);
        Collections.sort(list, ComparatorPool.getCommessaComparator());
        if (!mCommArrList.equals(list))
        {
            mCommArrList.clear();
            mCommArrList.addAll(list);
            mAdapter.notifyDataSetChanged();
            mAdapter.clearAlphabeticIndex();
            mOffRecyclerView.scrollToPosition(0);
            if (fromVolleyRequest) //Aggiorna solo se richiesta fatta da una volley
                mCommArrListO.addAll(list);
        }
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
        ArrayList<Commessa> matchingElement = new ArrayList<>();

        if (!TextUtils.isEmpty(query)) {
            query = query.toUpperCase();

            for (Commessa commessa : mCommArrListO) {

                String cliente = "";
                if (commessa.getCliente() != null && !TextUtils.isEmpty(commessa.getCliente().getNomeSocietà())) {
                    cliente = commessa.getCliente().getNomeSocietà();
                }

                String nomeRisorsa = "", cognomeRisorsa = "";
                if (commessa.getCommerciale() != null) {
                    cognomeRisorsa = commessa.getCommerciale().getCognome();
                    nomeRisorsa = commessa.getCommerciale().getNome();
                }

                String nomeCommessa = "";
                if (commessa.getNome_commessa() != null && !TextUtils.isEmpty(commessa.getNome_commessa())) {
                    nomeCommessa = commessa.getNome_commessa();
                }

                if (nomeCommessa.toUpperCase().contains(query) || cliente.toUpperCase().contains(query) || nomeRisorsa.toUpperCase().contains(query) || cognomeRisorsa.toUpperCase().contains(query)) {
                    matchingElement.add(commessa);
                }
            }

            updateList(matchingElement, false);
        } else
            updateList(mCommArrListO, false);

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

    private void showProgress(boolean show) {
        if (show) {
            mOffRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mOffRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

}
