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

import java.util.ArrayList;

public class MainActivity extends Activity {
    private DatabaseHelper dbHelper;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> materieList;
    private ArrayList<Integer> materieIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.listMaterie);
        materieList = new ArrayList<>();
        materieIdList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, materieList);
        listView.setAdapter(adapter);

        caricaMaterie();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, CapitoliActivity.class);
            intent.putExtra("materia_id", materieIdList.get(position));
            startActivity(intent);
        });

        findViewById(R.id.btnAggiungiMateria).setOnClickListener(v -> {
            EditText input = new EditText(this);
            new AlertDialog.Builder(this)
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
        materieIdList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, nome FROM Materie", null);
        while (cursor.moveToNext()) {
            materieIdList.add(cursor.getInt(0));
            materieList.add(cursor.getString(1));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void aggiungiMateria(String nome) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        db.insert("Materie", null, values);
    }
}

