package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class Main extends Application {
private String currentWord; // the randomly selected word
private TextField guessField; // the user enters their guess here
private Text currentWordText; // show the current word (with - for unguessed letters)
private Text outcomeText; // show the outcome of each guess and the game
private Text wrongGuessesText; // show a list of incorrect guesses
private Text wrongGuessNumberText; // show how many incorrect guesses (or how many guesses remain)
private final static int MAX_WRONG_GUESSES = 7;
private static final Color TITLE_AND_OUTCOME_COLOR = Color.rgb(221, 160, 221);
private static final Color INFO_COLOR = Color.rgb(224, 255, 255);
private static final Color WORD_COLOR = Color.rgb(224, 255, 255);
private ArrayList <Character> incorrectGuesses = new ArrayList<Character>();
private int wrongGuessNum = 0;
private String wrongGuesses = "";
private String currentWordTextFiller = "";
private boolean startGame = true;

	public void start(Stage primaryStage) {
		VBox mainVBox = new VBox();
		mainVBox.setStyle("-fx-background-color: royalblue");
		mainVBox.setAlignment(Pos.CENTER);
		mainVBox.setSpacing(10);
	
		Text welcomeText = new Text("Welcome to Hangman!");
		welcomeText.setFont(Font.font("Helvetica", FontWeight.BOLD, 36));
		welcomeText.setFill(TITLE_AND_OUTCOME_COLOR);
		Text introText1 = new Text("Guess a letter.");
		Text introText2 = new Text("You can make " + MAX_WRONG_GUESSES + " wrong guesses!");
		introText1.setFont(Font.font("Helvetica", 24));
		introText1.setFill(INFO_COLOR);
		introText2.setFont(Font.font("Helvetica", 24));
		introText2.setFill(INFO_COLOR);
	
		VBox introBox = new VBox(welcomeText, introText1, introText2);
		introBox.setAlignment(Pos.CENTER);
		introBox.setSpacing(10);
		mainVBox.getChildren().add(introBox);
	
		// create before game is started
	
		outcomeText = new Text("");
		guessField = new TextField();
		wrongGuessNumberText = new Text("");
		currentWord = chooseWord();
		for (int i = 0; i < currentWord.length(); i++) {
			currentWordTextFiller = currentWordTextFiller + "-";  //for length of random word, set up correct spacing
		}
		currentWordText = new Text(currentWordTextFiller);
		wrongGuessesText = new Text("Wrong Guesses: []");
		currentWordText.setFont(Font.font("Helvetica", FontWeight.BOLD, 48));
		currentWordText.setFill(WORD_COLOR);
		HBox currentBox = new HBox(currentWordText);
		currentBox.setAlignment(Pos.CENTER);
		currentBox.setSpacing(10);
		mainVBox.getChildren().add(currentBox);
	
		Text guessIntroText = new Text("Enter your guess: ");
		guessIntroText.setFont(Font.font("Helvetica", 26));
		guessIntroText.setFill(INFO_COLOR);
		if (startGame == true) {
			guessField.setEditable(true);
		}
		guessField.setOnAction(this::handleGuessField);
		HBox guessBox = new HBox(guessIntroText, guessField);
		guessBox.setAlignment(Pos.CENTER);
		guessBox.setSpacing(10);
		mainVBox.getChildren().add(guessBox);
	
		outcomeText.setFont(Font.font("Helvetica", 28));
		outcomeText.setFill(TITLE_AND_OUTCOME_COLOR);
		HBox outcomeBox = new HBox(outcomeText);
		outcomeBox.setAlignment(Pos.CENTER);
		outcomeBox.setSpacing(10);
		mainVBox.getChildren().add(outcomeBox);
	
		wrongGuessesText.setFont(Font.font("Helvetica", 24));
		wrongGuessesText.setFill(INFO_COLOR);
		HBox wrongGuessesBox = new HBox(wrongGuessesText);
		wrongGuessesBox.setAlignment(Pos.CENTER);
		wrongGuessesBox.setSpacing(10);
		mainVBox.getChildren().add(wrongGuessesBox);
	
		wrongGuessNumberText.setFont(Font.font("Helvetica", 24));
		wrongGuessNumberText.setFill(INFO_COLOR);
		HBox wrongGuessNumberBox = new HBox(wrongGuessNumberText);
		wrongGuessNumberBox.setAlignment(Pos.CENTER);
		mainVBox.getChildren().add(wrongGuessNumberBox);
	
		Scene scene = new Scene(mainVBox, 550, 500);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	
	private void handleGuessField(ActionEvent event) {
		String testUserGuess = guessField.getText().toLowerCase(); //ignores upper case
		Character userGuess;
			if (isGuessValid(testUserGuess) == true) { //checks if the user's guess is valid
				userGuess = testUserGuess.charAt(0); //if the user's guess is valid, set the character to userGuess
				if (currentWord.indexOf(userGuess) == -1) { 
					updateWhenWrong(userGuess); //if the user's guess is not in the mystery word, it will call updateWhenWrong
				}
				else {
					updateWhenRight(userGuess); //if the user's guess is in the mystery word, it will call updateWhenRight
				}
			}
	}
		
	private void updateWhenWrong(Character userGuess) {
		int checkDuplicate = 0;
		outcomeText.setText("Letter not found: " + userGuess);
		for (int i = 0; i < incorrectGuesses.size(); i++) { ///check for duplicate
			if (incorrectGuesses.get(i).equals(userGuess)) {
				checkDuplicate += 1;
				guessField.clear(); //clears the user's guess because it is a duplicate
			}
		}
		if (!(checkDuplicate >= 1)) {  //if not duplicate then code continues
			incorrectGuesses.add(userGuess); //adds userGuess to keep track of what the user has guessed already
			if (wrongGuessNum == 0) {
				wrongGuesses = String.valueOf(userGuess);  //first incorrect guess will display just as the value
			}
			else {
				wrongGuesses = wrongGuesses + ", " + String.valueOf(userGuess); //other guesses will have commas to separate each letter
			}
			wrongGuessesText.setText("Wrong Guesses: [" + wrongGuesses + "]"); //updates the wrongGuess array
			wrongGuessNum += 1; //keeps track of how many incorrect guesses
			String updateWrongNum = String.valueOf(Integer.valueOf(MAX_WRONG_GUESSES) - wrongGuessNum); //updates how many incorrect guesses the user has left
			wrongGuessNumberText.setText(updateWrongNum + " more guesses");
			guessField.clear(); //all other parts of the game clear the guess field too so keep it consistent
			if (wrongGuessNum == MAX_WRONG_GUESSES) {
				outcomeText.setText("You lost!"); //if the user has made as many incorrect guesses as the max, the user loses and cannot play anymore
				guessField.setEditable(false);
			}
		}
	}
	
	private void updateWhenRight(Character userGuess) {
		StringBuilder tempWord = new StringBuilder("");
		outcomeText.setText("Letter found: " + userGuess); //updates outcomeText
		int letterLocation = currentWord.indexOf(userGuess); //finds the location of the letter that the userGuessed
		tempWord.append(currentWordTextFiller); //copies what is displayed to the user which will start with dashes
		while (letterLocation != -1) { //loops to make sure all instances are found
			tempWord.setCharAt(letterLocation, userGuess); //sets the user's guess to the location which is was found in currentWord
			currentWordTextFiller = tempWord.toString(); //copies the tempWord to currentWordTextFiller 
			currentWordText.setText(currentWordTextFiller); //displays it to the user
			letterLocation = currentWord.indexOf(userGuess, letterLocation + 1); //end of loop to make sure instances are found
		}
		guessField.clear();
		if (currentWordTextFiller.equals(currentWord)) { //if the variable == currentWord then the user has won. 
			outcomeText.setText("You win!");
			guessField.setEditable(false);
		}
		
	}
	
	private boolean isGuessValid(String userInput) {
		Alert error;
		try {
			if (userInput.isEmpty()) { //scans for whitespace or space
				guessField.clear();
				throw new Exception("Empty.");
			}
			if (isNumber(userInput) == true) { //scans for if number
				guessField.clear(); 
				throw new Exception("Dont guess numbers.");
			}
			if (length1(guessField.getText()) == false) { //scans for length > 1
				guessField.clear();
				throw new Exception("Guess only one letter at a time.");
			}
			if (Character.isLetter(userInput.charAt(0)) == false) { //already scans for numbers and whitespace so this will only be false if it is a special char
				guessField.clear();
				throw new Exception("You cannot guess special characters.");
			}
		}
		catch (Exception e) {
			error = new Alert(AlertType.ERROR, e.getMessage());
			error.showAndWait();
			return false;
		}
		return true;
	}
	
	private boolean isNumber(String userInput) {
		try {
			double test = Double.parseDouble(userInput);
		}
		catch (NumberFormatException error) {
			return false;
		}
		return true;
	} 
	
	private boolean length1 (String userInput) {
		if (userInput.length() > 1) {
			return false;
		}
		else {
			return true;
		}
	}

	private String chooseWord() {
		String chosenWord = "";
		ArrayList <String> manyWords = new ArrayList<String>();
		try {
			File file = new File("/Users/ryan/Downloads/words.txt");
			Scanner scnr = new Scanner(file);
			while(scnr.hasNext()) {
				manyWords.add(scnr.nextLine());
			}
			Random randGen = new Random();
			int randNum = randGen.nextInt(manyWords.size());
			chosenWord = manyWords.get(randNum);
		}
		catch (FileNotFoundException e){
			Alert fileNotFound = new Alert(AlertType.ERROR, "Error: No dictionary");
			fileNotFound.showAndWait();
			startGame = false;
		}
		catch (Exception e) {
			Alert idek = new Alert(AlertType.ERROR, "Error!!!");
			idek.showAndWait();
			startGame = false;
		}
		
		return chosenWord;
	}

	public static void main(String[] args) {
	
	launch(args);
	
	}
}