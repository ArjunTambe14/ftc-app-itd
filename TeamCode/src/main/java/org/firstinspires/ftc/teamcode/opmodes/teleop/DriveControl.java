package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class DriveControl extends ControlModule {

    private Drivetrain drivetrain;
    private Odometry odometry;

    private ControllerMap.AxisEntry ax_drive_left_x;
    private ControllerMap.AxisEntry ax_drive_left_y;
    private ControllerMap.AxisEntry ax_drive_right_x;
    private ControllerMap.AxisEntry ax_slow;
    private ControllerMap.ButtonEntry dpad_up;

    private double forward_speed = 1;
    private double strafe_speed = 1.1;
    private double turn_speed = 0.75;

    private boolean field_centric = false;

    private double heading_delta = 0;
    private double heading_was = 0;
    private double heading_unwrapped = 0;
    private double wraparounds = 0;

    private double target_heading = 0;

    private double speed_dependent_steering = 0.5; //0 is no speed dependent steering, 1 is too much

    //private double heading_p = 0.009;

    public DriveControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        this.drivetrain = robot.drivetrain;
        this.odometry = robot.odometry;

        ax_drive_left_x = controllerMap.getAxisMap("drive:left_x", "gamepad1", "left_stick_x");
        ax_drive_left_y = controllerMap.getAxisMap("drive:right_y", "gamepad1", "left_stick_y");
        ax_drive_right_x = controllerMap.getAxisMap("drive:right_x", "gamepad1", "right_stick_x");
        ax_slow = controllerMap.getAxisMap("drive:slow", "gamepad1", "left_trigger");
        dpad_up = controllerMap.getButtonMap("drive:dpad_up", "gamepad1","dpad_up");

    }

    @Override
    public void init_loop(Telemetry telemetry) {
        odometry.Up();
    }

    @Override
    public void update(Telemetry telemetry) {

        drivetrain.updateHeading();

        double heading = drivetrain.getHeading();

        if (dpad_up.edge() == -1) {
            field_centric = !field_centric;
        }

        heading_delta = heading - heading_was;

        if (heading_delta > 320) {
            heading_delta -= 360;
            wraparounds -= 1;
        }
        if (heading_delta < -320) {
            heading_delta += 360;
            wraparounds += 1;
        }

        heading_unwrapped = -(heading + (wraparounds * 360));

        if (ax_drive_right_x.get() != 0) {
            heading_delta = 0;
        }

//        if (ax_drive_right_x.get() == 0) {
//            target_heading += (heading_unwrapped - target_heading) * 0.5;
//        }

        double slow = 1 - (ax_slow.get() / 3);

        double y = -ax_drive_left_y.get() * forward_speed * slow;
        double x = ax_drive_left_x.get() * strafe_speed * slow;
        double rx = ax_drive_right_x.get() * turn_speed * slow;

        //this makes turning slower as lateral motion gets faster
        rx *= (1 - (Math.sqrt(Math.pow(ax_drive_left_y.get(), 2) + Math.pow(ax_drive_left_x.get(), 2)) * speed_dependent_steering)); //pythagorean theorem

//        target_heading += rx * 11;

        double heading_radians = Math.toRadians(heading);

        double rotX = x * Math.cos(-heading_radians) - y * Math.sin(-heading_radians);
        double rotY = x * Math.sin(-heading_radians) + y * Math.cos(-heading_radians);

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

        if (field_centric) {
            drivetrain.move(rotY, rotX, rx, (heading_delta * 0.001), denominator);
        }
        else {
            //drivetrain.move(Math.pow(Math.abs(y), 1.6) * Math.signum(y),Math.pow(Math.abs(x), 1.6) * Math.signum(x),Math.pow(Math.abs(rx), 1.6) * Math.signum(rx) * 0.6,(heading_delta * 0.001));
            //drivetrain.move(Math.pow(Math.abs(y), 1.6) * Math.signum(y),Math.pow(Math.abs(x), 1.6) * Math.signum(x), ((target_heading - heading_unwrapped) * heading_p) + rx, 0);
            drivetrain.move(y, x, rx, (heading_delta * 0.001));
            //Math.pow(Math.abs(rx), 1.6) * Math.signum(rx) * 0.6

        }

        heading_was = heading;

        telemetry.addData("IMU Radians", Math.toRadians(heading));
        telemetry.addData("IMU", heading);

        telemetry.addData("Target Heading", target_heading);
        telemetry.addData("Unwrapped Heading", heading_unwrapped);

        telemetry.addData("rotX", rotX);
        telemetry.addData("rotY", rotY);
        telemetry.addData("denominator", denominator);

        telemetry.addData("Heading: ", heading);
//        telemetry.addData("Angular Velocity: ", drivetrain.getAngularVelocity());

        telemetry.addData("Field Centric",field_centric);


    }
    @Override
    public void stop() {
        super.stop();
        // drivetrain.closeIMU();
    }
}