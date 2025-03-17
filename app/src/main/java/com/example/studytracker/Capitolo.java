package com.example.studytracker;

public class Capitolo {
    private int id;
    private int materiaId;
    private String nome;
    private int stato;
    private int haEsercizi;
    private int eserciziFatti;

    // ▶️ Stati capitolo (puoi usarli come costanti per evitare errori nei numeri magic)
    public static final int STATO_NON_FATTO = 0;
    public static final int STATO_APPUNTATO = 1;
    public static final int STATO_STUDIATO = 2;
    public static final int STATO_ESERCIZI_FATTI = 3;

    // ✅ Costruttore completo
    public Capitolo(int id, int materiaId, String nome, int stato) {
        this.id = id;
        this.materiaId = materiaId;
        this.nome = nome;
        this.stato = stato;
    }

    // ✅ Costruttore senza ID (es. per creazione prima dell'inserimento)
    public Capitolo(int materiaId, String nome, int stato) {
        this.materiaId = materiaId;
        this.nome = nome;
        this.stato = stato;
    }

    public Capitolo(int id, String nome, int stato, int haEsercizi, int eserciziFatti) {
        this.id = id;
        this.nome = nome;
        this.stato = stato;
        this.haEsercizi = haEsercizi;
        this.eserciziFatti = eserciziFatti;
    }

    public int getHaEsercizi() { return haEsercizi; }
    public void setHaEsercizi(int haEsercizi) { this.haEsercizi = haEsercizi; }

    public int getEserciziFatti() { return eserciziFatti; }
    public void setEserciziFatti(int eserciziFatti) { this.eserciziFatti = eserciziFatti; }

    // ✅ Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMateriaId() { return materiaId; }
    public void setMateriaId(int materiaId) { this.materiaId = materiaId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getStato() { return stato; }
    public void setStato(int stato) { this.stato = stato; }

    // ✅ Metodo per ottenere una descrizione leggibile dello stato
    public String getStatoDescrizione() {
        switch (stato) {
            case STATO_NON_FATTO:
                return "Non fatto";
            case STATO_APPUNTATO:
                return "Appuntato";
            case STATO_STUDIATO:
                return "Studiato";
            case STATO_ESERCIZI_FATTI:
                return "Esercizi fatti";
            default:
                return "Stato sconosciuto";
        }
    }

    // ✅ toString() utile per debug o stampa
    @Override
    public String toString() {
        return "Capitolo{" +
                "id=" + id +
                ", materiaId=" + materiaId +
                ", nome='" + nome + '\'' +
                ", stato=" + stato + " (" + getStatoDescrizione() + ")" +
                '}';
    }
}
