package com.java.dev01.exercise04.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    public static String url = "jdbc:postgresql://localhost:5444/edb";
    public static String dbdriver = "org.postgresql.Driver";
    public static String username = "enterprisedb";
    public static String password = "Magaly123";

    public static final SessionFactory sessionFactory;
    public static final ThreadLocal session = new ThreadLocal();
    static Connection conn;
    static Statement st;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Fallo inicial en la creación de un SessionFactory." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session sessionCurrent() throws HibernateException {
        Session sess = (Session) session.get();
        if (sess == null) {
            sess = sessionFactory.openSession();
            session.set(sess);
        }
        return sess;
    }

    public static void sessionClose() throws HibernateException {
        Session sess = (Session) session.get();
        if (sess != null)
            sess.close();
        session.set(null);
    }

    public static void createStatement() {
        try {
            Class.forName(dbdriver);
            conn = DriverManager.getConnection(url, username, password);
            st = conn.createStatement();
        } catch (Exception e) {
            System.err.println("Ha ocurrido una Excepción! ");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void sqlExecute(String sql) {
        try {
            createStatement();
            st.executeUpdate(sql);
        } catch (Exception e) {
            System.err.println("Ha ocurrido una Excepción! ");
            e.printStackTrace();
            System.exit(0);
        }
    }

    // Elimina tabla si existe
    public static void tableDrop(String sql) {
        try {
            createStatement();
            st.executeUpdate(sql);
        } catch (Exception e) {
        }
    }

    public static void dataSelect(String sql) {
        String[] sqlSplit = sql.split(" ");
        System.out.println("\n*+*+*+*+*+* Tabla: " + sqlSplit[sqlSplit.length - 1] + " *+*+*+*+*+*");
        try {
            createStatement();
            ResultSet r = st.executeQuery(sql);
            HibernateUtil.resultsetOut(r);
            //conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resultsetOut(ResultSet rs) throws Exception {
        ResultSetMetaData metadata = rs.getMetaData();

        int numcols = metadata.getColumnCount();
        String[] labels = new String[numcols];
        int[] colwidths = new int[numcols];
        int[] colpos = new int[numcols];
        int linewidth;

        linewidth = 1;
        for (int i = 0; i < numcols; i++) {
            colpos[i] = linewidth;
            labels[i] = metadata.getColumnLabel(i + 1); // get its label
            int size = metadata.getColumnDisplaySize(i + 1);
            if (size > 30 || size == -1)
                size = 30;
            int labelsize = labels[i].length();
            if (labelsize > size)
                size = labelsize;
            colwidths[i] = size + 1; // save the column the size
            linewidth += colwidths[i] + 2; // increment total size
        }

        StringBuffer divider = new StringBuffer(linewidth);
        StringBuffer blankline = new StringBuffer(linewidth);
        for (int i = 0; i < linewidth; i++) {
            divider.insert(i, '-');
            blankline.insert(i, " ");
        }
        // Put special marks in the divider line at the column positions
        for (int i = 0; i < numcols; i++)
            divider.setCharAt(colpos[i] - 1, '+');
        divider.setCharAt(linewidth - 1, '+');

        // The next line of the table contains the column labels.
        // Begin with a blank line, and put the column names and column
        // divider characters "|" into it. overwrite() is defined below.
        StringBuffer line = new StringBuffer(blankline.toString());
        line.setCharAt(0, '|');
        for (int i = 0; i < numcols; i++) {
            int pos = colpos[i] + 1 + (colwidths[i] - labels[i].length()) / 2;
            overwrite(line, pos, labels[i]);
            overwrite(line, colpos[i] + colwidths[i], " |");
        }

        while (rs.next()) {
            line = new StringBuffer(blankline.toString());
            line.setCharAt(0, '|');
            for (int i = 0; i < numcols; i++) {
                Object value = rs.getObject(i + 1);
                if (value != null) {
                    overwrite(line, colpos[i] + 1, value.toString().trim());
                    overwrite(line, colpos[i] + colwidths[i], " |");
                }
            }
            System.out.println(line);
        }
        System.out.println(divider);
    }

    static void overwrite(StringBuffer b, int pos, String s) {
        int len = s.length();
        for (int i = 0; i < len; i++)
            b.setCharAt(pos + i, s.charAt(i));
    }
}