package com.example.studytracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView listViewMaterie;
    private MateriaAdapter adapter;
    private ArrayList<Materia> materieList = new ArrayList<>();
    private ArrayList<Integer> materieIdList;

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
}

