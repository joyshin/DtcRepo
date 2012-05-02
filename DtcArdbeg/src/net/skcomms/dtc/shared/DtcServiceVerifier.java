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
   * 지정된 경로의 유효성을 검사한다.
   * 
   * 정규식 "/([a-zA-Z0-9_.-]+/)*"에 일치하면 유효한 것으로 판단한다.
   * 
   * @param path
   *          검사할 경로
   * @return 유효한 경우 true; 아니면 false
   */
  public static boolean isValidDirectoryPath(String path) {
    if (path == null) {
      return false;
    }

    String regex = "/([a-zA-Z0-9_.-]+/)*";
    return path.matches(regex);
  }

  /**
   * 지정된 테스트 페이지의 유효성을 검사한다.
   * 
   * 정규식 "/([a-zA-Z0-9_.-]+/)*[a-zA-Z0-9_.-]+\\.ini"에 일치하면 유효한 것으로 판단한다.
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
