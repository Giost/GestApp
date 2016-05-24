package mcteamgestapp.momo.com.mcteamgestapp.Moduli.Gestionale.Commesse;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
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
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.itextpdf.text.DocumentException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mcteamgestapp.momo.com.mcteamgestapp.CustomRequest;
import mcteamgestapp.momo.com.mcteamgestapp.Models.Commessa;
import mcteamgestapp.momo.com.mcteamgestapp.Models.Rubrica.Nominativo;
import mcteamgestapp.momo.com.mcteamgestapp.Models.Rubrica.Societa;
import mcteamgestapp.momo.com.mcteamgestapp.Moduli.Home.HomeActivity;
import mcteamgestapp.momo.com.mcteamgestapp.Moduli.Login.LoginActivity;
import mcteamgestapp.momo.com.mcteamgestapp.R;

public class CommesseActivity extends AppCompatActivity {

    ArrayList<Commessa> mCommesseList;
    RequestQueue mRequestQueue;
    ListView mCommesseListView;
    ProgressBar mProgressBar;
    CommesseListAdapter mCommesseListAdapter;
    ArrayList<Commessa> mOriginalList;
    View mOverlay;
    FloatingActionsMenu menu;

    Comparator<Commessa> commessaComparator;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commesse);


        //***********************************************************************
        //Cambiare colore alla actionBar
        //************************************************************************
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
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
                    return -1;
                } else
                    return 2;
            }
        };

        //***********************************************************************
        //Inizializzazione della view
        //************************************************************************
        mCommesseListView = (ListView) findViewById(R.id.commesse_list);
        mCommesseListAdapter = new CommesseListAdapter(this, mCommesseList, false);
        mProgressBar = (ProgressBar) findViewById(R.id.commesse_progress_bar);
        mOverlay = findViewById(R.id.commesse_overlay_transparent);
        AddFloatingActionButton addNewElement = (AddFloatingActionButton) findViewById(R.id.fab_commesse_aggiungi_nuovo_accesso);

        menu = (FloatingActionsMenu) findViewById(R.id.commesse_menu);

        //Parte relativa alla creazione dello storico
        /*
        BootstrapButton storicoButton = (BootstrapButton) findViewById(R.id.commesse_storico_bootstrap);

        storicoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent storicoIntent = new Intent(CommesseActivity.this, StoricoCommesseActivity.class);
                startActivity(storicoIntent);
            }
        });*/

        mCommesseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Commessa commessa = (Commessa) parent.getItemAtPosition(position);
                Intent visualizzaCommessa = new Intent(getApplicationContext(), VisualizzaCommessaActivity.class);
                visualizzaCommessa.putExtra("commessaToView", commessa);
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

        addNewElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newElement = new Intent(getApplicationContext(), NuovaCommessaActivity.class);
                startActivity(newElement);
            }
        });
        mCommesseListView.setAdapter(mCommesseListAdapter);

        getRubricaList();

        FloatingActionButton printAll = (FloatingActionButton) findViewById(R.id.fab_commesse_stampa_tutto);
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


        final FloatingActionButton ricercaAvanzata = (FloatingActionButton) findViewById(R.id.fab_commesse_ricerca_avantazata);
        ricercaAvanzata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ricercaAvanzataIntent = new Intent(getApplicationContext(), RicercaAvanzataCommesseActivity.class);
                ricercaAvanzataIntent.putParcelableArrayListExtra("commesseList", mCommesseList);
                startActivity(ricercaAvanzataIntent);
            }
        });

        FloatingActionButton esportaExcel = (FloatingActionButton) findViewById(R.id.fab_commesse_esporta_excel);
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

    public void getRubricaList() {
        String url = getString(R.string.mobile_url);
        url += "commesse";

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
                ArrayList<Commessa> commesse = new ArrayList<>();

                Log.i("Commesse.class", " " + responseArray.length());

                for (int i = 0; i < responseArray.length(); i++) {

                    JSONObject response = responseArray.getJSONObject(i);

                    System.out.println(response);

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

    private void updateList(ArrayList<Commessa> list) {
        showProgress(false);
        Collections.sort(list, commessaComparator);
        mCommesseList.clear();
        mCommesseList.addAll(list);
        mCommesseListAdapter.notifyDataSetChanged();
    }

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
