package services;

import java.util.HashMap;
import java.util.Map;

import models.Game;
import models.WSResult;
import models.WSResults;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import play.Logger;
import play.libs.WS;

public class UpdateService {
	public static WSResults getResultsFromWebService(Game game) {
        WSResults wsResults = new WSResults();
        wsResults.setUpdated(false);
        String matchID = game.getWebserviceID();
        if (StringUtils.isNotBlank(matchID)) {
			Document document = getDocumentFromWebService(matchID);
	    	if (document != null) {
	    	    final String matchIsFinished = document.getElementsByTagName("matchIsFinished").item(0).getTextContent();
	    		if (("true").equalsIgnoreCase(matchIsFinished)) {
	    			wsResults = getEndResult(wsResults, document);
	    			wsResults.setUpdated(true);
	    		}
	    	}
		}
        return wsResults;
    }

    public static Document getDocumentFromWebService(String matchID) {
        final String WS_ENCODING = "UTF-8";
        final String WS_CONTENT_TYPE = "application/soap+xml";
        final String WS_URL = "http://www.openligadb.de/Webservices/Sportsdata.asmx";

        StringBuilder buffer = new StringBuilder();
        buffer.append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">");
        buffer.append("<soap12:Body>");
        buffer.append("<GetMatchByMatchID xmlns=\"http://msiggi.de/Sportsdata/Webservices\">");
        buffer.append("<MatchID>" + matchID + "</MatchID>");
        buffer.append("</GetMatchByMatchID>");
        buffer.append("</soap12:Body>");
        buffer.append("</soap12:Envelope>");

        Document document = null;
        try {
        	document = WS.url(WS_URL).setHeader("Content-Type", WS_CONTENT_TYPE).setHeader("charset", WS_ENCODING).body(buffer.toString()).post().getXml();
        } catch (Exception e) {
        	MailService.webserviceUpdateFailed(e.getMessage());
        	Logger.error("Updating of results from WebService failed", e);
        }
        return document;
    }

    public static WSResults getEndResult(WSResults wsResults, Document document) {
        Map<String, WSResult> resultsMap = new HashMap<String, WSResult>();
        Node matchResults = document.getElementsByTagName("matchResults").item(0);
        NodeList matchResult = matchResults.getChildNodes();

        for (int i=0; i < matchResult.getLength(); i++) {
            NodeList singleResults = matchResult.item(i).getChildNodes();
            String name = singleResults.item(0).getTextContent();

            if (StringUtils.isBlank(name)) {
                continue;
            }

            WSResult wsResult = new WSResult();
            String key = null;
            if (("Endergebnis").equalsIgnoreCase(name)) {
                key = "90";
            } else if (("Verlängerung").equalsIgnoreCase(name)) {
                key = "120";
            } else if (("Elfmeterschiessen").equalsIgnoreCase(name)) {
                key = "121";
            }

            if (StringUtils.isNotBlank(key)) {
                wsResult.setHomeScore(singleResults.item(1).getTextContent());
                wsResult.setAwayScore(singleResults.item(2).getTextContent());
                resultsMap.put(key, wsResult);
            }
        }
        wsResults.setWsResult(resultsMap);

        return wsResults;
    }
}