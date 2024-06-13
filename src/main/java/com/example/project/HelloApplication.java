package com.example.project;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {
    List<Stage> openedStages = new ArrayList<>();
    BddComm com = new BddComm();

    @Override
    public void start(Stage stage) {
        String url = "jdbc:mysql://localhost:3306/projectoop2";
        String user = "root";
        String pass = "1234";
        int db = com.connect(url, user, pass);
        if (db == 1) {
            VBox vb2 = createSignInForm();
            createAndShowStage("Antonine Univeristy", vb2, "null");
        }
    }

    private void createAndShowStage(String title, Node bodyContent, String pos) {
        Stage stage = new Stage();
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: rgb(210, 181, 245);");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 50px; -fx-font-weight: bold;");
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        root.setTop(titleLabel);
        Button back = new Button("Back");
        back.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        back.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(back, Pos.CENTER);
        root.setBottom(back);
        Button exit = new Button("Exit");
        exit.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        exit.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(exit, Pos.CENTER);
        exit.setVisible(false);
        exit.setOnAction(e -> {
            com.disconnect();
            closeAllStages();
        });
        BorderPane.setMargin(bodyContent, new Insets(20));
        root.setCenter(bodyContent);
        BorderPane.setAlignment(bodyContent, Pos.CENTER);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        back.setOnAction(e -> {
            closeAllStages();
            switch (pos) {
                case "Student" -> createHomePage("Student");
                case "Teacher" -> createHomePage("Teacher");
                case "Admin" -> createHomePage("Admin");
            }
        });
        if (pos.equals("null")) {
            back.setVisible(false);
            exit.setVisible(true);
            root.setBottom(exit);
        }
        stage.show();
        openedStages.add(stage);
    }

    private void closeAllStages() {
        for (Stage stage : openedStages) {
            stage.close();
        }
        openedStages.clear();
    }

    private void createHomePage(String userType) {
        Stage stage = new Stage();
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: rgb(210, 181, 245);");
        Label titleLabel = new Label("Homepage - " + userType);
        titleLabel.setStyle("-fx-font-size: 50px; -fx-font-weight: bold;");
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        root.setTop(titleLabel);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(30);
        grid.setVgap(30);
        Button signOutButton = new Button("Sign Out");
        signOutButton.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        signOutButton.setOnAction(e -> {
            closeAllStages();
            VBox vb2 = createSignInForm();
            createAndShowStage("Antonine University",vb2,"null");
        });
        signOutButton.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(signOutButton,Pos.CENTER);
        root.setBottom(signOutButton);
        switch (userType) {
            case "Student" -> {
                for (int row = 0; row < 2; row++) {
                    for (int col = 0; col < 3; col++) {
                        grid.add(createStudentButtonBox(row, col), col, row);
                    }
                }
            }
            case "Teacher" -> {
                HBox teacherFunctions = new HBox(30);
                VBox viewScheduleBox = createTeacherButtonBox("View Schedule", "C:\\Users\\Veronica\\Desktop\\v\\uni\\3rd year\\s5\\object oriented programming 2\\labs me\\project\\project\\src\\main\\resources\\00.png");
                VBox takeAttendanceBox = createTeacherButtonBox("Take Attendance", "C:\\Users\\Veronica\\Desktop\\v\\uni\\3rd year\\s5\\object oriented programming 2\\labs me\\project\\project\\src\\main\\resources\\21.png");
                VBox enterGradesBox = createTeacherButtonBox("Enter Grades", "C:\\Users\\Veronica\\Desktop\\v\\uni\\3rd year\\s5\\object oriented programming 2\\labs me\\project\\project\\src\\main\\resources\\11.png");
                teacherFunctions.setAlignment(Pos.CENTER);
                teacherFunctions.getChildren().addAll(viewScheduleBox, takeAttendanceBox, enterGradesBox);
                grid.getChildren().add(teacherFunctions);
            }
            case "Admin" -> {
                for (int row = 0; row < 2; row++) {
                    for (int col = 0; col < 3; col++) {
                        grid.add(createAdminButtonBox(row, col), col, row);
                    }
                }
            }
        }
        root.setCenter(grid);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
        openedStages.add(stage);
    }

    public VBox createSignInForm () {
        Label title = new Label("Please enter your credentials: ");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        Label errorLabel = new Label("Wrong email and/or password!");
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-font-weight: bold");
        errorLabel.setVisible(false);
        VBox vb2 = new VBox(50);
        vb2.setPadding(new Insets(20));
        vb2.setAlignment(Pos.CENTER);
        vb2.setMaxWidth(500);
        vb2.setMinWidth(200);
        vb2.setMaxHeight(400);
        vb2.setStyle("-fx-background-color: rgb(255, 255, 255, 0.7); -fx-background-radius: 20;");
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        TextField emailtf = new TextField();
        emailtf.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        PasswordField passwordtf = new PasswordField();
        passwordtf.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        Button signInButton = new Button("Sign In");
        signInButton.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        signInButton.setOnAction(e -> {
            String authenticationResult = com.authenticate(emailtf.getText(), passwordtf.getText());
            switch (authenticationResult) {
                case "Student" -> createHomePage("Student");
                case "Admin" -> createHomePage("Admin");
                case "Teacher" -> createHomePage("Teacher");
                default -> {
                    errorLabel.setVisible(true);
                    System.out.println("Error in authentication!");
                }
            }
        });
        GridPane loginGridPane = new GridPane();
        loginGridPane.setAlignment(Pos.CENTER);
        loginGridPane.setHgap(10);
        loginGridPane.setVgap(30);
        loginGridPane.setPadding(new Insets(10));
        loginGridPane.addRow(0, emailLabel, emailtf);
        loginGridPane.addRow(1, passwordLabel, passwordtf);
        vb2.getChildren().addAll(title, loginGridPane, signInButton, errorLabel);
        return vb2;
    }

    private VBox createStudentButtonBox(int row, int col) {
        ImageView iconImageView = new ImageView(new Image("C:\\Users\\Veronica\\Desktop\\v\\uni\\3rd year\\s5\\object oriented programming 2\\labs me\\project\\project\\src\\main\\resources\\"+col+row+".png"));
        iconImageView.setFitWidth(100);
        iconImageView.setFitHeight(100);
        String[] buttonTexts = {
                "View Schedule", "Enroll in Course", "Drop Course",
                "Swap Courses", "View Grades", "View Absences"
        };
        Button button = new Button(buttonTexts[row * 3 + col]);
        button.setStyle("-fx-font-size: 24px; -fx-focus-color: rgb(210, 181, 245);");
        button.setOnAction(e -> {
            switch (buttonTexts[row * 3 + col]) {
                case "View Schedule" -> showSchedule("Student");
                case "Enroll in Course" -> enrollCourse();
                case "Drop Course" -> dropCourse();
                case "Swap Courses" -> swapCourses();
                case "View Grades" -> viewGrades();
                case "View Absences" -> viewAbsences();
            }
        });
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(iconImageView, button);
        vbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        vbox.setPadding(new Insets(20));
        return vbox;
    }

    private VBox createAdminButtonBox(int row, int col) {
        ImageView iconImageView = new ImageView(new Image("C:\\Users\\Veronica\\Desktop\\v\\uni\\3rd year\\s5\\object oriented programming 2\\labs me\\project\\project\\src\\main\\resources\\admin"+col+row+".png"));
        iconImageView.setFitWidth(100);
        iconImageView.setFitHeight(100);
        String[] buttonTexts = {
                "Add Course", "Add Student", "Add Teacher",
                "Delete Course", "Delete Student", "Delete Teacher"
        };
        Button button = new Button(buttonTexts[row * 3 + col]);
        button.setStyle("-fx-font-size: 24px; -fx-focus-color: rgb(210, 181, 245);");
        button.setOnAction(e -> {
            switch (buttonTexts[row * 3 + col]) {
                case "Add Student" -> createAddOrDeleteForm("Student", "Add");
                case "Add Teacher" -> createAddOrDeleteForm("Teacher", "Add");
                case "Add Course" -> createAddOrDeleteForm("Course", "Add");
                case "Delete Student" -> createAddOrDeleteForm("Student", "Delete");
                case "Delete Teacher" -> createAddOrDeleteForm("Teacher", "Delete");
                case "Delete Course" -> createAddOrDeleteForm("Course", "Delete");
            }
        });
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(iconImageView, button);
        vbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        vbox.setPadding(new Insets(20));
        return vbox;
    }

    private VBox createTeacherButtonBox(String buttonText, String imagePath) {
        ImageView iconImageView = new ImageView(new Image(imagePath));
        iconImageView.setFitWidth(100);
        iconImageView.setFitHeight(100);
        Button button = new Button(buttonText);
        button.setStyle("-fx-font-size: 24px; -fx-focus-color: rgb(210, 181, 245);");
        button.setOnAction(e -> {
            switch (buttonText) {
                case "View Schedule" -> showSchedule("Teacher");
                case "Take Attendance" -> takeAttendance();
                case "Enter Grades" -> enterGrades();
                default -> {
                }
            }
        });
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(iconImageView, button);
        vbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        vbox.setPadding(new Insets(20));
        vbox.setMinWidth(400);
        vbox.setMaxWidth(400);
        vbox.setMinHeight(300);
        vbox.setMaxHeight(300);
        return vbox;
    }

    private GridPane createAddStudentForm() {
        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();
        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        firstNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        lastNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        emailLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        passwordLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        firstNameField.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        lastNameField.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        emailField.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        passwordField.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        GridPane formLayout = new GridPane();
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setHgap(10);
        formLayout.setVgap(15);
        formLayout.setPadding(new Insets(20));
        formLayout.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        formLayout.addRow(0, firstNameLabel, firstNameField);
        formLayout.addRow(1, lastNameLabel, lastNameField);
        formLayout.addRow(2, emailLabel, emailField);
        formLayout.addRow(3, passwordLabel, passwordField);
        return formLayout;
    }

    private GridPane createAddCourseForm() {
        Label titleLabel = new Label("Title:");
        TextField titleField = new TextField();
        Label offerDayLabel = new Label("Offer Day:");
        ComboBox<String> offerDayComboBox = new ComboBox<>();
        offerDayComboBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        Label startTimeLabel = new Label("Start Time:");
        ComboBox<String> startTimeComboBox = new ComboBox<>();
        startTimeComboBox.getItems().addAll("08:30", "11:30", "15:30", "18:30");
        Label endTimeLabel = new Label("End Time:");
        Label endTimeValueLabel = new Label();
        Label creditsLabel = new Label("Credits:");
        ComboBox<String> creditsComboBox = new ComboBox<>();
        creditsComboBox.getItems().addAll("1", "2", "3");
        Label courseDescriptionLabel = new Label("Course Description:");
        TextField courseDescriptionField = new TextField();
        Label instructorLabel = new Label("Select Instructor:");
        ComboBox<String> instructorComboBox = new ComboBox<>();
        List<String> instructors = com.getInstructors();
        instructorComboBox.getItems().addAll(instructors);
        startTimeComboBox.setOnAction(e -> {
            String selectedStartTime = startTimeComboBox.getValue();
            if (selectedStartTime != null) {
                endTimeValueLabel.setText(getComputedEndTime(selectedStartTime));
            }
        });
        GridPane formLayout = new GridPane();
        formLayout.setMinWidth(500);
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setHgap(10);
        formLayout.setVgap(15);
        formLayout.setPadding(new Insets(20));
        formLayout.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        formLayout.addRow(0, titleLabel, titleField);
        formLayout.addRow(1, offerDayLabel, offerDayComboBox);
        formLayout.addRow(2, startTimeLabel, startTimeComboBox);
        formLayout.addRow(3, endTimeLabel, endTimeValueLabel);
        formLayout.addRow(4, creditsLabel, creditsComboBox);
        formLayout.addRow(5, courseDescriptionLabel, courseDescriptionField);
        formLayout.addRow(6, instructorLabel, instructorComboBox);
        return formLayout;
    }

    private GridPane createDeleteForm(String entityType) {
        Label idLabel = new Label(entityType + " ID:");
        TextField idField = new TextField();
        idLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        idField.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        GridPane formLayout = new GridPane();
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setHgap(10);
        formLayout.setVgap(15);
        formLayout.setPadding(new Insets(20));
        formLayout.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        formLayout.addRow(0, idLabel, idField);
        return formLayout;
    }

    private void createAddOrDeleteForm(String entityType, String actionType) {
        Label formLabel = new Label(actionType + " " + entityType);
        Button actionButton = new Button(actionType);
        actionButton.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        GridPane form;
        Label resultText = new Label(entityType + " added successfully!");
        resultText.setVisible(false);
        if (actionType.equals("Add")) {
            switch (entityType) {
                case "Course" -> {
                    form = createAddCourseForm();
                    actionButton.setOnAction(e -> {
                        TextField titleField = (TextField) form.getChildren().get(1);
                        ComboBox<String> offerDayComboBox = (ComboBox<String>) form.getChildren().get(3);
                        ComboBox<String> startTimeComboBox = (ComboBox<String>) form.getChildren().get(5);
                        ComboBox<String> creditsComboBox = (ComboBox<String>) form.getChildren().get(9);
                        TextField courseDescriptionField = (TextField) form.getChildren().get(11);
                        ComboBox<String> instructorComboBox = (ComboBox<String>) form.getChildren().get(13);
                        boolean notOK = false;
                        if (titleField.getText().isEmpty() || offerDayComboBox == null || startTimeComboBox == null || creditsComboBox == null || courseDescriptionField.getText().isEmpty() || instructorComboBox == null) {
                            resultText.setText("Please fill all fields!");
                            notOK = true;
                        }
                        if (!notOK && !com.isInstructorAvailable(instructorComboBox.getValue(), offerDayComboBox.getValue(), startTimeComboBox.getValue())) {
                            resultText.setText("Chosen instructor isn't available at this time!");
                            notOK = true;
                        }
                        if (!notOK) {
                            String title = titleField.getText();
                            String offerday = offerDayComboBox.getValue();
                            String startTime = startTimeComboBox.getValue();
                            int credits = Integer.parseInt(creditsComboBox.getValue());
                            String courseDescription = courseDescriptionField.getText();
                            String selectedInstructor = instructorComboBox.getValue();
                            int insId = com.getInstructorId(selectedInstructor);
                            String endTime = getComputedEndTime(startTime);
                            com.addCourse(insId, title, offerday, startTime, endTime, credits, courseDescription);
                            resultText.setText(entityType + " added successfully!");
                        }
                        resultText.setVisible(true);
                    });
                }
                case "Student" -> {
                    form = createAddStudentForm();
                    actionButton.setOnAction(e -> {
                        TextField firstNameField = (TextField) form.getChildren().get(1);
                        TextField lastNameField = (TextField) form.getChildren().get(3);
                        TextField emailField = (TextField) form.getChildren().get(5);
                        PasswordField passwordField = (PasswordField) form.getChildren().get(7);
                        boolean notOK = false;
                        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                            resultText.setText("Please fill all fields!");
                            notOK = true;
                        }
                        if (isValidEmail(emailField.getText())) {
                            resultText.setText("Please enter a valid email!");
                            notOK = true;
                        }

                        if (isValidPassword(passwordField.getText())) {
                            resultText.setText("Password should have at least 8 characters!");
                            notOK = true;
                        }
                        if (!notOK) {
                            String firstName = firstNameField.getText();
                            String lastName = lastNameField.getText();
                            String email = emailField.getText();
                            String password = passwordField.getText();
                            com.addStudent(firstName, lastName, email, password);
                            resultText.setText(entityType + " added successfully!");
                        }
                        resultText.setVisible(true);
                    });
                }
                case "Teacher" -> {
                    form = createAddStudentForm();
                    actionButton.setOnAction(e -> {
                        TextField firstNameField = (TextField) form.getChildren().get(1);
                        TextField lastNameField = (TextField) form.getChildren().get(3);
                        TextField emailField = (TextField) form.getChildren().get(5);
                        PasswordField passwordField = (PasswordField) form.getChildren().get(7);
                        boolean notOK = false;
                        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                            resultText.setText("Please fill all fields!");
                            notOK = true;
                        }
                        if (isValidEmail(emailField.getText())) {
                            resultText.setText("Please enter a valid email!");
                            notOK = true;
                        }
                        if (isValidPassword(passwordField.getText())) {
                            resultText.setText("Password should have at least 8 characters!");
                            notOK = true;
                        }
                        if (!notOK) {
                            String firstName = firstNameField.getText();
                            String lastName = lastNameField.getText();
                            String email = emailField.getText();
                            String password = passwordField.getText();
                            com.addTeacher(firstName, lastName, email, password);
                            resultText.setText(entityType + " added successfully!");
                        }
                        resultText.setVisible(true);
                    });
                }
                default -> form = createDeleteForm(entityType);
            }
            VBox vBox = new VBox(30);
            vBox.setPadding(new Insets(20));
            vBox.setAlignment(Pos.CENTER);
            vBox.setMaxWidth(500);
            vBox.setMinWidth(200);
            vBox.setMinHeight(400);
            vBox.getChildren().addAll(form, actionButton, resultText);
            createAndShowStage(formLabel.getText(), vBox, "Admin");
        } else {
            form = createDeleteForm(entityType);
            actionButton.setOnAction(e -> {
                TextField idField = (TextField) form.getChildren().get(1);
                boolean notOK = false;
                if (idField.getText().isEmpty()) {
                    resultText.setText("Please fill the ID field!");
                    notOK = true;
                }
                if (!notOK) {
                    int id = Integer.parseInt(idField.getText());
                    boolean deleted = false;
                    switch (entityType) {
                        case "Course" -> {
                            deleted = com.deleteCourse(id);
                            resultText.setText(deleted ? "Course deleted successfully!" : "This ID doesn't exist!");
                        }
                        case "Student" -> {
                            deleted = com.deleteStudent(id);
                            resultText.setText(deleted ? "Student deleted successfully!" : "This ID doesn't exist!");
                        }
                        case "Teacher" -> {
                            deleted = com.deleteTeacher(id);
                            resultText.setText(deleted ? "Teacher deleted successfully!" : "This ID doesn't exist!");
                        }
                        default -> resultText.setText("Invalid entity type!");
                    }
                    if (!deleted) {
                        resultText.setText("This ID doesn't exist!");
                    }
                }
                resultText.setVisible(true);
            });
        }
        VBox vBox = new VBox(30);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.CENTER);
        vBox.setMaxWidth(500);
        vBox.setMinWidth(200);
        vBox.setMinHeight(400);
        vBox.getChildren().addAll(form, actionButton, resultText);
        createAndShowStage(formLabel.getText(), vBox, "Admin");
    }
    private void showSchedule(String pos) {
        GridPane scheduleGrid = new GridPane();
        scheduleGrid.setAlignment(Pos.CENTER);
        scheduleGrid.setPadding(new Insets(20));
        scheduleGrid.setStyle("-fx-background-color: rgb(210, 181, 245);");
        scheduleGrid.setMinSize(800, 600);
        String[] daysOfWeek = {"", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            dayLabel.setAlignment(Pos.CENTER);
            scheduleGrid.add(dayLabel, i, 1);
        }
        String[] timeSlots = {"08:30 - 11:15", "11:30 - 14:15", "15:30 - 18:15", "18:30 - 21:15"};
        for (int i = 0; i < timeSlots.length; i++) {
            Label timeLabel = new Label(timeSlots[i]);
            timeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            timeLabel.setAlignment(Pos.CENTER);
            timeLabel.setPadding(new Insets(5));
            scheduleGrid.add(timeLabel, 0, i + 2);
        }
        for (int row = 2; row <= 5; row++) {
            for (int col = 1; col <= 5; col++) {
                Pane emptyPane = new Pane();
                emptyPane.setMinSize(150, 100);
                emptyPane.setStyle("-fx-background-color: white; -fx-border-color: black");
                scheduleGrid.add(emptyPane, col, row);
            }
        }
        List<String> courses = null;
        if (pos.equals("Teacher"))
            courses = com.getCoursesForInstructor();
        else if (pos.equals("Student"))
            courses = com.getCoursesForStudent();
        if (courses != null) {
            for (String courseDetails : courses) {
                String[] details = courseDetails.split(",");
                String title = details[0];
                String dayOfWeek = details[1];
                String startTime = details[2];
                int rowCell = getTimeRow(startTime);
                int colCell = getDayColumn(dayOfWeek);
                if (rowCell != -1 && colCell != -1) {
                    Node existingNode = getNodeByRowColumnIndex(rowCell, colCell, scheduleGrid);
                    Label courseLabel = new Label(title);
                    courseLabel.setMaxWidth(140);
                    courseLabel.setWrapText(true);
                    courseLabel.setAlignment(Pos.CENTER);
                    courseLabel.setPadding(new Insets(8));
                    if (existingNode instanceof Pane existingPane) {
                        existingPane.getChildren().add(courseLabel);
                    }
                }
            }
        }
        createAndShowStage("Your Schedule", scheduleGrid, pos);
    }

    private Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> children = gridPane.getChildren();
        for (Node node : children) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }
        return result;
    }

    private void takeAttendance() {
        ComboBox<String> coursesComboBox = new ComboBox<>();
        List<String> teacherCourses = com.getCoursesForInstructor();
        List<String> courseTitles = new ArrayList<>();
        for (String courseDetail : teacherCourses) {
            String[] details = courseDetail.split(",");
            if (details.length > 0) {
                courseTitles.add(details[0]);
            }
        }
        coursesComboBox.getItems().addAll(courseTitles);
        Label chooseCourseLabel = new Label("Choose Course:");
        chooseCourseLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        VBox courseSelectionBox = new VBox(10);
        courseSelectionBox.setAlignment(Pos.CENTER);
        courseSelectionBox.getChildren().addAll(chooseCourseLabel, coursesComboBox);
        VBox studentAttendanceList = new VBox(10);
        studentAttendanceList.setVisible(false);
        Label res = new Label("Attendance marked successfully!");
        res.setVisible(false);
        Button markAttendanceButton = new Button("Mark Attendance");
        markAttendanceButton.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        markAttendanceButton.setOnAction(e -> res.setVisible(true));
        coursesComboBox.setOnAction(event -> {
            res.setVisible(false);
            String selectedCourse = coursesComboBox.getSelectionModel().getSelectedItem();
            studentAttendanceList.getChildren().clear();

            if (selectedCourse != null) {
                int courseId = com.getCourseId(selectedCourse);

                List<String> studentsForCourse = com.getStudentsForCourse(courseId);

                for (String studentName : studentsForCourse) {
                    CheckBox checkBox = new CheckBox(studentName);
                    checkBox.setStyle("-fx-font-size: 16px;");
                    studentAttendanceList.getChildren().add(checkBox);
                    boolean present = checkBox.isSelected();
                    int studentId = com.getStudentIdByName(studentName);
                    if (!present) {
                        com.markStudentAbsent(studentId, courseId);
                    }
                    studentAttendanceList.setVisible(true);
                }
            }
        });

        VBox attendanceBox = new VBox(20);
        attendanceBox.setAlignment(Pos.CENTER);
        attendanceBox.setPadding(new Insets(20));
        attendanceBox.setMaxHeight(500);
        attendanceBox.setMaxWidth(500);
        attendanceBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        attendanceBox.getChildren().addAll(courseSelectionBox, studentAttendanceList, markAttendanceButton, res);
        createAndShowStage("Take Attendance", attendanceBox,"Teacher");
    }

    private void enterGrades() {
        ComboBox<String> coursesComboBox = new ComboBox<>();
        List<String> teacherCourses = com.getCoursesForInstructor();
        List<String> courseTitles = new ArrayList<>();
        for (String courseDetail : teacherCourses) {
            String[] details = courseDetail.split(",");
            if (details.length > 0) {
                courseTitles.add(details[0]);
            }
        }
        coursesComboBox.getItems().addAll(courseTitles);
        Label chooseCourseLabel = new Label("Choose Course:");
        chooseCourseLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        VBox courseSelectionBox = new VBox(10);
        courseSelectionBox.setAlignment(Pos.CENTER);
        courseSelectionBox.getChildren().addAll(chooseCourseLabel, coursesComboBox);
        GridPane studentGradesGrid = new GridPane();
        studentGradesGrid.setVgap(10);
        studentGradesGrid.setHgap(10);
        studentGradesGrid.setAlignment(Pos.CENTER);
        studentGradesGrid.setVisible(false);
        Label res = new Label("Grades entered successfully!");
        res.setVisible(false);
        Button enterGradesButton = new Button("Enter Grades");
        enterGradesButton.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        coursesComboBox.setOnAction(event -> {
            res.setVisible(false);
            String selectedCourse = coursesComboBox.getSelectionModel().getSelectedItem();
            studentGradesGrid.getChildren().clear();
            studentGradesGrid.getColumnConstraints().clear();

            if (selectedCourse != null) {
                int courseId = com.getCourseId(selectedCourse);
                List<String> studentsForCourse = com.getStudentsForCourse(courseId);

                for (int i = 0; i < studentsForCourse.size(); i++) {
                    String studentName = studentsForCourse.get(i);
                    Label nameLabel = new Label(studentName);
                    nameLabel.setStyle("-fx-font-size: 16px;");
                    TextField gradeField = new TextField();
                    gradeField.setPromptText("Enter grade");
                    gradeField.setStyle("-fx-font-size: 16px;");
                    studentGradesGrid.addRow(i, nameLabel, gradeField);
                }
                studentGradesGrid.setVisible(true);
            }
        });
        enterGradesButton.setOnAction(e -> {
            String selectedCourse = coursesComboBox.getSelectionModel().getSelectedItem();
            int courseId = com.getCourseId(selectedCourse);
            addGrades(courseId, studentGradesGrid);
            res.setVisible(true);
        });
        VBox gradesBox = new VBox(20);
        gradesBox.setAlignment(Pos.CENTER);
        gradesBox.setPadding(new Insets(20));
        gradesBox.setMaxHeight(500);
        gradesBox.setMaxWidth(500);
        gradesBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        gradesBox.getChildren().addAll(courseSelectionBox, studentGradesGrid, enterGradesButton, res);
        createAndShowStage("Enter Grades", gradesBox, "Teacher");
    }

    private void addGrades(int courseId, GridPane studentGradesGrid) {
        ObservableList<Node> children = studentGradesGrid.getChildren();
        for (Node node : children) {
            if (node instanceof TextField) {
                int rowIndex = GridPane.getRowIndex(node);
                String studentName = ((Label) children.get(rowIndex * 2)).getText();
                int studentId = com.getStudentIdByName(studentName);

                String gradeText = ((TextField) node).getText();
                if (!gradeText.isEmpty()) {
                    int grade = Integer.parseInt(gradeText);
                    com.enterGrade(studentId, courseId, grade);
                }
            }
        }
    }

    public void viewAbsences() {
        GridPane absencesGrid = new GridPane();
        absencesGrid.setAlignment(Pos.CENTER);
        absencesGrid.setPadding(new Insets(20));
        absencesGrid.setHgap(30);
        absencesGrid.setVgap(20);
        absencesGrid.setMaxHeight(450);
        absencesGrid.setMaxWidth(400);
        absencesGrid.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        Label courseHeader = new Label("Course");
        courseHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        Label dateHeader = new Label("Date");
        dateHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        absencesGrid.add(courseHeader, 0, 0);
        absencesGrid.add(dateHeader, 1, 0);
        List<String> courseNames = new ArrayList<>();
        List<String> absenceDates = new ArrayList<>();
        List<String[]> absences = com.getAbsencesForStudent();
        for (String[] absence : absences) {
            courseNames.add(absence[0]);
            absenceDates.add(absence[1]);
        }
        for (int i = 0; i < courseNames.size(); i++) {
            Label courseLabel = new Label(courseNames.get(i));
            Label dateLabel = new Label(absenceDates.get(i));
            courseLabel.setStyle("-fx-font-size: 18px;");
            dateLabel.setStyle("-fx-font-size: 18px;");
            absencesGrid.add(courseLabel, 0, i + 1);
            absencesGrid.add(dateLabel, 1, i + 1);
        }
        createAndShowStage("Your Absences", absencesGrid, "Student");
    }

    public void viewGrades() {
        GridPane gradesGrid = new GridPane();
        gradesGrid.setAlignment(Pos.CENTER);
        gradesGrid.setPadding(new Insets(20));
        gradesGrid.setHgap(30);
        gradesGrid.setVgap(20);
        gradesGrid.setMaxHeight(450);
        gradesGrid.setMaxWidth(400);
        gradesGrid.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        Label courseHeader = new Label("Course");
        courseHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        Label gradeHeader = new Label("Grade");
        gradeHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        Label statusHeader = new Label("Status");
        statusHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        gradesGrid.add(courseHeader, 0, 0);
        gradesGrid.add(gradeHeader, 1, 0);
        gradesGrid.add(statusHeader, 2, 0);
        List<String> courseNames = new ArrayList<>();
        List<Integer> grades = new ArrayList<>();
        List<Character> status = new ArrayList<>();
        List<String[]> studentGrades = com.getGradesForStudent();
        for (String[] studentGrade : studentGrades) {
            courseNames.add(studentGrade[0]);
            grades.add(Integer.parseInt(studentGrade[1]));
            status.add(studentGrade[2].charAt(0));
        }
        for (int i = 0; i < courseNames.size(); i++) {
            Label courseLabel = new Label(courseNames.get(i));
            Label gradeLabel = new Label(String.valueOf(grades.get(i)));
            Label statusLabel = new Label(String.valueOf(status.get(i)));
            courseLabel.setStyle("-fx-font-size: 18px;");
            gradeLabel.setStyle("-fx-font-size: 18px;");
            statusLabel.setStyle("-fx-font-size: 18px;");
            gradesGrid.add(courseLabel, 0, i + 1);
            gradesGrid.add(gradeLabel, 1, i + 1);
            gradesGrid.add(statusLabel, 2, i + 1);
        }
        createAndShowStage("Your Grades", gradesGrid, "Student");
    }

    private void enrollCourse() {
        List<String> courses = com.getCoursesFromDatabase();
        ListView<String> courseListView = new ListView<>();
        courseListView.setMinHeight(600);
        VBox all = new VBox(5);
        Label res = new Label();
        courseListView.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        courseListView.setPadding(new Insets(20));
        courseListView.getItems().addAll(courses);
        courseListView.setStyle("-fx-border-color: #eedcfa; -fx-selection-bar: #eedcfa; -fx-selection-bar-non-focused: #eedcfa; -fx-selection-bar-non-focused-border: #eedcfa;");
        courseListView.setCellFactory(param -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        String[] courseDetails = item.split(", ");
                        VBox detailsVBox = new VBox();
                        for (String detail : courseDetails) {
                            Label label = new Label(detail);
                            detailsVBox.getChildren().add(label);
                        }
                        detailsVBox.setStyle("-fx-font-size: 14px;");
                        Button enrollButton = new Button("Enroll");
                        enrollButton.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
                        enrollButton.setOnAction(e -> {
                            String[] courseInfo = item.split(", ");
                            int courseId = Integer.parseInt(courseInfo[0].split(": ")[1]);
                            boolean hasConflict = com.checkEnrollmentConflict(courseId);
                            if (!hasConflict) {
                                com.enrollStudent(courseId);
                                res.setText("Enrolled successfully!");
                                res.setTextFill(Color.GREEN);
                            } else {
                                res.setText("Already enrolled or time conflict!");
                                res.setTextFill(Color.RED);
                            }
                        });
                        setPadding(new Insets(10));
                        setStyle("-fx-margin-bottom: 5px;");
                        BorderPane bp = new BorderPane();
                        bp.setLeft(detailsVBox);
                        bp.setRight(enrollButton);
                        BorderPane.setAlignment(enrollButton, Pos.CENTER);
                        setGraphic(bp);
                    }
                }
            };
            cell.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
            return cell;
        });
        all.getChildren().addAll(courseListView,res);
        createAndShowStage("Enroll in a course", all, "Student");
    }

    private void dropCourse() {
        List<String> courses = com.getCoursesForStudent();
        ListView<String> courseListView = new ListView<>();
        courseListView.setMinHeight(600);
        VBox all = new VBox(5);
        Label res = new Label();
        courseListView.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        courseListView.setPadding(new Insets(20));
        courseListView.getItems().addAll(courses);
        courseListView.setStyle("-fx-border-color: #eedcfa; -fx-selection-bar: #eedcfa; -fx-selection-bar-non-focused: #eedcfa; -fx-selection-bar-non-focused-border: #eedcfa;");
        courseListView.setCellFactory(param -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        String[] courseDetails = item.split(",");
                        VBox detailsVBox = new VBox();
                        for (String detail : courseDetails) {
                            Label label = new Label(detail);
                            detailsVBox.getChildren().add(label);
                        }
                        detailsVBox.setStyle("-fx-font-size: 14px;");
                        Button dropButton = new Button("Drop");
                        dropButton.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
                        dropButton.setOnAction(e -> {
                            String[] courseInfo = item.split(",");
                            int courseId = com.getCourseId(courseInfo[0]);
                            boolean dropped = com.dropCourseForStudent(courseId);
                            if (dropped) {
                                courseListView.getItems().remove(getItem());
                                res.setText("Course dropped successfully!");
                                res.setTextFill(Color.GREEN);
                            } else {
                                res.setText("Drop unsuccessful!");
                                res.setTextFill(Color.RED);
                            }
                        });
                        setPadding(new Insets(10));
                        setStyle("-fx-margin-bottom: 5px;");
                        BorderPane bp = new BorderPane();
                        bp.setLeft(detailsVBox);
                        bp.setRight(dropButton);
                        BorderPane.setAlignment(dropButton, Pos.CENTER);
                        setGraphic(bp);
                    }
                }
            };
            cell.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
            return cell;
        });
        all.getChildren().addAll(courseListView,res);
        createAndShowStage("Drop a course", all, "Student");
    }

    private void swapCourses() {
        Stage swapStage = new Stage();
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: rgb(210, 181, 245);");
        Label res = new Label();
        Label titleLabel = new Label("Swap Courses");
        titleLabel.setStyle("-fx-font-size: 50px; -fx-font-weight: bold;");
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        root.setTop(titleLabel);
        Label enrolledLabel = new Label("Enrolled Courses");
        enrolledLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        Label availableLabel = new Label("Available Courses");
        availableLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        List<String> enrolledCourses = com.getCoursesForStudent();
        List<String> availableCourses = com.getCoursesFromDatabase();
        ListView<String> enrolledListView = new ListView<>();
        enrolledListView.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        enrolledListView.setPadding(new Insets(20));
        enrolledListView.getItems().addAll(enrolledCourses);
        enrolledListView.setPrefWidth(500);
        enrolledListView.setStyle("-fx-border-color: #eedcfa; -fx-selection-bar: #eedcfa; -fx-selection-bar-non-focused: #eedcfa; -fx-selection-bar-non-focused-border: #eedcfa;");
        enrolledListView.setCellFactory(param -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        String[] courseDetails = item.split(",");
                        VBox detailsVBox = new VBox();
                        for (String detail : courseDetails) {
                            Label label = new Label(detail);
                            detailsVBox.getChildren().add(label);
                        }
                        detailsVBox.setStyle("-fx-font-size: 14px;");
                        setPadding(new Insets(10));
                        setStyle("-fx-margin-bottom: 5px;");
                        BorderPane bp = new BorderPane();
                        bp.setLeft(detailsVBox);
                        setGraphic(bp);
                    }
                }
            };
            cell.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
            return cell;
        });
        ListView<String> availableListView = new ListView<>();
        availableListView.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
        availableListView.setPadding(new Insets(20));
        availableListView.getItems().addAll(availableCourses);
        availableListView.setPrefWidth(500);
        availableListView.setStyle("-fx-border-color: #eedcfa; -fx-selection-bar: #eedcfa; -fx-selection-bar-non-focused: #eedcfa; -fx-selection-bar-non-focused-border: #eedcfa;");
        availableListView.setCellFactory(param -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        String[] courseDetails = item.split(", ");
                        VBox detailsVBox = new VBox();
                        for (String detail : courseDetails) {
                            Label label = new Label(detail);
                            detailsVBox.getChildren().add(label);
                        }
                        detailsVBox.setStyle("-fx-font-size: 14px;");
                        setPadding(new Insets(10));
                        setStyle("-fx-margin-bottom: 5px;");
                        BorderPane bp = new BorderPane();
                        bp.setLeft(detailsVBox);
                        setGraphic(bp);
                    }
                }
            };
            cell.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");
            return cell;
        });
        VBox enrolledVBox = new VBox(10, enrolledLabel, enrolledListView);
        VBox availableVBox = new VBox(10, availableLabel, availableListView);
        enrolledVBox.minWidth(300);
        enrolledVBox.setAlignment(Pos.CENTER);
        availableVBox.setAlignment(Pos.CENTER);
        Button swapButton = new Button("Swap");
        swapButton.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        swapButton.setOnAction(e -> {
            String enrolledCourse = enrolledListView.getSelectionModel().getSelectedItem();
            String availableCourse = availableListView.getSelectionModel().getSelectedItem();
            if (enrolledCourse != null && availableCourse != null) {
                String[] enrolledCourseParts = enrolledCourse.split(",");
                String courseTitle = enrolledCourseParts[0];
                int enrolledId = com.getCourseId(courseTitle);
                String[] availableCourseParts = availableCourse.split(", ");
                String[] courseIdDetails = availableCourseParts[0].split(": ");
                int availableId = Integer.parseInt(courseIdDetails[1]);
                boolean hasConflict = com.checkEnrollmentConflict(availableId);
                boolean enrollAvailable = false;
                boolean dropAvailable = false;
                if (!hasConflict) {
                    enrollAvailable = com.enrollStudent(availableId);
                    dropAvailable = com.dropCourseForStudent(enrolledId);
                }
                if (dropAvailable && enrollAvailable) {
                    swapCourses();
                    res.setText("Courses swapped successfully!");
                    res.setTextFill(Color.GREEN);
                } else {
                    res.setText("Swap failed! You have a time conflict or you're already enrolled in this course!");
                    res.setTextFill(Color.RED);
                }
            }
        });
        HBox buttonBox = new HBox(swapButton);
        buttonBox.setAlignment(Pos.CENTER);
        root.setLeft(enrolledVBox);
        root.setRight(availableVBox);
        root.setCenter(buttonBox);
        Button back = new Button("Back");
        back.setStyle("-fx-font-size: 20px; -fx-focus-color: rgb(210, 181, 245);");
        back.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(back, Pos.CENTER);
        VBox bottomContent = new VBox(10);
        bottomContent.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(bottomContent, Pos.CENTER);
        bottomContent.getChildren().addAll(res, back);
        root.setBottom(bottomContent);
        back.setOnAction(e -> {
            closeAllStages();
            createHomePage("Student");
        });
        Scene scene = new Scene(root);
        swapStage.setScene(scene);
        swapStage.setFullScreen(true);
        swapStage.setFullScreenExitHint("");
        swapStage.show();
        openedStages.add(swapStage);
    }

    private String getComputedEndTime(String startTime) {
        return switch (startTime) {
            case "08:30" -> "11:15";
            case "11:30" -> "14:15";
            case "15:30" -> "18:15";
            case "18:30" -> "21:15";
            default -> "";
        };
    }

    private int getDayColumn(String dayOfWeek) {
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            if (daysOfWeek[i].equalsIgnoreCase(dayOfWeek)) {
                return i + 1;
            }
        }
        return -1;
    }

    private int getTimeRow(String startTime) {
        String[] timeSlots = {"08:30:00", "11:30:00", "15:30:00", "18:30:00"};
        for (int i = 0; i < timeSlots.length; i++) {
            if (timeSlots[i].startsWith(startTime)) {
                return i + 2;
            }
        }
        return -1;
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return true;
        }
        boolean hasNumber = false;
        for (char ch : password.toCharArray()) {
            if (Character.isDigit(ch)) {
                hasNumber = true;
                break;
            }
        }
        return !hasNumber;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return !email.matches(emailRegex);
    }

    public static void main(String[] args) { launch(args); }
}
