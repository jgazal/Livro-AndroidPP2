// Permite ao usuário adicionar um novo contato ou editar um já existente
package com.cap8.addressbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class AddEditFragment extends Fragment {
   // método de callback implementado por MainActivity
   public interface AddEditFragmentListener {
      // chamado após a conclusão da edição para que o contato possa ser reexibido
      public void onAddEditCompleted(long rowID);
   }
   
   private AddEditFragmentListener listener; 
   
   private long rowID; // identificador de linha do contato no banco de dados
   private Bundle contactInfoBundle; // argumentos para editar um contato

   // componentes EditText para informações de contato
   private EditText nameEditText;
   private EditText phoneEditText;
   private EditText emailEditText;
   private EditText streetEditText;
   private EditText cityEditText;
   private EditText stateEditText;
   private EditText zipEditText;

   // configura AddEditFragmentListener quando o fragmento é anexado
   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
      listener = (AddEditFragmentListener) activity; 
   }

   // remove AddEditFragmentListener quando o fragmento é desanexado
   @Override
   public void onDetach() {
      super.onDetach();
      listener = null; 
   }

   // chamado quando a view do fragmento precisa ser criada
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);    
      setRetainInstance(true); // salva o fragmento entre mudanças de configuração
      setHasOptionsMenu(true); // o fragmento tem itens de menu a exibir

      // infla a interface gráfica do usuário e obtém referências para os componentes EditText
      View view = 
         inflater.inflate(R.layout.fragment_add_edit, container, false);
      nameEditText = (EditText) view.findViewById(R.id.nameEditText);
      phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
      emailEditText = (EditText) view.findViewById(R.id.emailEditText);
      streetEditText = (EditText) view.findViewById(R.id.streetEditText);
      cityEditText = (EditText) view.findViewById(R.id.cityEditText);
      stateEditText = (EditText) view.findViewById(R.id.stateEditText);
      zipEditText = (EditText) view.findViewById(R.id.zipEditText);

      contactInfoBundle = getArguments(); // null, se for a criação de um novo contato

      if (contactInfoBundle != null) {
         rowID = contactInfoBundle.getLong(MainActivity.ROW_ID);
         nameEditText.setText(contactInfoBundle.getString("name"));  
         phoneEditText.setText(contactInfoBundle.getString("phone"));  
         emailEditText.setText(contactInfoBundle.getString("email"));  
         streetEditText.setText(contactInfoBundle.getString("street"));  
         cityEditText.setText(contactInfoBundle.getString("city"));  
         stateEditText.setText(contactInfoBundle.getString("state"));  
         zipEditText.setText(contactInfoBundle.getString("zip"));  
      }

      // configura o receptor de eventos do componente Button Save Contact
      Button saveContactButton = 
         (Button) view.findViewById(R.id.saveContactButton);
      saveContactButton.setOnClickListener(saveContactButtonClicked);
      return view;
   }

   // responde ao evento gerado quando o usuário salva um contato
   OnClickListener saveContactButtonClicked = new OnClickListener() {
      @Override
      public void onClick(View v) {
         if (nameEditText.getText().toString().trim().length() != 0) {
            // AsyncTask para salvar contato e, então, notificar o receptor
            AsyncTask<Object, Object, Object> saveContactTask = 
               new AsyncTask<Object, Object, Object>() {
                  @Override
                  protected Object doInBackground(Object... params) {
                     saveContact(); // salva o contato no banco de dados
                     return null;
                  } 
      
                  @Override
                  protected void onPostExecute(Object result) {
                     // oculta o teclado virtual
                     InputMethodManager imm = (InputMethodManager) 
                        getActivity().getSystemService(
                           Context.INPUT_METHOD_SERVICE);
                     imm.hideSoftInputFromWindow(
                        getView().getWindowToken(), 0);

                     listener.onAddEditCompleted(rowID);
                  } 
               }; // fim de AsyncTask

            // salva o contato no banco de dados usando uma thread separada
            saveContactTask.execute((Object[]) null); 
         } 
         else {// o nome do contato obrigatório está em branco; portanto, exibe
            // caixa de diálogo de erro

            DialogFragment errorSaving = 
               new DialogFragment() {
                  @Override
                  public Dialog onCreateDialog(Bundle savedInstanceState) {
                     AlertDialog.Builder builder = 
                        new AlertDialog.Builder(getActivity());
                     builder.setMessage(R.string.error_message);
                     builder.setPositiveButton(R.string.ok, null);                     
                     return builder.create();
                  }               
               };
            
            errorSaving.show(getFragmentManager(), "error saving contact");
         } 
      } // fim do método onClick
   }; // fim de OnClickListener saveContactButtonClicked

   // salva informações de um contato no banco de dados
   private void saveContact() {
      // obtém DatabaseConnector para interagir com o banco de dados SQLite
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(getActivity());

      if (contactInfoBundle == null) {
         // insere as informações do contato no banco de dados
         rowID = databaseConnector.insertContact(
            nameEditText.getText().toString(),
            phoneEditText.getText().toString(), 
            emailEditText.getText().toString(), 
            streetEditText.getText().toString(),
            cityEditText.getText().toString(), 
            stateEditText.getText().toString(), 
            zipEditText.getText().toString());
      } 
      else {
         databaseConnector.updateContact(rowID,
            nameEditText.getText().toString(),
            phoneEditText.getText().toString(), 
            emailEditText.getText().toString(), 
            streetEditText.getText().toString(),
            cityEditText.getText().toString(), 
            stateEditText.getText().toString(), 
            zipEditText.getText().toString());
      }
   } // fim do método saveContact
} // fim da classe AddEditFragment

