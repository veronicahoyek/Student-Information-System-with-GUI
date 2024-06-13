package com.example.project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BddComm {
    private Connection connection;
    private int connectedID;

    public BddComm() {
    }

    public int connect(String jdbcUrl, String username, String password) {
        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connected to the database!");
            return 1;
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            return 0;
        }
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Disconnected from database!");
            }
        } catch (SQLException e) {
            System.err.println("Error disconnecting from the database: " + e.getMessage());
        }
    }

    public String authenticate(String user, String pass) {
        if (user.equals("admin@gmail.com") && pass.equals("admin"))
            return "Admin";
        String studentQuery = "SELECT * FROM student WHERE mail = ? AND password = ?";
        String instructorQuery = "SELECT * FROM instructor WHERE mail = ? AND password = ?";
        try (PreparedStatement studentStatement = connection.prepareStatement(studentQuery);
             PreparedStatement instructorStatement = connection.prepareStatement(instructorQuery)) {
            studentStatement.setString(1, user);
            studentStatement.setString(2, pass);
            ResultSet studentResult = studentStatement.executeQuery();
            if (studentResult.next()) {
                connectedID = studentResult.getInt("studentid");
                System.out.println(connectedID);
                return "Student";
            }
            instructorStatement.setString(1, user);
            instructorStatement.setString(2, pass);
            ResultSet instructorResult = instructorStatement.executeQuery();
            if (instructorResult.next()) {
                connectedID = instructorResult.getInt("instructorid");
                System.out.println(connectedID);
                return "Teacher";
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return "null";
        }
        return "null";
    }

    public List<String> getInstructors() {
        List<String> instructors = new ArrayList<>();
        String query = "SELECT firstname,lastname FROM instructor WHERE firstname <> 'Admin' OR lastname <> 'Admin'";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String instructorName = resultSet.getString("firstname") + " " + resultSet.getString("lastname");
                instructors.add(instructorName);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching instructors: " + e.getMessage());
        }
        return instructors;
    }

    public void addCourse(int insid, String title, String offerday, String startTime, String endTime, int credits, String courseDescription) {
        String query = "INSERT INTO course (insid, title, offerday, start_time, end_time, credits, coursedescription) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, insid);
            statement.setString(2, title);
            statement.setString(3, offerday);
            statement.setString(4, startTime);
            statement.setString(5, endTime);
            statement.setInt(6, credits);
            statement.setString(7, courseDescription);
            statement.executeUpdate();
            System.out.println("Course added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
        }
    }

    public int getInstructorId(String fullName) {
        int instructorId = -1;
        String[] names = fullName.split(" ");
        String firstName = names[0];
        String lastName = names[1];
        String query = "SELECT instructorid FROM instructor WHERE firstname = ? AND lastname = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                instructorId = resultSet.getInt("instructorid");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching instructor ID: " + e.getMessage());
        }
        return instructorId;
    }

    public boolean isInstructorAvailable(String instructorName, String offerDay, String startTime) {
        boolean isAvailable = false;
        String[] names = instructorName.split(" ");
        String firstName = names[0];
        String lastName = names[1];
        try {
            String query = "SELECT * FROM course c, instructor i WHERE c.insid = i.instructorid AND i.firstname = ? AND i.lastname = ? AND c.offerday = ? AND c.start_time = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, offerDay);
            statement.setString(4, startTime);

            ResultSet resultSet = statement.executeQuery();
            isAvailable = !resultSet.next();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return isAvailable;
        }
        return isAvailable;
    }

    public void addStudent(String firstName, String lastName, String email, String password) {
        String query = "INSERT INTO student (firstname, lastname, mail, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, password);
            statement.executeUpdate();
            System.out.println("Student added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
        }
    }

    public void addTeacher(String firstName, String lastName, String email, String password) {
        String query = "INSERT INTO instructor (firstname, lastname, mail, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, password);
            statement.executeUpdate();
            System.out.println("Teacher added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding teacher: " + e.getMessage());
        }
    }

    public boolean deleteStudent(int studentId) {
        String deleteEnrollmentsQuery = "DELETE FROM enrollment WHERE sid = ?";
        String deleteAbsencesQuery = "DELETE FROM absences WHERE enrid IN (SELECT enrollmentid FROM enrollment WHERE sid = ?)";
        String deleteGradesQuery = "DELETE FROM grades WHERE eid IN (SELECT enrollmentid FROM enrollment WHERE sid = ?)";
        String deleteStudentQuery = "DELETE FROM student WHERE studentid = ?";
        try (PreparedStatement deleteEnrollmentsStmt = connection.prepareStatement(deleteEnrollmentsQuery);
             PreparedStatement deleteAbsencesStmt = connection.prepareStatement(deleteAbsencesQuery);
             PreparedStatement deleteGradesStmt = connection.prepareStatement(deleteGradesQuery);
             PreparedStatement deleteStudentStmt = connection.prepareStatement(deleteStudentQuery)) {
            connection.setAutoCommit(false);
            deleteEnrollmentsStmt.setInt(1, studentId);
            deleteEnrollmentsStmt.executeUpdate();
            deleteAbsencesStmt.setInt(1, studentId);
            deleteAbsencesStmt.executeUpdate();
            deleteGradesStmt.setInt(1, studentId);
            deleteGradesStmt.executeUpdate();
            deleteStudentStmt.setInt(1, studentId);
            int affectedRows = deleteStudentStmt.executeUpdate();
            connection.commit();
            if (affectedRows > 0) {
                System.out.println("Student deleted successfully!");
                return true;
            } else {
                System.out.println("Student with ID " + studentId + " doesn't exist!");
                return false;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTeacher(int instructorId) {
        String deleteCoursesQuery = "DELETE FROM course WHERE insid = ?";
        String deleteEnrollmentsQuery = "DELETE FROM enrollment WHERE cid IN (SELECT courseid FROM course WHERE insid = ?)";
        String deleteGradesQuery = "DELETE FROM grades WHERE eid IN (SELECT enrollmentid FROM enrollment WHERE cid IN (SELECT courseid FROM course WHERE insid = ?))";
        String deleteTeacherQuery = "DELETE FROM instructor WHERE instructorid = ?";
        try (PreparedStatement deleteCoursesStmt = connection.prepareStatement(deleteCoursesQuery);
             PreparedStatement deleteEnrollmentsStmt = connection.prepareStatement(deleteEnrollmentsQuery);
             PreparedStatement deleteGradesStmt = connection.prepareStatement(deleteGradesQuery);
             PreparedStatement deleteTeacherStmt = connection.prepareStatement(deleteTeacherQuery)) {
            connection.setAutoCommit(false);
            deleteCoursesStmt.setInt(1, instructorId);
            deleteCoursesStmt.executeUpdate();
            deleteEnrollmentsStmt.setInt(1, instructorId);
            deleteEnrollmentsStmt.executeUpdate();
            deleteGradesStmt.setInt(1, instructorId);
            deleteGradesStmt.executeUpdate();
            deleteTeacherStmt.setInt(1, instructorId);
            int affectedRows = deleteTeacherStmt.executeUpdate();
            connection.commit();
            if (affectedRows > 0) {
                System.out.println("Teacher deleted successfully!");
                return true;
            } else {
                System.out.println("Teacher with ID " + instructorId + " doesn't exist!");
                return false;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error deleting teacher: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCourse(int courseId) {
        String deleteEnrollmentsQuery = "DELETE FROM enrollment WHERE cid = ?";
        String deleteGradesQuery = "DELETE FROM grades WHERE eid IN (SELECT enrollmentid FROM enrollment WHERE cid = ?)";
        String deleteAbsencesQuery = "DELETE FROM absences WHERE enrid IN (SELECT enrollmentid FROM enrollment WHERE cid = ?)";
        String deleteCourseQuery = "DELETE FROM course WHERE courseid = ?";
        try (PreparedStatement deleteEnrollmentsStmt = connection.prepareStatement(deleteEnrollmentsQuery);
             PreparedStatement deleteGradesStmt = connection.prepareStatement(deleteGradesQuery);
             PreparedStatement deleteAbsencesStmt = connection.prepareStatement(deleteAbsencesQuery);
             PreparedStatement deleteCourseStmt = connection.prepareStatement(deleteCourseQuery)) {
            connection.setAutoCommit(false);
            deleteAbsencesStmt.setInt(1, courseId);
            deleteAbsencesStmt.executeUpdate();
            deleteGradesStmt.setInt(1, courseId);
            deleteGradesStmt.executeUpdate();
            deleteEnrollmentsStmt.setInt(1, courseId);
            deleteEnrollmentsStmt.executeUpdate();
            deleteCourseStmt.setInt(1, courseId);
            int affectedRows = deleteCourseStmt.executeUpdate();
            connection.commit();
            if (affectedRows > 0) {
                System.out.println("Course deleted successfully!");
                return true;
            } else {
                System.out.println("Course with ID " + courseId + " doesn't exist!");
                return false;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error deleting course: " + e.getMessage());
            return false;
        }
    }

    public List<String> getCoursesForInstructor() {
        List<String> courseDetails = new ArrayList<>();
        String query = "SELECT title, offerday, start_time FROM course WHERE insid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, connectedID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String offerday = resultSet.getString("offerday");
                String startTime = resultSet.getString("start_time");
                String courseDetail = title + "," + offerday + "," + startTime;
                courseDetails.add(courseDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseDetails;
    }

    public List<String> getCoursesForStudent() {
        List<String> courseDetails = new ArrayList<>();
        String query = "SELECT c.title, c.offerday, c.start_time from course c,enrollment e where c.courseid = e.cid and e.sid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, connectedID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String offerday = resultSet.getString("offerday");
                String startTime = resultSet.getString("start_time");
                String courseDetail = title + "," + offerday + "," + startTime;
                courseDetails.add(courseDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseDetails;
    }

    public List<String> getStudentsForCourse(int courseId) {
        List<String> studentNames = new ArrayList<>();
        String query = "SELECT s.firstname, s.lastname FROM student s,enrollment e where s.studentid = e.sid and e.cid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, courseId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String fullName = firstName + " " + lastName;
                studentNames.add(fullName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentNames;
    }

    public int getCourseId(String courseTitle) {
        int courseId = -1;
        String query = "SELECT courseid FROM course WHERE title = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, courseTitle);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                courseId = resultSet.getInt("courseid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseId;
    }

    public int getStudentIdByName(String fullName) {
        int studentId = -1;
        String[] names = fullName.split(" ");
        if (names.length >= 2) {
            String firstName = names[0];
            String lastName = names[1];
            String query = "SELECT studentid FROM student WHERE firstname = ? AND lastname = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    studentId = resultSet.getInt("studentid");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return studentId;
    }


    public int getEnrollmentId(int studentId, int courseId) {
        int enrollmentId = -1;
        String query = "SELECT enrollmentid FROM enrollment WHERE sid = ? AND cid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, courseId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                enrollmentId = resultSet.getInt("enrollmentid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollmentId;
    }

    public void markStudentAbsent(int studentId, int courseId) {
        String query = "INSERT INTO absences (enrid, absdate) VALUES (?, CURDATE())";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, getEnrollmentId(studentId, courseId));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void enterGrade(int studentId, int courseId, int grade) {
        String query = "INSERT INTO grades (eid, grade, status) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int enrollmentId = getEnrollmentId(studentId, courseId);
            preparedStatement.setInt(1, enrollmentId);
            preparedStatement.setInt(2, grade);
            String status = (grade > 60) ? "P" : "F";
            preparedStatement.setString(3, status);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getAbsencesForStudent() {
        List<String[]> absences = new ArrayList<>();
        String query = "SELECT c.title, a.absdate from course c,enrollment e, absences a where c.courseid = e.cid and e.enrollmentid = a.enrid and e.sid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, connectedID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String[] absence = new String[2];
                absence[0] = resultSet.getString("title");
                absence[1] = resultSet.getString("absdate");
                absences.add(absence);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return absences;
    }

    public List<String[]> getGradesForStudent() {
        List<String[]> studentGrades = new ArrayList<>();
        String query = "SELECT c.title, g.grade, g.status FROM student s, enrollment e, course c, grades g where s.studentid = e.sid and e.cid = c.courseid and e.enrollmentid = g.eid and s.studentid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, connectedID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String courseTitle = resultSet.getString("title");
                    int grade = resultSet.getInt("grade");
                    String status = resultSet.getString("status");
                    String[] gradeInfo = {courseTitle, String.valueOf(grade), status};
                    studentGrades.add(gradeInfo);
                }
            }
            catch (SQLException e) {
            e.printStackTrace();
        }
        return studentGrades;
    }

    private String getInstructorName(int instructorId) {
        String query = "SELECT firstname, lastname FROM instructor WHERE instructorid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, instructorId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                return firstName + " " + lastName;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getCoursesFromDatabase() {
        List<String> courses = new ArrayList<>();
        String query = "SELECT * FROM course";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int courseId = resultSet.getInt("courseid");
                int instructorId = resultSet.getInt("insid");
                String instructorName = getInstructorName(instructorId);
                String title = resultSet.getString("title");
                String offeringDay = resultSet.getString("offerday");
                Time startTime = resultSet.getTime("start_time");
                Time endTime = resultSet.getTime("end_time");
                int credits = resultSet.getInt("credits");
                String courseDescription = resultSet.getString("coursedescription");
                if (instructorName != null && title != null && offeringDay != null && startTime != null && endTime != null) {
                    String courseDetail = "Course ID: " + courseId +
                            ", Instructor: " + instructorName +
                            ", Title: " + title +
                            ", Offering Day: " + offeringDay +
                            ", Time: " + startTime + " - " + endTime +
                            ", Credits: " + credits +
                            ", Description: " + courseDescription;
                    courses.add(courseDetail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public boolean checkEnrollmentConflict(int courseId) {
        String enrollmentCheckQuery = "SELECT * FROM enrollment WHERE sid = ? AND cid = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(enrollmentCheckQuery)) {
            checkStatement.setInt(1, connectedID);
            checkStatement.setInt(2, courseId);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Already enrolled in this course!");
                return true;
            }
            String courseInfoQuery = "SELECT offerday, start_time, end_time FROM course WHERE courseid = ?";
            try (PreparedStatement courseStatement = connection.prepareStatement(courseInfoQuery)) {
                courseStatement.setInt(1, courseId);
                ResultSet courseResult = courseStatement.executeQuery();
                if (courseResult.next()) {
                    String offerDay = courseResult.getString("offerday");
                    Time startTime = courseResult.getTime("start_time");
                    Time endTime = courseResult.getTime("end_time");
                    String conflictCheckQuery = "SELECT * FROM enrollment e, course c WHERE e.sid = ? AND c.courseid = e.cid AND c.offerday = ? AND ((c.start_time BETWEEN ? AND ?) OR (c.end_time BETWEEN ? AND ?))";
                    try (PreparedStatement conflictStatement = connection.prepareStatement(conflictCheckQuery)) {
                        conflictStatement.setInt(1, connectedID);
                        conflictStatement.setString(2, offerDay);
                        conflictStatement.setTime(3, startTime);
                        conflictStatement.setTime(4, endTime);
                        conflictStatement.setTime(5, startTime);
                        conflictStatement.setTime(6, endTime);
                        ResultSet conflictResult = conflictStatement.executeQuery();
                        if (conflictResult.next()) {
                            System.out.println("Time conflict detected!");
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean enrollStudent(int courseId) {
        String enrollStudentQuery = "INSERT INTO enrollment (sid, cid) VALUES (?, ?)";
        try (PreparedStatement enrollStatement = connection.prepareStatement(enrollStudentQuery)) {
            enrollStatement.setInt(1, connectedID);
            enrollStatement.setInt(2, courseId);
            int rowsAffected = enrollStatement.executeUpdate();
            System.out.println("Enrolled successfully!");
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public int findEnrollmentId(int courseId) {
        int enrollmentId = -1;
        String findEnrollmentQuery = "SELECT enrollmentid FROM enrollment WHERE sid = ? AND cid = ?";
        try (PreparedStatement findEnrollmentStatement = connection.prepareStatement(findEnrollmentQuery)) {
            findEnrollmentStatement.setInt(1,  connectedID);
            findEnrollmentStatement.setInt(2, courseId);
            ResultSet resultSet = findEnrollmentStatement.executeQuery();
            if (resultSet.next()) {
                enrollmentId = resultSet.getInt("enrollmentid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollmentId;
    }

    public boolean dropCourseForStudent(int courseId) {
        try {
            connection.setAutoCommit(false);
            int enrollmentId = findEnrollmentId(courseId);
            if (enrollmentId != -1) {
                String deleteAbsencesQuery = "DELETE FROM absences WHERE enrid = ?";
                try (PreparedStatement deleteAbsencesStatement = connection.prepareStatement(deleteAbsencesQuery)) {
                    deleteAbsencesStatement.setInt(1, enrollmentId);
                    deleteAbsencesStatement.executeUpdate();
                }
                String deleteGradesQuery = "DELETE FROM grades WHERE eid = ?";
                try (PreparedStatement deleteGradesStatement = connection.prepareStatement(deleteGradesQuery)) {
                    deleteGradesStatement.setInt(1, enrollmentId);
                    deleteGradesStatement.executeUpdate();
                }
                String deleteEnrollmentQuery = "DELETE FROM enrollment WHERE enrollmentid = ?";
                try (PreparedStatement deleteEnrollmentStatement = connection.prepareStatement(deleteEnrollmentQuery)) {
                    deleteEnrollmentStatement.setInt(1, enrollmentId);
                    int enrollmentDeleted = deleteEnrollmentStatement.executeUpdate();
                    if (enrollmentDeleted > 0) {
                        connection.commit();
                        return true;
                    } else {
                        connection.rollback();
                        return false;
                    }
                }
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}