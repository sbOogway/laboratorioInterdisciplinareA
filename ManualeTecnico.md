# MANUALE TECNICO

## Manuale Tecnico Book Recommender

Mattia Papaccioli 747053 CO

Versione 1.0

### Indice
1. Report tecnico della soluzione sviluppata (scelte di architettura, strutture dati, algoritmi utilizzati)
2. Limiti della soluzione sviluppata

### Report tecnico 
#### Scelte di architettura
Le principali funzioni per modificare i file sono tutte contenute nella classe Utils, i cui metodi sono tutti statici. Ho effettuato questa scelta per poter chiamare le funzioni nel main senza dover instanziare una classe e per mantenere il codice piu pulito.
Ho cercato di utilizzare il piu possibile java stream per effettuare operazioni di ricerca e per modificare liste in modo da rendere il codice piu coinciso possibile.
Ho utilizzato regex per assicurare che l input dell utente sia coerente con cio che viene richiesto.

Strategy pattern in prompt menu.

### Limiti della soluzione sviluppata
non si possono utilizzare virgole. questa scelta e stata presa per semplificare notevolmente lo sviluppo tramite file csv. avrei potuto usare una libreria ma ho preferito farlo in questo modo per semplicita. alla fine non e un vincolo troppo restringente.
