// Permite ao usuário configurar a cor de desenho no elemento DoodleView
package com.cap7.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

// classe para a caixa de diálogo Select Color
public class ColorDialogFragment extends DialogFragment {
   private SeekBar alphaSeekBar;
   private SeekBar redSeekBar;
   private SeekBar greenSeekBar;
   private SeekBar blueSeekBar;
   private View colorView;
   private int color;

   // cria um componente AlertDialog e o retorna
   @Override
   public Dialog onCreateDialog(Bundle bundle) {
      AlertDialog.Builder builder = 
         new AlertDialog.Builder(getActivity());
      View colorDialogView = 
         getActivity().getLayoutInflater().inflate(
            R.layout.fragment_color, null);
      builder.setView(colorDialogView); // adiciona a interface gráfica do usuário à
                                        // caixa de diálogo

      // configura a mensagem de AlertDialog
      builder.setTitle(R.string.title_color_dialog);
      builder.setCancelable(true);

      // obtém os componentes SeekBar de cor e configura seus receptores onChange
      alphaSeekBar = (SeekBar) colorDialogView.findViewById(
         R.id.alphaSeekBar);
      redSeekBar = (SeekBar) colorDialogView.findViewById(
         R.id.redSeekBar);
      greenSeekBar = (SeekBar) colorDialogView.findViewById(
         R.id.greenSeekBar);
      blueSeekBar = (SeekBar) colorDialogView.findViewById(
         R.id.blueSeekBar);
      colorView = colorDialogView.findViewById(R.id.colorView);

      // registra receptores de eventos de SeekBar
      alphaSeekBar.setOnSeekBarChangeListener(colorChangedListener);
      redSeekBar.setOnSeekBarChangeListener(colorChangedListener);
      greenSeekBar.setOnSeekBarChangeListener(colorChangedListener);
      blueSeekBar.setOnSeekBarChangeListener(colorChangedListener);
     
      // usa a cor de desenho atual para configurar os valores dos SeekBar
      final DoodleView doodleView = getDoodleFragment().getDoodleView();
      color = doodleView.getDrawingColor();
      alphaSeekBar.setProgress(Color.alpha(color));
      redSeekBar.setProgress(Color.red(color));
      greenSeekBar.setProgress(Color.green(color));
      blueSeekBar.setProgress(Color.blue(color));

      // adiciona o componente Button Set Color
      builder.setPositiveButton(R.string.button_set_color,
         new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               doodleView.setDrawingColor(color); 
            } 
         } 
      ); // fim da chamada a setPositiveButton
      
      return builder.create(); // retorna a caixa de diálogo
   } // fim do método onCreateDialog

   // obtém uma referência para o componente DoodleFragment
   private DoodleFragment getDoodleFragment() {
      return (DoodleFragment) getFragmentManager().findFragmentById(
         R.id.doodleFragment);
   }

   // informa DoodleFragment de que a caixa de diálogo está sendo exibida
   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
      DoodleFragment fragment = getDoodleFragment();
      
      if (fragment != null)
         fragment.setDialogOnScreen(true);
   }

   // informa DoodleFragment de que a caixa de diálogo não está mais sendo exibida
   @Override
   public void onDetach() {
      super.onDetach();
      DoodleFragment fragment = getDoodleFragment();
      
      if (fragment != null)
         fragment.setDialogOnScreen(false);
   }

   // OnSeekBarChangeListener para os componentes SeekBar na caixa de diálogo de cor
   private OnSeekBarChangeListener colorChangedListener = 
     new OnSeekBarChangeListener() {
        // exibe a cor atualizada
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress,
         boolean fromUser) {
         if (fromUser) // o usuário, não o programa, alterou o cursor do componente SeekBar
            color = Color.argb(alphaSeekBar.getProgress(), 
               redSeekBar.getProgress(), greenSeekBar.getProgress(), 
               blueSeekBar.getProgress());
         colorView.setBackgroundColor(color);
      } 
      
      @Override
      public void onStartTrackingTouch(SeekBar seekBar) { // obrigatório
      } 
      
      @Override
      public void onStopTrackingTouch(SeekBar seekBar) { // obrigatório

      }
   }; // fim de colorChanged
} // fim da classe ColorDialogFragment

