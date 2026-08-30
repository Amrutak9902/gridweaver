package gridweaver.statemachine;

import org.springframework.stereotype.Component;

@Component
public class BatteryStateMachine {

    public enum State {
        IDLE,
        CHARGING,
        DISCHARGING,
        LOW_BATTERY
    }

    private State currentState = State.IDLE;

    public State getCurrentState() {
        return currentState;
    }

    // Existing logic - keeps the old tests working
    public void updateState(
            double batteryLevel,
            boolean charging) {

        if (batteryLevel <= 20) {
            currentState = State.LOW_BATTERY;

        } else if (charging) {
            currentState = State.CHARGING;

        } else {
            currentState = State.DISCHARGING;
        }
    }

    // Week 2 logic - includes grid load
    public void updateState(
            double batteryLevel,
            boolean charging,
            double gridLoad) {

        if (batteryLevel <= 20) {
            currentState = State.LOW_BATTERY;

        } else if (gridLoad > 80) {
            currentState = State.DISCHARGING;

        } else if (charging) {
            currentState = State.CHARGING;

        } else {
            currentState = State.IDLE;
        }
    }
}