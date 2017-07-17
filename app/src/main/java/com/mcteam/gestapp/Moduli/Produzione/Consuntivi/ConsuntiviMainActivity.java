package com.mcteam.gestapp.Moduli.Produzione.Consuntivi;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mcteam.gestapp.Models.Associazione;
import com.mcteam.gestapp.Models.OrariAttivita;
import com.mcteam.gestapp.Models.UserInfo;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;
import com.mcteam.gestapp.NetworkReq.CustomRequest;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ConsuntiviMainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * Material Design Tabs
     */
    private TabLayout tabs;

    /**
     * L'user attuale che ha effettuato l'accesso
     */
    private UserInfo actualUser;

    /**
     * ArrayList di associazioni
     */

    ArrayList<Associazione> mAssociazioni;

    /**
     * JSON Parser vedere Google GSOn
     */
    Gson gson;
    /**
     * Lista di OrariAttivita dell'utente attuale
     */
    ArrayList<OrariAttivita> mOrariAttivitaList;


    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consuntivi_main);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        /*
        if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Drawable actionBarBack = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                actionBarBack = getDrawable(R.drawable.produzione_action_bar);
            }
            getSupportActionBar().setBackgroundDrawable(actionBarBack);
        }*/

        //Inizializza la coda di richieste volley
        mRequestQueue = Volley.newRequestQueue(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        // setup ViewPager with framgent for each month
        //set up the TabLayout
        tabs = (TabLayout) findViewById(R.id.tabs);

        mAssociazioni = new ArrayList<>();
        mOrariAttivitaList = new ArrayList<>();

        //Ottieni l'user attuale
        actualUser = getIntent().getParcelableExtra("actualUser");

        gson = new Gson();

        //Ottieni tutte le attività
        getCommesse(actualUser.getID());

    }

    /**
     * Otteiene le commesse dal database utilizzando le richieste HTTP GET
     * Vedere Volley.Requests
     *
     * @param id identificato utente nel database remoto nella tabella [allocazione_risorse][id_utente]
     */
    private void getCommesse(int id) {
        String url = getString(R.string.mobile_url);
        url += "commesse-utente/" + id;

        CustomRequest accessiRequest = new CustomRequest(url, null, new RubricaResponse(), new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Err. commesse-utente", error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Err. commesse-utente: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        });

        mRequestQueue.add(accessiRequest);
    }


    public class RubricaResponse implements Response.Listener<JSONArray> {

        @Override
        public void onResponse(JSONArray responseArray) {

            try {

                for (int i = 0; i < responseArray.length(); i++) {

                    JSONObject response = responseArray.getJSONObject(i);

                    Associazione associazione = gson.fromJson(response.toString(), Associazione.class);

                    System.out.println(associazione);

                    mAssociazioni.add(associazione);
                }

                if (responseArray.length() != 0)
                    getCalendarRange();

            } catch (JSONException e) {

                e.printStackTrace();
                Log.i("Err resp getCommesse", e.getMessage());

                Toast.makeText(getApplicationContext(),
                        "Err resp getCommesse: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * Ottiene il range dei mesi dalle associazioni
     * per ogni associazione di quella risorsa contronta i due estremi
     */
    private void getCalendarRange() {

        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        Date inizio = null;
        Date fine = null;

        for (Associazione associazione : mAssociazioni) {
            Date tempInizio;
            Date tempFine;
            try {
                tempInizio = sdf.parse(Functions.getFormattedDate(associazione.getData_inizio()));
                tempFine = sdf.parse(Functions.getFormattedDate(associazione.getData_fine()));
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            if (fine == null && inizio == null) {
                inizio = tempInizio;
                fine = tempFine;
            } else {
                if (tempInizio.before(inizio)) {
                    inizio = tempInizio;
                }
                if (tempFine.after(fine)) {
                    fine = tempFine;
                }
            }
        }

        setupViewPager(mViewPager, inizio, fine);

    }

    private void setupViewPager(ViewPager viewPager, Date start, Date end) {

        Calendar actuaDateFromStart = Calendar.getInstance();

        actuaDateFromStart.setTime(start);
        actuaDateFromStart.set(Calendar.DATE, 1);

        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");

        //Differenza di mesi tra due date
        int monthsNumber = 0;

        //Parsificare le date in una data locale
        LocalDate startD = LocalDate.fromDateFields(start);
        LocalDate endD = LocalDate.fromDateFields(end);

        //usare JOBA Date per ottenere la differenza in mesi
        Period period = new Period(startD, endD);
        monthsNumber = (period.getYears() * 12) + period.getMonths();
        if (period.getDays() > 0)
            monthsNumber++;

        for (int i = 0; i < monthsNumber; i++) {

            ArrayList<OrariAttivita> attivitaPerMonth = new ArrayList<>();

            String month_name = month_date.format(actuaDateFromStart.getTime());

            for (OrariAttivita attivita : mOrariAttivitaList) {

                if (attivita.getMonth() == actuaDateFromStart.get(Calendar.MONTH) && attivita.getYear() == actuaDateFromStart.get(Calendar.YEAR)) {
                    attivitaPerMonth.add(attivita);
                }
            }
            //Creamo un fragment per ogni mese
            mSectionsPagerAdapter.addFragment(MonthCalendarFragment.newInstance(i, actuaDateFromStart.getTime(), mAssociazioni, actualUser), month_name + "\n" + actuaDateFromStart.get(Calendar.YEAR));
            //Aumentiamo il numero di mesi
            actuaDateFromStart.add(Calendar.MONTH, 1);
        }
        viewPager.setAdapter(mSectionsPagerAdapter);
        tabs.setupWithViewPager(mViewPager);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragmentsList = new ArrayList<>();
        ArrayList<String> framentName = new ArrayList<>();
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }


        public void addFragment(Fragment frame, String name) {
            fragmentsList.add(frame);
            framentName.add(name);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return framentName.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.consuntivo_menu, menu);

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

}
