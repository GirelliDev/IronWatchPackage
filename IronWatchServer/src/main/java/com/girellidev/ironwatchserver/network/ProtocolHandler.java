package com.girellidev.ironwatchserver.network;

import com.girellidev.ironwatchserver.security.AuthCode;
import com.girellidev.ironwatchserver.security.CodeManager;
import com.girellidev.ironwatchserver.security.Session;
import com.girellidev.ironwatchserver.security.SessionManager;

public class ProtocolHandler {

    public static String handle(String request) {

        try {

            if (request.startsWith("AUTH|")) {

                String code = request.split("\\|")[1];

                AuthCode authCode = CodeManager.validate(code);

                if (authCode == null) {
                    return "FAILED";
                }

                switch (authCode.getType()) {

                    case MASTER_ADMIN:

                        Session session =
                                SessionManager.createSession("MASTER_DEVICE", "MASTER_ADMIN");

                        return "OK|" + session.getToken();

                    case ADMIN_INVITE:

                        Session adminSession =
                                SessionManager.createSession("ADMIN_DEVICE", "ADMIN");

                        return "OK|" + adminSession.getToken();

                    case EMPRESA_CLIENT:

                        Session empresaSession =
                                SessionManager.createSession("EMPRESA_DEVICE", "EMPRESA");

                        return "OK|" + empresaSession.getToken();

                }

            }

            if ("PING".equals(request)) {
                return "PONG";
            }

            return "UNKNOWN_COMMAND";

        } catch (Exception e) {

            return "ERROR";

        }

    }

}