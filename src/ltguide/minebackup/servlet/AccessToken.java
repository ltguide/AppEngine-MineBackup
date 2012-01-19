package ltguide.minebackup.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ltguide.minebackup.MineBackup;
import ltguide.minebackup.DropboxException;
import ltguide.minebackup.TokenPair;

@SuppressWarnings("serial") public class AccessToken extends HttpServlet {
	@Override public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		final HttpSession session = req.getSession(false);
		if (session == null) {
			resp.sendRedirect(MineBackup.getURL(req));
			return;
		}
		
		resp.setContentType("text/html");
		
		final PrintWriter out = resp.getWriter();
		out.print("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\" /><title>MineBackup Dropbox Linker</title></head><body>");
		
		try {
			final TokenPair tokenPair = MineBackup.getTokenPair("https://api.dropbox.com/1/oauth/access_token", new TokenPair(session.getAttribute("key").toString(), session.getAttribute("secret").toString()));
			
			out.print("<p>MineBackup has successfully been linked to your Dropbox account.</p>");
			out.print("<p>Use the command <span style=\"white-space:pre;font-family:monospace;\">minebackup dropbox " + tokenPair.key + " " + tokenPair.secret + "</span> from the console to enable.</p>");
		}
		catch (final DropboxException e) {
			MineBackup.outException(e, out);
		}
		finally {
			session.invalidate();
			out.print("</body></html>");
		}
	}
}
