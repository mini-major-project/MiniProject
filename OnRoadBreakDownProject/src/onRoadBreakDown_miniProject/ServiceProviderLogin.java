package onRoadBreakDown_miniProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ServiceProviderLogin
 */
@WebServlet("/ServiceProviderLogin")
public class ServiceProviderLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServiceProviderLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		String serviceProviderMail = request.getParameter("userMail");
		String serviceProviderPswd = request.getParameter("userPswd");
		PrintWriter out = response.getWriter();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/OnRoadBreakDown?autoReconnect=true&useSSL=false", "root", "Taraka123");
			PreparedStatement pstmt = con.prepareStatement("select * from serviceProvider where serviceProviderMail=?");
			pstmt.setString(1, serviceProviderMail);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() == false) {
				System.out.println("First Register");
				RequestDispatcher rd = request.getRequestDispatcher("ServiceProviderRegistration.html");
				rd.include(request, response);
			} else {
				String pswd = "";
				String serviceProviderMobile = "";
				String serviceProviderName="";
				String serviceProviderId="";
				do {
					pswd = rs.getString("serviceProviderPswd");
					PasswordEncryptionDecryption p=new PasswordEncryptionDecryption();
					pswd=p.decrypt(pswd);
					serviceProviderMobile = rs.getString("serviceProviderMobile");
					serviceProviderName=rs.getString("serviceProviderName");
					serviceProviderId=rs.getString("serviceProviderId");
				} while (rs.next());
				if (serviceProviderPswd.equals(pswd)) {
					System.out.println("Login Successful");
					HttpSession session = request.getSession();
					session.setAttribute("serviceProviderMail", serviceProviderMail);
					session.setAttribute("serviceProviderMobile", serviceProviderMobile);
					session.setAttribute("serviceProviderName", serviceProviderName);
					session.setAttribute("serviceProviderId", serviceProviderId);

					RequestDispatcher rd = request.getRequestDispatcher("ServiceProviderHomePage.html");
					rd.include(request, response);
				} else {
					System.out.println("Wrong Password");
					RequestDispatcher rd = request.getRequestDispatcher("ServiceProviderLogin.html");
					rd.include(request, response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
