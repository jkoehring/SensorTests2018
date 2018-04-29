package org.usfirst.frc.team6352.robot.subsystems;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * A subsystem for accessing the TFMini "LIDAR".
 */
public class TFMini extends Subsystem implements Runnable
{
	private SerialPort serialPort;
	
	public TFMini(SerialPort serialPort)
	{
		this.serialPort = serialPort;
	}

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public void initDefaultCommand()
	{
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}
}
