package sek2016;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

/**
 * 
 * @author Rogerio
 *Classe que controla a Thread main (metodo main) e 
 *chama a Thread do codigo da sek da classe AlienRescue
 */
public class EV3MainMenuClass {
	/**
	 * @exit
	 *  variavel que confirma se � pra dar reset no brick
	 */
	private static boolean exit = false;

	/**
	 * @MotorInstanciado
	 * Variavel que controla a instancia dos sensores e motores.<br>
	 * <b>true:</b> ja foi instanciado, (resetar gyroscopio);<br>
	 * <b>false:</b> ainda n�o foi instanciado, (primeira execu��o do codigo);
	 */
	private static boolean jaIniciado = false;

	/**
	 * @threadPrograma
	 *  principal do codigo, � instanciada dentro do selecionaOpcao
	 */
	public static Thread threadPrograma = null;

	//========================CONSTANTES int==========================
	
	public static final int
	ARENA_A = 1,
	ARENA_B = 2,
	ARENA_C = 3,
	CAV_DIR = 1,
	CAV_ESQ = 2,
	CAV_CIMA = 3;
	
	//======================VARIAVEIS DE CONDICAO INICIAL DA ARENA=================
	public static int
	configArena = 0, // config A = 1, config B = 2, config C = 3
	configCave = 0; // da uma lida no metodo mostraMenu2
	public static boolean
	bonecoNoCentro = true; // autoexplicativo (alterado a cada reinicio)

	//=====================MAIN===================================
	public static void main(String[] args) {
		while (!exit) {
			controleMenu();
			if (!exit) {
				Button.waitForAnyPress();
				Navigation.stop();
				AlienRescue.alienRescueON = false;
				threadPrograma.stop();
			}
		}
	}
	//=============================================================

	/**
	 * M�tpdo que imprime o primeiro menu para selecionar o tipo de arena <br>
	 * @param configArena inteiro que indica qual op��o est� real�ada:<br>
	 * <b>1:</b> Arena A, modulos perif�ricos nas laterais<br>
	 * <b>2:</b> Arena B, m�dulos perif�ricos a frente edireita<br>
	 * <b>3:</b> Arena C, m�dulos perif�ricos a frente esquerda<br>
	 */
	private static void mostraMenu1(int configArena) {
		LCD.clear();
		switch(configArena){
		case 1:{
			LCD.drawString("CONFIG DA ARENA", 0, 0);
			LCD.drawString(">" + "A    P", 0, 1);
			LCD.drawString("         r C r", 0, 2);
			LCD.drawString("           P", 0, 3);
			break;
		}
		case 2:{
			LCD.drawString("CONFIG DA ARENA", 0, 0);
			LCD.drawString(">" + "B    r", 0, 1);
			LCD.drawString("         r C P", 0, 2);
			LCD.drawString("           P", 0, 3);
			break;
		}case 3:{
			LCD.drawString("CONFIG DA ARENA", 0, 0);
			LCD.drawString(">" + "C    r", 0, 1);
			LCD.drawString("         P C r", 0, 2);
			LCD.drawString("           P", 0, 3);
			break;
		}
		}
	}

	/**
	 * M�tpdo que imprime o segundo menu para selecionar o local da caverna <br>
	 * @param configCave inteiro que indica qual op��o est� real�ada:<br>
	 */
	private static void mostraMenu2(int configCave) {
		LCD.clear();
		switch(configArena){
		case ARENA_A:{
			if(configCave == CAV_DIR){
				LCD.drawString("CAVERNA ESTA?", 0, 0);
				LCD.drawString("<  direita  >", 0, 1);
			}else{
				LCD.drawString("CAVERNA ESTA?", 0, 0);
				LCD.drawString("<  esquerda  >", 0, 1);
			}
		}
		case ARENA_B:{
			if(configCave == CAV_CIMA){
				LCD.drawString("CAVERNA ESTA?", 0, 0);
				LCD.drawString("<  cima  >", 0, 1);
			}else{
				LCD.drawString("CAVERNA ESTA?", 0, 0);
				LCD.drawString("<  direita  >", 0, 1);
			}
		}case ARENA_C:{
			if(configCave == CAV_CIMA){
				LCD.drawString("CAVERNA ESTA?", 0, 0);
				LCD.drawString("<  cima  >", 0, 1);
			}else{
				LCD.drawString("CAVERNA ESTA?", 0, 0);
				LCD.drawString("<  esquerda  >", 0, 1);
			}
		}
		}
	}

	/**
	 * Mostra o menu de decidir se tem ou nao boneco no modulo central
	 * @param bonecoNoCentro inteiro que indica qual op��o est� real�ada<br>
	 */
	private static void mostraMenu3(boolean bonecoNoCentro, boolean exit ){
		LCD.clear();
		if(bonecoNoCentro && !exit){
			LCD.drawString("BONECO NO CETNRO?", 0, 0);
			LCD.drawString("<  sim  >", 0, 1);
		}else if(!bonecoNoCentro && !exit){
			LCD.drawString("BONECO NO CENTRO?", 0, 0);
			LCD.drawString("<  nao  >", 0, 1);
		}else{
			LCD.drawString("RESET BRICK?", 0, 0);
			LCD.drawString("<  OW YES  >", 0, 1);
		}

	}


