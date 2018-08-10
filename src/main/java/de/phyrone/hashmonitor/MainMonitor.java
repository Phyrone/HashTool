package de.phyrone.hashmonitor;

import com.google.common.hash.HashCode;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.function.Function;

public class MainMonitor extends Application {


    Inputtype currendinputtype = Inputtype.TEXT;
    Scene scene;
    MenuButton alorythMenuButton;
    HashAlgorythm algorythm = HashAlgorythm.SHA1;
    TextField pathin;
    private TabPane tabPane;
    private Label outlabel;
    private TextArea textIn;
    private BlockingQueue<Runnable> workqueue = new LinkedBlockingQueue<>();
    private ThreadFactory workerFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Worker").build();
    private ExecutorService executors = Executors.newCachedThreadPool(workerFactory);

    public void start(final Stage stage) throws Exception {
        Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("QueueListener").build()).submit((Runnable) () -> {
            while (true) {
                try {
                    executors.submit(workqueue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        {
            stage.setTitle("HashGenerator");
            stage.setResizable(true);
            scene = new Scene(FXMLLoader.load(getClass().getResource("/ui/mainfx.fxml")));

        }
        {
            final CheckBox fullscxreenBTM = (CheckBox) scene.lookup("#fullscreenbtm");
            fullscxreenBTM.setOnMouseReleased((event) -> stage.setFullScreen(fullscxreenBTM.isSelected()));
        }
        {
            scene.lookup("#plaintextin").setOnKeyTyped(event -> workqueue.add(this::update));
            scene.lookup("#passwdin").setOnKeyTyped(event -> workqueue.add(this::update));
        }
        {
            try {
                tabPane = (TabPane) scene.lookup("#tabs");
                tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateInputype());
            } catch (Exception e) {
                System.err.println("Error get Tabs!");
                e.printStackTrace();
            }
        }
        {
            pathin = ((TextField) scene.lookup("#pathinputfield"));
            pathin.setText(new File(MainMonitor.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath());
            Button btm = (Button) scene.lookup("#selectfilebtm");
            btm.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select File to Hash");
                fileChooser.setInitialDirectory(new File("."));
                pathin.setText(
                        fileChooser.showOpenDialog(stage).getAbsolutePath()
                );
            });
        }

        {
            Button btm = (Button) scene.lookup("#checkfilebtm");
            btm.setOnAction(event -> workqueue.add(this::update));
        }
        {
            Button btm = (Button) scene.lookup("#copyhashbtm");
            btm.setOnAction(event -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(outlabel.getText()), null));
        }
        {
            alorythMenuButton = (MenuButton) scene.lookup("#hashdropdown");
            alorythMenuButton.getItems().clear();
            alorythMenuButton.setText(algorythm.name().toLowerCase());
            for (HashAlgorythm algorythm : HashAlgorythm.values()) {
                MenuItem item = new MenuItem();
                item.setId(algorythm.name().toLowerCase());
                item.setText(algorythm.name().toLowerCase());
                item.setOnAction(event -> {
                    alorythMenuButton.setText(item.getText());
                    MainMonitor.this.algorythm = algorythm;
                    workqueue.add(this::update);
                });
                alorythMenuButton.getItems().add(item);
            }
        }

        outlabel = (Label) scene.lookup("#showhashlabel");

        /* Starting FX */
        stage.setScene(scene);
        stage.show();
        update();
    }

    private void update() {

        update(currendinputtype.function.apply(scene));
    }

    private void updateInputype() {
        switch (tabPane.getSelectionModel().getSelectedItem().getId()) {
            case "plaintexttab":
                currendinputtype = Inputtype.TEXT;
                break;
            case "passwdtab":
                currendinputtype = Inputtype.PASSWORD;
                break;
            case "filetab":
                currendinputtype = Inputtype.FILE;
                return;
            default:
                System.err.println("Error Tab is Unknown!");
                break;
        }
        update();
    }

    private void update(byte[] input) {
        HashCode hash = algorythm.get().hashBytes(input);

        Platform.runLater(() -> outlabel.setText(hash.toString()));

    }

    enum Inputtype {
        TEXT(scene -> ((TextArea) scene.lookup("#plaintextin")).getText().getBytes(StandardCharsets.UTF_8)),
        PASSWORD(scene -> ((PasswordField) scene.lookup("#passwdin")).getText().getBytes(StandardCharsets.UTF_8)),
        FILE(scene -> {
            String path = ((TextField) scene.lookup("#pathinputfield")).getText();
            byte[] ret = new byte[0];
            try {
                try {
                    FileInputStream stream = new FileInputStream(new File(path));
                    ret = (IOUtils.toByteArray(stream));
                    stream.close();
                } catch (IOException e) {
                    ret = (IOUtils.toByteArray(new URL(path)));
                }
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("File or URL not Found");
                alert.show();
            }
            System.gc();
            return ret;
        });

        Function<Scene, byte[]> function;

        Inputtype(Function<Scene, byte[]> function) {
            this.function = function;
        }
    }
}
