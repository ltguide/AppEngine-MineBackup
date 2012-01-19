package ltguide.minebackup.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ltguide.minebackup.MineBackup;
import ltguide.minebackup.DropboxException;
import ltguide.minebackup.HttpUtils;
import ltguide.minebackup.TokenPair;

@SuppressWarnings("serial") public class RequestToken extends HttpServlet {
	@Override public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		try {
			final TokenPair tokenPair = MineBackup.getTokenPair("https://api.dropbox.com/1/oauth/request_token", null);
			
			final HttpSession session = req.getSession();
			session.setAttribute("key", tokenPair.key);
			session.setAttribute("secret", tokenPair.secret);
			
			resp.sendRedirect("https://www.dropbox.com/1/oauth/authorize?oauth_callback=" + HttpUtils.encode(MineBackup.getURL(req) + "/access_token") + "&oauth_token=" + HttpUtils.encode(tokenPair.key));
		}
		catch (final DropboxException e) {
			resp.setContentType("text/html");
			MineBackup.outException(e, resp.getWriter());
		}
	}
}
