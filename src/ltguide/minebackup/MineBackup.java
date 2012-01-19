package ltguide.minebackup;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

public class MineBackup {
	public final static String Key = "l5uwj4soziwf5de";
	public final static String Secret = "secret";
	
	public static String getURL(final HttpServletRequest req) {
		final StringBuffer buf = req.getRequestURL();
		return buf.substring(0, buf.length() - req.getRequestURI().length());
	}
	
	public static TokenPair getTokenPair(final String url, final TokenPair requestTokenPair) throws DropboxException {
		final Map<String, String> result = HttpUtils.postResponse(url, getAuth(url, requestTokenPair));
		
		if (!result.containsKey("oauth_token") || !result.containsKey("oauth_token_secret")) throw new DropboxException("Did not get tokens from Dropbox");
		
		return new TokenPair(result.get("oauth_token"), result.get("oauth_token_secret"));
	}
	
	private static String getAuth(final String url, final TokenPair requestTokenPair) {
		String secret = null;
		final SortedMap<String, String> oauth = new TreeMap<String, String>();
		oauth.put("oauth_consumer_key", Key);
		oauth.put("oauth_nonce", String.valueOf(new Random().nextInt()));
		oauth.put("oauth_signature_method", "HMAC-SHA1");
		oauth.put("oauth_timestamp", String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000));
		if (requestTokenPair != null) {
			secret = requestTokenPair.secret;
			oauth.put("oauth_token", requestTokenPair.key);
		}
		oauth.put("oauth_version", "1.0");
		
		return HttpUtils.createAuth(oauth, sign("POST", url, HttpUtils.urlencode(oauth), secret));
	}
	
	public static String sign(final String method, final String url, final String params, String key) {
		final String text = method + "&" + HttpUtils.encode(url) + "&" + HttpUtils.encode(params);
		key = HttpUtils.encode(Secret) + "&" + HttpUtils.encode(key);
		
		Mac mac;
		try {
			mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA1"));
			
			return HttpUtils.encode(Base64.encodeBase64String(mac.doFinal(text.getBytes("UTF-8"))));
		}
		catch (final Exception e) {
			return "";
		}
	}
	
	public static void outException(final Exception e, final PrintWriter out) {
		out.println("<p>" + e.getLocalizedMessage() + "</p>");
		
		out.println("<pre>" + e.getClass().getName());
		for (final StackTraceElement stack : e.getStackTrace())
			out.println("\t" + stack.toString());
		
		out.print("</pre>");
	}
}
