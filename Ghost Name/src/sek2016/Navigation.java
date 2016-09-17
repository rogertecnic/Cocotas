package sek2016;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;
import testes_variados.MainTeste;
import testes_variados.Testa_PID;


/**
 * Controle de movimento do robo
 */
public class Navigation {
	//---------------------DECLARACAO DOS MOTORES---------------------------
	static EV3LargeRegulatedMotor rodaE;
	static EV3LargeRegulatedMotor rodaD;
	static EV3MediumRegulatedMotor motorG;


	//---------------------CONSTANTES DE DESCRICAO--------------------------
	public final static float VELO_INI = 0.1f; // em m/s, velocidade linear do robo andar
	public final static float VELO_CURVA = 0.1f; // em m/s, velocidade linear do robo girar
	public final static float aceleration = 0.37978f; // m/s^2 PARA A RODA (equivale a 800 graus/s^2), 6000 de default do lejos (equivale a 2.84837 m/s^2
	public final static float DISTANCIA_ENTRE_RODAS = 0.13445f;//metros, ja conferido
	public final static float RAIO = 0.0272f; //metros, ja conferido (se alterar tem que alterar o de cima)

	// ------------------- CONSTANTES DE ORIENTACAO------------------------
	/**
	 * Constantes de orientação do robo    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<EM RELACAO A QUE?
	 */
	final static int BACK = 0,
			LEFT = 1,
			FRONT = 2,
			RIGTH = 3;

	/**
	 * Orientação real do robo
	 */
	static int orientation = FRONT;


	//---------------------VARIAVEIS DE PROCESSO----------------------------
	public static boolean garraFechada = false,//a garra esta fechada?
			curva = false; // o robo esta realisando uma curva?


	// --------------------METODOS------------------------------------------

	/**
	 * Gira o robo no proprio eixo, (não usa o giroscopio,
	 * usa o tacometro da rodaE, o método segura o programa dentro dele)
	 * @param graus inteiro positivo (anti-horário)
	 * inteiro negativo (horário)
	 */
	public static void turn(float graus){
		PID.pidRunning=false; // pausa o pid para não zoar as velocidades
		
		while(!PID.PIDparado){
			
		}
		PID.zeraPID();
		
		setVelocidade(VELO_CURVA, VELO_CURVA);

		if (-graus < 0) {
			int giro = (int) (-graus / -90);
			for (int x = 0; x < giro; x++) {
				alterOrientation(-1);
			}
		} else if (-graus > 0) {
			int giro = (int) (-graus / 90);
			for (int x = 0; x < giro; x++) {
				alterOrientation(1);
			}
		}


		float theta = (graus*DISTANCIA_ENTRE_RODAS)/(2*RAIO); // angulo que a roda precisa girar
		float positioninicialE = rodaE.getTachoCount(); // posicao em graus da roda e
		float positioninicialD = rodaD.getTachoCount(); // posicao em graus da roda d
		float wRoda = VELO_CURVA/RAIO*(float)(180/Math.PI);
		float acc = aceleration/RAIO*(float)(180/Math.PI);
		float ang_defasado = wRoda*(wRoda/acc)-(acc/2)*(wRoda/acc)*(wRoda/acc);
		
		if(graus>0){
			rodaD.forward();
			rodaE.backward();
			while(rodaE.getTachoCount()>(positioninicialE-theta+ang_defasado) && 
					rodaD.getTachoCount()<(positioninicialD+theta-ang_defasado)){
			}
		}
		if(graus<0){
			rodaE.forward();
			rodaD.backward();
			while(rodaD.getTachoCount()>(positioninicialD+theta+ang_defasado) && 
					rodaE.getTachoCount()<(positioninicialE-theta-ang_defasado)){
			}
		}
		Navigation.stop();
		curva = false;
	}



	/**
	 * robo anda pra frente(positivo) ou para traz (negativo) em uma determinada distancia.<br>
	 * velocidade definida pelo PID, metodo segura a thread dentro dele
	 * ate o robo chegar
	 * <h1>ESSE METODO NAO RESETA O PID, ISSO PODE CAUSAR PROBLEMAS</h1>
	 * @param dist ditancia em metros
	 */
	public static void andar(float dist){
		PID.pidRunning=false; // pausa o pid para não zoar as velocidades
		Delay.msDelay(40);
		while(!PID.PIDparado){
		}
		PID.zeraPID();
		while(Sensors.getAngle() !=0){
			Sensors.resetAngle();
		}
		
		PID.pidRunning = true;
		
		float theta =(dist/RAIO)*(float)(180/Math.PI); // graus da roda
		float positionE = rodaE.getTachoCount(); // posicao em graus da roda e
		float positionD = rodaD.getTachoCount(); // posicao em graus da roda d
		float wRoda = VELO_INI/RAIO*(float)(180/Math.PI);
		float acc = aceleration/RAIO*(float)(180/Math.PI);
		float ang_defasado = wRoda*(wRoda/(acc*(float)(180/Math.PI)))-(acc/2)*(wRoda/acc)*(wRoda/acc);

		if(dist>0){
			rodaE.forward();
			rodaD.forward();
			while(rodaE.getTachoCount()<(positionE+theta-ang_defasado) && 
					rodaD.getTachoCount()<(positionD+theta-ang_defasado)){
			}
		}else{
			rodaE.backward();
			rodaD.backward();
			while(rodaE.getTachoCount()>(positionE+theta+ang_defasado) && 
					rodaD.getTachoCount()>(positionD+theta+ang_defasado)){
			}
		}
		Navigation.stop();
	}

	/**
	 * Frente, velocidade definida pelo PID
	 */
	public static void forward() {
		rodaE.forward();
		rodaD.forward();
	}

