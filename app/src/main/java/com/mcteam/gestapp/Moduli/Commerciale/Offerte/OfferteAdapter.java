package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Moduli.Sistemi.OverflowOnClickListener;
import com.mcteam.gestapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Created by Riccardo Rossi on 02/10/2016.
 */
public class OfferteAdapter extends RecyclerView.Adapter<OfferteAdapter.MyViewHolder> {

    private ArrayList<Commessa> items;
    private OnItemClickListener listener;
    private HashMap<Character, Integer> mAlphabeticIndex = new HashMap<>();

    public interface OnItemClickListener {
        void onItemClick(Commessa item);
    }

    public OfferteAdapter(ArrayList<Commessa> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void clearAlphabeticIndex() {
        mAlphabeticIndex.clear();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_offerte_item,
                parent,
                false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //System.out.println("Chiamato onBindViewHolder alla posizione: " + position);
        holder.bind(items.get(position), listener, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView header, cliente, codCommessa, nomeCommessa;
        private ImageButton overflow;
        private OverflowOnClickListener overflowListener;

        public MyViewHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView.findViewById(R.id.commesse_item_header);
            cliente = (TextView) itemView.findViewById(R.id.commesse_item_cliente);
            codCommessa = (TextView) itemView.findViewById(R.id.commesse_item_codice_risorsa);
            nomeCommessa = (TextView) itemView.findViewById(R.id.commesse_item_nome);
            overflow = (ImageButton) itemView.findViewById(R.id.commesse_item_overflow);
        }

        public void bind(final Commessa commessa, final OnItemClickListener listener, int position) {
            Character firstChar = commessa.getCliente().getNomeSocietà().toUpperCase().charAt(0);

            if(!mAlphabeticIndex.containsKey(firstChar) || mAlphabeticIndex.get(firstChar) == position) {
                header.setText(String.valueOf(firstChar));
                header.setVisibility(View.VISIBLE);
                if(mAlphabeticIndex.get(firstChar) == null)
                    mAlphabeticIndex.put(firstChar, position);
            }
            else
                header.setVisibility(View.GONE);

            cliente.setText(commessa.getCliente().getNomeSocietà());
            codCommessa.setText(commessa.getCodice_commessa());
            nomeCommessa.setText(commessa.getNome_commessa());
            /*final CommesseOverflowListener overflowListener = new CommesseOverflowListener(commessa, itemView.getContext());
            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overflowListener.onClick(v);
                }
            });*/
            overflow.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(commessa);
                }
            });
        }
    }
}
