package com.example.studytracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.graphics.Color;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CapitoliActivity extends AppCompatActivity {
    private static DatabaseHelper dbHelper;
    private static int materiaId;
    private ListView listView;
    private static CapitoliAdapter adapter;
    private static ArrayList<Capitolo> capitoliList=new ArrayList<>();
    private static ArrayList<Integer> capitoliIdList=new ArrayList<>();
    private static PieChartView pieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capitoli);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        dbHelper = new DatabaseHelper(this);
        materiaId = getIntent().getIntExtra("materia_id", -1);

        TextView nomeMateriaTextView = findViewById(R.id.nomeMateria);
        nomeMateriaTextView.setText("Capitoli di " + dbHelper.getNomeMateria(materiaId));

        listView = findViewById(R.id.listCapitoli);
        adapter = new CapitoliAdapter(this, capitoliList, this);
        listView.setAdapter(adapter);

        caricaCapitoli();

        findViewById(R.id.btnAggiungiCapitolo).setOnClickListener(v -> {
            EditText input = new EditText(this);
            new androidx.appcompat.app.AlertDialog.Builder(CapitoliActivity.this)
                    .setTitle("Aggiungi Capitolo")
                    .setView(input)
                    .setPositiveButton("Salva", (dialog, which) -> {
                        aggiungiCapitolo(input.getText().toString());
                        caricaCapitoli();
                        aggiornaGrafico();
                    })
                    .setNegativeButton("Annulla", null)
                    .show();
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            mostraOpzioniCapitolo(capitoliIdList.get(position));
        });

        pieChartView = findViewById(R.id.pieChartView);
        if (materiaId != -1) {
            aggiornaGrafico();
        }

    }

    public static void caricaCapitoli() {
        capitoliList.clear(); // Pulisce la lista attuale
        capitoliIdList.clear(); // Pulisce la lista ID

        // Recupera e aggiunge capitoli alla lista già collegata all'adapter
        capitoliList.addAll(dbHelper.getCapitoli(materiaId));
        for (Capitolo c : capitoliList) {
            capitoliIdList.add(c.getId());
        }

        adapter.notifyDataSetChanged(); // Notifica l'adapter della modifica
    }


    private void aggiungiCapitolo(String nome) {
        dbHelper.aggiungiCapitolo(materiaId, nome, 0);
    }

    private void mostraOpzioniCapitolo(int capitoloId) {
        String[] opzioni = {"Non fatto", "Appuntato", "Studiato", "Esercizi"};

        new AlertDialog.Builder(this)
                .setTitle("Stato Capitolo")
                .setItems(opzioni, (dialog, which) -> {
                    if (which == 4) { // Se ha scelto "Elimina"
                        dbHelper.eliminaCapitolo(capitoloId);
                    } else {
                        dbHelper.aggiornaStatoCapitolo(capitoloId, which); // Aggiorna stato
                    }
                    caricaCapitoli(); // Ricarica la lista
                    aggiornaGrafico(); // Aggiorna il grafico
                })
                .show();
    }

    private String getStatoText(int stato) {
        switch (stato) {
            case 1: return "Appuntato";
            case 2: return "Studiato";
            case 3: return "Esercizi";
            default: return "Non fatto";
        }
    }

    public static void aggiornaGrafico() {
        HashMap<Integer, Integer> stati = dbHelper.getConteggioStatiCapitoli(materiaId);

        int nonFatto = stati.get(0);
        int appuntato = stati.get(1);
        int studiato = stati.get(2);
        int esercizi = stati.get(3);

        int totale = nonFatto + appuntato + studiato + esercizi;

        // Evita divisione per 0
        if (totale == 0) totale = 1;

        // Percentuali per le etichette
        float percNonFatto = (nonFatto * 100f) / totale;
        float percAppuntato = (appuntato * 100f) / totale;
        float percStudiato = (studiato * 100f) / totale;
        float percEsercizi = (esercizi * 100f) / totale;

        // Liste complete
        List<Float> datiCompleti = Arrays.asList(
                (float) nonFatto,
                (float) appuntato,
                (float) studiato,
                (float) esercizi
        );

        List<Integer> coloriCompleti = Arrays.asList(
                Color.parseColor("#F2548B"), // Rosso per Non fatto
                Color.parseColor("#FFF690"), // Giallo per Appuntato
                Color.parseColor("#FA98FF8D"), // Verde per Studiato
                Color.parseColor("#FA8DDDFF")  // Blu per Esercizi
        );

        List<String> labelsCompleti = Arrays.asList(
                "Non fatto: " + String.format("%.1f", percNonFatto) + "%",
                "Appuntato: " + String.format("%.1f", percAppuntato) + "%",
                "Studiato: " + String.format("%.1f", percStudiato) + "%",
                "Esercizi: " + String.format("%.1f", percEsercizi) + "%"
        );

        // 🔎 Filtra solo quelli con valore > 0
        List<Float> datiFiltrati = new ArrayList<>();
        List<Integer> coloriFiltrati = new ArrayList<>();
        List<String> labelsFiltrati = new ArrayList<>();

        for (int i = 0; i < datiCompleti.size(); i++) {
            if (datiCompleti.get(i) > 0) {
                datiFiltrati.add(datiCompleti.get(i));
                coloriFiltrati.add(coloriCompleti.get(i));
                labelsFiltrati.add(labelsCompleti.get(i));
            }
        }

        // Passa solo i dati filtrati al PieChartView
        pieChartView.setDataWithLabels(datiFiltrati, coloriFiltrati, labelsFiltrati);
    }

    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

