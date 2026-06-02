#!/bin/bash
# iPad-cmc-extractor

# Stałe określające parametry
EXPECTED_WIDTH=2732
EXPECTED_HEIGHT=2048

CROP_WIDTH=1728
CROP_HEIGHT=1475

CROP_X=10
CROP_Y=414

IMGC_DEFAULT_DIR=$HOME/Images/imgc/iPad/cmc-extr

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

# Przygotowanie nazwy katalogu wyjściowego
FILENAME=$(basename "$INPUT_FILE")       # wyciąga samą nazwę pliku (bez ścieżki) z $INPUT_FILE
EXTENSION="${FILENAME##*.}"              # wyodrębnia rozszerzenie (część po ostatniej kropce)
FILENAME_WITHOUT_EXT="${FILENAME%.*}"    # wyodrębnia nazwę bez rozszerzenia

DATE_TIMESTAMP=$(date +%Y%m%dT%H%M%S)
echo "Data: ${DATE_TIMESTAMP}"
OUTPUT_DIR_NAME="${FILENAME_WITHOUT_EXT}_${DATE_TIMESTAMP}"
OUTPUT_DIR_PATH="${IMGC_DEFAULT_DIR}/${OUTPUT_DIR_NAME}"

echo "Tworzenie katalogu ${OUTPUT_DIR_PATH}"
mkdir -p "${OUTPUT_DIR_PATH}"

# Funkcja wycinająca fragment obrazu
crop_image() {
    local OUT_DIR="$1"
    local OUT_FILENAME="$2"
    local C_WIDTH=$(( $4 - $6 + 1 ))
    local C_HEIGHT=$(( $5 - $3 + 1 ))
    local C_X="$6"
    local C_Y="$3"
    local STEP_ID="$7"

    local OUT_PATH="${OUT_DIR}/${OUT_FILENAME}"

    if magick "$INPUT_FILE" -crop "${C_WIDTH}x${C_HEIGHT}+${C_X}+${C_Y}" "$OUT_PATH"; then
        echo "Sukces (${STEP_ID}): Utworzono plik $OUT_PATH"
        return 0
    else
        echo "Błąd (${STEP_ID}): Nie udało się przetworzyć obrazu!"
        exit "$STEP_ID"
    fi
}

# STANDARD_FULL_IMAGE
crop_image "${OUTPUT_DIR_PATH}" "standardFullImage.${EXTENSION}" 0 2732 2048 0 1000

# SNAPSHOT_DATETIME
crop_image "${OUTPUT_DIR_PATH}" "snapshotDateTime.${EXTENSION}" 9 300 37 33 1001

# NOT_CHART_MARKER
crop_image "${OUTPUT_DIR_PATH}" "notChartMarker.${EXTENSION}" 77 90 120 35 1002

# VALOR_NAME
crop_image "${OUTPUT_DIR_PATH}" "valorName.${EXTENSION}" 165 182 201 30 1003

# NARROW_TOOLBOX_MARKER
crop_image "${OUTPUT_DIR_PATH}" "narrowToolboxMarker.${EXTENSION}" 1910 119 1940 89 1004

# WIDE_TOOLBOX_MARKER
crop_image "${OUTPUT_DIR_PATH}" "wideToolboxMarker.${EXTENSION}" 1910 163 1940 133 1005

# INTERVAL_LINE_1
crop_image "${OUTPUT_DIR_PATH}" "intervalLine1.${EXTENSION}" 1941 101 1963 66 1006

# INTERVAL_LINE_2
crop_image "${OUTPUT_DIR_PATH}" "intervalLine2.${EXTENSION}" 1974 113 1991 66 1007

# INTERVAL_OPTIONS
crop_image "${OUTPUT_DIR_PATH}" "intervalOptions.${EXTENSION}" 1906 81 1926 22 1008

# DEFAULT_NARROW_TOOLBOX_VALUES_FRAME
crop_image "${OUTPUT_DIR_PATH}" "defaultNarrowToolboxValuesFrame.${EXTENSION}" 414 1738 1889 10 1009

# DEFAULT_WIDE_TOOLBOX_VALUES_FRAME
crop_image "${OUTPUT_DIR_PATH}" "defaultWideToolboxValuesFrame.${EXTENSION}" 414 2618 1889 10 1010

# DEFAULT_NARROW_VALUES_FRAME
crop_image "${OUTPUT_DIR_PATH}" "defaultNarrowValuesFrame.${EXTENSION}" 414 1738 1989 10 1011

# DEFAULT_WIDE_VALUES_FRAME
crop_image "${OUTPUT_DIR_PATH}" "defaultWideValuesFrame.${EXTENSION}" 414 2618 1989 10 1012
