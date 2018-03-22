package hfz.svoeoggau.at.hundatfuenfazwanzg.views;

import android.widget.LinearLayout;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;

/**
 * Created by Christian on 22.06.2014.
 */
public class ChartLegend extends LinearLayout {

    class ChartLegendItem {
        int color;
        String text;

        ChartLegendItem(int color, String text) {
            this.color = color;
            this.text = text;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    private int columns = 2;
    private float textSize = 48;
    private int textPadding = 18;
    private int textColor = 0xff000000;
    private int boxSize = 50;

    private Vector<ChartLegendItem> items = new Vector<ChartLegendItem>();
    private Context context;

    public ChartLegend(Context context) {
        super(context);
    }

    public ChartLegend(Context context, AttributeSet attrs) throws Exception {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public ChartLegend(Context context, AttributeSet attrs, int defStyle) throws Exception {
        super(context, attrs, defStyle);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) throws Exception {
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ChartLegend,
                0, 0);
        columns = a.getInt(R.styleable.ChartLegend_columns, columns);
        textSize = a.getDimension(R.styleable.ChartLegend_textSize, textSize);
        textPadding = a.getInt(R.styleable.ChartLegend_textPadding, textPadding);
        textColor = a.getColor(R.styleable.ChartLegend_textColor, textColor);
        boxSize = a.getInt(R.styleable.ChartLegend_boxSize, boxSize);
        this.setOrientation(VERTICAL);
    }


    public void addItem(int color, String text) {
        ChartLegendItem chartLegendItem = new ChartLegendItem(color, text);
        items.add(chartLegendItem);
        drawItems();
    }

    public void removeItems() {
        this.items = new Vector<ChartLegendItem>();
        drawItems();
    }

    public void drawItems() {

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams columnParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams colLeftParams = new LinearLayout.LayoutParams(boxSize, boxSize);
        LinearLayout.LayoutParams colRightParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);

        int lines = (int)Math.ceil(items.size() / columns);
        this.removeAllViews();

        int item = 0;
        for(int line = 1; line <= lines; line++) {

            LinearLayout row = new LinearLayout(context);
            row.setLayoutParams(rowParams);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);

            for(int column = 1; column <= columns; column ++) {
                if(item < items.size()) {
                    LinearLayout col = new LinearLayout(context);
                    col.setOrientation(LinearLayout.HORIZONTAL);
                    col.setGravity(Gravity.CENTER_VERTICAL);

                    TextView color = new TextView(context);
                    color.setBackgroundColor(items.get(item).getColor());
                    color.setGravity(Gravity.CENTER_VERTICAL);

                    TextView text = new TextView(context);
                    text.setPadding(textPadding, textPadding, textPadding, textPadding);
                    text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    text.setTextColor(textColor);
                    text.setText(items.get(item).getText());
                    text.setGravity(Gravity.CENTER_VERTICAL);

                    col.addView(color, colLeftParams);
                    col.addView(text, colRightParams);

                    row.addView(col, columnParams);
                    item++;
                }
            }

            addView(row, rowParams);
        }
    }
}
