package onRoadBreakDown_miniProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class UserRequestProcessing
 */
@WebServlet("/UserRequestProcessing")
public class UserRequestProcessing extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserRequestProcessing() {
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

		List<String> servicesSelected = new ArrayList<>();

		System.out.println("sern: " + request.getParameter("MechAsst"));
		if (request.getParameter("MechAsst") != null) {
			servicesSelected.add("MechAsst");
		}
		if (request.getParameter("BatRepl") != null) {
			servicesSelected.add("BatRepl");
		}
		if (request.getParameter("TyreRepl") != null) {
			servicesSelected.add("TyreRepl");
		}
		if (request.getParameter("VechTow") != null) {
			servicesSelected.add("VechTow");
		}
		if (request.getParameter("FuelRef") != null) {
			servicesSelected.add("FuelRef");
		}
		if (request.getParameter("Servicing") != null) {
			servicesSelected.add("Servicing");
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/OnRoadBreakDown", "root","Taraka123");

			HttpSession session = request.getSession();
			String userPinCode = (String) session.getAttribute("userPinCode");
			String userName = (String) session.getAttribute("userName");
			String userMobile = (String) session.getAttribute("userMobile");
			String userMail = (String) session.getAttribute("userMail");
			int userId = Integer.parseInt((String) session.getAttribute("userId"));

			PreparedStatement pstmt = con
					.prepareStatement("select * from serviceProvider where serviceProviderPinCode=?");
			pstmt.setString(1, userPinCode);
			ResultSet rs = pstmt.executeQuery();
			List<String> availableServiceProviders = new ArrayList<>();
			while (rs.next()) {
				int flag = 0;
				for (String service : servicesSelected) {
					if (!rs.getString(service).equals("YES")) {
						flag = 1;
						break;
					}
				}
				if (flag == 0) {
					availableServiceProviders.add(rs.getInt("serviceProviderId") + "");
				}
			}

			System.out.println(servicesSelected + " " + userPinCode);
			System.out.println(availableServiceProviders);

			Random random = new Random();
			int sizeOfList = availableServiceProviders.size();
			if(sizeOfList>0) {
			int selectedSp = random.nextInt(sizeOfList);
			System.out.println("Size of list : " + sizeOfList + " selected service provider : " + selectedSp);

			int selectedServiceProvider = Integer.parseInt(availableServiceProviders.get(selectedSp));
			System.out.println("servId :" + selectedServiceProvider);

			String mailBody="Sorry for Inconvience. We couldn't provide Service Providers near you.";
			
			
			PreparedStatement pstmt2 = con.prepareStatement(
					"select * from serviceProvider where serviceProviderId=" + selectedServiceProvider + ";");
			// pstmt.setInt(1, selectedServiceProvider);
            
			ResultSet rs2 = pstmt2.executeQuery();
			if (rs2.next()) {

				PreparedStatement pstmt3 = con.prepareStatement(
						"insert into transaction(serviceProviderId,serviceProviderName,userId,userName,userPinCode,userMobile) values(?,?,?,?,?,?);");
				mailBody= "Service Provider details are: "+rs2.getString("serviceProviderName")+" "+rs2.getString("serviceProviderMobile")+ " "+rs2.getString("serviceProviderAddress")+" "+rs2.getString("serviceProviderMail");
				
				pstmt3.setInt(1, rs2.getInt("serviceProviderId"));
				pstmt3.setString(2, rs2.getString("serviceProviderName"));
				pstmt3.setInt(3, userId);
				pstmt3.setString(4, userName);
				pstmt3.setString(5, userPinCode);
				pstmt3.setString(6, userMobile);

				int rs3 = pstmt3.executeUpdate();
                if(rs3>0) {
                	SendingMail sm=new SendingMail();
                	sm.setMailServerProperties();
        			sm.createEmailMessage(userMail, mailBody);
        			sm.sendEmail();
                	System.out.println("Entered into Transaction table successfully");
                }
				out.println("<html>");
				out.println("<head>");
				out.println("<title>Details of the Requested Service Provider");
				out.println("</title>");
				out.println("\r\n" + 
						"    <meta charset=\"UTF-8\">\r\n" + 
						"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + 
						"    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\r\n" + 
						"    <style>\r\n" + 
						"            body {\r\n" + 
						"      background-color: whitesmoke;\r\n" + 
						"    }\r\n" + 
						"    ul {\r\n" + 
						"            list-style-type: none;\r\n" + 
						"           \r\n" + 
						"            overflow: hidden;\r\n" + 
						"            background-color:purple;\r\n" + 
						"          }\r\n" + 
						"          \r\n" + 
						"          li {\r\n" + 
						"            float: left;\r\n" + 
						"          }\r\n" + 
						"          \r\n" + 
						"          li a {\r\n" + 
						"            display: block;\r\n" + 
						"            color: white;\r\n" + 
						"            text-align: center;\r\n" + 
						"            padding: 14px 16px;\r\n" + 
						"            text-decoration: none;\r\n" + 
						"          }\r\n" + 
						"          .margin\r\n" + 
						"      {\r\n" + 
						"  \r\n" + 
						"  margin-right: 80px;\r\n" + 
						"  margin-left: 80px;\r\n" + 
						"}\r\n" + 
						"\r\n" + 
						"          </style>");
				out.println("</head>");
				out.println("<body>");
				out.println("<ul>\r\n" + 
						"                <li><a href=\"home.html\">Home</a></li>\r\n" + 
						"               \r\n" + 
						"                <li><a href=\"aboutus.html\">About Us</a></li>\r\n" + 
						"               \r\n" + 
						"                \r\n" + 
						"                <li><a href=\"contactus.html\">Contact Us</a></li>\r\n" + 
						"                <li><a href=\"reviews.html\">Reviews</a></li>\r\n" + 
						"                <li><a href=\"faqs.html\">FAQ's</a></li>\r\n" + 
						"              </ul>");
				out.println("<center><h2>The Details of the Requested Service Provider</h2></center>" + "<br />");
				out.println("<center>Name : " + rs2.getString("serviceProviderName") + "<br />");
				out.println("Mail id : " + rs2.getString("serviceProviderMail") + "<br />");
				out.println("Mobile Number : " + rs2.getString("serviceProviderMobile") + "<br />");
				out.println("Pin Code : " + rs2.getString("serviceProviderPinCode") + "<br />");
				out.println("Address : " + rs2.getString("serviceProviderAddress") + "</center><br />");

				out.println("<form method='get' action='UserLogout'><br/>");
				out.println("<center><input type=\"submit\" name=\"submit\" value=\"Logout\"></center><br><br>");
				out.println("</form></body>");
				out.println("</html>");

			} else {
				System.out.println("RS EMPTY::::::::::::");
			}
			}else {
				out.println("<html>");
				out.println("<head>");
				out.println("<title>Details of the Requested Service Provider");
				out.println("</title>");
				out.println("</head>");
				out.println("<body>");
				out.println("Sorry we couldn't provide you the Requested Service Provider" + "<br />");
				out.println("</body>");
				out.println("</html>");
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
		//doGet(request, response);
		PrintWriter out = response.getWriter();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/OnRoadBreakDown", "root",
					"Taraka123");

			HttpSession session = request.getSession();
			int serviceProviderId = Integer.parseInt((String) session.getAttribute("serviceProviderId"));

			PreparedStatement pstmt = con.prepareStatement("select * from transaction where serviceProviderId=?");
			pstmt.setInt(1, serviceProviderId);
			ResultSet rs = pstmt.executeQuery();
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Details of the Services Provided By You: ");
			out.println("</title>");
			out.println("\r\n" + 
					"    <meta charset=\"UTF-8\">\r\n" + 
					"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + 
					"    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\r\n" + 
					"    <style>\r\n" + 
					"            body {\r\n" + 
					"      background-color: whitesmoke;\r\n" + 
					"    }\r\n" + 
					"    ul {\r\n" + 
					"            list-style-type: none;\r\n" + 
					"           \r\n" + 
					"            overflow: hidden;\r\n" + 
					"            background-color:purple;\r\n" + 
					"          }\r\n" + 
					"          \r\n" + 
					"          li {\r\n" + 
					"            float: left;\r\n" + 
					"          }\r\n" + 
					"          \r\n" + 
					"          li a {\r\n" + 
					"            display: block;\r\n" + 
					"            color: white;\r\n" + 
					"            text-align: center;\r\n" + 
					"            padding: 14px 16px;\r\n" + 
					"            text-decoration: none;\r\n" + 
					"          }\r\n" + 
					"          .margin\r\n" + 
					"      {\r\n" + 
					"  \r\n" + 
					"  margin-right: 80px;\r\n" + 
					"  margin-left: 80px;\r\n" + 
					"}\r\n" + 
					"\r\n" + 
					"          </style>");
			out.println("</head>");
			out.println("<body>");
			out.println("<ul>\r\n" + 
					"                <li><a href=\"home.html\">Home</a></li>\r\n" + 
					"               \r\n" + 
					"                <li><a href=\"aboutus.html\">About Us</a></li>\r\n" + 
					"               \r\n" + 
					"                \r\n" + 
					"                <li><a href=\"contactus.html\">Contact Us</a></li>\r\n" + 
					"                <li><a href=\"reviews.html\">Reviews</a></li>\r\n" + 
					"                <li><a href=\"faqs.html\">FAQ's</a></li>\r\n" + 
					"              </ul>");
			out.println("<center><h1> Details of the Services Provided By You:</h1></center>" + "<br />");
			out.println("<center>");
			out.println("<table border='1' width='700px'>\r\n" + "  <tr>\r\n" + "    <th>User Name</th>\r\n"
					+ "    <th>User Mobile Number</th>\r\n" + "    <th>Pin Code Of the area </th>\r\n" + "  </tr>");
			while (rs.next()) {
				out.println("<tr>");
				out.println("<td>" + rs.getString("userName") + "</td>");
				out.println("<td>" + rs.getString("userMobile") + "</td>");
				out.println("<td>" + rs.getString("userPinCode") + "</td></tr>");
			}
			out.println("</center></table>");
			out.println("<form method='get' action='ServiceProviderLogout'><br/>");
			out.println("<center><input type=\"submit\" name=\"submit\" value=\"Logout\"></center><br><br>");
			out.println("</form></body>");
			out.println("</html>");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
