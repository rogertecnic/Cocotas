package sek2016;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Sensors {

	private static EV3GyroSensor gyro; // nem preciso falar
	private static EV3UltrasonicSensor ultrasonic; // nem esse
	private static EV3ColorSensor dollColor; // verificar cor do boneco no modo RGB
	private static EV3ColorSensor floorColor; // verificar a cor do chao
	private static float[] angleSample,		// vetor contendo um elemento do gyro, angulo em graus
				distSample,     			// vetor contendo um elemento do ultrassom, a dist em metros
				rgbSample;					// vetor contendo 3 elementos de nivel de intensidade RGB de 0 a 1 do doll color

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
			rgbSample = new float [3];
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
	 * Metodo que verifica somente a posicao do gyro;
	 * @return valor float com a diferenca em graus da posicao da ultima zerada para a posicao atual
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
		if(Navigation.rodaD.isMoving() || Navigation.rodaE.isMoving()){
			Navigation.stop();
		}
		gyro.reset();
	}
	
	public static int Verificacordoll(){
		dollColor.getRGBMode().fetchSample(color, 0);
		
		
	}
}

