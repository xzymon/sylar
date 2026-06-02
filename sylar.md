# SYLAR
(do przeniesienia do resources/md)

Generalnie skrypty dostarczają następujących funkcjonalności:
1. Przenoszenie plików PNG z katalogu do którego są ściągane pierwotnie - do katalogu roboczego SYLAR.
2. Przenoszenie plików CSV z katalogu w którym zostają utworzone przez SYLAR - do katalogu w którym mają być składowane.
3. Przenoszenie plików PNG i CSV do archiwum.


### Zmienne wraz z ich ustawieniem
   **root**
   ```bash
   SYLAR_PROJECT_DIR=/home/coder/git/xzymon/sylar
   SYLAR_PROJECT_SCRIPTS_DIR=/home/coder/git/xzymon/sylar/scripts
   ```
   ```bash
   SYLAR_DATA_SRC_HOME_DIR=/opt/stooq
   SYLAR_DATA_SRC_IN_DIR=/opt/stooq/in
   ```
   ```bash
   SYLAR_CURR_HOME_DIR=/opt/stooq/live/curr
   SYLAR_CRYPTO_HOME_DIR=/opt/stooq/live/crypto
   ```
   ```bash
   SYLAR_IN=/home/coder/Downloads/stooq/sylar_in
   SYLAR_CSV=/home/coder/Downloads/stooq/sylar_csv
   ```
   ```bash
   LOGS_PARENT_DIR=/srv/http/logs
   STOOQ_PNG_PARENT_DIR=/opt/stooq/live
   STOOQ_CSV_PARENT_DIR=/srv/http/resources/csv/maiordomus/to-load
   STOOQ_CSV_B15M_PARENT_DIR=/srv/http/resources/csv/maiordomus/to-load/b15m
   B15M_PARENT_DIR=/srv/http/resources/csv/maiordomus/to-load/b15m
   ```
   **user**
   ```bash
   SYLAR_PROJECT_SCRIPTS_DIR=/home/coder/git/xzymon/sylar/scripts
   SYLAR_DATA_SRC_IN_DIR=/opt/stooq/in
   IN_DIR=/opt/stooq/in
   ```

## Opis procesu przetworzenia pliku PNG

Proces dotyczy przetworzenia pliku PNG (z wykresem dla dnia danej waluty). Rezultatem przetworzenia jest plik CSV. Plik CSV jest potem ładowany przez aplikację **maiordomus** - do jej bazy danych.

### Procedura przygotowawcza dla aplikacji SYLAR
Poniższy fragment jest wariantem gdy przetwarzane są pliki dla walut (`curr` = `currencies`). Możliwa jest też 2ga opcja - dotycząca kryptowalut (`crypto`). 
Nazewnictwo katalogów jest spójne - w celu przełączenia na wariant krypto: po prostu to co w opisie poniżej ma człon `curr` należy zamienić na `crypto`.

