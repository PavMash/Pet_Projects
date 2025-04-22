import java.util.Scanner;

/**
 * Main class of the program.
 * Constants introduced here represent number of devices in system and initial attribute values of some device types.
 */
public class SmartHomeManagementSystem {

    static final int NUM_OF_DEVICES = 10;
    static final int INIT_ANGLE = 45;
    static final int INIT_TEMP = 20;

    /**
     * main method. Here I declare an array of all presented smart devices and initialize them with initial parameters.
     * Indexes of devices in array represent their IDs.
     * Also, here the commands are scanned and submitted for processing.
     * @param args default main parameter.
     */
    public static void main(String[] args) {
        SmartDevice[] devices = new SmartDevice[NUM_OF_DEVICES];
        for (int i = CommandManager.MIN_LIGHT_ID; i <= CommandManager.MAX_LIGHT_ID; i++) {
            devices[i] = new Light(SmartDevice.Status.ON, false, Light.BrightnessLevel.LOW, Light.LightColor.YELLOW);
            devices[i].setDeviceId(i);
        }
        for (int i = CommandManager.MIN_CAMERA_ID; i <= CommandManager.MAX_CAMERA_ID; i++) {
            devices[i] = new Camera(SmartDevice.Status.ON, false, false, INIT_ANGLE);
            devices[i].setDeviceId(i);
        }
        for (int i = CommandManager.MIN_HEATER_ID; i <= CommandManager.MAX_HEATER_ID; i++) {
            devices[i] = new Heater(SmartDevice.Status.ON, INIT_TEMP);
            devices[i].setDeviceId(i);
        }

        String command = "";
        CommandManager manager = new CommandManager();
        Scanner sc = new Scanner(System.in);
        while (!command.equals("end")) {
            command = sc.nextLine();
            manager.detectCommand(command, devices);
        }
    }
}


/**
 * Abstract class SmartDevice represents common features of smart devices from the task.
 * This class implements methods of interface Controllable.
 */
abstract class SmartDevice implements Controllable {
    private Status status;
    private int deviceId;
    private static int numberOfDevices;

    /**
     * Enumeration Status represents states(either on or off) of smart devices.
     */
    enum Status {
        OFF,
        ON;
    }

    /**
     * Constructor for SmartDevice class.
     * @param status on/off status of device.
     */
    public SmartDevice(Status status) {
        this.status = status;
    }

    public abstract String displayStatus();

    /**
     * Getter for DeviceID.
     * @return ID of smart device.
     */
    public int getDeviceId() {
        return deviceId;
    }

    /**
     * Setter for DeviceID.
     * @param deviceId ID of smart device.
     */
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
        numberOfDevices++;
    }

    /**
     * Getter for status.
     * @return on/off status of smart device.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Setter for status.
     * @param status on/off status of smart device.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Method checks current on/off status of device, and set it to off, if it is currently on.
     * @return true, if device was on before the method call; false, if it was off.
     */
    @Override
    public boolean turnOff() {
        if (this.isOn()) {
            this.setStatus(Status.OFF); return true;
        }
        return false;
    }

    /**
     * Method checks current on/off status of device, and set it to on, if it is currently off.
     * @return true, if device was off before the method call; false, if it was on.
     */
    @Override
    public boolean turnOn() {
        if (!this.isOn()) {
            this.setStatus(Status.ON); return true;
        }
        return false;
    }

    /**
     * Method checks, if device is currently on.
     * @return true, if device is currently on; false, if it is off.
     */
    @Override
    public boolean isOn() {
        return status == Status.ON;
    }

    /**
     * Method checks, if device's attribute can be changed.
     * All attribute setting methods of class CommandManager
     * (except startCharging() and stopCharging()) use this method.
     * @return true, if device's attribute can be changed (it is on); false, if it cannot be (device if off).
     */
    public boolean checkStatusAccess() {
        return this.isOn();
    }

}


/**
 * Class Camera represents common features of remote control cameras from the task.
 * This class inherits from abstract class SmartDevice,
 * and implements methods of interface Chargeable.
 */
