package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

//import com.outoftheboxrobotics.photoncore.PhotonCore;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ArmControl;
import org.firstinspires.ftc.teamcode.opmodes.teleop.ControlMgr;
//import org.firstinspires.ftc.teamcode.opmodes.teleop.RobotControl;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.util.Persistent;
import org.firstinspires.ftc.teamcode.util.Scheduler;
import org.firstinspires.ftc.teamcode.util.event.EventBus;
import org.opencv.android.OpenCVLoader;

@TeleOp(name = "!!Arm Control!!")
public class ArmControlTele extends LoggingOpMode
{
    // Robot and Controller Vars
    private Robot robot;
    private ControllerMap controllerMap;
    private ControlMgr controlMgr;

    private EventBus evBus;
    private Scheduler scheduler;
    static
    {
        OpenCVLoader.initDebug();
    }

    @Override
    public void init()
    {
        //PhotonCore.enable();
        super.init();
        robot = Robot.initialize(hardwareMap);
        evBus = robot.eventBus;
        scheduler = robot.scheduler;

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        controllerMap = new ControllerMap(gamepad1, gamepad2, evBus);

        controlMgr = new ControlMgr(robot, controllerMap);

        // Controller Modules
        controlMgr.addModule(new ArmControl("Arm Control"));
        controlMgr.initModules();

    }

    @Override
    public void init_loop()
    {
    }

    @Override
    public void start()
    {
        Persistent.clear();
        LoopTimer.resetTimer();
    }

    @Override
    public void loop()
    {
        // Loop Updaters
        controllerMap.update();
        try {
            controlMgr.loop(telemetry);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scheduler.loop();
        evBus.update();
        telemetry.update();
        LoopTimer.resetTimer();
    }

    @Override
    public void stop()
    {
        controlMgr.stop();
        super.stop();
    }
}
