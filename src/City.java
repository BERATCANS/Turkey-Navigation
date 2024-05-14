public class City {
    public String cityName;
    public double x;
    public double y;
    public City(String name, double x, double y) {
        this.cityName = name;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return cityName;
    }

    public double distanceTo(City other){
        double dx= this.x-other.x;
        double dy= this.y-other.y;
        return Math.sqrt(dx*dx+dy*dy);
    }
}
