package com.example.studytracker;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
        TextView txtStato = convertView.findViewById(R.id.textStato);
        txtStato.setText(getStatoText(capitolo.getStato()));

        txtStato.setOnClickListener(v -> {
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

        ImageButton eserciziIcon = convertView.findViewById(R.id.eserciziIcon);

        // Imposta alpha al 25% di default
        if (capitolo.getHaEsercizi() == 0) {
            eserciziIcon.setAlpha(0.25f); // 25% visibile
            eserciziIcon.setImageResource(R.drawable.img); // Icona generica esercizi
        } else if (capitolo.getHaEsercizi() == 1 && capitolo.getEserciziFatti() == 0) {
            eserciziIcon.setAlpha(1.0f); // 100% visibile
            eserciziIcon.setImageResource(R.drawable.img); // Da fare
        } else if (capitolo.getHaEsercizi() == 1 && capitolo.getEserciziFatti() == 1) {
            eserciziIcon.setAlpha(1.0f); // 100% visibile
            eserciziIcon.setImageResource(R.drawable.tick); // Fatti
        }

// ⚙️ Logica click per cicli di stato
        eserciziIcon.setOnClickListener(v -> {
            if (capitolo.getHaEsercizi() == 0) {
                // Passa a "ha esercizi da fare"
                capitolo.setHaEsercizi(1);
                capitolo.setEserciziFatti(0);
                eserciziIcon.setAlpha(1.0f); // pienamente visibile
                eserciziIcon.setImageResource(R.drawable.img);

                // Salva nel DB
                dbHelper.aggiornaHaEsercizi(capitolo.getId(), 1);
                dbHelper.aggiornaEserciziFatti(capitolo.getId(), 0);

            } else if (capitolo.getHaEsercizi() == 1 && capitolo.getEserciziFatti() == 0) {
                // Passa a "esercizi fatti"
                capitolo.setEserciziFatti(1);
                eserciziIcon.setImageResource(R.drawable.tick);

                // Salva nel DB
                dbHelper.aggiornaEserciziFatti(capitolo.getId(), 1);

            } else if (capitolo.getHaEsercizi() == 1 && capitolo.getEserciziFatti() == 1) {
                // Torna a "nessun esercizio"
                capitolo.setHaEsercizi(0);
                capitolo.setEserciziFatti(0);
                eserciziIcon.setAlpha(0.25f); // Semi-trasparente
                eserciziIcon.setImageResource(R.drawable.img); // Icona generica

                // Salva nel DB
                dbHelper.aggiornaHaEsercizi(capitolo.getId(), 0);
                dbHelper.aggiornaEserciziFatti(capitolo.getId(), 0);
            }
            CapitoliActivity.aggiornaGraficoEsercizi();
        });


        return convertView;
    }

    private String getStatoText(int stato) {
        switch (stato) {
            case 1: return "Appuntato";
            case 2: return "Studiato";
            default: return "Non fatto";
        }
    }

    private void mostraOpzioniCapitolo(int capitoloId) {
        String[] opzioni = {"Non fatto", "Appuntato", "Studiato"};

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
