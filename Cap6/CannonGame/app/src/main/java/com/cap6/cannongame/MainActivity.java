// MainActivity exibe o elemento CannonGameFragment
package com.cap6.cannongame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    // chamado quando o aplicativo é ativado pela primeira vez
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // chama o método onCreate de super
        setContentView(R.layout.activity_main); // infla o layout
    }
} // fim da classe MainActivity
