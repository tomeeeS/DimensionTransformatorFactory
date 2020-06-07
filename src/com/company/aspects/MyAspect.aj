package com.company.aspects;

import com.company.*;

aspect Print {
    int callNextPhaseCount = 0;

    pointcut callNextPhase( Robot robot ): call( void Controller.setNextPhase( Robot ) ) && args( robot );

    before( Robot robot ): callNextPhase( robot ) {
        ++callNextPhaseCount;
        Phase robotNextPhase = Phase.values()[ robot.getCurrentPhase().ordinal() + 1 ];
        System.out.printf( "Controller: setting robot %d's phase to %s count %d %n", robot.getId(), robotNextPhase, callNextPhaseCount );
    }
}