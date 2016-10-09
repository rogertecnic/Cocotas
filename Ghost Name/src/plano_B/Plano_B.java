package plano_B;

import sek2016.Plano_A;

public class Plano_B {
	//======================VARIAVEIS DE CONDICAO INICIAL DA ARENA=================
	public static int
	configArena = 0, // config A = 1, config B = 2, config C = 3
	configCave = 0; // da uma lida no metodo mostraMenu2
	public static boolean
	bonecoNoCentro = true; // autoexplicativo (alterado a cada reinicio)

	//========================CONSTANTES int==========================
	public static final int
	ARENA_A = 1,
	ARENA_B = 2,
	ARENA_C = 3,
	CAV_DIR = 1,
	CAV_ESQ = 2,
	CAV_CIMA = 3;


	/**
	 * Metodo que rege todo o plano B, seria o main do plano B
	 */
	public static void partiu(){
		configArena = Plano_A.configArena;
		configCave = Plano_A.configCave;
		bonecoNoCentro = Plano_A.bonecoNoCentro;

		Navegacao_secundaria.setaArena(configArena, configCave);
	}
}

