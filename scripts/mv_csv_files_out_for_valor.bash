#!/bin/bash

DEST_PARENT_DIR=$B15M_PARENT_DIR
OUT=$SYLAR_CSV
LOG_FILE=$LOGS_PARENT_DIR/sylar/csv_files.log

# Przenoszenie plików CSV wygenerowanych przez sylar - do katalogu gdzie będą składane dla danego waloru
# Argumentem jest katalog o nazwie odpowiadającej nazwie waloru
# Sprawdź, czy podano argument
if [ $# -eq 0 ]; then
  echo "Nie podano argumentu. Użyj: $0 <nazwa-waloru>"
  exit 1
fi

# Wypisz wartość argumentu
echo "Podano argument: $1"

if [ ! -d "$DEST_PARENT_DIR/$1" ]; then
  echo "Katalog $DEST_PARENT_DIR/$1 nie istnieje."
  echo "Tworzenie katalogu $DEST_PARENT_DIR/$1"
  mkdir -p $DEST_PARENT_DIR/$1
fi

# Sprawdź, czy katalog istnieje
if [ -d "$DEST_PARENT_DIR/$1" ]; then
  echo "Przenoszenie plików, logowane do logu $LOG_FILE"
  mv -v $OUT/$1*.csv $DEST_PARENT_DIR/$1 >> $LOG_FILE
  echo "Po przenoszeniu - zawartość katalogu $OUT"
  ls -al $OUT
  echo "Po przenoszeniu - zawartość katalogu $DEST_PARENT_DIR/$1"
  ls -al $DEST_PARENT_DIR/$1
  echo "Zakończono pomyślnie"
else
  echo "Ale coś poszło nie tak... chyba nie udało się utworzyć katalogu"
  echo "Katalog $DEST_PARENT_DIR/$1 nie istnieje."
  exit 1
fi

# Zakończ skrypt
exit 0