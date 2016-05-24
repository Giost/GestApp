package mcteamgestapp.momo.com.mcteamgestapp.Moduli.Amministrazione.PrimaNotaCassa;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import mcteamgestapp.momo.com.mcteamgestapp.Constants;
import mcteamgestapp.momo.com.mcteamgestapp.Models.PrimaNota.NotaCassa;
import mcteamgestapp.momo.com.mcteamgestapp.R;
import mcteamgestapp.momo.com.mcteamgestapp.ToolUtils;
import mcteamgestapp.momo.com.mcteamgestapp.VolleyRequests;

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
    //Bottoni FAB
    private FloatingActionButton mButtonAdd, mButtonExcel, mButtonPrintAll;

    private Spinner mYearsSpinner;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prima_nota_cassa);

        /////////////////////////////////////////////////////////////////
        /* GET PARAMETRI DAL LAYOUT ED INIZIALIZZAZIONE PARAMETRI      */
        /////////////////////////////////////////////////////////////////
        mOverlay = findViewById(R.id.cassa_overlay);
        mButtonExcel = (FloatingActionButton) findViewById(R.id.fab_cassa_esporta_excel);
        mButtonPrintAll = (FloatingActionButton) findViewById(R.id.fab_cassa_stampa_tutto);
        mProgressBar = (ProgressBar) findViewById(R.id.prima_nota_cassa_progress);
        mNotaCassa = new ArrayList<>();
        mVolleyRequest = new VolleyRequests(this, this);

        //Set colore action bar
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = getDrawable(R.drawable.actionbar_prima_nota_cassa);
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
                        Log.d("LOGGGGGGG", item.toString());
                        Intent intent = new Intent(getApplicationContext(), VisualElimPrimaNotaCassa.class);
                        intent.putExtra(Constants.VISUAL_ELIMINA, true); //false -> delete
                        intent.putExtra(Constants.NOTA_CASSA, item);
                        startActivityForResult(intent, Constants.NOTA_SHOW);
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
                    ToolUtils.showProgress(mRecyclerView, mProgressBar, true);

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
                    ToolUtils.showProgress(mRecyclerView, mProgressBar, true);

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
                    ToolUtils.showProgress(mRecyclerView, mProgressBar, true);
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

    //0 -> gennaio ...
    private void updateList(int month, int year, int opType) {
        mNotaCassa.clear(); //Pulisco l'arraylist
        mVolleyRequest.getPrimaNotaCassaList(mNotaCassa, mAdapterRecycler, month, year, opType); //Invio una nuova richiesta
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prima_nota_cassa, menu);

        return true;
    }

    //Click bottone "aggiungi nuovo"
    public void onClickInsertNew(View view) {
        mFam.collapse();
        Intent intent = new Intent(getApplicationContext(), NewEditPrimaNotaCassaActivity.class);
        startActivityForResult(intent, Constants.NOTA_ADD);
    }

    public void onClickStampa(View view) {
        try {
            PrimaNotaCassaUtils.printAll(mAdapterRecycler.getArrayList(), getApplicationContext(), (String)mTypeSpinner.getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("requestCode = " + requestCode + "resultCode = " + resultCode);
        if (requestCode == Constants.NOTA_EDIT || requestCode == Constants.NOTA_DELETE || requestCode == Constants.NOTA_ADD || requestCode == Constants.NOTA_SHOW)
            if (resultCode == Activity.RESULT_OK) { //Refresh recyclerview
                //Ricavo valori degli spinner e lancio una richiesta volley
                int year = Integer.parseInt((String) mYearsSpinner.getSelectedItem());
                int month = mMonthSpinner.getSelectedItemPosition();
                int type = mTypeSpinner.getSelectedItemPosition();
                updateList(month, year, type);
                mAdapterRecycler.notifyDataSetChanged();
            }
    }

    /*public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }*/
}

