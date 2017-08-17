package edu.webapde.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import edu.webapde.bean.Photo;
import edu.webapde.bean.Tag;

public class TagService {
	public static List<Tag> getAllTags() {
		List<Tag> tagList = null;

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqldb");
		EntityManager em = emf.createEntityManager();

		EntityTransaction trans = em.getTransaction();

		try {
			trans.begin();
			String hql = "SELECT * FROM tags t ORDER BY t.tagId DESC;";
			Query q = em.createNativeQuery(hql, Tag.class);
			tagList = q.getResultList();
			trans.commit();
			System.out.println("Result from getAllTags(): " + tagList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			em.close();
		}

		return tagList;
	}
	
	public static Tag addTag(Tag tag) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqldb");
		EntityManager em = emf.createEntityManager();

		EntityTransaction trans = em.getTransaction();
		
		if(isTagFound(tag)) {
			return getTag(tag.getTagname());
		}

		try {
			trans.begin();
			em.persist(tag);
			trans.commit();
			System.out.println("Result from addTag(tag): " + tag);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			em.close();
		}
		return tag;
	}
	
	public static Tag getTag(String tagname) {
		Tag t = null;

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqldb");
		EntityManager em = emf.createEntityManager();

		EntityTransaction trans = em.getTransaction();

		try {
			trans.begin();
			
			String hql = "SELECT * FROM tags t WHERE tagname = '" + tagname + "';"; 
			Query q = em.createNativeQuery(hql, Tag.class); 
			if(q.getResultList().size() > 0)
				t = (Tag) q.getResultList().get(0);
			trans.commit();
			System.out.println("Result from getPhotos(id): " + t);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			em.close();
		}
		return t;
	}
	
	public static boolean isTagFound(Tag tag) {
		boolean b = false;
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqldb");
		EntityManager em = emf.createEntityManager();

		EntityTransaction trans = em.getTransaction();

		try {
			trans.begin();
			
			String hql = "SELECT * FROM tags t WHERE tagname = '" + tag.getTagname() + "';"; 
			System.out.println(hql);
			Query q = em.createNativeQuery(hql, Tag.class); 
			if(q.getResultList().size() > 0)
				b = true;
			trans.commit();
			System.out.println("Result from isTagFound(tag): " + tag);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			em.close();
		}
		return b;
	}
}
