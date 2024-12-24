package com.xzymon.sylar.constants;

import java.util.*;
import java.util.stream.Collectors;

public class FontSize8Characters {
	public static final int LINE_HEIGHT = 12;
	public static final int LINE_HEIGHT_HALF = 6;
	public static final int LETTER_WIDTH = 6;
	public static final int LETTER_HEIGHT = 8;
	public static final int LETTER_COLOR = 1;
	public static final int BACKGROUND_COLOR = 0;

	//5*11, pierwszy wiersz ponad linią tekstu, potem 8 wierszy w lini tekstu, na dole 2 linie na piksele spadające pod linię tekstu
	// pomiędzy literami 1 kolumna pusta, pomiędzy wierszami tekstu 1 linia pusta
	// czyli mamy teoretyczną wielkość obszaru 1 litery : (5+1) * (1 + 8 + 2 + 1) = 6*12
	public static final int[] DIGIT_0 = {0,0,0,0,0,0,0,1,0,0,0,1,0,1,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,0,1,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] DIGIT_1 = {0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,1,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] DIGIT_2 = {0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] DIGIT_3 = {0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,0,0,0,0,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,1,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] DIGIT_4 = {0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,1,0,1,0,1,0,0,1,0,1,0,0,1,0,1,1,1,1,1,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] DIGIT_5 = {0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,1,1,1,1,0,1,0,0,0,1,0,0,0,0,1,0,0,0,0,1,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] DIGIT_6 = {0,0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,0,0,0,0,1,1,1,1,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] DIGIT_7 = {0,0,0,0,0,1,1,1,1,1,0,0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] DIGIT_8 = {0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,1,0,0,0,1,0,1,1,1,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] DIGIT_9 = {0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,0,1,1,1,1,0,0,0,0,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0};

