package com.mcteam.gestapp.Moduli.Amministrazione.PrimaNotaCassa;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.mcteam.gestapp.Utils.AndroidUtils;
import com.mcteam.gestapp.Utils.Constants;
import com.mcteam.gestapp.Models.PrimaNota.NotaCassa;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.Application.MyApp;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.NetworkReq.VolleyRequests;
import com.mcteam.gestapp.Utils.Functions;

/**
 * Created by Riccardo Rossi on 13/05/2016.
 */
public class PrimaNotaCassaActivity extends AppCompatActivity {

    //Array list per note cassa
    private ArrayList<NotaCassa> mNotaCassa;

    //Recyclerview lista delle note
    private RecyclerView mRecyclerView;
    //Adapter recyclerview
    private PrimaNotaCassaRecyclerAdapter mAdapterRecycler;

    //Spinner tipo operazione
    private Spinner mTypeSpinner;

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
        setContentView(R.layout.activity_prima_nota_cassa);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        /////////////////////////////////////////////////////////////////
        /* GET PARAMETRI DAL LAYOUT ED INIZIALIZZAZIONE PARAMETRI      */
        /////////////////////////////////////////////////////////////////
        mOverlay = findViewById(R.id.cassa_overlay);
        mProgressBar = (ProgressBar) findViewById(R.id.prima_nota_cassa_progress);
        mNotaCassa = new ArrayList<>();

        mVolleyRequest = new VolleyRequests(this, this);

        //Set colore action bar
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_amministrazione);
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
            getSupportActionBar().setIcon(R.drawable.ic_accessibility_white_24dp);
        }

        //Set recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.simpleRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapterRecycler = new PrimaNotaCassaRecyclerAdapter(
                mNotaCassa,
                new PrimaNotaCassaRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(NotaCassa item) {
                        Intent intent = new Intent(getApplicationContext(), VisualElimCassaActivity.class);
                        intent.putExtra(Constants.VISUAL_ELIMINA, true); //false -> delete
                        intent.putExtra(Constants.NOTA_CASSA, item);
                        startActivity(intent);
                    }
                });
        mRecyclerView.setAdapter(mAdapterRecycler);

        /////////////////////////////////////////////////////////////////
        /*                 SPINNER DEL TIPO DI OPERAZIONE              */
        /////////////////////////////////////////////////////////////////
        mTypeSpinner = (Spinner) findViewById(R.id.spinner_type);

        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int check = 0;


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                check++;
                if (check > 1) {
                    AndroidUtils.showProgress(mRecyclerView, mProgressBar, true);

                    int month = mMonthSpinner.getSelectedItemPosition();
                    int year = Integer.parseInt((String) mYearsSpinner.getSelectedItem());
                    int type = mTypeSpinner.getSelectedItemPosition();

                    updateList(month, year, type); //MESE - ANNO - TIPO_OPERAZIONE
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /////////////////////////////////////////////////////////////////
        /*                 SPINNER DEL MESE                            */
        /////////////////////////////////////////////////////////////////

        //Recupero mese corrente
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int currentMonth = cal.get(Calendar.MONTH);

        mMonthSpinner = (Spinner) findViewById(R.id.spinner_month);
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
                    int type = mTypeSpinner.getSelectedItemPosition();

                    updateList(position, year, type); //MESE - ANNO - TIPO_OPERAZIONE
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

        mYearsSpinner = (Spinner) findViewById(R.id.spinner_years);
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
                    int type = mTypeSpinner.getSelectedItemPosition();

                    //Toast.makeText(getApplicationContext(), "year: " + year + ", month: " + month + ", type: " + type, Toast.LENGTH_SHORT).show();

                    updateList(month, year, type); //MESE - ANNO - TIPO_OPERAZIONE
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //floating action menu -> FAM
        mFam = (FloatingActionsMenu) findViewById(R.id.prima_nota_cassa_fam);
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

        mVolleyRequest.getPrimaNotaCassaList(mNotaCassa, mAdapterRecycler, currentMonth, currentYear, 0); //Di default è valuta

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.printAll();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Non hai autorizzato i permessi.", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //0 -> gennaio ...
    private void updateList(int month, int year, int opType) {
        mVolleyRequest.getPrimaNotaCassaList(mNotaCassa, mAdapterRecycler, month, year, opType); //Invio una nuova richiesta
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
        int type = mTypeSpinner.getSelectedItemPosition();
        updateList(month, year, type);
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
        Intent intent = new Intent(getApplicationContext(), NuovoModifCassaActivity.class);
        int month = mMonthSpinner.getSelectedItemPosition();
        String year = (String) mYearsSpinner.getSelectedItem();
        intent.putExtra("MONTH", month);
        intent.putExtra("YEAR", year);
        startActivity(intent);
    }

    public void onClickStampa(View view) {
        mFam.collapse();
        AndroidUtils.checkPermissions(this);
        printAll();

    }

    private void printAll() {
        String type = (String) mTypeSpinner.getSelectedItem();
        String month = (String) mMonthSpinner.getSelectedItem();
        String year = (String) mYearsSpinner.getSelectedItem();
        try {
            PrimaNotaCassaUtils.printAll(mAdapterRecycler.getArrayList(), getApplicationContext(), type, month, year);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickExcel(View view) {
        mFam.collapse();
        String type = (String) mTypeSpinner.getSelectedItem();
        String month = (String) mMonthSpinner.getSelectedItem();
        String year = (String) mYearsSpinner.getSelectedItem();
        try {
            PrimaNotaCassaUtils.esportaExcel(mAdapterRecycler.getArrayList(), getApplicationContext(), type, month, year);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEmptyString(boolean show) {
        TextView emptyString = (TextView) findViewById(R.id.empty_string);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.simpleRecyclerView);
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

