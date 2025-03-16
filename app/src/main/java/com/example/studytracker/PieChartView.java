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
        for (Float value : values) {
            total += value;
        }

        float startAngle = 0;
        RectF rect = new RectF(50, 50, getWidth() - 50, getHeight() - 50);

        for (int i = 0; i < values.size(); i++) {
            float sweepAngle = (values.get(i) / total) * 360;
            paint.setColor(colors.get(i));
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
        }
    }
}
