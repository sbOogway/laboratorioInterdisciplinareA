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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;
import java.util.Scanner;

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

	@Override
	public String toString() {
		return String.format("nome:\t\t%s\ncognome:\t%s\ncodiceFiscale:\t%s\nemail:\t\t%s\nuserid:\t\t%s\npassword:\t%s\n", this.nome, this.cognome, this.codiceFiscale, this.email, this.userid, this.password);
		
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
			line = reader.readLine();
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

	
	static List<Libro> cercaLibro(List<Libro> books, String query, queryMode mode) {
		return books.stream()
			    .filter(x -> mode.apply(x).toLowerCase().contains(query.toLowerCase()))
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

	static void registrazione(String file, User utente, List<User> users) {
		List<User> qusers = users.stream()
		     .filter(x -> x.getUserid().equals(utente.userid))
		     .collect(Collectors.toList());

		if (qusers.length() != 0) {
			System.err.println("userid not available");
			return;
		}

		csvWriter(file, utente.getData(), "");
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
	static final String libriDati       = "data/Libri.dati";
	static final String userDati        = "data/UtentiRegistrati.dati"; 
	static final String valutazioniDati = "data/ValutazioniLibri.dati"; 

	static void printMenu() {
		System.out.print("\nwhat u wanna do?\n1. look for a book\n2. view book review\n3. register\n4. login\n5. quit\nur choice: ");	
	}

	/**
	 * entry point to the application.
	 *
	 * @param args need to add user and pass auth from cli
	 */
	public static void main(String[] args) {
		// List<Libro> books = Utils.getBooksFromCsv(libriDati);
		List<Libro> books = Utils.csvReader(libriDati, Libro.class);
		// books.forEach(System.out::println);
		List<User> users = Utils.csvReader(userDati, User.class);
		// users.forEach(System.out::println);
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
		
		Utils.registrazione(userDati, Utils.generateUser());

		Scanner scanner = new Scanner(System.in);

		System.out.println("bookrecommender");
		
		while (true) {
			printMenu();
			String input = scanner.nextLine();

			switch (input) {
				// look for a book
				case "1" -> {
					System.out.print("\nselect query mode\n1. Title -> t\n2. Author -> a\n3. Author and Year -> y\nur choice: ");
					String qMode = scanner.nextLine();
					String query, year;
					List<Libro> result;

					switch (qMode)  {
						case "1", "t" -> {
							System.out.print("\nenter the title: ");
							query = scanner.nextLine();
							result = Utils.cercaLibro(books, query, queryMode.TITOLO);		
							result.forEach(System.out::println);
						}

						case "2", "a" -> {
							System.out.print("\nenter the author: ");
							query = scanner.nextLine();
							result = Utils.cercaLibro(books, query, queryMode.AUTORE);		
							result.forEach(System.out::println);

						}
						case "3", "y" -> {
							System.out.print("\nenter the author: ");
							query = scanner.nextLine();
							System.out.print("\nenter the year: ");
							year = scanner.nextLine();
							result = Utils.cercaLibro(Utils.cercaLibro(books, year, queryMode.ANNO), query, queryMode.AUTORE);		
							result.forEach(System.out::println);

						}
						default -> {System.err.println("mode not available"); break;}
					}

				}

				// view book review
				case "2" -> {
					System.out.print("\nenter book id: ");
					String ids = scanner.nextLine();
					int id;

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
					System.out.print("\nenter your name: ");
					String name          = scanner.nextLine();
					System.out.print("\nenter your surname: ");
					String surname       = scanner.nextLine();
					System.out.print("\nenter your codice fiscale: ");
					String codiceFiscale = scanner.nextLine();
					System.out.print("\nenter your email: ");
					String email         = scanner.nextLine();
					System.out.print("\nenter your userid: ");
					String userid        = scanner.nextLine();
					System.out.print("\nenter your password: ");
					String password      = scanner.nextLine();

					User user = new User(name, surname, codiceFiscale, email, userid, password);
					Utils.registrazione(userDati, user, users);
					
					System.out.println("registered succesfully!!!");
				}
				
				// login
				// make sure userid is unique -> read users in a list like the books
				case "4" -> {
					System.out.print("\nenter your userid: ");
					String userid        = scanner.nextLine();
					System.out.print("\nenter your password: ");
					String password      = scanner.nextLine();
					
				}

				// quit
				case "5", "q" -> {
					scanner.close(); 
					return;
				}
			} 

			
		}




		
	}
}
