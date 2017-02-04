// Calcula valores de conta usando porcentagens de 15% e personalizadas.

package com.cap3.tipcalculator;

import android.icu.text.NumberFormat; // para formatação de moeda corrente
import android.os.Bundle; // para salvar informações de estado
import android.support.v7.app.AppCompatActivity;
import android.text.Editable; // para tratamento de eventos de EditText
import android.text.TextWatcher; // receptor de EditText
import android.widget.EditText; // para entrada do valor da conta
import android.widget.SeekBar; // para alterar a porcentagem de gorjeta personalizada
import android.widget.SeekBar.OnSeekBarChangeListener; // receptor de SeekBar
import android.widget.TextView; // para exibir texto

// classe MainActivity do aplicativo Tip Calculator
public class MainActivity extends AppCompatActivity {
    // formatadores de moeda corrente e porcentagem
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();

    private double billAmount = 0.0; // valor da conta inserido pelo usuário
    private double customPercent = 0.18; // porcentagem de gorjeta padronizada inicial
    private TextView amountDisplayTextView; // mostra o valor da conta formatado
    private TextView percentCustomTextView; // mostra a porcentagem de gorjeta personalizada
    private TextView tip15TextView; // mostra gorjeta de 15%
    private TextView total15TextView; // mostra o total com 15% de gorjeta
    private TextView tipCustomTextView; // mostra o valor da gorjeta personalizada
    private TextView totalCustomTextView; // mostra o total com a gorjeta personalizada

    // chamado quando a atividade é criada
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // chama a versão da superclasse
        setContentView(R.layout.activity_main); // infla a interface gráfica do usuário

        // obtém referências para os componentes TextView
        // com que MainActivity interage via programação
        amountDisplayTextView = (TextView) findViewById(R.id.amountDisplayText);
        percentCustomTextView = (TextView) findViewById(R.id.percentCustomTextView);
        tip15TextView = (TextView) findViewById(R.id.tip15TextView);
        total15TextView = (TextView) findViewById(R.id.total15TextView);
        tipCustomTextView = (TextView) findViewById(R.id.tipCustomTextView);
        totalCustomTextView = (TextView) findViewById(R.id.totalCustomTextView);

        // atualiza a interface gráfica do usuário com base em billAmount e customPercent
        amountDisplayTextView.setText(currencyFormat.format(billAmount));
        updateStandard(); // atualiza os componentes TextView de gorjeta de 15%
        updateCustom(); // atualiza os componentes TextView de gorjeta personalizada

        // configura TextWatcher de amountEditText
        EditText amountEditText = (EditText) findViewById(R.id.amountEditText);
        amountEditText.addTextChangedListener(amountEditTextWatcher);

        // configura OnSeekBarChangeListener de customTipSeekBar
        SeekBar customTipSeekBar = (SeekBar) findViewById(R.id.customTipSeekBar);
        customTipSeekBar.setOnSeekBarChangeListener(customSeekBarListener);
    } // fim do método onCreate

    // atualiza os componentes TextView de gorjeta de 15%
    private void updateStandard() {
        // calcula a gorjeta de 15% e o total
        double fifteenPercentTip = billAmount * 0.15;
        double fifteenPercentTotal = billAmount + fifteenPercentTip;

        // exibe a gorjeta de 15% e o total formatados como moeda corrente
        tip15TextView.setText(currencyFormat.format(fifteenPercentTip));
        total15TextView.setText(currencyFormat.format(fifteenPercentTotal));
    } // fim do método updateStandard

    // atualiza os componentes TextView de gorjeta personalizada e total
    private void updateCustom() {
        // mostra customPercent em percentCustomTextView, formatado como %
        percentCustomTextView.setText(percentFormat.format(customPercent));

        // calcula a gorjeta personalizada e o total
        double customTip = billAmount * customPercent;
        double customTotal = billAmount + customTip;

        // exibe a gorjeta personalizada e o total, formatados como moeda corrente
        tipCustomTextView.setText(currencyFormat.format(customTip));
        totalCustomTextView.setText(currencyFormat.format(customTotal));
    } // fim do método updateCustom

    // chamado quando o usuário muda a posição de SeekBar
    private OnSeekBarChangeListener customSeekBarListener =
            new OnSeekBarChangeListener() {
                // atualiza customPercent e chama updateCustom
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // configura customPercent com a posição do cursor de SeekBar
                    customPercent = progress / 100.0;
                    updateCustom(); // atualiza os componentes TextView de gorjeta personalizada
                } // fim do método onProgressChanged

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                } // fim do método onStartTrackingTouch

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                } // fim do método onStopTrackingTouch
            }; // fim de OnSeekBarChangeListener

    // objeto de tratamento de eventos que responde aos eventos de amountEditText
    private TextWatcher amountEditTextWatcher = new TextWatcher() {
        // chamado quando o usuário insere um número
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // converte o texto de amountEditText em um valor double
            try {
                billAmount = Double.parseDouble(s.toString()) / 100.0;
            } // fim do try
            catch (NumberFormatException e) {
                billAmount = 0.0; // o padrão, caso ocorra uma exceção
            } // fim do catch

            // exibe o valor da conta formatado como moeda corrente
            amountDisplayTextView.setText(currencyFormat.format(billAmount));
            updateStandard(); // atualiza os componentes TextView de gorjeta de 15%
            updateCustom(); // atualiza os componentes TextView de gorjeta personalizada
        } // fim do método onTextChanged

        @Override
        public void afterTextChanged(Editable s) {
        } // fim do método afterTextChanged

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        } // fim do método beforeTextChanged
    }; // fim de amountEditTextWatcher
}  // fim da classe MainActivity

