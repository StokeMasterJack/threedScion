package c3i.admin.server;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WebAppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ThreedAdminApp app = new ThreedAdminApp();
        sce.getServletContext().setAttribute(ThreedAdminApp.class.getName(), app);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
