/**
 * @author  Mattia Papaccioli 747053 CO
 * @version 1.0
 * @since   1.0
 */

package it.uninsubria.bookrecommender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.Random;
import java.util.OptionalDouble;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * A class that represents a valutazione for a book.
 * Five Valutazione put together makes a Review.
 * @see Review
 */
class Valutazione {
	/**
	 * The vote for the category.
	 */
	private int voto;
	/**
	 * The note for the category.
	 */
	private String note = new String(new char[256]);
	/**
	 * The category name.
	 */
	private String nome;
	
        /**
	 * @param nome the category being evaluated (stile, contenuto, gradevolezza, originalita, edizione).
	 * @param voto the voto for the category.
	 * @param note an eventual note for the category, max 256 characters. "NA" is the not available note.
	 */
	public Valutazione(String nome, int voto, String note) {
		this.nome = nome;
		this.voto = voto;
		this.note = note;
		// if (note.equals("NA")) {this.note = "";}
	}

	/**
	 * voto field getter.
	 * @return the category vote.
	 */
	public int getVoto() {
		return this.voto;
	}
	
	/**
	 * note field getter.
	 * @return the category note.
	 */
	public String getNote() {
		return this.note;
	}
	
	/**
	 * returns the valutazione object to string.
	 * @return Valutazione object to string.
	 */
	@Override
	public String toString() {
		return String.format("\nnome:\t%s\nvoto:\t%s\nnote:\t%s\n", this.nome, this.voto, this.note);
	}
}

/**
 * A class that represent a review for a book.
 */
class Review {
	/**
	 * For each category we have a Valutazione.
	 */
	private List<Valutazione> vals;

	/**
	 * The average of the votes for each category.
	 */
	private float votoFinale;

	/**
	 * The userid of the review writer.
	 */
	private String owner;


	/**
	 * @param l     a list of Valutazione, one for each category. @see Valutazione
	 * @param owner the userid of the Review writer.
	 */
	public Review(List<Valutazione> l, String owner) {
		this.owner      = owner;
		this.vals       = l;
		this.votoFinale = (float) vals.stream().mapToInt(v -> v.getVoto()).average().orElse(1.0); 
	}

	/**
	 * @param csvLine the csvLine read from file. we create one object for each line read in the file.
	 */
	public Review(String csvLine) {
		String[] infos = csvLine.split(","); 
		this.owner = infos[1];
		Valutazione s = new Valutazione("stile",        Integer.parseInt(infos[2]), infos[7]);
		Valutazione c = new Valutazione("contenuto",    Integer.parseInt(infos[3]), infos[8]);
		Valutazione g = new Valutazione("gradevolezza", Integer.parseInt(infos[4]), infos[9]);
		Valutazione o = new Valutazione("originalita",  Integer.parseInt(infos[5]), infos[10]);
		Valutazione e = new Valutazione("edizione",     Integer.parseInt(infos[6]), infos[11]);
		this.vals = Arrays.asList(s, c, g, o, e);
		this.votoFinale = (float) vals.stream().mapToInt(v -> v.getVoto()).average().orElse(1.0); 
	}
	
	/**
	 * returns the Review object to string.
	 * @return Valutazione object to string.
	 */
	@Override
	public String toString() {
		List<String> vs = this.vals.stream().map(Valutazione::toString).collect(Collectors.toList());
		return String.format("---review---\nowner:\t%s\n%s\nfinal:\t%.2f\n---end---\n", this.owner, vs.toString().replace(",", "").replace("[", "").replace("]", ""), this.votoFinale);

	}

	/**
	 * @return the votes and notes for each category concatenated in a list of strings, ready to be written to a file. @see Utils.csvWriter
	 */
	public List<String> toCsv() {
		List<String> votes =  this.vals.stream().map(Valutazione::getVoto).map(String::valueOf).collect(Collectors.toList());
		List<String> notes = this.vals.stream().map(Valutazione::getNote).collect(Collectors.toList());
		return Stream.concat(votes.stream(), notes.stream()).collect(Collectors.toList());
	}
}

/**
 * A class that represents a book present in the repository.
 */
