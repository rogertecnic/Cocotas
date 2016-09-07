package sek2016;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

/**
 * Controle de movimento do robo
 */
public class Navigation {
	//---------------------DECLARACAO DOS MOTORES---------------------------
	static EV3LargeRegulatedMotor rodaE;
	static EV3LargeRegulatedMotor rodaD;
	static EV3MediumRegulatedMotor motorG;

	//---------------------CONSTANTES DE DESCRICAO--------------------------
	private final static float VELO_INI = 8.0f;
	private final static float DISTANCIA_ENTRE_RODAS = 0.1378f;//metros, ja conferido
	private final static float RAIO = 0.0280f; //metros, ja conferido (se alterar tem que alterar o de cima)

	//---------------------VARIAVEIS DE PROCESSO----------------------------
	static float[] WdWe = new float[2]; //Velocidade angular da roda Direita e Esquerda
	private static boolean garraFechada = false;//para não abrir se ja estiver aberta ou vice versa

	// --------------------METODOS------------------------------------------
	/**
	 * Gira o robo no proprio eixo, (não usa o giroscopio,
	 * usa o tacometro da rodaE, o método segura o programa dentro dele)
	 * @param graus inteiro positivo (horário)
	 * inteiro negativo (antihorário)
	 */
	public static void turn(float graus){
		PID.pidRunning=false; // pausa o pid para não zoar as velocidades
		
		setVelocidade(300, 300);
		setAcceleration(400, 400);
		//float theta = ((3.14159265f*graus/180)*DISTANCIA_ENTRE_RODAS*180f)/(2f*RAIO*3.14159265f);
		float theta = (graus*DISTANCIA_ENTRE_RODAS)/(2*RAIO);
		int	thetaAnterior = rodaE.getTachoCount();
		if(graus<0){ // anti horario
			rodaD.forward();
			rodaE.backward();
			while(rodaE.getTachoCount()> thetaAnterior+theta){ // tacho diminui
				Delay.msDelay(20);
			}
		}else{ // horario
			rodaE.forward();
			rodaD.backward();
			while(rodaE.getTachoCount()< thetaAnterior+theta){ // thaco aumenta
				Delay.msDelay(20);
			}
		}
		Navigation.stop();
		PID.zeraPID(); // zera o pid para a nova posição
		PID.pidRunning=true; // continua o pid

	}

	/**
	 * Virar robo a esquerda 90 graus usando o giroscopio<br>
	 * trava o programa dentro do metodo ate o giro terminar
	 */
	public static void turnLeft() {
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

	/**
	 * Virar robo a direita 90 graus usando o giroscopio<br>
	 * trava o programa dentro do metodo ate o giro terminar
	 */
	public static void turnRight() {
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

	/**
	 * robo anda pra frente em uma determinada distancia.<br>
	 * velocidade definida pelo PID, metodo segura a thread dentro dele
	 * ate o robo chegar
	 * <h1>ESSE METODO NAO RESETA O PID, ISSO PODE CAUSAR PROBLEMAS</h1>
	 * @param dist ditancia em metros
	 */
	public static void forward(float dist){ //   s=theta*r
		float theta;
		theta=(dist*180)/(RAIO*3.14159265f);
		float position = rodaE.getTachoCount(); // posicao em graus
		//rodaD.rotate(theta); //se usar o rodaX.rotate() o robo da uma viradinha quando chega
		//rodaE.rotate(theta);
		Navigation.forward();
		while(rodaE.getTachoCount()<(position+theta)){
			Delay.msDelay(20);
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
	 * seta aceleracao de cada roda em graus/s^2 DA RODA
	 * @param motorD motor direito
	 * @param motorE motor esquerdo
	 */
	public static void setAcceleration(int motorD, int motorE){
		Navigation.rodaD.setAcceleration(motorD);
		Navigation.rodaE.setAcceleration(motorE);
	}

	/**
	 * seta velocidade em graus/s de cada roda
	 * @param veloD inteiro  em graus/seg da roda DIREITA
	 * @param veloE inteiro em graus/seg^2 da roda ESQUERDA
	 */
	public static void setVelocidade (int veloD, int veloE){
		Navigation.rodaD.setSpeed(veloD);
		Navigation.rodaE.setSpeed(veloE);
	}



	/**
	 * Parar o robo
	 */
	public static void stop() {
		rodaD.stop(true);
		rodaE.stop(false); // evita aquela jogadinha pro lado quando robo termina um movimento
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
	 * Metodo que converte o valor do PID para velocidades
	 * angulares das rodas em graus/seg
	 * @return float[]:<br>
	 * [0] Wd, graus/seg da roda direita;<br>
	 * [1] We, graus/seg da roda esquerda;
	 */
	private static float[] convertePara_WdWe() {
		float Wd = (2 * VELO_INI - DISTANCIA_ENTRE_RODAS * PID.getPID()) / (2 * RAIO);
		float We = (2 * VELO_INI + DISTANCIA_ENTRE_RODAS * PID.getPID()) / (2 * RAIO);

		return new float[] { Wd, We };

	}

	/**
	 * seta velocidade de cada roda de acordo com o PID em graus/s DA RODA
	 */
	public static void setVelocidade(){
		WdWe = convertePara_WdWe();
		Navigation.rodaD.setSpeed(WdWe[0]);
		Navigation.rodaE.setSpeed(WdWe[1]);
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

		}
	}
}
