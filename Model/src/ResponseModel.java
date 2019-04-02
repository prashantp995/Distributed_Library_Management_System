import java.io.Serializable;

public class ResponseModel implements Serializable {

  private String status;
  private String response;
  private int requestId;
  private String clientId;
  private String itemId;
  private String note;
  private String replicaName;

  public ResponseModel() {
  }

  public ResponseModel(String clientId, String itemId, String response, String status,
      int requestId) {
    this.clientId = clientId;
    this.itemId = itemId;
    this.status = status;
    this.requestId = requestId;
    this.response = response;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public int getRequestId() {
    return requestId;
  }

  public void setRequestId(int requestId) {
    this.requestId = requestId;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getReplicaName() {
    return replicaName;
  }

  public void setReplicaName(String replicaName) {
    this.replicaName = replicaName;
  }
}
