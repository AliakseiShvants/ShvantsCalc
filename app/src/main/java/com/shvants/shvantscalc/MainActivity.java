package com.shvants.shvantscalc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

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
    public static final Character ADD_SIGN = '+';
    public static final Character DIFF_SIGN = '-';
    public static final Character DOT = '.';
    public static final Character DIV_SIGN_UNICODE = '\u00f7';
    public static final Character MUL_SIGN_UNICODE = '\u00D7';

    public static final int MAX_TEXT_SIZE = 50;
    public static final int MEDIUM_TEXT_SIZE = 40;
    public static final int MIN_TEXT_SIZE = 30;
    public static final int UOE_TEXT_SIZE = 25;
    public static final int MEDIUM_LINE_LENGTH = 13;
    public static final int MAX_LINE_LENGTH = 16;

    TextView displayLine;
    TextView enterLine;
    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat numberFormat = new DecimalFormat(PATTERN, otherSymbols);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayLine = findViewById(R.id.displayLine);
        enterLine = findViewById(R.id.enterLine);
    }

    public void clickNumber(View view){
        Button button = (Button) view;
        String number = button.getText().toString();
        String line = enterLine.getText().toString();
        Character last = line.charAt(line.length() - 1);
        int lastIndex = line.length() - 1;

        if (line.length() < MEDIUM_LINE_LENGTH){
            enterLine.setTextSize(MAX_TEXT_SIZE);
        } else if (line.length() < MAX_LINE_LENGTH){
            enterLine.setTextSize(MEDIUM_TEXT_SIZE);
        } else {
            enterLine.setTextSize(MIN_TEXT_SIZE);
        }

        if (line.equals(UOE_MESSAGE) || line.equals(ZERO)){
            enterLine.setText(number);
        }else if (last == ZERO.charAt(0)
                && !Character.isDigit(line.charAt(line.length() - 2))
                && line.charAt(line.length() - 2) != DOT){
            enterLine.setText(line.substring(0, lastIndex).concat(number));
        }
        else if (last != PERCENT_SIGN.charAt(0)){
            enterLine.setText(line.concat(number));
        }
    }

    public void clickSign(View view){
        Button button = (Button) view;
        String sign = button.getText().toString();
        String line = enterLine.getText().toString();
        int lastIndex = line.length() - 1;
        Character last = line.charAt(lastIndex);

        if (Character.isDigit(last)){
            enterLine.setText(line.concat(sign));
        } else {
            enterLine.setText(line.substring(0, lastIndex).concat(sign));
        }
    }

    public void clickDot(View view){
        String line = enterLine.getText().toString();
        String[] operands = line.split(SPLIT_PATTERN);
        String last = operands[operands.length - 1];
        Character[] operatorArr = new Character[]{
                MUL_SIGN_UNICODE, DIV_SIGN_UNICODE, ADD_SIGN, DIFF_SIGN
        };
        boolean flag = false;
        for (Character sign : operatorArr){
            if (last.equals(sign.toString())){
                flag = true;
                break;
            }
        }
        if (!last.contains(DOT.toString()) || flag){
            enterLine.setText(line.concat(DOT.toString()));
        }
    }

    public void clickCancel(View view){
        enterLine.setText(ZERO);
    }

    public void moveOneCharacter(View view){
        String line = enterLine.getText().toString();
        if (line.length() == 1){
            clickCancel(view);
        } else {
            enterLine.setText(line.substring(0, line.length() - 1));
        }
    }

    public void clickEqual(View view){
        Double result;
        String line = enterLine.getText().toString();

        if (line.equals(UOE_MESSAGE)){
            enterLine.setTextSize(MAX_TEXT_SIZE);
            enterLine.setText(ZERO);
            return;
        }
        String prepareLine = line.replace(DIV_SIGN_UNICODE, DIV_SIGN)
                .replace(MUL_SIGN_UNICODE, MUL_SIGN)
                .replace(PERCENT_SIGN, ONE_PERCENT);

        if(prepareLine.contains(ZERO_DIV)){
            displayLine.setText(line.concat(EQUAL));
            enterLine.setText(UOE_MESSAGE);
            enterLine.setTextSize(UOE_TEXT_SIZE);
            return;
        }
        Expression expression = new ExpressionBuilder(prepareLine).build();
        result = expression.evaluate();
        displayLine.setText(line.concat(EQUAL));
        enterLine.setText(numberFormat.format(result));
    }
}
