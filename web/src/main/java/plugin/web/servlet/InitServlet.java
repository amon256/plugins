package plugin.web.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class InitServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2876171476301229758L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("=============="+System.getProperty("user.dir"));
	}
}