class Libro {
	/**
	 * Self descriptive.
	 */
	private String title, authors, publisher, category;

	/**
	 * Year of the book publication.
	 */
	private short year;

	/**
	 * Index of the book in the csv file. it is handy because it univocally represents a book (primary key).
	 */
	private int index;

	/**
	 * List of Review objects. @see Review
	 */
	private List<Review> reviews;

	/**
	 * @param title     the title of the book.
	 * @param authors   the authors of the book.
	 * @param publisher the publisher of the book.
	 * @param category  the category of the book.
	 * @param year      the publication year of the book.  
	 */
	public Libro (String title, String authors, String publisher, String category, short year) {
		this.title     = title;
		this.authors   = authors;
		this.publisher = publisher;
		this.category  = category;
		this.year      = year;
	}

	/**
	 * In category field we replace double space with single space because of first char of each line in the csv is empty.
	 * @param csvLine the csvLine read from file. we create one object for each line read in the file.
	 */
	public Libro (String csvLine) {
		String[] infos = csvLine.split(",");
		this.index       = Integer.parseInt(infos[0]);
		this.title       = infos[1];
		this.authors     = infos[2];
		// first category char is empty
		try {
			this.category    = infos[3].replace("  ", " ").substring(1);
		} catch (Exception e) {
			this.category = "";
		}
		this.publisher   = infos[4];
		this.year        = Short.parseShort(infos[5]);

	}

	/**
	 * returns the Libro object to string.
	 * @return Libro object to string.
	 */
	@Override
	public String toString() {
		return String.format("title:\t%s\nauths:\t%s\npubl:\t%s\ncat:\t%s\ndate:\t%d\nid:\t%d\n", this.title, this.authors, this.publisher, this.category, this.year, this.index);
	}

	/**
	 * title field getter.
	 * @return the book title.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * author field getter.
	 * @return the book author.
	 */
	public String getAuthor() {
		return this.authors;
	}

	/**
	 * year field getter.
	 * @return the book publication year.
	 */
	public short getYear() {
		return this.year;
	}

}

/**
 * A class that represents a user.
 */
class User {
	/**
	 * Basic user information. Each field is self descriptive.
	 */
	private String nome, cognome, codiceFiscale, email, userid, password;

	/**
	 * Basic user information put together in a list of string to easily write it in a csv file.
	 */
	private List<String> data;

	/**
	 * List of libraries written by the user. @see Library
	 */
	private List<Library> libs;

	/**
	 * @param nome          the name of the user.
	 * @param cognome       the surname of the user.
	 * @param codiceFiscale the codiceFiscale of the user.
	 * @param email         the email of the user.
	 * @param userid        the userid of the user.
	 * @param password      the password of the user.
	 */
	public User(String nome, String cognome, String codiceFiscale, String email, String userid, String password) {
		this.nome          = nome;
		this.cognome       = cognome;
		this.codiceFiscale = codiceFiscale;
		this.email         = email;
		this.userid        = userid;
		this.password      = password;
		this.data          = List.of(nome, cognome, codiceFiscale, email, userid, password);
	}

	/**
	 * @param csvLine the csvLine we read from file containing book information.
	 */
	public User (String csvLine) {
		String[] infos = csvLine.split(",");
		this.nome          = infos[0];
		this.cognome       = infos[1];
		this.codiceFiscale = infos[2];
		this.email         = infos[3];
		this.userid        = infos[4];
		this.password      = infos[5];


	}

	/**
	 * data field getter.
	 * @return the user data, ready to be written to a csv file.
	 */
	public List<String> getData() {
		return new ArrayList<>(this.data);
	}

	/**
	 * userid field getter.
	 * @return the user userid.
	 */
	public String getUserid() {
		return this.userid;
	}

	/**
	 * password field getter.
	 * @return the user password.
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * user libraries getter.
	 * @return the user libraries.
	 */
	public List<Library> getLibs() {
		return this.libs;
	}

