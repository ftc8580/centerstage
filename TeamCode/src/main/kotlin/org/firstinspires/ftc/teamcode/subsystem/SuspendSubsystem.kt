package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

class SuspendSubsystem(hardware: HardwareManager, private val telemetry: MultipleTelemetry? = null) : SubsystemBase() {
    private val suspendMotor: DcMotorEx
    private val suspendServo: Servo

    init {
        suspendMotor = hardware.suspendMotor!!
        suspendServo = hardware.suspendServo!!
    }

    fun deployHooks() {
        suspendServo.position = HOOK_DEPLOYED_POSITION
    }

    fun setMotorPower(power: Double) {
        suspendMotor.power = power
    }

    companion object {
        // TODO: Figure out what the actual positions are
        private const val HOOK_DEPLOYED_POSITION = 1.0
        private const val HOOK_RETRACTED_POSITION = 0.0
    }
}