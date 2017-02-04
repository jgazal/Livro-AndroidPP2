// Fornece fácil conexão e criação do banco de dados UserContacts.
package com.cap8.addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector {
   // nome do banco de dados
   private static final String DATABASE_NAME = "UserContacts";
      
   private SQLiteDatabase database; // para interagir com o banco de dados
   private DatabaseOpenHelper databaseOpenHelper; // cria o banco de dados

   // construtor public de DatabaseConnector
   public DatabaseConnector(Context context) {
      // cria um novo DatabaseOpenHelper
      databaseOpenHelper = 
         new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
   }

   // abre a conexão de banco de dados
   public void open() throws SQLException {
      // cria ou abre um banco de dados para leitura/gravação
      database = databaseOpenHelper.getWritableDatabase();
   }

   // fecha a conexão com o banco de dados
   public void close() {
      if (database != null)
         database.close(); // fecha a conexão com o banco de dados
   }

   // insere um novo contato no banco de dados
   public long insertContact(String name, String phone, String email,  
      String street, String city, String state, String zip) {
      ContentValues newContact = new ContentValues();
      newContact.put("name", name);
      newContact.put("phone", phone);
      newContact.put("email", email);
      newContact.put("street", street);
      newContact.put("city", city);
      newContact.put("state", state);
      newContact.put("zip", zip);

      open(); // abre o banco de dados
      long rowID = database.insert("contacts", null, newContact);
      close(); // fecha o banco de dados
      return rowID;
   } // fim do método insertContact

   // atualiza um contato existente no banco de dados
   public void updateContact(long id, String name, String phone, 
      String email, String street, String city, String state, String zip) {
      ContentValues editContact = new ContentValues();
      editContact.put("name", name);
      editContact.put("phone", phone);
      editContact.put("email", email);
      editContact.put("street", street);
      editContact.put("city", city);
      editContact.put("state", state);
      editContact.put("zip", zip);

      open(); // abre o banco de dados
      database.update("contacts", editContact, "_id=" + id, null);
      close(); // fecha o banco de dados
   } // fim do método updateContact

   // retorna um Cursor com todos os nomes de contato do banco de dados
   public Cursor getAllContacts() {
      return database.query("contacts", new String[] {"_id", "name"}, 
         null, null, null, null, "name");
   }

   // retorna um Cursor contendo as informações do contato especificado
   public Cursor getOneContact(long id) {
      return database.query(
         "contacts", null, "_id=" + id, null, null, null, null);
   }

   // exclui o contato especificado pelo nome String fornecido
   public void deleteContact(long id) {
      open(); // abre o banco de dados
      database.delete("contacts", "_id=" + id, null);
      close(); // fecha o banco de dados
   } 
   
   private class DatabaseOpenHelper extends SQLiteOpenHelper {
      // construtor
      public DatabaseOpenHelper(Context context, String name,
         CursorFactory factory, int version) {
         super(context, name, factory, version);
      }

      // cria a tabela de contatos quando o banco de dados é gerado
      @Override
      public void onCreate(SQLiteDatabase db) {
         // consulta para criar uma nova tabela chamada contacts
         String createQuery = "CREATE TABLE contacts" +
            "(_id integer primary key autoincrement," +
            "name TEXT, phone TEXT, email TEXT, " +
            "street TEXT, city TEXT, state TEXT, zip TEXT);";
                  
         db.execSQL(createQuery); // executa a consulta para criar o banco de dados
      } 

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
          int newVersion) {
      }
   } // fim da classe DatabaseOpenHelper
} // fim da classe DatabaseConnector

