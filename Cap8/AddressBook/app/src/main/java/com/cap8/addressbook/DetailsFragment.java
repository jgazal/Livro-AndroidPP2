// Exibe os detalhes de um contato
package com.cap8.addressbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
   // métodos de callback implementados por MainActivity
   public interface DetailsFragmentListener {
      // chamado quando um contato é excluído
      public void onContactDeleted();

      // chamado para passar objeto Bundle com informações de contato para edição
      public void onEditContact(Bundle arguments);
   }
   
   private DetailsFragmentListener listener;
   
   private long rowID = -1; // rowID do contato selecionado
   private TextView nameTextView; // exibe o nome do contato
   private TextView phoneTextView; // exibe o telefone do contato
   private TextView emailTextView; // exibe o e-mail do contato
   private TextView streetTextView; // exibe a rua do contato
   private TextView cityTextView; // exibe a cidade do contato
   private TextView stateTextView; // exibe o estado do contato
   private TextView zipTextView; // exibe o código postal do contato

   // configura DetailsFragmentListener quando o fragmento é anexado
   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
      listener = (DetailsFragmentListener) activity;
   }

   // remove DetailsFragmentListener quando o fragmento é desanexado
   @Override
   public void onDetach() {
      super.onDetach();
      listener = null;
   }

   // chamado quando o view de DetailsFragmentListener precisa ser criada
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);  
      setRetainInstance(true); // salva o fragmento entre mudanças de configuração

      // se DetailsFragment está sendo restaurado, obtém o identificador de linha salvo
      if (savedInstanceState != null) 
         rowID = savedInstanceState.getLong(MainActivity.ROW_ID);
      else {
         // obtém o Bundle de argumentos e extrai o identificador de linha do contato
         Bundle arguments = getArguments(); 
         
         if (arguments != null)
            rowID = arguments.getLong(MainActivity.ROW_ID);
      }

      // infla o layout de DetailsFragment
      View view = 
         inflater.inflate(R.layout.fragment_details, container, false);               
      setHasOptionsMenu(true); // este fragmento tem itens de menu a exibir

      // obtém os componentes EditText
      nameTextView = (TextView) view.findViewById(R.id.nameTextView);
      phoneTextView = (TextView) view.findViewById(R.id.phoneTextView);
      emailTextView = (TextView) view.findViewById(R.id.emailTextView);
      streetTextView = (TextView) view.findViewById(R.id.streetTextView);
      cityTextView = (TextView) view.findViewById(R.id.cityTextView);
      stateTextView = (TextView) view.findViewById(R.id.stateTextView);
      zipTextView = (TextView) view.findViewById(R.id.zipTextView);
      return view;
   }

   // chamado quando o elemento DetailsFragment recomeça
   @Override
   public void onResume() {
      super.onResume();
      new LoadContactTask().execute(rowID); // carrega o contato em rowID
   }

   // salva o identificador de linha do contato que está sendo exibido
   @Override
   public void onSaveInstanceState(Bundle outState) {
       super.onSaveInstanceState(outState);
       outState.putLong(MainActivity.ROW_ID, rowID);
   }

   // exibe os seleções de menu deste fragmento
   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.fragment_details_menu, menu);
   }

   // trata as seleções de item de menu
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_edit:
            // cria objeto Bundle contendo os dados do contato a editar
            Bundle arguments = new Bundle();
            arguments.putLong(MainActivity.ROW_ID, rowID);
            arguments.putCharSequence("name", nameTextView.getText());
            arguments.putCharSequence("phone", phoneTextView.getText());
            arguments.putCharSequence("email", emailTextView.getText());
            arguments.putCharSequence("street", streetTextView.getText());
            arguments.putCharSequence("city", cityTextView.getText());
            arguments.putCharSequence("state", stateTextView.getText());
            arguments.putCharSequence("zip", zipTextView.getText());            
            listener.onEditContact(arguments); // passa objeto Bundle para o receptor
            return true;
         case R.id.action_delete:
            deleteContact();
            return true;
      }
      
      return super.onOptionsItemSelected(item);
   }

   // executa a consulta de banco de dados fora da thread da interface gráfica do usuário
   private class LoadContactTask extends AsyncTask<Long, Object, Cursor> {
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(getActivity());

      // abre o banco de dados e obtém o objeto Cursor que representa os dados do
      // contato especificado
      @Override
      protected Cursor doInBackground(Long... params) {
         databaseConnector.open();
         return databaseConnector.getOneContact(params[0]);
      }

      // usa o Cursor retornado pelo método doInBackground
      @Override
      protected void onPostExecute(Cursor result) {
         super.onPostExecute(result);
         result.moveToFirst(); // move para o primeiro item

         // obtém o índice de coluna de cada item de dado
         int nameIndex = result.getColumnIndex("name");
         int phoneIndex = result.getColumnIndex("phone");
         int emailIndex = result.getColumnIndex("email");
         int streetIndex = result.getColumnIndex("street");
         int cityIndex = result.getColumnIndex("city");
         int stateIndex = result.getColumnIndex("state");
         int zipIndex = result.getColumnIndex("zip");

         // preenche os componentes TextView com os dados recuperados
         nameTextView.setText(result.getString(nameIndex));
         phoneTextView.setText(result.getString(phoneIndex));
         emailTextView.setText(result.getString(emailIndex));
         streetTextView.setText(result.getString(streetIndex));
         cityTextView.setText(result.getString(cityIndex));
         stateTextView.setText(result.getString(stateIndex));
         zipTextView.setText(result.getString(zipIndex));
   
         result.close(); // fecha o cursor de resultado
         databaseConnector.close(); // fecha a conexão de banco de dados
      } // fim do método onPostExecute
   } // fim da classe LoadContactTask

   // exclui um contato
   private void deleteContact() {
      // usa FragmentManager para exibir o componente DialogFragment de confirmDelete
      confirmDelete.show(getFragmentManager(), "confirm delete");
   }

   // DialogFragment para confirmar a exclusão de contato
   private DialogFragment confirmDelete = 
      new DialogFragment() {
         // cria um componente AlertDialog e o retorna
         @Override
         public Dialog onCreateDialog(Bundle bundle) {
            // cria um novo AlertDialog Builder
            AlertDialog.Builder builder = 
               new AlertDialog.Builder(getActivity());
      
            builder.setTitle(R.string.confirm_title); 
            builder.setMessage(R.string.confirm_message);

            // fornece um botão OK que simplesmente descarta a caixa de diálogo
            builder.setPositiveButton(R.string.button_delete,
               new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(
                     DialogInterface dialog, int button) {
                     final DatabaseConnector databaseConnector = 
                        new DatabaseConnector(getActivity());

                     // AsyncTask exclui contato e notifica o receptor
                     AsyncTask<Long, Object, Object> deleteTask =
                        new AsyncTask<Long, Object, Object>() {
                           @Override
                           protected Object doInBackground(Long... params) {
                              databaseConnector.deleteContact(params[0]); 
                              return null;
                           } 
      
                           @Override
                           protected void onPostExecute(Object result) {
                              listener.onContactDeleted();
                           }
                        }; // fim de AsyncTask

                     // executa AsyncTask para excluir o contato em rowID
                     deleteTask.execute(new Long[] { rowID });               
                  } // fim do método onClick
               } // fim da classe interna anônima
            ); // fim da chamada ao método setPositiveButton
            
            builder.setNegativeButton(R.string.button_cancel, null);
            return builder.create(); // retorna o componente AlertDialog
         }
      }; // fim da classe interna anônima de DialogFragment
} // fim da classe DetailsFragment

