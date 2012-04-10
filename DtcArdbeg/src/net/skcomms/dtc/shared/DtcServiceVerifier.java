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
   * @param path
   * @return
   */
  public static boolean isValidDirectory(String path) {
    if (path == null) {
      return false;
    }

    String regex = "([a-zA-Z0-9_.-]+/)+|/";
    return path.matches(regex);
  }

}
