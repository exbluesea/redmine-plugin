/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.redmine.tokens;

import hudson.plugins.redmine.RedmineProjectProperty;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import sun.misc.BASE64Encoder;

/**
 *
 * @author yangxiaole
 */
public class RedmineAPI {
        public static List<Element> redmineAPI(String urlString, String xpath, String username, String password) {
        HttpURLConnection conn = null;
        List<Element> list = new ArrayList<Element>();
        
        try {
            URL url = new URL(urlString);
            
            conn = (HttpURLConnection)url.openConnection();
            
            String auth = username + ":" + password;
            BASE64Encoder base = new BASE64Encoder();
            String encodedAuth = base.encode(auth.getBytes());

            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
            conn.setRequestMethod("GET"); 
            
            SAXReader reader = new SAXReader();
            
            Document doc = reader.read(conn.getInputStream());
            
            list.addAll(doc.selectNodes(xpath));
            
        } catch (DocumentException ex) {
            Logger.getLogger(MemberListToken.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MemberListToken.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
        
        return list;
    }
    
    public static Map<String, String> getUsers(RedmineProjectProperty prop) {
        
        Map<String, String> map = new HashMap<String, String>();
        
        String urlString = prop.redmineWebsite +"users.xml?limit=100";
        String xpath = "/users/user";
        
        for(Element e : redmineAPI(urlString, xpath, prop.username, prop.password)) {
            map.put(e.elementText("id"), e.elementText("mail"));
        }
        
        return map;
    }
    
    private static final Map<String, String> roleMap = new HashMap<String, String>() {{
        put("manager","3");
        put("dev",    "4");
        put("test",   "5");        
    }};
    
    public static List<String> getMembershipFor(RedmineProjectProperty prop, String role) {
        List<String> users = new ArrayList<String>();
        
        Map<String, String> tmpRoleMap = new HashMap<String, String>();
        
        if(prop.roleMap != null && prop.roleMap.trim().length() > 0) {
            String[] pairs = prop.roleMap.split(",");
            for(String pair : pairs) {
                String[] tmp = pair.split(":");
                if(tmp.length != 2) {
                    continue;
                }
                
                tmpRoleMap.put(tmp[0], tmp[1]);
            }
        } else {
            tmpRoleMap.putAll(roleMap);
        }
   
        if(!tmpRoleMap.containsKey(role)) {
            return users;
        }
        
        String urlString = prop.redmineWebsite + "projects/" + prop.projectName + "/memberships.xml?limit=100";
        String xpath = "/memberships/membership[roles/role[@id='" +tmpRoleMap.get(role)+ "']]/user";
        
        for(Element element : redmineAPI(urlString, xpath, prop.username, prop.password)) {
            String val = element.attributeValue("id");
                
            if(!users.contains(val)) {
                users.add(val);
            }
        }

        return users;
    } 
    
    public static Element getProjectInfo(RedmineProjectProperty prop) {
        String urlString = prop.redmineWebsite + "projects/" + prop.projectName + ".xml";
        String xpath = "/project";
        
        List<Element> list = redmineAPI(urlString, xpath, prop.username, prop.password);
        
        if(list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
