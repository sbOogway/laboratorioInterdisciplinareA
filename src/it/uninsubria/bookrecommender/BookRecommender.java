/**
 * @author Mattia Papaccioli 747053 CO
 * @version 1.0
 * @since 1.0
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
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// λ lambda
// @FunctionalInterface
// interface λ<T, R> {
	// R apply(T t);
// }

class Libro {
	private String title, authors, publisher, category;
	private short year;
	private int index;

	public Libro (String title, String authors, String publisher, String category, short year) {
		this.title     = title;
		this.authors   = authors;
		this.publisher = publisher;
		this.category  = category;
		this.year      = year;
	}

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

	@Override
	public String toString() {
		return String.format("title:\t%s\nauths:\t%s\npubl:\t%s\ncat:\t%s\ndate:\t%d\nid:\t%d\n", this.title, this.authors, this.publisher, this.category, this.year, this.index);
	}

	public String getTitle() {
		return this.title;
	}

	public String getAuthor() {
		return this.authors;
	}

	public short getYear() {
		return this.year;
	}

}

class User {
	private String nome, cognome, codiceFiscale, email, userid, password;
	private List<String> data;
	private List<Library> libs;

	public User(String nome, String cognome, String codiceFiscale, String email, String userid, String password) {
		this.nome          = nome;
		this.cognome       = cognome;
		this.codiceFiscale = codiceFiscale;
		this.email         = email;
		this.userid        = userid;
		this.password      = password;
		this.data          = List.of(nome, cognome, codiceFiscale, email, userid, password);
	}

	public User (String csvLine) {
		String[] infos = csvLine.split(",");
		this.nome          = infos[0];
		this.cognome       = infos[1];
		this.codiceFiscale = infos[2];
		this.email         = infos[3];
		this.userid        = infos[4];
		this.password      = infos[5];


	}

	public List<String> getData() {
		return new ArrayList<>(this.data);
	}

	public String getUserid() {
		return this.userid;
	}

	public String getPassword() {
		return this.password;
	}

	public List<Library> getLibs() {
		return this.libs;
	}

	@Override
	public String toString() {
		return String.format("nome:\t\t%s\ncognome:\t%s\ncodiceFiscale:\t%s\nemail:\t\t%s\nuserid:\t\t%s\npassword:\t%s\n", this.nome, this.cognome, this.codiceFiscale, this.email, this.userid, this.password);
		
	}

	public void setLibrary(List<Library> l) {
		this.libs = l;
	}
}

class Library {
	private String nome;
	private List<Integer> books;

	public Library(String data, boolean fromCsv) {
		this.nome = data;
		this.books = new ArrayList<Integer>();
	}

	public Library(String csvLine) {
		String[] infos = csvLine.split(",");
		this.nome = infos[1];
		String[] bs = Arrays.copyOfRange(infos, 2, infos.length);
		this.books = Arrays.stream(bs).map(x -> Integer.parseInt(x)).collect(Collectors.toList()); 
	}

	boolean addBook(int bookId) {
		int found = Utils.cerca(this.books, id -> id == bookId).size();
		return switch (found) {
			case 0  -> {this.books.add(bookId); yield true;}
			default -> false; 
		};
	}

	public List<Integer> getBooks() {
		return this.books;
	}
	
	public String getName() {
		return this.nome;
	}

	@Override
	public String toString() {
		return String.format("name:\t%s\nids:\t%s\n", this.nome, this.books.toString());
	}
}

enum queryMode {
	TITOLO {
		@Override 
		String apply(Libro book) {
			return book.getTitle();
		}
	},

	AUTORE {
		@Override
		String apply(Libro book) {
			return book.getAuthor();
		}
	}, 

	ANNO {
		@Override 
		String apply(Libro book) {
			return String.format("%d", book.getYear());
		}
	};

	abstract String apply(Libro book);
}

class Utils {
	static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static final String numbers = "0123456789";
	static Random random = new Random();

	static boolean assertTrue(boolean predicate, String errMsg) {
		if (predicate) {
			return true;
		}
		System.err.println(errMsg); 
		return false;
	}
	// static ArrayList<Libro> getBooksFromCsv(String filepath) {
		// ArrayList<Libro> result = new ArrayList<Libro>();
		// String line = null;
		// try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
			// skip first line cause it has headers
			// line = reader.readLine();
			// while ((line = reader.readLine()) != null) {
				// Libro l = new Libro(line);
				// result.add(l);
			// }
		// } catch (IOException e) {
			// System.err.println(String.format("error reading file: %s", filepath));
		// }
		// return result;
	// }

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

	
	static List<Libro> cercaLibro(List<Libro> books, String query, queryMode mode) {
		return books.stream()
			    .filter(x -> mode.apply(x).toLowerCase().contains(query.toLowerCase()))
			    .collect(Collectors.toList());
	}
	
	static <T> List<T> cerca(List<T> items, Predicate<T> f) {
		return items.stream()
			    .filter(f)
			    .collect(Collectors.toList());

	}

	static void csvWriter(String filepath, List<String> data, String lineToWrite) {
		switch (data.size()) {
			case 0  ->  {
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter(filepath, true));
					out.write(String.format("%s\n", lineToWrite).substring(1));
					out.close();
				} catch (IOException e) {
					System.err.println(String.format("error writing to file: %s", filepath));
				}

				return;
			}
			default -> {
				String field = data.remove(0);
				csvWriter(filepath, data, String.format("%s,%s", lineToWrite, field));
			}
		}
	}

	static void visualizzaLibro(Libro book) {
				
	}

	static void inserisciValutazioneLibro() {

	}

	static boolean registrazione(String file, User utente, List<User> users) {
		List<User> qusers = cerca(users, user -> user.getUserid().equals(utente.getUserid()));

		if (qusers.size() != 0) {
			System.err.println(String.format("\nuserid %s not available", utente.getUserid()));
			return false;
		}

		csvWriter(file, utente.getData(), "");
		return true;
	}

	static void generateReview(Libro book) {

	}

	static String generateRandomChar() {
		return String.valueOf(characters.substring(random.nextInt(characters.length())).charAt(0));
	}

	static int generateFromInterval(int lower, int upper) {
		return random.nextInt(upper - lower + 1) + lower;
	}

	static String generateRandomString(int length, String out) {
		return switch (length) {
			case 0  -> out;
			default -> generateRandomString(length-1, String.format("%s%s", out, generateRandomChar()));
		};
	}

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

class BookRecommender {
	static boolean isUserLogged = false;
	static User activeUser;

	static final String libriDati       = "data/Libri.dati";
	static final String userDati        = "data/UtentiRegistrati.dati"; 
	static final String valutazioniDati = "data/ValutazioniLibri.dati"; 
	static final String librerieDati    = "data/Librerie.dati"; 

	static final Pattern namePattern    = Pattern.compile("^[a-zA-Z]+$");
	static final Pattern emailPattern   = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
	static final Pattern noCommaPattern = Pattern.compile("^[^,]*$");

	static String menu                  = "\n%s\nwhat u wanna do?\n1. look for a book\n2. view book review\n3. register\n4. login\n5. quit\n6. create a new library\n7. insert book review\n8. insert recommandation for book\n9. view your libraries\nur choice: ";

	static final String noLoggedInMenu  = "NOT LOGGED IN";
	static String prompt;


	static String handleInput(String msg, Scanner s) {
		System.out.print(msg);
		return s.nextLine();
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
		List<String> libs  = Utils.csvReaderFiltered(librerieDati, xl -> xl.split(",")[0].equals("pollo"));
		List<Library> lls  = libs.stream().map(Library::new).collect(Collectors.toList());

		lls.forEach(System.out::println);
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
		prompt = noLoggedInMenu;
		
		while (true) {
			String input = handleInput(String.format(menu, prompt), scanner);

			switch (input) {
				// look for a book
				case "1" -> {
					String qMode = handleInput("\nselect query mode\n1. Title -> t\n2. Author -> a\n3. Author and Year -> y\nur choice: ", scanner);
					String query, year;
					List<Libro> result;

					switch (qMode)  {
						case "1", "t" -> {
							query = handleInput("\nenter the title: ", scanner);
							result = Utils.cerca(books, book -> book.getTitle().toLowerCase().contains(query.toLowerCase()));
							// result = Utils.cercaLibro(books, query, queryMode.TITOLO);	
							result.forEach(System.out::println);
						}

						case "2", "a" -> {
							query = handleInput("\nenter the author: ", scanner);
							result = Utils.cercaLibro(books, query, queryMode.AUTORE);	
							result.forEach(System.out::println);

						}
						case "3", "y" -> {
							query = handleInput("\nenter the author: ", scanner);
							year  = handleInput("\nenter the year: ", scanner);
							result = Utils.cercaLibro(Utils.cercaLibro(books, year, queryMode.ANNO), query, queryMode.AUTORE);		
							result.forEach(System.out::println);

						}
						default -> {System.err.println("mode not available"); break;}
					}

				}

				// view book review
				case "2" -> {
					int id;
					String ids = handleInput("\nenter book id: ", scanner);

					try {
						id = Integer.parseInt(ids);

					} catch (java.lang.NumberFormatException e) {
						System.err.println("invalid id");
						break;
					}

					try {
						System.out.println("\n" + books.get(id));

					} catch (java.lang.IndexOutOfBoundsException e) {
						System.err.println("invalid id");
						break;
					}




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
				// ensure that each user cannot have two libraries with the same name
				case "6" -> {
					System.out.println();
					Utils.assertTrue(isUserLogged, "u need to login in order to create a new library");
					String name, id; int idTmp;

					do {name = handleInput("enter library name:\t\t", scanner);}
					while (!Utils.assertTrue(noCommaPattern.matcher(name).matches(), "dont use commas"));

					final String fname = name;

					if (!Utils.assertTrue(activeUser.getLibs().stream().filter(ff -> ff.equals(fname)).collect(Collectors.toList()).size() != 0, "u already have a library with this name")) {
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
				}

				// insert book review
				case "7" -> {
					System.out.println();
					Utils.assertTrue(isUserLogged, "u need to login in order to insert book review");
				}
				
				// insert recommandation for book 
				case "8" -> {
					System.out.println();
					Utils.assertTrue(isUserLogged, "u need to login in order to insert recommandation for book");
				}

				// view ur libraries
				case "9" -> {
					System.out.println();
					Utils.assertTrue(isUserLogged, "u need to login in order to view ur libraries");
					activeUser.getLibs().forEach(System.out::println);
				}
				
				default -> {
					System.err.println("\nmode not available");
				}






			} 

			
		}




		
	}
}
