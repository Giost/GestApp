package com.mcteam.gestapp.Moduli.Amministrazione.PrimaNotaBanca;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.mcteam.gestapp.Application.MyApp;
import com.mcteam.gestapp.Models.PrimaNota.NotaBanca;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.VolleyRequests;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.AndroidUtils;
import com.mcteam.gestapp.Utils.Constants;
import com.mcteam.gestapp.Utils.Functions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Riccardo Rossi on 13/05/2016.
 */
public class PrimaNotaBancaActivity extends AppCompatActivity {

    //Array list per note banca
    private ArrayList<NotaBanca> mNotaBanca;

    //Recyclerview lista delle note
    private RecyclerView mRecyclerView;

    //Adapter recyclerview
    private PrimaNotaBancaRecyclerAdapter mAdapterRecycler;

    //spinner mese
    private Spinner mMonthSpinner;

    private ProgressBar mProgressBar;

    //Vista di overlay quando si clicca il fam
    private View mOverlay;

    //FAM
    private FloatingActionsMenu mFam;

    //per richiesta volley
    private VolleyRequests mVolleyRequest;

    private Spinner mYearsSpinner;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prima_nota_banca);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        /////////////////////////////////////////////////////////////////
        /* GET PARAMETRI DAL LAYOUT ED INIZIALIZZAZIONE PARAMETRI      */
        /////////////////////////////////////////////////////////////////
        mOverlay = findViewById(R.id.banca_overlay);
        mProgressBar = (ProgressBar) findViewById(R.id.prima_nota_banca_progress);
        mNotaBanca = new ArrayList<>();

        mVolleyRequest = new VolleyRequests(this, this);

        //Set colore action bar
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_amministrazione);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
            getSupportActionBar().setIcon(R.drawable.ic_accessibility_white_24dp);
        }

        //Set recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_banca);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapterRecycler = new PrimaNotaBancaRecyclerAdapter(
                mNotaBanca,
                new PrimaNotaBancaRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(NotaBanca item) {
                        Intent intent = new Intent(getApplicationContext(), VisualElimBancaActivity.class);
                        intent.putExtra(Constants.VISUAL_ELIMINA, true); //false -> delete
                        intent.putExtra(Constants.NOTA_BANCA, item);
                        startActivity(intent);
                    }
                });
        mRecyclerView.setAdapter(mAdapterRecycler);

        /////////////////////////////////////////////////////////////////
        /*                 SPINNER DEL MESE                            */
        /////////////////////////////////////////////////////////////////

        //Recupero mese corrente
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int currentMonth = cal.get(Calendar.MONTH);

        mMonthSpinner = (Spinner) findViewById(R.id.spinner_month_banca);
        mMonthSpinner.setSelection(currentMonth);

        //ArrayAdapter<CharSequence> monthadapter = ArrayAdapter.createFromResource(
        //        this, R.array.month, android.R.layout.simple_spinner_item);
        //monthadapter.setDropDownViewResource(R.layout.spinner_layout);
        //month.setAdapter(monthadapter);

        mMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int check = 0;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                check++;
                if (check > 1) {
                    AndroidUtils.showProgress(mRecyclerView, mProgressBar, true);

                    int year = Integer.parseInt((String) mYearsSpinner.getSelectedItem());

                    updateList(position, year); //MESE - ANNO
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /////////////////////////////////////////////////////////////////
        /*                    SPINNER DELL'ANNO                        */
        /////////////////////////////////////////////////////////////////

        ArrayList<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1995; i <= 2100; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);

        mYearsSpinner = (Spinner) findViewById(R.id.spinner_years_banca);
        mYearsSpinner.setAdapter(adapter);
        mYearsSpinner.setSelection(currentYear - 1995);

        mYearsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int check = 0;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                check++;
                if (check > 1) {
                    AndroidUtils.showProgress(mRecyclerView, mProgressBar, true);
                    int year = Integer.parseInt((String) parent.getItemAtPosition(position));
                    int month = mMonthSpinner.getSelectedItemPosition();

                    //Toast.makeText(getApplicationContext(), "year: " + year + ", month: " + month + ", type: " + type, Toast.LENGTH_SHORT).show();

                    updateList(month, year); //MESE - ANNO
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //floating action menu -> FAM
        mFam = (FloatingActionsMenu) findViewById(R.id.prima_nota_banca_fam);
        //Nasconde listview quando il fab è espanso
        mFam.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                mOverlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                mOverlay.setVisibility(View.GONE);
            }
        });

        //Nasconde il menu a comparsa dei bottoni quando clicco fuori
        mOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFam.collapse();
            }
        });

        mVolleyRequest.getPrimaNotaBancaList(mNotaBanca, mAdapterRecycler, currentMonth, currentYear); //Di default è valuta

    }

    //0 -> gennaio ...
    private void updateList(int month, int year) {
        mVolleyRequest.getPrimaNotaBancaList(mNotaBanca, mAdapterRecycler, month, year); //Invio una nuova richiesta
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prima_nota_cassa, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                iconRefresh(false); //Disabilito l'icona di refresh per permettere la richiesta asincrona
                onRestart();
                Toast.makeText(getApplicationContext(), "Dati aggiornati", Toast.LENGTH_SHORT).show();
                return true;
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

    @Override
    protected void onRestart() {
        super.onRestart();
        //Ricavo valori degli spinner e lancio una richiesta volley
        int year = Integer.parseInt((String) mYearsSpinner.getSelectedItem());
        int month = mMonthSpinner.getSelectedItemPosition();
        updateList(month, year);
    }

    public void iconRefresh(boolean enable) {
        ActionMenuItemView refresh = (ActionMenuItemView) findViewById(R.id.action_refresh);
        if (enable)
            refresh.setEnabled(true);
        else
            refresh.setEnabled(false);
    }

    private void goHome() {
        Intent goHome = new Intent(this, HomeActivity.class);
        goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goHome);
        finish();
    }

    //Click bottone "aggiungi nuovo"
    public void onClickInsertNew(View view) {
        mFam.collapse();
        Intent intent = new Intent(getApplicationContext(), NuovoModifBancaActivity.class);
        int month = mMonthSpinner.getSelectedItemPosition();
        String year = (String) mYearsSpinner.getSelectedItem();
        intent.putExtra("MONTH", month);
        intent.putExtra("YEAR", year);
        startActivity(intent);
    }

    public void onClickStampa(View view) {
        mFam.collapse();
        String month = (String) mMonthSpinner.getSelectedItem();
        String year = (String) mYearsSpinner.getSelectedItem();
        try {
            PrimaNotaBancaUtils.printAll(mAdapterRecycler.getArrayList(), getApplicationContext(), month, year);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PrimaNotaBancaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickExcel(View view) {
        mFam.collapse();
        String month = (String) mMonthSpinner.getSelectedItem();
        String year = (String) mYearsSpinner.getSelectedItem();
        try {
            PrimaNotaBancaUtils.esportaExcel(mAdapterRecycler.getArrayList(), getApplicationContext(), month, year);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEmptyString(boolean show) {
        TextView emptyString = (TextView) findViewById(R.id.empty_string_banca);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_banca);
        if (show) {
            emptyString.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyString.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("requestCode = " + requestCode + "resultCode = " + resultCode);
        if (requestCode == Constants.NOTA_EDIT || requestCode == Constants.NOTA_DELETE || requestCode == Constants.NOTA_ADD)
            if (resultCode == Activity.RESULT_OK) { //Refresh recyclerview
                //Ricavo valori degli spinner e lancio una richiesta volley
                int year = Integer.parseInt((String) mYearsSpinner.getSelectedItem());
                int month = mMonthSpinner.getSelectedItemPosition();
                int type = mTypeSpinner.getSelectedItemPosition();
                updateList(month, year, type);
                mAdapterRecycler.notifyDataSetChanged();
            }
    }*/

    /*public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }*/
}

