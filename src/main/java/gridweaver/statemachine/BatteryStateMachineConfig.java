package gridweaver.statemachine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachine
public class BatteryStateMachineConfig
        extends StateMachineConfigurerAdapter<BatteryStateMachine.State,String> {

    @Override
    public void configure(StateMachineStateConfigurer<BatteryStateMachine.State, String> states)
            throws Exception {

        states
            .withStates()
            .initial(BatteryStateMachine.State.IDLE)
            .states(java.util.EnumSet.allOf(BatteryStateMachine.State.class));
    }

    @Override
    public void configure(
            StateMachineTransitionConfigurer<BatteryStateMachine.State, String> transitions)
            throws Exception {

        transitions
            .withExternal()
            .source(BatteryStateMachine.State.IDLE)
            .target(BatteryStateMachine.State.CHARGING)
            .event("CHARGE")

            .and()
            .withExternal()
            .source(BatteryStateMachine.State.IDLE)
            .target(BatteryStateMachine.State.DISCHARGING)
            .event("DISCHARGE")

            .and()
            .withExternal()
            .source(BatteryStateMachine.State.CHARGING)
            .target(BatteryStateMachine.State.IDLE)
            .event("IDLE")

            .and()
            .withExternal()
            .source(BatteryStateMachine.State.DISCHARGING)
            .target(BatteryStateMachine.State.LOW_BATTERY)
            .event("LOW_BATTERY");
    }
}