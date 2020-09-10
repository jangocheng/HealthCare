package bupt.mxly.healthcare.db;

public class Sipserver {
    public String getSipid() {
        return sipid;
    }

    public void setSipid(String sipid) {
        this.sipid = sipid;
    }

    public int getOccupied() {
        return occupied;
    }

    public void setOccupied(int occupied) {
        this.occupied = occupied;
    }

    private String sipid;
    private int occupied;
}
