package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

class DroneSubsystem(hardware: HardwareManager, private val telemetry: MultipleTelemetry? = null) : SubsystemBase() {
    private val droneServo: Servo

    init {
        droneServo = hardware.droneServo!!
    }

    fun launch() {
        telemetry?.addLine("Launching drone!")
        droneServo.position = LAUNCH_POSITION
    }

    companion object {
        private const val LAUNCH_POSITION = 0.35
    }
}