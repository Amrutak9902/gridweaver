package gridweaver.statemachine;

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

    public void updateState(double batteryLevel, boolean charging) {

        if (batteryLevel <= 20) {
            currentState = State.LOW_BATTERY;
        } else if (charging) {
            currentState = State.CHARGING;
        } else if (batteryLevel > 20) {
            currentState = State.DISCHARGING;
        } else {
            currentState = State.IDLE;
        }
    }
}