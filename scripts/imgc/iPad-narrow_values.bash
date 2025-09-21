#!/bin/bash

# iPad-narrow_values


# Stałe określające parametry
EXPECTED_WIDTH=2732
EXPECTED_HEIGHT=2048

CROP_WIDTH=1728
CROP_HEIGHT=1475

CROP_X=10
CROP_Y=414

IMGC_DEFAULT_DIR=$HOME/Images/imgc/iPad

OUTPUT_SUFFIX="_narrow_values"

FULL_SUBSTITUTION="_1728x1475"

# Sprawdzenie czy podano argument help
if [ "$1" = "-h" ]; then
    echo "Skrypt do wycinania z obrazu o wymiarach ${EXPECTED_WIDTH}x${EXPECTED_HEIGHT} fragmentu ${CROP_WIDTH}x${CROP_HEIGHT}+${CROP_X}+${CROP_Y}"
    echo "Czyli wycinany jest obraz o wymiarach ${CROP_WIDTH}x${CROP_HEIGHT} mającego lewy górny róg w ${CROP_X}x${CROP_Y}"
    echo "Użycie: $0 nazwa_pliku"
    echo "Parameters:"
    echo "  -h     Display this help message"
    echo "Script requires ImageMagick to be installed"
    exit 0
fi

# Sprawdzenie czy podano argument
if [ $# -ne 1 ]; then
    echo "Użycie: $0 nazwa_pliku"
    exit 1
fi

INPUT_FILE="$1"

# Sprawdzenie czy plik istnieje
if [ ! -f "$INPUT_FILE" ]; then
    echo "Błąd: Plik $INPUT_FILE nie istnieje!"
    exit 2
fi

# Pobranie wymiarów obrazu używając identify
DIMENSIONS=$(identify -format "%wx%h" "$INPUT_FILE")
ACTUAL_WIDTH=$(echo $DIMENSIONS | cut -d'x' -f1)
ACTUAL_HEIGHT=$(echo $DIMENSIONS | cut -d'x' -f2)

# Sprawdzenie wymiarów
if [ "$ACTUAL_WIDTH" -ne "$EXPECTED_WIDTH" ] || [ "$ACTUAL_HEIGHT" -ne "$EXPECTED_HEIGHT" ]; then
    echo "Błąd: Nieprawidłowe wymiary obrazu!"
    echo "Oczekiwano: ${EXPECTED_WIDTH}x${EXPECTED_HEIGHT}"
    echo "Otrzymano: ${ACTUAL_WIDTH}x${ACTUAL_HEIGHT}"
    exit 3
fi

# Sprawdzenie i utworzenie katalogu docelowego
if [ ! -d "$IMGC_DEFAULT_DIR" ]; then
    echo "Tworzenie katalogu ${IMGC_DEFAULT_DIR} - ponieważ jeszcze nie istnieje"
    mkdir -p "$IMGC_DEFAULT_DIR"
fi

# Przygotowanie nazwy pliku wyjściowego
FILENAME=$(basename "$INPUT_FILE")
EXTENSION="${FILENAME##*.}"
FILENAME_WITHOUT_EXT="${FILENAME%.*}"
if [[ "$FILENAME_WITHOUT_EXT" == *"_full" ]]; then
    OUTPUT_FILE_NAME="${FILENAME_WITHOUT_EXT/_full/$FULL_SUBSTITUTION}${OUTPUT_SUFFIX}.${EXTENSION}"
else
    OUTPUT_FILE_NAME="${FILENAME_WITHOUT_EXT}${OUTPUT_SUFFIX}.${EXTENSION}"
fi
OUTPUT_FILE="${IMGC_DEFAULT_DIR}/${OUTPUT_FILE_NAME}"

# Wykonanie wycinania
if magick "$INPUT_FILE" -crop "${CROP_WIDTH}x${CROP_HEIGHT}+${CROP_X}+${CROP_Y}" "$OUTPUT_FILE"; then
    echo "Sukces: Utworzono plik $OUTPUT_FILE"
    exit 0
else
    echo "Błąd: Nie udało się przetworzyć obrazu!"
    exit 4
fi
