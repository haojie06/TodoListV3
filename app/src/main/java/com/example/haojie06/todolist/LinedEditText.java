package com.example.haojie06.todolist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by haojie06 on 2017/12/6.
 */
//生成有下划线的EditText
public class LinedEditText extends android.support.v7.widget.AppCompatEditText {
    private Paint linePaint;
    private float margin;
    private int paperColor;

    public LinedEditText(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        this.linePaint = new Paint();
    }

    protected void onDraw(Canvas paramCanvas) {
        paramCanvas.drawColor(this.paperColor);
        int i = getLineCount();
        int j = getHeight();
        int k = getLineHeight();
        int m = 1 + j / k;
        if (i < m)
            i = m;
        int n = getCompoundPaddingTop();
        paramCanvas.drawLine(120.0F, n, getRight(), n, this.linePaint);
        for (int i2 = 0; ; i2++) {
            if (i2 >= i) {
                setPadding(10 + (int) this.margin, 0, 0, 0);
                super.onDraw(paramCanvas);
                paramCanvas.restore();
                return;
            }
            n += k;
            paramCanvas.drawLine(75.0F, n, getRight(), n, this.linePaint);
            paramCanvas.save();
        }
    }

}
