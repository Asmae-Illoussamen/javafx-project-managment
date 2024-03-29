package App.presentation.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import App.dao.*;
import App.IntroPageAdmin;
import App.IntroPageEmployee;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProjectSummaryController implements Initializable {
    Stage stage;

    @FXML
    private JFXButton btnProjectDetail;
    @FXML
    private Accordion accordion;
    @FXML
    private JFXButton homeBackBtn;
    @FXML
    private JFXButton allproject;

    private String userRole;
    private int employeeId;
    private int adminId;

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    @FXML
    private void allproject(ActionEvent event) {
        if(event.getSource() == allproject) {
            FXMLLoader Loader = new FXMLLoader();

            Loader.setLocation(getClass().getResource("../views/projectsummary.fxml"));

            try{
                Loader.load();
            } catch (Exception e){
                e.printStackTrace();
            }

            ProjectSummaryController projectSummaryController = Loader.getController();
            projectSummaryController.setUserRole(getUserRole());
            if(getUserRole().matches("ADMIN_AUTH")) {
                projectSummaryController.setAdminId(getAdminId());
            } else {
                projectSummaryController.setEmployeeId(getEmployeeId());
            }
            projectSummaryController.initializeProjects(getUserRole());

            Parent p = Loader.getRoot();
            stage = (Stage) allproject.getScene().getWindow();
            Scene scene = new Scene(p);
            stage.setScene(scene);
            stage.show();
        }
    }
    // When "All project" button is clicked
    @FXML
    private void ProjectDetail(ActionEvent event) throws IOException {
            if(event.getSource() == btnProjectDetail) {

                FXMLLoader Loader = new FXMLLoader();

                Loader.setLocation(getClass().getResource("../views/projectdetail.fxml"));

                try {
                    Loader.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ProjectDetailController projectDetailController = Loader.getController();
                projectDetailController.setUserRole(getUserRole());
                projectDetailController.getClientList();

                if (getUserRole().matches("ADMIN_AUTH")) {
                    projectDetailController.setAdminId(getAdminId());
                } else {
                    projectDetailController.setEmployeeId(getEmployeeId());
                }

                Parent p = Loader.getRoot();
                stage = (Stage) allproject.getScene().getWindow();
                Scene scene = new Scene(p);
                stage.setScene(scene);
                stage.show();
            }
        }


    public void initializeProjects(String userRole){
        FXMLLoader Loader = new FXMLLoader();
        String sql;

        if(userRole.matches("ADMIN_AUTH")) {
            sql  = "SELECT * FROM PROJECT_INFO,CLIENT WHERE CLIENT.id=client_id";
        }
        else {
            sql = "SELECT distinct PROJECT_INFO.id, project_name, start_date, end_date, estimated_time,client_id,CLIENT.name FROM PROJECT_INFO, PROJECT_TASK,CLIENT WHERE PROJECT_INFO.id = PROJECT_TASK.id AND CLIENT.id = client_id AND assigned="+ getEmployeeId();

            btnProjectDetail.setDisable(true);
        }


        try {
            SingletonConnexionDB singletonConnexionDB = new SingletonConnexionDB();
            Connection connection = singletonConnexionDB.getConnection();

            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                TitledPane titledpane = new TitledPane();
                titledpane.setText(rs.getString("project_name"));
                VBox content = new VBox();

                String id = rs.getString("id");
                String projectname = rs.getString("project_name");
                String startdate = rs.getString("start_date");
                String enddate = rs.getString("end_date");
                String estitime = rs.getString("estimated_time");
                String clientName = rs.getString("name");

                content.getChildren().add(new Label("ID: " + id));
                content.getChildren().add(new Label("Client: " + clientName));
                content.getChildren().add(new Label("Désignation " + projectname));
                content.getChildren().add(new Label("Début: " + startdate));
                content.getChildren().add(new Label("Fin: " + enddate));
                content.getChildren().add(new Label("Temps estimé: " + estitime + " Days"));
                JFXButton showporject = new JFXButton("Afficher les détails du projet");
                showporject.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #7a8d9b, #7a8d9b);");
                content.getChildren().add(showporject);

                titledpane.setContent(content);

                accordion.getPanes().add(titledpane);

                showporject.setOnAction((event) -> {

                    Loader.setLocation(getClass().getResource("../views/projectdetail.fxml"));

                    try {
                        Loader.load();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ProjectDetailController projectDetailController = Loader.getController();

                    projectDetailController.setAdminId(getAdminId());
                    projectDetailController.setUserRole(getUserRole());

                    projectDetailController.setProjectID(id);
                    projectDetailController.getProjectID().setDisable(true);
                    projectDetailController.setProjectName(projectname);
                    projectDetailController.getProjectName().setEditable(false);
                    projectDetailController.setStart_date(LocalDate.parse(startdate));
                    projectDetailController.getStart_date().setDisable(true);

                    projectDetailController.setEnd_date(LocalDate.parse(enddate));

                    projectDetailController.getEnd_date().setDisable(true);
                    projectDetailController.setEsti_time(estitime);
                    projectDetailController.getEsti_time().setEditable(false);

                    // Calling getTableData() method to fill the table data from database following the above information
                    projectDetailController.getTableData();
                    projectDetailController.getClientList();

//                    ObservableList<String> clientList = FXCollections.observableArrayList();
//                    clientList.add(clientName);
//                    projectDetailController.projectClient.setValue(clientName);
                    projectDetailController.ClientNameText.setDisable(false);
                    projectDetailController.ClientNameText.setText(clientName);
                    projectDetailController.projectClient.setVisible(false);

                    projectDetailController.setUserRole(getUserRole());

                    if(getUserRole().matches("EMPLOYEE_AUTH")) {
                        projectDetailController.setEmployeeId(getEmployeeId());
                        projectDetailController.userIsEmployee();
                    }

                    Parent p = Loader.getRoot();
                    stage = (Stage) showporject.getScene().getWindow();
                    Scene scene = new Scene(p);
                    stage.setScene(scene);
                    stage.show();
                });
            }
            rs.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private JFXButton btnAddEmployee;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void homeBackBtnAction(ActionEvent event) {
        if(event.getSource() == homeBackBtn) {
            FXMLLoader Loader = new FXMLLoader();

            if(getUserRole().matches("ADMIN_AUTH")){
                Loader.setLocation(getClass().getResource("../views/intropageadmin.fxml"));

                try {
                    Loader.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                IntroPageAdmin introPageAdmin = Loader.getController();
                introPageAdmin.setAdminId(getAdminId());
                introPageAdmin.setUserRole(getUserRole());
                introPageAdmin.getAdminName(getAdminId());

                Parent p = Loader.getRoot();
                stage = (Stage) homeBackBtn.getScene().getWindow();
                Scene scene = new Scene(p);
                stage.setScene(scene);
                stage.show();
            }

            else{
                Loader.setLocation(getClass().getResource("../views/intropageemployee.fxml"));

                try {
                    Loader.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                IntroPageEmployee introPageEmp = Loader.getController();
                introPageEmp.setEmployeeId(getEmployeeId());
                introPageEmp.setUserRole(getUserRole());
                introPageEmp.getEmployeeName(getEmployeeId());
                introPageEmp.getProjectTableData();

                Parent p = Loader.getRoot();
                stage = (Stage) homeBackBtn.getScene().getWindow();
                Scene scene = new Scene(p);
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();

            }
        }

    }
}