	/**
	 * returns the User object to string.
	 * @return User object to string.
	 */
	@Override
	public String toString() {
		return String.format("nome:\t\t%s\ncognome:\t%s\ncodiceFiscale:\t%s\nemail:\t\t%s\nuserid:\t\t%s\npassword:\t%s\n", this.nome, this.cognome, this.codiceFiscale, this.email, this.userid, this.password);
		
	}

	/**
	 * library field setter.
	 * @param l the libraries read from file that belong to the user.
	 */
	public void setLibrary(List<Library> l) {
		this.libs = l;
	}
}

/**
 * A class that represent a library. Each library is a list of books that a user puts together for various purposes.
 */
class Library {

	/**
	 * The name that the user gives to the library.
	 */
	private String nome;

	/**
	 * List of book ids present in the library.
	 */
	private List<Integer> books;

	/**
	 * @param data    the library name.
	 * @param fromCsv it makes nothing. placeholder to distinguish constructor with only csvLine.
	 */
	public Library(String data, boolean fromCsv) {
		this.nome = data;
		this.books = new ArrayList<Integer>();
	}

	/**
	 * @param csvLine constructs Library object from csvLine parsing each field conventionally. @see valutazioneDati
	 */
	public Library(String csvLine) {
		String[] infos = csvLine.split(",");
		this.nome = infos[1];
		String[] bs = Arrays.copyOfRange(infos, 2, infos.length);
		this.books = Arrays.stream(bs).map(x -> Integer.parseInt(x)).collect(Collectors.toList()); 
	}
	
	/**
	 * if a book is alraedy in the library it will not be added.
	 * @param bookId the book id to add to the library.
	 * @return whether the book was added or not.
	 */
	boolean addBook(int bookId) {
		int found = Utils.cerca(this.books, id -> id == bookId).size();
		return switch (found) {
			case 0  -> {this.books.add(bookId); yield true;}
			default -> false; 
		};
	}

	/**
	 * books field getter.
	 * @return the books id in the library.
	 */
	public List<Integer> getBooks() {
		return this.books;
	}
	
	/**
	 * name field getter.
	 * @return the name of the library.
	 */
	public String getName() {
		return this.nome;
	}

	/**
	 * returns the Library object to string.
	 * @return Library object to string.
	 */
	@Override
	public String toString() {
		return String.format("name:\t%s\nids:\t%s\n", this.nome, this.books.toString());
	}
}


/**
 * Static class with helper methods using in the main app.
 */
class Utils {
	/**
	 * Uppercase and lowercase characters, used for generating random strings.
	 */
	static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	/**
	 * Number characters.
	 */
	static final String numbers = "0123456789";

	/**
	 * Random object for generating random stuff.
	 */
	static Random random = new Random();

	/**
	 * Helper function that prints an error message to stderr if a condition is not met.
	 * @param predicate the condition evaluated.
	 * @param errMsg    the message printed to stderr in the case the condition is false.
	 * @return          the value of predicate.
	 */
	static boolean assertTrue(boolean predicate, String errMsg) {
		if (predicate) {
			return true;
		}
		System.err.println(errMsg); 
		return false;
	}

