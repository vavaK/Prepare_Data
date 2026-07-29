import java.util.ArrayList;
import java.util.Arrays;

public class Process {

    // All Sundays
    private static final String[] sundays = {//"20260208", "20260215", "20260222", "20260301", "20260308", "20260315",
            //"20260322", "20260329", "20260405", "20260412", "20260419",
            "20260426", "20260503", "20260510", "20260517",
            "20260524", "20260531", "20260607", "20260614", "20260621", "20260628", "20260705", "20260712", "20260719",
            "20260726"};

    /**
     * Structures the String to the wanted form
     *
     * @param dataList An ArrayList which contains the data from the Excelsheet
     * @return The data but structured with clearer information
     */
    public static ArrayList<String> structureDataString(ArrayList<String> dataList) {
        StringBuilder replacement = new StringBuilder();
        String[] name = dataList.get(0).split(",");
        int dataSize = dataList.size();
        for(int i = 1; i < dataSize; i++) {
            // Adding the name of the device
            // Split the date and the measurement into different Strings
            String[] splitData = dataList.get(1).split(",");
            // Checks which form of measurement was taken and adds it
            replacement.append(name[1].replaceAll("\"", ""));
            switch (splitData[1].charAt(splitData[1].length() - 1)) {
                // CO2
                case 'm' -> replacement.append(",CO2");

                // Humidity
                case '%' -> replacement.append(",Humidity");

                // Temperature
                case 'C' -> replacement.append(",Temperature");
                default -> {
                }
            }
            // Add the date and the measurement
            replacement.append(",").append(splitData[0]);
            replacement.append(",").append(removeUnit(splitData[1]));
            // Adds the data String to the ArrayList
            dataList.add(replacement.toString());
            // Clears replacement and removes the last seen measurement
            replacement = new StringBuilder();
            dataList.remove(1);
        }
        dataList.remove(0);
        return dataList;
    }

    /**
     * A method to remove everything but the measurement itself
     *
     * @param information The raw data string with the measurement and the corresponding unit
     *
     * @return Only the measurement without the unit
     */
    public static String removeUnit(String information) {
        String[] splitInformation;
        // Checks which unit is in information
        // For temperatur and CO2 split using a regex
        switch(information.charAt(information.length() - 1)) {
            // CO2 and Temperature have the same structure
            case 'm':
            case 'C':
                splitInformation = information.split(" ");
                return splitInformation[0];
            // Humidity
            case '%':
                information = information.substring(0, information.length() - 1);
                return information;
            // In case there is not a compatible Unit
            default:
                return "";
        }
    }

    /**
     * The events are parsed into a measurement Object
     *
     * @param dataList An Arraylist with all events
     * @return An ArrayList which contains every event as an object
     */
    public static ArrayList<Measurement> parseToMeasurement(ArrayList<String> dataList) {
        // The ArrayList which is returned
        ArrayList<Measurement> measurementList = new ArrayList<>();
        // Temporary ArrayList to separate the measurements from the Milesight and the Dragino
        ArrayList<String> tempDrag = new ArrayList<>();
        ArrayList<String> tempMile= new ArrayList<>();
        // The Timestamps from the Milesight are synchronised
        dataList = syncTimestamp(dataList);
        // The devices are split
        for (String event: dataList) {
            if(event.charAt(0) == 'M') {
                tempMile.add(event);
            } else {
                tempDrag.add(event);
            }
        }
        // Milesight events are divided into thirds because there are 3 different measurements taken
        int events = tempMile.size() / 3;
        for (int i = 0; i < events; i++) {
            // Get the Measurements
            String[] mileCO = tempMile.get(i).split(",");
            String[] mileHum = tempMile.get(i + events).split(",");
            String[] mileTem = tempMile.get(i + 2 * events).split(",");
            // Create the Milesight object
            measurementList.add(new Measurement(mileCO[0], dateToLong(mileCO[2]), Integer.parseInt(mileCO[3]),
                    0.0, Double.parseDouble(mileHum[3]), 0.0,
                    Double.parseDouble(mileTem[3]), 0.0, 0, 0, false,
                    false, 0, 0, 0, 0,
                    0));
        }
        // Dragino events are cut in half because there are 2 different measurements taken
        events = tempDrag.size() / 2;
        for (int i = 0; i < events; i++) {
            // Get the Measurements
            String[] dragHum = tempDrag.get(i).split(",");
            String[] dragTem = tempDrag.get(i + events).split(",");
            // Create the Dragino object
            measurementList.add(new Measurement(dragHum[0], dateToLong(dragHum[2]), 0, 0.0,
                    Double.parseDouble(dragHum[3]), 0.0, Double.parseDouble(dragTem[3]),
                    0.0, 0, 0, false, false, 0,
                    0, 0, 0, 0));
        }
        return measurementList;
    }

