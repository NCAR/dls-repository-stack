package edu.ucar.dls.harvest.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dlese.dpc.util.TimedURLConnection;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.tools.JsonUtil;


/**

 */

public class SMSHelper {
    public static final int SERVER_REQUEST_TIMEOUT = 60000;
    private HashMap<String, Object> smsInfoMap = new HashMap<String, Object>();
    private static final String GRADES = "grades";


    /**
     *
     */
    public List<String> getEducationalLevels(String asnId) throws Exception {
        List<String> educationLevels = null;

        Map<String, Object> smsInfo = this.getSMSInfo(asnId);

        if (smsInfo == null || !smsInfo.containsKey(GRADES))
            return null;

        List<String> grades = (List<String>) smsInfo.get(GRADES);

        // grades in the ASN are given as a range -1 through 12. We need
        // to convert this to something useable in nsdl_dc. By putting
        // Grade in front of the number for all grades except kindergarten
        // and pre kindergarten
        if (grades != null) {
            educationLevels = new ArrayList<String>();

            for (String grade : grades) {
                String educationLevel = null;
                if (grade.equals("K"))
                    educationLevel = "Kindergarten";
                else
                    educationLevel = String.format("Grade %s", grade);
                educationLevels.add(educationLevel);
            }
        }
        return educationLevels;
    }

    /**
     * Retrieve the asn info for a particular ASN via the nsdl resolver. And
     * cache it in memory. You can view the struture of these response by just
     * looking at example one
     * domain/asn/service.do?verb=GetStandard&id=http://asn.jesandco.org/resources/S1012C79
     *
     * @param smsId
     * @return
     * @throws Exception
     */
    private Map<String, Object> getSMSInfo(String smsId) throws Exception {

        if (this.smsInfoMap.containsKey(smsId)) {
            // Retrieve from cache if we've fetched this already:
            return (Map<String, Object>) this.smsInfoMap.get(smsId);
        }

        String url = String.format(Config.SMS_INFO_JSON_URL_FORMAT, smsId);
        String fileAsString = TimedURLConnection.importURL(url, Config.ENCODING, SERVER_REQUEST_TIMEOUT);

        HashMap<String, Object> smsInfo = new HashMap<String, Object>();

        Document document = Dom4jUtils.getXmlDocument(JsonUtil.convertToXML(fileAsString));

        List<Element> gradeRangeElements = document.selectNodes(
                "//SMS-CSIP/QueryResponse/SMS/Record/itemRecord/Data/GradeRanges/GradeRange");

        ArrayList<String> grades = new ArrayList<String>();

        for (Element gradeRangeElement : gradeRangeElements) {
            grades.add(gradeRangeElement.getText());
        }
        smsInfo.put(GRADES, grades);

        // save for next time
        this.smsInfoMap.put(smsId, smsInfo);
        return smsInfo;

    }
}