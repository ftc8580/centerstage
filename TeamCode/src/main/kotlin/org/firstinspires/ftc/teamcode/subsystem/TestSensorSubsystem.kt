package org.firstinspires.ftc.teamcode.subsystem

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.DistanceSensor
import com.qualcomm.robotcore.hardware.TouchSensor
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

class TestSensorSubsystem(hardware: HardwareManager) : SubsystemBase() {
    private var touchSensor: TouchSensor? = null
    private var colorSensor: ColorSensor? = null
    private var distanceSensor: DistanceSensor? = null

    init {
        touchSensor = hardware.touchSensor
        colorSensor = hardware.colorSensor
        distanceSensor = hardware.distanceSensor
    }
}