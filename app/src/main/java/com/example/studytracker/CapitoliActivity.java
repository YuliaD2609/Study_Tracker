package com.example.studytracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private static PieChartView pieChartEsercizi;

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
            mostraDialogAggiungiCapitoli();
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            mostraOpzioniCapitolo(capitoliIdList.get(position));
        });
        pieChartEsercizi = findViewById(R.id.pieChartViewEsercizi);
        pieChartView = findViewById(R.id.pieChartView);
        CapitoliActivity.aggiornaGrafico();
        CapitoliActivity.aggiornaGraficoEsercizi();

    }

    private void mostraDialogAggiungiCapitoli() {
        // Crea un AlertDialog con un EditText per l'inserimento dei capitoli
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Inserisci i capitoli separati da virgola");

        new AlertDialog.Builder(this)
                .setTitle("Aggiungi Capitoli")
                .setMessage("Inserisci i capitoli separati da una virgola (es: Capitolo 1,Capitolo 2)")
                .setView(input)
                .setPositiveButton("Salva", (dialog, which) -> {
                    String capitoliText = input.getText().toString().trim();
                    if (!capitoliText.isEmpty()) {
                        aggiungiCapitolo(capitoliText);
                        caricaCapitoli(); // Ricarica la lista dei capitoli
                    }
                })
                .setNegativeButton("Annulla", null)
                .show();
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


    private void aggiungiCapitolo(String capitoliText) {
        String[] capitoli = capitoliText.split(",");

        for (String capitoloNome : capitoli) {
            if (!capitoloNome.trim().isEmpty()) { // Se il capitolo non è vuoto
                dbHelper.aggiungiCapitolo(materiaId, capitoloNome, 0);
            }
        }
    }

    private void mostraOpzioniCapitolo(int capitoloId) {
        Capitolo capitolo = null;
        // Trova il capitolo dalla lista
        for (Capitolo c : capitoliList) {
            if (c.getId() == capitoloId) {
                capitolo = c;
                break;
            }
        }

        if (capitolo == null) return; // Sicurezza

        List<String> opzioniList = new ArrayList<>(Arrays.asList("Non fatto", "Appuntato", "Studiato"));

        if (capitolo.getHaEsercizi() == 0) {
            opzioniList.add("Aggiungi Esercizi");
        } else {
            if (capitolo.getEserciziFatti() == 0) {
                opzioniList.add("Segna Esercizi Fatti");
            } else {
                opzioniList.add("Segna Esercizi NON Fatti");
            }
        }

        opzioniList.add("Elimina Capitolo");

        String[] opzioni = opzioniList.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("Stato Capitolo")
                .setItems(opzioni, (dialog, which) -> {
                    String scelta = opzioni[which];
                    switch (scelta) {
                        case "Non fatto":
                            dbHelper.aggiornaStatoCapitolo(capitoloId, 0);
                            break;
                        case "Appuntato":
                            dbHelper.aggiornaStatoCapitolo(capitoloId, 1);
                            break;
                        case "Studiato":
                            dbHelper.aggiornaStatoCapitolo(capitoloId, 2);
                            break;
                        case "Aggiungi Esercizi":
                            dbHelper.aggiornaHaEsercizi(capitoloId, 1);
                            break;
                        case "Segna Esercizi Fatti":
                            dbHelper.aggiornaEserciziFatti(capitoloId, 1);
                            break;
                        case "Segna Esercizi NON Fatti":
                            dbHelper.aggiornaEserciziFatti(capitoloId, 0);
                            break;
                        case "Elimina Capitolo":
                            dbHelper.eliminaCapitolo(capitoloId);
                            break;
                    }
                    caricaCapitoli(); // Ricarica la lista
                    aggiornaGrafico(); // Aggiorna grafico
                    aggiornaGraficoEsercizi();
                })
                .show();
    }


    public static void aggiornaGrafico() {
        HashMap<Integer, Integer> stati = dbHelper.getConteggioStatiCapitoli(materiaId);

        int nonFatto = stati.get(0);
        int appuntato = stati.get(1);
        int studiato = stati.get(2);

        int totale = nonFatto + appuntato + studiato;

        // Evita divisione per 0
        if (totale == 0) totale = 1;

        // Percentuali per le etichette
        float percNonFatto = (nonFatto * 100f) / totale;
        float percAppuntato = (appuntato * 100f) / totale;
        float percStudiato = (studiato * 100f) / totale;

        // Liste complete
        List<Float> datiCompleti = Arrays.asList(
                (float) nonFatto,
                (float) appuntato,
                (float) studiato
        );

        List<Integer> coloriCompleti = Arrays.asList(
                Color.parseColor("#F8A6A6"), // Rosso per Non fatto
                Color.parseColor("#F9E28B"), // Giallo per Appuntato
                Color.parseColor("#A2E8B7") // Verde per Studiato
        );

        List<String> labelsCompleti = Arrays.asList(
                "Non fatto: " + String.format("%.1f", percNonFatto) + "%",
                "Appuntato: " + String.format("%.1f", percAppuntato) + "%",
                "Studiato: " + String.format("%.1f", percStudiato) + "%"
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

    public static void aggiornaGraficoEsercizi() {
        int eserciziDaFare = dbHelper.contaEserciziDaFare(materiaId);
        int eserciziFatti = dbHelper.contaEserciziFatti(materiaId);

        int totale = eserciziDaFare + eserciziFatti;

        // Evita divisione per 0
        if (totale == 0) totale = 1;

        // Percentuali per le etichette
        float percDaFare = (eserciziDaFare * 100f) / totale;
        float percFatti = (eserciziFatti * 100f) / totale;

        // Liste complete
        List<Float> datiCompleti = Arrays.asList(
                (float) eserciziDaFare,
                (float) eserciziFatti
        );

        List<Integer> coloriCompleti = Arrays.asList(
                Color.parseColor("#1583F0"), // Blu scuro per Da fare
                Color.parseColor("#ADD8E6")  // Blu chiaro per Fatti
        );

        List<String> labelsCompleti = Arrays.asList(
                "Esercizi da fare: " + String.format("%.1f", percDaFare) + "%",
                "Esercizi fatti: " + String.format("%.1f", percFatti) + "%"
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

        // Passa solo i dati filtrati al PieChartView degli esercizi

        pieChartEsercizi.setDataWithLabels(datiFiltrati, coloriFiltrati, labelsFiltrati);
    }

}

