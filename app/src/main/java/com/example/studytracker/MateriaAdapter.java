package com.example.studytracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class MateriaAdapter extends BaseAdapter {
    private Context context;
    private List<Materia> materieList;
    private DatabaseHelper dbHelper;

    public MateriaAdapter(Context context, List<Materia> materieList) {
        this.context = context;
        this.materieList = materieList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return materieList.size();
    }

    @Override
    public Object getItem(int position) {
        return materieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return materieList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_materia, parent, false);
        }

        TextView txtMateriaNome = convertView.findViewById(R.id.txtMateriaNome);
        ImageButton btnEliminaMateria = convertView.findViewById(R.id.btnEliminaMateria);

        Materia materia = materieList.get(position);
        txtMateriaNome.setText(materia.getNome());

        // 📚 Cliccando sulla materia vai ai capitoli
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CapitoliActivity.class);
            intent.putExtra("materia_id", materia.getId());
            context.startActivity(intent);
        });

        // ❌ Elimina materia
        btnEliminaMateria.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Elimina Materia")
                    .setMessage("Sei sicuro di voler eliminare \"" + materia.getNome() + "\"?")
                    .setPositiveButton("Elimina", (dialog, which) -> {
                        dbHelper.eliminaMateria(materia.getId());
                        materieList.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("Annulla", null)
                    .show();
        });

        return convertView;
    }
}
