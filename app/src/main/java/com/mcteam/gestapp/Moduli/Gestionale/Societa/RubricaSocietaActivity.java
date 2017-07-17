package com.mcteam.gestapp.Moduli.Gestionale.Societa;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.mcteam.gestapp.Models.Rubrica.Societa;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.CustomRequest;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RubricaSocietaActivity extends AppCompatActivity {


    ListView mRubricaListaView;
    ArrayList<Societa> mRubricaSocieta;
    RubricaSocietaListAdapter mRubricaAdapter;
    FloatingActionsMenu fabMenu;
    View overlay;
    ArrayList<Societa> mSearchList;
    ProgressBar mProgressBar;


    private RequestQueue mRequestQueue;

    Comparator<Societa> societaComparator;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rubrica_societa);

        //Permette landscape e portrait solo se è un tablet
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mRubricaListaView = (ListView) findViewById(R.id.rubrica_societa_list);

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_rubrica);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
            getSupportActionBar().setIcon(R.drawable.ic_accessibility_white_24dp);
        }
        //Comparatore per ordine alfabetico del nome della società
        societaComparator = new Comparator<Societa>() {
            @Override
            public int compare(Societa lhs, Societa rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.getNomeSocietà(), rhs.getNomeSocietà());
            }
        };

        AddFloatingActionButton addNewSocietaButton = (AddFloatingActionButton) findViewById(R.id.fab_societa_add);

        addNewSocietaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NuovaSocietaActivity.class);
                startActivity(intent);
            }
        });

        mRubricaSocieta = new ArrayList<>();

        mProgressBar = (ProgressBar) findViewById(R.id.rubrica_societa_progress);

        mRequestQueue = Volley.newRequestQueue(this);

        mRubricaAdapter = new RubricaSocietaListAdapter(this, mRubricaSocieta);

        //mRubricaListaView.setFastScrollEnabled(true);

        mRubricaListaView.setAdapter(mRubricaAdapter);

        overlay = findViewById(R.id.societa_overlay);

        fabMenu = (FloatingActionsMenu) findViewById(R.id.societa_menu);
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

        FloatingActionButton exportaExcel = (FloatingActionButton) findViewById(R.id.fab_societa_esporta_excel);
        exportaExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    esportaExcel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        FloatingActionButton printAll = (FloatingActionButton) findViewById(R.id.fab_societa_stampa_tutto);
        printAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SocietaUtils.printAll(mRubricaSocieta, getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton ricercaAvanzata = (FloatingActionButton) findViewById(R.id.fab_societa_ricerca_avantazata);
        ricercaAvanzata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent advancedSearch = new Intent(getApplicationContext(), SocietaAdvanceSearchActivity.class);
                    advancedSearch.putParcelableArrayListExtra("listaSocieta", mRubricaSocieta);
                    startActivity(advancedSearch);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        mRubricaListaView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("position cliccked -> " + position);
                Societa selectedSociety;
                selectedSociety = (Societa) parent.getItemAtPosition(position);
                Intent visualizzaIntent = new Intent(getApplicationContext(), VisualizzaSocietaActivity.class);
                visualizzaIntent.putExtra("societaToView", selectedSociety);
                startActivity(visualizzaIntent);
            }
        });


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


        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_ricerca_semplice).getActionView();



        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        */
        return true;
    }

    /**
    * Metodo per la ricerca della query passata tra gli elementi
    */
    private void simpleSearch(String query) {
        ArrayList<Societa> matchingElement = new ArrayList<>();
        if (!TextUtils.isEmpty(query)) {
            query = query.toUpperCase();
            for (Societa societa : mSearchList) {
                if (societa.getNomeSocietà().toUpperCase().contains(query) || societa.getIndirizzo().toUpperCase().contains(query)) {
                    matchingElement.add(societa);
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
    /**
    * Metodo per tornare alla home
    */
    private void goHome() {
        Intent goHome = new Intent(this, HomeActivity.class);
        goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goHome);
        finish();
    }
    /**
    * Metodo per la richiesta al database dei record presenti nella tabella "rubrica societa"
    */
    public void getRubricaList() {
        String url = getString(R.string.mobile_url);
        url += "rubrica-societa";

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

        @Override
        public void onResponse(JSONArray responseArray) {
            try {
                ArrayList<Societa> societas = new ArrayList<>();

                Log.i("SistemiActivity.class", " " + responseArray.length());
                // Parsing json object response
                // response will be a json object
                for (int i = 0; i < responseArray.length(); i++) {
                    Societa societa = new Societa();
                    JSONObject response = responseArray.getJSONObject(i);
                    if(!response.getString("SOCIETA").equals("")) {
                        societa.setID(response.getInt("ID_CLIENTE"));
                        societa.setCodiceSocieta(response.getInt("ID"));
                        societa.setNomeSocietà(response.getString("SOCIETA"));
                        societa.setIndirizzo(response.getString("INDIRIZZO"));
                        societa.setCap(response.getString("CAP"));
                        societa.setSito(response.getString("WWW"));
                        societa.setmProvincia(response.getString("PROVINCIA"));
                        societa.setStato(response.getString("STATO"));
                        societa.setmFax(response.getString("FAX"));
                        societa.setmCitta(response.getString("CITTA"));
                        societa.setPartitaIva(response.getString("IVA"));
                        societa.setmTelefono(response.getString("TELEFONO"));
                        societa.setCOD_FISCALE(response.getString("COD_FISCALE"));
                        societa.setmCellulare(response.getString("CELLULARE"));
                        societa.setNote(response.getString("NOTE"));
                        societa.setmTipologia(response.getString("TIPOLOGIA"));
                        societas.add(societa);
                    }
                }
                updateList(societas);
                mSearchList = societas;
            } catch (JSONException e) {

                e.printStackTrace();
                Log.i("LoginResponse", e.getMessage());

                Toast.makeText(getApplicationContext(),
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgress(true);
        getRubricaList();
        fabMenu.collapse();
    }
    /**
    * Metodo richiamato ad ogni caricamento della ListView ed in ogni caso in cui la lista degli elementi da 
    * visualizzare cambia
    */
    private void updateList(ArrayList<Societa> list) {
        showProgress(false);
        Collections.sort(list, societaComparator);
        if (!mRubricaSocieta.equals(list))
        {
            mRubricaSocieta.clear();
            mRubricaSocieta.addAll(list);
            mRubricaAdapter.clearAlphabeticIndex();
            mRubricaAdapter.notifyDataSetChanged();
            mRubricaListaView.setSelectionAfterHeaderView();
        }
    }

    
    public void esportaExcel() throws Exception {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/GestApp/societa/excel");

        //make them in case they're not there
        dir.mkdirs();
        //create a standard java.io.File object for the Workbook to use
        File wbfile = new File(dir, "Societa.xlsx");

        Workbook wb = new HSSFWorkbook();

        Cell cella = null;

        CellStyle cs = wb.createCellStyle();

        cs.setFillForegroundColor(HSSFColor.RED.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        Sheet foglio1 = null;
        foglio1 = wb.createSheet("Società");

        Row row = foglio1.createRow(0);

        cella = row.createCell(0);
        cella.setCellValue("Società");
        cella.setCellStyle(cs);

        cella = row.createCell(1);
        cella.setCellValue("Indirizzo");
        cella.setCellStyle(cs);

        cella = row.createCell(2);
        cella.setCellValue("Cap");
        cella.setCellStyle(cs);

        cella = row.createCell(3);
        cella.setCellValue("Città");
        cella.setCellStyle(cs);

        cella = row.createCell(4);
        cella.setCellValue("Telefono");
        cella.setCellStyle(cs);

        cella = row.createCell(5);
        cella.setCellValue("Fax");
        cella.setCellStyle(cs);

        int nRow = 1;

        for (Societa societa : mRubricaSocieta) {

            row = foglio1.createRow(nRow++);

            cella = row.createCell(0);
            cella.setCellValue(societa.getNomeSocietà());

            cella = row.createCell(1);
            cella.setCellValue(societa.getIndirizzo());

            cella = row.createCell(2);
            cella.setCellValue(societa.getCap());

            cella = row.createCell(3);
            cella.setCellValue(societa.getmCitta());

            cella = row.createCell(4);
            cella.setCellValue(societa.getmTelefono());

            cella = row.createCell(5);
            cella.setCellValue(societa.getmFax());

        }

        //Write the workbook in file system
        FileOutputStream out = null;
        System.out.println("xlsx written successfully");


        try {
            out = new FileOutputStream(wbfile);
            wb.write(out);
            readCreatedFile(Uri.fromFile(wbfile));
            Log.w("FileUtils", "Writing file" + wbfile);
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + wbfile, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != out)
                    out.close();
            } catch (Exception ex) {
            }
        }

    }

    void readCreatedFile(Uri path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(path, "application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
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
