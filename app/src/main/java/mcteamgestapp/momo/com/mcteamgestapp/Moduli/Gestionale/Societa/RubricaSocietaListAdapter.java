package mcteamgestapp.momo.com.mcteamgestapp.Moduli.Gestionale.Societa;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import mcteamgestapp.momo.com.mcteamgestapp.Models.Rubrica.Societa;
import mcteamgestapp.momo.com.mcteamgestapp.R;

/**
 * Created by meddaakouri on 18/11/2015.
 */
public class RubricaSocietaListAdapter extends ArrayAdapter<Societa> {

    private List<Societa> mRubrica = new ArrayList<>();
    Context mContext;
    HashMap<String, Integer> mAlphabeticIndex = new HashMap<>();

    public RubricaSocietaListAdapter(Context context, List<Societa> rubrica) {
        super(context, 0, rubrica);
        mContext = context;
        mRubrica.clear();
        mRubrica.addAll(rubrica);
    }

    public void clearAlphabeticIndex() {
        mAlphabeticIndex.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Societa societa = getItem(position);

        String nome = societa.getNomeSocietà();
        //System.out.println("position: " + position + ", name: " + nome);
        Log.d("NOME: ", nome);
        String iniziale = nome.substring(0, 1);

        iniziale = iniziale.toUpperCase(Locale.ITALIAN);
        if (!mAlphabeticIndex.containsKey(iniziale))
            mAlphabeticIndex.put(iniziale, position);


        //get the data from the position
        //ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        //if(convertView == null){

        convertView = mInflater.inflate(R.layout.rubrica_societa_item, null);

        //holder = new ViewHolder();

        TextView txtIndirizzo = (TextView) convertView.findViewById(R.id.rubrica_societa_indirizzo);
        TextView txtNome = (TextView) convertView.findViewById(R.id.rubrica_societa_nome);
        TextView txtTelfono = (TextView) convertView.findViewById(R.id.rubrica_societa_phone);
        ImageButton overflowButton = (ImageButton) convertView.findViewById(R.id.rubrica_societa_overflow);
        TextView txtHeader = (TextView) convertView.findViewById(R.id.header_text_letter);

        //}else {
        //  holder = (ViewHolder)convertView.getTag();
        //}

        if (mAlphabeticIndex.containsValue(position)) {
            txtHeader.setText(iniziale);
        } else {
            txtHeader.setVisibility(View.GONE);
        }

        SocietaOverflowClickListener overflowClickListener = new SocietaOverflowClickListener(societa, getContext());

        txtNome.setText(societa.getNomeSocietà());
        txtIndirizzo.setText(societa.getIndirizzo());
        txtTelfono.setText(societa.getmTelefono());
        overflowButton.setOnClickListener(overflowClickListener);


        return convertView;
    }

    private class ViewHolder {
        TextView txtHeader;
        TextView txtNome;
        TextView txtIndirizzo;
        TextView txtTelfono;
        ImageButton overflowButton;
    }
}
