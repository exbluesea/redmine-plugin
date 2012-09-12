/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.redmine.tokens;

import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.plugins.redmine.RedmineProjectProperty;
import java.io.IOException;
import org.dom4j.Element;
import org.jenkinsci.plugins.tokenmacro.DataBoundTokenMacro;
import org.jenkinsci.plugins.tokenmacro.MacroEvaluationException;

/**
 *
 * @author yangxiaole
 */
public class ProjectInfoToken extends DataBoundTokenMacro {

    public String field;
    
    @Override
    public String evaluate(AbstractBuild<?, ?> ab, TaskListener tl, String string) throws MacroEvaluationException, IOException, InterruptedException {
        RedmineProjectProperty prop = ab.getProject().getProperty(RedmineProjectProperty.class);
        String result = "";
        
        Element e = RedmineAPI.getProjectInfo(prop);
        
        if(e != null) {
            result = e.elementText(field);
        }
        
        return result;
    }

    @Override
    public boolean acceptsMacroName(String string) {
        return "REDMINE_PROJECT_INFO".equals(string);
    }
    
}
