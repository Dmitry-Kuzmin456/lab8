package org.server.managers;

import org.server.network.Connector;
import org.common.request.Request;
import org.server.utils.UserInfo;

import java.util.concurrent.ExecutorService;

public class RequestProcessor implements Runnable {
    private final ExecutorService responsePool;
    private final Connector connector;
    private final Request request;
    private final UserInfo userInfo;

    public RequestProcessor(Request request, ExecutorService responsePool, Connector connector, UserInfo userInfo) {
        this.responsePool = responsePool;
        this.connector = connector;
        this.request = request;
        this.userInfo = userInfo;
    }

    public void run() {
        CommandManager.getInstance().executeCommand(request, responsePool, connector, userInfo);
    }
}
