package plano_B;

import java.util.Stack;
import sek2016.*;

public class Navegacao_secundaria {
	//========================CONSTANTES int==========================
		public static final int
		ARENA_A = 1,
		ARENA_B = 2,
		ARENA_C = 3,
		CAV_DIR = 1,
		CAV_ESQ = 2,
		CAV_CIMA = 3;
		
	//======================PILHAS DE SEGMENTOS=========================
	/**
	 * pilha de segmentos que o robo vai seguir no modulo central ate trocar de modulo
	 */
	public static Stack<Segmento> segmentosCentral = new Stack<Segmento>();
	
	/**
	 * pilha de segmentos que o robo vai seguir no modulo central ate trocar de modulo
	 */
	public static Stack<Segmento> segmentosCaverna = new Stack<Segmento>();
	
	/**
	 * se nao tiver boneco no centro, vai pra sala que nao tem cave
	 * @param configArena
	 */
	public static void setaArena(int configArena, int configCave){
		switch(configArena){
		case ARENA_A:{
			Navigation.andar(0.925f+0.33f);
			if(configCave == CAV_DIR){
				Navigation.turn(90);
			}else{
				Navigation.turn(-90);
			}
			Navigation.andar(0.925f);
			break;
		}
		case ARENA_B:{
			if(configCave == CAV_CIMA){
				
			}else{
				
			}
			break;
		}
		case ARENA_C:{
			if(configCave == CAV_CIMA){
				
			}else{
				
			}
			break;
		}
		}
	}
	
	/*
	LCD.drawString("<A>        r", 0, 1);
	LCD.drawString("         P C P", 0, 2);
	LCD.drawString("           r", 0, 3);
	LCD.drawString("r mod. entrada",0, 4);
	LCD.drawString("P perifericos",0, 5);
	LCD.drawString("C central",0, 6);
	break;
	}
	case 2:{
	LCD.drawString("CONFIG DA ARENA", 0, 0);
	LCD.drawString("<B>        P", 0, 1);
	LCD.drawString("         r C P", 0, 2);
	LCD.drawString("           r", 0, 3);
	LCD.drawString("r mod. entrada",0, 4);
	LCD.drawString("P perifericos",0, 5);
	LCD.drawString("C central",0, 6);
	break;
	}case 3:{
	LCD.drawString("CONFIG DA ARENA", 0, 0);
	LCD.drawString("<C>        P", 0, 1);
	LCD.drawString("         P C r", 0, 2);
	LCD.drawString("           r", 0, 3);
	 */
}