class Camera extends SmartDevice implements Chargeable {
    private boolean charging;
    private boolean recording;
    private int angle;
    static final int MAX_CAMERA_ANGLE = 60;
    static final int MIN_CAMERA_ANGLE = -60;

    /**
     * Constructor for class Camera.
     * @param status on/off status of camera.
     * @param charging charging status of camera (charging or not).
     * @param recording recording status of camera (recording or not).
     * @param angle angle of camera.
     */
    public Camera(Status status, boolean charging, boolean recording, int angle) {
        super(status);
        this.charging = charging;
        this.recording = recording;
        this.angle = angle;
    }

    /**
     * Getter for angle.
     * @return angle of camera.
     */
    public int getAngle() {
        return angle;
    }

    /**
     * Setter for angle.
     * @param angle angle to be set for camera.
     * @return true, if angle is within permitted range and can be set as current angle;
     * false, if its value is restricted.
     */
    public boolean setCameraAngle(int angle) {
        if (MIN_CAMERA_ANGLE <= angle && angle <= MAX_CAMERA_ANGLE) {
            this.angle = angle;
            return true;
        }
        return false;
    }

    /**
     * Method checks current recording status of camera, and set it to true, if it is currently false.
     * @return true, if recording status of camera was false; false, if it was true.
     */
    public boolean startRecording() {
        if (!recording) {
            recording = true; return true;
        }
        return false;
    }

    /**
     * Method checks current recording status of camera, and set it to false, if it is currently true.
     * @return true, if recording status of camera was true; false, if it was false.
     */
    public boolean stopRecording() {
        if (recording) {
            recording = false; return true;
        }
        return false;
    }

    /**
     * Method checks current recording status of camera.
     * @return true, if recording status of camera is true; false, if it is false.
     */
    public boolean isRecording() {
        return recording;
    }

    /**
     * Method checks current charging status of camera.
     * @return true, if charging status of camera is true; false, if it is false.
     */
    @Override
    public boolean isCharging() {
        return charging;
    }

    /**
     * Method checks current charging status of camera, and set it to true, if it is currently false.
     * @return true, if charging status of camera was false; false, if it was true.
     */
    @Override
    public boolean startCharging() {
        if (!charging) {
            charging = true; return true;
        }
        return false;
    }

    /**
     * Method checks current charging status of camera, and set it to false, if it is currently true.
     * @return true, if charging status of camera was true; false, if it was false.
     */
    @Override
    public boolean stopCharging() {
        if (charging) {
            charging = false; return true;
        }
        return false;
    }

    /**
     * Method that takes current camera's attributes and display them as one string.
     * @return string that contains camera's current ID, on/off status, angle, charging status, and recording status.
     */
    @Override
    public String displayStatus() {
        return "Camera " + this.getDeviceId()
                + " is " + this.getStatus() + ", the angle is "
                + this.getAngle() + ", the charging status is "
                + this.isCharging() + ", and the recording status is "
                + this.isRecording() + ".";
    }
}


/**
 * Class Heater represents common features of remote control heaters form the task.
 * This class inherits from abstract class SmartDevice.
 */
class Heater extends SmartDevice {
    private int temperature;
    static final int MAX_HEATER_TEMP = 30;
    static final int MIN_HEATER_TEMP = 15;

    /**
     * Constructor for class Heater.
     * @param status on/off status of heater.
     * @param temperature temperature of heater.
     */
    public Heater(Status status, int temperature) {
        super(status);
        this.temperature = temperature;
    }

    /**
     * Getter for temperature.
     * @return current temperature of heater.
     */
    public int getTemperature() {
        return temperature;
    }

    /**
     * Setter for temperature of heater.
     * @param temperature temperature to be set for heater
     * @return true, if temperature is within permitted range and can be set as current temperature;
     * false, if its value is restricted.
     */
    public boolean setTemperature(int temperature) {
        if (MIN_HEATER_TEMP <= temperature && temperature <= MAX_HEATER_TEMP) {
            this.temperature = temperature;
            return true;
        }
        return false;
    }

