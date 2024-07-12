# MANUALE UTENTE

## Manuale Utente Book Recommender

Mattia Papaccioli 747053 CO 

Versione 1.0

### Indice
1. Installazione
    - Requisiti di sistema
    - Setup ambiente
    - Installazione programma
2. Esecuzione ed uso
    - Setup e lancio del programma
    - Uso delle funzionalita (logged in and not logged in)
    - Data set di test ??? 
3. Limiti della soluzione sviluppata
4. Sitografia/Bibliografia

### Installazione

#### Requisiti di sistema
Avere java, git ed evantualmente make installato nel proprio sistema operativo.

#### Setup ambiente
Non necessario.

#### Installazione programma
Clonare la repo da github.
```
git clone https://github.com/sbOogway/laboratorioInterdisciplinareA
```
Spostarsi nella cartella della repo.
```
cd laboratorioInterdisciplinareA
```
Compilare il package bookrecommender tramite
```
javac -d bin src/it/uninsubria/bookrecommender/*.java
```
oppure creare il jar per l'applicazione tramite
```  
jar cfm BookRecommender.jar manifest.txt -C bin .
```
infine eseguire l applicazione con java
```
java -cp bin it.uninsubria.bookrecommender.BookRecommender
```
oppure con java -jar
```
java -jar BookRecommender.jar
```
oppure eseguire l applicazione con make
```
make compile && make run
```

### Esecuzione ed uso

#### Setup e lancio del programma
Dopo aver avviato l applicazione, viene mostrato un menu tramite il quale e possibile interagire con la repository di libri, cioe visualizzare i contenuti di essa ed aggiungere recensioni e valutazioni.

#### Uso delle funzionalita
semplicente seguire cio che il programma richiede ad ogni passaggio. dove viene mostrato un elenco puntato e numerato l input richiesto e il numero della funzione corrispondente

#### Data set di test
e stato utilizzato il dataset https://www.kaggle.com/datasets/elvinrustam/books-dataset/data per la repo dei libri


### Limiti della soluzione sviluppata
non si possono usare virgole

### Sitografia/Bibliografia
