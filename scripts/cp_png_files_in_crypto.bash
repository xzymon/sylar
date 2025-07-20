#!/bin/bash

THIS_FILE_DIR=$SYLAR_PROJECT_SCRIPTS_DIR
IN_DIR=$SYLAR_DATA_SRC_IN_DIR

SUBSCRIPT_FILE_NAME=cp_png_files_in_for_valor_crypto.bash
DATES_FILE_NAME=sylar_in_dates_crypto
VALORS_FILE_NAME=valors_crypto

SUBSCRIPT_FILE=$THIS_FILE_DIR/$SUBSCRIPT_FILE_NAME

DATES_FILE=$IN_DIR/$DATES_FILE_NAME
VALORS_FILE=$IN_DIR/$VALORS_FILE_NAME

# Pamiętaj o odpowiednim ustawieniu wartości - powinno być:
# tradingDays.generator=crypto

# Dla wszystkich walorów z pliku wejściowego - wykorzystanie innego skryptu
# Kopiowanie plików PNG (zawierających świece z jednego dnia) - do katalogu wejściowego dla SYLAR
# + zmienianie nazwy plików, tak by nie było w nich duplikatów i by nazwa jednoznacznie opisywała zawartość

# Sprawdzenie, czy plik SUBSCRIPT_FILE istnieje
if [ ! -f "$SUBSCRIPT_FILE" ]; then
    echo "Błąd: Plik \"$SUBSCRIPT_FILE\" nie istnieje."
    exit 1
fi

# Sprawdzenie, czy plik DATES_FILE istnieje
if [ ! -f "$DATES_FILE" ]; then
    echo "Błąd: Plik \"$DATES_FILE\" nie istnieje."
    exit 2
fi

# Sprawdzenie, czy plik VALORS_FILE istnieje
if [ ! -f "$VALORS_FILE" ]; then
    echo "Błąd: Plik \"$VALORS_FILE\" nie istnieje."
    exit 3
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