    /**
     * Method that takes current heater's attributes and display them as one string.
     * @return string that contains heater's current ID, on/off status, and temperature.
     */
    @Override
    public String displayStatus() {
        return "Heater " + this.getDeviceId() + " is "
                + this.getStatus() + " and the temperature is "
                + this.getTemperature() + ".";
    }
}


/**
 * Class Light represents common features of remote control lights form the task.
 * This class inherits from abstract class SmartDevice,
 * and implements methods of interface Chargeable.
 */
class Light extends SmartDevice implements Chargeable {
    private boolean charging;
    private BrightnessLevel brightnessLevel;
    private LightColor lightColor;

    /**
     * Enumeration LightColor represents color that can be emitted by lights (either white or yellow).
     */
    enum LightColor {
        WHITE,
        YELLOW;
    }

    /**
     * Enumeration BrightnessLevel represents brightness of light emitted by lights (either low, medium or high).
     */
    enum BrightnessLevel {
        HIGH,
        MEDIUM,
        LOW;
    }

    /**
     * Constructor for class Light.
     * @param status on/off status of light.
     * @param charging charging status of light (charging or not).
     * @param brightnessLevel brightness level of light.
     * @param lightColor color of light.
     */
    public Light(Status status, boolean charging, BrightnessLevel brightnessLevel, LightColor lightColor) {
        super(status);
        this.charging = charging;
        this.brightnessLevel = brightnessLevel;
        this.lightColor = lightColor;
    }

    /**
     * Getter for lightColor.
     * @return current color of light.
     */
    public LightColor getLightColor() {
        return lightColor;
    }
    /**
     * Setter for lightColor.
     * @param lightColor color of light to be set.
     */
    public void setLightColor(LightColor lightColor) {
        this.lightColor = lightColor;
    }

    /**
     * Getter for brightnessLevel.
     * @return current brightness level of light.
     */
    public BrightnessLevel getBrightnessLevel() {
        return brightnessLevel;
    }

    /**
     * Setter for brightnessLevel.
     * @param brightnessLevel brightness level of light to be set.
     */
    public void setBrightnessLevel(BrightnessLevel brightnessLevel) {
        this.brightnessLevel = brightnessLevel;
    }

    /**
     * Method checks current charging status of light.
     * @return true, if charging status of light is true; false, if it is false.
     */
    @Override
    public boolean isCharging() {
        return charging;
    }

    /**
     * Method checks current charging status of light, and set it to true, if it is currently false.
     * @return true, if charging status of light was false; false, if it was true.
     */
    @Override
    public boolean startCharging() {
        if (!charging) {
            charging = true; return true;
        } else {
            return false;
        }
    }

    /**
     * Method checks current charging status of light, and set it to false, if it is currently true.
     * @return true, if charging status of light was true; false, if it was false.
     */
    @Override
    public boolean stopCharging() {
        if (charging) {
            charging = false; return true;
        } else {
            return false;
        }
    }

    /**
     * Method that takes current light's attributes and display them as one string.
     * @return string that contains light's current ID, on/off status, color, charging status, and brightness level.
     */
    @Override
    public String displayStatus() {
        return "Light " + this.getDeviceId() + " is "
                + this.getStatus() + ", the color is "
                + this.getLightColor() + ", the charging status is "
                + this.isCharging() + ", and the brightness level is "
                + this.getBrightnessLevel() + ".";
    }
}


/**
 * Interface Chargeable represents common behaviour features of chargeable devices from the task.
 */
interface Chargeable {
    boolean isCharging();
    boolean startCharging();
    boolean stopCharging();
}


/**
 * Interface Controllable represents common behaviour features of controllable devices from the task.
 */
interface Controllable {
    boolean turnOff();
    boolean turnOn();
    boolean isOn();
}


/**
 * Class CommandManager implements methods corresponding to input commands,
 * and methods that are used in command methods' implementation.
 * Constants introduced in this class represent numerical constants used in command validation,
 * command names, error messages, and success messages.
 */
class CommandManager {
    //command validation
    private static final int TEMPLATE1 = 1;
    private static final int TEMPLATE2 = 3;
    private static final int TEMPLATE3 = 4;
    private static final int THIRD_ARG = 3;
    static final int MIN_LIGHT_ID = 0;
    static final int MAX_LIGHT_ID = 3;
    static final int MIN_CAMERA_ID = 4;
    static final int MAX_CAMERA_ID = 5;
    static final int MIN_HEATER_ID = 6;
    static final int MAX_HEATER_ID = 9;

