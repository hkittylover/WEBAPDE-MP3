package edu.webapde.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import edu.webapde.bean.User;

public class UserService {
	public static boolean addUser(User u) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqldb");
		EntityManager em = emf.createEntityManager();

		EntityTransaction trans = em.getTransaction();

		u.setUsername(u.getUsername().toLowerCase());

		// if the username already exist in the DB
		if (isUserFound(u.getUsername())) {
			System.out.println("From addUser(u): username already exist.");
			return false;
		}

		boolean b = false;

		try {
			trans.begin();
			em.persist(u);
			trans.commit();
			b = true;
		} catch (Exception e) {
			// TODO: handle exception
			if (trans != null)
				trans.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}
		return b;
	}

	public static User getUser(String username) {
		List<User> u = null;

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqldb");
		EntityManager em = emf.createEntityManager();

		EntityTransaction trans = em.getTransaction();

		try {
			trans.begin();
			TypedQuery<User> q = em.createQuery("FROM users WHERE username = '" + username + "'", User.class);
			u = q.getResultList();
			trans.commit();

			if (u.size() > 0) {
				System.out.println("Found user!");
				return u.get(0);
			} else {
				System.out.println("User not found...");
			}
			// System.out.println("ERROR: Password does not match!");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			em.close();
		}

		return null;
	}

	public static User getUser(int userId) {
		User u = null;

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqldb");
		EntityManager em = emf.createEntityManager();

		EntityTransaction trans = em.getTransaction();

		try {
			trans.begin();
			u = em.find(User.class, userId);
			trans.commit();

			if (u != null) {
				System.out.println("Found user!");
			} else {
				System.out.println("User not found...");
			}
			// System.out.println("ERROR: Password does not match!");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			em.close();
		}

		return u;
	}

	public static boolean isUserFound(String username) {
		List<User> u = null;

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqldb");
		EntityManager em = emf.createEntityManager();

		EntityTransaction trans = em.getTransaction();

		boolean b = false;

		try {
			trans.begin();
			TypedQuery<User> q = em.createQuery("FROM users WHERE username = '" + username + "'", User.class);
			u = q.getResultList();
			trans.commit();

			// if the user is found
			if (u.size() != 0)
				b = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			em.close();
		}

		return b;
	}
	
	public static List<String> getAllUsername() {
		List<String> usernames = null;

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqldb");
		EntityManager em = emf.createEntityManager();

		EntityTransaction trans = em.getTransaction();

		boolean b = false;

		try {
			trans.begin();
			TypedQuery<String> q = em.createQuery("SELECT username FROM users", String.class);
			usernames = q.getResultList();
			trans.commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			em.close();
		}
		
		return usernames;
	}
	
	public static String getUserDescription(int userId) {
		User u = null;

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqldb");
		EntityManager em = emf.createEntityManager();

		EntityTransaction trans = em.getTransaction();

		String str = "";

		try {
			trans.begin();
			u = em.find(User.class, userId);
			trans.commit();

			if (u != null) {
				// check if the password is equal
				str = u.getDescription();
			} else {
				System.out.println("User not found...");
			}
			// System.out.println("ERROR: Password does not match!");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			em.close();
		}

		return str;
	}
	
	public static String getUserDescription(String username) {
		String description = "";

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqldb");
		EntityManager em = emf.createEntityManager();

		EntityTransaction trans = em.getTransaction();

		boolean b = false;

		try {
			trans.begin();
			TypedQuery<String> q = em.createQuery("SELECT description FROM users WHERE username = '" + username + "'", String.class);
			if(q.getResultList().size() > 0)
				description = q.getResultList().get(0);
			trans.commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			em.close();
		}
		
		return description;
	}

	public static void main(String[] args) {
		User u = new User("Chiu", "krazykrizia");
		if (addUser(u))
			System.out.println(u.toString());
		else
			System.out.println("ERROR");

		System.out.println(getAllUsername());
	}
}
