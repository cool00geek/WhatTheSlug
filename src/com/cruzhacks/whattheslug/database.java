package com.cruzhacks.whattheslug;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
//import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

@WebServlet("/database")
public class database extends HttpServlet {

    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // reading the user input
        String event_name = request.getParameter("event_name");
        String event_date = request.getParameter("event_date").replaceAll("-", "/");
        String event_time = request.getParameter("event_time");
        String event_loc = request.getParameter("event_loc");
        String event_room = request.getParameter("event_room");
        System.out.println("Event Name: " + event_name);
        System.out.println("Event Date: " + event_date);
        System.out.println("Event Time: " + event_time);
        System.out.println("Event loca: " + event_loc);
        System.out.println("Event Room: " + event_room);

        response.sendRedirect("/");

        response.setContentType("text/html;charset=UTF-8");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();

        // Write the response message, in an HTML page
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html><head>");
            out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
            out.println("<title>Hello, World</title></head>");
            out.println("<body>");
            out.println("<p>Request URI: " + event_name + "</p>");
            out.println("<p>Protocol: " + event_date + "</p>");
            out.println("<p>PathInfo: " + event_time + "</p>");
            out.println("<p>Remote Address: " + event_loc + "</p>");
            // Generate a random number upon each request
            out.println("<p>A Random Number: <strong>" + event_room + "</strong></p>");
            out.println("<p>Now for the cool stuff</p>");
            Scanner scanner = new Scanner(new File("data.csv"));
            while (scanner.hasNextLine()) {
                out.println("<p>" + scanner.nextLine() + "</p>");
            }
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();  // Always close the output writer
        }

        try {
            event_date = checkDate(event_date);
        } catch (Exception e) {
            response.sendRedirect("/#baddate");
        }

        try {
            String t = checkTime(event_time);
            String realAm = "";
            if (event_time.length() > 5) {
                realAm = amPm(event_time.substring(event_time.length() - 2));
            }
            event_time = t + realAm;
        } catch (Exception e) {
            response.sendRedirect("/#badtime");
        }

        // TODO Add to db
        //uploadData(event_name,event_date, event_time, event_loc, event_room);
        try {
            saveToCsv(event_name, event_date, event_time, event_loc, event_room);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // https://examples.javacodegeeks.com/enterprise-java/servlet/java-servlet-sendredirect-example/
        //response.sendRedirect("/#success");
    }

    private static String amPm(String amPm) {
        if (amPm.equalsIgnoreCase("am")) {
            amPm = "AM";
        } else if (amPm.equalsIgnoreCase("pm")) {
            amPm = "PM";
        } else {
            throw new NumberFormatException();
        }
        return amPm;
    }

    private static String checkTime(String time) {
        if (time.indexOf(":") == 1) {
            time = "0" + time;
        }
        boolean amPmTrue = true;
        if (time.length() < 6) {
            amPmTrue = false;
        }
        if (time.substring(time.indexOf(":")).length() > 5) {
            throw new NumberFormatException();
        }
        time = time.substring(0, 5);

        LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        int hour = localTime.get(ChronoField.CLOCK_HOUR_OF_DAY);
        int minute = localTime.get(ChronoField.MINUTE_OF_HOUR);

        String amPm = "";
        if (!(hour < 0 || hour > 24 || minute < 0 || minute > 59)) {
            if (hour == 24 || hour == 12) {
                hour = 12;
                amPm = "AM";
            }
            if (hour > 12) {
                //Military Time Conversion
                hour -= 12;
                amPm = "PM";
            }
        } else {
            System.out.println("INVALID");
            throw new NumberFormatException();
        }

        String hr = "";
        String min;

        if (hour >= 1 && hour <= 9) {
            hr = "0" + hour;
        } else if (hour >= 10) {
            hr = "" + hour;
        }

        if (minute <= 9) {
            min = "0" + minute;
        } else {
            min = "" + minute;
        }
        if (amPmTrue) {
            return hr + ":" + min + " ";
        } else {
            return hr + ":" + min + " " + amPm;
        }
    }

    private static String checkDate(String date) {
        if (date.trim().equals("")) {
            return "";
        } else {
            int year = Integer.parseInt(date.substring(0, date.indexOf("/")));
            int month = Integer.parseInt(date.substring(date.indexOf("/") + 1, date.lastIndexOf("/")));
            int day = Integer.parseInt(date.substring(date.lastIndexOf("/") + 1));

            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);

            int currentYear = cal.get(Calendar.YEAR);

            String yr;
            String mo;
            String d;

            if (year >= (currentYear)) {
                yr = "" + year;
            } else {
                throw new NumberFormatException();
            }

            if (month == 1 || month == 3 || month == 5 || month == 7
                    || month == 8 || month == 10 || month == 12) {
                if (day <= 31 && day >= 1) {
                    mo = "" + month;
                    d = "" + day;
                } else {
                    throw new NumberFormatException();
                }
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                if (day <= 30 && day >= 1) {
                    mo = "" + month;
                    d = "" + day;
                } else {
                    throw new NumberFormatException();
                }
            } else if (month == 2) {
                if (day <= 28 && day >= 1) {
                    mo = "" + month;
                    d = "" + day;
                } else {
                    throw new NumberFormatException();
                }
            } else {
                throw new NumberFormatException();
            }

            return yr + "/" + mo + "/" + d;
        }
    }

    private static void printSources() {
        System.out.println("https://examples.javacodegeeks.com/enterprise-java/servlet/java-servlet-jsp-example/");
        System.out.println("https://crunchify.com/servlet-tutorial-getting-starting-with-jsp-servlet-example/");
    }

    private void uploadData(String name, String date, String time, String loc, String room) {
        // The configuration object specifies behaviors for the connection pool.
        HikariConfig config = new HikariConfig();

// Configure which instance and what database user to connect with.
        //config.setJdbcUrl(String.format("jdbc:mysql:///%s", "SlugEvents"));
        config.setJdbcUrl("jdbc:mysql://whattheslugisgoingon/SlugEvents");
        config.setUsername("root"); // e.g. "root", "postgres"
        config.setPassword("GoSlugs"); // e.g. "my-password"

// For Java users, the Cloud SQL JDBC Socket Factory can provide authenticated connections.
// See https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory for details.
        config.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", "whattheslugisgoingon:us-west2:whattheslugisgoingon");
        config.addDataSourceProperty("useSSL", "false");

        config.setMaximumPoolSize(5);
// minimumIdle is the minimum number of idle connections Hikari maintains in the pool.
// Additional connections will be established to meet this value unless the pool is full.
        config.setMinimumIdle(5);

        config.setConnectionTimeout(10000); // 10 seconds
// idleTimeout is the maximum amount of time a connection can sit in the pool. Connections that
// sit idle for this many milliseconds are retried if minimumIdle is exceeded.
        config.setIdleTimeout(600000); // 10 minutes

        config.setMaxLifetime(1800000); // 30 minutes

// ... Specify additional connection properties here.

// ...

// Initialize the connection pool using the configuration object.
        DataSource pool = new HikariDataSource(config);


        try (Connection conn = pool.getConnection()) {

            // PreparedStatements can be more efficient and project against injections.
            PreparedStatement voteStmt = conn.prepareStatement(
                    "INSERT INTO events (event_name, event_date, event_time, event_loc, event_room) VALUES (?, ?, ?, ?, ?);");
            voteStmt.setString(1, name);
            voteStmt.setDate(2, (java.sql.Date) new Date(date));
            //voteStmt.setTime(3, new Time(time));
            voteStmt.setTime(3, new Time(198235));
            voteStmt.setString(4, loc);
            voteStmt.setString(5, room);

            // Finally, execute the statement. If it fails, an error will be thrown.
            voteStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveToCsv(String name, String date, String time, String loc, String room) throws IOException, InterruptedException {


        ProcessBuilder pb = new ProcessBuilder("curl", "52.183.97.242:54213",
                "-H",
                "Content-Type: application/json",
                "--data", "{\"name\":\"" + name + "\", \"date\":\"" + date + "\", \"time\":\"" + time + "\", \"location\":\"" + loc + "\", \"room\":\"" + room + "\"}");

        Process p;
        try {
            p = pb.start();
            InputStream is = p.getInputStream();
            InputStream errorStream = p.getErrorStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedReader br = new BufferedReader(new InputStreamReader(errorStream));
            String strLine = "";
            while ((strLine = br.readLine()) != null) {
                System.out.println(strLine);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void copy(InputStream input, OutputStream output) throws IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = input.read(buffer);
            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
        } finally {
            input.close();
            output.close();
        }
    }

    private GcsFilename getFileName(HttpServletRequest req) {
        String[] splits = req.getRequestURI().split("/", 4);
        if (!splits[0].equals("") || !splits[1].equals("gcs")) {
            throw new IllegalArgumentException("The URL is not formed as expected. " +
                    "Expecting /gcs/<bucket>/<object>");
        }
        return new GcsFilename(splits[2], splits[3]);
    }
}
