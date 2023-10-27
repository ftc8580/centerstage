package org.firstinspires.ftc.teamcode.subsystem

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.hardware.HardwareManager
import org.firstinspires.ftc.teamcode.util.ServoUtil

class IntakeSubsystem(hardware: HardwareManager) : SubsystemBase() {
    private val servo: Servo

    init {
        servo = hardware.deployIntakeServo!!
    }

    fun moveTo(position: Double) {
        servo.position = position
    }

    fun getMoveTimeMs(targetPosition: Double): Double {
        // TODO: account for bounded range
        return ServoUtil.getSweepTimeMs(servo.position, targetPosition)
    }
}