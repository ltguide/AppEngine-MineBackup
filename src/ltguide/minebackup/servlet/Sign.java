package ltguide.minebackup.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ltguide.minebackup.MineBackup;

@SuppressWarnings("serial") public class Sign extends HttpServlet {
	@Override public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().print(MineBackup.sign("PUT", "https://api-content.dropbox.com/1/files_put/sandbox/" + req.getParameter("url"), req.getParameter("params"), req.getParameter("oauth_token_secret")));
	}
}
