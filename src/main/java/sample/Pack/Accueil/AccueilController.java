package sample.Pack.Accueil;


import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import net.sourceforge.javaflacencoder.FLACFileWriter;
import sample.Data.R;
import sample.Data.S;

import java.net.URL;
import java.util.ResourceBundle;

public class AccueilController implements Initializable, GSpeechResponseListener {
    private double xoffset, yoffset;
    protected String old_text = "";
    private static boolean record_active = false;
    private Image image_black_micro = new Image("sample/Pack/Accueil/image/icons8-microphone-100.png");
    private Image image_red_micro = new Image("sample/Pack/Accueil/image/icons8-microphone-100 (1).png");
    private GSpeechDuplex duplex;
    private final Microphone mic = new Microphone(FLACFileWriter.FLAC);
    private StringProperty transcription = new SimpleStringProperty("Listen some audio in your computer");
    protected Thread Recorde_thread;

    @FXML
    public AnchorPane Parent;

    @FXML
    public JFXTextArea Text_transcription;

    @FXML
    public JFXButton Micro_button;

    @FXML
    public ImageView micro;

    @FXML
    public ChoiceBox<String> language_list;

    @FXML
    public void Close(ActionEvent event) {
        System.exit(0);
    }
    @FXML
    public void Clear(ActionEvent event) {
        transcription.set("");
        old_text = "";
        Text_transcription.clear();
    }


    public void initialize(URL url, ResourceBundle resourceBundle){
        R.stringproperties.put(S.StrP_Text_Transcription, Text_transcription.textProperty());
        /**
         * Initialization Data language support
         */

        R.strings.put(S.English, "en");
        R.strings.put(S.French, "fr");
        R.strings.put(S.Russian, "ru");

        /**
         * Initialization Propmt text Data
         */
        R.strings_propmt_text.put(S.English, S.Str_Prompt_text_English);
        R.strings_propmt_text.put(S.French, S.Str_Prompt_text_French);
        R.strings_propmt_text.put(S.Russian, S.Str_Prompt_text_Russian);

        Parent.setOnMousePressed(e-> {
            xoffset = e.getSceneX();
            yoffset = e.getSceneY();
        });
        Parent.setOnMouseDragged(e->{
            Parent.getScene().getWindow().setX(e.getScreenX()-xoffset);
            Parent.getScene().getWindow().setY(e.getScreenY()-yoffset);
        });

        //Text_transcription.textProperty().bind(transcription);

        Micro_button.setOnAction(e->{
            if(record_active){
                /**
                 * record stop
                 */
                record_active = false;
                micro.imageProperty().set(image_black_micro);
                mic.close();
                duplex.stopSpeechRecognition();
            }else{
                /**
                 * record start
                 */
                record_active = true;
                micro.imageProperty().set(image_red_micro);
                Recorde_thread = new Thread(() -> {
                    try {
                        duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                });
                Recorde_thread.start();
            }
        });

        language_list.getItems().add("English");
        language_list.getItems().add("Français");
        language_list.getItems().add("Русский");
        language_list.getSelectionModel().selectFirst();
        /**
         * change language each time other language in app is selected
         */
        language_list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            duplex.setLanguage(R.strings.get(newValue));
            Text_transcription.clear();
            Text_transcription.promptTextProperty().set(R.strings_propmt_text.get(newValue));
            System.out.println();
            if(record_active){
                /**
                 * If language change, the record must restart
                 */
                Micro_button.getOnAction().handle(new ActionEvent());
                Micro_button.getOnAction().handle(new ActionEvent());
            }
        });

        /**
         * this part allow to implement speech transcription
         */
        duplex = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");
        duplex.setLanguage(R.strings.get(language_list.getSelectionModel().getSelectedItem()));

        duplex.addResponseListener(new GSpeechResponseListener() {

            @Override
            public void onResponse(GoogleResponse gr) {

                transcription.set(gr.getResponse());
                if (gr.getResponse() == null) {
                    old_text = Text_transcription.getText();
                    if (old_text.contains("(")) {
                        old_text = old_text.substring(0, old_text.indexOf('('));
                    }
                    old_text = ( Text_transcription.getText() + " " );
                    old_text = old_text.replace(")", "").replace("( ", "");
                    Text_transcription.setText(old_text);
                    return;
                }
                if (transcription.get().contains("(")) {
                    transcription.set(transcription.get().substring(0, transcription.get().indexOf('(')));
                }
                if (!gr.getOtherPossibleResponses().isEmpty()) {
                    transcription.set(transcription.get() + " (" + (String) gr.getOtherPossibleResponses().get(0) + ")");
                }
                if(transcription.get().length()>120){
                    transcription.set(transcription.get().substring(transcription.get().length()-120, transcription.get().length()-1));
                }
                System.out.println(transcription.get());
                /**
                 * this help to skip the current Thread
                 */
                Platform.runLater(()->{
                    Text_transcription.textProperty().set(transcription.get());
                });
                //R.stringproperties.get(S.StrP_Text_Transcription).set();
                //Text_transcription.textProperty().set(transcription.get());
            }
        });

    }

    @Override
    public void onResponse(GoogleResponse gr) {

    }
}
