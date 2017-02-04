// Exibe a lista de nomes de contato
package com.cap8.addressbook;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactListFragment extends ListFragment {
   // métodos de callback implementados por MainActivity
   public interface ContactListFragmentListener {
      // chamado quando o usuário seleciona um contato
      public void onContactSelected(long rowID);

      // chamado quando o usuário decide adicionar um contato
      public void onAddContact();
   }
   
   private ContactListFragmentListener listener; 
   
   private ListView contactListView; // ListView de ListActivity
   private CursorAdapter contactAdapter; // adaptador para ListView

   // configura ContactListFragmentListener quando o fragmento é anexado
   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
      listener = (ContactListFragmentListener) activity;
   }

   // remove ContactListFragmentListener quando o fragmento é desanexado
   @Override
   public void onDetach() {
      super.onDetach();
      listener = null;
   }

   // chamado depois que a View é criada
   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      setRetainInstance(true); // salva o fragmento entre mudanças de configuração
      setHasOptionsMenu(true); // este fragmento tem itens de menu a exibir

      // configura o texto a exibir quando não houver contatos
      setEmptyText(getResources().getString(R.string.no_contacts));

      // obtém referência de ListView e configura ListView
      contactListView = getListView(); 
      contactListView.setOnItemClickListener(viewContactListener);      
      contactListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

      // mapeia o nome de cada contato em um componente TextView no layout de ListView
      String[] from = new String[] { "name" };
      int[] to = new int[] { android.R.id.text1 };
      contactAdapter = new SimpleCursorAdapter(getActivity(), 
         android.R.layout.simple_list_item_1, null, from, to, 0);
      setListAdapter(contactAdapter); // configura o adaptador que fornece dados
   }

   // responde ao toque do usuário no nome de um contato no componente ListView
   OnItemClickListener viewContactListener = new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, 
         int position, long id) {
         listener.onContactSelected(id); // passa a seleção para MainActivity
      } 
   }; // fim de viewContactListener

   // quando o fragmento recomeça, usa um elemento GetContactsTask para carregar os contatos
   @Override
   public void onResume() {
      super.onResume(); 
      new GetContactsTask().execute((Object[]) null);
   }

   // executa a consulta de banco de dados fora da thread da interface gráfica do usuário
   private class GetContactsTask extends AsyncTask<Object, Object, Cursor> {
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(getActivity());

      // abre o banco de dados e retorna um Cursor para todos os contatos
      @Override
      protected Cursor doInBackground(Object... params) {
         databaseConnector.open();
         return databaseConnector.getAllContacts(); 
      }

      // usa o Cursor retornado pelo método doInBackground
      @Override
      protected void onPostExecute(Cursor result) {
         contactAdapter.changeCursor(result); // configura o Cursor do adaptador
         databaseConnector.close();
      } 
   } // fim da classe GetContactsTask

   // quando o fragmento para, fecha o Cursor e remove de contactAdapter
   @Override
   public void onStop() {
      Cursor cursor = contactAdapter.getCursor(); // obtém o objeto Cursor atual
      contactAdapter.changeCursor(null); // agora o adaptador não tem objeto Cursor
      
      if (cursor != null) 
         cursor.close(); // libera os recursos do Cursor
      
      super.onStop();
   }

   // exibe os itens de menu deste fragmento
   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.fragment_contact_list_menu, menu);
   }

   // trata a escolha no menu de opções
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_add:
            listener.onAddContact();
            return true;
      }
      
      return super.onOptionsItemSelected(item); // chama o método de super
   }

   // atualiza o conjunto de dados
   public void updateContactList() {
      new GetContactsTask().execute((Object[]) null);
   }
} // fim da classe ContactListFragment

