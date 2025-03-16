package com.example.studytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "study_tracker.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Materie (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT)");
        db.execSQL("CREATE TABLE Capitoli (id INTEGER PRIMARY KEY AUTOINCREMENT, materia_id INTEGER, nome TEXT, stato INTEGER, FOREIGN KEY(materia_id) REFERENCES Materie(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Capitoli");
        db.execSQL("DROP TABLE IF EXISTS Materie");
        onCreate(db);
    }

    /*** 📌 1. Metodo per aggiungere una nuova materia ***/
    public long aggiungiMateria(String nome) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        return db.insert("Materie", null, values);
    }

    /*** 📌 2. Metodo per aggiungere un nuovo capitolo ***/
    public long aggiungiCapitolo(int materiaId, String nome, int stato) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("materia_id", materiaId);
        values.put("nome", nome);
        values.put("stato", stato);
        return db.insert("Capitoli", null, values);
    }

    /*** 📌 3. Metodo per aggiornare lo stato di un capitolo ***/
    public void aggiornaStatoCapitolo(int capitoloId, int stato) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("stato", stato);
        db.update("Capitoli", values, "id = ?", new String[]{String.valueOf(capitoloId)});
    }


    /*** 📌 4. Metodo per eliminare una materia e tutti i suoi capitoli ***/
    public void eliminaMateria(int materiaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Capitoli", "materia_id = ?", new String[]{String.valueOf(materiaId)});
        db.delete("Materie", "id = ?", new String[]{String.valueOf(materiaId)});
    }

    /*** 📌 5. Metodo per eliminare un singolo capitolo ***/
    public void eliminaCapitolo(int capitoloId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Capitoli", "id = ?", new String[]{String.valueOf(capitoloId)});
    }

    /*** 📌 6. Metodo per ottenere il numero di capitoli per stato di una certa materia ***/
    public HashMap<Integer, Integer> getStatiCapitoli(int materiaId) {
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<Integer, Integer> stati = new HashMap<>();
        stati.put(0, 0); // Non fatto
        stati.put(1, 0); // Appuntato
        stati.put(2, 0); // Studiato
        stati.put(3, 0); // Esercizi fatti

        String query = "SELECT stato, COUNT(*) FROM Capitoli WHERE materia_id = ? GROUP BY stato";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(materiaId)});

        if (cursor.moveToFirst()) {
            do {
                int stato = cursor.getInt(0);
                int count = cursor.getInt(1);
                stati.put(stato, count);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return stati;
    }

    /*** 📌 Metodo per ottenere il numero di capitoli per ciascun stato di una materia ***/
    public HashMap<Integer, Integer> getConteggioStatiCapitoli(int materiaId) {
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<Integer, Integer> stati = new HashMap<>();

        // Inizializza tutti gli stati con valore 0 per evitare NullPointerException
        stati.put(0, 0); // Non fatto
        stati.put(1, 0); // Appuntato
        stati.put(2, 0); // Studiato
        stati.put(3, 0); // Esercizi fatti

        // Query per ottenere il numero di capitoli per ciascun stato
        String query = "SELECT stato, COUNT(*) FROM Capitoli WHERE materia_id = ? GROUP BY stato";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(materiaId)});

        if (cursor.moveToFirst()) {
            do {
                int stato = cursor.getInt(0);
                int count = cursor.getInt(1);
                stati.put(stato, count);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return stati;
    }

    public String getNomeMateria(int materiaId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String nomeMateria = "Materia Sconosciuta";  // Default nel caso non venga trovata

        Cursor cursor = db.rawQuery("SELECT nome FROM Materie WHERE id = ?", new String[]{String.valueOf(materiaId)});
        if (cursor.moveToFirst()) {
            nomeMateria = cursor.getString(0);
        }
        cursor.close();
        return nomeMateria;
    }

    /*** 📌 7. Metodo per ottenere tutti i capitoli di una materia ***/
    public ArrayList<Capitolo> getCapitoli(int materiaId) {
        ArrayList<Capitolo> listaCapitoli = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, materia_id, nome, stato FROM Capitoli WHERE materia_id = ?",
                new String[]{String.valueOf(materiaId)}
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                int mId = cursor.getInt(1);
                String nome = cursor.getString(2);
                int stato = cursor.getInt(3);

                Capitolo capitolo = new Capitolo(id, mId, nome, stato);
                listaCapitoli.add(capitolo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaCapitoli;
    }


}
