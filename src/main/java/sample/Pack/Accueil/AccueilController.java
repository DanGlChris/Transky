package sample.Pack.Accueil;


import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import net.sourceforge.javaflacencoder.FLACFileWriter;
import sample.Data.R;
import sample.Data.S;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class AccueilController implements Initializable, GSpeechResponseListener {
    private double xoffset, yoffset;
    private final double text_transcription_normal = 38, text_transcription_large = 82;
    protected String old_text = "";
    private static boolean record_active = false;
    private BooleanProperty clearActionProperty = new SimpleBooleanProperty(false);
    private Image image_black_micro = new Image("sample/Pack/Accueil/image/icons8-microphone-100.png");
    private Image image_red_micro = new Image("sample/Pack/Accueil/image/icons8-microphone-100 (1).png");
    private final Microphone mic = new Microphone(FLACFileWriter.FLAC);
    private StringProperty transcription = new SimpleStringProperty("Listen some audio in your computer");
    protected Thread Recorde_thread;

    /**
     * google services
     */
    private GSpeechDuplex duplex = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");
    private Translate translator = TranslateOptions.newBuilder().setApiKey("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw").build().getService();

    @FXML
    public AnchorPane Parent;

    @FXML
    public JFXTextArea Text_transcription;

    @FXML
    public JFXTextArea Text_translate;

    @FXML
    public JFXButton Micro_button;

    @FXML
    public ImageView micro;

    @FXML
    public ChoiceBox<String> language_list;

    @FXML
    public ChoiceBox<String> Translate_to;

    @FXML
    public CheckBox Check_btn;

    @FXML
    public void Close(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    public void Clear(ActionEvent event) {
        Micro_button.getOnAction().handle(new ActionEvent());
        Micro_button.getOnAction().handle(new ActionEvent());
        Text_transcription.clear();
    }

    @FXML
    public void Open_Link(ActionEvent event) {
        openWebpage("https://github.com/DanGlChris");
    }


    public void initialize(URL url, ResourceBundle resourceBundle){
        R.stringproperties.put(S.StrP_Text_Transcription, Text_transcription.textProperty());
        R.booleanproperties.put(S.Bool_Prop_Restart_listen, clearActionProperty);
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
        /**
         * add posibitily to desable translation
         */
        Check_btn.setOnAction(e->{
            if(!Check_btn.isSelected()){
                Text_transcription.prefHeightProperty().set(text_transcription_large);
                Text_translate.setVisible(false);
                Translate_to.setDisable(true);
            }else{
                Text_transcription.prefHeightProperty().set(text_transcription_normal);
                Text_translate.setVisible(true);
                Translate_to.setDisable(false);
            }
        });

        /**
         * add 3 language into choicebox
         */
        language_list.getItems().add("English");
        language_list.getItems().add("Français");
        language_list.getItems().add("Русский");
        language_list.getSelectionModel().selectFirst();

        Translate_to.getItems().add("English");
        Translate_to.getItems().add("Français");
        Translate_to.getItems().add("Русский");
        Translate_to.getSelectionModel().select(1); //Français
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
         * This boolean will reactive microphone anywhere
         */
        clearActionProperty.addListener((observable, oldValue, newValue) -> {
            Clear(new ActionEvent());
        });


        /**
         * this part allow to implement speech transcription
         */
        duplex.setLanguage(R.strings.get(language_list.getSelectionModel().getSelectedItem()));

        duplex.addResponseListener(new GSpeechResponseListener() {

            @Override
            public void onResponse(GoogleResponse gr) {
                transcription.set(gr.getResponse());
                if (gr.getResponse() == null) {
                    old_text = transcription.get();
                    if (old_text.contains("(")) {
                        old_text = old_text.substring(0, old_text.indexOf('('));
                    }
                    old_text = ( transcription.get() + " " );
                    old_text = old_text.replace(")", "").replace("( ", "");
                    transcription.set(old_text);
                    return;
                }
                if (transcription.get().contains("(")) {
                    transcription.set(transcription.get().substring(0, transcription.get().indexOf('(')));
                }
                if (!gr.getOtherPossibleResponses().isEmpty()) {
                    transcription.set(transcription.get() + " ("
                            + (String) gr.getOtherPossibleResponses().get(0) + ")");
                    /**
                     * this method allow to get translate of the possible o
                     */
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


        /**
         * Synchronise text to translator
         */
        Text_transcription.textProperty().addListener((observable, oldValue, newValue) -> {
            if(language_list.getSelectionModel().getSelectedItem()
                    !=Translate_to.getSelectionModel().getSelectedItem()){

                Text_translate.textProperty().set(
                        translator.translate(
                                newValue,
                                Translate.TranslateOption.sourceLanguage(
                                        R.strings.get(language_list.getSelectionModel().getSelectedItem())),
                                Translate.TranslateOption.targetLanguage(
                                        R.strings.get(Translate_to.getSelectionModel().getSelectedItem()))).getTranslatedText());

            }else{
                Text_translate.textProperty().set(Text_transcription.textProperty().getValue());
            }
            Platform.runLater(() -> {
                Text_transcription.positionCaret(Text_transcription.getText().length());
                Text_translate.positionCaret(Text_translate.getText().length());
            });


        });
    }

    @Override
    public void onResponse(GoogleResponse gr) {

    }

    /**
     * this fuction help to open web browser
     * when a link is clicked
     * @param urlString
     */
    public static void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