	public static final int[] LETTER_UPPER_A = {0,0,0,0,0,0,0,1,0,0,0,1,0,1,0,1,0,0,0,1,1,0,0,0,1,1,1,1,1,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_B = {0,0,0,0,0,1,1,1,1,0,1,0,0,0,1,1,0,0,0,1,1,1,1,1,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_C = {0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_D = {0,0,0,0,0,1,1,1,1,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_E = {0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,1,0,0,0,0,1,1,1,1,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_F = {0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,1,0,0,0,0,1,1,1,1,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_G = {0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,1,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,0,0,0,1,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_H = {0,0,0,0,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,1,1,1,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_I = {0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_J = {0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,1,0,0,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_K = {0,0,0,0,0,1,0,0,0,1,1,0,0,0,1,1,0,0,1,0,1,0,1,0,0,1,1,1,0,0,1,0,0,1,0,1,0,0,0,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_L = {0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_M = {0,0,0,0,0,1,0,0,0,1,1,1,0,1,1,1,0,1,0,1,1,0,1,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_N = {0,0,0,0,0,1,0,0,0,1,1,1,0,0,1,1,1,0,0,1,1,0,1,0,1,1,0,1,0,1,1,0,0,1,1,1,0,0,1,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_O = {0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_P = {0,0,0,0,0,1,1,1,1,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,1,1,1,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_Q = {0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,1,0,1,0,1,1,1,0,0,0,0,0,1,0,0,0,0,0};
	public static final int[] LETTER_UPPER_R = {0,0,0,0,0,1,1,1,1,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,1,1,1,0,1,0,1,0,0,1,0,0,1,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_S = {0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,1,0,0,0,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,1,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_T = {0,0,0,0,0,1,1,1,1,1,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_U = {0,0,0,0,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_V = {0,0,0,0,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_W = {0,0,0,0,0,1,0,0,0,1,1,0,0,0,1,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,1,0,1,1,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_X = {0,0,0,0,0,1,0,0,0,1,1,0,0,0,1,0,1,0,1,0,0,0,1,0,0,0,0,1,0,0,0,1,0,1,0,1,0,0,0,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_Y = {0,0,0,0,0,1,0,0,0,1,1,0,0,0,1,0,1,0,1,0,0,1,0,1,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_UPPER_Z = {0,0,0,0,0,1,1,1,1,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0};

	public static final int[] LETTER_LOWER_A = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,1,0,1,1,1,1,1,0,0,0,1,1,0,0,1,1,0,1,1,0,1,0,0,0,0,0,0,0,0,0,0};
	//b
	public static final int[] LETTER_LOWER_C = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_LOWER_D = {0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,1,1,0,1,1,0,0,1,1,1,0,0,0,1,1,0,0,0,1,1,0,0,1,1,0,1,1,0,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_LOWER_E = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,1,1,1,1,1,1,0,0,0,0,1,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	//f
	//g
	public static final int[] LETTER_LOWER_H = {0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,1,1,0,1,1,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_LOWER_I = {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_LOWER_J = {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,1,0,0,1,0,1,0,0,1,0,0,1,1,0,0};
	public static final int[] LETTER_LOWER_K = {0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,1,0,1,0,1,0,0,1,1,0,0,0,1,0,1,0,0,1,0,0,1,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_LOWER_L = {0,0,0,0,0,0,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_LOWER_M = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,0,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_LOWER_N = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,0,1,1,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_LOWER_O = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_LOWER_P = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,0,1,1,0,0,1,1,0,0,0,1,1,0,0,0,1,1,1,0,0,1,1,0,1,1,0,1,0,0,0,0,1,0,0,0,0};
	public static final int[] LETTER_LOWER_Q = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,0,0,1,1,1,0,0,0,1,1,0,0,0,1,1,0,0,1,1,0,1,1,0,1,0,0,0,0,1,0,0,0,0,1};
	public static final int[] LETTER_LOWER_R = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,0,1,1,0,0,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_LOWER_S = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1,0,0,0,1,0,1,1,0,0,0,0,0,1,0,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_LOWER_T = {0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,1,1,1,1,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] LETTER_LOWER_U = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,1,1,0,1,1,0,1,0,0,0,0,0,0,0,0,0,0};
	//v
	public static final int[] LETTER_LOWER_W = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,0,1,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0};
	//x
	public static final int[] LETTER_LOWER_Y = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,1,1,0,1,1,0,1,0,0,0,0,1,0,0,0,1,0,1,1,1,0,0};
	public static final int[] LETTER_LOWER_Z = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0};

	public static final int[] SPECIAL_CHARACTER_DOT              = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] SPECIAL_CHARACTER_WHITESPACE       = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] SPECIAL_CHARACTER_DASH             = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] SPECIAL_CHARACTER_COLON            = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] SPECIAL_CHARACTER_PARENTESIS_OPEN  = {0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] SPECIAL_CHARACTER_PARENTESIS_CLOSE = {0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0};
	public static final int[] SPECIAL_CHARACTER_SLASH            = {0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0};

	public final Map<Character, int[]> alphabeticsMap = new HashMap<>();
	public final Map<Character, int[]> numbersMap = new HashMap<>();
	public final Map<Character, int[]> specialsMap = new HashMap<>();

	public final Map<Character, int[]> allKnownNoWhitespaceMap = new HashMap<>();
	public final Map<Character, int[]> allKnownMap = new HashMap<>();
	public final Map<Character, int[]> numbersDotMap = new HashMap<>();
	public final Map<Character, int[]> numbersColonMap = new HashMap<>();

	List<FontSize8Character> allKnownCharsNoWhitespace;
	List<FontSize8Character> allKnownCharsNoWhitespaceSorted;

	List<FontSize8Character> allKnownChars;
	List<FontSize8Character> allKnownCharsSorted;

	List<FontSize8Character> numbersDotChars;
	List<FontSize8Character> numbersDotCharsSorted;

	List<FontSize8Character> numbersColonChars;
	List<FontSize8Character> numbersColonCharsSorted;

	public FontSize8Characters() {
		init();
	}

	public void init() {
		numbersMap.put('0', DIGIT_0);
		numbersMap.put('1', DIGIT_1);
		numbersMap.put('2', DIGIT_2);
		numbersMap.put('3', DIGIT_3);
		numbersMap.put('4', DIGIT_4);
		numbersMap.put('5', DIGIT_5);
		numbersMap.put('6', DIGIT_6);
		numbersMap.put('7', DIGIT_7);
		numbersMap.put('8', DIGIT_8);
		numbersMap.put('9', DIGIT_9);
		
		alphabeticsMap.put('A', LETTER_UPPER_A);
		alphabeticsMap.put('B', LETTER_UPPER_B);
		alphabeticsMap.put('C', LETTER_UPPER_C);
		alphabeticsMap.put('D', LETTER_UPPER_D);
		alphabeticsMap.put('E', LETTER_UPPER_E);
		alphabeticsMap.put('F', LETTER_UPPER_F);
		alphabeticsMap.put('G', LETTER_UPPER_G);
		alphabeticsMap.put('H', LETTER_UPPER_H);
		alphabeticsMap.put('I', LETTER_UPPER_I);
		alphabeticsMap.put('J', LETTER_UPPER_J);
		alphabeticsMap.put('K', LETTER_UPPER_K);
		alphabeticsMap.put('L', LETTER_UPPER_L);
		alphabeticsMap.put('M', LETTER_UPPER_M);
		alphabeticsMap.put('N', LETTER_UPPER_N);
		alphabeticsMap.put('O', LETTER_UPPER_O);
		alphabeticsMap.put('P', LETTER_UPPER_P);
		alphabeticsMap.put('Q', LETTER_UPPER_Q);
		alphabeticsMap.put('R', LETTER_UPPER_R);
		alphabeticsMap.put('S', LETTER_UPPER_S);
		alphabeticsMap.put('T', LETTER_UPPER_T);
		alphabeticsMap.put('U', LETTER_UPPER_U);
		alphabeticsMap.put('V', LETTER_UPPER_V);
		alphabeticsMap.put('W', LETTER_UPPER_W);
		alphabeticsMap.put('X', LETTER_UPPER_X);
		alphabeticsMap.put('Y', LETTER_UPPER_Y);
		alphabeticsMap.put('Z', LETTER_UPPER_Z);
		
		alphabeticsMap.put('a', LETTER_LOWER_A);
		alphabeticsMap.put('c', LETTER_LOWER_C);
		alphabeticsMap.put('d', LETTER_LOWER_D);
		alphabeticsMap.put('e', LETTER_LOWER_E);
		alphabeticsMap.put('h', LETTER_LOWER_H);
		alphabeticsMap.put('i', LETTER_LOWER_I);
		alphabeticsMap.put('j', LETTER_LOWER_J);
		alphabeticsMap.put('k', LETTER_LOWER_K);
		alphabeticsMap.put('l', LETTER_LOWER_L);
		alphabeticsMap.put('m', LETTER_LOWER_M);
		alphabeticsMap.put('n', LETTER_LOWER_N);
		alphabeticsMap.put('o', LETTER_LOWER_O);
		alphabeticsMap.put('p', LETTER_LOWER_P);
		alphabeticsMap.put('q', LETTER_LOWER_Q);
		alphabeticsMap.put('r', LETTER_LOWER_R);
		alphabeticsMap.put('s', LETTER_LOWER_S);
		alphabeticsMap.put('t', LETTER_LOWER_T);
		alphabeticsMap.put('u', LETTER_LOWER_U);
		alphabeticsMap.put('w', LETTER_LOWER_W);
		alphabeticsMap.put('y', LETTER_LOWER_Y);
		alphabeticsMap.put('z', LETTER_LOWER_Z);

		specialsMap.put('.', SPECIAL_CHARACTER_DOT);
		specialsMap.put('-', SPECIAL_CHARACTER_DASH);
		specialsMap.put(':', SPECIAL_CHARACTER_COLON);
		specialsMap.put('(', SPECIAL_CHARACTER_PARENTESIS_OPEN);
		specialsMap.put(')', SPECIAL_CHARACTER_PARENTESIS_CLOSE);
		specialsMap.put('/', SPECIAL_CHARACTER_SLASH);

		allKnownNoWhitespaceMap.putAll(numbersMap);
		allKnownNoWhitespaceMap.putAll(alphabeticsMap);
		allKnownNoWhitespaceMap.putAll(specialsMap);
		allKnownCharsNoWhitespace = convertMapToTypedList(allKnownNoWhitespaceMap);
		allKnownCharsNoWhitespaceSorted = toSortedList(allKnownCharsNoWhitespace);

		allKnownMap.putAll(allKnownNoWhitespaceMap);
		allKnownMap.put(' ', SPECIAL_CHARACTER_WHITESPACE);
		allKnownChars = convertMapToTypedList(allKnownMap);
		allKnownCharsSorted = toSortedList(allKnownChars);

		numbersDotMap.putAll(numbersMap);
		numbersDotMap.put('.', SPECIAL_CHARACTER_DOT);
		numbersDotChars = convertMapToTypedList(numbersDotMap);
		numbersDotCharsSorted = toSortedList(numbersDotChars);

		numbersColonMap.putAll(numbersMap);
		numbersColonMap.put(':', SPECIAL_CHARACTER_COLON);
		numbersColonChars = convertMapToTypedList(numbersColonMap);
		numbersColonCharsSorted = toSortedList(numbersColonChars);
	}

	public List<FontSize8Character> getAllKnownCharsNoWhitespaceSorted() {
		return allKnownCharsNoWhitespaceSorted;
	}

	public List<FontSize8Character> getAllKnownCharsSorted() {
		return allKnownCharsSorted;
	}

	public List<FontSize8Character> getNumbersDotCharsSorted() {
		return numbersDotCharsSorted;
	}

	public List<FontSize8Character> getNumbersColonCharsSorted() {
		return numbersColonCharsSorted;
	}
/*
	public String detectInArea(BufferedPaletteImage img, FrameCoords entireFC, List<FontSize8Character> knownChars) {

	}*/

	private List<FontSize8Character> convertMapToTypedList(Map<Character, int[]> map) {
		return map.entrySet().stream()
				.map(entry -> new FontSize8Character(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	private List<FontSize8Character> toSortedList(List<FontSize8Character> list) {
		return list.stream().sorted().collect(Collectors.toList());
	}

	public static class FontSize8Character implements Comparable<FontSize8Character> {
		public static final int LINE_HEIGHT = FontSize8Characters.LINE_HEIGHT-1;
		public static final int LETTER_WIDTH = FontSize8Characters.LETTER_WIDTH-1;

		private char character;
		private int[] array;

		public FontSize8Character(char character, int[] array) {
			this.array = array;
			this.character = character;
		}

		public char getCharacter() {
			return character;
		}

		public int[] getArray() {
			return array;
		}
		@Override
		public String toString() {
			return "FontSize8Character [character=" + character + ", array=" + Arrays.toString(array) + "]";
		}

		@Override
		public int compareTo(FontSize8Character o) {
			return 0;
		}
	}
}