    /**
     * Because sometimes the Measurement of the Milesight is asynchron it gets re-dubbed
     *
     * @param data The ArrayList with every event
     * @return The ArrayList but the events are synchron
     */
    private static ArrayList<String> syncTimestamp(ArrayList<String> data) {
        ArrayList<String> tempDrag = new ArrayList<>();
        ArrayList<String> tempMile= new ArrayList<>();
        // Split the events based on their different devices
        for(String event: data) {
            if(event.charAt(0) == 'D') {
                tempDrag.add(event);
            } else {
                tempMile.add(event);
            }
        }

        // Because there are 3 different measurements we divide by 3 to get the amount of measurements
        int events = tempMile.size() / 3;
        for(int i = 0; i < events; i++) {
            String[] tempEventHum = tempMile.get(i + events).split(",");
            String[] tempEventTem = tempMile.get(i + 2 * events).split(",");

            // If the timestamps of the CO2 and Humidity event are not equal they will be synchronised
            if(!tempMile.get(i).split(",")[2].equals(tempMile.get(i + events).split(",")[2])) {
                tempMile.set(i + events, tempEventHum[0] + "," + tempEventHum[1] + "," +
                        tempMile.get(i).split(",")[2] + "," + tempEventHum[3]);
            }
            // If the timestamps of the CO2 and Temperature event are not equal they will be synchronised
            if(!tempMile.get(i).split(",")[2].equals(tempMile.get(i + 2 * events).split(",")[2])) {
                tempMile.set(i + 2 * events, tempEventTem[0] + "," + tempEventTem[1] + "," +
                        tempMile.get(i).split(",")[2] + "," + tempEventTem[3]);
            }
        }
        tempMile.addAll(tempDrag);
        return tempMile;
    }

    /**
     * Converts the date of an event to a long number
     *
     * @param dateString The date as a String
     * @return The date as a number
     */
    public static long dateToLong(String dateString){
        // Checks if there are one or two dates in event
        dateString = dateString.replaceAll("[- :]", "");

        return Long.parseLong(dateString);
    }

    /**
     * Determines the amount of people that are in the room to an event
     *
     * @param data The List of every event
     * @param date The list of every event's date as a number
     * @return The List of all events with the amount of people calculated
     */
    public static ArrayList<Measurement> determineAmountPeople(ArrayList<Measurement> data, ArrayList<String> date){
        // Check for every event if there is a timeslot in which this event occurs
        for (int i = 1; i < data.size(); i++) {
            for (int j = 1; j < date.size(); j++) {
                String[] period = date.get(j).split(",");
                if(data.get(i).getDate() >= dateToLong(period[0]) && data.get(i).getDate() <= dateToLong(period[2])) {
                    data.get(i).setPeople(Integer.parseInt(period[1].split("p")[0]));
                }
            }
        }
        return data;
    }