	/**
	 * Reads a file in a list of a given class.
	 * @param filepath the path of the csv file we want to read.
	 * @param type     the class we want to instatiate. it has to have a constructor with only a string representing the csvLine.
	 * @param <T>      the type of the class we want to instatiate.
	 * @return         a mutable list of class type.
	 */
	static <T> ArrayList<T> csvReader(String filepath, Class<T> type) {
		ArrayList<T> result = new ArrayList<>();
		String line = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
			// skip first line cause it has headers
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				T obj = type.getDeclaredConstructor(String.class).newInstance(line); //new Libro(line);
				result.add(obj);
			}
		} catch (IOException e) {
			System.err.println(String.format("error reading file: %s", filepath));
		} catch (Exception e) {
			System.err.println("error creating object");
		}
		return result;
	}

	/**
	 * Reads a file in a list of strings, excluding the lines that do not respect the predicate.
	 * @param filepath the path of the csv file we want to read.
	 * @param f        we want to filter each line based on the predicate.
	 * @param <T>      generic type for the predicate.
	 * @return         a list of strings read from csv line filtered base on the predicate.
	 */
	static <T> ArrayList<String> csvReaderFiltered(String filepath, Predicate<String> f) {
		ArrayList<String> fLines = new ArrayList<>();	
		try (Stream<String> lines = Files.lines(Paths.get(filepath))) {
			fLines = lines.filter(f)
			     .collect(Collectors.toCollection(ArrayList::new));
		} catch (IOException e) {
			System.err.println("error reading file");
		}
		return fLines;

	}


	static <T> List<T> cercaLibro(List<T> items, Predicate<T> f) {
		return cerca(items, f);
	}

      /** Looks for an item in a list.
        * @param items list of objects where we wanna search.  
        * @param f     predicate where we filter to look for a specific item.
	* @param <T>   the type of the class we want to instatiate for the list.
	* @return      a list where there are the items we looked for in the predicate.
	*/
	static <T> List<T> cerca(List<T> items, Predicate<T> f) {
		return items.stream()
			    .filter(f)
			    .collect(Collectors.toList());
	}

      /**
	* Writes a list of strings to a file in the csv format, recursively.
	* @param file the filepath where we write the data.
	* @param data the data we are writing to file.
	* @param lineToWrite buffer where we store the data written at each recursive call. if we need to append in front
	* something to the text we can write in it when we call the method, otherwise we leave it empty
	*/
	static void csvWriter(String file, List<String> data, String lineToWrite) {
		switch (data.size()) {
			case 0  ->  {
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
					out.write(String.format("%s\n", lineToWrite).substring(1));
					out.close();
				} catch (IOException e) {
					System.err.println(String.format("error writing to file: %s", file));
				}

				return;
			}
			default -> {
				String field = data.remove(0);
				csvWriter(file, data, String.format("%s,%s", lineToWrite, field));
			}
		}
	}

	static void visualizzaLibro(Libro book) {
				
	}

	static void inserisciValutazioneLibro() {

	}

      /**
	* Writes new user to a file after checking that a user with the same name already exists.
	* @param file the filepath where we write the new user, "data/UtentiRegistrati.dati".
	* @param utente the new user we are writing to file.
	* @param users the already registered users.
	* @return whether the registrazione was succesfull or not.
	*/
	static boolean registrazione(String file, User utente, List<User> users) {
		List<User> qusers = cerca(users, user -> user.getUserid().equals(utente.getUserid()));

		if (qusers.size() != 0) {
			System.err.println(String.format("\nuserid %s not available", utente.getUserid()));
			return false;
		}

		csvWriter(file, utente.getData(), "");
		return true;
	}

      /**
	* Generates dummy character for debugging purposes.
	* @return random alphabet character.
	*/
	static String generateRandomChar() {
		return String.valueOf(characters.substring(random.nextInt(characters.length())).charAt(0));
	}

      /**
	* Generates dummy integer from interval for debugging purposes.
	* @param lower lower bound.
	* @param upper upper bound.
	* @return integer in the interval.
	*/
	static int generateFromInterval(int lower, int upper) {
		return random.nextInt(upper - lower + 1) + lower;
	}

      /**
	* Creates dummy string for debugging purposes.
	* @param length length of the generated string.
	* @param out buffer where we pass the string recursively.
	* @return random generated string.
	*/
	static String generateRandomString(int length, String out) {
		return switch (length) {
			case 0  -> out;
			default -> generateRandomString(length-1, String.format("%s%s", out, generateRandomChar()));
		};
	}

      /**
	* Creates dummy user for debugging purposes.
	* @return dummy user.
	*/
	static User generateUser() {
		String nome          = generateRandomString(generateFromInterval(5, 12), "");
		String cognome       = generateRandomString(generateFromInterval(5, 12), "");
		String codiceFiscale = String.format("%s%s%s", nome.substring(0,3), cognome.substring(0,3), generateRandomString(10, ""));
		String email         = String.format("%s@%s.%s", nome, cognome, generateRandomString(3, ""));
		String userid        = generateRandomString(generateFromInterval(5, 12), "");
		String password      = "password";
		return new User(nome, cognome, codiceFiscale, email, userid, password);


	}

}


