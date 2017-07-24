package com.skyline.kattaclientapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by MIHIR on 26-05-2016.
 */
public class QuantityBox extends LinearLayout {

    View rootView;
    Button plusButton;
    Button minusButton;
    TextView quantityTextView;
    Button addButton;

    public QuantityBox(Context context) {
        super(context);
        init(context);
    }

    public QuantityBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        rootView = inflate(context, R.layout.quantity_box, QuantityBox.this);
        quantityTextView = (TextView) rootView.findViewById(R.id.quantityTextView);
        plusButton = (Button) rootView.findViewById(R.id.plus_button);
        minusButton = (Button) rootView.findViewById(R.id.minus_button);
        addButton = (Button) rootView.findViewById(R.id.add_button);

        plusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                increment();
            }
        });

        minusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement();
            }
        });

        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addButtonClick();
            }
        });
    }

    public int getQuantity() {
        return Integer.valueOf(quantityTextView.getText().toString());
    }

    public void setQuantity(int quantity) {
        if (quantity == 0) {
            addButton.setVisibility(VISIBLE);
        } else {
            addButton.setVisibility(GONE);
        }
        quantityTextView.setText(String.valueOf(quantity));

    }

    public void increment() {
        int currentValue = Integer.valueOf(quantityTextView.getText().toString());
        quantityTextView.setText(String.valueOf(currentValue + 1));
    }

    public void decrement() {
        int currentValue = Integer.valueOf(quantityTextView.getText().toString());
        if (currentValue == 1) {
            addButton.setVisibility(VISIBLE);
        }
        quantityTextView.setText(String.valueOf(currentValue - 1));
    }

    public void addButtonClick() {
        addButton.setVisibility(GONE);
        quantityTextView.setText("1");
    }

}
