package se.scalablesolutions.akka.camel.component;

import org.apache.commons.io.IOUtils;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <b>Experimental hack</b>.
 *
 * @author Martin Krasser
 */
public class DefaultHandler implements AtmosphereHandler<HttpServletRequest, HttpServletResponse> {
    public void onRequest(AtmosphereResource<HttpServletRequest, HttpServletResponse> resource) throws IOException {
        if (resource.getRequest().getMethod().equalsIgnoreCase("GET")) {
            resource.suspend();
        } else if (resource.getRequest().getMethod().equalsIgnoreCase("POST")) {
            String message = IOUtils.toString(resource.getRequest().getInputStream());
            resource.getBroadcaster().broadcast(message);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void onStateChange(AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) throws IOException {
        String message = (String)event.getMessage();
        event.getResource().getResponse().getWriter().println(message);
        event.getResource().getResponse().getWriter().flush();
        event.getResource().resume();
    }
}

