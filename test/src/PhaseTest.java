/**
 * Created by satsaat on 2018. 04. 24..
 */
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.company.Phase;
import org.junit.jupiter.api.Test;

public class PhaseTest {
    @Test
    public void test(){
        Phase phase = Phase.ASSEMBLE_DIMENSION_BREAKER;
        assertEquals(1, phase.getMinRobotWorkingTimeMs());
    }
}
