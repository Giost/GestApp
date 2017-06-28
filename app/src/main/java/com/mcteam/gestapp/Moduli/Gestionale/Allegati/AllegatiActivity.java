package com.mcteam.gestapp.Moduli.Gestionale.Allegati;

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
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.mcteam.gestapp.Models.Allegato;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.CustomRequest;
import com.mcteam.gestapp.NetworkReq.VolleyRequests;
import com.mcteam.gestapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllegatiActivity extends AppCompatActivity {

    ListView mAllegatiListView;
    AllegatiListAdapter mAllegatiAdapter;
    ArrayList<Allegato> mAllegatiList;
    ProgressBar mProgressBar;
    View mOverlay;
    VolleyRequests mVolleyRequests;
    ArrayList<Allegato> mOriginalList = null;
    Gson gson;
    FloatingActionsMenu mMenu;
    RequestQueue mRequestQueue;
    Comparator<Allegato> allegatoComparator = new Comparator<Allegato>() {
        @Override
        public int compare(Allegato lhs, Allegato rhs) {
            return String.CASE_INSENSITIVE_ORDER.compare(lhs.getDescrizione(), rhs.getDescrizione());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allegati);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                actionBarBack = getDrawable(R.drawable.actionbar_gestionale);
            }
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }

        mAllegatiListView = (ListView) findViewById(R.id.main_allegati_list);
        mProgressBar = (ProgressBar) findViewById(R.id.allegati_progress);
        mOverlay = findViewById(R.id.allegati_overlay);
        mMenu = (FloatingActionsMenu) findViewById(R.id.allegati_menu);
        FloatingActionButton advanceSearch = (FloatingActionButton) findViewById(R.id.fab_allegati_ricerca_avantazata);

        advanceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ricercaAvanzataIntent = new Intent(getApplicationContext(), RicercaAvanzataAllegati.class);
                ricercaAvanzataIntent.putParcelableArrayListExtra("listaAllegati", mOriginalList);
                startActivity(ricercaAvanzataIntent);
            }
        });

        mOriginalList = new ArrayList<>();

        mAllegatiList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(this);

        mAllegatiAdapter = new AllegatiListAdapter(this, mAllegatiList);

        mAllegatiListView.setAdapter(mAllegatiAdapter);

        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.fab_allegati_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newAllegatoIntent = new Intent(getApplicationContext(), NuovoAllegatoActivity.class);
                startActivity(newAllegatoIntent);
            }
        });

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
                mOverlay.setVisibility(View.GONE);
                mMenu.collapse();
            }
        });

        gson = new Gson();

        getAllegatiList();
    }

    public void getAllegatiList() {
        String url = getString(R.string.mobile_url);
        url += "allegati";

        CustomRequest accessiRequest = new CustomRequest(url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray responseArray) {
                try {
                    // response will be a json object
                    ArrayList<Allegato> allegati = new ArrayList<>();
                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject response = responseArray.getJSONObject(i);
                        System.out.println(response.toString());
                        Allegato allegato = gson.fromJson(response.toString(), Allegato.class);
                        allegati.add(allegato);
                    }
                    updateList(allegati);
                    mOriginalList.clear();
                    mOriginalList.addAll(allegati);
                } catch (JSONException e) {

                    e.printStackTrace();
                    Log.i("LoginResponse", e.getMessage());

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Errore richiesta Volley", "Error: " + error.getMessage());
                // hide the progress dialog
            }
        });

        mRequestQueue.add(accessiRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllegatiList();
        mMenu.collapse();
    }

    private void updateList(ArrayList<Allegato> list) {
        showProgress(false);
        Collections.sort(list, allegatoComparator);
        if (!mAllegatiAdapter.equals(list))
        {
            mAllegatiAdapter.clear();
            mAllegatiAdapter.addAll(list);
            mAllegatiAdapter.cleanAlphabeticIndex();
            mAllegatiAdapter.notifyDataSetChanged();
            mAllegatiListView.setSelectionAfterHeaderView();
        }
    }

    private void showProgress(boolean show) {
        if (show) {
            mAllegatiListView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mAllegatiListView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
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

        ArrayList<Allegato> matchingElement = new ArrayList<>();
        if (!TextUtils.isEmpty(query)) {
            query = query.toUpperCase();

            for (Allegato allegato : mOriginalList) {

                if (allegato.getDescrizione().toUpperCase().contains(query) || allegato.getFile().toUpperCase().contains(query) || allegato.getUpload().contains(query)) {
                    matchingElement.add(allegato);
                }

            }
            updateList(matchingElement);
        } else
            updateList(mOriginalList);
    }

}
