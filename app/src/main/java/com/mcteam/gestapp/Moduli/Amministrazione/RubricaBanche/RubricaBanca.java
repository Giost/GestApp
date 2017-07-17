package com.mcteam.gestapp.Moduli.Amministrazione.RubricaBanche;

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
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.mcteam.gestapp.Models.Rubrica.Banca;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.CustomRequest;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RubricaBanca extends AppCompatActivity {

    ListView mBancheListView;
    ArrayList<Banca> mRubricaBancheList;
    RubricaBancaListAdapter mBancaAdapter;

    ArrayList<Banca> mOriginalList;
    private ProgressBar mProgressBar;
    private View mOverlay;
    FloatingActionsMenu mMenu;

    private RequestQueue mRequestQueue;

    Comparator<Banca> bancaComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rubrica_banca);

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
                actionBarBack = getDrawable(R.drawable.actionbar_amministrazione);
            }
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        //*************************************************
        //  View Inizialization
        //*************************************************

        mBancheListView = (ListView) findViewById(R.id.rubrica_banca_list);
        mProgressBar = (ProgressBar) findViewById(R.id.rubrica_banca_progress);
        mOverlay = findViewById(R.id.banca_overlay);
        mMenu = (FloatingActionsMenu) findViewById(R.id.banca_menu);


        //*************************************************
        //  Global variable Inizialization
        //*************************************************
        mRubricaBancheList = new ArrayList<>();
        mBancaAdapter = new RubricaBancaListAdapter(this, mRubricaBancheList);
        mRequestQueue = Volley.newRequestQueue(this);
        mBancheListView.setAdapter(mBancaAdapter);

        mMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
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
                mMenu.collapse();
            }
        });

        //***********************************************************************
        //Comparatore per nome per ordinare la lista
        //************************************************************************

        bancaComparator = new Comparator<Banca>() {
            @Override
            public int compare(Banca lhs, Banca rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.getNome(), rhs.getNome());
            }
        };

        mBancheListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Banca bancaSelected = (Banca) parent.getItemAtPosition(position);
                Intent apriBanca = new Intent(getApplicationContext(), VisualizzaBancaActivity.class);
                apriBanca.putExtra("bancaToView", bancaSelected);
                startActivity(apriBanca);
            }
        });

        AddFloatingActionButton addNewBanca = (AddFloatingActionButton) findViewById(R.id.fab_banca_add);
        addNewBanca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewBancaIntent = new Intent(getApplicationContext(), NuovaBancaActivity.class);
                startActivity(addNewBancaIntent);
            }
        });

        FloatingActionButton stampaTutto = (FloatingActionButton) findViewById(R.id.fab_banca_stampa_tutto);
        stampaTutto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BancaUtils.printAll(mOriginalList, getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton ricercaAvanzata = (FloatingActionButton) findViewById(R.id.fab_banca_ricerca_avantazata);
        ricercaAvanzata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cercaAvanzataIntent = new Intent(getApplicationContext(), RicercaAvanzataBanca.class);
                cercaAvanzataIntent.putParcelableArrayListExtra("listaBanche", mOriginalList);
                startActivity(cercaAvanzataIntent);
            }
        });

        FloatingActionButton esportaExcel = (FloatingActionButton) findViewById(R.id.fab_banca_esporta_excel);
        esportaExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BancaUtils.esportaExcel(mOriginalList, getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        getBancheList();

    }
    /**
     * Metodo per la richiesta al database dei record presenti nella tabella "banche"
     */
    void getBancheList() {
        String url = getString(R.string.mobile_url);
        url += "banche";

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
                ArrayList<Banca> banche = new ArrayList<>();

                Log.i("Commesse.class", " " + responseArray.length());

                for (int i = 0; i < responseArray.length(); i++) {

                    JSONObject response = responseArray.getJSONObject(i);

                    Banca banca = gson.fromJson(response.toString(), Banca.class);
                    System.out.println("Risposta -------> " + response);

                    banche.add(banca);
                }
                updateList(banche);
                mOriginalList = banche;
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
    private void updateList(ArrayList<Banca> list) {
        showProgress(false);
        Collections.sort(list, bancaComparator);
        if (!mBancaAdapter.equals(list))
        {
            mBancaAdapter.clear();
            mBancaAdapter.addAll(list);
            mBancaAdapter.cleanAlphabeticIndex();
            mBancaAdapter.notifyDataSetChanged();
        }
    }
    /**
     * Metodo per la gestione della rotella di caricamento
     */
    private void showProgress(boolean show) {
        if (show) {
            mBancheListView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mBancheListView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMenu.collapse();
        getBancheList();
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
    /**
     * Metodo per la ricerca della query passata tra gli elementi
     */
    private void simpleSearch(String query) {

        ArrayList<Banca> matchingElement = new ArrayList<>();
        if (!TextUtils.isEmpty(query)) {
            query = query.toUpperCase();

            for (Banca banca : mOriginalList) {

                if (banca.getNome().toUpperCase().contains(query)) {
                    matchingElement.add(banca);
                }

            }
            updateList(matchingElement);
        } else
            updateList(mOriginalList);
    }

}
