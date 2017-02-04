// Permite ao usuário configurar a cor de desenho no elemento DoodleView
package com.cap7.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

// classe para a caixa de diálogo Select Color
public class LineWidthDialogFragment extends DialogFragment {
   private ImageView widthImageView;

   // cria um componente AlertDialog e o retorna
   @Override
   public Dialog onCreateDialog(Bundle bundle) {
      AlertDialog.Builder builder = 
         new AlertDialog.Builder(getActivity());
      View lineWidthDialogView = getActivity().getLayoutInflater().inflate(
         R.layout.fragment_line_width, null);
      builder.setView(lineWidthDialogView); // adiciona a interface gráfica do
                                            // usuário à caixa de diálogo

      // configura a mensagem de AlertDialog
      builder.setTitle(R.string.title_line_width_dialog);
      builder.setCancelable(true);

      // obtém o componente ImageView
      widthImageView = (ImageView) lineWidthDialogView.findViewById(
         R.id.widthImageView);

      // configura widthSeekBar
      final DoodleView doodleView = getDoodleFragment().getDoodleView();
      final SeekBar widthSeekBar = (SeekBar) 
         lineWidthDialogView.findViewById(R.id.widthSeekBar);
      widthSeekBar.setOnSeekBarChangeListener(lineWidthChanged);
      widthSeekBar.setProgress(doodleView.getLineWidth());

      // adiciona o componente Button Set Line Width
      builder.setPositiveButton(R.string.button_set_line_width,
         new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               doodleView.setLineWidth(widthSeekBar.getProgress());
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

   // OnSeekBarChangeListener para o componente SeekBar na caixa de diálogo de largura
   private OnSeekBarChangeListener lineWidthChanged = 
      new OnSeekBarChangeListener() {
         Bitmap bitmap = Bitmap.createBitmap( 
            400, 100, Bitmap.Config.ARGB_8888);
         Canvas canvas = new Canvas(bitmap); // associa ao Canvas
         
         @Override
         public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
            // configura um objeto Paint para o valor de SeekBar atual
            Paint p = new Paint();
            p.setColor(
               getDoodleFragment().getDoodleView().getDrawingColor());
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(progress);

            // apaga o bitmap e redesenha a linha
            bitmap.eraseColor(
               getResources().getColor(android.R.color.transparent));
            canvas.drawLine(30, 50, 370, 50, p);
            widthImageView.setImageBitmap(bitmap);
         } 
   
         @Override
         public void onStartTrackingTouch(SeekBar seekBar) { // obrigatório

         } 
   
         @Override
         public void onStopTrackingTouch(SeekBar seekBar)  { // obrigatório

         } 
      }; // fim de lineWidthChanged
}
