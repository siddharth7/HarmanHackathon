package siddharth.com.tagtraqr;


public class Devices {

    private long id;
    private String name;
    private String mac;
    private double lati;
    private double longi;
    private byte[] _image;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMac() {
        return mac;
    }
    public void setMac(String description) {
        this.mac = description;
    }
    public byte[] getImage() {
        return this._image;
    }
    public void setImage(byte[] image) {
        this._image = image;
    }
    public double getLati() {
        return lati;
    }
    public void setLati(double lati) {
        this.lati = lati;
    }
    public double getLongi() {
        return longi;
    }
    public void setLongi(double longi) {
        this.longi = longi;
    }
}
