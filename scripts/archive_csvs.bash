#!/bin/bash

# Sprawdzenie, czy podano wystarczającą liczbę argumentów
if [ "$#" -lt 4 ]; then
    echo "Użycie: $0 <nazwa_archiwum.zip> <nazwa_katalogu_roboczego> <prefix> <plik_z_listą_katalogów>"
    # Przykładowo:
    # $ bash $SYLAR_PROJECT_SCRIPTS_DIR/archive_csvs.bash stooq_csv_archive_202502.zip 202502 202502 $SYLAR_DATA_SRC_IN_DIR/valors_curr
    exit 1
fi

# Stałe
DATA_KIND=b15m
DIRS_PARENT_PATH=$STOOQ_CSV_PARENT_DIR/$DATA_KIND


# Argumenty
output_zip="$1"       # Nazwa archiwum - np. stooq_csv_archive_202502.zip
work_dir="$2"         # Nazwa dla katalogu roboczego, do którego będą kopiowane pliki - np. 202502 (yyyymm)
prefix="$3"           # Prefiks plików - np. 202502
catalog_list_file="$4" # Plik zawierający listę katalogów

# Sprawdzenie, czy plik zawierający listę katalogów istnieje
if [ ! -f "$catalog_list_file" ]; then
    echo "Błąd: Plik z listą katalogów \"$catalog_list_file\" nie istnieje."
    exit 2
fi

# Upewnij się, że archiwum nie istnieje, aby uniknąć nadpisania
if [ -f "$output_zip" ]; then
    echo "Plik $output_zip już istnieje. Usuń go lub zmień nazwę."
    exit 3
fi

# Tworzymy tymczasowy katalog na pliki do spakowania
mkdir $work_dir

# Loop przez linie w pliku zawierającym listę katalogów
while IFS= read -r directory; do
    # Pomijamy puste linie i komentarze w pliku
    [[ -z "$DIRS_PARENT_PATH/$directory" || "$DIRS_PARENT_PATH/$directory" == \#* ]] && continue

    if [ -d "$DIRS_PARENT_PATH/$directory" ]; then
        # Znajdź pliki z prefiksem i skopiuj je do katalogu tymczasowego
        mkdir $work_dir/$directory
        MASK="${directory}_${DATA_KIND}_${prefix}*"
        echo ${MASK}
        find "$DIRS_PARENT_PATH/$directory" -type f -name ${MASK} -exec cp -v -- "{}" "$work_dir/$directory/" \;
        if zip -v -r "$output_zip" "$work_dir" > /dev/null; then
          echo "Dodano katalog $directory do archiwum $output_zip"
        else
              echo "Błąd podczas tworzenia archiwum."
        fi
    else
        echo "Ostrzeżenie: Katalog \"$DIRS_PARENT_PATH/$directory\" nie istnieje. Pomijam..."
    fi
done < "$catalog_list_file"

# Usuwamy katalog tymczasowy
rm -rf "$work_dir"

# sprawdzenie czy zgadza sie ilosc plików:
# 1. rozpakuj utworzone archiwum:
#    $ unzip <nazwa>
# 2. wejdz do katalogu - jego nazwa odpowiada $work_dir
# 3. wykonaj polecenie
#    $ ls -1 * | wc -l
# 4. uzyskany wynik = ((avg_subdir_count + 2) * N) - 1
#    gdzie:
#    avg_subdir_count - średnia ilosc plikow przypadająca na katalog
#    N - ilość katalogów odpowiadająca ilości walorów
# 5. Mając powyższą wiedzę policz z tego avg_subdir_count - jezeli odpowiada ilości plików jaka powinna przypadać na katalog - to jest OK.
#
# Wyjaśnienie - dlaczego we wzorze w 4) jest +2 a potem -1
#    bo ls -1 * przed każdym kolejnym katalogiem wyswietla jego nazwę (co powoduje +1)
#    i pomiędzy wyswietlanymi katalogami dodaje linię odstępu (co summa summarum daje wkład +2 = 1 + 1)
#    przed pierwszą linią zawierającą nazwę pierwszego katalogu - nie ma pustej linii - zatem należy wykonać -1
#
# Przykładowo:
#    $ ls -1 * | wc -l
#    -> 3656
#    3656 = ((avg_subdir_count + 2) * N) - 1
#    N = 53 # (tyle jest katalogów)
# z czego:
#    avg_subdir_count = 67    - i tyle właśnie powinno być plików w ramach 2024
# a sama ilość plików to:
#    N * avg_subdir_count = 53 * 67 = 3551
