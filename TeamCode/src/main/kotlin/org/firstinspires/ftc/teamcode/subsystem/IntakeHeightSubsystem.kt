package org.firstinspires.ftc.teamcode.subsystem

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.hardware.HardwareManager
import org.firstinspires.ftc.teamcode.util.ServoUtil

class IntakeHeightSubsystem(hardware: HardwareManager) : SubsystemBase() {
    private var servo: Servo? = null

    init {
        servo = hardware.deployIntakeServo
    }

    fun moveTo(position: Double) {
        TODO()
    }

    fun getMoveTimeMs(targetPosition: Double): Double {
        if (servo?.position == null) return 0.0

        return ServoUtil.getSweepTimeMs(servo!!.position, targetPosition)
    }
}