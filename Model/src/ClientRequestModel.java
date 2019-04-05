import java.io.Serializable;

/**
 * This is request model . when FE receive call from the client , FE needs to send  request to RM
 * Udp Server . Object of this class will be serialized and send over the network
 */
public class ClientRequestModel implements Serializable {

  private String methodName;
  private String itemName;
  private String itemId; //can be used as old item id for the exchange operation
  private String userId;
  private int numberOfDays;
  private int requestId;
  private int frontEndPort;
  private int quantity;
  private String newItemId; //this is for the exchange operation
  private String replicaName;

    public String getReplicaName() {
        return replicaName;
    }

    public void setReplicaName(String replicaName) {
        this.replicaName = replicaName;
    }

    public int getFrontEndPort() {
    return frontEndPort;
  }

  public void setFrontEndPort(int frontEndPort) {
    this.frontEndPort = frontEndPort;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public int getNumberOfDays() {
    return numberOfDays;
  }

  public void setNumberOfDays(int numberOfDays) {
    this.numberOfDays = numberOfDays;
  }

  public String getNewItemId() {
    return newItemId;
  }

  public void setNewItemId(String newItemId) {
    this.newItemId = newItemId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public ClientRequestModel(String methodName, String itemId, String userId) {
    this.methodName = methodName;
    this.itemId = itemId;
    this.userId = userId;
  }

  public ClientRequestModel(String methodName, String itemId, String userId, int numberOfDays) {
    this.methodName = methodName;
    this.itemId = itemId;
    this.userId = userId;
    this.numberOfDays = numberOfDays;
  }

  public ClientRequestModel() {
  }

  public ClientRequestModel(String methodName, String userId) {
    this.methodName = methodName;
    this.userId = userId;
  }

  public ClientRequestModel(String methodName) {
    this.methodName = methodName;
  }

  public int getRequestId() {
    return requestId;
  }

  public void setRequestId(int requestId) {
    this.requestId = requestId;
  }

    @Override
    public String toString() {
        return "ClientRequestModel{" +
                "methodName='" + methodName + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemId='" + itemId + '\'' +
                ", userId='" + userId + '\'' +
                ", numberOfDays=" + numberOfDays +
                ", requestId=" + requestId +
                ", frontEndPort=" + frontEndPort +
                ", quantity=" + quantity +
                ", newItemId='" + newItemId + '\'' +
                ", replicaName='" + replicaName + '\'' +
                '}';
    }
}
