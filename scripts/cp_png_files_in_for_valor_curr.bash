#!/bin/bash

SOURCE_PARENT_DIR=$SYLAR_CURR_HOME_DIR
IN=$SYLAR_IN
LOG_FILE=$LOGS_PARENT_DIR/sylar/processed_dirs.log

# Dla pojedynczego waloru!!!
# Kopiowanie plików PNG (zawierających świece z jednego dnia) - do katalogu wejściowego dla SYLAR
# + zmienianie nazwy plików, tak by nie było w nich duplikatów i by nazwa jednoznacznie opisywała zawartość

# Sprawdzenie, czy podano wystarczającą liczbę argumentów
if [ "$#" -lt 2 ]; then
    echo "Użycie: $0 <nazwa_katalogu_z_plikami_png> <plik_z_listą_dni>"
    exit 1
fi

# Argumenty
valor_dir_name="$1"   # katalog o nazwie odpowiadającej nazwie waloru (katalog ma zawierać pliki PNG)
days_list_file="$2"   # plik z listą dni - dni podane w formacie yyyymmdd

SOURCE_DIR="$SOURCE_PARENT_DIR/$valor_dir_name"

# Sprawdzenie, czy katalog $valor_dir_name istnieje
if [ ! -d $SOURCE_DIR ]; then
    echo "Błąd: Katalog \"$SOURCE_DIR\" nie istnieje."
    exit 2
fi

# Sprawdzenie, czy plik z listą dni istnieje
if [ ! -f $days_list_file ]; then
    echo "Błąd: Plik z listą dni \"$days_list_file\" nie istnieje."
    exit 3
fi

echo "Kopiowanie plików, logowane do logu $LOG_FILE"

#cp -v $SOURCE_PARENT_DIR/$1/br*.png $IN >> $LOG_FILE


# Iterate through the values in the provided days_list_file
while IFS= read -r day; do
  echo "Processing day: $day"

  # Your logic for each day can go here
  for file in "$SOURCE_DIR"/br_"$day".png; do
    if [ -f "$file" ]; then
      # Podaj nowy wzorzec nazwy plików
      base_name=$(basename "$file") # Pobierz nazwę pliku bez katalogu
      new_name="${valor_dir_name}_$base_name"

      # Skopiuj plik i zmień nazwę
      cp -v "$file" "$IN/$new_name" >> $LOG_FILE
      chown -v coder:coder "$IN/$new_name"
    else
      echo "There is no file for day: $day , skipping"
    fi
  done
done < "$days_list_file"

echo "Po skopiowaniu: zawartość katalogu $IN z uwzględnieniem tylko plików z prefixem $1"
ls -al "$IN/$valor_dir_name"_br*.png
echo "Zakończono dla katalogu $valor_dir_name"

# Zakończ skrypt
exit 0