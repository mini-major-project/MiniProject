package onRoadBreakDown_miniProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

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
 * Servlet implementation class ServiceProviderRegistration
 */
@WebServlet("/ServiceProviderRegistration")
public class ServiceProviderRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServiceProviderRegistration() {
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
		PrintWriter out = response.getWriter();

		String serviceProviderMail = request.getParameter("serviceProviderMail");
		String serviceProviderPswd = request.getParameter("serviceProviderPswd");
		String serviceProviderPswd2 = request.getParameter("serviceProviderPswd2");
		String serviceProviderName = request.getParameter("serviceProviderName");
		String serviceProviderAddress = request.getParameter("serviceProviderAddress");
		String serviceProviderPinCode = request.getParameter("serviceProviderPinCode");
		String serviceProviderMobile = request.getParameter("serviceProviderMobile");
		
		String MechAsst = "";
		String BatRepl = "";
		String TyreRepl = "";
		String VechTow = "";
		String FuelRef = "";
		String Servicing = "";
		System.out.println("sern: " +request.getParameter("MechAsst") );
		if (request.getParameter("MechAsst")!=null ) {
			MechAsst = "YES";
		} else {
			MechAsst = "NO";
		}
		if (request.getParameter("BatRepl")!=null) {
			BatRepl = "YES";
		} else {
			BatRepl = "NO";
		}
		
		if (request.getParameter("TyreRepl")!=null ) {
			TyreRepl = "YES";
		} else {
			TyreRepl = "NO";
		}
		if (request.getParameter("VechTow")!=null) {
			VechTow = "YES";
		} else {
			VechTow = "NO";
		}
		if (request.getParameter("FuelRef")!=null ) {
			FuelRef = "YES";
		} else {
			FuelRef = "NO";
		}
		if (request.getParameter("Servicing")!=null ) {
			Servicing = "YES";
		} else {
			Servicing = "NO";
		}
		
		
		SendingMail sm = new SendingMail();
		String generatedotp = sm.getRandom();
		System.out.println("Generated otp: " + generatedotp);

		System.out.println("Before including otp: ");
		HttpSession session = request.getSession();
		session.setAttribute("generatedotp", generatedotp);
		session.setAttribute("serviceProviderMail", serviceProviderMail);
		session.setAttribute("serviceProviderMobile", serviceProviderMobile);
		session.setAttribute("serviceProviderName", serviceProviderName);
		session.setAttribute("serviceProviderPinCode", serviceProviderPinCode);
		session.setAttribute("serviceProviderPswd", serviceProviderPswd);
		session.setAttribute("serviceProviderAddress", serviceProviderAddress);
		session.setAttribute("generatedotp", generatedotp);
		
		session.setAttribute("MechAsst", MechAsst);
		session.setAttribute("BatRepl", BatRepl);
		session.setAttribute("TyreRepl", TyreRepl);
		session.setAttribute("VechTow", VechTow);
		session.setAttribute("FuelRef", FuelRef);
		session.setAttribute("Servicing", Servicing);

		try {
			sm.setMailServerProperties();
			sm.createEmailMessage(serviceProviderMail, generatedotp);
			sm.sendEmail();
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RequestDispatcher rd = request.getRequestDispatcher("EnterOtpSp.html");
		rd.forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		
		SendingMail sm = new SendingMail();

		String enteredotp = request.getParameter("otp");
		System.out.println("entered otp by user is: " + enteredotp);

		HttpSession session = request.getSession();

		String serviceProviderPinCode = (String) session.getAttribute("serviceProviderPinCode");
		String serviceProviderName = (String) session.getAttribute("serviceProviderName");
		String serviceProviderMobile = (String) session.getAttribute("serviceProviderMobile");
		String serviceProviderMail = (String) session.getAttribute("serviceProviderMail");
		String serviceProviderPswd = (String) session.getAttribute("serviceProviderPswd");
		String serviceProviderAddress = (String) session.getAttribute("serviceProviderAddress");
		String generatedotp = (String) session.getAttribute("generatedotp");
		
		String MechAsst = (String) session.getAttribute("MechAsst");
		String BatRepl = (String) session.getAttribute("BatRepl");
		String TyreRepl = (String) session.getAttribute("TyreRepl");
		String VechTow = (String) session.getAttribute("VechTow");
		String FuelRef = (String) session.getAttribute("FuelRef");
		String Servicing = (String) session.getAttribute("Servicing");

		System.out.println(
				serviceProviderName + " " + serviceProviderMobile + " " + serviceProviderMail + " " + serviceProviderPswd + " " + serviceProviderAddress + " " + serviceProviderPinCode);

		if (enteredotp.equals(generatedotp)) {
			// sm.insert(userName, userPswd, userAddress, userMobile, userMail,
			// userPinCode);
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/OnRoadBreakDown", "root",
					"Taraka123");
			PreparedStatement pstmt = con.prepareStatement(
					"insert into serviceProvider(serviceProviderName,serviceProviderPswd,serviceProviderAddress,serviceProviderMobile,serviceProviderMail,serviceProviderPinCode,MechAsst,BatRepl,TyreRepl,VechTow,FuelRef,Servicing) values(?,?,?,?,?,?,?,?,?,?,?,?);");
			PasswordEncryptionDecryption p;
			String encryptedServiceProviderPswd;
			p = new PasswordEncryptionDecryption();
			encryptedServiceProviderPswd = p.encrypt(serviceProviderPswd);
			pstmt.setString(1, serviceProviderName);
			pstmt.setString(2, encryptedServiceProviderPswd);
			pstmt.setString(3, serviceProviderAddress);
			pstmt.setString(4, serviceProviderMobile);
			pstmt.setString(5, serviceProviderMail);
			pstmt.setString(6, serviceProviderPinCode);
			// MechAsst,BatRepl,TyreRepl,VechTow,FuelRef,Servicing
			pstmt.setString(7, MechAsst);
			pstmt.setString(8, BatRepl);
			pstmt.setString(9, TyreRepl);
			pstmt.setString(10, VechTow);
			pstmt.setString(11, FuelRef);
			pstmt.setString(12, Servicing);

			int rs = pstmt.executeUpdate();
			if (rs > 0) {
				System.out.println("Registered Successfully- into DB");
				RequestDispatcher rd = request.getRequestDispatcher("ServiceProviderLogin.html");
				rd.forward(request, response);
			} else {
				System.out.print("Error");
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("OTP verified - Registered Successfully");
		
		RequestDispatcher rd = request.getRequestDispatcher("ServiceProviderLogin.html");
		rd.forward(request, response);
		
	} else {
		System.out.println("Invalid OTP");
	}

}

}
