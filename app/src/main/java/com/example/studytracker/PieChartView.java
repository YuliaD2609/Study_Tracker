package com.example.studytracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class PieChartView extends View {
    private Paint paint;
    private List<Float> values;
    private List<Integer> colors;
    private List<String> labels;

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void setData(List<Float> values, List<Integer> colors) {
        this.values = values;
        this.colors = colors;
        invalidate(); // Ridisegna la View
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (values == null || values.isEmpty()) return;

        float total = 0;
        for (Float val : values) total += val;
        if (total == 0) total = 1; // Evita divisione per zero

        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        float startAngle = 0;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);

        for (int i = 0; i < values.size(); i++) {
            float sweepAngle = (values.get(i) / total) * 360;
            paint.setColor(colors.get(i));
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);

            // Calcolo posizione per l'etichetta
            float angle = startAngle + sweepAngle / 2;
            float radius = getWidth() / 3; // Puoi regolare il raggio
            float x = (float) (getWidth() / 2 + radius * Math.cos(Math.toRadians(angle)));
            float y = (float) (getHeight() / 2 + radius * Math.sin(Math.toRadians(angle)));

            // Disegna etichetta
            if (labels != null && !labels.get(i).isEmpty()) {
                canvas.drawText(labels.get(i), x, y, textPaint);
            }

            startAngle += sweepAngle;
        }
    }

    public void setDataWithLabels(List<Float> data, List<Integer> colors, List<String> labels) {
        this.values = data;
        this.colors = colors;
        this.labels = labels; // Salva anche le etichette
        invalidate(); // Ridisegna il grafico
    }

}
