// Subclasse de PreferenceFragment para gerenciar configurações do aplicativo
package com.cap5.flagquiz;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
   // cria interface gráfica de preferências do usuário a partir do arquivo
   // preferences.xml em res/xml
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.preferences); // carrega do XML
   } 
} // fim da classe SettingsFragment
