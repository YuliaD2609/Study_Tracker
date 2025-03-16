package com.example.studytracker;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.BaseAdapter;
import java.util.List;

public class CapitoliAdapter extends BaseAdapter {
    private Context context;
    private List<Capitolo> capitoliList;
    private DatabaseHelper dbHelper;
    private CapitoliActivity activity;

    public CapitoliAdapter(Context context, List<Capitolo> capitoliList, CapitoliActivity activity) {
        this.context = context;
        this.capitoliList = capitoliList;
        this.dbHelper = new DatabaseHelper(context);
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return capitoliList.size();
    }

    @Override
    public Object getItem(int position) {
        return capitoliList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return capitoliList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_capitolo, parent, false);
        }

        TextView txtCapitoloNome = convertView.findViewById(R.id.txtCapitoloNome);
        ImageButton btnEliminaCapitolo = convertView.findViewById(R.id.btnEliminaCapitolo);

        Capitolo capitolo = capitoliList.get(position);
        txtCapitoloNome.setText(capitolo.getNome());

        txtCapitoloNome.setOnClickListener(v -> {
            mostraOpzioniCapitolo(capitolo.getId());
        });

        // Gestisce il click per eliminare il capitolo
        btnEliminaCapitolo.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Elimina Capitolo")
                    .setMessage("Sei sicuro di voler eliminare \"" + capitolo.getNome() + "\"?")
                    .setPositiveButton("Elimina", (dialog, which) -> {
                        dbHelper.eliminaCapitolo(capitolo.getId());
                        capitoliList.remove(position);
                        notifyDataSetChanged();
                        CapitoliActivity.aggiornaGrafico();
                    })
                    .setNegativeButton("Annulla", null)
                    .show();
        });

        return convertView;
    }

    private void mostraOpzioniCapitolo(int capitoloId) {
        String[] opzioni = {"Non fatto", "Appuntato", "Studiato", "Esercizi"};

        new AlertDialog.Builder(context)
                .setTitle("Stato Capitolo")
                .setItems(opzioni, (dialog, which) -> {
                    if (which == 4) { // Se ha scelto "Elimina"
                        dbHelper.eliminaCapitolo(capitoloId);
                    } else {
                        dbHelper.aggiornaStatoCapitolo(capitoloId, which); // Aggiorna stato
                    }
                    CapitoliActivity.caricaCapitoli(); // Ricarica la lista
                    CapitoliActivity.aggiornaGrafico(); // Aggiorna il grafico
                })
                .show();
    }
}
