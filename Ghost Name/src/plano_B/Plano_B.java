package plano_B;

import sek2016.EV3MainMenuClass;
import sek2016.Navigation;
import sek2016.Sensors;

public class Plano_B {
	//======================VARIAVEIS DE CONDICAO INICIAL DA ARENA=================
	public static int
	cor_resgate = 3,
	configArena = 0, // config A = 1, config B = 2, config C = 3
	configCave = 0; // da uma lida no metodo mostraMenu2
	public static boolean
	bonecoNoCentro = false; // autoexplicativo (alterado a cada reinicio)
	

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
		
		cor_resgate = Sensors.verificaChao();
		Navegacao_secundaria.inicioSemBoneco(configArena, configCave); // testar todas as 9 possibilidades
		sequenciaBuscaParede(); // testar se esta buscando e resgatando mesmo
		
		
		
		
		
	}
	
	
	public static void sequenciaBuscaParede() {
		boolean achou = false;
		while(!achou){
			achou = Navegacao_secundaria.giroBusca(60, Navegacao_secundaria.PAREDE); // testar se esta virando e indo ate o boneco e fechando a garra
		if(!achou){
			Navigation.andar(0.2f);
			Navegacao_secundaria.pushSegmento(0, 0.2f, Navegacao_secundaria.PAREDE);
		}
		}
		boolean resgatar = Navegacao_secundaria.verificaBoneco(); // verificar se esta retornando o correto inclusive com o vermelho deve retornar false
		if(resgatar){
			Navegacao_secundaria.voltaNaPilha(Navegacao_secundaria.PAREDE);
			Navegacao_secundaria.voltaNaPilha(Navegacao_secundaria.CENTRAL);
		}
		Navigation.openGarra();
	}
}

