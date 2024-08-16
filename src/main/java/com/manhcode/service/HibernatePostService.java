package com.manhcode.service;

import com.manhcode.model.Post;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

@Service
public class HibernatePostService implements IPostService {

    private static SessionFactory sessionFactory;
    private static EntityManager entityManager;

    static {
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.conf.xml")
                    .buildSessionFactory();
            entityManager = sessionFactory.createEntityManager();
        }catch (HibernateException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> findAll() {
        String queryStr = "SELECT p FROM Post AS p";
        TypedQuery<Post> query = entityManager.createQuery(queryStr, Post.class);
        return query.getResultList();
    }

    @Override
    public void save(Post post) {
        Transaction transaction = null;
        Post origin;
        if (post.getId() == 0) {
            origin = new Post();
        } else {
            origin = findById(post.getId());
        }
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            origin.setTitle(post.getTitle());
            origin.setContent(post.getContent());
            origin.setDescription(post.getDescription());
            origin.setImage(post.getImage());
            session.saveOrUpdate(origin);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public Post findById(int id) {
        String queryStr = "SELECT p FROM Post AS p WHERE p.id = :id";
        TypedQuery<Post> query = entityManager.createQuery(queryStr, Post.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public void update(int id, Post post) {
        Transaction transaction = null;
        Post existingPost = findById(id);

        if (existingPost != null) {
            try (Session session = sessionFactory.openSession()) {
                transaction = session.beginTransaction();

                // Update the fields of the existing song with the new values
                existingPost.setTitle(post.getTitle());
                existingPost.setContent(post.getContent());
                existingPost.setDescription(post.getDescription());
                existingPost.setImage(post.getImage());

                // Save the updated song to the database
                session.saveOrUpdate(existingPost);

                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
                if (transaction != null) {
                    transaction.rollback();
                }
            }
        }
    }

    @Override
    public void remove(int id) {
        Post post = findById(id);
        if (post != null) {
            Transaction transaction = null;
            try (Session session = sessionFactory.openSession()) {
                transaction = session.beginTransaction();
                session.remove(post);
                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
                if (transaction != null) {
                    transaction.rollback();
                }
            }
        }
    }
}
