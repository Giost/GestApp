package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.mcteam.gestapp.Models.Commerciale.Offerta;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.R;
import com.mcteam.gestapp.Utils.Constants;
import com.mcteam.gestapp.Utils.OverflowPopupMenu;
import com.mcteam.gestapp.Utils.PopupListenerBuilder;

/**
 * @author Created by Riccardo Rossi on 15/10/2016.
 */

public class DettaglioOffertaOverflow implements View.OnClickListener {
    private Offerta mElement;
    private Commessa mCommessa;
    private Context mContext;


    public DettaglioOffertaOverflow(Offerta element, Commessa commessa, Context context) {
        mElement = element;
        mCommessa = commessa;
        mContext = context;
    }

    @Override
    public void onClick(View v) {

        //Creating the instance of PopupMenu
        OverflowPopupMenu popupMenu = new OverflowPopupMenu(mContext, v);
        //Inflating the Popup using xml file
        popupMenu.getMenuInflater().inflate(R.menu.overflow_menu_noprint, popupMenu.getMenu());

        //registering popup with OnMenuItemClickListener
        popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_action_elimina:
                        Intent eliminaIntent = new Intent(mContext, EliminaOffertaActivity.class);
                        eliminaIntent.putExtra("OFFERTA", mElement);
                        eliminaIntent.putExtra("COMMESSA",mCommessa);
                        mContext.startActivity(eliminaIntent);
                        return true;
                    case R.id.menu_action_modifica:
                        Intent modificaIntent = new Intent(mContext, ModificaOffertaActivity.class);
                        modificaIntent.putExtra("OFFERTA", mElement);
                        modificaIntent.putExtra("COMMESSA",mCommessa);
                        mContext.startActivity(modificaIntent);
                        return true;
                    default:
                        return false;
                }
            }
        });
        //Forza le icone a mostrarsi
        popupMenu.forceIconToShow();
        //Mostra il Popup
        popupMenu.show();

        /*PopupMenu popupMenu = new PopupMenu(mContext, v) {

            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_action_elimina:
                        Intent eliminaIntent = new Intent(mContext, EliminaOffertaActivity.class);
                        eliminaIntent.putExtra("OFFERTA_TO_DELETE", mElement);
                        mContext.startActivity(eliminaIntent);
                        return true;
                    case R.id.menu_action_modifica:
                        Intent modificaIntent = new Intent(mContext, NuovaOffertaActivity.class);
                        modificaIntent.putExtra("OFFERTA_TO_MODIFY", mElement);
                        mContext.startActivity(modificaIntent);
                        return true;
                    default:
                        return super.onMenuItemSelected(menu, item);
                }
            }
        };
        popupMenu.inflate(R.menu.accessi_overflow_menu);

        //Forse icons to show
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);

        } catch (Exception e) {
            e.printStackTrace();
            popupMenu.show();
            return;
        }
        popupMenu.show();*/
    }

}
