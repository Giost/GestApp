package com.mcteam.gestapp.Moduli.Gestionale.Nominativo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mcteam.gestapp.Models.Rubrica.Nominativo;
import com.mcteam.gestapp.Models.UserInfo;
import com.mcteam.gestapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Riccardo Rossi on 18/11/2015.
 */
public class RubricaNominativaListAdapter extends ArrayAdapter<Nominativo> {

    private ArrayList<Nominativo> mRubrica;
    NominativoOverflowOnClickListener overflowOnClickListener;
    UserInfo mUser;
    HashMap<String, Integer> mAlphabeticIndex = new HashMap<>();


    public RubricaNominativaListAdapter(Context context, ArrayList<Nominativo> rubrica, UserInfo user) {
        super(context, 0, rubrica);
        mRubrica = rubrica;
        mUser = user;
    }
    /**
     * Azzera la suddivisione in lettere, deve essere richiamato prima di ogni notifyDataChanged, cioè
     * prima di ogni ricaricamento della ListView
     */
    public void cleanAlphabeticIndex() {
        mAlphabeticIndex.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the data from the position
        Nominativo nominativo = getItem(position);
        //ViewHolder holder = null;

        String nome = nominativo.getCognome();
        String iniziale = nome.substring(0, 1);
        iniziale = iniziale.toUpperCase(Locale.ITALIAN);
        if (!mAlphabeticIndex.containsKey(iniziale))
            mAlphabeticIndex.put(iniziale, position);

        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        //if(convertView == null){

        convertView = mInflater.inflate(R.layout.rubrica_nominativo_item, null);

        //}

        overflowOnClickListener = new NominativoOverflowOnClickListener(nominativo, getContext(), mUser);

        //holder = new ViewHolder();
        TextView txtCognome = (TextView) convertView.findViewById(R.id.rubrica_nominativo_cognome);
        TextView txtNome = (TextView) convertView.findViewById(R.id.rubrica_nominativo_nome);
        TextView txtSocieta = (TextView) convertView.findViewById(R.id.rubrica_nominativo_societa);
        TextView txtHeader = (TextView) convertView.findViewById(R.id.rubrica_nominativo_header);

        if (mAlphabeticIndex.containsValue(position)) {
            txtHeader.setText(iniziale);
        } else {
            txtHeader.setVisibility(View.GONE);
        }

        txtNome.setText(nominativo.getNome());
        txtCognome.setText(nominativo.getCognome());

        if (nominativo.getSocieta() != null)
            txtSocieta.setText(nominativo.getSocieta().getNomeSocietà());

        ImageButton overflow = (ImageButton) convertView.findViewById(R.id.rubrica_nominativo_overflow);

        overflow.setOnClickListener(overflowOnClickListener);

        return convertView;
    }

}
