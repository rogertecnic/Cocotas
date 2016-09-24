package plano_B;

import java.util.Stack;

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
	
	public static void setaArena(int configArena){
		switch(configArena){
		case ARENA_A:{
			
			break;
		}case ARENA_B:{

			break;
		}case ARENA_C:{

			break;
		}
		}
	}
	
}
