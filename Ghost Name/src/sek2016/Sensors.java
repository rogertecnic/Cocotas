package sek2016;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Sensors {

	private static EV3GyroSensor gyro;
	private static EV3UltrasonicSensor ultrasonic;
	private static EV3ColorSensor dollColor;
	private static EV3ColorSensor floorColor;
	private static float[] angleSample,		// vetor contendo um elemento do gyro, angulo em graus
				veloAngleSample,			// vetor contendo dois elemento do gyro, velo em graus/seg e angulo em graus
				distSample;					// vetor contendo um elemento do ultrassom, a dist em metros
	private static final float DIST_MIN = 0.07320f; // distancia minima do boneco
	private static final float DIST_MAX = 0.0953f; // distancia maxima do boneco
	
	/**
	 * Metodo que instancia todos os sensores.
	 * @param DC true ou false
	 * @param US true ou false
	 * @param FC true ou false
	 * @param GR true ou false
	 */
	public static void init(boolean DC,boolean US, boolean FC, boolean GR) {
		if(GR == true){
			gyro = new EV3GyroSensor(SensorPort.S2);
			angleSample = new float[1];
			veloAngleSample = new float [2];
		}
		if(US == true){
			ultrasonic = new EV3UltrasonicSensor(SensorPort.S3);
			distSample = new float[1];
		}
		if(FC == true){
			floorColor = new EV3ColorSensor(SensorPort.S4);
		}
		if(DC == true){
			dollColor = new EV3ColorSensor(SensorPort.S1);
		}
	}
	
	/**
	 * Metodo que verifica se tem algo na frente do ultrassom
	 * entre DIST_MIN e DIST_MAX;
	 * @return true se tiver;<br>
	 * 			false se nao tiver;
	 */
	public static boolean verificaObstaculo(){
		ultrasonic.getDistanceMode().fetchSample(distSample, 0);
		if((distSample[0] >= DIST_MIN) && (distSample[0] <= DIST_MAX))
			return true;
		else 
			return false;
	}
	
	/**
	 * Metodo que verifica a aceleracao angular e a posicao do gyro;
	 * @return array float[] com:<br>
	 * [0]: velocidade angular em graus/s<br>
	 * [1]: posicao angular em graus
	 */
	public static float[] getAllGyro() {
		gyro.getAngleAndRateMode().fetchSample(veloAngleSample, 0);
		return veloAngleSample;
	}
	
	/**
	 * Metodo que verifica somente a posicao do gyro;
	 * @return array float[] com:<br>
	 * [0]: posicao angular em graus
	 */
	public static float getAngle() {
		gyro.getAngleMode().fetchSample(angleSample, 0);
		return angleSample[0];
	}
	
	/**
	 * Modifica a posicao angular do gyro para 0 graus para recalibrar;<br>
	 * <h1>O ROBO DEVE ESTAR PARADO;
	 */
	public static void resetAngle(){
		gyro.reset();
	}
}
