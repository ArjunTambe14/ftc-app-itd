package org.firstinspires.ftc.teamcode.hardware;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

public class Lift {
    private DigitalChannel lift_limit;
    private DcMotorEx lift1;
    private DcMotorEx lift2;
    private Servo dumper;
    private Servo latch;
    private double lift1Target;
    private double liftCurrent;
    private double dumper_pos = 0;

    private boolean old_state = true;

    public Lift(DigitalChannel lift_limit, DcMotorEx lift1, DcMotorEx lift2, Servo dumper, Servo latch){
        this.lift_limit = lift_limit;
        this.lift1 = lift1;
        this.lift2 = lift2;
        this.dumper = dumper;
        this.latch = latch;
    }

    public void update() {
        liftCurrent = lift1.getCurrentPosition() * 5.23 / 3.7;
    }

    public void setLiftPower(double power){
        lift1.setPower(power);
        lift2.setPower(-power);
    }

    public void setDumper(double pos){
        dumper.setPosition(pos);
        dumper_pos = pos;
    }

    public void setLatch(double pos){
        latch.setPosition(pos);
    }

    public void setDumperState(boolean on){
        if (on != old_state) { //if the state changed
            if (on) {
                dumper.setPosition(dumper_pos);
            }
            old_state = on;
        }
        if (!on) {
            dumper.setPosition(0);
        }
    }

    public boolean getLift_limit(){
        return !lift_limit.getState();
    }

    public void resetLiftEncoder(){
        lift1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setLiftTarget(double pos){
        lift1Target = pos;
    }

    public double getLiftTarget(){
        return lift1Target;
    }

    public double getLiftCurrent(){
        return liftCurrent;
    }

}