/**
 * 
 */
package net.skcomms.dtc.shared;

/**
 * @author jujang@sk.com
 * 
 */
public class DtcServiceVerifier {

  /**
   * 吏����寃쎈�������깆� 寃�����.
   * 
   * �����"/([a-zA-Z0-9_.-]+/)*"���쇱���㈃ �����寃��濡�������.
   * 
   * @param path
   *          寃����寃쎈�
   * @return �����寃쎌� true; ���硫�false
   */
  public static boolean isValidDirectoryPath(String path) {
    if (path == null) {
      return false;
    }

    String regex = "/([a-zA-Z0-9_.-]+/)*";
    return path.matches(regex);
  }

  public static boolean isValidMethod(String httpMethod) {

    if (httpMethod.toLowerCase().equals("get") || httpMethod.toLowerCase().equals("post"))
      return true;
    else
      return false;
  }

  /**
   * 吏������������吏�� ����깆� 寃�����.
   * 
   * �����"/([a-zA-Z0-9_.-]+/)*[a-zA-Z0-9_.-]+\\.ini"���쇱���㈃ �����寃��濡�������.
   * 
   * @param path
   * @return
   */
  public static boolean isValidTestPage(String path) {
    if (path == null) {
      return false;
    }

    String regex = "/([a-zA-Z0-9_.-]+/)*[a-zA-Z0-9_.-]+\\.ini";
    return path.matches(regex);
  }
}
