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
 * Servlet implementation class PhotoController
 */
@WebServlet(urlPatterns = { "/allphotos", "/alltags", "/upload", "/share", "/view", "/tag", "/search", "/photo" })
public class PhotoController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PhotoController() {
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
		HttpSession session = request.getSession();
		if (session.getAttribute("role") == null) {
			checkRole(request, response);
		}
		request.setAttribute("action", "none");
		switch (urlPattern) {
		case "/allphotos":
			getAllPhotos(request, response);
			break;
		case "/alltags":
			getAllTags(request, response);
			break;
		case "/upload":
			request.setAttribute("action", "upload");
			uploadPhoto(request, response);
			break;
		case "/share":
			request.setAttribute("action", "share");
			sharePhoto(request, response);
			break;
		case "/view":
			request.setAttribute("action", "view");
			viewPhoto(request, response);
			break;
		case "/tag":
			request.setAttribute("action", "tag");
			tagPhoto(request, response);
			break;
		case "/search":
			request.setAttribute("action", "search");
			searchPhoto(request, response);
			break;
		case "/photo":
			request.setAttribute("action", "photo");
			showPhoto(request, response);
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

	private void getAllPhotos(HttpServletRequest request, HttpServletResponse response) {

	}

	private void getAllTags(HttpServletRequest request, HttpServletResponse response) {

	}

	private void uploadPhoto(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String role = (String) session.getAttribute("role");

		String username = (String) session.getAttribute("sUsername");
		String title = request.getParameter("title");
		String description = request.getParameter("description");
		String filepath = "img/" + request.getParameter("file");
		String privacy = request.getParameter("selector");
		String date = "";
		String[] tags = request.getParameter("tags").split(", +");
		String[] allowedUsers = request.getParameter("allowed").split(", +");
		// new Photo(username, title, description, filepath, privacy, date)

		Photo p = new Photo(username, title, description, filepath, privacy, date);

		for (String t : tags) {
			p.addTag(t);
		}

		for (String u : allowedUsers) {
			p.addAllowedUser(u);
		}

		PhotoService.addPhoto(p);

		List<Photo> photoList = PhotoService.getAllMyPhotos(username);
		request.setAttribute("photoList", photoList);
		
		request.setAttribute("username", username);
		request.setAttribute("description", UserService.getUserDescription(username));

		RequestDispatcher rd = request.getRequestDispatcher("userpage.jsp");
		rd.forward(request, response);
	}

	private void sharePhoto(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int photoId = Integer.parseInt(request.getParameter("id"));
		String user = request.getParameter("user");

		boolean b = PhotoService.addAllowedUserToPhoto(photoId, user);

		String bool = String.valueOf(b);

		response.getWriter().write(bool);
	}

	private void viewPhoto(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int photoId = Integer.parseInt(request.getParameter("id"));
		HttpSession session = request.getSession();
		String username = ((String) session.getAttribute("sUsername")).toLowerCase();

		Photo p = PhotoService.getPhoto(photoId);
		response.setContentType("text/plain");
		if (!p.getAllowedUsers().contains(username) && p.getPrivacy().equals("private")
				&& !p.getUsername().equals(username)) {
			p = null;
			response.getWriter().write("null");
		} else
			response.getWriter().write(p.toString());
	}

	private void tagPhoto(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int photoId = Integer.parseInt(request.getParameter("id"));
		String tag = request.getParameter("tag");

		boolean b = PhotoService.addTagsToPhoto(photoId, tag);

		String bool = String.valueOf(b);

		response.getWriter().write(bool);
	}

	private void searchPhoto(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String role = (String) session.getAttribute("role");
		String keyword = request.getParameter("keyword");
		request.setAttribute("keyword", keyword);
		System.out.println("ROLE: " + role);

		if (role == null)
			role = "";

		if (keyword == null)
			keyword = "";

		if (role.equals("user")) {
			String username = (String) session.getAttribute("sUsername");
			List<Photo> publicPhotoList = PhotoService.getPublicPhotosByTag(keyword);
			request.setAttribute("publicPhotoList", publicPhotoList);

			List<Photo> sharedPhotoList = PhotoService.getSharedPhotosByTag(keyword, username);
			request.setAttribute("sharedPhotoList", sharedPhotoList);

			List<Photo> myPhotoList = PhotoService.getMyPhotosByTag(keyword, username);
			request.setAttribute("myPhotoList", myPhotoList);

			System.out.println("SEARCHING AS USER...");
			// forward to success page or page if success
			RequestDispatcher rd = request.getRequestDispatcher("search.jsp");
			rd.forward(request, response);
		} else {
			List<Photo> publicPhotoList = PhotoService.getPublicPhotosByTag(keyword);
			request.setAttribute("publicPhotoList", publicPhotoList);
			request.setAttribute("sharedPhotoList", "[]");
			request.setAttribute("myPhotoList", "[]");
			System.out.println("SEARCHING AS GUEST...");
			// forward to success page or page if success
			RequestDispatcher rd = request.getRequestDispatcher("search.jsp");
			rd.forward(request, response);
		}
	}

	private void showPhoto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String role = (String) session.getAttribute("role");
		if (role.equals("user")) {
			String username = (String) session.getAttribute("sUsername");
			List<Photo> publicPhotoList = PhotoService.getAllPublicPhotos();
			request.setAttribute("publicPhotoList", publicPhotoList);

			List<Photo> sharedPhotoList = PhotoService.getAllSharedPhotos(username);
			request.setAttribute("sharedPhotoList", sharedPhotoList);

			request.setAttribute("role", "user");
		}
		else {
			List<Photo> publicPhotoList = PhotoService.getAllPublicPhotos();
			request.setAttribute("publicPhotoList", publicPhotoList);

			request.setAttribute("sharedPhotoList", "[]");

			request.setAttribute("role", "guest");
		}

		System.out.println("SHOW PHOTO");
		// forward to success page or page if success
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		rd.forward(request, response);
	}
}
