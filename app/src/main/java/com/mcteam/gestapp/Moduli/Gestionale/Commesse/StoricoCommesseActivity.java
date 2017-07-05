package com.mcteam.gestapp.Moduli.Gestionale.Commesse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.itextpdf.text.DocumentException;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.CustomRequest;
import com.mcteam.gestapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StoricoCommesseActivity extends AppCompatActivity {

    ArrayList<Commessa> mCommesseList;
    RequestQueue mRequestQueue;
    ListView mCommesseListView;
    ProgressBar mProgressBar;
    CommesseListAdapter mCommesseListAdapter;
    ArrayList<Commessa> mOriginalList;
    View mOverlay;

    Comparator<Commessa> commessaComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storico_commesse);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //***********************************************************************
        //Cambiare colore alla actionBar
        //************************************************************************
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            }
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        //***********************************************************************
        //Inizializzazione delle variabili
        //************************************************************************
        mRequestQueue = Volley.newRequestQueue(this);
        mCommesseList = new ArrayList<>();

        //***********************************************************************
        //Comparatore per ordinare la lista
        //************************************************************************
        commessaComparator = new Comparator<Commessa>() {
            @Override
            public int compare(Commessa lhs, Commessa rhs) {
                String lhsNomeSocieta = null;
                String rhsNomeSocieta = null;

                if (lhs.getCliente() != null && lhs.getCliente().getNomeSocietà() != null)
                    lhsNomeSocieta = lhs.getCliente().getNomeSocietà();
                if (rhs.getCliente() != null && rhs.getCliente().getNomeSocietà() != null)
                    rhsNomeSocieta = rhs.getCliente().getNomeSocietà();

                if (rhsNomeSocieta != null && lhsNomeSocieta != null)
                    return String.CASE_INSENSITIVE_ORDER.compare(lhsNomeSocieta, rhsNomeSocieta);
                if (lhsNomeSocieta != null && rhsNomeSocieta == null) {
                    return 2;
                } else
                    return -1;
            }
        };

        //***********************************************************************
        //Inizializzazione della view
        //************************************************************************
        mCommesseListView = (ListView) findViewById(R.id.storico_commesse_list);
        mCommesseListAdapter = new CommesseListAdapter(this, mCommesseList, true);

        mProgressBar = (ProgressBar) findViewById(R.id.storico_commesse_progress_bar);
        mOverlay = findViewById(R.id.storico_commesse_overlay_transparent);
        final FloatingActionsMenu menu = (FloatingActionsMenu) findViewById(R.id.storico_commesse_menu);


        mCommesseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Commessa commessa = (Commessa) parent.getItemAtPosition(position);
                Intent visualizzaCommessa = new Intent(getApplicationContext(), VisualizzaCommessaActivity.class);
                visualizzaCommessa.putExtra("commessaToView", commessa);
                visualizzaCommessa.putExtra("isStorico", true);
                startActivity(visualizzaCommessa);
            }
        });

        menu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                mOverlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                mOverlay.setVisibility(View.GONE);
            }
        });

        mOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.collapse();
                mOverlay.setVisibility(View.GONE);
            }
        });

        mCommesseListView.setAdapter(mCommesseListAdapter);

        getRubricaList();

        FloatingActionButton printAll = (FloatingActionButton) findViewById(R.id.fab_storico_commesse_stampa_tutto);
        printAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CommesseUtils.printAll(mCommesseList, getApplicationContext());
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


        final FloatingActionButton ricercaAvanzata = (FloatingActionButton) findViewById(R.id.fab_storico_commesse_ricerca_avantazata);
        ricercaAvanzata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ricercaAvanzataIntent = new Intent(getApplicationContext(), RicercaAvanzataCommesseActivity.class);
                ricercaAvanzataIntent.putParcelableArrayListExtra("commesseList", mCommesseList);
                startActivity(ricercaAvanzataIntent);
            }
        });

        FloatingActionButton esportaExcel = (FloatingActionButton) findViewById(R.id.fab_storico_commesse_esporta_excel);
        esportaExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CommesseUtils.esportaExcel(mCommesseList, getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }
    /**
     * Metodo per la richiesta al database dei record presenti nella tabella "commesse storico"
     */
    public void getRubricaList() {
        String url = getString(R.string.mobile_url);
        url += "storico-commesse";

        CustomRequest accessiRequest = new CustomRequest(url, null, new RubricaResponse(), new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Errore richiesta Volley", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        });

        mRequestQueue.add(accessiRequest);
    }
    /**
     * Classe che implementa il Response listener, cioè viene richiamata quando viene ricevuta una risposta ad una
     * richiesta fatta precedentemente, nel nostro caso, al database
     */
    public class RubricaResponse implements Response.Listener<JSONArray> {

        Gson gson = new Gson();

        @Override
        public void onResponse(JSONArray responseArray) {
            try {
                ArrayList<Commessa> commesse = new ArrayList<>();

                Log.i("Commesse.class", " " + responseArray.length());

                for (int i = 0; i < responseArray.length(); i++) {

                    JSONObject response = responseArray.getJSONObject(i);


                    Commessa commessa = gson.fromJson(response.toString(), Commessa.class);

                    commesse.add(commessa);
                }
                updateList(commesse);
                mOriginalList = commesse;
            } catch (JSONException e) {

                e.printStackTrace();
                Log.i("LoginResponse", e.getMessage());

                Toast.makeText(getApplicationContext(),
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    /**
     * Metodo richiamato ad ogni caricamento della ListView ed in ogni caso in cui la lista degli elementi da
     * visualizzare cambia
     */
    private void updateList(ArrayList<Commessa> list) {
        showProgress(false);
        Collections.sort(list, commessaComparator);
        mCommesseList.clear();
        mCommesseList.addAll(list);
        mCommesseListAdapter.notifyDataSetChanged();
    }
    /**
     * Metodo per la gestione della rotella di caricamento
     */
    private void showProgress(boolean show) {
        if (show) {
            mCommesseListView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mCommesseListView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRubricaList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_societa, menu);

        MenuItem searchItem = menu.findItem(R.id.action_ricerca_semplice);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                simpleSearch(text);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                simpleSearch(text);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                goHome();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        Intent goLogin = new Intent(this, LoginActivity.class);
        goLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goLogin);
        finish();
    }

    private void goHome() {
        Intent goHome = new Intent(this, HomeActivity.class);
        goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goHome);
        finish();
    }
    /**
     * Metodo per la ricerca della query passata tra gli elementi
     */
    private void simpleSearch(String query) {
        ArrayList<Commessa> matchingElement = new ArrayList<>();
        if (!TextUtils.isEmpty(query)) {
            query = query.toUpperCase();

            for (Commessa commessa : mOriginalList) {

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
            updateList(matchingElement);
        } else
            updateList(mOriginalList);
    }

}
