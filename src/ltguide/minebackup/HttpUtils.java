package ltguide.minebackup;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;

public class HttpUtils {
	public static final long maxSize = 180 * 1024 * 1024;
	
	public static Map<String, String> parseResponse(final InputStream response) throws DropboxException {
		final Scanner scanner = new Scanner(response).useDelimiter("&");
		final Map<String, String> result = new HashMap<String, String>();
		
		while (scanner.hasNext()) {
			final String nameValue = scanner.next();
			final String[] parts = nameValue.split("=");
			if (parts.length == 2) result.put(parts[0], parts[1]);
		}
		
		return result;
	}
	
	public static Map<String, String> postResponse(final String url, final String auth) throws DropboxException {
		return parseResponse(post(url, auth));
	}
	
	public static InputStream post(final String url, final String auth) throws DropboxException {
		try {
			final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestProperty("Authorization", auth);
			connection.setRequestMethod("POST");
			
			final int code = connection.getResponseCode();
			if (code != 200 && code != 304) throw new DropboxException(code);
			
			return connection.getInputStream();
		}
		catch (final IOException e) {
			throw new DropboxException(e);
		}
	}
	
	public static String urlencode(final Map<String, String> params) {
		final StringBuilder sb = new StringBuilder();
		for (final Map.Entry<String, String> param : params.entrySet()) {
			sb.append("&");
			sb.append(param.getKey());
			sb.append("=");
			sb.append(encode(param.getValue()));
		}
		
		return sb.substring(1);
	}
	
	public static String createAuth(final SortedMap<String, String> params, final String signature) {
		final StringBuilder sb = new StringBuilder("OAuth ");
		
		for (final Map.Entry<String, String> param : params.entrySet()) {
			sb.append(param.getKey());
			sb.append("=\"");
			sb.append(encode(param.getValue()));
			sb.append("\", ");
		}
		
		sb.append("oauth_signature=\"");
		sb.append(signature);
		sb.append("\"");
		
		return sb.toString();
	}
	
	public static String encode(final String string) {
		try {
			return URLEncoder.encode(string, "UTF-8").replace("+", "%20").replace("*", "%2A");
		}
		catch (final Exception e) {
			return "";
		}
	}
}
