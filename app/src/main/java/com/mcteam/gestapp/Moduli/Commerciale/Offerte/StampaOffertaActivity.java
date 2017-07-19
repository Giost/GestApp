package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcteam.gestapp.Models.Commerciale.Offerta;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Moduli.Gestionale.Allegati.AllegatiUtils;
import com.mcteam.gestapp.R;

public class StampaOffertaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stampa_offerta);

        Offerta offerta = getIntent().getParcelableExtra("OFFERTA");
        Commessa commessa = getIntent().getParcelableExtra("COMMESSA");

        TextView textOffertaCodComm = (TextView) findViewById(R.id.stampa_offerta_codcomm);
        TextView textOffertaClient = (TextView) findViewById(R.id.stampa_offerta_cliente);
        TextView textOffertaRef1 = (TextView) findViewById(R.id.stampa_offerta_ref1);
        TextView textOffertaRef2 = (TextView) findViewById(R.id.stampa_offerta_ref2);
        TextView textOffertaRef3 = (TextView) findViewById(R.id.stampa_offerta_ref3);
        TextView textOffertaDataOff = (TextView) findViewById(R.id.stampa_offerta_dataoff);
        TextView textOffertaObj = (TextView) findViewById(R.id.stampa_offerta_oggetto); //E' il nome della commessa o cosa?
        CheckBox textOffertaPresent = (CheckBox) findViewById(R.id.stampa_offerta_present);
        ImageView textOffertaAlleg = (ImageView) findViewById(R.id.stampa_offerta_allegato);
        Button stampa = (Button) findViewById(R.id.stampa_offerta_conferma_btn);

        stampa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //NominativoUtils.printSimple(mNominativoAttuale, getApplicationContext(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button annulla = (Button) findViewById(R.id.stampa_offerta_annulla_btn);

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textOffertaCodComm.setText(commessa.getCodice_commessa());
        textOffertaClient.setText(commessa.getCliente().getNomeSocietà());
        textOffertaRef1.setText(commessa.getCommerciale().getNome() + commessa.getCommerciale().getCognome());
        textOffertaRef2.setText(commessa.getReferente1().getNome() + commessa.getReferente1().getCognome());
        textOffertaRef3.setText(commessa.getReferente2().getNome() + commessa.getReferente2().getCognome());
        textOffertaDataOff.setText(offerta.getDataOfferta());
        textOffertaObj.setText(commessa.getNome_commessa()); //Non so principalmente che cosa settare, dal doc non si capisce
        textOffertaPresent.setChecked(offerta.getAccettata() == 1);
        textOffertaAlleg.setImageBitmap(AllegatiUtils.getAllegatoLogo(getResources(), offerta.getAllegato()));
    }
}
