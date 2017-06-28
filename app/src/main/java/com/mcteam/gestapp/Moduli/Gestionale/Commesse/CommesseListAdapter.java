package com.mcteam.gestapp.Moduli.Gestionale.Commesse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Riccardo Rossi on 16/12/2015.
 */
public class CommesseListAdapter extends ArrayAdapter<Commessa> {

    private ArrayList<Commessa> mRubrica;
    HashMap<String, Integer> mAlphabeticIndex = new HashMap<>();
    boolean isStorico = false;
    CommesseOverflowListener overflowOnClickListener = null;
    StoricoCommesseOverflowListener storicoCommesseOverflowListener = null;

    public CommesseListAdapter(Context context, ArrayList<Commessa> rubrica, boolean storico) {
        super(context, 0, rubrica);
        mRubrica = rubrica;
        isStorico = storico;
    }

    public void clearAlphabeticIndex() {
        mAlphabeticIndex.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the data from the position
        Commessa commessa = getItem(position);

        String iniziale = null;

        if (commessa.getCliente() != null && commessa.getCliente().getNomeSocietà() != null) {
            String nome = commessa.getCliente().getNomeSocietà();
            iniziale = nome.substring(0, 1);
            iniziale = iniziale.toUpperCase(Locale.ITALIAN);
            if (!mAlphabeticIndex.containsKey(iniziale))
                mAlphabeticIndex.put(iniziale, position);
        }

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_commesse_item, parent, false);


        TextView txtCliente = (TextView) convertView.findViewById(R.id.commesse_item_cliente);
        TextView txtCodice = (TextView) convertView.findViewById(R.id.commesse_item_codice_risorsa);
        TextView txtNome = (TextView) convertView.findViewById(R.id.commesse_item_nome);
        TextView txtHeader = (TextView) convertView.findViewById(R.id.commesse_item_header);

        if (commessa.getCliente() != null)
            txtCliente.setText(commessa.getCliente().getNomeSocietà());
        txtNome.setText(commessa.getNome_commessa());

        if (commessa.getCommerciale() != null)
            txtCodice.setText(commessa.getCodice_commessa());

        ImageButton overflow = (ImageButton) convertView.findViewById(R.id.commesse_item_overflow);

        if (!isStorico) {
            overflowOnClickListener = new CommesseOverflowListener(commessa, getContext());
            overflow.setOnClickListener(overflowOnClickListener);
        } else {
            storicoCommesseOverflowListener = new StoricoCommesseOverflowListener(commessa, getContext());
            overflow.setOnClickListener(storicoCommesseOverflowListener);
        }

        if (iniziale != null) {
            if (mAlphabeticIndex.containsValue(position)) {
                txtHeader.setText(iniziale);
            } else {
                txtHeader.setVisibility(View.GONE);
            }
        }

        return convertView;
    }
}

