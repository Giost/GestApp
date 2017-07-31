package com.mcteam.gestapp.NetworkReq;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Models.PrimaNota.NotaBanca;
import com.mcteam.gestapp.Models.PrimaNota.NotaCassa;
import com.mcteam.gestapp.Models.Rubrica.Nominativo;
import com.mcteam.gestapp.Models.Rubrica.Societa;
import com.mcteam.gestapp.Moduli.Amministrazione.PrimaNotaBanca.PrimaNotaBancaActivity;
import com.mcteam.gestapp.Moduli.Amministrazione.PrimaNotaBanca.PrimaNotaBancaRecyclerAdapter;
import com.mcteam.gestapp.Moduli.Amministrazione.PrimaNotaCassa.PrimaNotaCassaActivity;
import com.mcteam.gestapp.Moduli.Amministrazione.PrimaNotaCassa.PrimaNotaCassaRecyclerAdapter;
import com.mcteam.gestapp.Moduli.Commerciale.Offerte.OfferteActivity;
import com.mcteam.gestapp.Moduli.Gestionale.Commesse.CommesseActivity;
import com.mcteam.gestapp.Moduli.Gestionale.Commesse.NominativoSpinnerAdapter;
import com.mcteam.gestapp.Moduli.Gestionale.Nominativo.SocietaSpinnerAdapter;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.AndroidUtils;
import com.mcteam.gestapp.Utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author Created by Riccardo on 18/12/2015.
 */
public class VolleyRequests {

    Context mContext;
    RequestQueue mRequestQueue;
    Gson gson = new Gson();
    AlertDialog.Builder dialogBuilder;
    Activity mActivity;
    boolean isShowing = false;
    ProgressDialog mWaiting;