	/**
	 * Traz. velocidade definida pelo PID
	 */
	public static void backward() {
		rodaE.backward();
		rodaD.backward();
	}

	/**
	 * Virar robo a esquerda 90 graus usando o giroscopio<br>
	 * trava o programa dentro do metodo ate o giro terminar
	 */
	/*public static void turnLeft() {
		PID.pidRunning=false; // pausa o pid para não zoar as velocidades
		float angle = Sensors.getAngle();
		while (angle <= 85) {
			rodaE.setSpeed(100);
			rodaD.setSpeed(100);
			rodaE.backward();
			rodaD.forward();
			angle = Sensors.getAngle();
		}
		stop();
		Delay.msDelay(10);
		Sensors.resetAngle();
		PID.zeraPID(); // zera o pid para a nova posição
		PID.pidRunning=true; // continua o pid
	}
	 */

	/**
	 * Virar robo a direita 90 graus usando o giroscopio<br>
	 * trava o programa dentro do metodo ate o giro terminar
	 */
	/*public static void turnRight() {
		PID.pidRunning=false; // pausa o pid para não zoar as velocidades
		float angle = Sensors.getAngle();
		while (angle >= -85) {
			rodaE.setSpeed(100);
			rodaD.setSpeed(100);
			rodaE.forward();
			rodaD.backward();
			angle = Sensors.getAngle();
		}
		stop();
		Delay.msDelay(10);
		Sensors.resetAngle();
		PID.zeraPID(); // zera o pid para a nova posição
		PID.pidRunning=true; // continua o pid
	}
	 */

	/**
	 * seta aceleracao em graus/s^2 de cada roda, EVITAR USAR
	 * @param accD aceleracao em m/s^2 motor direito
	 * @param accE aceleracao em m/s^2 motor esquerdo
	 */
	public static void setAcceleration(float accD, float accE){
		Navigation.rodaD.setAcceleration((int)((accD/RAIO)*(180/Math.PI)));
		Navigation.rodaE.setAcceleration((int)((accE/RAIO)*(180/Math.PI)));
	}

	/**
	 * seta velocidade em graus/s de cada roda
	 * @param veloD float  em m/seg da roda DIREITA
	 * @param veloE float em m/seg^2 da roda ESQUERDA
	 */
	public static void setVelocidade (float veloD, float veloE){
		Navigation.rodaD.setSpeed((int)((veloD/RAIO)*(180/Math.PI)));
		Navigation.rodaE.setSpeed((int)((veloE/RAIO)*(180/Math.PI)));
	}

	/**
	 * Parar o robo (trava o codigo dentro enquanto o robo nao parar)
	 */
	public static void stop(){
		rodaD.stop(true);
		rodaE.stop(true); // evita aquela jogadinha pro lado quando robo termina um movimento
		while(rodaE.isMoving() || rodaD.isMoving()){
		}
	}

	/**
	 * fechar garra
	 */
	public static void closeGarra(){
		if(garraFechada == false){
			garraFechada = true;
			motorG.rotate(60);
		}
		else{
			LCD.clear();
			LCD.drawString("Garra fechada", 0, 0);
		}
	}

	/**
	 * Abrir garra
	 */
	public static void openGarra(){
		if(garraFechada == true){
			garraFechada = false;
			motorG.rotate(-60);
		}
		else{
			LCD.clear();
			LCD.drawString("Garra aberta", 0, 0);
		}
	}

	/**
	 * instancia os motores
	 * @param initMotors: true ou false
	 */
	public static void init(boolean initMotors) {
		if(initMotors == true){
			rodaE = new EV3LargeRegulatedMotor(MotorPort.A);
			rodaD = new EV3LargeRegulatedMotor(MotorPort.B);
			motorG = new EV3MediumRegulatedMotor(MotorPort.C);
			setAcceleration(aceleration, aceleration);
			setVelocidade(VELO_INI, VELO_INI);
		}
	}
	
	
	
	
	//---------------------METODOS DA TACOMETRIA---------------------------
	/**
	 * Metodo que retorna a tacometria dos motores de acordo com os parametros
	 * especificados, caso o parametro seja desconhecido, retorna null.
	 * 
	 * @param L
	 * 		(Left) para retornar a tacometria do motor esquerdo <br>
	 * 		<b>R</b> (Right) para retornar a tacometria do motor direito<br>
	 * 		<b>B</b> (Both) para retornar a média da tacometria dos dois motores
	 * @return Retorna valores float <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<QUE VALORES FLOAT?
	 */
	public static float getTacho(String x) {
		if (x.toUpperCase() == "L") {
			return rodaE.getTachoCount();

		} else if (x.toUpperCase() == "R") {

			return rodaD.getTachoCount();

		} else if (x.toUpperCase() == "B") {
			float temp1 = rodaE.getTachoCount();
			float temp2 = rodaD.getTachoCount();

			return ((temp1 + temp2) / 2);

		} else {

			return (Float) null;
		}
	}
	
	/**
	 * Reseta a contagem dos tacometros de cada roda
	 */
	public static void resetTacho(){
		rodaE.resetTachoCount();
		rodaD.resetTachoCount();
	}
	
	/**
	 * Altera a orientacao do robo 
	 * @param ortt NAO ENTENDI O METODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Q Q ISSO???
	 */
	private static void alterOrientation(int ortt) {
		if (ortt > 1) {
			ortt = 1;
		} else if (ortt < -1) {
			ortt = -1;
		}

		switch (ortt) {
		case 1:
			orientation++;

			if (orientation > 3) {
				orientation = 0;
			}
			break;

		case -1:
			orientation--;

			if (orientation < 0) {
				orientation = 3;
			}
			break;
		}
	}
}
