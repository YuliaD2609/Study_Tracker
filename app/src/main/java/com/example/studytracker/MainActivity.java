package com.example.studytracker;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView listViewMaterie;
    private MateriaAdapter adapter;
    private ArrayList<Materia> materieList = new ArrayList<>();
    private static PieChartView pieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dbHelper = new DatabaseHelper(this);
        listViewMaterie = findViewById(R.id.listViewMaterie);
        adapter = new MateriaAdapter(this, materieList);
        listViewMaterie.setAdapter(adapter);

        caricaMaterie();

        findViewById(R.id.btnAggiungiMateria).setOnClickListener(v -> {
            EditText input = new EditText(this);
            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("Aggiungi Materia")
                    .setView(input)
                    .setPositiveButton("Salva", (dialog, which) -> {
                        aggiungiMateria(input.getText().toString());
                        caricaMaterie();
                    })
                    .setNegativeButton("Annulla", null)
                    .show();
        });
        pieChartView = findViewById(R.id.pieChartView);
        aggiornaGrafico();
    }

    private void caricaMaterie() {
        materieList.clear();
        materieList.addAll(dbHelper.getMaterie());
        adapter.notifyDataSetChanged();
    }

    private void aggiungiMateria(String nome) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        db.insert("Materie", null, values);
    }

    public void aggiornaGrafico() {
        // Inizializza i totali per tutti gli stati
        int totaleNonFatto = 0;
        int totaleAppuntato = 0;
        int totaleStudiato = 0;
        int totaleEsercizi = 0;

        // Recupera tutte le materie
        List<Materia> materie = dbHelper.getMaterie();

        // Somma i conteggi degli stati per ogni materia
        for (Materia materia : materie) {
            // Recupera il conteggio degli stati dei capitoli per ogni materia
            HashMap<Integer, Integer> stati = dbHelper.getConteggioStatiCapitoli(materia.getId());

            totaleNonFatto += stati.getOrDefault(0, 0);
            totaleAppuntato += stati.getOrDefault(1, 0);
            totaleStudiato += stati.getOrDefault(2, 0);
            totaleEsercizi += stati.getOrDefault(3, 0);
        }

        // Calcola il totale complessivo
        int totale = totaleNonFatto + totaleAppuntato + totaleStudiato + totaleEsercizi;

        // Evita divisione per 0
        if (totale == 0) totale = 1;

        // Calcola le percentuali per ciascuna categoria
        float percNonFatto = (totaleNonFatto * 100f) / totale;
        float percAppuntato = (totaleAppuntato * 100f) / totale;
        float percStudiato = (totaleStudiato * 100f) / totale;
        float percEsercizi = (totaleEsercizi * 100f) / totale;

        // Dati completi
        List<Float> datiCompleti = Arrays.asList(
                (float) totaleNonFatto,
                (float) totaleAppuntato,
                (float) totaleStudiato,
                (float) totaleEsercizi
        );

        // Colori personalizzati
        List<Integer> coloriCompleti = Arrays.asList(
                Color.parseColor("#F8A6A6"), // Rosso per Non fatto
                Color.parseColor("#F9E28B"), // Giallo per Appuntato
                Color.parseColor("#A2E8B7"), // Verde per Studiato
                Color.parseColor("#A7C9F9")  // Blu per Esercizi
        );

        // Etichette con percentuali
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


}

