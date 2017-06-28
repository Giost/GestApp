package com.mcteam.gestapp.Moduli.Gestionale.Associazioni;

import android.annotation.TargetApi;
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
import com.mcteam.gestapp.Models.Associazione;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.CustomRequest;
import com.mcteam.gestapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AssociazioniActivity extends AppCompatActivity {

    ListView mAssociazioniListView;
    AssociazioniListAdapter mListAdapeter;
    ArrayList<Associazione> mAssociazioniList;
    ArrayList<Associazione> mOriginalList;
    RequestQueue mRequestQueue;
    ProgressBar mProgressBar;
    Gson gson;
    View mOverlay;
    FloatingActionsMenu menu;

    Comparator<Associazione> associazioniComparator = new Comparator<Associazione>() {
        @Override
        public int compare(Associazione lhs, Associazione rhs) {
            String rNomeSocieta = null;
            String lNomeSocieta = null;

            if (lhs.getCommessa() != null && lhs.getCommessa().getCliente() != null && lhs.getCommessa().getCliente().getNomeSocietà() != null)
                lNomeSocieta = lhs.getCommessa().getCliente().getNomeSocietà();
            if (rhs.getCommessa() != null && rhs.getCommessa().getCliente() != null && rhs.getCommessa().getCliente().getNomeSocietà() != null)
                rNomeSocieta = rhs.getCommessa().getCliente().getNomeSocietà();

            if (rNomeSocieta != null && lNomeSocieta != null)
                return String.CASE_INSENSITIVE_ORDER.compare(lNomeSocieta, rNomeSocieta);
            if (lNomeSocieta != null && rNomeSocieta == null) {
                return -1;
            } else
                return 2;
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associazioni);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //***********************************************************************
        //Cambiare colore alla actionBar
        //************************************************************************
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        mAssociazioniListView = (ListView) findViewById(R.id.associazioni_list);
        mAssociazioniList = new ArrayList<>();
        mListAdapeter = new AssociazioniListAdapter(this, mAssociazioniList);
        mProgressBar = (ProgressBar) findViewById(R.id.assocazioni_progress_bar);
        mOverlay = findViewById(R.id.associazioni_overlay_transparent);
        mOriginalList = new ArrayList<>();

        mAssociazioniListView.setAdapter(mListAdapeter);

        mRequestQueue = Volley.newRequestQueue(this);

        gson = new Gson();

        mAssociazioniListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Associazione ass = (Associazione) parent.getItemAtPosition(position);
                Intent visualizzaAss = new Intent(getApplicationContext(), VisualizzaAssociazioneActivity.class);
                visualizzaAss.putExtra("associazioneToView", ass);
                startActivity(visualizzaAss);
            }
        });

        FloatingActionButton addNew = (FloatingActionButton) findViewById(R.id.fab_associazioni_aggiungi_nuovo);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewIntent = new Intent(getApplicationContext(), NuovaAssociazioneActivity.class);
                startActivity(addNewIntent);
            }
        });

        FloatingActionButton printAll = (FloatingActionButton) findViewById(R.id.fab_associazioni_stampa_tutto);
        printAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AssociazioniTools.printAll(mAssociazioniList, getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton esporta = (FloatingActionButton) findViewById(R.id.fab_associazioni_esporta_excel);
        esporta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AssociazioniTools.esportaExcel(mOriginalList, getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton ricercaAvanzata = (FloatingActionButton) findViewById(R.id.fab_associazioni_ricerca_avantazata);
        ricercaAvanzata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ricercaAvanzata = new Intent(getApplicationContext(), AssociazioniRicercaAvanzataActivity.class);
                ricercaAvanzata.putParcelableArrayListExtra("associazioniList", mOriginalList);
                startActivity(ricercaAvanzata);
            }
        });

        menu = (FloatingActionsMenu) findViewById(R.id.associazioni_menu);
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
            }
        });
        getRubricaList();
    }

    public void getRubricaList() {
        String url = getString(R.string.mobile_url);
        url += "associazioni";

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

    public class RubricaResponse implements Response.Listener<JSONArray> {

        Gson gson = new Gson();

        @Override
        public void onResponse(JSONArray responseArray) {
            try {
                ArrayList<Associazione> associazioni = new ArrayList<>();

                Log.i("Commesse.class", " " + responseArray.length());

                for (int i = 0; i < responseArray.length(); i++) {

                    JSONObject response = responseArray.getJSONObject(i);

                    System.out.println(response.toString());

                    Associazione associazione = gson.fromJson(response.toString(), Associazione.class);

                    associazioni.add(associazione);
                }

                updateList(associazioni);
                mOriginalList = associazioni;

            } catch (JSONException e) {

                e.printStackTrace();
                Log.i("LoginResponse", e.getMessage());

                Toast.makeText(getApplicationContext(),
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateList(ArrayList<Associazione> list) {
        showProgress(false);
        Collections.sort(list, associazioniComparator);
        if (!mAssociazioniList.equals(list))
        {
            mAssociazioniList.clear();
            mAssociazioniList.addAll(list);
            mListAdapeter.cleanAlphaIndex();
            mListAdapeter.notifyDataSetChanged();
            mAssociazioniListView.setSelectionAfterHeaderView();
        }
    }


    private void showProgress(boolean show) {
        if (show) {
            mAssociazioniListView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mAssociazioniListView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRubricaList();
        menu.collapse();
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


    private void simpleSearch(String query) {
        ArrayList<Associazione> matchingElement = new ArrayList<>();
        if (!TextUtils.isEmpty(query)) {
            query = query.toUpperCase();

            for (Associazione associazione : mOriginalList) {

                String cliente = "";
                String codice = "";
                String risorsa = "";

                if (associazione.getCommessa() != null && associazione.getCommessa().getCliente() != null) {
                    if (associazione.getCommessa().getCliente().getNomeSocietà() != null)
                        cliente = associazione.getCommessa().getCliente().getNomeSocietà();
                    codice = associazione.getCommessa().getNome_commessa();
                }

                if (associazione.getRisorsa() != null) {
                    risorsa = associazione.getRisorsa().getCognome() + " " + associazione.getRisorsa().getNome();
                }

                if (cliente.toUpperCase().contains(query) || codice.toUpperCase().contains(query) || risorsa.toUpperCase().contains(query)) {
                    matchingElement.add(associazione);
                }
            }
            updateList(matchingElement);
        } else
            updateList(mOriginalList);
    }


}
