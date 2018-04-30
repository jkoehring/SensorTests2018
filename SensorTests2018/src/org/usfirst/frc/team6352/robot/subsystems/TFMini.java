package org.usfirst.frc.team6352.robot.subsystems;

import org.usfirst.frc.team6352.robot.commands.ReportTFMini;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * A subsystem for accessing the TFMini "LIDAR".
 * 
 * The TFMini reports distance in centimeters.
 * 
 * The TFMini frame format is as follows:
 *   Byte 0 - 0x59 frame header
 *   Byte 1 - 0x59 frame header
 *   Byte 2 - Distance low byte
 *   Byte 3 - Distance high byte
 *   Byte 4 - Strength low byte
 *   Byte 5 - Strength high byte
 *   Byte 6 - Reserved
 *   Byte 7 - Original signal quality degree
 *   Byte 8 - Checksum = low byte of sum of preceding 8 bytes 
 */
public class TFMini extends Subsystem implements Runnable
{
	private SerialPort serialPort;
	
	private final int frameHeaderByte = 0x59;
	
	// Current values:
	private int distanceCm = -1;
	private int signalStrength = -1;
	private int originalSignalQualityDegree = -1;
	
	public TFMini(SerialPort serialPort)
	{
		this.serialPort = serialPort;
		
		new Thread(this).start();
	}

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public void initDefaultCommand()
	{
		setDefaultCommand(new ReportTFMini());
	}
	
	public void report()
	{
		SmartDashboard.putNumber("TFMini Distance Cm", distanceCm);
		SmartDashboard.putNumber("TFMini Signal Strength", signalStrength);
		SmartDashboard.putNumber("TFMini Original Quality", originalSignalQualityDegree);
	}

	@Override
	public void run()
	{
		// Reset serial port to empty buffers;
		serialPort.reset();
		
		int[] frame = new int[9];
		byte[] data = new byte[0];
		int frameIndex = 0;
		int dataIndex = 0;
		
		while (true)
		{
			if (dataIndex >= data.length)
			{
				data = serialPort.read(serialPort.getBytesReceived());
				if (0 == data.length)
				{
					continue;
				}
				dataIndex = 0;
			}
			
			// The next byte to process:
			int b = data[dataIndex++] & 0xff;
			
			// See if we are expecting the frame header:
			if (frameIndex < 2)
			{
				// Need to get a frame header byte:
				if (b == frameHeaderByte)
				{
					frame[frameIndex++] = b;
				}
				else
				{
					frameIndex = 0;
				}
				continue;
			}
			
			// We are past frame header - put current byte in frame:
			frame[frameIndex++] = b;
			if (frameIndex < frame.length)
			{
				continue;
			}
			
			// Reset indices for next frame:
			frameIndex = 0;
			
			// We have a complete frame.
			// Check the checksum:
			if (((frame[0] + frame[1] + frame[2] + frame[3] + frame[4] + frame[5] + frame[6] + frame[7]) & 0xff) != frame[8])
			{
				continue;
			}
			
			// Pull data out of the frame:
			distanceCm = frame[3] << 8 + frame[2];
			signalStrength = frame[5] << 8 + frame[4];
			originalSignalQualityDegree = frame[7];
		}
	}
}
