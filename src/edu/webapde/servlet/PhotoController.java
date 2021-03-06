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

import com.google.gson.Gson;

import edu.webapde.bean.Photo;
import edu.webapde.bean.User;
import edu.webapde.service.PhotoService;
import edu.webapde.service.TagService;
import edu.webapde.service.UserService;

/**
 * Servlet implementation class PhotoController
 */
@WebServlet(urlPatterns = { "/allphotos", "/upload", "/share", "/photodetails", "/tag", "/search", "/photo", "/deletetag" })
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
		List<String> tags = TagService.getAllTagnames();
		request.setAttribute("tagnames", new Gson().toJson(tags));
		List<String> users = UserService.getAllUsername();
		request.setAttribute("usernames", new Gson().toJson(users));
		switch (urlPattern) {
		case "/allphotos":
			request.setAttribute("action", "allphotos");
			getAllPhotos(request, response);
			break;
		case "/upload":
			request.setAttribute("action", "upload");
			uploadPhoto(request, response);
			break;
		case "/share":
			request.setAttribute("action", "share");
			sharePhoto(request, response);
			break;
		case "/photodetails":
			request.setAttribute("action", "photodetails");
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
		case "/deletetag":
			request.setAttribute("action", "deletetag");
			deleteTag(request, response);
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
				User u = UserService.getUser(cookieValue1);
				if(u.isPasswordEqual(password)) {
				// set session for username
					session.setAttribute("sUsername", u.getUsername());
					session.setAttribute("sUserId", u.getUserId());
					session.setAttribute("role", "user");
	
					System.out.println("LOGGED IN");
				} 
				else {
					session.setAttribute("sUsername", "");
					session.setAttribute("sUserId", "");
					session.setAttribute("role", "guest");

					System.out.println("AM I HERE???????????");
				}
			}

			// if not found go public
			else {
				session.setAttribute("sUsername", "");
				session.setAttribute("sUserId", "");
				session.setAttribute("role", "guest");

				System.out.println("AM I HERE???????????");
			}
		} else {
			System.out.println("cookie not found");
			session.setAttribute("role", "0");
			session.setAttribute("sUsername", "");
			session.setAttribute("sUserId", "");
		}
	}

	private void getAllPhotos(HttpServletRequest request, HttpServletResponse response) {

	}
	
	private void deleteTag(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		int userId = (int) session.getAttribute("sUserId");
		int photoId = Integer.parseInt(request.getParameter("photoId"));
		String tagname = request.getParameter("tagname");
		Photo p = PhotoService.getPhoto(photoId);
		System.out.println("I AM HEREE");
		System.out.println(p.getUser().getUserId());
		System.out.println(userId);
		if(p.getUser().getUserId() == userId) {
			p.removeTag(tagname);
			PhotoService.updatePhoto(p.getPhotoId(), p);
			response.getWriter().write("true");
		} else {
			response.getWriter().write("false");
		}
	}

	private void uploadPhoto(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String role = (String) session.getAttribute("role");

		int userId = (int) session.getAttribute("sUserId");
		String title = request.getParameter("title");
		String description = request.getParameter("description");
		String filepath = "img/" + request.getParameter("file");
		String privacy = request.getParameter("selector");
		String[] tags = request.getParameter("tags").split(", +");
		String[] allowedUsers = request.getParameter("allowed").split(", +");
		User user = UserService.getUser(userId);
		Photo p = new Photo(user, title, description, filepath, privacy);

		for (String t : tags) {
			p.addTag(t);
		}

		for (String s : allowedUsers) {
			User u = UserService.getUser(s);
			p.addAllowedUser(u);
		}

		PhotoService.addPhoto(p);

		List<Photo> photoList = PhotoService.getAllMyPhotos(userId);
		request.setAttribute("photoList", new Gson().toJson(photoList));
		
		request.setAttribute("username", user.getUsername());
		request.setAttribute("description", user.getDescription());

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
		response.setContentType("application/json");
		if (!p.containInList(p.getAllowedUsers(), UserService.getUser(username)) && p.getPrivacy().equals("private")
				&& !p.getUser().getUsername().equals(username)) {
			p = null;
			response.getWriter().write("null");
		} else
			response.getWriter().write(new Gson().toJson(p));
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
		String action = request.getParameter("action");
		request.setAttribute("keyword", keyword);
		System.out.println("ROLE: " + role);

		if (role == null)
			role = "";

		if (keyword == null)
			keyword = "";
		if(action.equalsIgnoreCase("NONE")) {
			if (role.equals("user")) {
				String username = (String) session.getAttribute("sUsername");
				List<Photo> publicPhotoList = PhotoService.getAllPublicPhotosByTag(keyword);
				request.setAttribute("publicPhotoList", new Gson().toJson(publicPhotoList));
	
				List<Photo> sharedPhotoList = PhotoService.getAllSharedPhotosByTag(keyword, username);
				request.setAttribute("sharedPhotoList", new Gson().toJson(sharedPhotoList));
	
				List<Photo> myPhotoList = PhotoService.getAllMyPhotosByTag(keyword, username);
				request.setAttribute("myPhotoList", new Gson().toJson(myPhotoList));
	
				System.out.println("SEARCHING AS USER...");
				// forward to success page or page if success
				RequestDispatcher rd = request.getRequestDispatcher("search.jsp");
				rd.forward(request, response);
			} else {
				List<Photo> publicPhotoList = PhotoService.getAllPublicPhotosByTag(keyword);
				request.setAttribute("publicPhotoList", new Gson().toJson(publicPhotoList));
				request.setAttribute("sharedPhotoList", "[]");
				request.setAttribute("myPhotoList", "[]");
				System.out.println("SEARCHING AS GUEST...");
				// forward to success page or page if success
				RequestDispatcher rd = request.getRequestDispatcher("search.jsp");
				rd.forward(request, response);
			}
		} else {
			String keyword2 = request.getParameter("keyword2");
			if (keyword2 == null)
				keyword2 = "";
			if (role.equals("user")) {
				String username = (String) session.getAttribute("sUsername");
				if(action.equals("AND")) {
					String hql = "(t.tagname = 'game' OR t.tagname = 'best') GROUP BY p.photoId HAVING COUNT(t.tagId) = 2";
					List<Photo> publicPhotoList = PhotoService.getAllPublicPhotosByCustomTag(hql);
					request.setAttribute("publicPhotoList", new Gson().toJson(publicPhotoList));
		
					List<Photo> sharedPhotoList = PhotoService.getAllSharedPhotosByCustomTag(hql, username);
					request.setAttribute("sharedPhotoList", new Gson().toJson(sharedPhotoList));
		
					List<Photo> myPhotoList = PhotoService.getAllMyPhotosByCustomTag(hql, username);
					request.setAttribute("myPhotoList", new Gson().toJson(myPhotoList));
				} else if(action.equals("OR")) {
					String hql = "(t.tagname = '" + keyword + "' OR t.tagname = '" + keyword2 + "')  GROUP BY p.photoId";
					List<Photo> publicPhotoList = PhotoService.getAllPublicPhotosByCustomTag(hql);
					request.setAttribute("publicPhotoList", new Gson().toJson(publicPhotoList));
		
					List<Photo> sharedPhotoList = PhotoService.getAllSharedPhotosByCustomTag(hql, username);
					request.setAttribute("sharedPhotoList", new Gson().toJson(sharedPhotoList));
		
					List<Photo> myPhotoList = PhotoService.getAllMyPhotosByCustomTag(hql, username);
					request.setAttribute("myPhotoList", new Gson().toJson(myPhotoList));
				} else {
					request.setAttribute("publicPhotoList", "[]");
					request.setAttribute("sharedPhotoList", "[]");
					request.setAttribute("myPhotoList", "[]");
				}
				System.out.println("SEARCHING AS USER...");
				// forward to success page or page if success
				RequestDispatcher rd = request.getRequestDispatcher("search.jsp");
				rd.forward(request, response);
			} else {
				if(action.equals("AND")) {
					String hql = "(t.tagname = 'game' OR t.tagname = 'best') GROUP BY p.photoId HAVING COUNT(t.tagId) = 2";
					List<Photo> publicPhotoList = PhotoService.getAllPublicPhotosByCustomTag(hql);
					request.setAttribute("publicPhotoList", new Gson().toJson(publicPhotoList));
					request.setAttribute("sharedPhotoList", "[]");
					request.setAttribute("myPhotoList", "[]");
				} else if(action.equals("OR")) {
					String hql = "(t.tagname = '" + keyword + "' OR t.tagname = '" + keyword2 + "')  GROUP BY p.photoId";
					List<Photo> publicPhotoList = PhotoService.getAllPublicPhotosByCustomTag(hql);
					request.setAttribute("publicPhotoList", new Gson().toJson(publicPhotoList));
					request.setAttribute("sharedPhotoList", "[]");
					request.setAttribute("myPhotoList", "[]");
				} else {
					request.setAttribute("publicPhotoList", "[]");
					request.setAttribute("sharedPhotoList", "[]");
					request.setAttribute("myPhotoList", "[]");
				}
				
				System.out.println("SEARCHING AS GUEST...");
				// forward to success page or page if success
				RequestDispatcher rd = request.getRequestDispatcher("search.jsp");
				rd.forward(request, response);
			}
		}
	}

	private void showPhoto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println(request.getParameterNames());
		HttpSession session = request.getSession();
		String role = (String) session.getAttribute("role");
		if (role.equals("user")) {
			String username = (String) session.getAttribute("sUsername");
			List<Photo> publicPhotoList = PhotoService.getAllPublicPhotos();
			request.setAttribute("publicPhotoList", new Gson().toJson(publicPhotoList));

			List<Photo> sharedPhotoList = PhotoService.getAllSharedPhotos(username);
			request.setAttribute("sharedPhotoList", new Gson().toJson(sharedPhotoList));

			request.setAttribute("role", "user");
		}
		else {
			List<Photo> publicPhotoList = PhotoService.getAllPublicPhotos();
			request.setAttribute("publicPhotoList", new Gson().toJson(publicPhotoList));

			request.setAttribute("sharedPhotoList", "[]");

			request.setAttribute("role", "guest");
		}

		System.out.println("SHOW PHOTO");
		// forward to success page or page if success
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		rd.forward(request, response);
	}
}
