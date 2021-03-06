package com.mcteam.gestapp.Moduli.Gestionale.Associazioni;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ListView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mcteam.gestapp.Models.UserInfo;
import com.mcteam.gestapp.R;

import java.util.ArrayList;

public class ConsulenteMultiSelectionDialog extends AppCompatActivity implements View.OnClickListener {

    ListView mMultiSelectionListView;
    ConsulentiListAdapter mListAdapter;

    ArrayList<UserInfo> consulentiList;
    ArrayList<UserInfo> alreadySelectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulente_multi_selection);

        //Permette landscape e portrait solo se è un tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        consulentiList = getIntent().getParcelableArrayListExtra("consulentiList");
        alreadySelectedItems = getIntent().getParcelableArrayListExtra("selectedConsulenti");

        mListAdapter = new ConsulentiListAdapter(this, consulentiList);

        mMultiSelectionListView = (ListView) findViewById(R.id.consulente_multi_selection);

        mMultiSelectionListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mMultiSelectionListView.setAdapter(mListAdapter);


        for (UserInfo selected : alreadySelectedItems) {
            int index = consulentiList.indexOf(selected);
            mMultiSelectionListView.setItemChecked(index, true);
        }

        BootstrapButton getSelected = (BootstrapButton) findViewById(R.id.multiple_consulente_get_selected);
        BootstrapButton annulla = (BootstrapButton) findViewById(R.id.multiple_consulente_annulla);

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<UserInfo> selectedConsulenti = new ArrayList<UserInfo>();
                SparseBooleanArray checkedItems = mMultiSelectionListView.getCheckedItemPositions();
                int itemCheckedCount = checkedItems.size();
                for (int i = 0; i < itemCheckedCount; ++i) {
                    int position = checkedItems.keyAt(i);
                    if (checkedItems.valueAt(i)) {
                        selectedConsulenti.add(mListAdapter.getItem(position));
                    }
                }
                Intent data = new Intent();
                data.putParcelableArrayListExtra("selectedItems", selectedConsulenti);
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }
                finish();
            }
        });


    }

    @Override
    public void onClick(View v) {

    }
}
