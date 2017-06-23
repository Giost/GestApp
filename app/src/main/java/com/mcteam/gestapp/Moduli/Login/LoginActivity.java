package com.mcteam.gestapp.Moduli.Login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mcteam.gestapp.Application.MyApp;
import com.mcteam.gestapp.Models.UserInfo;
import com.mcteam.gestapp.Moduli.Home.HomeActivity;
import com.mcteam.gestapp.NetworkReq.JSONObjectRequest;
import com.mcteam.gestapp.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private UserInfo loggedInUser; //Non serve

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private RequestQueue mRequestQueue; //Volley request per login
    private CheckBox mCheckRememberMe;
    boolean rememberMe = false;
    AlertDialog.Builder dialogBuilder; //Dialog dati o user errati

    private SharedPreferences mSharedPreferences; // per remember me
    private SharedPreferences.Editor mLoginEditor; // per remember me

    /*@TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
                    Toast.makeText(this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //Permette landscape e portrait solo se è un tablet
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        /* CTRL + SHIFT + / -> COMMENTO CON SELEZIONE
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        Constants.REQUEST_CODE_ASK_PERMISSIONS);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
        }*/

        //Se esiste un altro utente in sessione entra senza effettuare il login
        UserInfo user = ((MyApp) this.getApplication()).getCurrentUser();
        if (user != null) {
            goToHome();
        }

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mCheckRememberMe = (CheckBox) findViewById(R.id.check_remember_me);

        mPasswordView = (EditText) findViewById(R.id.password);

        mSharedPreferences = getSharedPreferences("GestAppLogin", MODE_PRIVATE); //Prendo file preferences tramite il nome

        mLoginEditor = mSharedPreferences.edit(); //Apro il file (se non esiste lo crea) per modificare le shared preferences

        dialogBuilder = new AlertDialog.Builder(this);

        rememberMe = mSharedPreferences.getBoolean("rememberMe", false);

        if (rememberMe) { //Se remember me è stato abilitato al login precendente
            mEmailView.setText(mSharedPreferences.getString("email", ""));
            mPasswordView.setText(mSharedPreferences.getString("password", ""));
            mCheckRememberMe.setChecked(true);
        }

        Button mRegisterButton = (Button) findViewById(R.id.register_button);

        //Da mettere in xml la callback onclick
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegistration();
            }
        });

        //Da mettere in xml la callback onclick
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mProgressView = findViewById(R.id.login_progress); //Caricamento login -> progressView
        mLoginFormView = findViewById(R.id.login_form_view); //Linear layout principale

        mRequestQueue = Volley.newRequestQueue(this);
    }

    private void goToRegistration() {
        Intent goToReg = new Intent(this, RegistrationActivity.class);
        startActivity(goToReg);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false; //variabile usata per capire se effetture il login o meno
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password)); //set errore password
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            //mAuthTask = new UserLoginTask(email, password, this);
            //mAuthTask.execute((Void) null);
            tryLoginOnline(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    /*
    *  Se show = TRUE nasconde il form di login e mostra la progress view
    *  mostra il form di login e nasconde la progress view altrimenti
    */
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            //
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void tryLoginOnline(String username, String password) {
        String url = getString(R.string.mobile_url);
        url += "login?email=" + username + "&password=" + password;

        System.out.println("the url -> " + url);

        //Crea ma non passa al costruttore di JSONObjectRequest
        /*HashMap loginParam = new HashMap<String, String>();
        loginParam.put("email", username);
        loginParam.put("password", password);*/

        JSONObjectRequest loginRequest = new JSONObjectRequest(url, null, new LoginResponse(), new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("Internet error", error.getMessage());
                Toast.makeText(getApplicationContext(), "Nessuna connessione Internet trovata, verifica e riprova", Toast.LENGTH_SHORT).show();
                showProgress(false);
                // hide the progress dialog
            }
        });

        //Richiesta di login
        mRequestQueue.add(loginRequest);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class LoginResponse implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {
            try {
                UserInfo user = new UserInfo();
                // Parsing json object response
                // response will be a json object
                if (response.getBoolean("error") == false) {
                    user.setID(response.getInt("ID_UTENTE"));
                    user.setNome(response.getString("NOME"));
                    user.setCognome(response.getString("COGNOME"));
                    user.setEmail(response.getString("EMAIL"));
                    user.setPassword(response.getString("PWD"));
                    user.setCommerciale(response.getInt("COMMERCIALE"));
                    user.setGestionale(response.getInt("GESTIONALE"));
                    user.setConsulente(response.getInt("CONSULENTE"));
                    user.setAmministratore(response.getInt("AMMINISTRATORE"));
                    user.setProduzione(response.getInt("PRODUZIONE"));
                    user.setSistemi(response.getInt("SISTEMI"));
                    user.setPersonale(response.getInt("PERSONALE"));
                    user.setDirezione(response.getInt("DIREZIONE"));
                    user.setCapoProgetto(response.getInt("CAPO_PROGETTO"));
                    user.setPhone(response.getString("TELEFONO"));
                    user.setLuogoNascita(response.getString("luogo_nascita"));
                    user.setDataNascita(response.getString("data_nascita"));
                    ((MyApp) getApplication()).setCurrentUser(user);
                    rememberMeFirst();
                    goToHome();
                } else {
                    showError(response.getString("error_type"));
                    showProgress(false);
                }
            } catch (JSONException e) {

                showProgress(false);

                e.printStackTrace();
                Log.i("LoginResponse", e.getMessage());

                Toast.makeText(getApplicationContext(),
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void goToHome() {
        Intent registrationIntent = new Intent(this, HomeActivity.class);
        startActivity(registrationIntent);
        finish();
    }

    private void rememberMeFirst() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        if (mCheckRememberMe.isChecked()) {
            mLoginEditor.putBoolean("rememberMe", true);
            mLoginEditor.putString("email", email);
            mLoginEditor.putString("password", password);
            mLoginEditor.commit();
        } else {
            mLoginEditor.clear();
            mLoginEditor.commit();
        }
    }

    public void showError(String errorType) {
        dialogBuilder.setTitle("Accesso Fallito");
        dialogBuilder.setMessage(errorType);
        dialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

}

