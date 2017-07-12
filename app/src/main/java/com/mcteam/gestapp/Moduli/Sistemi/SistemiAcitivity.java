package com.mcteam.gestapp.Moduli.Sistemi;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mcteam.gestapp.Models.UserInfo;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.CustomRequest;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.HeaderFooterPageEvent;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SistemiAcitivity extends AppCompatActivity {

    private ListView mAccessiListView;
    private SistemiListAdapter mAccessiListAdapter;
    ArrayList<UserInfo> mAccessiList;
    ArrayList<UserInfo> mSearchList;
    Gson gson;
    Comparator<UserInfo> surnameSortingComparator;

    private RequestQueue mRequestQueue;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sistemi_acitivity);

        //Permette landscape e portrait solo se è un tablet
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_sistemi);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
            getSupportActionBar().setIcon(R.drawable.ic_accessibility_white_24dp);
        }

        mRequestQueue = Volley.newRequestQueue(this);

        mAccessiList = new ArrayList<>();

        surnameSortingComparator = new Comparator<UserInfo>() {
            @Override
            public int compare(UserInfo lhs, UserInfo rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.getCognome(), rhs.getCognome());
            }
        };

        mAccessiListView = (ListView) findViewById(R.id.accessi_list);

        mAccessiListAdapter = new SistemiListAdapter(this, mAccessiList);

        mAccessiListView.setAdapter(mAccessiListAdapter);

        mAccessiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo selectedUser;
                selectedUser = (UserInfo) parent.getItemAtPosition(position);
                Intent modificaIntent = new Intent(getApplicationContext(), VisualizzaActivity.class);
                modificaIntent.putExtra("userToView", selectedUser);
                startActivity(modificaIntent);
            }
        });

        AddFloatingActionButton aggiungiNovo = (AddFloatingActionButton) findViewById(R.id.fab_aggiungi_nuovo_accesso);
        aggiungiNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNew = new Intent(getApplicationContext(), NuovoAccessoActivity.class);
                startActivity(addNew);
            }
        });

        final View overlay = findViewById(R.id.overlay_transparent);

        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.accessi_menu);

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
                if (overlay.getVisibility() == View.VISIBLE) {
                    overlay.setVisibility(View.GONE);
                    fabMenu.collapse();
                }
            }
        });

        FloatingActionButton esportaExcel = (FloatingActionButton) findViewById(R.id.fab_accessi_esporta_excel);

        esportaExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    esportaExcel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        final FloatingActionButton stampaTutto = (FloatingActionButton) findViewById(R.id.fab_stampa_tutto);

        stampaTutto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    printAll();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton advancedSearch = (FloatingActionButton) findViewById(R.id.fab_ricerca_avantazata);

        advancedSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent advancedSearch = new Intent(getApplicationContext(), AdvanceSearchActivity.class);
                advancedSearch.putParcelableArrayListExtra("listaAccessi", mAccessiList);
                startActivity(advancedSearch);
            }
        });

        gson = new Gson();

        getAccessiList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accessi, menu);

        MenuItem searchItem = menu.findItem(R.id.action_ricerca_semplice);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_ricerca_semplice).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                simpleSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                simpleSearch(newText);
                return true;
            }
        });
        // Assumes current activity is the searchable activity
        // Do not iconify the widget; expand it by default
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

    @Override
    protected void onResume() {
        super.onResume();
        getAccessiList();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void printAll() throws DocumentException, FileNotFoundException {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/GestApp/accessiStampa/pdf");

        File pdf = new File(dir, "accessi.pdf");

        //creazione tabella
        //make them in case they're not there
        dir.mkdirs();
        Font boldTitle = new Font(Font.FontFamily.HELVETICA, 15f, Font.BOLD);
        Font tableTitleFond = new Font(Font.FontFamily.UNDEFINED, Font.BOLD);
        tableTitleFond.setColor(BaseColor.WHITE);
        tableTitleFond.setSize(15);


        Document pdfToPrint = new Document(PageSize.A4.rotate(), 20, 20, 100, 25);


        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileOutputStream fos = new FileOutputStream(pdf);
        PdfWriter writer = PdfWriter.getInstance(pdfToPrint, fos);
        HeaderFooterPageEvent event = new HeaderFooterPageEvent("STAMPA TUTTI UTENTI");
        event.setImage(getApplicationContext());


        writer.setPageEvent(event);
        pdfToPrint.open();


        //adding meta data
        pdfToPrint.addTitle("STAMPA TUTTI UTENTI");

        PdfPTable table = new PdfPTable(5);

        PdfPCell c1 = new PdfPCell(new Phrase("Cognome".toUpperCase(), tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Nome".toUpperCase(), tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Username".toUpperCase(), tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);


        c1 = new PdfPCell(new Phrase("Tipologia Utente".toUpperCase(), tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Abilitato".toUpperCase(), tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        table.setHeaderRows(1);

        for (UserInfo user : mAccessiList) {
            table.addCell(user.getCognome());
            table.addCell(user.getNome());
            table.addCell(user.getEmail());

            ///--------------------- TIPOLOGIA ---------------------------------
            String tipologiaUser = "";
            tipologiaUser += user.isAmministratore() ? "Amministrazione - " : "";
            tipologiaUser += user.isCommerciale() ? "Commerciale - " : "";
            tipologiaUser += user.isSistemi() ? "Sistemi - " : "";
            tipologiaUser += user.isGestionale() ? "Gestionale - " : "";
            tipologiaUser += user.isProduzione() ? "Produzione - " : "";
            tipologiaUser += user.isCapoProgetto() ? "Capo Progetto - " : "";
            tipologiaUser += user.isConsulente() ? "Consulente - " : "";
            tipologiaUser += user.isDirezione() ? "Direzionale - " : "";
            tipologiaUser += user.isPersonale() ? "Personale - " : "";
            table.addCell(tipologiaUser);
            ///----------------------------------------------------------------

            table.addCell(user.isAbilitato() ? "Si" : "No");

        }

        pdfToPrint.add(table);

        //Write the workbook in file system
        readPdfFile(Uri.fromFile(pdf));

        pdfToPrint.close();

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

    public void getAccessiList() {
        String url = getString(R.string.mobile_url);
        url += "accessi";

        CustomRequest accessiRequest = new CustomRequest(url, null, new AccessiResponse(), new Response.ErrorListener() {

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

    public class AccessiResponse implements Response.Listener<JSONArray> {

        @Override
        public void onResponse(JSONArray responseArray) {
            try {
                ArrayList<UserInfo> userInfos = new ArrayList<>();
                Log.i("SistemiActivity.class", " " + responseArray.length());
                // Parsing json object response
                // response will be a json object
                for (int i = 0; i < responseArray.length(); i++) {

                    JSONObject response = responseArray.getJSONObject(i);
                    System.out.println(response.toString());

                    UserInfo user = gson.fromJson(response.toString(), UserInfo.class);

                    userInfos.add(user);
                }
                updateList(userInfos);
                mSearchList = userInfos;
            } catch (JSONException e) {

                e.printStackTrace();
                Log.i("LoginResponse", e.getMessage());

                Toast.makeText(getApplicationContext(),
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateList(ArrayList<UserInfo> list) {
        Collections.sort(list, surnameSortingComparator);
        if (!mAccessiList.equals(list))
        {
            mAccessiList.clear();
            mAccessiList.addAll(list);
            mAccessiListAdapter.cleanAlphabeticIndex();
            mAccessiListAdapter.notifyDataSetChanged();
            mAccessiListView.setSelectionAfterHeaderView();
        }
    }

    private void simpleSearch(String query) {
        ArrayList<UserInfo> matchingElement = new ArrayList<>();

        if (!TextUtils.isEmpty(query)) {
            for (UserInfo user : mSearchList) {
                if (user.getCognome().toUpperCase().contains(query.toUpperCase()) || user.getNome().toUpperCase().contains(query.toUpperCase())) {
                    matchingElement.add(user);
                }
            }
            updateList(matchingElement);
        } else
            updateList(mSearchList);

    }

    public void esportaExcel() throws Exception {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/GestApp/accessi/excel");

        //make them in case they're not there
        dir.mkdirs();
        //create a standard java.io.File object for the Workbook to use
        File wbfile = new File(dir, "Accessi.xlsx");

        Workbook wb = new HSSFWorkbook();

        Cell cella = null;

        CellStyle cs = wb.createCellStyle();

        cs.setFillForegroundColor(HSSFColor.RED.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        Sheet foglio1 = null;
        foglio1 = wb.createSheet("Tutti gli Accessi");


        Row row = foglio1.createRow(0);

        cella = row.createCell(0);
        cella.setCellValue("Cognome");
        cella.setCellStyle(cs);

        cella = row.createCell(1);
        cella.setCellValue("Nome");
        cella.setCellStyle(cs);

        cella = row.createCell(2);
        cella.setCellValue("Username");
        cella.setCellStyle(cs);

        cella = row.createCell(3);
        cella.setCellValue("Abilitato");
        cella.setCellStyle(cs);

        cella = row.createCell(4);
        cella.setCellValue("Ruoli");
        cella.setCellStyle(cs);

        foglio1.addMergedRegion(new CellRangeAddress(
                0, //first row (0-based)
                0, //last row (0-based)
                4, //first column (0-based)
                13 //last column (0-based)
        ));

        int nRow = 1;

        for (UserInfo user : mAccessiList) {

            row = foglio1.createRow(nRow++);

            cella = row.createCell(0);
            cella.setCellValue(user.getCognome());

            cella = row.createCell(1);
            cella.setCellValue(user.getNome());

            cella = row.createCell(2);
            cella.setCellValue(user.getUsername());

            cella = row.createCell(3);
            cella.setCellValue(user.isAbilitato() ? "Si" : "No");

            cella = row.createCell(4);
            cella.setCellValue(user.isAmministratore() ? "Amministrazione" : "");

            cella = row.createCell(5);
            cella.setCellValue(user.isCommerciale() ? "Commerciale" : "");

            cella = row.createCell(6);
            cella.setCellValue(user.isSistemi() ? "Sistemi" : "");

            cella = row.createCell(7);
            cella.setCellValue(user.isGestionale() ? "Gestionale" : "");

            cella = row.createCell(8);
            cella.setCellValue(user.isProduzione() ? "Produzione" : "");

            cella = row.createCell(9);
            cella.setCellValue(user.isCapoProgetto() ? "Capo Progetto" : "");

            cella = row.createCell(10);
            cella.setCellValue(user.isConsulente() ? "Consulente" : "");

            cella = row.createCell(11);
            cella.setCellValue(user.isDirezione() ? "Direzionale" : "");

            cella = row.createCell(12);
            cella.setCellValue(user.isPersonale() ? "Personale" : "");

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

    void readPdfFile(Uri path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(path, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
        }
    }
}
