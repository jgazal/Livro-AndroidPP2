// Configura o layout de MainActivity
package com.cap7.doodlz;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // determina o tamanho da tela
      int screenSize = 
         getResources().getConfiguration().screenLayout & 
         Configuration.SCREENLAYOUT_SIZE_MASK;

      // usa paisagem para tablets extragrandes; caso contrário, usa retrato
      if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
         setRequestedOrientation(
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      else
         setRequestedOrientation(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   }
} // fim da classe MainActivity

