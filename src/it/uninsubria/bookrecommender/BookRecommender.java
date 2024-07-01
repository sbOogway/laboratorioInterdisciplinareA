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
		return String.format("title:\t%s\nauths:\t%s\npubl:\t%s\ncat:\t%s\ndate:\t%d\n", this.title, this.authors, this.publisher, this.category, this.year);
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

	public List<String> getData() {
		return new ArrayList<>(this.data);
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

	static ArrayList<Libro> getBooksFromCsv(String filepath) {
		ArrayList<Libro> result = new ArrayList<Libro>();
		String line = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
			// skip first line cause it has headers
			line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				Libro l = new Libro(line);
				result.add(l);
			}
		} catch (IOException e) {
			System.err.println(String.format("error reading file: %s", filepath));
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

	static void registrazione(String file, User utente){
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
	/**
	 * entry point to the application.
	 *
	 * @param args need to add user and pass auth from cli
	 */
	public static void main(String[] args) {
		List<Libro> books = Utils.getBooksFromCsv(libriDati);
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
		
		System.out.println(Utils.generateRandomChar());
		
		System.out.println(Utils.generateRandomString(10, ""));
		
		Utils.registrazione(userDati, Utils.generateUser());

		
	}
}
