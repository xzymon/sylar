#!/bin/bash

THIS_FILE_DIR=$SYLAR_PROJECT_SCRIPTS_DIR
IN_DIR=$SYLAR_DATA_SRC_IN_DIR

SUBSCRIPT_FILE_NAME=mv_csv_files_out_for_valor.bash
VALORS_FILE_NAME=valors_curr

SUBSCRIPT_FILE=$THIS_FILE_DIR/$SUBSCRIPT_FILE_NAME
VALORS_FILE=$IN_DIR/$VALORS_FILE_NAME

# Dla wszystkich walorów z pliku wejściowego - wykorzystanie innego skryptu
# Przenoszenie plików CSV wygenerowanych przez sylar - do katalogu gdzie będą składane dla danego waloru

# Sprawdzenie, czy plik VALORS_FILE istnieje
if [ ! -f "$VALORS_FILE" ]; then
    echo "Błąd: Plik \"$VALORS_FILE\" nie istnieje."
    exit 1
fi

# Przetwarzanie wartości z pliku VALORS_FILE
while IFS= read -r valor; do
    echo "Dla: $valor"

    # Wywołanie skryptu dla każdej wartości
    bash "$SUBSCRIPT_FILE" "$valor" "$DATES_FILE"

    # Można dodać dodatkowe operacje dla każdej wartości, jeśli to konieczne
done < "$VALORS_FILE"

echo "Zakończono bez błędów"

# Zakończ skrypt
exit 0