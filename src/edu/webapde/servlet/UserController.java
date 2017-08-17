package edu.webapde.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.webapde.bean.Photo;
import edu.webapde.bean.User;
import edu.webapde.service.PhotoService;
import edu.webapde.service.UserService;

/**
 * Servlet implementation class UserController
 */
@WebServlet(urlPatterns = { "/homepage", "/login", "/register", "/logout", "/userpage", "/hasusername", "/verifyaccount" })
public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserController() {
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
		// response.getWriter().append("Served at:
		// ").append(request.getContextPath());
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String urlPattern = request.getServletPath();
		System.out.println(urlPattern);
		HttpSession session = request.getSession();
		if(session.getAttribute("role") == null) {
			checkRole(request, response);
		}
		request.setAttribute("action", "none");
		switch (urlPattern) {
		case "/homepage":
			request.setAttribute("action", "homepage");
			goToHomepage(request, response);
			break;
		case "/login":
			request.setAttribute("action", "login");
			loginUser(request, response);
			break;
		case "/register":
			request.setAttribute("action", "register");
			registerUser(request, response);
			break;
		case "/logout":
			request.setAttribute("action", "logout");
			logoutUser(request, response);
			break;
		case "/userpage":
			request.setAttribute("action", "userpage");
			goToUserpage(request, response);
			break;
		case "/hasusername":
			request.setAttribute("action", "hasusername");
			hasUsername(request, response);
			break;
		case "/verifyaccount":
			request.setAttribute("action", "verifyaccount");
			verifyAccount(request, response);
			break;
		default:
			break;
		}
	}
	

	private void checkRole(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		Cookie[] cookies = request.getCookies();
		String cookieValue1 = null;
		String cookieValue2 = null;
		
		if (cookies != null) {
			for (Cookie c : cookies) {
				// find cookie to check if remembered
				if (c.getName().equals("oink")) {
					// get the value
					cookieValue1 = c.getValue();
				} else if (c.getName().equals("oinkoink")) {
					// get the value
					cookieValue2 = c.getValue();
				}
			}

			// if there is cookie
			if (cookieValue1 != null && cookieValue2 != null) {
				String username = cookieValue1;
				String password = "";
				String[] arr = cookieValue2.split("@%g&#HDjm68ysc@%g&#HDjm6");
				for (int i = 0; i < arr.length; i++) {
					password += arr[i];
				}
				User u = UserService.getUser(cookieValue1, password);

				// set session for username
				session.setAttribute("sUsername", username);
				session.setAttribute("sDescription", u.getDescription());
				session.setAttribute("role", "user");

				System.out.println("LOGGED IN");
			}

			// if not found go public
			else {
				session.setAttribute("sUsername", "");
				session.setAttribute("sDescription", "");
				session.setAttribute("role", "guest");
				
				System.out.println("AM I HERE???????????");
			}
		} else {
			System.out.println("cookie not found");
			session.setAttribute("role", "0");
			session.setAttribute("sUsername", "");
			session.setAttribute("sDescription", "");
		}
	}

	private void goToHomepage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// find cookie for user
		HttpSession session = request.getSession();
		Cookie[] cookies = request.getCookies();
		String cookieValue1 = null;
		String cookieValue2 = null;

		if (cookies != null) {
			for (Cookie c : cookies) {
				// find cookie to check if remembered
				if (c.getName().equals("oink")) {
					// get the value
					cookieValue1 = c.getValue();
				} else if (c.getName().equals("oinkoink")) {
					// get the value
					cookieValue2 = c.getValue();
				}
			}

			// if there is cookie
			if (cookieValue1 != null && cookieValue2 != null) {
				String username = cookieValue1;
				String password = "";
				String[] arr = cookieValue2.split("@%g&#HDjm68ysc@%g&#HDjm6");
				for (int i = 0; i < arr.length; i++) {
					password += arr[i];
				}
				User u = UserService.getUser(cookieValue1, password);

				// set session for username
				session.setAttribute("sUsername", username);
				session.setAttribute("sDescription", u.getDescription());
				session.setAttribute("role", "user");
				
				// set attributes to request
				request.setAttribute("user", u);

				request.setAttribute("username", username);

				request.setAttribute("description", u.getDescription());

				List<Photo> publicPhotoList = PhotoService.getAllPublicPhotos();
				request.setAttribute("publicPhotoList", publicPhotoList);
				
				List<Photo> sharedPhotoList = PhotoService.getAllSharedPhotos(username);
				request.setAttribute("sharedPhotoList", sharedPhotoList);

				request.setAttribute("role", "user");

				System.out.println("LOGGED IN");
				// forward to success page or page if success
				RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
				rd.forward(request, response);
			}

			// if not found go public
			else {
				session.setAttribute("sUsername", "");
				session.setAttribute("sDescription", "");
				session.setAttribute("role", "guest");
				
				System.out.println("AM I HERE???????????");
				List<Photo> publicPhotoList = PhotoService.getAllPublicPhotos();
				request.setAttribute("publicPhotoList", publicPhotoList);
				
				request.setAttribute("sharedPhotoList", "[]");
				request.setAttribute("role", "guest");

				RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
				rd.forward(request, response);
			}
		} else {
			System.out.println("cookie not found");
			session.setAttribute("role", "0");
			List<Photo> publicPhotoList = PhotoService.getAllPublicPhotos();
			request.setAttribute("publicPhotoList", publicPhotoList);
			request.setAttribute("sharedPhotoList", "[]");
			request.setAttribute("role", "0");

			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);
		}
	}

	private void loginUser(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		HttpSession session = request.getSession();
		String username = request.getParameter("username").toLowerCase();
		String password = request.getParameter("password");
		String remember = request.getParameter("remember");
		System.out.println("CONTROLLER username: " + username);
		System.out.println("CONTROLLER password: " + password);
		User u = UserService.getUser(username, password);

		// if the user is found
		if (u != null) {
			// set session for username
			session.setAttribute("sUsername", username);
			session.setAttribute("sDescription", u.getDescription());
			session.setAttribute("role", "user");
			
			// set attributes to request
			request.setAttribute("username", username);

			request.setAttribute("description", u.getDescription());

			List<Photo> publicPhotoList = PhotoService.getAllPublicPhotos();
			request.setAttribute("publicPhotoList", publicPhotoList);
			List<Photo> sharedPhotoList = PhotoService.getAllSharedPhotos(username);
			request.setAttribute("sharedPhotoList", sharedPhotoList);

			request.setAttribute("role", "user");

			String generatedStr = "";

			for (int i = 0; i < password.length(); i++) {
				generatedStr += password.toCharArray()[i];
				if (i + 1 < password.length())
					generatedStr += "@%g&#HDjm68ysc@%g&#HDjm6";
			}

			// create cookie
			Cookie usernameCookie = new Cookie("oink", username);
			Cookie passwordCookie = new Cookie("oinkoink", generatedStr);

			// if remember me is checked
			if (remember != null && remember.equals("remember")) {
				usernameCookie.setMaxAge(60 * 60 * 24 * 365 * 3);
				passwordCookie.setMaxAge(60 * 60 * 24 * 365 * 3);
			}

			response.addCookie(usernameCookie);
			response.addCookie(passwordCookie);

			System.out.println("LOGGED IN");
			// forward to success page or page if success
			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);
		}

		// if the user is not found or the password is wrong
		else {
			session.setAttribute("role", "guest");
			
			// go to failed page or same page
			System.out.println("FAILED TO LOG IN");
			request.setAttribute("ERROR", "failed");

			List<Photo> publicPhotoList = PhotoService.getAllPublicPhotos();
			request.setAttribute("publicPhotoList", publicPhotoList);
			
			request.setAttribute("sharedPhotoList", "[]");

			RequestDispatcher rd = request.getRequestDispatcher("homepage");
			rd.forward(request, response);
		}
	}

	private void registerUser(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String username = request.getParameter("username").toLowerCase();
		String password = request.getParameter("password");
		String description = request.getParameter("description");

		User u = new User();
		u.setUsername(username);
		u.setPassword(password.toCharArray());
		u.setDescription(description);

		boolean flag = UserService.addUser(u);

		// if the user registered successfully
		if (flag) {
			// set session for username
			session.setAttribute("sUsername", username);
			session.setAttribute("sDescription", u.getDescription());
			session.setAttribute("role", "user");
			
			// set attributes to request
			request.setAttribute("username", username);

			request.setAttribute("description", u.getDescription());

			List<Photo> publicPhotoList = PhotoService.getAllPublicPhotos();
			request.setAttribute("publicPhotoList", publicPhotoList);
			
			List<Photo> sharedPhotoList = PhotoService.getAllSharedPhotos(username);
			request.setAttribute("sharedPhotoList", sharedPhotoList);

			request.setAttribute("role", "user");
			
			String generatedStr = "";

			for (int i = 0; i < password.length(); i++) {
				generatedStr += password.toCharArray()[i];
				if (i + 1 < password.length())
					generatedStr += "@%g&#HDjm68ysc@%g&#HDjm6";
			}

			// create cookie
			Cookie usernameCookie = new Cookie("oink", username);
			Cookie passwordCookie = new Cookie("oinkoink", generatedStr);

			response.addCookie(usernameCookie);
			response.addCookie(passwordCookie);
			
			System.out.println("REGISTERED");
			// forward to success page or page if success
			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);
		}

		// if the username exists or registered failed
		else {
			session.setAttribute("role", "guest");
			
			// go to failed page or same page
			System.out.println("FAILED TO REGISTER");
			request.setAttribute("ERROR", "failed");

			List<Photo> publicPhotoList = PhotoService.getAllPublicPhotos();
			request.setAttribute("publicPhotoList", publicPhotoList);
			
			request.setAttribute("sharedPhotoList", "[]");

			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);
		}
	}

	private void logoutUser(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// kill cookie username
		Cookie[] cookies = request.getCookies();
		
		HttpSession session = request.getSession();
		session.setAttribute("sUsername", "");
		session.setAttribute("sDescription", "");
		session.setAttribute("role", "guest");
		
		for (Cookie c : cookies) {
			// find username cookie and kill it
			if (c.getName().equals("oink") || c.getName().equals("oinkoink")) {
				// kill it
				c.setMaxAge(0);
				response.addCookie(c);
			}
		}
		
		List<Photo> publicPhotoList = PhotoService.getAllPublicPhotos();
		request.setAttribute("publicPhotoList", publicPhotoList);
		
		request.setAttribute("sharedPhotoList", "[]");
		
		request.setAttribute("role", "guest");
		
		// redirect to non-logged in page
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		rd.forward(request, response);
	}
	
	private void goToUserpage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("sUsername");
		String otherUser = request.getParameter("user");
		System.out.println(username);
		System.out.println(otherUser);
		if(username.equals(otherUser)) {
			List<Photo> photoList = PhotoService.getAllMyPhotos(username);
			request.setAttribute("photoList", photoList);
			
			request.setAttribute("username", username);
			request.setAttribute("description", UserService.getUserDescription(username));
		} else if(!username.equals("")){
			List<Photo> photoList = PhotoService.getAllSharedPhotos(username, otherUser);
			System.out.println(photoList);
			request.setAttribute("photoList", photoList);
			
			request.setAttribute("username", otherUser);
			request.setAttribute("description", UserService.getUserDescription(otherUser));
		} else {
			List<Photo> photoList = PhotoService.getAllPublicPhotos(otherUser);
			System.out.println(photoList);
			request.setAttribute("photoList", photoList);
			
			request.setAttribute("username", otherUser);
			request.setAttribute("description", UserService.getUserDescription(otherUser));
		}
			
		
		RequestDispatcher rd = request.getRequestDispatcher("userpage.jsp");
		rd.forward(request, response);
	}
	
	private void hasUsername(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String username = request.getParameter("username");

		boolean b = UserService.isUserFound(username);

		String bool = String.valueOf(b);

		response.getWriter().write(bool);
	}
	
	private void verifyAccount(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		User u = UserService.getUser(username, password);

		if(u != null)
			response.getWriter().write(String.valueOf(true));
		else
			response.getWriter().write(String.valueOf(false));
	}
}
