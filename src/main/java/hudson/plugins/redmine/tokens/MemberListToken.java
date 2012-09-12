/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.redmine.tokens;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.plugins.redmine.RedmineProjectProperty;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.jenkinsci.plugins.tokenmacro.DataBoundTokenMacro;
import org.jenkinsci.plugins.tokenmacro.MacroEvaluationException;

/**
 *
 * @author yangxiaole
 */
@Extension
public class MemberListToken extends DataBoundTokenMacro {

    @Parameter
    public String role;
    
    @Override
    public String evaluate(AbstractBuild<?, ?> ab, TaskListener tl, String string) throws MacroEvaluationException, IOException, InterruptedException {
        RedmineProjectProperty prop = ab.getProject().getProperty(RedmineProjectProperty.class);
        Map<String, String> userMap = RedmineAPI.getUsers(prop);
        
        List<String> list = RedmineAPI.getMembershipFor(prop, role);
        
        StringBuilder buf = new StringBuilder();
        
        int i = 0;
        for(String id : list) {
            buf.append(userMap.get(id));
            if(++i != list.size()) {
                buf.append(",");
            }
        }
        
        return buf.toString();
    }

    @Override
    public boolean acceptsMacroName(String string) {
        return "REDMINE_MEMBER".equals(string);
    }
    

}
