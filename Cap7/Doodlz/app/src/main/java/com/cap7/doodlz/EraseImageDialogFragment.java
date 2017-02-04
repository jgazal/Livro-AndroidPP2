// Permite ao usuário apagar a imagem
package com.cap7.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

// classe para a caixa de diálogo Select Color
public class EraseImageDialogFragment extends DialogFragment {
   // cria um componente AlertDialog e o retorna
   @Override
   public Dialog onCreateDialog(Bundle bundle) {
      AlertDialog.Builder builder = 
         new AlertDialog.Builder(getActivity());

      // configura a mensagem de AlertDialog
      builder.setMessage(R.string.message_erase);
      builder.setCancelable(false);
    
      // adiciona o componente Button Erase
      builder.setPositiveButton(R.string.button_erase,
         new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               getDoodleFragment().getDoodleView().clear(); // clear image
            } 
         } 
      ); // fim da chamada a setPositiveButton

      // adiciona o componente Button Cancel
      builder.setNegativeButton(R.string.button_cancel, null);

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
} // fim da classe EraseImageDialogFragment

