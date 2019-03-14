import java.io.Serializable;

/**
 * This is request model . when FE receive call from the client , FE needs to send  request to RM
 * Udp Server . Object of this class will be serialized and send over the network
 */
public class ClientRequestModel implements Serializable {

  private String methodName;
  private String itemName;
  private String itemId;
  private String userId;
  private int numberOfDays;

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
}
