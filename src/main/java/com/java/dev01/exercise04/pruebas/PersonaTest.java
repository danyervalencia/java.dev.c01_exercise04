package com.java.dev01.exercise04.pruebas;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import com.java.dev01.exercise04.pojos.Persona;
import com.java.dev01.exercise04.util.HibernateUtil;

public class PersonaTest {
    public static void main(String[] args) {
        // Set up database tables
        HibernateUtil.tableDrop("drop table public.personas");
        HibernateUtil.sqlExecute("create table public.personas(pers_id serial, pers_paternal VARCHAR(50), pers_maternal VARCHAR(50), pers_name VARCHAR(50))");

        // Create SessionFactory and Session object
        SessionFactory sessions = new Configuration().configure().buildSessionFactory();
        Session session = sessions.openSession();

        // Perform life-cycle operations under a transaction
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // Crea un objeto persona y la graba a la base de datos
            Persona p1 = new Persona();
            //p1.setId(101);
            p1.setPaternal("Valencia");
            p1.setMaternal("LLamoca");
            p1.setName("Danyer");
            session.save(p1); // pase por referencia

            // Crea otra persona y la agrega a la base de datos
            Persona p2 = new Persona();
            //p2.setId(102);
            p2.setPaternal("Valencia");
            p2.setMaternal("Castro");
            p2.setName("Oscar");
            session.save(p2);

            // Crea tercera persona y la agrega a la base de datos
            Persona p3 = new Persona();
            //p3.setId(103);
            p3.setPaternal("Aldea");
            p3.setMaternal("Pezo");
            p3.setName("Juan");
            session.save(p3);

            // Crea tercera persona y la agrega a la base de datos
            Persona p4 = new Persona();
            //p4.setId(104);
            p4.setPaternal("Vasquez");
            p4.setMaternal("Cardenas");
            p4.setName("Axel");
            session.save(p4);

            // Obtiene objetos de la base de datos
            Persona persona = (Persona) session.get(Persona.class, p1.getId());
            System.out.println("Primer registro => " + persona.getPaternal() +", "+ persona.getName());

            persona = (Persona) session.get(Persona.class, p2.getId());
            System.out.println("Segunda registro => " + persona.getPaternal() +", "+ persona.getName());

            persona = (Persona) session.get(Persona.class, p3.getId());
            System.out.println("Tercer registro => " + persona.getPaternal() +", "+ persona.getName());

            persona = (Persona) session.get(Persona.class, p4.getId());
            System.out.println("Cuarta registro => " + persona.getPaternal() +", "+ persona.getName());

            tx.commit();
            tx = null;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        HibernateUtil.dataSelect("select * from public.personas");
    }
}