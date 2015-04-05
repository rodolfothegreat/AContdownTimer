package com.manzana.rde.acontdowntimer;

/**
 * Created by Rodolfo on 4/04/2015.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * TODO: document your custom view class.
 */
public class RdeSelectorView extends LinearLayout {

    Button btnUp = null;
    Button btnDown = null;
    EditText  edtNumber = null;
    TextView tvCaption;
    private int mMax = 59;

    public void setEnabled(boolean enabled){

        super.setEnabled(enabled);
        if (btnUp != null)
            btnUp.setEnabled(enabled);

        if (btnDown != null)
            btnDown.setEnabled(enabled);

        if(edtNumber != null)
            edtNumber.setEnabled(enabled);

    }

    static int stringtoInteger(String x) {
        String value = "";
        for (int i = 0; i < x.length(); i++) {
            char character = x.charAt(i);
            if (Character.isDigit(character)) {
                value = value + character;
            }
        }
        if (value == "")
            value = "0";
        return Integer.parseInt(value);
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int amax) {
        mMax = amax;
        if (mMax > 99)
            mMax = 99;
    }

    public void setValue(int avalue) {
        if (avalue < 0)
            avalue = 0;
        if(avalue > mMax)
            avalue = mMax;
        edtNumber.setText(String.valueOf(avalue));
    }

    public int getValue() {
        return stringtoInteger(edtNumber.getText().toString().trim());
    }

    public void setCaption(String acaption) {
        tvCaption.setText(acaption);
    }

    public RdeSelectorView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RdeSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RdeSelectorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        LayoutInflater inflater = LayoutInflater.from(context);
        //inflater.inflate(R.layout.rde_selector_view, this);
        inflater.inflate(R.layout.layout_extra , this);

        btnUp = (Button) findViewById(R.id.btnUp);
        btnUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String avalue = edtNumber.getText().toString().trim();
                if (avalue.length() == 0)
                    avalue = "0";

                int thevalue = RdeSelectorView.stringtoInteger(avalue) + 1;
                if (thevalue > mMax)
                    thevalue = mMax;

                edtNumber.setText(String.valueOf(thevalue) );

            }
        });
        btnDown = (Button) findViewById(R.id.btnDown);
        btnDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String avalue = edtNumber.getText().toString().trim();
                if(avalue.length() == 0)
                    avalue = "0";

                int thevalue = RdeSelectorView.stringtoInteger(avalue) - 1;
                if (thevalue < 0)
                    thevalue = 0;

                edtNumber.setText(String.valueOf(thevalue) );

            }
        });
        edtNumber = (EditText)findViewById(R.id.edtNumber);
        edtNumber.setText("0");
        edtNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String avalue = edtNumber.getText().toString().trim();
                int thevalue = RdeSelectorView.stringtoInteger(avalue);
                int oldvalue = thevalue;
                if (thevalue < 0)
                    thevalue = 0;
                if (thevalue > mMax)
                    thevalue = mMax;
                if (oldvalue != thevalue)
                    edtNumber.setText(String.valueOf(thevalue));
            }
        });

        tvCaption = (TextView) findViewById(R.id.tvCaption);


        // Set up a default TextPaint object

        // Update TextPaint and text measurements from attributes
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

}
