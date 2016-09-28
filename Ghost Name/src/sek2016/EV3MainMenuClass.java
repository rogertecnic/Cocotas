package sek2016;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import plano_B.Plano_B;

/**
 * 
 * @author Rogerio
 *Classe que controla a Thread main (metodo main) e 
 *chama a Thread do codigo da sek da classe AlienRescue
 */
public class EV3MainMenuClass {
	/**
	 *  variavel que confirma se é pra dar reset no brick
	 */
	private static boolean exit = false;

	/**
	 * Variavel que controla a instancia dos sensores e motores e 
	 * configuracoes iniciais que serao feita uma vez.<br>
	 * <b>true:</b> ja foi instanciado, (resetar gyroscopio);<br>
	 * <b>false:</b> ainda não foi instanciado, (primeira execução do codigo);
	 */
	private static boolean jaIniciado = false;

	/**
	 *  principal do codigo, é instanciada dentro do selecionaOpcao
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
	public static void main(String[] args) { // foca so no metodo start, o resto eh coisa pra controlar o menu
		while (!exit) {
			Navigation.init(!jaIniciado);
			Sensors.init(!jaIniciado,!jaIniciado,!jaIniciado,!jaIniciado);
			if(!jaIniciado){
				Sensors.calibraCorDoll();
				Sensors.calibraCorChao();
			}
			
			// ===== essa parte so eh executada se for o plano b
			if(Plano_B.planob && !jaIniciado){
				//birl, so fazer o menu la no planob e colocar aqui
				Plano_B.controleMenuParede();
			}
			// ===== essa parte so eh executada se for o plano b
			
			Plano_B.planob = false;
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
	 * Métpdo que imprime o primeiro menu para selecionar o tipo de arena <br>
	 * @param configArena inteiro que indica qual opção está realçada:<br>
	 * <b>1:</b> Arena A, modulos periféricos nas laterais<br>
	 * <b>2:</b> Arena B, módulos periféricos a frente edireita<br>
	 * <b>3:</b> Arena C, módulos periféricos a frente esquerda<br>
	 */
	private static void mostraMenu1(int configArena) {
		LCD.clear();
		switch(configArena){
		case 1:{
			LCD.drawString("CONFIG DA ARENA", 0, 0);
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
			LCD.drawString("r mod. entrada",0, 4);
			LCD.drawString("P perifericos",0, 5);
			LCD.drawString("C central",0, 6);
			break;
		}
		}
	}

	/**
	 * Métpdo que imprime o segundo menu para selecionar o local da caverna <br>
	 * @param configCave inteiro que indica qual opção está realçada:<br>
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
			break;
		}
		case ARENA_B:{
			if(configCave == CAV_CIMA){
				LCD.drawString("CAVERNA ESTA?", 0, 0);
				LCD.drawString("<  cima  >", 0, 1);
			}else{
				LCD.drawString("CAVERNA ESTA?", 0, 0);
				LCD.drawString("<  direita  >", 0, 1);
			}
			break;
		}
		case ARENA_C:{
			if(configCave == CAV_CIMA){
				LCD.drawString("CAVERNA ESTA?", 0, 0);
				LCD.drawString("<  cima  >", 0, 1);
			}else{
				LCD.drawString("CAVERNA ESTA?", 0, 0);
				LCD.drawString("<  esquerda  >", 0, 1);
			}
			break;
		}
		}
	}

	/**
	 * Mostra o menu de decidir se tem ou nao boneco no modulo central
	 * @param bonecoNoCentro inteiro que indica qual opção está realçada<br>
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

	/**
	 * Metodo que executa a thread do AlienRescue e faz<br>
	 * todas as definicoes de acordo com as opcoes escolhidas nos menus
	 */
	private static void start(){
		threadPrograma = new Thread(new AlienRescue());
		threadPrograma.setDaemon(true);
		threadPrograma.setName("AlienRescue");
		jaIniciado = true; // seta que a config inicial ja foi feita
		Delay.msDelay(500);
		//-------------------------------------------------------------
		if(configArena == ARENA_A){
			if(configCave == CAV_DIR){ // qui eh executado na configuracao arena A cave dir
				LCD.clear(); // pode apagar
				LCD.drawString("A DIR", 0, 3); // pode apagar
			}else if(configCave == CAV_ESQ){// qui eh executado na configuracao arena A cave esq
				LCD.clear(); // pode apagar
				LCD.drawString("A ESQ", 0, 3); // pode apagar
			}
		}
		if(configArena == ARENA_B){
			if(configCave == CAV_CIMA){// qui eh executado na configuracao arena B cave cima
				LCD.clear(); // pode apagar
				LCD.drawString("B CIMA", 0, 3); // pode apagar
			}else if(configCave == CAV_DIR){// qui eh executado na configuracao arena B cave dir
				LCD.clear(); // pode apagar
				LCD.drawString("B DIR", 0, 3); // pode apagar
			}
		}
		if(configArena == ARENA_C){
			LCD.clear(); // pode apagar
			if(configCave == CAV_CIMA){// qui eh executado na configuracao arena C cave cima
				LCD.drawString("C CIMA", 0, 3); // pode apagar
			}else if(configCave == CAV_ESQ){// qui eh executado na configuracao arena C cave esq
				LCD.drawString("C ESQ", 0, 3);// pode apagar
			}
		}
		LCD.drawString("boneco:" + (bonecoNoCentro?"sim":"nao"), 0, 2); // pode apagar

		AlienRescue.alienRescueON = true;
		threadPrograma.start(); // inicia a thread
	}

	/**
	 * Metodo que faz o controle do menu e a interação da tecla com o menu<br>
	 * cuidado, metodo insanamente grande com alta probabilidade de dar problemas<br>
	 * não modificar, está funcionando perfeitamente
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
			case Button.ID_RIGHT: {
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
			case Button.ID_LEFT: {
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
					//System.out.println("testando1");
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
				break;
			}
			}
		}

		//==============MENU DOS BONECOS "FINAL"=============================
		boolean boneco = true, //indica se tem ou não boneco ou se é pra sair do codigo
				exit = false;
		while(noMenu3){
			mostraMenu3(boneco, exit);
			switch (Button.waitForAnyPress()) {
			case Button.ID_RIGHT: {
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
			case Button.ID_LEFT: {
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
