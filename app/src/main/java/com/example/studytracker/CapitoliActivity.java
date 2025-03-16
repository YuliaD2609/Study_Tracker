package com.example.studytracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CapitoliActivity extends Activity {
    private DatabaseHelper dbHelper;
    private int materiaId;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> capitoliList;
    private ArrayList<Integer> capitoliIdList;
    private PieChartView pieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capitoli);

        dbHelper = new DatabaseHelper(this);
        materiaId = getIntent().getIntExtra("materia_id", -1);
        listView = findViewById(R.id.listCapitoli);
        capitoliList = new ArrayList<>();
        capitoliIdList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, capitoliList);
        listView.setAdapter(adapter);

        caricaCapitoli();

        findViewById(R.id.btnAggiungiCapitolo).setOnClickListener(v -> {
            EditText input = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Aggiungi Capitolo")
                    .setView(input)
                    .setPositiveButton("Salva", (dialog, which) -> {
                        aggiungiCapitolo(input.getText().toString());
                        caricaCapitoli();
                        aggiornaGrafico(); // Aggiorna il grafico dopo l'aggiunta
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

    private void caricaCapitoli() {
        capitoliList.clear();
        capitoliIdList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, nome, stato FROM Capitoli WHERE materia_id = ?", new String[]{String.valueOf(materiaId)});
        while (cursor.moveToNext()) {
            capitoliIdList.add(cursor.getInt(0));
            capitoliList.add(cursor.getString(1) + " - " + getStatoText(cursor.getInt(2)));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void aggiungiCapitolo(String nome) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("materia_id", materiaId);
        values.put("nome", nome);
        values.put("stato", 0);
        db.insert("Capitoli", null, values);
    }

    private void mostraOpzioniCapitolo(int capitoloId) {
        String[] stati = {"Non fatto", "Appuntato", "Studiato", "Esercizi"};
        new AlertDialog.Builder(this)
                .setTitle("Stato Capitolo")
                .setItems(stati, (dialog, which) -> {
                    dbHelper.aggiornaStatoCapitolo(capitoloId, which);
                    caricaCapitoli();
                    aggiornaGrafico(); // Aggiorna il grafico dopo la modifica dello stato
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

    private void aggiornaGrafico() {
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