    /**
     * Calculates the gradient of the current event
     * 1.0-> The Measurement doesn't change
     * <1.0-> The Measurement decreases
     * >1.0-> The Measurement increases
     *
     * @param data An ArrayList with every event
     * @return The ArrayList but the gradient is appended
     */
    public static ArrayList<Measurement> calcGradient(ArrayList<Measurement> data) {
        ArrayList<Measurement> tempMile = new ArrayList<>();
        for (Measurement measurement : data) {
            if(measurement.getDevice().charAt(0) == 'M') {
                tempMile.add(measurement);
            }
        }
        for(int i = 0; i < data.size(); i++) {
            // The first event has a gradient of 1
            if (i == 0 || i == tempMile.size()) {
                data.get(i).setCoTwoGradient(1.0);
                data.get(i).setHumidityGradient(1.0);
                data.get(i).setTemperatureGradient(1.0);
            } else {
                // If previous measurement was 0 the gradient is calculated
                if(data.get(i - 1).getCoTwo() == 0) {
                    data.get(i).setCoTwoGradient((double)data.get(i).getCoTwo() / 0.001);
                // If current measurement is 0 the gradient is calculated
                } else if(data.get(i).getCoTwo() == 0) {
                    data.get(i).setCoTwoGradient(0.001 / (double)data.get(i - 1).getCoTwo());
                // The gradient is calculated
                } else {
                    data.get(i).setCoTwoGradient((double)data.get(i).getCoTwo() / (double)data.get(i - 1).getCoTwo());
                }
                // If previous measurement was 0 the gradient is calculated
                if(data.get(i - 1).getHumidity() == 0) {
                    data.get(i).setHumidityGradient(data.get(i).getHumidity() / 0.001);
                // If current measurement is 0 the gradient is calculated
                } else if(data.get(i).getHumidity() == 0) {
                    data.get(i).setHumidityGradient(0.001 / data.get(i - 1).getHumidity());
                // The gradient is calculated
                } else {
                    data.get(i).setHumidityGradient(data.get(i).getHumidity() / data.get(i - 1).getHumidity());
                }
                // If previous measurement was 0 the gradient is calculated
                if(data.get(i - 1).getTemperature() == 0) {
                    data.get(i).setTemperatureGradient(data.get(i).getTemperature() / 0.001);
                // If current measurement is 0 the gradient is calculated
                } else if(data.get(i).getTemperature() == 0) {
                    data.get(i).setTemperatureGradient(0.001 / data.get(i - 1).getTemperature());
                // The gradient is calculated
                } else {
                    data.get(i).setTemperatureGradient(data.get(i).getTemperature() / data.get(i - 1).getTemperature());
                }
            }
        }
        return data;
    }

    /**
     * Checks the ArrayList if there is an event that does lead to a CO2 Spike but can't be explained because there is
     * no entry of someone in the Room. The explanation for that is: Someone entered the room but did not "sign in".
     *
     * @param data An ArrayList of every event
     * @return The ArrayList but every false positive is removed
     */
    public static ArrayList<Measurement> flagFalsePositive(ArrayList<Measurement> data) {
        // Two counters
        int counter = 0;
        int amountEvents = data.size();
        // The threshold CO2 value
        double threshold = calcMean(data);
        System.out.println(threshold);
        // False positive detected
        boolean falsePositive = false;
        // The room was recently used
        boolean recentlyUsed = false;
        // The timeslots in which a false positive occurs
        ArrayList<Long[]> timeSlots = new ArrayList<>();

        // Detects a false positive
        for (int i = 0; i < amountEvents; i++) {
            // Only iterate through the Milesight measurements because only there are CO2 values be measured
            // We only note CO2 because it is a good indicator
            if(data.get(i).getDevice().charAt(0) == 'M') {
                int amountPeople = data.get(i).getPeople();
                int coTwo = data.get(i).getCoTwo();
                double gradient = data.get(i).getCoTwoGradient();
                long date = data.get(i).getDate();
                // If the amount of people in the event is greater than 0 the room was recently used
                if(amountPeople > 0) {
                    // If there was a false positive ends
                    if(falsePositive) {
                        falsePositive = false;
                        timeSlots.get(counter)[1] = date;
                        counter += 1;
                    }
                    recentlyUsed = true;
                    // If the CO2 value has fallen under the threshold again the false positive ends
                } else if((coTwo <= threshold && gradient < 1.1)) {
                    if(falsePositive) {
                        falsePositive = false;
                        timeSlots.get(counter)[1] = date;
                        counter += 1;
                    }
                    recentlyUsed = false;
                }
                // If there are no more persons in the room a false positive starts
                // or the CO2 value increases notably and nobody is in the room it is a false positive
                else if(!recentlyUsed && (coTwo > threshold || gradient >= 1.1)) {
                    if (!falsePositive) {
                        timeSlots.add(new Long[]{date, null});
                        falsePositive = true;
                    }
                }
            }
        }
        for(int j = 0; j < data.size(); j++) {
            // Set the value of false positive
            data.get(j).setFalsePositive(checkFalsePositive(data.get(j), timeSlots));
        }
        return data;
    }

