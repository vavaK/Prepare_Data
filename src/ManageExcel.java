import java.io.*;
import java.util.ArrayList;

public class ManageExcel {

    /**
     * Reads and stores the Excelsheet
     *
     * @param path The path to the Excelsheet
     * @return A string holding the Data out of the Excelsheet
     */
    public static ArrayList<String> readData(String path) {
        // An ArrayList in which the data is stored in
        ArrayList<String> dataOfSheets = new ArrayList<String>();
        try {
            // Initialize the File Reader and the Buffered Reader
            FileReader file = new FileReader(path);
            BufferedReader br = new BufferedReader(file);

            // Iterates through every row of the sheets and stores the data
            String currentRow;
            while ((currentRow = br.readLine()) != null) {
                dataOfSheets.add(currentRow);
            }

        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
        return dataOfSheets;
    }

    /**
     * Output of every event
     *
     * @param data The List of every event
     */
    public static void outputEventsObject(ArrayList<Measurement> data) {
        ArrayList<String> outputString = new ArrayList<>();
        outputString.add("Device,Date,CO2,CO2 Gradient,Humidity," +
                "Humidity Gradient,Temperatur,Temperatur Gradient,Amount People,Ventilation Needed," +
                "Door ventilation,Window 1 ventilation,Window 2 ventilation,Window 3 ventilation," +
                "Window 4 ventilation,Timeslot");
        for (Measurement event: data) {
            outputString.add(event.getDevice() + "," + event.getDate() + "," + event.getCoTwo() + "," +
                    event.getCoTwoGradient() + "," + event.getHumidity() + "," + event.getHumidityGradient() + "," +
                    event.getTemperature() + "," + event.getTemperatureGradient() + "," + event.getPeople() + "," +
                    event.isVentilationNeeded() + "," + event.getDoorVentilation() + "," +
                    event.getWindowOneVentilation() + "," + event.getWindowTwoVentilation() + "," +
                    event.getWindowThreeVentilation() + "," + event.getWindowFourVentilation() + "," +
                    event.getTimeslot());
        }

        // Creates a .xls file with every event
        try {
            File file = new File("Ausgabedaten\\dataAvgTrain.csv");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (String event: outputString) {
                bufferedWriter.write(event + "\n");
                System.out.println(event);
            }
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
