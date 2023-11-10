package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

class SuspendSubsystem(hardware: HardwareManager, private val telemetry: MultipleTelemetry? = null) : SubsystemBase() {
    private val suspendMotor: DcMotorEx
    private val suspendServoLeft: Servo
    private val suspendServoRight: Servo

    init {
        suspendMotor = hardware.suspendMotor!!
        suspendServoLeft = hardware.suspendServoLeft!!
        suspendServoRight = hardware.suspendServoRight!!
    }

    fun deployHooks() {
        suspendServoLeft.position = HOOK_DEPLOYED_POSITION
        suspendServoRight.position = HOOK_DEPLOYED_POSITION
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