import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
//TODO : This module may need to move to FrontEndModule.
public class SequencerMain {

  public static void main(String[] args) {

  }

  private byte[] getByteArrayOfRequest(ClientRequestModel request) {
    byte[] bytes = new byte[1000];
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ObjectOutputStream os = null;
    try {
      os = new ObjectOutputStream(outputStream);
      os.writeObject(request);
      bytes = outputStream.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bytes;

  }
}
