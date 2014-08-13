import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.junit.Ignore

/**
 * Created by David on 8/7/2014.
 */

//@Ignore
class WebAppRunner {

    private static Server jetty

    public static void main(String[] args) {

        jetty = new Server(8556)
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setResourceBase("../../main/webapp");
        webapp.setBaseResource()
        //context.setContextPath("/");
        //webapp.setWar(jetty_home+"/webapps/test.war");
        jetty.setHandler(webapp);

        jetty.start();
        jetty.join();
    }


}
