package com.vs_project.vs_gruppentrainingsplan.socket;

import com.vs_project.vs_gruppentrainingsplan.helper.LeaderBoardUpdateDTO;
import com.vs_project.vs_gruppentrainingsplan.helper.LeaderBoardUpdateEncoder;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Diese Klasse ist inspiriert durch
 * <a href="https://www.baeldung.com/java-websockets">Baeldung</a>
 */
@ServerEndpoint(value = "/group/{groupName}", encoders = LeaderBoardUpdateEncoder.class)
public class GroupSocket {

    private Session session;
    private String groupName;
    private static final Set<GroupSocket> endpoints
            = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("groupName") String groupName) throws IOException, EncodeException {

        this.session = session;
        this.groupName = groupName;
        endpoints.add(this);
    }

    @OnMessage
    public void onMessage(Session session, String message)
            throws IOException, EncodeException {

    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {

        endpoints.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    public static void broadcast(LeaderBoardUpdateDTO update, Collection<String> groupNames)
            throws IOException {

        endpoints.stream()
                .filter(endpoint -> groupNames.contains(endpoint.groupName))
                .forEach(endpoint -> {
                    synchronized (endpoint) {
                        try {
                            endpoint.session.getBasicRemote().
                                    sendObject(update);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (EncodeException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }
}