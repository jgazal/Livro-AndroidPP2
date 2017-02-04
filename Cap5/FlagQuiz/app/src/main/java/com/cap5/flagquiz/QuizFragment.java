// Contém a lógica do aplicativo Flag Quiz
package com.cap5.flagquiz;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuizFragment extends Fragment {
   // String usada ao registrar mensagens de erro
   private static final String TAG = "FlagQuiz Activity";

   private static final int FLAGS_IN_QUIZ = 10; 
   
   private List<String> fileNameList; // nomes de arquivo de bandeira
   private List<String> quizCountriesList; // países no teste atual
   private Set<String> regionsSet; // regiões do mundo no teste atual
   private String correctAnswer; // país correto da bandeira atual
   private int totalGuesses; // número de palpites dados
   private int correctAnswers; // número de palpites corretos
   private int guessRows; // número de linhas exibindo Button de palpite
   private SecureRandom random; // usado para tornar o teste aleatório
   private Handler handler; // usado para atrasar o carregamento da próxima bandeira
   private Animation shakeAnimation; // animação para palpite incorreto
   
   private TextView questionNumberTextView; // mostra o número da pergunta atual
   private ImageView flagImageView; // exibe uma bandeira
   private LinearLayout[] guessLinearLayouts; // linhas de Buttons de resposta
   private TextView answerTextView; // exibe Correct! ou Incorrect!

   // configura QuizFragment quando sua View é criada
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);    
      View view = 
         inflater.inflate(R.layout.fragment_quiz, container, false);

      fileNameList = new ArrayList<String>();
      quizCountriesList = new ArrayList<String>();
      random = new SecureRandom(); 
      handler = new Handler();

      // carrega a animação de tremular utilizada para respostas incorretas
      shakeAnimation = AnimationUtils.loadAnimation(getActivity(), 
         R.anim.incorrect_shake); 
      shakeAnimation.setRepeatCount(3); // a animação se repete 3 vezes

      // obtém referências para componentes da interface gráfica do usuário
      questionNumberTextView = 
         (TextView) view.findViewById(R.id.questionNumberTextView);
      flagImageView = (ImageView) view.findViewById(R.id.flagImageView);
      guessLinearLayouts = new LinearLayout[3];
      guessLinearLayouts[0] = 
         (LinearLayout) view.findViewById(R.id.row1LinearLayout);
      guessLinearLayouts[1] = 
         (LinearLayout) view.findViewById(R.id.row2LinearLayout);
      guessLinearLayouts[2] = 
         (LinearLayout) view.findViewById(R.id.row3LinearLayout);
      answerTextView = (TextView) view.findViewById(R.id.answerTextView);

      // configura receptores para os componentes Button de palpite
      for (LinearLayout row : guessLinearLayouts) {
         for (int column = 0; column < row.getChildCount(); column++) {
            Button button = (Button) row.getChildAt(column);
            button.setOnClickListener(guessButtonListener);
         }
      }

      // configura o texto de questionNumberTextView
      questionNumberTextView.setText(
         getResources().getString(R.string.question, 1, FLAGS_IN_QUIZ));
      return view; // retorna a view do fragmento para exibir
   } // fim do método onCreateView

   // atualiza guessRows com base no valor em SharedPreferences
   public void updateGuessRows(SharedPreferences sharedPreferences) {
      // obtém o número de botões de palpite que devem ser exibidos
      String choices = 
         sharedPreferences.getString(MainActivity.CHOICES, null);
      guessRows = Integer.parseInt(choices) / 3;

      // oculta todos os componentes LinearLayout de botão de palpite
      for (LinearLayout layout : guessLinearLayouts)
         layout.setVisibility(View.INVISIBLE);

      // exibe os componentes LinearLayout de botão de palpite apropriados
      for (int row = 0; row < guessRows; row++) 
         guessLinearLayouts[row].setVisibility(View.VISIBLE);
   }

   // atualiza as regiões do mundo para o teste, com base nos valores de
   // SharedPreferences
   public void updateRegions(SharedPreferences sharedPreferences) {
      regionsSet = 
         sharedPreferences.getStringSet(MainActivity.REGIONS, null);
   }

   // prepara e inicia o próximo teste
   public void resetQuiz() {
      // usa AssetManager para obter nomes de arquivo de imagem para as regiões habilitadas
      AssetManager assets = getActivity().getAssets(); 
      fileNameList.clear(); // esvazia a lista de nomes de arquivo de imagem
      
      try {
         // faz loop por cada região
         for (String region : regionsSet) {
            // obtém uma lista de todos os arquivos de imagem de bandeira nessa região
            String[] paths = assets.list(region);

            for (String path : paths) 
               fileNameList.add(path.replace(".png", ""));
         } 
      } 
      catch (IOException exception) {
         Log.e(TAG, "Error loading image file names", exception);
      } 
      
      correctAnswers = 0; // redefine o número de respostas corretas dadas
      totalGuesses = 0; // redefine o número total de palpites dados pelo usuário
      quizCountriesList.clear(); // limpa a lista anterior de países do teste
      
      int flagCounter = 1; 
      int numberOfFlags = fileNameList.size();

      // adiciona FLAGS_IN_QUIZ nomes de arquivo aleatórios a quizCountriesList
      while (flagCounter <= FLAGS_IN_QUIZ) {
         int randomIndex = random.nextInt(numberOfFlags);

         // obtém o nome de arquivo aleatório
         String fileName = fileNameList.get(randomIndex);

         // se a região está habilitada e ainda não foi escolhida
         if (!quizCountriesList.contains(fileName)) {
            quizCountriesList.add(fileName); // adiciona o arquivo à lista
            ++flagCounter;
         } 
      } 

      loadNextFlag(); // inicia o teste carregando a primeira bandeira
   } // fim do método resetQuiz

   // depois que adivinha uma bandeira correta, carrega a próxima bandeira
   private void loadNextFlag() {
      // obtém o nome do arquivo da próxima bandeira e o remove da lista
      String nextImage = quizCountriesList.remove(0);
      correctAnswer = nextImage; // atualiza a resposta correta
      answerTextView.setText(""); // limpa answerTextView

      // exibe o número da pergunta atual
      questionNumberTextView.setText(
         getResources().getString(R.string.question, 
            (correctAnswers + 1), FLAGS_IN_QUIZ));

      // extrai a região a partir do nome da próxima imagem
      String region = nextImage.substring(0, nextImage.indexOf('-'));

      // usa AssetManager para carregar a próxima imagem da pasta assets
      AssetManager assets = getActivity().getAssets(); 

      try {
         // obtém um objeto InputStream para o asset que representa a próxima bandeira
         InputStream stream = 
            assets.open(region + "/" + nextImage + ".png");

         // carrega o asset como um objeto Drawable e exibe no componente flagImageView
         Drawable flag = Drawable.createFromStream(stream, nextImage);
         flagImageView.setImageDrawable(flag);                       
      } 
      catch (IOException exception) {
         Log.e(TAG, "Error loading " + nextImage, exception);
      } 

      Collections.shuffle(fileNameList); // embaralha os nomes de arquivo

      // coloca a resposta correta no final de fileNameList
      int correct = fileNameList.indexOf(correctAnswer);
      fileNameList.add(fileNameList.remove(correct));

      // adiciona 3, 6 ou 9 componentes Button de palpite, baseado no valor de guessRows
      for (int row = 0; row < guessRows; row++) {
         // coloca componentes Button em currentTableRow
         for (int column = 0; 
            column < guessLinearLayouts[row].getChildCount(); column++) {
            // obtém referência para o componente Button a configurar
            Button newGuessButton = 
               (Button) guessLinearLayouts[row].getChildAt(column);
            newGuessButton.setEnabled(true);

            // obtém o nome do país e o configura como o texto de newGuessButton
            String fileName = fileNameList.get((row * 3) + column);
            newGuessButton.setText(getCountryName(fileName));
         } 
      } 
      
      /// substitui aleatoriamente um componente Button com a resposta correta
      int row = random.nextInt(guessRows); // seleciona linha aleatória
      int column = random.nextInt(3); // seleciona coluna aleatória
      LinearLayout randomRow = guessLinearLayouts[row]; // obtém a linha
      String countryName = getCountryName(correctAnswer);
      ((Button) randomRow.getChildAt(column)).setText(countryName);    
   } // fim do método loadNextFlag

   // obtém o nome do arquivo de bandeira de países via parsing e retorna o nome do país gb
   private String getCountryName(String name) {
      return name.substring(name.indexOf('-') + 1).replace('_', ' ');
   }

   // chamado quando um componente Button de palpite é tocado
   private OnClickListener guessButtonListener = new OnClickListener() 
   {
      @Override
      public void onClick(View v) 
      {
         Button guessButton = ((Button) v); 
         String guess = guessButton.getText().toString();
         String answer = getCountryName(correctAnswer);
         ++totalGuesses; // incrementa o número de palpites dados pelo usuário
         
         if (guess.equals(answer)){ // se o palpite é correto

            ++correctAnswers; // incrementa o número de respostas corretas

            // exibe a resposta correta em texto verde
            answerTextView.setText(answer + "!");
            answerTextView.setTextColor(
               getResources().getColor(R.color.correct_answer));

            disableButtons(); // desabilita todos os componentes Button de palpite

            // se o usuário identificou FLAGS_IN_QUIZ bandeiras corretamente
            if (correctAnswers == FLAGS_IN_QUIZ) {
               // DialogFragment para exibir as estatísticas do teste e iniciar outro
               DialogFragment quizResults = 
                  new DialogFragment() {
                     // cria um componente AlertDialog e o retorna
                     @Override
                     public Dialog onCreateDialog(Bundle bundle)
                     {
                        AlertDialog.Builder builder = 
                           new AlertDialog.Builder(getActivity());
                        builder.setCancelable(false); 
                        
                        builder.setMessage(
                           getResources().getString(R.string.results, 
                           totalGuesses, (1000 / (double) totalGuesses)));

                        // componente Button “Reset Quiz”
                        builder.setPositiveButton(R.string.reset_quiz,
                           new DialogInterface.OnClickListener()                
                           {                                                       
                              public void onClick(DialogInterface dialog, 
                                 int id) 
                              {
                                 resetQuiz();                                      
                              } 
                           } // fim da classe interna anônima
                        ); // fim da chamada a setPositiveButton
                        
                        return builder.create(); // retorna o componente AlertDialog
                     } // fim do método onCreateDialog
                  }; // fim da classe interna anônima DialogFragment

               // usa FragmentManager para exibir o componente DialogFragment
               quizResults.show(getFragmentManager(), "quiz results");
            } 
            else {// a resposta está correta, mas o teste não acabou

               // carrega a próxima bandeira após um atraso de 2 segundos
               handler.postDelayed(
                  new Runnable()
                  { 
                     @Override
                     public void run()
                     {
                        loadNextFlag();
                     }
                  }, 2000); // 2000 milissegundos para um atraso de 2 segundos
            } 
         } 
         else { // o palpite foi incorreto

            flagImageView.startAnimation(shakeAnimation); // reproduz o tremular

            /// exibe “Incorrect!” em vermelho
            answerTextView.setText(R.string.incorrect_answer);
            answerTextView.setTextColor(
               getResources().getColor(R.color.incorrect_answer));
            guessButton.setEnabled(false); // desabilita a resposta incorreta
         } 
      }
   }; // fim de guessButtonListener

   // método utilitário que desabilita todos os componentes Button de resposta
   private void disableButtons() {
      for (int row = 0; row < guessRows; row++) {
         LinearLayout guessRow = guessLinearLayouts[row];
         for (int i = 0; i < guessRow.getChildCount(); i++)
            guessRow.getChildAt(i).setEnabled(false);
      } 
   } 
} // fim da classe FlagQuiz

