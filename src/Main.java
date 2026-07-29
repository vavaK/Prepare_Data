import java.util.ArrayList;
import java.util.Collections;

public class Main {

    // The path that leads to the folder which hold the data
    private static final String prePath = "Eingabedaten\\";
    // An array which holds every data-sheet
    private static final String[] postPaths = {"DraginoTemp",
            "DraginoHum",
            "MilesightTemp",
            "MilesightHum",
            "CO2"};
    private static final String personsPath = "PersonsData.csv";
    private static final String personsBinaryPath = "PersonsDataBinaer.csv";

    // An array which holds every ventilation data-sheet
    private static final String[] ventilationPaths = {"Door.csv",
            "Window1.csv",
            "Window2.csv",
            "Window3.csv",
            "Window4.csv"};
    private static final boolean binary = false;
    private static final boolean test = false;
    private static final boolean equalize = true;

    // 0 -> 50% 0 Persons, 50% Rest | 1 -> Every Event is capped by the average
    private static final int reduceVariant = 1;

    // Amount of different person amounts with recorded measurements and how many classifications are missing
    // Test -> 4, Train -> 11
    private static final int peopleMissing = 11;
    private static final int peopleCap = 18;

    /** ArrayList that holds the Data
     * It has the Structure:
     * Device name, Date, CO2, CO2 Gradient, Humidity, Humidity Gradient, Temperature, Temperature Gradient,
     * Amount of people inside the room, False Positive, If Door is used for ventilation,
     * If Window 1 is used for ventilation, If Window 2 is used for ventilation, If Window 3 is used for ventilation,
     * If Window 4 is used for ventilation, Events since last people change */
    private static ArrayList<String> data = new ArrayList<>();
    private static ArrayList<Measurement> dataM = new ArrayList<>();

    /** ArrayList that holds the information about people inside the room */
    private static ArrayList<String> persons = new ArrayList<>();

    /** ArrayList that holds the information about ventilation inside the room */
    private static ArrayList<ArrayList<String>> ventilation = new ArrayList<>();

    /** ArrayList that holds the current Data
     * It has the Structure:
     * Device name, Date, CO2, CO2 Gradient, Humidity, Humidity Gradient, Temperature, Temperature Gradient,
     * Amount of people inside the room, False Positive, If Door is used for ventilation,
     * If Window 1 is used for ventilation, If Window 2 is used for ventilation, If Window 3 is used for ventilation,
     * If Window 4 is used for ventilation, Events since last people change */
    private static ArrayList<String> currentData = new ArrayList<>();
    
    public static void main(String[] args) {
        for (String path: postPaths) {
            if(test) {
                currentData = ManageExcel.readData(prePath + "Test\\" + path + "Test.csv");
            } else{
                currentData = ManageExcel.readData(prePath + "Train\\" + path + ".csv");
            }
            currentData = Process.structureDataString(currentData);
            data.addAll(currentData);
        }
        Collections.sort(data);
        dataM = Process.parseToMeasurement(data);
        if(binary) {
            persons = ManageExcel.readData(prePath + personsBinaryPath);
        } else {
            persons = ManageExcel.readData(prePath + personsPath);
        }
        dataM = Process.determineAmountPeople(dataM, persons);
        for (int i = 0; i < ventilationPaths.length; i++) {
            ArrayList<String> ventilationData = ManageExcel.readData(prePath + ventilationPaths[i]);
            ventilationData.remove(0);
            ventilation.add(ventilationData);
        }
        dataM = Process.roomVentilated(dataM, ventilation);
        dataM = Process.calcGradient(dataM);
        dataM = Process.flagFalsePositive(dataM);
        //dataM = Process.removeEvents(dataM);
        dataM = Process.timeSlots(dataM);
        dataM = Process.checkVentilationNeeded(dataM);
        if(equalize) {
            dataM = Process.evenOut(dataM, reduceVariant, peopleMissing, peopleCap);
        }
        ManageExcel.outputEventsObject(dataM);

    }
}