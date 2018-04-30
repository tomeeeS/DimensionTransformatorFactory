import com.company.Phase;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Sajti Tamás
 */
public class PhaseTest {
    @Test
    public void test(){
        Phase phase = Phase.ASSEMBLE_DIMENSION_BREAKER;
        assertEquals(1, phase.getMinRobotWorkingTimeMs());
    }
}
