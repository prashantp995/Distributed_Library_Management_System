public class LostPacketModel {
    private Integer requestID;

    public LostPacketModel(Integer requestID) {
        this.requestID = requestID;
    }

    public Integer getRequestID() {
        return requestID;
    }

    public void setRequestID(Integer requestID) {
        this.requestID = requestID;
    }
}
