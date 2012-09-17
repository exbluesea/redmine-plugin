/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.redmine;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.TaskListener;
import hudson.plugins.redmine.tokens.MemberListToken;
import hudson.plugins.redmine.tokens.RedmineAPI;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.jenkinsci.plugins.tokenmacro.MacroEvaluationException;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 *
 * @author yangxiaole
 */
public class RedmineMemberTokenTest extends HudsonTestCase {
    private static final String REDMINE_URL = "http://10.122.81.238:8080";
    private static final String PROJECT_URL = "sztv-iphone";
    private static final String USER_NAME   = "admin";
    private static final String PASSWORD    = "admin";
    private static final String ROLE_MAP    = "manager:3,dev:4,test:5,designer:6";
    
    public void testRedmineAPI() {
        RedmineProjectProperty prop = new RedmineProjectProperty(
                REDMINE_URL, PROJECT_URL, USER_NAME, PASSWORD, ROLE_MAP, true);
        
        assertTrue("got user list", RedmineAPI.getUsers(prop).size() > 0);
        System.out.println(RedmineAPI.getUsers(prop));
        
        assertTrue("got developer list",RedmineAPI.getMembershipFor(prop, "dev").size() > 0);
        System.out.println(RedmineAPI.getMembershipFor(prop, "dev"));
        
        assertTrue("got tester list",RedmineAPI.getMembershipFor(prop, "test").size() > 0);
        System.out.println(RedmineAPI.getMembershipFor(prop, "test"));
        
        assertTrue("got manager list",RedmineAPI.getMembershipFor(prop, "manager").size() > 0);
        System.out.println(RedmineAPI.getMembershipFor(prop, "manager"));
        
        assertTrue("got designer list",RedmineAPI.getMembershipFor(prop, "designer").size() > 0);
        System.out.println(RedmineAPI.getMembershipFor(prop, "designer"));
    }
    
    public void testMemberListEvaluate() throws IOException, InterruptedException, ExecutionException, MacroEvaluationException {
        FreeStyleProject project = createFreeStyleProject("test");
        
        project.addProperty(new RedmineProjectProperty(REDMINE_URL, PROJECT_URL, USER_NAME, PASSWORD, ROLE_MAP, true));
       
        FreeStyleBuild build = project.scheduleBuild2( 0 ).get();
        
        MemberListToken token = new MemberListToken();

        token.role = "test";
        String out = token.evaluate(build, TaskListener.NULL, "REDMINE_MEMBER");
        assertEquals(out, "sha.li@theotino.com");
        
        token.role = "manager";
        assertEquals(token.evaluate(build, TaskListener.NULL, "REDMINE_MEMBER"), "xiaole.yang@theotino.com");
        
    }
}