    private static boolean checkFalsePositive(Measurement data, ArrayList<Long[]> timeSlots) {
        for (Long[] timeSlot: timeSlots) {
            if(data.getDate() >= timeSlot[0] && data.getDate() <= timeSlot[1]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the mean value of all CO2 events recorded by Milesight
     *
     * @param data An ArrayList with every event
     * @return The mean CO2 value
     */
    private static double calcMean(ArrayList<Measurement> data) {
        double mean = 0.0;
        int counter = 0;

        for (Measurement event : data) {
            if(event.getDevice().charAt(0) == 'M') {
                mean += event.getCoTwo();
                counter += 1;
            }
        }
        return mean / counter;
    }

    /**
     * Removes every event which is either a false positive or happens during the week without a person inside the room
     *
     * @param data The whole List of Arrays
     * @return The List without the people and door sensors entries
     */
    public static ArrayList<Measurement> removeEvents(ArrayList<Measurement> data) {
        for (int i = 0; i < data.size(); i++) {
            // If the event is a false positive, or it is not a sunday and there are 0 Persons in the room it is removed
            if (data.get(i).isFalsePositive() ||
                    (!isSunday(data.get(i).getDate() + "") && data.get(i).getPeople() == 0)) {
                data.remove(data.get(i));
                i -= 1;
            }
        }
        return data;
    }

    /**
     * Checks if the given Day is a sunday because it is a good default Day and nobody should enter the room
     *
     * @param date The day which is checked
     * @return Weither or not the day is a Sunday
     */
    private static Boolean isSunday(String date) {
        for (String day: sundays) {
            if (date.contains(day)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts how often events occur in succession with the same attributes
     *
     * @param data An ArrayList with every event
     * @return The ArrayList but the timeslots are appended
     */
    public static ArrayList<Measurement> timeSlots(ArrayList<Measurement> data) {
        int counter = 1;
        Measurement event;
        Boolean mileMinute;
        Boolean mileHour;
        Boolean dragMinute;
        Boolean dragHour;
        long time;
        long prevTime;

        for(int i = 0; i < data.size(); i++) {
            event = data.get(i);
            if(i == 0) {
                data.get(i).setTimeslot(counter);
            } else {
                // The time of the current event
                time = event.getDate();
                // The time of the previous event
                prevTime = data.get(i - 1).getDate();
                // Checks if the events are between 8 and 12 or ,because an hour only counts to 60, 48 and 52 Minutes apart
                mileMinute = (time - prevTime <= 1200 && time - prevTime >= 800);
                mileHour = (time - prevTime <= 5200 && time - prevTime >= 4800);
                // Checks if the events are between 18 and 22 or ,because an hour only counts to 60, 58 and 62 Minutes apart
                dragMinute = (time - prevTime <= 2200 && time - prevTime >= 1800);
                dragHour = (time - prevTime <= 6200 && time - prevTime >= 5800);

                // The counter continues if the event occurs in succession
                if(((mileMinute || mileHour) && event.getDevice().charAt(0) == 'M' ||
                        (dragMinute || dragHour) && event.getDevice().charAt(0) == 'D') &&
                        event.getPeople() == data.get(i - 1).getPeople()) {
                    counter += 1;
                } else {
                    counter = 1;
                }
                data.get(i).setTimeslot(counter);
            }
        }
        return data;
    }

    /**
     * Checks if Ventilation should be considered
     *
     * @param data An ArrayList with every event
     * @return The ArrayList
     */
    public static ArrayList<Measurement> checkVentilationNeeded(ArrayList<Measurement> data) {
        for (Measurement event : data) {
            if(event.getTemperature() <= 26.0 && (event.getHumidity() >= 40.0 && event.getHumidity() <= 60.0) &&
                    (event.getCoTwo() <= 1500)) {
                event.setVentilationNeeded(false);
            } else {
                event.setVentilationNeeded(true);
            }
        }
        return data;
    }

    /**
     * Reduces the event List so the events are equal distributed based on the amount of people
     *
     * @param data An ArrayList with every event
     * @param version Which Version should be used 0-> equal, else-> average
     *
     * @return The reduced list of events
     */
    public static ArrayList<Measurement> evenOut(ArrayList<Measurement> data, int version, int peopleMissing, int peopleCap) {
        // Counter for every Event for each amount of persons
        int[] equalCounterMile = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] equalCounterDrag = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        ArrayList<Measurement> equalDataMile = new ArrayList<>();
        ArrayList<Measurement> equalDataDrag = new ArrayList<>();

        // Counts the events with the correlating people amount
        for (Measurement event: data) {
            if(event.getDevice().equals("Milesight")) {
                equalCounterMile[event.getPeople()] += 1;
                equalDataMile.add(event);
            } else {
                equalCounterDrag[event.getPeople()] += 1;
                equalDataDrag.add(event);
            }
        }

        equalCounterMile = reduceCounter(equalCounterMile, version, peopleMissing, peopleCap);
        equalCounterDrag = reduceCounter(equalCounterDrag, version, peopleMissing, peopleCap);

        equalDataMile = reduceEvents(equalDataMile, equalCounterMile);
        equalDataMile.addAll(reduceEvents(equalDataDrag, equalCounterDrag));

        return equalDataMile;
    }

    private static int[] reduceCounter(int[] equalCounter, int version, int peopleMissing, int peopleCap) {
        // Counter for how many events exist with Amount People > 0
        int counter;
        // The average of all people counter
        int average;

        // Overall Counter + average of all events with amount people > 0
        counter = Arrays.stream(equalCounter).sum();
        counter -= equalCounter[0];
        average = counter / (peopleCap - peopleMissing);

        // Reducing the counter of the events
        if(version == 0) {
            equalCounter[0] = counter;
        } else {
            for (int i = 0; i < equalCounter.length; i++) {
                if(equalCounter[i] > average) {
                    equalCounter[i] = average;
                }
            }
        }
        return equalCounter;
    }

    private static ArrayList<Measurement> reduceEvents(ArrayList<Measurement> data, int[] equalCounter) {
        ArrayList<Measurement> equalData = new ArrayList<>();
        // Reducing the actual events
        for (Measurement event: data) {
            if(equalCounter[event.getPeople()] > 0) {
                equalData.add(event);
                equalCounter[event.getPeople()] -= 1;
            }
        }
        return equalData;
    }



    /**
     * Determines if the event is in a timeslot where the room was ventilated
     *
     * @param data An ArrayList with every event
     * @param ventilation An ArrayList with every ventilation event of every sensor
     * @return  An ArrayList with every updated event
     */
    public static ArrayList<Measurement> roomVentilated(ArrayList<Measurement> data,
                                                        ArrayList<ArrayList<String>> ventilation) {
        // A List of all timeslots in which the room was ventilated
        ArrayList<ArrayList<Long[]>> timeslots = ventilationTimeslots(ventilation);
        // // Loop to cover every sensor
        for (int i = 0; i < timeslots.size(); i++) {
            // Loop to cover every event
            for (int j = 0; j < data.size(); j++) {
                // Loop to check if any event lies in within a timeslot
                for(Long[] timeslot: timeslots.get(i)) {
                    // Checks if any event lies in within a timeslot
                    if(timeslot[0] <= data.get(j).getDate() && timeslot[1] >= data.get(j).getDate()) {
                        // Switch to check which Sensor ist activated
                        switch (i) {
                            case 0:
                                data.get(j).setDoorVentilation(1);
                                break;
                            case 1:
                                data.get(j).setWindowOneVentilation(1);
                                break;
                            case 2:
                                data.get(j).setWindowTwoVentilation(1);
                                break;
                            case 3:
                                data.get(j).setWindowThreeVentilation(1);
                                break;
                            default:
                                data.get(j).setWindowFourVentilation(1);
                        }
                    }
                }
            }
        }
        return data;
    }

    /**
     * Determines every ventilation timeslots
     *
     * @param ventilation  An ArrayList with every ventilation event
     * @return A List of all timeslots in which the room was ventilated
     */
    private static ArrayList<ArrayList<Long[]>> ventilationTimeslots(ArrayList<ArrayList<String>> ventilation) {
        // Stores the timeslots in whom was ventilated
        ArrayList<ArrayList<Long[]>> timeslots = new ArrayList<>();
        boolean start = false;
        // Loop to cover every sensor
        for (int i = 0; i < ventilation.size(); i++) {
            timeslots.add(new ArrayList<>());
            // Loop to cover every measurement
            for (String sensordata : ventilation.get(i)) {
                // The current timestamp
                long timestamp = dateToLong(sensordata.split(",")[0]);
                // The current value -> 0 Sensor closed,
                // 0<x<1 Sensor was only opened a brief amount of time, 1 Sensor open
                double value = Double.parseDouble(sensordata.split(",")[1]);
                // Checks if Sensor is open
                if(value == 1.00 && !start) {
                    timeslots.get(i).add(new Long[]{timestamp, null});
                    start = true;
                // Checks if Sensor is cloesed again
                } else if (value != 1.00 && start) {
                    timeslots.get(i).get(timeslots.get(i).size() - 1)[1] = timestamp;
                    start = false;
                }
            }
        }
        return timeslots;
    }
}
