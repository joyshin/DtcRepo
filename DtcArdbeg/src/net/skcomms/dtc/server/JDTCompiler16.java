/**
 * 
 */
package net.skcomms.dtc.server;

import org.apache.tools.ant.taskdefs.Javac;
import org.eclipse.jdt.core.JDTCompilerAdapter;

/**
 * @author jujang@sk.com
 * 
 */
public class JDTCompiler16 extends JDTCompilerAdapter {
  @Override
  public void setJavac(Javac attributes) {
    if (attributes.getTarget() == null) {
      attributes.setTarget("1.6");
    }
    if (attributes.getSource() == null) {
      attributes.setSource("1.6");
    }
    super.setJavac(attributes);
  }
}
