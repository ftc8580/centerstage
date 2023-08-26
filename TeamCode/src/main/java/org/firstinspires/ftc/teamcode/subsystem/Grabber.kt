package org.firstinspires.ftc.teamcode.subsystem

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

// TODO: For example purposes only, remove when unneeded

class Grabber(hardware: HardwareManager) : SubsystemBase() {
    private var extendServo: Servo
    private var grabServo: Servo

    init {
        extendServo = hardware.grabberExtendServo
        grabServo = hardware.grabberServo

        extendServo.scaleRange(EXTEND_SCALE_RANGE_MIN, EXTEND_SCALE_RANGE_MAX)
        grabServo.scaleRange(GRAB_SCALE_RANGE_MIN, GRAB_SCALE_RANGE_MAX)

        extendServo.position = 0.0
        grabServo.position = 1.0
    }

    fun getExtendPosition(): Double = extendServo.position

    fun getGrabPosition(): Double = grabServo.position

    fun setExtendPosition(extendPositionTarget: Double) {
        extendServo.position = extendPositionTarget
    }

    fun setGrabPosition(grabPositionTarget: Double) {
        grabServo.position = grabPositionTarget
    }

    companion object {
        private const val EXTEND_SCALE_RANGE_MIN = 0.5
        private const val EXTEND_SCALE_RANGE_MAX = 0.9
        private const val GRAB_SCALE_RANGE_MIN = 0.3
        private const val GRAB_SCALE_RANGE_MAX = 0.75
    }
}