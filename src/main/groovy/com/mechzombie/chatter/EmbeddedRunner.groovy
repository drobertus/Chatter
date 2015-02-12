package com.mechzombie.chatter

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext

/**
 * Created by David on 2/11/2015.
 */
class EmbeddedRunner {

    public static void main (String[] args) {

        Server jetty = new Server(9000)
        WebAppContext context = new WebAppContext()
        context.setContextPath("/")
        context.setDescriptor("./src/main/webapp/WEB-INF/web.xml");
        //context.setWar("./build/libs/chatter.war")
        context.setResourceBase("./src/main/webapp")

        jetty.setHandler(context)


        jetty.start()
        jetty.join()

    }
}
