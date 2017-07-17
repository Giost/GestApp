package com.mcteam.gestapp.Moduli.Gestionale.Nominativo;

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
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.itextpdf.text.DocumentException;
import com.mcteam.gestapp.Application.MyApp;
import com.mcteam.gestapp.Models.Rubrica.Nominativo;
import com.mcteam.gestapp.Models.Rubrica.Societa;
import com.mcteam.gestapp.Models.UserInfo;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.CustomRequest;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RubricaNominativaActivity extends AppCompatActivity {


    ListView mRubricaListaView;
    ArrayList<Nominativo> mRubricaNominativo;
    RubricaNominativaListAdapter mRubricaAdapter;
    ProgressBar mProgressBar;
    ArrayList<Nominativo> mOriginalList;
    View mOverlay;
    FloatingActionsMenu mMenu;

    private RequestQueue mRequestQueue;

    Comparator<Nominativo> nominativoComparator;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rubrica_nominativo);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mRubricaListaView = (ListView) findViewById(R.id.rubrica_nominativi_list);

        //Set colore action bar -> blu in questo caso
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_rubrica);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
            getSupportActionBar().setIcon(R.drawable.ic_accessibility_white_24dp);
        }
        //Comparatore per ordine alfabetico del cognome
        nominativoComparator = new Comparator<Nominativo>() {
            @Override
            public int compare(Nominativo lhs, Nominativo rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.getCognome(), rhs.getCognome());
            }
        };

        //Non serve, è in MyApp
        final UserInfo actualUser = getIntent().getParcelableExtra("actualUser");

        mRubricaNominativo = new ArrayList<>();

        mProgressBar = (ProgressBar) findViewById(R.id.rubrica_nominativi_progress);

        mOriginalList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(this);

        mRubricaAdapter = new RubricaNominativaListAdapter(this, mRubricaNominativo, actualUser);

        //mRubricaListaView.setFastScrollEnabled(true);

        mRubricaListaView.setAdapter(mRubricaAdapter);

        mOverlay = findViewById(R.id.nominativi_overlay);

        mMenu = (FloatingActionsMenu) findViewById(R.id.nominativi_menu);

        //Nasconde listview quando il fab è espanso
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

        //Listener della listview
        mRubricaListaView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Nominativo selectedNominativo = (Nominativo) parent.getItemAtPosition(position);
                Intent viewNominativo = new Intent(getApplicationContext(), VisualizzaNominativoActivity.class);
                viewNominativo.putExtra("nominativoToView", selectedNominativo);
                viewNominativo.putExtra("actualUser", actualUser);
                startActivity(viewNominativo);
            }
        });

        AddFloatingActionButton addNewNominativo = (AddFloatingActionButton) findViewById(R.id.fab_nominativi_add);

        addNewNominativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewNominativo = new Intent(getApplicationContext(), NuovoNominativoActivity.class);
                viewNominativo.putExtra("actualUser", actualUser);
                startActivity(viewNominativo);
            }
        });

        final FloatingActionButton stampaTutto = (FloatingActionButton) findViewById(R.id.fab_nominativi_stampa_tutto);
        stampaTutto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    NominativoUtils.printAll(mRubricaNominativo, getApplicationContext());
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton exportaExcel = (FloatingActionButton) findViewById(R.id.fab_nominativi_esporta_excel);
        exportaExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    NominativoUtils.esportaExcel(mOriginalList, getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton ricercaAvanzata = (FloatingActionButton) findViewById(R.id.fab_nominativi_ricerca_avantazata);
        ricercaAvanzata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ricercaAvanzata = new Intent(getApplicationContext(), NominativiAdvanceSearch.class);
                ricercaAvanzata.putExtra("actualUser", actualUser);
                ricercaAvanzata.putExtra("listaNominativi", mOriginalList);
                startActivity(ricercaAvanzata);
            }
        });

        //Nasconde il menu a comparsa dei bottoni quando clicco fuori
        mOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.collapse();
            }
        });

        getRubricaList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRubricaList();
        mMenu.collapse();
    }
    /**
     * Metodo per la richiesta al database dei record presenti nella tabella "rubrica nominativi"
     */
    public void getRubricaList() {
        String url = getString(R.string.mobile_url);
        url += "rubrica-nominativi";

        CustomRequest accessiRequest = new CustomRequest(url, null, new RubricaResponse(), new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Errore richiesta Volley", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        });

        accessiRequest.setShouldCache(false);
        mRequestQueue.add(accessiRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_societa, menu);

        //Recupera il tasto di ricerca ed aggancia il listener con le callback
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
        ArrayList<Nominativo> matchingElement = new ArrayList<>();
        if (!TextUtils.isEmpty(query)) {
            query = query.toUpperCase();

            for (Nominativo nominativo : mOriginalList) {

                //Recupero nome società se esiste
                String societa = "";
                if (nominativo.getSocieta() != null && !TextUtils.isEmpty(nominativo.getSocieta().getNomeSocietà())) {
                    societa = nominativo.getSocieta().getNomeSocietà();
                }

                if (nominativo.getNome().toUpperCase().contains(query) || nominativo.getCognome().toUpperCase().contains(query) || societa.toUpperCase().contains(query)) {
                    matchingElement.add(nominativo);
                }
            }
            updateList(matchingElement);
        } else
            updateList(mOriginalList);
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
                ArrayList<Nominativo> nominativi = new ArrayList<>();

                Log.i("SistemiActivity.class", " " + responseArray.length());
                // Parsing json object response
                // response will be a json object
                for (int i = 0; i < responseArray.length(); i++) {
                    Nominativo nominativo = new Nominativo();
                    JSONObject response = responseArray.getJSONObject(i);
                    System.out.println(response);
                    nominativo.setID(response.getInt("ID_NOMINATIVO"));
                    nominativo.setIDSocieta(response.getInt("ID_SOCIETA"));
                    nominativo.setTitolo(response.getString("TITOLO"));
                    nominativo.setCognome(response.getString("COGNOME"));
                    nominativo.setNome(response.getString("NOME"));
                    nominativo.setIndirizzo(response.getString("INDIRIZZO"));
                    nominativo.setCap(response.getString("CAP"));
                    nominativo.setProvincia(response.getString("PROVINCIA"));
                    nominativo.setCitta(response.getString("CITTA"));
                    nominativo.setDataNascita(response.getString("data_nascita"));
                    nominativo.setLuogoNascita(response.getString("luogo_nascita"));
                    nominativo.setPIVA(response.getString("p_iva"));
                    nominativo.setCod_Fiscale(response.getString("cod_fiscale"));
                    nominativo.setCartaID(response.getString("carta_id"));
                    nominativo.setPatente(response.getString("patente"));
                    nominativo.setTesseraSanitaria(response.getString("tessera_sanitaria"));
                    nominativo.setSitoWeb(response.getString("sito_web"));
                    nominativo.setNomeBanca(response.getString("nome_banca"));
                    nominativo.setIndirizzoBanca(response.getString("indirizzo_banca"));
                    nominativo.setIBAN(response.getString("iban"));
                    nominativo.setNazionalita(response.getString("NAZIONALITA"));
                    nominativo.setTelefono(response.getString("TELEFONO"));
                    nominativo.setFax(response.getString("FAX"));
                    nominativo.setCellulare(response.getString("CELLULARE"));
                    nominativo.setEmail(response.getString("EMAIL"));
                    nominativo.setNote(response.getString("NOTE"));
                    nominativo.setNoteDett(response.getString("NOTE_DETT"));
                    nominativo.setIDStage(response.getString("id_stage"));

                    Societa societa = gson.fromJson(response.getString("SOCIETA"), Societa.class);

                    nominativo.setSocieta(societa);

                    nominativi.add(nominativo);
                }
                updateList(nominativi);
                mOriginalList = nominativi;
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
    private void updateList(ArrayList<Nominativo> list) {
        showProgress(false);
        Collections.sort(list, nominativoComparator);
        if (!mRubricaNominativo.equals(list))
        {
            mRubricaNominativo.clear();
            mRubricaNominativo.addAll(list);
            mRubricaAdapter.cleanAlphabeticIndex();
            mRubricaAdapter.notifyDataSetChanged();
            mRubricaListaView.setSelectionAfterHeaderView();
        }
    }

    /**
     * Metodo per la gestione della rotella di caricamento
     */
    private void showProgress(boolean show) {
        if (show) {
            mRubricaListaView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mRubricaListaView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

}
