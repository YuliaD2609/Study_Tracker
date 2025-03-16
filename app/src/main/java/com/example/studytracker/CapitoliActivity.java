package com.example.studytracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
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

        pieChartView.setData(
                Arrays.asList((float) nonFatto, (float) appuntato, (float) studiato, (float) esercizi),
                Arrays.asList(Color.GRAY, Color.YELLOW, Color.GREEN, Color.BLUE)
        );
    }

}

