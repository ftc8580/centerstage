package org.firstinspires.ftc.teamcode.command.intake

import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

class IntakePixel(hardware: HardwareManager) : CommandBase() {
    private var motor: DcMotorEx

    init {
        motor = hardware.intakeMotor!!
    }


}