/**
 * Main class that handles the execution of the application.
 */
class BookRecommender {
	/**
	 * Whether an user is currently logged in the app.
	 */
	static boolean isUserLogged = false;

	/**
	 * Currently active user in the app.
	 */
	static User activeUser;

	/**
	 * The csv file path of the books data.
	 * csvFormat = index,Title,Authors,Category,Publisher,Year 
	 */
	static final String libriDati       = "data/Libri.dati";

	/**
	 * The csv file path of the users data.
	 * csvFormat = nome,cognome,codiceFiscale,email,userid,password 
	 */
	static final String userDati        = "data/UtentiRegistrati.dati"; 

	/**
	 * The csv file path of the ValutazioniLibri data.
	 * csvFormat = bookid,user,votoStile,votoContenuto,votoGradevolezza,votoOriginalita,votoEdizione,notaStile,notaContenuto,notaGradevolezza,notaOriginalita,notaEdizione
	 */
	static final String valutazioniDati = "data/ValutazioniLibri.dati"; 

	/**
	 * The csv file path of the books recommandation.
	 * csvFormat = bookid,userid,id1,id2,id3
	 */
	static final String consigliDati    = "data/ConsigliLibri.dati"; 

	/**
	 * The csv file path of the Lbreria data.
	 * csvFormat = userid,nomeLibreria,(listOfBookIdsSeparatedByComma) 
	 */
	static final String librerieDati    = "data/Librerie.dati"; 

	/**
	 * Regex that ensures there are only alphabet characters in a string.
	 */
	static final Pattern namePattern    = Pattern.compile("^[a-zA-Z]+$");

	/**
	 * Regex that ensures that a string is a valid email in the format, example@info.com
	 */
	static final Pattern emailPattern   = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

	/**
	 * Regex that ensures there is no comma in a string, is used in every input to not break csv reading and writing.
	 */
	static final Pattern noCommaPattern = Pattern.compile("^[^,]*$");

	/**
	 * String containing the main menu of the application.
	 */
	static String menu                  = "\n%s\nwhat u wanna do?\n1. look for a book\n2. view book review\n3. register\n4. login\n5. quit\n6. create a new library\n7. insert book review\n8. insert recommandation for book\n9. view your libraries\n0. logout\nur choice: ";

	/**
	 * String containing the not logged in prompt.
	 */
	static final String noLoggedInMenu  = "NOT LOGGED IN";

	/**
	 * String used to format the menu prompt.
	 */
	static String prompt;

	/**
	 * handle string input.
	 * @param msg the message containing what we wanna receive as input from the user.
	 * @param s   scanner object containing stdin input.
	 * @return    the string written by the user. 
	 */
	static String handleInput(String msg, Scanner s) {
		System.out.print(msg);
		return s.nextLine();
	}
	
	/**
	 * handle integer input.
	 * @param msg the message containing what we wanna receive as input from the user.
	 * @param s   scanner object containing stdin input.
	 * @return    the integer written by the user.
	 */
	static int handleIntInput(String msg, Scanner s) {
		int intg = -1;
		String ids = handleInput(msg, s);

		try {
			intg = Integer.parseInt(ids);
		} catch (java.lang.NumberFormatException e) {
			System.err.println("invalid integer");
		}
		return intg;
	}

	/**
	 * handle Valutazione input.
	 * @param name the name of the valutazione we want to read. @see Valutazione
	 * @param s    scanner object containing stdin input.
	 * @return     the Valutazione written by the user .
	 */
	static Valutazione handleValutazione(String name, Scanner s) {
		int voto; String note;
		do { voto = handleIntInput(String.format("insert voto for %s", name), s);}
		while (voto <= 0 || voto > 5);
		do { note = handleInput(String.format("insert note for %s", name), s);}
		while (!Utils.assertTrue(noCommaPattern.matcher(note).matches(), "dont use commas in the note"));
		System.out.println(note);
		if (note.equals("") || note.isEmpty()) {note="NA";}
		return new Valutazione(name.replace("\t", "").replace(":", ""), voto, note);
	}
	