	private static void start(){
		System.out.println(configArena + "  " + configCave + "  " + bonecoNoCentro);
		//------------tirar apos todos os codigos ja estarem feitos
		AlienRescue.alienRescueON = true;
		Navigation.init(!jaIniciado);
		Sensors.init(!jaIniciado,!jaIniciado,!jaIniciado,!jaIniciado);
		jaIniciado = true;
		threadPrograma = new Thread(new AlienRescue());
		threadPrograma.setDaemon(true);
		threadPrograma.setName("AlienRescue");
		threadPrograma.start();
		//-------------------------------------------------------------
		if(configArena == ARENA_A){
			if(configCave == CAV_DIR){
				
			}else if(configCave == CAV_ESQ){
				System.out.println("A ESQ");
			}
		}
		if(configArena == ARENA_B){
			if(configCave == CAV_CIMA){
				System.out.println("B CIMA");
			}else if(configCave == CAV_DIR){
				System.out.println("B DIR");
			}
		}
		if(configArena == ARENA_C){
			if(configCave == CAV_CIMA){
				System.out.println("C ");
			}else if(configCave == CAV_ESQ){

			}
		}
	}

	/**
	 * Metodo que faz o controle do menu e a intera��o da tecla com o menu<br>
	 * cuidado, metodo insanamente grande com alta probabilidade de dar problemas<br>
	 * n�o modificar, est� funcionando perfeitamente
	 */
	private static void controleMenu() {
		int arena = ARENA_A; // indica qual opcao foi selecionada no menu da arena
		boolean 
		noMenu1 = !jaIniciado,
		noMenu2 = !jaIniciado,
		noMenu3 = true;
		//================MENU DA ARENA==================================
		while (noMenu1) {
			mostraMenu1(arena);
			switch (Button.waitForAnyPress()) {
			case Button.ID_DOWN: {
				switch(arena){
				case ARENA_A:{
					arena = ARENA_B;
					break;
				}case ARENA_B:{
					arena = ARENA_C;
					break;
				}case ARENA_C:{
					arena = ARENA_A;
					break;
				}
				}
				break;
			}
			case Button.ID_UP: {
				switch(arena){
				case ARENA_A:{
					arena = ARENA_C;
					break;
				}case ARENA_B:{
					arena = ARENA_A;
					break;
				}case ARENA_C:{
					arena = ARENA_B;
					break;
				}
				}
				break;
			}
			case Button.ID_ENTER: {
				configArena = arena;
				LCD.clear();
				noMenu1 = false;
				break;
			}
			}
		}

		int caverna; //indica qual opcao foi selecionada no menu da caverna
		switch(arena){
		case ARENA_A:{
			caverna = CAV_DIR;
			break;
		}default:{
			caverna = CAV_CIMA;
			break;
		}
		}

		//==============MENU DA CAVERNA=============================
		while(noMenu2){
			mostraMenu2(caverna);
			switch (Button.waitForAnyPress()) {
			case Button.ID_ENTER: {
				configCave = caverna;
				LCD.clear();
				noMenu2 = false;
				break;
			}default:{
				switch(arena){
				case ARENA_A:{
					if(caverna == CAV_DIR)
						caverna = CAV_ESQ;
					else caverna = CAV_DIR;
					break;
				}case ARENA_B:{
					if(caverna == CAV_CIMA)
						caverna = CAV_DIR;
					else caverna = CAV_CIMA;
					break;
				}case ARENA_C:{
					if(caverna == CAV_CIMA)
						caverna = CAV_ESQ;
					else caverna = CAV_CIMA;
					break;
				}
				}
			}
			}
		}

		//==============MENU DOS BONECOS "FINAL"=============================
		boolean boneco = true, //indica se tem ou n�o boneco ou se � pra sair do codigo
				exit = false;
		while(noMenu3){
			mostraMenu3(boneco, exit);
			switch (Button.waitForAnyPress()) {
			case Button.ID_DOWN: {
				if(boneco)
					boneco = false;
				else if(!exit)
					exit = true;
				else{
					boneco = true;
					exit = false;
				}
				break;
			}
			case Button.ID_UP: {
				if(boneco){
					boneco = false;
					exit = true;
				}else if(exit)
					exit = false;
				else boneco = true;
				break;
			}
			case Button.ID_ENTER: {
				bonecoNoCentro = boneco;
				EV3MainMenuClass.exit = exit;
				LCD.clear();
				noMenu3 = false;
				break;
			}
			}
		}
		if(!exit) start();
	}
}
