package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.util.ValidationUtil;
import java.io.*;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class ChatRoomFormController extends Thread{

    public AnchorPane chatRoomContext;
    public TextField txtMsg;
    public JFXButton btnSendMsg;
    public VBox vBox;
    public ScrollPane sp_Main;

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;
    private String username;

    final int PORT=5000;

    private LinkedHashMap<TextField, Pattern> map = new LinkedHashMap<>();

    private FileChooser fileChooser;
    private File filePath;

    public void initialize(){

          btnSendMsg.setVisible(false);
          username = LogInFormController.userName;

        try {
            socket = new Socket("localhost", PORT);
            System.out.println("Socket is connected with server!");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(username + " has entered the chat!\n");

            this.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        vBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sp_Main.setVvalue((Double)newValue);
            }
        });


        //----------------validation-----------------------
        Pattern msgPattern = Pattern.compile("^[A-z0-9 ,./?;-_`~\\<>*+'\"|!@#$%^&*(){}-]{1,}$");
        map.put(txtMsg,msgPattern);

    }

    @Override
    public void run(){
            try {
                while (true) {

                    String msg = reader.readLine();
                    String[] tokens = msg.split(" ");
                    String cmd = tokens[0];


                    String[] msgToAr = msg.split(" ");
                    String st = "";
                    for (int i = 0; i < msgToAr.length - 1; i++) {
                        st += msgToAr[i + 1] + " ";
                    }
            //======================================================================

                    Text text = new Text(st);
                    String firstChars = "";
                    if (st.length() > 3) {
                        firstChars = st.substring(0, 3);

                    }
                    System.out.println(firstChars);

                    if (firstChars.equalsIgnoreCase("img")) {
             //==============for the Images============================================

                        st = st.substring(3, st.length() - 1);

                        File file = new File(st);
                        Image image = new Image(file.toURI().toString());

                        ImageView imageView = new ImageView(image);

                        imageView.setFitHeight(150);
                        imageView.setFitWidth(200);

                        HBox hBox = new HBox(10);
                        hBox.setAlignment(Pos.BOTTOM_RIGHT);

                        if (!cmd.equalsIgnoreCase(username)) {

                            vBox.setAlignment(Pos.TOP_LEFT);
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            Text text1=new Text("  "+cmd+" :");


                            TextFlow textFlow=new TextFlow(text1,imageView);//if text big wrap it to another line
                            textFlow.setStyle(
                                    "-fx-background-color: rgb(15,125,242); " +
                                            "-fx-background-radius: 20px;" );
                            textFlow.setPadding(new Insets(5,10,5,10));



                            hBox.getChildren().add(textFlow);

                        } else {
                            TextFlow textFlow=new TextFlow(imageView);//if text big wrap it to another line
                            textFlow.setStyle(
                                    "-fx-background-color: rgb(70, 185, 28); " +
                                            "-fx-background-radius: 20px" );
                            textFlow.setPadding(new Insets(5,10,5,10));

                            hBox.setAlignment(Pos.BOTTOM_RIGHT);
                            hBox.getChildren().add(textFlow);


                        }

                        Platform.runLater(() -> vBox.getChildren().addAll(hBox));

                    } else {
                        TextFlow textFlow = new TextFlow();
                        textFlow.setStyle("-fx-color: rgb(239,242,255); " +
                                "-fx-background-color: rgb(15,125,242); " +
                                "-fx-background-radius: 20px" );

                        textFlow.setPadding(new Insets(5,10,5,10));
                        text.setFill(Color.color(0.934,0.945,0.996));

                        if (!cmd.equalsIgnoreCase(username + ":")) {
                            Text txtName = new Text(cmd + " ");
                            textFlow.getChildren().add(txtName);
                        }

                        textFlow.getChildren().add(text);
                        textFlow.setMaxWidth(200); //200

                        TextFlow flow = new TextFlow(textFlow);

                        HBox hBox = new HBox(12); //12

             //=======================================================================================

                    if (!cmd.equalsIgnoreCase(username + ":")) {

                            vBox.setAlignment(Pos.TOP_LEFT);
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.getChildren().add(flow);

                        } else {
                            Text text2=new Text(st);
                            TextFlow flow2 = new TextFlow();
                        TextFlow textFlow2 = new TextFlow(text2);
                        textFlow2.setStyle("-fx-color: rgb(239,242,255); " +
                                "-fx-background-color: rgb(70, 185, 28); " +
                                "-fx-background-radius: 20px" );

                        textFlow2.setPadding(new Insets(5,10,5,10));
                        text2.setFill(Color.color(0.934,0.945,0.996));
                            hBox.setAlignment(Pos.BOTTOM_RIGHT);
                            hBox.getChildren().add(textFlow2);
                        }
                        Platform.runLater(() -> vBox.getChildren().addAll(hBox));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public void sendMsg(){
        String msg = txtMsg.getText();
        writer.println(username + ": " + txtMsg.getText());

        txtMsg.clear();

        if(msg.equalsIgnoreCase("BYE") || (msg.equalsIgnoreCase("EXIT"))) {
            writer.println("\n"+username + " has left the chat!\n");
            System.exit(0);
        }
        btnSendMsg.setVisible(false);
    }

    public void btnSendOnAction(ActionEvent actionEvent) {
        sendMsg();
    }

    public void txtSendMsgOnAction(ActionEvent actionEvent) {
        if (txtMsg.getLength() != 0){
            sendMsg();
        }
    }

    public void imagesOnAction(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        this.filePath = fileChooser.showOpenDialog(stage);
        writer.println(username + " " + "img" + filePath.getPath());
    }

    public void textFields_Key_Released(KeyEvent keyEvent) {
        ValidationUtil.validate(map,btnSendMsg);
        if (keyEvent.getCode() == KeyCode.ENTER) {
            Object response =  ValidationUtil.validate(map,btnSendMsg);

            if (response instanceof TextField) {
                TextField textField = (TextField) response;
                textField.requestFocus();
            }
        }
    }


}
