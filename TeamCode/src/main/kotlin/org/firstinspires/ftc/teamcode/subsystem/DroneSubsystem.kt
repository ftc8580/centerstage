package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.hardware.HardwareManager
import org.firstinspires.ftc.teamcode.util.CDRuntime
import org.firstinspires.ftc.teamcode.util.ServoUtil

class DroneSubsystem(hardware: HardwareManager, private val telemetry: MultipleTelemetry? = null) : SubsystemBase() {
    private val droneServo: Servo
    val rotationTime = ServoUtil.getSweepTimeMs(
        DroneSubsystem.START_POSITION,
        DroneSubsystem.LAUNCH_POSITION,
        0.0,
        1.0,
        5.0 * 270.0
    )

    init {
        droneServo = hardware.droneServo!!
    }

    fun launch() {
        telemetry?.addLine("Launching drone!")
        droneServo.position = LAUNCH_POSITION
    }

    fun reset() {
        droneServo.position = START_POSITION
    }

    companion object {
        private const val LAUNCH_POSITION = 0.35
        private const val START_POSITION = 0.5
    }
}