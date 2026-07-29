public class Measurement {
    private String device;
    private long date;
    private int coTwo;
    private double coTwoGradient;
    private double humidity;
    private double humidityGradient;
    private double temperature;
    private double temperatureGradient;
    private int people;
    private int timeslot;
    private boolean falsePositive;
    private boolean ventilationNeeded;
    private int doorVentilation;
    private int windowOneVentilation;
    private int windowTwoVentilation;
    private int windowThreeVentilation;
    private int windowFourVentilation;

    public Measurement(String device, Long date, int coTwo, double coTwoGradient, double humidity,
                       double humidityGradient, double temperature, double temperatureGradient, int people,
                       int timeslot, boolean falsePositive, boolean ventilationNeeded, int doorVentilation,
                       int windowOneVentilation, int windowTwoVentilation, int windowThreeVentilation,
                       int windowFourVentilation){
        this.device = device;
        this.date = date;
        this.coTwo = coTwo;
        this.coTwoGradient = coTwoGradient;
        this.humidity = humidity;
        this.humidityGradient = humidityGradient;
        this.temperature = temperature;
        this.temperatureGradient = temperatureGradient;
        this.people = people;
        this.timeslot = timeslot;
        this.falsePositive = falsePositive;
        this.ventilationNeeded = ventilationNeeded;
        this.doorVentilation = doorVentilation;
        this.windowOneVentilation = windowOneVentilation;
        this.windowTwoVentilation = windowTwoVentilation;
        this.windowThreeVentilation = windowThreeVentilation;
        this.windowFourVentilation = windowFourVentilation;
    }

    public String getDevice() {
        return device;
    }

    public long getDate() {
        return date;
    }

    public int getCoTwo() {
        return coTwo;
    }

    public double getCoTwoGradient() {
        return coTwoGradient;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getHumidityGradient() {
        return humidityGradient;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getTemperatureGradient() {
        return temperatureGradient;
    }

    public int getPeople() {
        return people;
    }

    public int getTimeslot() {
        return timeslot;
    }

    public boolean isFalsePositive() {
        return falsePositive;
    }

    public boolean isVentilationNeeded() {
        return ventilationNeeded;
    }

    public int getDoorVentilation() {
        return doorVentilation;
    }

    public int getWindowOneVentilation() {
        return windowOneVentilation;
    }

    public int getWindowTwoVentilation() {
        return windowTwoVentilation;
    }

    public int getWindowThreeVentilation() {
        return windowThreeVentilation;
    }

    public int getWindowFourVentilation() {
        return windowFourVentilation;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setCoTwo(int coTwo) {
        this.coTwo = coTwo;
    }

    public void setCoTwoGradient(double coTwoGradient) {
        this.coTwoGradient = coTwoGradient;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public void setHumidityGradient(double humidityGradient) {
        this.humidityGradient = humidityGradient;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setTemperatureGradient(double temperatureGradient) {
        this.temperatureGradient = temperatureGradient;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public void setTimeslot(int timeslot) {
        this.timeslot = timeslot;
    }

    public void setFalsePositive(boolean falsePositive) {
        this.falsePositive = falsePositive;
    }

    public void setVentilationNeeded(boolean ventilationNeeded) {
        this.ventilationNeeded = ventilationNeeded;
    }

    public void setDoorVentilation(int doorVentilation) {
        this.doorVentilation = doorVentilation;
    }

    public void setWindowOneVentilation(int windowOneVentilation) {
        this.windowOneVentilation = windowOneVentilation;
    }

    public void setWindowTwoVentilation(int windowTwoVentilation) {
        this.windowTwoVentilation = windowTwoVentilation;
    }

    public void setWindowThreeVentilation(int windowThreeVentilation) {
        this.windowThreeVentilation = windowThreeVentilation;
    }

    public void setWindowFourVentilation(int windowFourVentilation) {
        this.windowFourVentilation = windowFourVentilation;
    }
}