	// static String handleInputCondition(String msg, Scanner s, boolean condition) {
		// String in;
		// do {
			// System.out.print(msg);
			// in = s.nextLine();
		// } while (condition);
		// return in;
	// }

	/**
	 * entry point to the application.
	 *
	 * @param args need to add user and pass auth from cli
	 */
	public static void main(String[] args) {
		// List<Libro> books = Utils.getBooksFromCsv(libriDati);
		List<Libro>  books = Utils.csvReader(libriDati,    Libro.class);
		List<User>   users = Utils.csvReader(userDati,     User.class);
		// List<String> libs  = Utils.csvReaderFiltered(librerieDati, xl -> xl.split(",")[0].equals("pollo"));
		// List<Library> lls  = libs.stream().map(Library::new).collect(Collectors.toList());

		// lls.forEach(System.out::println);
		// System.exit(10);
		// List<Libro> titoli = books.stream()
			// .map(Libro::getTitle)
			// .filter(str -> str.getTitle().contains("Quick"))
			// .collect(Collectors.toList()); 
		// titoli.forEach(System.out::println);
		// System.out.println(Utils.search(titoli, "Quick"));
		// List<Libro> titoli = Utils.cercaLibro(books, "quick", queryMode.TITOLO);
		// titoli.forEach(System.out::println);
		// List<Libro> autori = Utils.cercaLibro(Utils.cercaLibro(books, "Shakespeare", queryMode.AUTORE), "1962", queryMode.ANNO);
		// autori.forEach(System.out::println);
		// List<Libro> anno = Utils.cercaLibro(books, "1999", queryMode.ANNO);
		// anno.forEach(System.out::println);
		//
		// List<String> fruits = List.of("Apple", "Banana", "Cherry");
		// List<String> mfruits = new ArrayList<>(fruits);
		// Utils.csvWriter("data/test.csv", mfruits, "");
		
		// System.out.println(Utils.generateRandomChar());
		
		// System.out.println(Utils.generateRandomString(10, ""));
		
		// Utils.registrazione(userDati, Utils.generateUser(), users);

		Scanner scanner = new Scanner(System.in);

		System.out.println("bookrecommender");
		// strategy pattern
		prompt = noLoggedInMenu;
		
		while (true) {
			String input = handleInput(String.format(menu, prompt), scanner);

			switch (input) {
				// look for a book
				case "1" -> {
					String qMode = handleInput("\nselect query mode\n1. Title -> t\n2. Author -> a\n3. Author and Year -> y\nur choice: ", scanner);
					String query, year;
					List<Libro> result, yearq;

					switch (qMode)  {
						case "1", "t" -> {
							query = handleInput("\nenter the title: ", scanner);
							result = Utils.cercaLibro(books, book -> book.getTitle().toLowerCase().contains(query.toLowerCase()));
							// result = Utils.cercaLibro(books, query, queryMode.TITOLO);	
							result.forEach(System.out::println);
						}

						case "2", "a" -> {
							query = handleInput("\nenter the author: ", scanner);
							result = Utils.cercaLibro(books, book -> book.getAuthor().toLowerCase().contains(query.toLowerCase()));	
							result.forEach(System.out::println);

						}
						case "3", "y" -> {
							query = handleInput("\nenter the author: ", scanner);
							year  = handleInput("\nenter the year: ", scanner);
							yearq = Utils.cercaLibro(books, book -> String.valueOf(book.getYear()).equals(year));
							result = Utils.cercaLibro(yearq, book -> book.getAuthor().toLowerCase().contains(query.toLowerCase()));		
							result.forEach(System.out::println);

						}
						default -> {System.err.println("mode not available"); break;}
					}

				}

				// view book review
				// need to add book recommandations and average of each category and final vote
				// need to add a prospect for recommandations
				case "2" -> {
					int id = handleIntInput("\nenter book id: ", scanner);
					// String ids = handleInput("\nenter book id: ", scanner);

					// try {
						// id = Integer.parseInt(ids);

					// } catch (java.lang.NumberFormatException e) {
						// System.err.println("invalid id");
						// break;
					// }

					try {
						System.out.println("\n" + books.get(id));

					} catch (java.lang.IndexOutOfBoundsException e) {
						System.err.println("invalid id");
						break;
					}

					List<String> rs = Utils.csvReaderFiltered(valutazioniDati, x -> x.split(",")[0].equals(String.valueOf(id)));
					List<Review> rrs = rs.stream().map(Review::new).collect(Collectors.toList());
					
					rrs.forEach(System.out::println);
						




				}

				// register
				case "3" -> {
					String name, surname, codiceFiscale, email, userid, password;
					System.out.println();

					do { name = handleInput("enter your name:\t\t", scanner);} 
					while (!Utils.assertTrue(namePattern.matcher(name).matches() && noCommaPattern.matcher(name).matches(), "dont use numbers or commas in your name"));
					
					do { surname = handleInput("enter your surname:\t\t", scanner);}
					while (!Utils.assertTrue(namePattern.matcher(surname).matches() && noCommaPattern.matcher(surname).matches(), "dont use numbers or commas in your surname"));

					do { codiceFiscale = handleInput("enter your codice fiscale:\t", scanner);}
					while (!Utils.assertTrue(noCommaPattern.matcher(codiceFiscale).matches(), "dont use commas in your codiceFiscale"));

					do { email = handleInput("enter your email:\t\t", scanner); }
					while (!Utils.assertTrue(emailPattern.matcher(email).matches(), "email not valid"));
					
					do { userid = handleInput("enter your userid:\t\t", scanner);}
					while (!Utils.assertTrue(noCommaPattern.matcher(userid).matches(), "dont use commas in your userid"));

					do { password = handleInput("enter your password:\t\t", scanner);}
					while (!Utils.assertTrue(noCommaPattern.matcher(password).matches(), "dont use commas in your password"));

					User user = new User(name, surname, codiceFiscale, email, userid, password);
					boolean succ = Utils.registrazione(userDati, user, users);
					// System.out.println(succ);
					if (!succ) {
						break;
					}
					
					System.out.println(String.format("\n%s registered succesfully!!!", user.getUserid()));
					users = Utils.csvReader(userDati, User.class);
				}
				
				// login
				// make sure userid is unique -> read users in a list like the books
				case "4" -> {
					List<User> found; String password;
					System.out.println();
					
					do {
						String userid = handleInput("enter your userid:\t", scanner);
						found  = Utils.cerca(users, user -> user.getUserid().equals(userid));
					} while (!Utils.assertTrue(found.size() == 1, "userid not found"));
					activeUser = found.get(0);
					
					do { password = handleInput("enter your password:\t", scanner);}
					while ( !Utils.assertTrue(activeUser.getPassword().equals(password), "wrong password"));

					System.out.println(String.format("\nlogin as %s succesfull!!!", activeUser.getUserid()));
					isUserLogged = true;

					prompt = String.format("\nLOGGED IN AS %s", activeUser.getUserid()); 

					List<Library> llibs  = Utils.csvReaderFiltered(librerieDati, xl -> xl.split(",")[0].equals(activeUser.getUserid()))
						.stream()
						.map(Library::new)
						.collect(Collectors.toList());

					activeUser.setLibrary(llibs);
					
				}

				// quit
				case "5", "q" -> {
					scanner.close(); 
					return;
				}
				
				// create new library
				// ensure that each user cannot have two libraries with the same name -> done
				// need to wrap this in registraLibreria()
				case "6" -> {
					System.out.println();
					if (!Utils.assertTrue(isUserLogged, "u need to login in order to create a new library")) {
						break;
					}
					String name, id; int idTmp;

					do {name = handleInput("enter library name:\t\t", scanner);}
					while (!Utils.assertTrue(noCommaPattern.matcher(name).matches(), "dont use commas"));

					final String fname = name;

					List <String> test = Utils.csvReaderFiltered(librerieDati, x -> x.split(",")[0].equals(activeUser.getUserid())); //activeUser.getLibs().stream().collect(Collectors.toList());

					System.out.println(test);
					// test.forEach(System.out::println);
					// if (!Utils.assertTrue(activeUser.getLibs().stream().map(Library::getName).filter(ff -> ff.equals(fname)).collect(Collectors.toList()).size() == 0, "u already have a library with this name")) {

					if (!Utils.assertTrue(test.stream().map(Library::new).map(Library::getName).filter(ff -> ff.equals(fname)).collect(Collectors.toList()).size() == 0, "u already have a library with this name")) {
						break;
					}

					Library l = new Library(name, false);	

					while (true) {
						id = handleInput("enter book id or type end:\t", scanner);
						if (id.equals("end")) {
							break;
						}
						try {
							idTmp = Integer.parseInt(id);
							
						} catch (java.lang.NumberFormatException e) {
							System.err.println("invalid book id");
							continue;
						}

						if (idTmp > books.size() || idTmp < 0) {
							System.err.println("invalid book id");
							continue;
						}

						l.addBook(idTmp);
					}

					List<String> lStr = l.getBooks().stream()
						                        .map(idi -> String.valueOf(idi))
									.collect(Collectors.toList());

					Utils.csvWriter(librerieDati, lStr, String.format(",%s,%s", activeUser.getUserid(), l.getName()));
					
					// wrap this in a function also in 4 switch menu
					List<Library> llibs  = Utils.csvReaderFiltered(librerieDati, xl -> xl.split(",")[0].equals(activeUser.getUserid()))
						.stream()
						.map(Library::new)
						.collect(Collectors.toList());

					activeUser.setLibrary(llibs);
				}

				// insert book review
				case "7" -> {
					System.out.println();
					if (!Utils.assertTrue(isUserLogged, "u need to login in order to insert book review")) {
						break;
					}

					int id = handleIntInput("\nenter book id: ", scanner);

					if (id > books.size() || id < 0) {
						System.err.println("invalid book id");
						break;
					}

					Valutazione stile        = handleValutazione("stile:\t\t",      scanner);
					Valutazione contenuto    = handleValutazione("contenuto:\t",    scanner);
					Valutazione gradevolezza = handleValutazione("gradevolezza:\t", scanner);
					Valutazione originalita  = handleValutazione("originalita:\t",  scanner);
					Valutazione edizione     = handleValutazione("edizione:\t",     scanner);
					
					List<Valutazione> v = Arrays.asList(stile, contenuto, gradevolezza, originalita, edizione);
					Review r = new Review(v, activeUser.getUserid());

					System.out.println(r);

					Utils.csvWriter(valutazioniDati, r.toCsv(), String.format(",%d,%s", id, activeUser.getUserid()));


				}
				
				// insert recommandation for book 
				// need to handle better user input and to link this to command number 2
				case "8" -> {
					System.out.println();
					if (!Utils.assertTrue(isUserLogged, "u need to login in order to insert recommandation for book")) {
						break;
					}
					
					String id = String.valueOf(handleIntInput("enter book id u want to recommend from:\t", scanner));
					String i1 = String.valueOf(handleIntInput("enter book id u want to recommend to:\t", scanner));
					String i2 = String.valueOf(handleIntInput("enter book id u want to recommend to:\t", scanner));
					String i3 = String.valueOf(handleIntInput("enter book id u want to recommend to:\t", scanner));

					List<String> rec = new ArrayList<>(Arrays.asList(i1, i2, i3));

					
					
					System.out.println(rec);


					Utils.csvWriter(consigliDati, rec, String.format(",%s,%s", id, activeUser.getUserid()));


				}

				// view ur libraries
				case "9" -> {
					System.out.println();
					if (!Utils.assertTrue(isUserLogged, "u need to login in order to view ur libraries")) {
						break;
					}
					activeUser.getLibs().forEach(System.out::println);
				}
				
				// logout
				case "0" -> {
					System.out.println();
					if (!Utils.assertTrue(isUserLogged, "u need to login in order to log out")) {
						break;
					}

					activeUser = null;
					isUserLogged = false;
					prompt = noLoggedInMenu;

				}
				
				default -> {
					System.err.println("\nmode not available");
				}
			} 
		}
	}
}
