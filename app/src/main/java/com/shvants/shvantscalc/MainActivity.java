package com.shvants.shvantscalc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends Activity {

    public static final String ZERO = "0";
    public static final String EQUAL = "=";
    public static final String UOE_MESSAGE = "НА НОЛЬ ДЕЛИТЬ НЕЛЬЗЯ!";
    public static final String ZERO_DIV = "/0";
    public static final String PATTERN = "#.########";
    public static final String PERCENT_SIGN = "%";
    public static final String ONE_PERCENT = "/100";
    public static final String SPLIT_PATTERN = "[\u00f7\u00d7+-]";

    public static final Character MUL_SIGN = '*';
    public static final Character DIV_SIGN = '/';
    public static final Character DOT = '.';
    public static final Character DIV_SIGN_UNICODE = '\u00f7';
    public static final Character MUL_SIGN_UNICODE = '\u00D7';

    public static final int MAX_TEXT_SIZE = 50;
    public static final int MEDIUM_TEXT_SIZE = 40;
    public static final int MIN_TEXT_SIZE = 30;
    public static final int UOE_TEXT_SIZE = 25;
    public static final int MEDIUM_LINE_LENGTH = 10;
    public static final int MAX_LINE_LENGTH = 15;

    private TextView displayView;
    private TextView enterView;
    private String displayLine;
    private String enterLine;
    private DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
    private DecimalFormat numberFormat = new DecimalFormat(PATTERN, otherSymbols);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayView = findViewById(R.id.displayView);
        enterView = findViewById(R.id.enterView);

        if (savedInstanceState != null){
            displayLine = savedInstanceState.getString("display");
            enterLine = savedInstanceState.getString("enter");

            displayView.setText(displayLine);
            enterView.setText(enterLine);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        displayLine = displayView.getText().toString();
        enterLine = enterView.getText().toString();

        savedInstanceState.putString("display", displayLine);
        savedInstanceState.putString("enter", enterLine);
    }

    public void clickNumber(View view){
        Button button = (Button) view;
        String number = button.getText().toString();
        String line = enterView.getText().toString();
        Character last = line.charAt(line.length() - 1);
        int lastIndex = line.length() - 1;

        if (line.length() < MEDIUM_LINE_LENGTH){
            enterView.setTextSize(MAX_TEXT_SIZE);
        } else if (line.length() < MAX_LINE_LENGTH){
            enterView.setTextSize(MEDIUM_TEXT_SIZE);
        } else {
            enterView.setTextSize(MIN_TEXT_SIZE);
        }

        if (line.equals(UOE_MESSAGE) || line.equals(ZERO)){
            enterView.setText(number);
        }else if (last == ZERO.charAt(0)
                && !Character.isDigit(line.charAt(line.length() - 2))
                && line.charAt(line.length() - 2) != DOT){
            enterView.setText(line.substring(0, lastIndex).concat(number));
        }
        else if (last != PERCENT_SIGN.charAt(0)){
            enterView.setText(line.concat(number));
        }
    }

    public void clickSign(View view){
        Button button = (Button) view;
        String sign = button.getText().toString();
        String line = enterView.getText().toString();
        int lastIndex = line.length() - 1;
        Character last = line.charAt(lastIndex);

        if (Character.isDigit(last)){
            enterView.setText(line.concat(sign));
        } else if (last != DOT && !Character.isDigit(line.charAt(lastIndex - 1))){
            enterView.setText(line.substring(0, lastIndex).concat(sign));
        }
    }

    public void clickDot(View view){
        String line = enterView.getText().toString();
        String[] operands = line.split(SPLIT_PATTERN);
        String lastOperand = operands[operands.length - 1];
        Character lastChar = line.charAt(line.length() - 1);
        if (!lastOperand.contains(DOT.toString()) || !Character.isDigit(lastChar)){
            enterView.setText(line.concat(DOT.toString()));
        }
    }

    public void clickCancel(View view){
        enterView.setText(ZERO);
    }

    public void moveOneCharacter(View view){
        String line = enterView.getText().toString();
        if (line.length() == 1){
            clickCancel(view);
        } else {
            enterView.setText(line.substring(0, line.length() - 1));
        }
    }

    public void clickEqual(View view){
        Double result;
        String line = enterView.getText().toString();

        if (line.equals(UOE_MESSAGE)){
            enterView.setTextSize(MAX_TEXT_SIZE);
            enterView.setText(ZERO);
            return;
        }
        String prepareLine = line.replace(DIV_SIGN_UNICODE, DIV_SIGN)
                .replace(MUL_SIGN_UNICODE, MUL_SIGN)
                .replace(PERCENT_SIGN, ONE_PERCENT);

        if(prepareLine.contains(ZERO_DIV)){
            displayView.setText(line.concat(EQUAL));
            enterView.setText(UOE_MESSAGE);
            enterView.setTextSize(UOE_TEXT_SIZE);
            return;
        }
        Expression expression = new ExpressionBuilder(prepareLine).build();
        result = expression.evaluate();
        displayView.setText(line.concat(EQUAL));
        enterView.setText(numberFormat.format(result));
    }
}