1. **Umieść pliki PNG** w odpowiednim podkatalogu z nazwą waloru (np. `USDJPY`) znajdującym się w katalogu dla wykresów walut (`/opt/stooq/live/curr`).
2. **Wypełnij plik** `sylar_in_dates_curr` (z katalogu `/opt/stooq/in`) odpowiednimi datami - pokrywającymi się z datami obrazków do przetworzenia.
3. **Dostosuj zawartość pliku** `valors_curr` (z katalogu `/opt/stooq/in`).
4. **Uruchom poniższy skrypt** jako użytkownik `root`:
   ```bash
   $ scripts/cp_png_files_in_curr.bash
   ```
   - Polecenie kopiuje wszystkie kwalifikujące się pliki z katalogu `/opt/stooq/live/curr/<walor>/br_<data>.png` i umieszcza je w katalogu `in`, wykorzystywanym przez aplikację **sylar**.
   - Pliki otrzymują prefix nazwy katalogu, np. `USDJPY_` (gdy `<walor>` to USDJPY, aby uniknąć konfliktów nazw plików z różnych katalogów.

### Uruchomienie SYLAR  -> zasysanie dancyh z plików stooq 15m

1. Aplikacja **sylar** wczytuje pliki z katalogu `sylar_in`, a następnie:
   - Pliki przetworzone prawidłowo zostaną przeniesione do katalogu `sylar_processed`.
   - W przypadku błędu przetwarzanie zostaje zatrzymane - zwykle powodem jest plik który nie zawiera wykresu ("Brak danych"). W wypadku gdy plik zawiera prawidłowy wykres - nie powinno być błędów.  
   - Wynikowe pliki CSV są umieszczane w katalogu `sylar_csv`.
2. **Uruchom polecenie wyjściowe** (alias skryptu) jako użytkownik `root`:
   ```bash
   $ sylarout-curr
   # lub
   # $ sylarout-crypto
   ```
   - Polecenie przenosi pliki walorów z kategorii `curr` (lub odpowiednio `crypto`) o odpowiednich prefixach (np. `USDJPY_`) do katalogu `/srv/http/resources/csv/maiordomus/to-load/b15m/$NAZWA_WALORU`.
   - `sylarout-curr` jest aliasem skryptu `sylarout-curr.bash` który jest kopią pliku `mv_csv_files_out_curr.bash` (analogicznie dla `sylarout-crypto`).
3. OPCJONALNIE - ale ZALECANE. Opróżnij katalog `sylar_processed`:
   - Chociaż pełny katalog nie powoduje problemów, może z czasem zgromadzić dużą liczbę plików. Usuń zawartość katalogu przy pomocy:
     ```bash
     rm -rf processed/*
     ```
4. OPCJONALNIE - zależnie od ilości uzbieranych plików: wygenerować archiwa zawierające pliki per miesiąc. De facto generuję 4 rodzaje archiwum:
   - `stooq_png_crypto_archive_202510` : gotowiec:
   ```bash
   $ sylarch-png-crypto stooq_png_crypto_archive_202510.zip 202510 br_202510 $SYLAR_DATA_SRC_IN_DIR/valors_crypto
   ```
   - `stooq_png_curr_archive_202510` : gotowiec:
   ```bash
   $ sylarch-png-curr stooq_png_curr_archive_202510.zip 202510 br_202510 $SYLAR_DATA_SRC_IN_DIR/valors_curr
   ```
   - `stooq_csv_crypto_archive_202510` : gotowiec:
   ```bash
   $ sylarch-csv stooq_csv_crypto_archive_202510.zip 202510 202510 $SYLAR_DATA_SRC_IN_DIR/valors_crypto
   ```
   - `stooq_csv_curr_archive_202510` : gotowiec:
   ```bash
   $ sylarch-csv stooq_csv_curr_archive_202510.zip 202510 202510 $SYLAR_DATA_SRC_IN_DIR/valors_curr
   ```
   Tak wygenerowane pliki umieścić w katalogu `/opt/stooq/archive/csv` lub `/opt/stooq/archive/png`. Kopię zapasową umieścić w chmurze: Google Drive, w katalogu backupy.
---

### Procedura dla aplikacji MAIORDOMUS
1. Przygotuj pliki i umieść je w odpowiednim podkatalogu `typ/walor`, np. `b15m/USDJPY`.
2. **Pobierz pliki** (najlepiej wiele) za pomocą przeglądarki i zapisz je w katalogu roboczym.
3. **Stwórz paczkę plików:**
   - Spakuj pliki w archiwum, np. `zip` lub `tar`.
   - Zaszyfruj paczkę za pomocą klucza kryptograficznego (np. **AES**, **RSA**, **PGP**).
4. **Wypakuj archiwum** w katalogu roboczym aplikacji **maiordomus**. Powinna pojawić się paczka przetworzonych plików.
5. Aplikacja **maiordomus** przetwarza pliki:
   - Pliki przetworzone prawidłowo zostają usunięte.
   - Pliki z błędami zostają przeniesione do katalogu błędów w celu późniejszej weryfikacji.
6. Usuń paczkę archiwum po zakończeniu procesu.
7. **Utwórz kopie bezpieczeństwa archiwum** w dwóch lokalizacjach:
   - **NAS** (Network Attached Storage),
   - **Przestrzeń w chmurze**.
8. Po zakończeniu procesu usuń pliki źródłowe, które były użyte do utworzenia archiwum.

### SYLAR - cmc 30s 
Wiadomo już, że to trzeba wersjonować - bo layout w aplikacji (i zresztą chyba tak w ogóle to systemowy też) się zmienia.
Muszę ustalić punkty odcięcia - kiedy doszło do zmiany. Co najmniej 4 wersje do teraz 2026-06-02.
v1: 2025-03-12 raw/01/IMG_0515.PNG | 2025-05-30 raw/04/IMG_2209.PNG
   Cechy wersji:
   - kolorystyka - szara,
   - systemowa data/godzina - wąskie,
   - trzy kropki w układzie poziomym - pośrodku belki na wysokości systemowej daty/godziny,
   - napis: "G...we" pod datą/godziną <--- to jest chyba  najbardziej własna cecha tej wersji.
v2: 2025-05-30 raw/04/IMG_2210.PNG | 2025-10-17 raw/20/IMG_5229.PNG
   Cechy wersji:
   - kolorystyka - czarna,
   - systemowa data/godzina - wąskie,
   - napis: "G...we" pod datą/godziną - jak w v1.
   - można wstępnie zaryzykować, że to będzie to samo co v1 - tylko kolorystyka jest inna.
v3: 2025-10-19 raw/21/IMG_5233.PNG | 2025-01-20 raw/26/IMG_6917.PNG
   Cechy wersji:
   - kolorystyka - czarna,
   - systemowa data/godzina - wąskie,
   - napis: "Gotowe" pod datą/godziną
v4: 2026-02-21 raw/26/IMG_6918.PNG | 2026-04-17 raw/34/IMG_9188.PNG
   Cechy wersji:
   - kolorystyka - czarna,
   - systemowa data/godzina - odsunięte i większe,
   - pod systemową datą/godziną nie ma napisu tylko jest wielki krzyżyk w obramowaniu oraz nad wykresem po prawej lista i zegarek również w obramowaniu - i mają szerszą belkę niż do v3. 
v5: 2026-04-17 raw/34/IMG_9189.PNG | 2026-05-29 raw/39/IMG_2134.PNG ---> conajmniej do tego miejsca obowiązuje ta wersja
   Cechy wersji:
   - kolorystyka - czarna,
   - systemowa data/godzina - odsunięte i większe,
   - pod systemową datą/godziną nie ma napisu tylko jest wielki krzyżyk w obramowaniu oraz nad wykresem po prawej lista i zegarek również w obramowaniu - i mają szerszą belkę niż do v3,
   - nad panelem na prawo od wykresu jest info o saldzie konta - i ten panel zaczyna się nieco niżej niż do v4.

Jeszcze jedna istotna sprawa w kontekście przetwarzania - granica pomiędzy 2025 a 2026: gdzieś pomiędzy katalogiem 25 a 26 (akurat tak przypadkowo :D).
Prawdopodobnie ostatni plik zrzucony w 2025: 2025-12-31 raw/26/IMG_6656.PNG. Ale to nie oznacza, że to ostatni plik z danymi dotyczącymi 2025.
1 stycznia są pliki które zawierają dane z 31 grudnia 2025.
Przy okazji widać tu wyraźnie że trzeba opracować mechanizm który znając datę zrzutu - i grzebiąc w wykresie - może określić datę i godzinę danych ze zrzutu.