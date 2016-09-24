package plano_B;

import java.util.Stack;

import lejos.hardware.lcd.LCD;

public class Navegacao_secundaria {
	// ========================CONSTANTES int==========================
	public static final int ARENA_A = 1, ARENA_B = 2, ARENA_C = 3, CAV_DIR = 1, CAV_ESQ = 2, CAV_CIMA = 3;

	// ======================PILHAS DE SEGMENTOS=========================
	/**
	 * pilha de segmentos que o robo vai seguir no modulo central ate trocar de
	 * modulo
	 */
	public static Stack<Segmento> segmentosCentral = new Stack<Segmento>();

	/**
	 * pilha de segmentos que o robo vai seguir no modulo central ate trocar de
	 * modulo
	 */
	public static Stack<Segmento> segmentosCaverna = new Stack<Segmento>();

	/**
	 * faz o primeiro movimento de acordo com a configuracao de arena e da cave
	 */
	public static void PrimeiroMov(int configArena, int configCave) {
		if (configArena == ARENA_A) {
			if (configCave == CAV_DIR) {

			} else {

			}
		}
		if (configArena == ARENA_B) {
			if (configCave == CAV_CIMA) {

			} else {

			}
		}
		if (configArena == ARENA_C) {
			LCD.clear(); // pode apagar
			if (configCave == CAV_CIMA) {

			} else {

			}
		}
	}

}