    //commands
    private static final String DISP_ALL = "DisplayAllStatus";
    private static final String TRN_ON = "TurnOn";
    private static final String TRN_OFF = "TurnOff";
    private static final String SRT_CHG = "StartCharging";
    private static final String STP_CHG = "StopCharging";
    private static final String ST_TEMP = "SetTemperature";
    private static final String ST_BRIGHT = "SetBrightness";
    private static final String ST_CLR = "SetColor";
    private static final String ST_ANG = "SetAngle";
    private static final String SRT_REC = "StartRecording";
    private static final String STP_REC = "StopRecording";
    private static final String END = "end";

    //errors
    private static final String INV_COM = "Invalid command";
    private static final String NOT_FOUND = "The smart device was not found";
    private static final String NON_CHG = " is not chargeable";
    private static final String NOT_HEAT = " is not a heater";
    private static final String WR_BRIGHT = "The brightness can only be one of \"LOW\", \"MEDIUM\", or \"HIGH\"";
    private static final String NOT_LIGHT = " is not a light";
    private static final String WR_CLR = "The light color can only be \"YELLOW\" or \"WHITE\"";
    private static final String NOT_CAM = " is not a camera";
    private static final String ALR_OFF = " is already off";
    private static final String ALR_ON = " is already on";
    private static final String STAT_CHG_PT_1 = "You can't change the status of the ";
    private static final String STAT_CHG_PT_2 = " while it is off";
    private static final String WR_ANG = " angle should be in the range [-60, 60]";
    private static final String ALR_REC = " is already recording";
    private static final String NOT_REC = " is not recording";
    private static final String ALR_CHG = " is already charging";
    private static final String NOT_CHG = " is not charging";
    private static final String WR_TEMP = " temperature should be in the range [15, 30]";

    //success messages
    private static final String IS_ON = " is on";
    private static final String IS_OFF = " is off";
    private static final String IS_CHG = " is charging";
    private static final String STPD_CHG = " stopped charging";
    private static final String TEMP_READY = " temperature is set to ";
    private static final String BRIGHT_READY = " brightness level is set to ";
    private static final String CLR_READY = " color is set to ";
    private static final String ANG_READY = " angle is set to ";
    private static final String IS_REC = " started recording";
    private static final String STPD_REC = " stopped recording";

    /**
     * Constructor for class CommandManager.
     */
    public CommandManager() { }

