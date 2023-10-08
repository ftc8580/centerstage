package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.DistanceSensor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.TouchSensor
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.config.CDConfig
import org.firstinspires.ftc.teamcode.util.Encoder
import org.firstinspires.ftc.teamcode.util.LynxModuleUtil
import java.lang.Exception

class HardwareManager(private val config: CDConfig, val hardwareMap: HardwareMap) {
    lateinit var batteryVoltageSensor: VoltageSensor
    private lateinit var leftFrontMotor: DcMotorEx
    private lateinit var leftRearMotor: DcMotorEx
    private lateinit var rightRearMotor: DcMotorEx
    private lateinit var rightFrontMotor: DcMotorEx
    lateinit var driveMotors: List<DcMotorEx>

    // Subsystem motors
    var viperMotor: DcMotorEx? = null
    var intakeMotor: DcMotorEx? = null
    var climbMotor: DcMotorEx? = null

    // Servos
    var bucketServo: Servo? = null
    var deployIntakeServo: Servo? = null

    // Dead wheels
    var leftEncoder: Encoder? = null
    var rightEncoder: Encoder? = null
    var frontEncoder: Encoder? = null

    // Sensors
    var touchSensor: TouchSensor? = null
    var colorSensor: ColorSensor? = null
    var distanceSensor: DistanceSensor? = null

    init {
        systemCheck(hardwareMap)
        initializeBatteryVoltageSensor(hardwareMap)
        initializeLynxModules(hardwareMap)
        initializeWheelLocalizers(hardwareMap)
        initializeDriveMotors(hardwareMap)
        initializeSubsystemMotors(hardwareMap)
        initializeServos(hardwareMap)
        initializeSensors(hardwareMap)
    }

    private fun systemCheck(hardware: HardwareMap)  {
        LynxModuleUtil.ensureMinimumFirmwareVersion(hardware)
    }

    private fun initializeBatteryVoltageSensor(hardware: HardwareMap) {
        batteryVoltageSensor = hardware.voltageSensor.iterator().next()
    }

    private fun initializeLynxModules(hardware: HardwareMap) {
        for (module in hardware.getAll(LynxModule::class.java)) {
            module.bulkCachingMode = LynxModule.BulkCachingMode.AUTO
        }
    }

    private fun initializeWheelLocalizers(hardware: HardwareMap) {
        val leftEncoderMotor = safelyGetHardware<DcMotorEx>(hardware, config.driveMotors.leftFront)
        val rightEncoderMotor = safelyGetHardware<DcMotorEx>(hardware, config.driveMotors.rightFront)
        val frontEncoderMotor = safelyGetHardware<DcMotorEx>(hardware, config.driveMotors.rightRear)

        // If any of the encoders are missing, don't initialize any of them
        if (leftEncoderMotor == null || rightEncoderMotor == null || frontEncoderMotor == null) return

        leftEncoder = Encoder(leftEncoderMotor)
        rightEncoder = Encoder(rightEncoderMotor)
        frontEncoder = Encoder(frontEncoderMotor)

        // TODO: reverse any encoders using Encoder.setDirection(Encoder.Direction.REVERSE)
    }

    private fun initializeDriveMotors(hardware: HardwareMap) {
        leftFrontMotor = hardware.get(DcMotorEx::class.java, config.driveMotors.leftFront)
        leftRearMotor = hardware.get(DcMotorEx::class.java, config.driveMotors.leftRear)
        rightRearMotor = hardware.get(DcMotorEx::class.java, config.driveMotors.rightRear)
        rightFrontMotor = hardware.get(DcMotorEx::class.java, config.driveMotors.rightFront)

        leftFrontMotor.direction = DcMotorSimple.Direction.FORWARD
        leftRearMotor.direction = DcMotorSimple.Direction.FORWARD
        rightRearMotor.direction = DcMotorSimple.Direction.REVERSE
        rightFrontMotor.direction = DcMotorSimple.Direction.REVERSE

        driveMotors = listOf(leftFrontMotor, leftRearMotor, rightRearMotor, rightFrontMotor)

        for (motor in driveMotors) {
            val motorConfigurationType = motor.motorType.clone()
            motorConfigurationType.achieveableMaxRPMFraction = 1.0
            motor.motorType = motorConfigurationType

            // Set zero power behavior
            motor.zeroPowerBehavior = ZeroPowerBehavior.BRAKE

            // Run without encoder since we're using dead wheels
            motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        }
    }

    private fun initializeSubsystemMotors(hardware: HardwareMap) {
        viperMotor = safelyGetHardware<DcMotorEx>(hardware, config.viperMotor)
        intakeMotor = safelyGetHardware<DcMotorEx>(hardware, config.intakeMotor)
        climbMotor = safelyGetHardware<DcMotorEx>(hardware, config.climbMotor)
    }

    private fun initializeServos(hardware: HardwareMap) {
        bucketServo = safelyGetHardware<Servo>(hardware, config.bucketServo)
        deployIntakeServo = safelyGetHardware<Servo>(hardware, config.deployIntakeServo)
    }

    private fun initializeSensors(hardware: HardwareMap) {
        touchSensor = safelyGetHardware<TouchSensor>(hardware, "touchSensor")
        colorSensor = safelyGetHardware<ColorSensor>(hardware, "colorSensor")
        distanceSensor = safelyGetHardware<DistanceSensor>(hardware, "distanceSensor")
    }

    private inline fun <reified T> safelyGetHardware(hardware: HardwareMap, deviceName: String?): T? {
        if (deviceName.isNullOrBlank()) return null

        return try {
            hardware.get(T::class.java, deviceName)
        } catch (e: Exception) {
            // Ignore exception and return null
            null
        }
    }

    val rawExternalHeading: Double
        get() = 0.0

    val externalHeadingVelocity: Double
        get() = 0.0

    fun setMotorPowers(frontLeft: Double, rearLeft: Double, rearRight: Double, frontRight: Double) {
        leftFrontMotor.power = frontLeft
        leftRearMotor.power = rearLeft
        rightRearMotor.power = rearRight
        rightFrontMotor.power = frontRight
    }
}