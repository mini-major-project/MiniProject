package onRoadBreakDown_miniProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class UserRegistration
 */
@WebServlet("/UserRegistration")
public class UserRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserRegistration() {
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
		// response.getWriter().append("Served at: ").append(request.getContextPath());
		PrintWriter out = response.getWriter();

		String userMail = request.getParameter("userMail");
		String userPswd = request.getParameter("userPswd");
		String userPswd2 = request.getParameter("userPswd2");
		String userName = request.getParameter("userName");
		String userAddress = request.getParameter("userAddress");
		String userPinCode = request.getParameter("userPinCode");
		String userMobile = request.getParameter("userMobile");

		SendingMail sm = new SendingMail();
		String generatedotp = sm.getRandom();
		System.out.println("Generated otp: " + generatedotp);

		System.out.println("Before including otp: ");
		HttpSession session = request.getSession();
		session.setAttribute("generatedotp", generatedotp);
		session.setAttribute("userMail", userMail);
		session.setAttribute("userMobile", userMobile);
		session.setAttribute("userName", userName);
		session.setAttribute("userPinCode", userPinCode);
		session.setAttribute("userPswd", userPswd);
		session.setAttribute("userAddress", userAddress);
		session.setAttribute("generatedotp", generatedotp);

		try {
			sm.setMailServerProperties();
			sm.createEmailMessage(userMail, "Your OTP is:" + generatedotp);
			sm.sendEmail();
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RequestDispatcher rd = request.getRequestDispatcher("EnterOtp.html");
		rd.forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);
		SendingMail sm = new SendingMail();

		String enteredotp = request.getParameter("otp");
		System.out.println("entered otp by user is: " + enteredotp);

		HttpSession session = request.getSession();

		String userPinCode = (String) session.getAttribute("userPinCode");
		String userName = (String) session.getAttribute("userName");
		String userMobile = (String) session.getAttribute("userMobile");
		String userMail = (String) session.getAttribute("userMail");
		String userPswd = (String) session.getAttribute("userPswd");
		String userAddress = (String) session.getAttribute("userAddress");
		String generatedotp = (String) session.getAttribute("generatedotp");

		System.out.println(
				userName + " " + userMobile + " " + userMail + " " + userPswd + " " + userAddress + " " + userPinCode);

		if (enteredotp.equals(generatedotp)) {
			// sm.insert(userName, userPswd, userAddress, userMobile, userMail,
			// userPinCode);
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/OnRoadBreakDown", "root",
						"Taraka123");
				PreparedStatement pstmt = con.prepareStatement(
						"insert into user(userName,userPswd,userAddress,userMobile,userMail,userPinCode) values(?,?,?,?,?,?);");
				PasswordEncryptionDecryption p;
				String encryptedUserPswd;
				p = new PasswordEncryptionDecryption();
				encryptedUserPswd = p.encrypt(userPswd);

				pstmt.setString(1, userName);
				pstmt.setString(2, encryptedUserPswd);
				pstmt.setString(3, userAddress);
				pstmt.setString(4, userMobile);
				pstmt.setString(5, userMail);
				pstmt.setString(6, userPinCode);

				int rs = pstmt.executeUpdate();
				if (rs > 0) {
					System.out.println("Registered Successfully");
					RequestDispatcher rd = request.getRequestDispatcher("UserLogin.html");
					rd.forward(request, response);
				} else {
					System.out.print("Error");
				}
			}

			catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("OTP verified - Registered Successfully");

			RequestDispatcher rd = request.getRequestDispatcher("UserLogin.html");
			rd.forward(request, response);

		} else {
			System.out.println("Invalid OTP");
		}

	}

}
