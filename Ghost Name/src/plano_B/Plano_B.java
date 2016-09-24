package plano_B;

import lejos.hardware.lcd.LCD;
import sek2016.EV3MainMenuClass;

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
		configArena = EV3MainMenuClass.configArena;
		configCave = EV3MainMenuClass.configCave;
		bonecoNoCentro = EV3MainMenuClass.bonecoNoCentro;

		
		
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