    /**
     * Method that takes whole command string, checks command name and number of attributes,
     * and call corresponding method or output error message.
     * @param command whole command string containing command name and attributes (if any).
     * @param devices array of devices, available for user.
     */
    public void detectCommand(String command, SmartDevice[] devices) {
        //Split command string into separated arguments.
        String[] args = command.split(" ");
        switch (args[0]) {
            case DISP_ALL:
                if (args.length == TEMPLATE1) {
                    this.displayAllStatus(devices); break;
                } else {
                    System.out.println(INV_COM); break;
                }
            case TRN_ON:
                if (args.length == TEMPLATE2) {
                    this.turnOn(devices, args[1], Integer.parseInt(args[2])); break;
                } else {
                    System.out.println(INV_COM); break;
                }
            case TRN_OFF:
                if (args.length == TEMPLATE2) {
                    this.turnOff(devices, args[1], Integer.parseInt(args[2])); break;
                } else {
                    System.out.println(INV_COM); break;
                }
            case SRT_CHG:
                if (args.length == TEMPLATE2) {
                    this.startCharging(devices, args[1], Integer.parseInt(args[2])); break;
                } else {
                    System.out.println(INV_COM); break;
                }
            case STP_CHG:
                if (args.length == TEMPLATE2) {
                    this.stopCharging(devices, args[1], Integer.parseInt(args[2])); break;
                } else {
                    System.out.println(INV_COM); break;
                }
            case ST_TEMP:
                if (args.length == TEMPLATE3) {
                    this.setTemp(devices, args[1], Integer.parseInt(args[2]), Integer.parseInt(args[THIRD_ARG])); break;
                } else {
                    System.out.println(INV_COM); break;
                }
            case ST_BRIGHT:
                if (args.length == TEMPLATE3) {
                    this.setBrightness(devices, args[1], Integer.parseInt(args[2]), args[THIRD_ARG]); break;
                } else {
                    System.out.println(INV_COM); break;
                }
            case ST_CLR:
                if (args.length == TEMPLATE3) {
                    this.setColor(devices, args[1], Integer.parseInt(args[2]), args[THIRD_ARG]); break;
                } else {
                    System.out.println(INV_COM); break;
                }
            case ST_ANG:
                if (args.length == TEMPLATE3) {
                    this.setAngle(devices, args[1], Integer.parseInt(args[2]), Integer.parseInt(args[THIRD_ARG]));
                    break;
                } else {
                    System.out.println(INV_COM); break;
                }
            case SRT_REC:
                if (args.length == TEMPLATE2) {
                    this.startRecord(devices, args[1], Integer.parseInt(args[2])); break;
                } else {
                    System.out.println(INV_COM); break;
                }
            case STP_REC:
                if (args.length == TEMPLATE2) {
                    this.stopRecord(devices, args[1], Integer.parseInt(args[2])); break;
                } else {
                    System.out.println(INV_COM); break;
                }
            case END:
                if (args.length == TEMPLATE1) {
                    break;
                } else {
                    System.out.println(INV_COM); break;
                }
            default: System.out.println(INV_COM);
        }
    }

    /**
     * Method that checks, if the device with such name and ID exists in system.
     * All command methods use this method.
     * @param name name of device.
     * @param id ID of device.
     * @return true, if such device exists in system; false, if it does not.
     */
    public boolean deviceCheck(String name, int id) {
        switch (name) {
            case "Camera":
                if (MIN_CAMERA_ID <= id && id <= MAX_CAMERA_ID) {
                    return true;
                }
                return false;
            case "Light":
                if (MIN_LIGHT_ID <= id && id <= MAX_LIGHT_ID) {
                    return true;
                }
                return false;
            case "Heater":
                if (MIN_HEATER_ID <= id && id <= MAX_HEATER_ID) {
                    return true;
                }
                return false;
            default: return false;
        }
    }

    /**
     * Method that output current status strings of all devices in system.
     * @param devices array of devices, available for user.
     */
    public void displayAllStatus(SmartDevice[] devices) {
        for (int i = 0; i < devices.length; i++) {
            System.out.println(devices[i].displayStatus());
        }
    }

    /**
     * Method that output checks existence of device,
     * turns device on, if it was off, or output error message, if it was on.
     * @param devices array of devices, available for user.
     * @param name name of device.
     * @param id ID of device.
     */
    public void turnOn(SmartDevice[] devices, String name, int id) {
        if (!deviceCheck(name, id)) {
            System.out.println(NOT_FOUND);
            return;
        }
        if (devices[id].turnOn()) {
            System.out.println(name + " " + id + IS_ON);
        } else {
            System.out.println(name + " " + id + ALR_ON);
        }
    }

    /**
     * Method that turns device off, if it was on, or output error message, if it was off.
     * @param devices array of devices, available for user.
     * @param name name of device.
     * @param id ID of device.
     */
    public void turnOff(SmartDevice[] devices, String name, int id) {
        if (!deviceCheck(name, id)) {
            System.out.println(NOT_FOUND);
            return;
        }
        if (devices[id].turnOff()) {
            System.out.println(name + " " + id + IS_OFF);
        }  else {
            System.out.println(name + " " + id + ALR_OFF);
        }
    }

