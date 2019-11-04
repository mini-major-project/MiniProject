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
 * Servlet implementation class UserLogin
 */
@WebServlet("/UserLogin")
public class UserLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserLogin() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		String userMail = request.getParameter("userMail");
		String userPswd = request.getParameter("userPswd");
		String userGivenPinCode = request.getParameter("userGivenPinCode");
		System.out.println(userMail+" "+userPswd);
		PrintWriter out = response.getWriter();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/OnRoadBreakDown?autoReconnect=true&useSSL=false", "root", "Taraka123");
			PreparedStatement pstmt = con.prepareStatement("select * from user where userMail=?");
			pstmt.setString(1, userMail);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() == false) {
				System.out.println("First Register");
				RequestDispatcher rd = request.getRequestDispatcher("UserRegistration.html");
				rd.include(request, response);
			} else {
				String pswd = "";
				String userMobile = "";
				String userName="";
				String userId="";
				String userPinCode="";
				do {
					pswd = rs.getString("userPswd");
					PasswordEncryptionDecryption p=new PasswordEncryptionDecryption();
					pswd=p.decrypt(pswd);
					userMobile = rs.getString("userMobile");
					userName=rs.getString("userName");
                    userId=rs.getString("userId");
                    userPinCode=rs.getString("userPinCode");
				} while (rs.next());
				if (userPswd.equals(pswd)) {
					System.out.println("Login Successful");
					/*
					 * SendingMail sm=new SendingMail(); 
					 * String rand=sm.getRandom();
					 * System.out.println("Random otp: "+rand);
					 */
					HttpSession session = request.getSession();
					session.setAttribute("userMail", userMail);
					session.setAttribute("userMobile", userMobile);
					session.setAttribute("userName", userName);
					session.setAttribute("userId", userId);
					if(userGivenPinCode==null) {
						userGivenPinCode=userPinCode;
					}
					session.setAttribute("userPinCode", userGivenPinCode);

					System.out.println("pincode for searching: "+userGivenPinCode);
					RequestDispatcher rd = request.getRequestDispatcher("UserHomePage.html");
					rd.include(request, response);
				} else {
					System.out.println("Wrong Password");
					RequestDispatcher rd = request.getRequestDispatcher("UserLogin.html");
					rd.include(request, response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
