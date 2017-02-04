// Armazena os fragmentos do aplicativo Address Book
package com.cap8.addressbook;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
   implements ContactListFragment.ContactListFragmentListener,
      DetailsFragment.DetailsFragmentListener, 
      AddEditFragment.AddEditFragmentListener {
   // chaves para armazenar identificador de linha no objeto Bundle passado a um fragmento
   public static final String ROW_ID = "row_id"; 
   
   ContactListFragment contactListFragment; // exibe a lista de contatos

   // exibe ContactListFragment quando MainActivity é carregada
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // retorna se a atividade está sendo restaurada, não precisa recriar a
      // interface gráfica do usuário
      if (savedInstanceState != null) 
         return;

      // verifica se o layout contém fragmentContainer (layout para telefone);
      // ContactListFragment é sempre exibido
      if (findViewById(R.id.fragmentContainer) != null) {
         // cria ContactListFragment
         contactListFragment = new ContactListFragment();

         // adiciona o fragmento a FrameLayout
         FragmentTransaction transaction = 
            getFragmentManager().beginTransaction();
         transaction.add(R.id.fragmentContainer, contactListFragment);
         transaction.commit(); // faz ContactListFragment aparecer
      }
   }

   // chamado quando MainActivity recomeça
   @Override
   protected void onResume() {
      super.onResume();

      // se contactListFragment é null, a atividade está sendo executada em tablet;
      // portanto, obtém referência a partir de FragmentManager
      if (contactListFragment == null) {
         contactListFragment = 
            (ContactListFragment) getFragmentManager().findFragmentById(
               R.id.contactListFragment);      
      }
   }

   // exibe DetailsFragment do contato selecionado
   @Override
   public void onContactSelected(long rowID) {
      if (findViewById(R.id.fragmentContainer) != null) // telefone
         displayContact(rowID, R.id.fragmentContainer);
      else {// tablet

         getFragmentManager().popBackStack();  // remove o topo da pilha de retrocesso
         displayContact(rowID, R.id.rightPaneContainer);
      }
   }

   // exibe um contato
   private void displayContact(long rowID, int viewID) {
      DetailsFragment detailsFragment = new DetailsFragment();

      // especifica rowID como argumento para DetailsFragment
      Bundle arguments = new Bundle();
      arguments.putLong(ROW_ID, rowID);
      detailsFragment.setArguments(arguments);

      // usa um elemento FragmentTransaction para exibir o componente DetailsFragment
      FragmentTransaction transaction = 
         getFragmentManager().beginTransaction();
      transaction.replace(viewID, detailsFragment);
      transaction.addToBackStack(null);
      transaction.commit(); // faz DetailsFragment aparecer
   }

   // exibe o componente AddEditFragment para adicionar um novo contato
   @Override
   public void onAddContact() {
      if (findViewById(R.id.fragmentContainer) != null) // telefone
         displayAddEditFragment(R.id.fragmentContainer, null); 
      else // tablet
         displayAddEditFragment(R.id.rightPaneContainer, null);
   }

   // exibe fragmento para adicionar um novo contato ou editar um já existente
   private void displayAddEditFragment(int viewID, Bundle arguments) {
      AddEditFragment addEditFragment = new AddEditFragment();
      
      if (arguments != null) // editando contato existente
         addEditFragment.setArguments(arguments);

      // usa um elemento FragmentTransaction para exibir o componente AddEditFragment
      FragmentTransaction transaction = 
         getFragmentManager().beginTransaction();
      transaction.replace(viewID, addEditFragment);
      transaction.addToBackStack(null);
      transaction.commit(); // faz AddEditFragment aparecer
   }

   // retorna à lista de contatos quando exibiu contato excluído
   @Override
   public void onContactDeleted() {
      getFragmentManager().popBackStack(); // remove o topo da pilha de retrocesso
      
      if (findViewById(R.id.fragmentContainer) == null) // tablet
         contactListFragment.updateContactList();
   }

   // exibe o componente AddEditFragment para editar um contato já existente
   @Override
   public void onEditContact(Bundle arguments) {
      if (findViewById(R.id.fragmentContainer) != null) // telefone
         displayAddEditFragment(R.id.fragmentContainer, arguments); 
      else // tablet
         displayAddEditFragment(R.id.rightPaneContainer, arguments);
   }

   // atualiza a interface gráfica do usuário após um contato novo ou atualizado ser salvo
   @Override
   public void onAddEditCompleted(long rowID) {
      getFragmentManager().popBackStack(); // remove o topo da pilha de retrocesso

      if (findViewById(R.id.fragmentContainer) == null) {// tablet

         getFragmentManager().popBackStack(); // remove o topo da pilha de retrocesso
         contactListFragment.updateContactList(); // atualiza os contatos

         // em tablet, exibe o contato que acabou de ser adicionado ou editado
         displayContact(rowID, R.id.rightPaneContainer); 
      }
   }   
}