    /**
     * Method that starts charging device, if it was not charging, or output error message, if it was already charging.
     * @param devices array of devices, available for user.
     * @param name name of device.
     * @param id ID of device.
     */
    public void startCharging(SmartDevice[] devices, String name, int id) {
        if (!deviceCheck(name, id)) {
            System.out.println(NOT_FOUND);
            return;
        }
        //Determine the device type to call type-specified startCharging() method.
        switch (name) {
            case "Heater":
                //Heaters are not chargeable.
                System.out.println(name + " " + id + NON_CHG);
                return;
            case "Camera":
                Camera cam = (Camera) devices[id];
                if (cam.startCharging()) {
                    System.out.println(name + " " + id + IS_CHG); break;
                } else {
                    System.out.println(name + " " + id + ALR_CHG); break;
                }
            case "Light":
                Light lit = (Light) devices[id];
                if (lit.startCharging()) {
                    System.out.println(name + " " + id + IS_CHG); break;
                } else {
                    System.out.println(name + " " + id + ALR_CHG); break;
                }
            default: break;
        }
    }

    /**
     * Method that stops charging device, if it was charging, or output error message, if it was not charging.
     * @param devices array of devices, available for user.
     * @param name name of device.
     * @param id ID of device.
     */
    public void stopCharging(SmartDevice[] devices, String name, int id) {
        if (!deviceCheck(name, id)) {
            System.out.println(NOT_FOUND);
            return;
        }
        //Determine the device type to call type-specified startCharging() method.
        switch (name) {
            case "Heater":
                //Heaters are not chargeable.
                System.out.println(name + " " + id + NON_CHG);
                return;
            case "Camera":
                Camera cam = (Camera) devices[id];
                if (cam.stopCharging()) {
                    System.out.println(name + " " + id + STPD_CHG); break;
                } else {
                    System.out.println(name + " " + id + NOT_CHG); break;
                }
            case "Light":
                Light lit = (Light) devices[id];
                if (lit.stopCharging()) {
                    System.out.println(name + " " + id + STPD_CHG); break;
                } else {
                    System.out.println(name + " " + id + NOT_CHG); break;
                }
            default: break;
        }
    }

    /**
     * Method that checks that if the device is heater (if not output, error message),
     * and sets new temperature value for it (if temperature is out of range, output error message).
     * @param devices array of devices, available for user.
     * @param name name of device.
     * @param id ID of device.
     * @param temp temperature of heater.
     */
    public void setTemp(SmartDevice[] devices, String name, int id, int temp) {
        if (!deviceCheck(name, id)) {
            System.out.println(NOT_FOUND);
            return;
        }
        if (!devices[id].checkStatusAccess()) {
            System.out.println(STAT_CHG_PT_1 + name + " " + id + STAT_CHG_PT_2);
            return;
        }
        if (!name.equals("Heater")) {
            System.out.println(name + " " + id + NOT_HEAT);
            return;
        }
        Heater heater = (Heater) devices[id];
        if (heater.setTemperature(temp)) {
            System.out.println(name + " " + id + TEMP_READY + temp);
        } else {
            System.out.println(name + " " + id + WR_TEMP);
        }
    }

    /**
     * Method that checks that if the device is light (if not, output error message),
     * and sets new brightness level for it (if brightness level is not appropriate, output error message).
     * @param devices array of devices, available for user.
     * @param name name of device.
     * @param id ID of device.
     * @param bright brightness level of light.
     */
    public void setBrightness(SmartDevice[] devices, String name, int id, String bright) {
        if (!deviceCheck(name, id)) {
            System.out.println(NOT_FOUND);
            return;
        }
        if (!devices[id].checkStatusAccess()) {
            System.out.println(STAT_CHG_PT_1 + name + " " + id + STAT_CHG_PT_2);
            return;
        }
        if (!name.equals("Light")) {
            System.out.println(name + " " + id + NOT_LIGHT);
            return;
        }
        Light lit = (Light) devices[id];
        //Checks content of string bright to set appropriate brightness level.
        switch (bright) {
            case "LOW":
                lit.setBrightnessLevel(Light.BrightnessLevel.LOW);
                System.out.println(name + " " + id + BRIGHT_READY + bright);
                break;
            case "MEDIUM":
                lit.setBrightnessLevel(Light.BrightnessLevel.MEDIUM);
                System.out.println(name + " " + id + BRIGHT_READY + bright);
                break;
            case "HIGH":
                lit.setBrightnessLevel(Light.BrightnessLevel.HIGH);
                System.out.println(name + " " + id + BRIGHT_READY + bright);
                break;
            default: System.out.println(WR_BRIGHT);
        }
    }

