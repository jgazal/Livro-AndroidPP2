// Atividade para exibir SettingsFragment em um telefone
package com.cap5.flagquiz;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
    // usa FragmentManager para exibir SettingsFragment
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_settings);
   }
} // fim da classe SettingsActivity
