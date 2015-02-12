package com.mechzombie.chatter;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "Test WS Servlet")
public class ChatterWSServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        System.out.println("web socket servlet initialized");
        factory.register(ChatterSocketListener.class);
    }
}