    /**
     * Method that checks that if the device is light (if not, output error message),
     * and sets new light color for it (if light color is not appropriate, output error message).
     * @param devices array of devices, available for user.
     * @param name name of device.
     * @param id ID of device.
     * @param clr color of light.
     */
    public void setColor(SmartDevice[] devices, String name, int id, String clr) {
        if (!deviceCheck(name, id)) {
            System.out.println(NOT_FOUND);
            return;
        }
        if (!devices[id].checkStatusAccess()) {
            System.out.println(STAT_CHG_PT_1 + name + " " + id + STAT_CHG_PT_2);
            return;
        }
        if (!name.equals("Light")) {
            System.out.println(name + " " + id + NOT_LIGHT);
            return;
        }
        Light lit = (Light) devices[id];
        //Checks content of string clr to set appropriate light color.
        switch (clr) {
            case "WHITE":
                System.out.println(name + " " + id + CLR_READY + clr);
                lit.setLightColor(Light.LightColor.WHITE);
                break;
            case "YELLOW":
                System.out.println(name + " " + id + CLR_READY + clr);
                lit.setLightColor(Light.LightColor.YELLOW);
                break;
            default:System.out.println(WR_CLR);
        }
    }

    /**
     * Method that checks that if the device is camera (if not, output error message),
     * and sets new angle for it (if angle is out of range, output error message).
     * @param devices array of devices, available for user.
     * @param name name of device.
     * @param id ID of device.
     * @param angle angle of camera.
     */
    public void setAngle(SmartDevice[] devices, String name, int id, int angle) {
        if (!deviceCheck(name, id)) {
            System.out.println(NOT_FOUND);
            return;
        }
        if (!devices[id].checkStatusAccess()) {
            System.out.println(STAT_CHG_PT_1 + name + " " + id + STAT_CHG_PT_2);
            return;
        }
        if (!name.equals("Camera")) {
            System.out.println(name + " " + id + NOT_CAM);
            return;
        }
        Camera cam = (Camera) devices[id];
        if (cam.setCameraAngle(angle)) {
            System.out.println(name + " " + id + ANG_READY + angle);
        } else {
            System.out.println(name + " " + id + WR_ANG);
        }
    }

    /**
     * Method that checks that if the device is camera (if not, output error message),
     * and starts recording on it (if camera was already recording, output error message).
     * @param devices array of devices, available for user.
     * @param name name of device.
     * @param id ID of device.
     */
    public void startRecord(SmartDevice[] devices, String name, int id) {
        if (!deviceCheck(name, id)) {
            System.out.println(NOT_FOUND);
            return;
        }
        if (!devices[id].checkStatusAccess()) {
            System.out.println(STAT_CHG_PT_1 + name + " " + id + STAT_CHG_PT_2);
            return;
        }
        if (!name.equals("Camera")) {
            System.out.println(name + " " + id + NOT_CAM);
            return;
        }
        Camera cam = (Camera) devices[id];
        if (cam.startRecording()) {
            System.out.println(name + " " + id + IS_REC);
        } else {
            System.out.println(name + " " + id + ALR_REC);
        }
    }

    /**
     * Method that checks that if the device is camera (if not, output error message),
     * and stops recording on it (if camera was not recording, output error message).
     * @param devices array of devices, available for user.
     * @param name name of device.
     * @param id ID of device.
     */
    public void stopRecord(SmartDevice[] devices, String name, int id) {
        if (!deviceCheck(name, id)) {
            System.out.println(NOT_FOUND);
            return;
        }
        if (!devices[id].checkStatusAccess()) {
            System.out.println(STAT_CHG_PT_1 + name + " " + id + STAT_CHG_PT_2);
            return;
        }
        if (!name.equals("Camera")) {
            System.out.println(name + " " + id + NOT_CAM);
            return;
        }
        Camera cam = (Camera) devices[id];
        if (cam.stopRecording()) {
            System.out.println(name + " " + id + STPD_REC);
        } else {
            System.out.println(name + " " + id + NOT_REC);
        }
    }
}
