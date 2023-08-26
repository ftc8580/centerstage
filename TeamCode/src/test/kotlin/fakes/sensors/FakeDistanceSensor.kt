package fakes.sensors

import com.qualcomm.robotcore.hardware.DistanceSensor
import com.qualcomm.robotcore.hardware.HardwareDevice
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

class FakeDistanceSensor : DistanceSensor {
    private var distance: Double = 0.0

    override fun getManufacturer(): HardwareDevice.Manufacturer = HardwareDevice.Manufacturer.Other

    override fun getDeviceName(): String = "Fake Distance Sensor"

    override fun getConnectionInfo(): String = ""

    override fun getVersion(): Int = 0

    override fun resetDeviceConfigurationForOpMode() {}

    override fun close() {}

    override fun getDistance(unit: DistanceUnit?): Double = distance

    fun setDistance(newDistance: Double) {
        distance = newDistance
    }
}