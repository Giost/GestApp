package com.mcteam.gestapp.Moduli.Amministrazione.RubricaBanche;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mcteam.gestapp.Models.Rubrica.Banca;
import com.mcteam.gestapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Riccardo Rossi on 14/01/2016.
 */
public class RubricaBancaListAdapter extends ArrayAdapter<Banca> {

    Context mContext;
    ArrayList<Banca> mBancheList;
    BancaOverflowOnClickListener overflowOnClickListener;
    HashMap<String, Integer> mAlphabeticIndex = new HashMap<>();


    public RubricaBancaListAdapter(Context context, ArrayList<Banca> rubrica) {
        super(context, 0, rubrica);
        mBancheList = rubrica;
        mContext = context;
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

        Banca banca = getItem(position);

        String nome = banca.getNome();
        String iniziale = nome.substring(0, 1);
        iniziale = iniziale.toUpperCase(Locale.ITALIAN);
        if (!mAlphabeticIndex.containsKey(iniziale))
            mAlphabeticIndex.put(iniziale, position);

        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        //if(convertView == null){

        convertView = mInflater.inflate(R.layout.banca_item, null);

        //}

        overflowOnClickListener = new BancaOverflowOnClickListener(banca, getContext());

        TextView txtNomeBanca = (TextView) convertView.findViewById(R.id.banca_item_nome_banca);
        TextView txtHeader = (TextView) convertView.findViewById(R.id.banca_item_header);

        if (mAlphabeticIndex.containsValue(position)) {
            txtHeader.setText(iniziale);
        } else {
            txtHeader.setVisibility(View.GONE);
        }

        txtNomeBanca.setText(banca.getNome());

        ImageButton overflow = (ImageButton) convertView.findViewById(R.id.rubrica_banca_overflow);

        overflow.setOnClickListener(overflowOnClickListener);

        return convertView;
    }
}
