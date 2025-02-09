#!/bin/bash

SOURCE_PARENT_DIR=$SYLAR_CURR_HOME_DIR
IN=$SYLAR_IN
LOG_FILE=$LOGS_PARENT_DIR/sylar/processed_dirs.log

# Sprawdź, czy podano argument
if [ $# -eq 0 ]; then
  echo "Nie podano argumentu. Użyj: $0 <argument>"
  exit 1
fi

# Wypisz wartość argumentu
echo "Podano argument: $1"

# Sprawdź, czy katalog istnieje
if [ -d "$SOURCE_PARENT_DIR/$1" ]; then
  echo "Kopiowanie plików, logowane do logu $LOG_FILE"

  #cp -v $SOURCE_PARENT_DIR/$1/br*.png $IN >> $LOG_FILE

  # Przejdź przez wszystkie pliki w katalogu źródłowym
  for file in "$SOURCE_PARENT_DIR/$1"/br*.png; do
    # Podaj nowy wzorzec nazwy plików - np. dodanie prefixu "kopiowane_"
    base_name=$(basename "$file") # Pobierz nazwę pliku bez katalogu
    new_name="$1_$base_name"

    # Skopiuj plik i zmień nazwę
    cp -v "$file" "$IN/$new_name"
    chown -v coder:coder "$IN/$new_name"
  done

  echo "Po skopiowaniu: zawartość katalogu $IN z uwzględnieniem tylko plików z prefixem $1"
  ls -al "$IN/$1"_br*.png
  echo "Zakończono dla katalogu $SOURCE_PARENT_DIR/$1"
else
  echo "Katalog $SOURCE_PARENT_DIR/$1 nie istnieje."
  exit 1
fi

# Zakończ skrypt
exit 0