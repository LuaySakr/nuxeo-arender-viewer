package org.nuxeo.viewer.arender;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.services.config.ConfigurationService;

public class ArenderRedirectFilter implements Filter {

    protected static final Pattern PDFJS_URL = Pattern.compile(

            "^(https?://.*?(?::\\d+)?/\\w+/)viewers/pdfjs/.*?/(?:repo/(\\w+)/)?id/(.{8}-.{4}-.{4}-.{4}-.{12})/(?:@blob/(\\w+:\\w+)/)?.*",
            CASE_INSENSITIVE);

    protected static final String CURRENT_USER_KEY = "currentUser";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to init
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        Matcher matcher = PDFJS_URL.matcher(getURL((HttpServletRequest) request));
        if (!matcher.matches()) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = ((HttpServletRequest) request).getSession();
        if (session == null || session.getAttribute(CURRENT_USER_KEY) == null) {
            chain.doFilter(request, response);
            return;
        }

        String serverUri = getPropertyOrDefault("arender.nuxeo.cmis", matcher.group(1).concat("atom/cmis"));
        String repository = matcher.group(2);
        String guid = matcher.group(3);
        String user = session.getAttribute(CURRENT_USER_KEY).toString();
        String field = matcher.group(4);

        if (field != null && !field.equals("file:content")) {
            // XXX Not handled by CMIS for now; only main stream
            chain.doFilter(request, response);
            return;
        }

        try {
            String arenderUri = getPropertyOrDefault("arender.uri", null);
            URIBuilder builder = new URIBuilder(arenderUri).addParameter("nodeRef", guid)
                                                           .addParameter("nuxeo-username", user)
                                                           .addParameter("atompub", serverUri);

            if (StringUtils.isNotBlank(repository)) {
                builder.addParameter("repository", repository);
            }

            ((HttpServletResponse) response).sendRedirect(builder.build().toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {

    }

    protected static String getPropertyOrDefault(String key, String value) {
        return Framework.getService(ConfigurationService.class).getProperty(key, null);
    }

    protected static String getURL(HttpServletRequest req) {
        String reqUrl = req.getRequestURL().toString();
        String queryString = req.getQueryString();
        if (queryString != null) {
            reqUrl += "?" + queryString;
        }
        return reqUrl;
    }
}
