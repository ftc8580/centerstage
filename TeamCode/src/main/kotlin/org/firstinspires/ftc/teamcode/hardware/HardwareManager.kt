package org.firstinspires.ftc.teamcode.hardware



import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.config.CDConfig
import org.firstinspires.ftc.teamcode.util.Encoder
import org.firstinspires.ftc.teamcode.util.LynxModuleUtil
// Webcam stuff
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
class HardwareManager(private val config: CDConfig, val hardwareMap: HardwareMap) {
    lateinit var batteryVoltageSensor: VoltageSensor
    private lateinit var leftFrontMotor: DcMotorEx
    private lateinit var leftRearMotor: DcMotorEx
    private lateinit var rightRearMotor: DcMotorEx
    private lateinit var rightFrontMotor: DcMotorEx
    lateinit var motors: List<DcMotorEx>
    lateinit var webcam: WebcamName
    lateinit var cameraDirection: BuiltinCameraDirection
//    lateinit var grabberServo: Servo
//    lateinit var grabberExtendServo: Servo
    lateinit var leftEncoder: Encoder
    lateinit var rightEncoder: Encoder
    lateinit var frontEncoder: Encoder
    init {
        systemCheck(hardwareMap)
        initializeBatteryVoltageSensor(hardwareMap)
        initializeLynxModules(hardwareMap)
        initializeWheelLocalizers(hardwareMap)
        initializeDriveMotors(hardwareMap)
        initializeServos(hardwareMap)
        initializeWebcam(hardwareMap)
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
        leftEncoder = Encoder(
            hardware.get(DcMotorEx::class.java, config.driveMotors.leftFront)
        )
        rightEncoder = Encoder(
            hardware.get(DcMotorEx::class.java, config.driveMotors.rightFront)
        )
        frontEncoder = Encoder(
            hardware.get(DcMotorEx::class.java, config.driveMotors.rightRear)
        )
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

        motors = listOf(leftFrontMotor, leftRearMotor, rightRearMotor, rightFrontMotor)

        for (motor in motors) {
            val motorConfigurationType = motor.motorType.clone()
            motorConfigurationType.achieveableMaxRPMFraction = 1.0
            motor.motorType = motorConfigurationType

            // Set zero power behavior
            motor.zeroPowerBehavior = ZeroPowerBehavior.BRAKE
        }
    }
    private fun initializeWebcam(hardware: HardwareMap) {
        webcam = hardware.get(WebcamName::class.java, "webcam" )
        cameraDirection = BuiltinCameraDirection.BACK
    }
    private fun initializeServos(hardware: HardwareMap) {
        // TODO: For example purposes only, replace these with real hardware
//        grabberExtendServo = hardware.get(Servo::class.java, "servoExtend")
//        grabberServo = hardware.get(Servo::class.java, "servoGrab")
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