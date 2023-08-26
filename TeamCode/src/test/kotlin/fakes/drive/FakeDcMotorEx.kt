package fakes.drive

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorController
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareDevice.Manufacturer
import com.qualcomm.robotcore.hardware.PIDCoefficients
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import java.util.EnumMap


class FakeDcMotorEx : DcMotorEx {
    private var motorEnable = true
    private val pidCoefficients: EnumMap<DcMotor.RunMode, PIDCoefficients> = EnumMap(DcMotor.RunMode::class.java)
    private val pidfCoefficients: EnumMap<DcMotor.RunMode, PIDFCoefficients> = EnumMap(DcMotor.RunMode::class.java)
    private var motorPower = 0.0
    private var currentEncoderPosition = 0
    private var targetPositionTolerance = 0
    private var simpleVelocity = 0.0
    private var targetPosition = 0
    private var isBusy = false
    private var runMode: DcMotor.RunMode? = null

    override fun setMotorEnable() {
        motorEnable = true
    }

    override fun setMotorDisable() {
        motorEnable = false
    }

    override fun isMotorEnabled(): Boolean = motorEnable

    override fun setVelocity(angularRate: Double) {
        simpleVelocity = angularRate
    }

    override fun setVelocity(angularRate: Double, unit: AngleUnit?) {
        throw IllegalArgumentException("Not implemented")
    }

    override fun getVelocity(unit: AngleUnit?): Double {
        throw IllegalArgumentException("Not implemented")
    }

    override fun setPIDCoefficients(mode: DcMotor.RunMode, pidCoefficients: PIDCoefficients?) {
        this.pidCoefficients[mode] = pidCoefficients
    }

    @Throws(UnsupportedOperationException::class)
    override fun setPIDFCoefficients(mode: DcMotor.RunMode, pidfCoefficients: PIDFCoefficients?) {
        this.pidfCoefficients[mode] = pidfCoefficients
    }

    override fun setVelocityPIDFCoefficients(p: Double, i: Double, d: Double, f: Double) {}

    override fun setPositionPIDFCoefficients(p: Double) {}

    override fun getPIDCoefficients(mode: DcMotor.RunMode): PIDCoefficients? =pidCoefficients[mode]

    override fun getCurrent(unit: CurrentUnit?): Double = 0.0

    override fun getCurrentAlert(unit: CurrentUnit?): Double = 0.0

    override fun setCurrentAlert(current: Double, unit: CurrentUnit?) {}

    override fun isOverCurrent(): Boolean = false

    override fun getPIDFCoefficients(mode: DcMotor.RunMode): PIDFCoefficients? = pidfCoefficients[mode]

    override fun setTargetPositionTolerance(tolerance: Int) {
        targetPositionTolerance = tolerance
    }

    override fun getTargetPositionTolerance(): Int {
        return targetPositionTolerance
    }

    override fun getMotorType(): MotorConfigurationType = MotorConfigurationType()

    override fun setMotorType(motorType: MotorConfigurationType?) {}

    override fun getController(): DcMotorController? = null

    override fun getPortNumber(): Int = 0

    override fun setZeroPowerBehavior(zeroPowerBehavior: DcMotor.ZeroPowerBehavior?) {}

    override fun getZeroPowerBehavior(): DcMotor.ZeroPowerBehavior? = null

    override fun setPowerFloat() {}

    override fun getVelocity(): Double = simpleVelocity

    override fun getPowerFloat(): Boolean = false

    override fun setTargetPosition(position: Int) {
        targetPosition = position
    }

    override fun getTargetPosition(): Int = targetPosition

    override fun isBusy(): Boolean = isBusy

    fun setBusy(isBusy: Boolean) {
        this.isBusy = isBusy
    }

    override fun getCurrentPosition(): Int = currentEncoderPosition

    fun setCurrentPosition(currentMotorPosition: Int) {
        currentEncoderPosition = currentMotorPosition
    }

    @Deprecated("")
    fun setCurrentPosistion(currentMotorPosistion: Int) {
        currentEncoderPosition = currentMotorPosistion
    }

    override fun setMode(mode: DcMotor.RunMode) {
        runMode = mode
        isBusy = mode == DcMotor.RunMode.RUN_TO_POSITION
    }

    override fun getMode(): DcMotor.RunMode? = runMode

    override fun setDirection(direction: DcMotorSimple.Direction?) {}

    override fun getDirection(): DcMotorSimple.Direction? = null

    override fun setPower(power: Double) {
        motorPower = power
    }

    override fun getPower(): Double = motorPower

    override fun getManufacturer(): Manufacturer? = null

    override fun getDeviceName(): String? = null

    override fun getConnectionInfo(): String? = null

    override fun getVersion(): Int = 0

    override fun resetDeviceConfigurationForOpMode() {}

    override fun close() {}
}