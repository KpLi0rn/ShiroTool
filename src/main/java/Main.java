import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    // 启动代码
    @Override
    public void start(Stage primaryStage) throws Exception{
        // 文件位置不对
        Parent root = FXMLLoader.load(this.getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Shiro Key 探测工具 coded by 天下大木头");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
