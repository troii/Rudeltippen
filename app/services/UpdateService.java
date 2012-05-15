package services;

import models.Game;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import play.Logger;
import play.libs.WS;
import utils.AppUtils;

public class UpdateService {
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
        	Logger.error("Updating of Results from WebService failed", e);
        }
        return document;
    }

    public static void setResultFromWebservice(Game game) {
        Document document = null;
    	String matchID = game.getWebserviceID();
        if (StringUtils.isNotBlank(matchID)) {
        	document = getDocumentFromWebService(matchID);
        }

        if (document != null) {
    		Node matchResults = document.getElementsByTagName("matchResults").item(0);
            NodeList matchResult = matchResults.getChildNodes();

            for (int i=0; i < matchResult.getLength(); i++) {
                NodeList singleResults = matchResult.item(i).getChildNodes();
                String name = singleResults.item(0).getTextContent();

                if (StringUtils.isBlank(name)) {
                    continue;
                }

                boolean overtime = false;
                String key = null;
                if (("nach 45 minuten").equalsIgnoreCase((name))) {
                    key = "45";
                } else if (("nach 90 minuten").equalsIgnoreCase(name)) {
                    key = "90";
                } else if (("nach verlÃ¤ngerung").equalsIgnoreCase(name)) {
                	overtime = true;
                    key = "120";
                } else if (("nach elfmeterschiessen").equalsIgnoreCase(name)) {
                	overtime = true;
                    key = "121";
                }

                if (StringUtils.isNotBlank(key)) {
                    String homeScore = singleResults.item(1).getTextContent();
                    String awayScore = singleResults.item(2).getTextContent();
                    String extratime = null;
                    String homeScoreExtratime = null;
                    String awayScoreExtratime = null;

                    if (overtime) {
                    	homeScoreExtratime = homeScore;
                    	awayScoreExtratime = awayScore;

                    	if (("120").equals(key)) {
                    		extratime = "n.V.";
                    	} else {
                    		extratime = "i.E.";
                    	}
                    }
                    AppUtils.setGameScore(game.getId().toString(), homeScore, awayScore, extratime, homeScoreExtratime, awayScoreExtratime);
                    AppUtils.calculateScoresAndPoints();
                }
            }
        }
    }
}