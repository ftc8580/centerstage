import com.qualcomm.robotcore.hardware.HardwareMap
import fakes.util.FakeHardwareMapFactory
import org.firstinspires.ftc.teamcode.config.CDConfig
import org.firstinspires.ftc.teamcode.hardware.HardwareManager
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class HardwareManagerTest {
    private val config = CDConfig()
    private val hardwareMap: HardwareMap = FakeHardwareMapFactory.getFakeHardwareMap("centerstage.xml")

    @Test
    fun `successfully creates HardwareManager instance`() {
        val hardwareManager = HardwareManager(config, hardwareMap)
        Assertions.assertNotNull(hardwareManager)
    }
}