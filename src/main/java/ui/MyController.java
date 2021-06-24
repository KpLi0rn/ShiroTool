package ui;

import common.HttpUtils;
import core.ShiroKeyBrute;
import entity.ControllersFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


public class MyController implements Initializable {

    public ShiroKeyBrute shiroKeyBrute = null;

    @FXML
    private Button button;

    @FXML
    private TextField attackUrl;

    @FXML
    private TextField postParam;

    @FXML
    private ComboBox<String> httpMethod;

    @FXML
    public TextArea result;

    @FXML
    private ComboBox<String> contentType;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComboBox();
        // 将对象存进去
        ControllersFactory.controllers.put(MyController.class.getSimpleName(),this);
    }

    @FXML
    public void execResult(ActionEvent event) throws Exception {
        String method = httpMethod.getValue();
        String contenttype = contentType.getValue();
        // 将我们的方法存进去
        ControllersFactory.paramsContext.put("Method",method);
        ControllersFactory.paramsContext.put("ContentType",contenttype);
        ControllersFactory.paramsContext.put("PostParam",postParam.getText());

        String targetUrl = attackUrl.getText();
        // 这样的话每按一次按钮都重新创建对象
        if (targetUrl.length() != 0){
            this.shiroKeyBrute = new ShiroKeyBrute();
            this.shiroKeyBrute.shiroDetect(targetUrl);
        } else {
            this.result.appendText("[!] URL地址为空\n");
        }
    }

    public void initComboBox(){
        ObservableList<String> methods = FXCollections.observableArrayList(new String[] { "GET", "POST" });
        httpMethod.setPromptText("GET");
        httpMethod.setValue("GET");
        // 设置combobox 的属性
        httpMethod.setItems(methods);

        httpMethod.setOnAction(event ->{
            if (httpMethod.getValue().equals("POST")){
                contentType.setDisable(false);
                postParam.setDisable(false);
            }

            if (httpMethod.getValue().equals("GET")){
                contentType.setDisable(true);
                postParam.setDisable(true);
            }
            }
        );

        ObservableList<String> contentTypes = FXCollections.observableArrayList(new String[] { "application/x-www-form-urlencoded", "application/json","application/xml" });
        contentType.setPromptText("application/x-www-form-urlencoded");
        contentType.setValue("application/x-www-form-urlencoded");
        contentType.setItems(contentTypes);

        contentType.setDisable(true);
        postParam.setDisable(true);
    }

    public void initAttack(){
        String targetUrl = attackUrl.getText();
    }

}
