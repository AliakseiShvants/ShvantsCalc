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

import static com.shvants.shvantscalc.Constants.MAX_DISPLAY_TEXT_SIZE;
import static com.shvants.shvantscalc.Constants.MAX_ENTER_TEXT_SIZE;
import static com.shvants.shvantscalc.Constants.MAX_LINE_LENGTH;
import static com.shvants.shvantscalc.Constants.MEDIUM_DISPLAY_TEXT_SIZE;
import static com.shvants.shvantscalc.Constants.MEDIUM_ENTER_TEXT_SIZE;
import static com.shvants.shvantscalc.Constants.MEDIUM_LINE_LENGTH;
import static com.shvants.shvantscalc.Constants.MIN_DISPLAY_TEXT_SIZE;
import static com.shvants.shvantscalc.Constants.MIN_ENTER_TEXT_SIZE;
import static com.shvants.shvantscalc.Constants.ONE;
import static com.shvants.shvantscalc.Constants.TWO;
import static com.shvants.shvantscalc.Constants.UOE_TEXT_SIZE;
import static com.shvants.shvantscalc.Constants.ZERO;

public class MainActivity extends Activity {

    public static final String EQUAL = "=";
    public static final String UOE_MESSAGE = "НА НОЛЬ ДЕЛИТЬ НЕЛЬЗЯ!";
    public static final String ZERO_DIV = "/0";
    public static final String PATTERN = "#.########";
    public static final String PERCENT_SIGN = "%";
    public static final String ONE_PERCENT = "/100";
    public static final String SPLIT_PATTERN = "[\u00f7\u00d7+-]";
    public static final String DISPLAY = "display";
    public static final String ENTER = "enter";

    public static final Character MUL_SIGN = '*';
    public static final Character DIV_SIGN = '/';
    public static final Character DOT = '.';
    public static final Character DIV_SIGN_UNICODE = '\u00f7';
    public static final Character MUL_SIGN_UNICODE = '\u00D7';
    public static final Character ZERO_CHAR = '0';

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

        displayView = findViewById(R.id.main_display_view);
        enterView = findViewById(R.id.main_enter_view);

        if (savedInstanceState != null) {
            displayLine = savedInstanceState.getString(DISPLAY);
            enterLine = savedInstanceState.getString(ENTER);

            displayView.setText(displayLine);
            enterView.setText(enterLine);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        displayLine = displayView.getText().toString();
        enterLine = enterView.getText().toString();

        savedInstanceState.putString(DISPLAY, displayLine);
        savedInstanceState.putString(ENTER, enterLine);
    }

    public void clickNumber(View view) {
        Button button = (Button) view;
        String number = button.getText().toString();
        String line = enterView.getText().toString();
        int lastIndex = line.length() - ONE;
        char last = line.charAt(lastIndex);


        if (line.equals(UOE_MESSAGE) || line.equals(ZERO_CHAR.toString())) {
            enterView.setText(number);
        } else if (last == ZERO_CHAR
                && !Character.isDigit(line.charAt(line.length() - TWO))
                && line.charAt(line.length() - TWO) != DOT) {
            enterView.setText(line.substring(ZERO, lastIndex).concat(number));
        } else if (last != PERCENT_SIGN.charAt(ZERO)) {
            enterView.setText(line.concat(number));
        }

        setTextSize(enterView.getText().toString());
    }

    private void setTextSize(String line) {
        if (line.length() < MEDIUM_LINE_LENGTH) {
            displayView.setTextSize(MAX_DISPLAY_TEXT_SIZE);
            enterView.setTextSize(MAX_ENTER_TEXT_SIZE);
        } else if (line.length() < MAX_LINE_LENGTH) {
            displayView.setTextSize(MEDIUM_DISPLAY_TEXT_SIZE);
            enterView.setTextSize(MEDIUM_ENTER_TEXT_SIZE);
        } else {
            displayView.setTextSize(MIN_DISPLAY_TEXT_SIZE);
            enterView.setTextSize(MIN_ENTER_TEXT_SIZE);
        }
    }

    public void clickSign(View view) {
        Button button = (Button) view;
        String sign = button.getText().toString();
        String line = enterView.getText().toString();
        int lastIndex = line.length() - ONE;
        Character last = line.charAt(lastIndex);

        if (Character.isDigit(last)) {
            enterView.setText(line.concat(sign));
        } else if (last != DOT && !Character.isDigit(line.charAt(lastIndex - ONE))) {
            enterView.setText(line.substring(ZERO, lastIndex).concat(sign));
        }

        setTextSize(enterView.getText().toString());
    }

    public void clickDot(View view) {
        String line = enterView.getText().toString();
        String[] operands = line.split(SPLIT_PATTERN);
        String lastOperand = operands[operands.length - ONE];
        char lastChar = line.charAt(line.length() - ONE);

        if (!lastOperand.contains(DOT.toString()) && Character.isDigit(lastChar)) {
            enterView.setText(line.concat(DOT.toString()));
        }
    }

    public void clickCancel(View view) {
        enterView.setText(ZERO_CHAR);
        setTextSize(enterView.getText().toString());
    }

    public void moveOneCharacter(View view) {
        String line = enterView.getText().toString();

        if (line.length() == ONE) {
            clickCancel(view);
        } else {
            enterView.setText(line.substring(ZERO, line.length() - ONE));
        }

        setTextSize(enterView.getText().toString());
    }

    public void clickEqual(View view) {
        Double result;
        String line = enterView.getText().toString();

        if (line.equals(UOE_MESSAGE)) {
            enterView.setTextSize(MAX_ENTER_TEXT_SIZE);
            enterView.setText(ZERO_CHAR);

            return;
        }

        String prepareLine = line.replace(DIV_SIGN_UNICODE, DIV_SIGN)
                .replace(MUL_SIGN_UNICODE, MUL_SIGN)
                .replace(PERCENT_SIGN, ONE_PERCENT);

        if (prepareLine.contains(ZERO_DIV)) {
            displayView.setText(line.concat(EQUAL));
            enterView.setText(UOE_MESSAGE);
            enterView.setTextSize(UOE_TEXT_SIZE);

            return;
        }

        Expression expression = new ExpressionBuilder(prepareLine).build();
        result = expression.evaluate();

        displayView.setText(line.concat(EQUAL));
        enterView.setText(numberFormat.format(result));
        setTextSize(enterView.getText().toString());
    }
}