    public VolleyRequests(Context context, Activity activity) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context);
        dialogBuilder = new AlertDialog.Builder(context);
        mWaiting = new ProgressDialog(context);
        mActivity = activity;
    }

    public void getCommesseList() {
        String url = mContext.getString(R.string.mobile_url);
        url += "commesse";

        CustomRequest accessiRequest = new CustomRequest(url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray responseArray) {
                try {
                    ArrayList<Commessa> commesse = new ArrayList<>();
                    Log.i("Commesse.class", " " + responseArray.length());

                    for (int i = 0; i < responseArray.length(); i++) {

                        JSONObject response = responseArray.getJSONObject(i);

                        //System.out.println(response);

                        Commessa commessa = gson.fromJson(response.toString(), Commessa.class);

                        commesse.add(commessa);
                    }
                    if(mContext instanceof CommesseActivity) {
                        CommesseActivity commesseActivity = ((CommesseActivity) mContext);
                        commesseActivity.updateOriginalList(commesse);
                        commesseActivity.updateList(commesse);
                    }
                    else if(mContext instanceof OfferteActivity) {
                        OfferteActivity offerteActivity = ((OfferteActivity)mContext);
                        offerteActivity.updateList(commesse, true);
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                    Log.i("LoginResponse", e.getMessage());

                    Toast.makeText(mContext,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Errore richiesta Volley", "Error: " + error.getMessage());
                Toast.makeText(mContext,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        });

        mRequestQueue.add(accessiRequest);
    }

    public void getNominativiList(final ArrayList<Nominativo> list, final NominativoSpinnerAdapter adapter) {
        String url = mContext.getString(R.string.mobile_url);
        url += "rubrica-nominativi";

        CustomRequest accessiRequest = new CustomRequest(url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray responseArray) {
                try {
                    Log.i("SistemiActivity.class", " " + responseArray.length());
                    // Parsing json object response
                    // response will be a json object
                    ArrayList<Nominativo> societas = new ArrayList<>();
                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject response = responseArray.getJSONObject(i);
                        //DEBUG
                        //System.out.println(response);
                        Nominativo nominativo = gson.fromJson(response.toString(), Nominativo.class);
                        societas.add(nominativo);
                    }
                    list.addAll(societas);
                    adapter.notifyDataSetChanged();
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

        accessiRequest.setShouldCache(false);
        mRequestQueue.add(accessiRequest);
    }

    public void getSocietaList(final ArrayList<Societa> list, final SocietaSpinnerAdapter adapter) {
        String url = mContext.getString(R.string.mobile_url);
        url += "rubrica-societa";

        CustomRequest accessiRequest = new CustomRequest(url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray responseArray) {
                try {
                    Log.i("SistemiActivity.class", " " + responseArray.length());
                    // Parsing json object response
                    // response will be a json object
                    ArrayList<Societa> societas = new ArrayList<>();
                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject response = responseArray.getJSONObject(i);
                        Societa societa = gson.fromJson(response.toString(), Societa.class);
                        societas.add(societa);
                    }
                    list.addAll(societas);
                    adapter.notifyDataSetChanged();
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

        accessiRequest.setShouldCache(false);
        mRequestQueue.add(accessiRequest);
    }

    public void addNewElementRequest(String json, String route) {
        String url = mContext.getString(R.string.mobile_url);
        url += route;

        System.out.println("The url -> " + url);

        HashMap<String, String> params = new HashMap<>();
        params.put("data", json);

        System.out.println(json);

        showWaitingDialog(true); //Mostra dialog caricamento

        dialogBuilder.setTitle("Inserimento nuovo valore");

        PUTRequest modifyRequestJson = new PUTRequest(Request.Method.PUT, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean errore = response.getBoolean("error");
                    if (errore) {
                        try {
                            String error_type = response.getString("error_type");
                            showError(true, error_type);

                        } catch (JSONException e) {
                            showError(true);
                        }
                    } else {
                        showError(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showError(true);
            }
        });

        modifyRequestJson.setShouldCache(false);
        mRequestQueue.add(modifyRequestJson);

    }

    public void addNewElementRequestNoAlert(String json, String route) {
        String url = mContext.getString(R.string.mobile_url);
        url += route;

        HashMap<String, String> params = new HashMap<>();
        params.put("data", json);

        System.out.println(json);

        dialogBuilder.setTitle("Modifica nominativo");

        PUTRequest modifyRequestJson = new PUTRequest(Request.Method.PUT, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("ADD NEW ELEMENT", response.toString());
                /*
                try {
                    boolean errore = response.getBoolean("error");
                    if(errore) {
                        try {
                            String error_type = response.getString("error_type");
                            showError(true,error_type);

                        } catch (JSONException e) {
                            showError(true);
                        }
                    }else{
                        showError(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showError(true);
            }
        });

        modifyRequestJson.setShouldCache(false);
        mRequestQueue.add(modifyRequestJson);

    }

    private void showError(boolean error) {
        dialogBuilder = new AlertDialog.Builder(mContext);
        showWaitingDialog(false); //Dismette dialog caricamento
        if (error) {
            dialogBuilder.setTitle("Errore");
            dialogBuilder.setMessage("Operazione fallita");
            dialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isShowing = false;
                }
            });
            AlertDialog dialog = dialogBuilder.create();
            if (!isShowing) {
                dialog.show();
                isShowing = true;
            } else
                dialog.dismiss();
        } else {
            dialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent data = new Intent();
                    if (mActivity.getParent() == null) {
                        mActivity.setResult(Activity.RESULT_OK, data);
                    } else {
                        mActivity.getParent().setResult(Activity.RESULT_OK, data);
                    }
                    mActivity.finish();
                }
            });
            AlertDialog dialog = dialogBuilder.create();
            if (!isShowing) {
                dialog.setTitle("Successo!");
                dialog.setMessage("Operazione avvenuto con successo");
                dialog.show();
                isShowing = true;
            } else
                dialog.dismiss();
        }
    }

    private void showError(boolean error, String type) {

        dialogBuilder = new AlertDialog.Builder(mContext);
        if (error) {
            dialogBuilder.setTitle("Errore");
            dialogBuilder.setMessage(type);
            dialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isShowing = false;
                }
            });
            AlertDialog dialog = dialogBuilder.create();
            if (!isShowing) {
                dialog.show();
                isShowing = true;
            } else {
                dialog.dismiss();
            }
        } else {
            dialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent data = new Intent();
                    if (mActivity.getParent() == null) {
                        mActivity.setResult(Activity.RESULT_OK, data);
                    } else {
                        mActivity.getParent().setResult(Activity.RESULT_OK, data);
                    }
                    mActivity.finish();
                }
            });
            AlertDialog dialog = dialogBuilder.create();
            dialog.setTitle("Successo!");
            dialog.setMessage("Operazione avvenuto con successo");
            if (!isShowing) {
                dialog.show();
                isShowing = true;
            }
        }
    }

    public void deleteElement(final String path) {
        dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setTitle("Sicuro di voler eliminare?");
        dialogBuilder.setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitingDialog(true);
                attemptDelete(path);
            }
        });

        dialogBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog mDeleteDialog = dialogBuilder.create();
        mDeleteDialog.show();
    }

    private void attemptDelete(String path) {
        String url = mContext.getString(R.string.mobile_url);
        url += path;

        PUTRequest deleteRequestJson = new PUTRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean errore = response.getBoolean("error");
                    if (errore) {
                        try {
                            String error_type = response.getString("error_type");
                            showError(true, error_type);

                        } catch (JSONException e) {
                            showError(true);
                        }
                    } else {
                        showError(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showError(true);
            }
        });

        deleteRequestJson.setShouldCache(false);
        mRequestQueue.add(deleteRequestJson);
    }

    /*public void getAllegatiList(final ArrayList<Allegato> list, final AllegatiListAdapter adapter) {
        String url = mContext.getString(R.string.mobile_url);
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
                    list.addAll(allegati);
                    adapter.notifyDataSetChanged();
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

        accessiRequest.setShouldCache(false);
        mRequestQueue.add(accessiRequest);
    }*/

    String twoHyphens = "--";
    String lineEnd = "\r\n";
    String boundary = "apiclient-" + System.currentTimeMillis();
    String mimeType = "multipart/form-data;boundary=" + boundary;
    byte[] multipartBody;

    public void uploadFile(File file, String description) throws IOException {

        System.out.println("inside upladFile");

        String url = mContext.getString(R.string.mobile_url);
        url += "allegato/upload";

        byte[] bDescription = description.getBytes();

        byte[] bfile = fullyReadFileToBytes(file);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        buildFilePart(dos, bfile, file.getName());

        buildTextPart(dos, bDescription);

        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        multipartBody = baos.toByteArray();

        MultipartRequest multipartRequest = new MultipartRequest(url, null, mimeType, multipartBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println(response.toString());

                    boolean errore = response.getBoolean("error");
                    if (errore) {
                        showError(true);
                    } else {
                        showError(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        });

        mRequestQueue.add(multipartRequest);
    }

    public void uploadFileOfferta(File file) throws IOException {

        System.out.println("inside uploadFileOfferta");

        String url = mContext.getString(R.string.mobile_url);
        url += "offerta-allegato-nuovo";

        byte[] bfile = fullyReadFileToBytes(file);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        buildFilePart(dos, bfile, file.getName());

        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        multipartBody = baos.toByteArray();

        MultipartRequest multipartRequest = new MultipartRequest(url, null, mimeType, multipartBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println(response.toString());

                    boolean errore = response.getBoolean("error");
                    if (errore) {
                        showError(true);
                    } else {
                        showError(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        });

        mRequestQueue.add(multipartRequest);
    }

    public void getPrimaNotaCassaList(final ArrayList<NotaCassa> mNotaCassa, final PrimaNotaCassaRecyclerAdapter adapter, int month, int year, int opType) {

        String url = mActivity.getString(R.string.mobile_url);
        url += "prima-nota-cassa/" + (month + 1) + "/" + year + "/" + opType;

        mNotaCassa.clear(); //Pulisco l'arraylist

        PrimaNotaCassaActivity activityPrimaNota = (PrimaNotaCassaActivity) mContext;
        activityPrimaNota.showEmptyString(false);

        System.out.println("the url -> " + url);

        final CustomRequest accessiRequest = new CustomRequest(url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        PrimaNotaCassaActivity activityPrimaNota = (PrimaNotaCassaActivity) mContext;
                        ProgressBar progressBar = (ProgressBar) mActivity.findViewById(R.id.prima_nota_cassa_progress);
                        RecyclerView recyclerView = (RecyclerView) mActivity.findViewById(R.id.simpleRecyclerView);
                        try {
                            if (jsonArray.length() == 0) {
                                activityPrimaNota.showEmptyString(true);
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject response = jsonArray.getJSONObject(i);
                                    System.out.println(response.toString());
                                    //NotaCassa notaCassa = gson.fromJson(response.toString(), NotaCassa.class);

                                    NotaCassa notaCassa = new NotaCassa();
                                    notaCassa.setID(response.getInt("riga"));
                                    notaCassa.setCassa(response.getInt("cassa"));
                                    //notaCassa.setDataPagamento(response.getString("data_pagamento"));
                                    String dataString = response.getString("data_pagamento");
                                    notaCassa.setDataPagamento(Functions.getFormattedDate(dataString));


                                    if (response.get("cod_dare").equals("") || response.get("cod_dare") == null)
                                        notaCassa.setCausaleContabile(""); //Se nullo o vuoto nel database -> il campo resta vuoto
                                    else
                                        notaCassa.setCausaleContabile(response.getString("cod_dare"));

                                    if (response.get("cod_avere").equals("") || response.get("cod_avere") == null)
                                        notaCassa.setSottoconto(""); //Se nullo o vuoto nel database -> il campo resta vuoto
                                    else
                                        notaCassa.setSottoconto(response.getString("cod_avere"));

                                    notaCassa.setDescrizione(response.getString("descrizione"));

                                    if (response.isNull("nr_protocollo"))
                                        notaCassa.setNumeroProtocollo(0); //Non presente
                                    else
                                        notaCassa.setNumeroProtocollo(response.getInt("nr_protocollo"));

                                    //Check se campo dare è vuoto -> problema decoding
                                    if (response.get("dare").equals("") || response.isNull("dare"))
                                        notaCassa.setDare(0);
                                    else
                                        notaCassa.setDare(Functions.parse(response.getString("dare")));

                                    if (response.get("avere").equals("") || response.isNull("avere"))
                                        notaCassa.setAvere(0);
                                    else
                                        notaCassa.setAvere(Functions.parse(response.getString("avere")));

                                    mNotaCassa.add(notaCassa);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            activityPrimaNota.iconRefresh(true);
                            AndroidUtils.showProgress(recyclerView, progressBar, false); //Nascondo barra circolare di caricamento


                        } catch (JSONException jsonEx) {
                            jsonEx.printStackTrace();
                            AndroidUtils.showProgress(recyclerView, progressBar, false); //Nascondo barra circolare di caricamento
                            Toast.makeText(mContext, "Errore nel caricamento, dati incompleti", Toast.LENGTH_SHORT).show();
                        }
                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(mContext, "Errore nel caricamento dei dati!", Toast.LENGTH_SHORT).show();

                        ProgressBar progressBar = (ProgressBar) mActivity.findViewById(R.id.prima_nota_cassa_progress);
                        RecyclerView recyclerView = (RecyclerView) mActivity.findViewById(R.id.simpleRecyclerView);
                        AndroidUtils.showProgress(recyclerView, progressBar, false); //Nascondo barra circolare di caricamento
                    }
                });

        /*accessiRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        accessiRequest.setShouldCache(false);
        mRequestQueue.add(accessiRequest);
    }

    public void getPrimaNotaBancaList(final ArrayList<NotaBanca> mNotaBanca, final PrimaNotaBancaRecyclerAdapter adapter, int month, int year) {
        String url = mActivity.getString(R.string.mobile_url);
        url += "prima-nota-banca/" + (month + 1) + "/" + year;

        mNotaBanca.clear(); //Pulisco l'arraylist

        System.out.println("the url -> " + url);

        PrimaNotaBancaActivity activityPrimaNota = (PrimaNotaBancaActivity) mContext;
        activityPrimaNota.showEmptyString(false);

        final CustomRequest accessiRequest = new CustomRequest(url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        PrimaNotaBancaActivity activityPrimaNota = (PrimaNotaBancaActivity) mContext;
                        ProgressBar progressBar = (ProgressBar) mActivity.findViewById(R.id.prima_nota_banca_progress);
                        RecyclerView recyclerView = (RecyclerView) mActivity.findViewById(R.id.recyclerview_banca);
                        try {
                            if (jsonArray.length() == 0) {
                                activityPrimaNota.showEmptyString(true);
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject response = jsonArray.getJSONObject(i);
                                    System.out.println(response.toString());
                                    //NotaCassa notaCassa = gson.fromJson(response.toString(), NotaCassa.class);

                                    NotaBanca notaBanca = new NotaBanca();
                                    notaBanca.setID(response.getInt("riga"));
                                    notaBanca.setGruppo(response.getInt("gruppo"));

                                    String dataOpString = response.getString("data_pagamento");
                                    notaBanca.setDataPagamento(Functions.getFormattedDate(dataOpString));

                                    String dataValString = response.getString("data_valuta");
                                    notaBanca.setDataValuta(Functions.getFormattedDate(dataValString));

                                    notaBanca.setDescrizione(response.getString("descrizione"));

                                    if (response.get("nr_protocollo").equals("") || response.get("nr_protocollo").equals(" ") || response.isNull("nr_protocollo"))
                                        notaBanca.setNumeroProtocollo(0); //Non presente
                                    else
                                        notaBanca.setNumeroProtocollo(response.getInt("nr_protocollo"));

                                    //Check se campo dare è vuoto -> problema decoding
                                    if (response.get("dare").equals("") || response.get("dare").equals(" ") || response.isNull("dare"))
                                        notaBanca.setDare(0);
                                    else
                                        notaBanca.setDare(Functions.parse(response.getString("dare")));


                                    if (response.get("avere").equals("") || response.get("avere").equals(" ") || response.isNull("avere"))
                                        notaBanca.setAvere(0);
                                    else
                                        notaBanca.setAvere(Functions.parse(response.getString("avere")));

                                    mNotaBanca.add(notaBanca);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            //Recupero le view per la progressbar
                            //PrimaNotaCassaActivity activity = (PrimaNotaCassaActivity) mActivity;
                            //activity.iconRefresh(true);

                            AndroidUtils.showProgress(recyclerView, progressBar, false); //Nascondo barra circolare di caricamento


                        } catch (JSONException jsonEx) {
                            jsonEx.printStackTrace();
                            AndroidUtils.showProgress(recyclerView, progressBar, false); //Nascondo barra circolare di caricamento
                            Toast.makeText(mContext, "Errore nel caricamento, dati incompleti", Toast.LENGTH_SHORT).show();
                        }
                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(mContext, "Errore nel caricamento dei dati!", Toast.LENGTH_SHORT).show();

                        ProgressBar progressBar = (ProgressBar) mActivity.findViewById(R.id.prima_nota_banca_progress);
                        RecyclerView recyclerView = (RecyclerView) mActivity.findViewById(R.id.recyclerview_banca);
                        AndroidUtils.showProgress(recyclerView, progressBar, false); //Nascondo barra circolare di caricamento
                    }
                });

        accessiRequest.setShouldCache(false);
        mRequestQueue.add(accessiRequest);
    }

    /*public void AddNewNotaCassa(String json) {
        String url = mActivity.getString(R.string.mobile_url);
        url += "nota-cassa-nuovo";

        HashMap params = new HashMap();
        params.put("data", json);

        System.out.println(json);

        dialogBuilder.setTitle("Inserisci nuova cassa");

        PUTRequest modifyRequestJson = new PUTRequest(Request.Method.PUT, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean errore = response.getBoolean("error");
                    if (errore) {
                        showError(true);
                    } else {
                        showError(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showError(true);
            }
        });

        mRequestQueue.add(modifyRequestJson);
    }*/

    public void buildFilePart(DataOutputStream dos, byte[] fileData, String filename) throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: from-data; name=\"uploaded_file\"; filename=\"" + filename + "\"" + lineEnd);
        dos.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;

        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dos.writeBytes(lineEnd);
    }

    public void buildTextPart(DataOutputStream dos, byte[] fileData) throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: from-data; name=\"file_description\";" + lineEnd);
        dos.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;

        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dos.writeBytes(lineEnd);
    }


    byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);

        int read = fis.read(bytes, 0, size);
        if (read < size) {
            int remain = size - read;
            while (remain > 0) {
                read = fis.read(tmpBuff, 0, remain);
                System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                remain -= read;
            }
        }

        return bytes;
    }

    private void showWaitingDialog(boolean show) {
        if (show)
            mWaiting = ProgressDialog.show(mActivity, "", "Caricamento. Attendere...", true);
        else
            mWaiting.dismiss();
    }

}
