// Hospeda o componente QuizFragment em um telefone e os
// componentes QuizFragment e SettingsFragment em um tablet
package com.cap5.flagquiz;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // chaves para ler dados de SharedPreferences
    public static final String CHOICES = "pref_numberOfChoices";
    public static final String REGIONS = "pref_regionsToInclude";

    private boolean phoneDevice = true; // usado para impor o modo retrato
    private boolean preferencesChanged = true; // as preferências mudaram?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // configura valores padrão no elemento SharedPreferences do aplicativo
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // registra receptor para alterações em SharedPreferences
        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(
                        preferenceChangeListener);

        // determina o tamanho da tela
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        // se o dispositivo é um tablet, configura phoneDevice como false
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE )
            phoneDevice = false; // não é um dispositivo do tamanho de um telefone

        // se estiver sendo executado em dispositivo do tamanho de um telefone, só
        // permite orientação retrato
        if (phoneDevice)
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    } // fim do método onCreate b b

    // chamado depois que onCreate completa a execução
    @Override
    protected void onStart() {
        super.onStart();

        if (preferencesChanged) {
            // agora que as preferências padrão foram configuradas,
            // inicializa QuizFragment e inicia o teste
            QuizFragment quizFragment = (QuizFragment)
                    getFragmentManager().findFragmentById(R.id.quizFragment);
            quizFragment.updateGuessRows(
                    PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.updateRegions(
                    PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.resetQuiz();
            preferencesChanged = false;
        }
    } // fim do método onStart

    // mostra o menu se o aplicativo estiver sendo executado em um telefone ou em um
    // tablet na orientação retrato
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // obtém o objeto Display padrão que representa a tela
        Display display = ((WindowManager)
                getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // usado para armazenar o tamanho da tela
        display.getRealSize(screenSize); // armazena o tamanho em screenSize

        // só exibe o menu do aplicativo na orientação retrato
        if (screenSize.x < screenSize.y){ // x é a largura, y é a altura

            getMenuInflater().inflate(R.menu.main, menu); // infla o menu
            return true;
        }
        else
            return false;
    } // fim do método onCreateOptionsMenu

    // exibe SettingsActivity ao ser executado em um telefone
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
        return super.onOptionsItemSelected(item);
    }

    // receptor para alterações feitas no elemento SharedPreferences do aplicativo
    private OnSharedPreferenceChangeListener preferenceChangeListener =
            new OnSharedPreferenceChangeListener() {
                // chamado quando o usuário altera as preferências do aplicativo
                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {
                    preferencesChanged = true; // o usuário mudou as configurações do aplicativo

                    QuizFragment quizFragment = (QuizFragment)
                            getFragmentManager().findFragmentById(R.id.quizFragment);

                    if (key.equals(CHOICES)) { // o nº de escolhas a exibir mudou

                        quizFragment.updateGuessRows(sharedPreferences);
                        quizFragment.resetQuiz();
                    }
                    else if (key.equals(REGIONS)){ // as regiões a incluir mudaram

                        Set<String> regions =
                                sharedPreferences.getStringSet(REGIONS, null);

                        if (regions != null && regions.size() > 0) {
                            quizFragment.updateRegions(sharedPreferences);
                            quizFragment.resetQuiz();
                        }
                        else { // deve selecionar uma região -- configura North America como padrão

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            regions.add(
                                    getResources().getString(R.string.default_region));
                            editor.putStringSet(REGIONS, regions);
                            editor.commit();
                            Toast.makeText(MainActivity.this,
                                    R.string.default_region_message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    Toast.makeText(MainActivity.this,
                            R.string.restarting_quiz, Toast.LENGTH_SHORT).show();
                } // fim do método onSharedPreferenceChanged
            }; // fim da classe interna anônima
} // fim da classe MainActivity

