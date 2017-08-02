package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mcteam.gestapp.Models.Commerciale.Offerta;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Moduli.Gestionale.Allegati.AllegatiUtils;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Functions;

/**
 * @author Created by Riccardo Rossi on 17/10/2016.
 */
public class VisualOffertaActivity extends AppCompatActivity {

    private Offerta offerta;
    private Commessa commessa;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_offerta);

        offerta = getIntent().getParcelableExtra("OFFERTA");
        commessa = getIntent().getParcelableExtra("COMMESSA");

        TextView textOffertaCodComm = (TextView) findViewById(R.id.visual_offerta_codcomm);
        TextView textOffertaClient = (TextView) findViewById(R.id.visual_offerta_cliente);
        TextView textOffertaRef1 = (TextView) findViewById(R.id.visual_offerta_ref1);
        TextView textOffertaRef2 = (TextView) findViewById(R.id.visual_offerta_ref2);
        TextView textOffertaRef3 = (TextView) findViewById(R.id.visual_offerta_ref3);
        TextView textOffertaDataOff = (TextView) findViewById(R.id.visual_offerta_dataoff);
        TextView textOffertaObj = (TextView) findViewById(R.id.visual_offerta_oggetto); //E' il nome della commessa o cosa?
        CheckBox textOffertaPresent = (CheckBox) findViewById(R.id.visual_offerta_present);
        ImageView textOffertaAlleg = (ImageView) findViewById(R.id.visual_offerta_allegato);
        BootstrapButton btnModifica = (BootstrapButton) findViewById(R.id.visual_offerta_modifica);
        BootstrapButton btnElimina = (BootstrapButton) findViewById(R.id.visual_offerta_elimina);

        btnModifica.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent modificaIntent = new Intent(v.getContext(), ModificaOffertaActivity.class);
                modificaIntent.putExtra("OFFERTA", offerta);
                modificaIntent.putExtra("COMMESSA",commessa);
                v.getContext().startActivity(modificaIntent);
            }
        });

        btnElimina.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent eliminaIntent = new Intent(v.getContext(), EliminaOffertaActivity.class);
                eliminaIntent.putExtra("OFFERTA", offerta);
                eliminaIntent.putExtra("COMMESSA",commessa);
                v.getContext().startActivity(eliminaIntent);
            }
        });

        textOffertaCodComm.setText(commessa.getCodice_commessa());
        textOffertaClient.setText(commessa.getCliente() != null ? commessa.getCliente().getNomeSocietà() : "");
        textOffertaRef1.setText(commessa.getCommerciale() != null ? commessa.getCommerciale().getNome() + " " + commessa.getCommerciale().getCognome() : "");
        textOffertaRef2.setText(commessa.getReferente1() != null ? commessa.getReferente1().getNome() + " " + commessa.getReferente1().getCognome() : "");
        textOffertaRef3.setText(commessa.getReferente2() != null ? commessa.getReferente2().getNome() + " " + commessa.getReferente2().getCognome() : "");
        textOffertaDataOff.setText(Functions.getFormattedDate(offerta.getDataOfferta()));
        textOffertaObj.setText(commessa.getNome_commessa());
        textOffertaPresent.setChecked(offerta.getAccettata() == 1);
        textOffertaAlleg.setImageBitmap(AllegatiUtils.getAllegatoLogo(getResources(), offerta.getAllegato()));

    }
}
