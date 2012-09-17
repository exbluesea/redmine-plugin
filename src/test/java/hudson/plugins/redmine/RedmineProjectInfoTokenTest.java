/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.redmine;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.TaskListener;
import hudson.plugins.redmine.tokens.ProjectInfoToken;
import hudson.plugins.redmine.tokens.RedmineAPI;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.dom4j.Element;
import org.jenkinsci.plugins.tokenmacro.MacroEvaluationException;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 *
 * @author yangxiaole
 */
public class RedmineProjectInfoTokenTest extends HudsonTestCase {
    private static final String REDMINE_URL = "http://10.122.81.238:8080";
    private static final String PROJECT_URL = "sztv-iphone";
    private static final String USER_NAME   = "admin";
    private static final String PASSWORD    = "admin";

    public void testGetProjectInfo () {
        RedmineProjectProperty prop = new RedmineProjectProperty(
                REDMINE_URL, PROJECT_URL, USER_NAME, PASSWORD, null, true);
        
        Element e = RedmineAPI.getProjectInfo(prop);
        
        assertTrue("project description not empty", !e.elementText("description").isEmpty());
    }
    
    public void testProjectInfoToken() throws IOException, InterruptedException, ExecutionException, MacroEvaluationException {
        FreeStyleProject project = createFreeStyleProject("test");
        
        project.addProperty(new RedmineProjectProperty(REDMINE_URL, PROJECT_URL, USER_NAME, PASSWORD, null, true));
       
        FreeStyleBuild build = project.scheduleBuild2( 0 ).get();
        
        ProjectInfoToken token = new ProjectInfoToken();

        token.field = "description";
        String out = token.evaluate(build, TaskListener.NULL, "REDMINE_PROJECT_INFO");
        assertEquals(out, "<p>苏州电视台iPhone版</p>\n");
    }
}
