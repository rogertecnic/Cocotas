package testes_variados;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3GyroSensor;
import plano_B.Const;


public class MainTeste {
	public static EV3LargeRegulatedMotor rodaE= new EV3LargeRegulatedMotor(MotorPort.A);
	public static EV3LargeRegulatedMotor rodaD= new EV3LargeRegulatedMotor(MotorPort.B);
	public static EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S2);
	private static Thread threadPID = new Thread(new Testa_PID());
	private static final float graus_rad = (float)Math.PI/180,
			rad_graus = 180/(float)Math.PI;
	public static int aceleration = 200;

	public static void main(String[] Args){
		rodaE.setAcceleration(aceleration);
		rodaD.setAcceleration(aceleration);
		threadPID.setDaemon(true);
		threadPID.setName("threadPID");
		gyro.reset();
		threadPID.start();


			Teste_raio_roda.testaRaio(Const.LADO_MODULO_CENTRAL);

			//Teste_largura_robo.testa_largura(-90f);
	}
}
