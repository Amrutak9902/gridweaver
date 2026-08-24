package gridweaver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import gridweaver.statemachine.BatteryStateMachine;

public class BatteryStateMachineTest {

    @Test
    void shouldStartInIdleState() {

        BatteryStateMachine machine = new BatteryStateMachine();

        assertEquals(
                BatteryStateMachine.State.IDLE,
                machine.getCurrentState()
        );
    }

    @Test
    void shouldChangeToChargingState() {

        BatteryStateMachine machine = new BatteryStateMachine();

        machine.updateState(80, true);

        assertEquals(
                BatteryStateMachine.State.CHARGING,
                machine.getCurrentState()
        );
    }

    @Test
    void shouldChangeToDischargingState() {

        BatteryStateMachine machine = new BatteryStateMachine();

        machine.updateState(80, false);

        assertEquals(
                BatteryStateMachine.State.DISCHARGING,
                machine.getCurrentState()
        );
    }

    @Test
    void shouldChangeToLowBatteryState() {

        BatteryStateMachine machine = new BatteryStateMachine();

        machine.updateState(15, false);

        assertEquals(
                BatteryStateMachine.State.LOW_BATTERY,
                machine.getCurrentState()
        );
    }
}