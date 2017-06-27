package futin;

import futin.view.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;

public class Main extends Application {

    public static final int PERIOD_TO_NEXT_VOCABULARY = 3000000; // 5 minutes.

    private Stage mPrimaryStage;
    private MainController mMainController;
    private Timer mTimer;
    private LoadNextWordTask mTask;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mPrimaryStage = primaryStage;
        mPrimaryStage.setTitle("Vocabulary tool");
        mPrimaryStage.setAlwaysOnTop(true);

        initLayout();

        mTimer = new Timer();

        mTask = new LoadNextWordTask(new LoadNextWordTask.LoadNextWordTaskCallback() {
            @Override
            public void preExecute() {
                mMainController.loadingNextVocabulary();
            }

            @Override
            public void postExecute(Vocabulary vocabulary) {
                Platform.runLater(() -> mMainController.updateVocabulary(vocabulary));
                mMainController.finishLoadingNextVocabulary();
            }
        });

        mTimer.schedule(mTask, 0, PERIOD_TO_NEXT_VOCABULARY);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        mTask.cancel();
        mTimer.cancel();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void initLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("view/main.fxml"));
            Parent root = loader.load();
            mPrimaryStage.setScene(new Scene(root));

            mMainController = loader.getController();
            mMainController.setMain(this);

            mPrimaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextVocabulary() {
        mTask.run();
    }

    public void showSearchWindow() {
        try {
            // Load the fxml and create a new stage for the search window.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/search.fxml"));
            AnchorPane searchWindow = loader.load();

            // Create the new stage.
            Stage stage = new Stage();
            stage.setTitle("Search a word");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mPrimaryStage);
            Scene scene = new Scene(searchWindow);
            stage.setScene(scene);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
