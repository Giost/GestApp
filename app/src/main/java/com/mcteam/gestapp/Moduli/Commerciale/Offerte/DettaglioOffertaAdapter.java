package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcteam.gestapp.Models.Commerciale.Offerta;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Moduli.Gestionale.Allegati.AllegatiUtils;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

import java.util.ArrayList;

/**
 * @author Created by Riccardo Rossi on 13/10/2016.
 */

public class DettaglioOffertaAdapter extends RecyclerView.Adapter<DettaglioOffertaAdapter.MyViewHolder> {

    private ArrayList<Offerta> items;
    private Commessa commessa;

    public DettaglioOffertaAdapter(ArrayList<Offerta> items, Commessa commessa) {
        this.items = items;
        this.commessa = commessa;
    }

    public interface OnItemClickListener {
        void onItemClick(Offerta item);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_dett_offerte_item,
                parent,
                false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView holderVersione, holderDataOfferta, holderAccettata;
        private ImageView holderOverflow, holderAllegato;
        private Context activityContext;

        public MyViewHolder(View itemView) {
            super(itemView);
            holderVersione = (TextView) itemView.findViewById(R.id.offerte_item_versione);
            holderDataOfferta = (TextView) itemView.findViewById(R.id.offerte_item_data);
            holderAccettata = (TextView) itemView.findViewById(R.id.offerte_item_accettata);
            holderAllegato = (ImageView) itemView.findViewById(R.id.offerte_item_allegato);
            holderOverflow = (ImageView) itemView.findViewById(R.id.offerte_item_overflow);
            activityContext = itemView.getContext();
        }

        public void bind(final Offerta offerta) {
            holderVersione.setText("v_"+String.valueOf(offerta.getVersione()));
            holderDataOfferta.setText(Functions.getFormattedDate(offerta.getDataOfferta()));
            holderAccettata.setText(offerta.getAccettata()== 0 ? "NO" : "SI");
            holderAllegato.setImageBitmap(AllegatiUtils.getAllegatoLogo(activityContext.getResources(), offerta.getAllegato()));
            DettaglioOffertaOverflow listener = new DettaglioOffertaOverflow(offerta,commessa, activityContext);
            holderOverflow.setOnClickListener(listener);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activityContext, VisualOffertaActivity.class);
                    i.putExtra("OFFERTA", offerta);
                    i.putExtra("COMMESSA", commessa);
                    activityContext.startActivity(i);
                }
            });
        }
    }
}
