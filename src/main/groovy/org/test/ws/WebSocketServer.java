package org.test.ws;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebSocketServer {

    public static void main(String[] args) throws Exception {

        Server server = new Server(8555);

        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(TestWebSocketHandler.class);
            }
        };

        server.setHandler(wsHandler);
        server.start();
        server.join();
    }
}
