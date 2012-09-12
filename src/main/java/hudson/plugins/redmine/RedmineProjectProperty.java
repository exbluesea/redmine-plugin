package hudson.plugins.redmine;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Property for {@link AbstractProject} that stores the associated Redmine website URL.
 * 
 * @author gaooh
 * @date 2008/10/13
 */
public class RedmineProjectProperty extends JobProperty<AbstractProject<?, ?>> {

	public final String redmineWebsite;
	
	public final String projectName;
        
        public final String username;
        
        public final String password;
        
        public final String roleMap;
        
	public final Boolean redmineVersion;
        
	@DataBoundConstructor
	public RedmineProjectProperty(String redmineWebsite, String projectName, String username, String password, String roleMap, Boolean redmineVersion) {
		if (StringUtils.isBlank(redmineWebsite)) {
			redmineWebsite = null;
		} else {
			if (!redmineWebsite.endsWith("/")) {
				redmineWebsite += '/';
			}
		}
                
		this.redmineWebsite = redmineWebsite;
		this.projectName = projectName;
                this.username = username;
                this.password = password;
                this.roleMap  = roleMap;
		this.redmineVersion = redmineVersion;
	}

	@Override
    public Action getJobAction(AbstractProject<?,?> job) {
        return new RedmineLinkAction(this);
    }
	
	@Override
	public JobPropertyDescriptor getDescriptor() {
		return DESCRIPTOR;
	}

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	public static final class DescriptorImpl extends JobPropertyDescriptor {
		private transient String redmineWebsite;

		public DescriptorImpl() {
			super(RedmineProjectProperty.class);
			load();
		}

		@Override
		public boolean isApplicable(Class<? extends Job> jobType) {
			return AbstractProject.class.isAssignableFrom(jobType);
		}

		public String getDisplayName() {
			return "Associated Redmine website";
		}

		@Override
		public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
			try {
				String redmineWebSite = req.getParameter("redmine.redmineWebsite");
				String projectName = req.getParameter("redmine.projectName");
				String redmineVersion = req.getParameter("redmine.version");
                                String username = req.getParameter("redmine.username");
                                String password = req.getParameter("redmine.password");
                                String roleMap  = req.getParameter("redmine.roleMap");
				
				Boolean version = false;
				if(StringUtils.isNotBlank(redmineVersion) && redmineVersion.equals("on")) {
					version = true;
				}
                                
				return new RedmineProjectProperty(redmineWebSite, projectName, username, password, roleMap, version);
			
			} catch (IllegalArgumentException e) {
				throw new FormException("redmine.redmineWebsite", "redmine.redmineWebSite");
			}
		}
		
	}
}
