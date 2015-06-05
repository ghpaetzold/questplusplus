/**
 *
 */
package shef.mt.xmlwrap;

import java.util.Comparator;

import shef.mt.features.impl.Feature;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

/**
 * @author cat
 *
 */
public class TranslationElementComparator implements Comparator<Element> {

    @Override
    public int compare(Element arg0, Element arg1) {
        // TODO Auto-generated method stub
        float e1 = Float.parseFloat(arg0.getAttribute("prob"));
        float e2 = Float.parseFloat(arg1.getAttribute("prob"));
        if (e1 <= e2) {
            return 1;
        } else {
            return -1;
        }
    }
}