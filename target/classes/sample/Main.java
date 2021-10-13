package sample;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Data.M;
import sample.Data.R;
import sample.Data.S;
import sample.Pack.Accueil.AccueilController;


/**
 *
 * @author DanGlChris
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane root =  FXMLLoader.load(getClass().getResource("Pack/Accueil/Accueil.fxml"));

        Scene scene = new Scene(root);

        R.scenes.put(S.Scne_Accueil, scene);
        R.stages.put(S.Stge_Accueil, stage);  //on ajoute le stage dans la liste des stages
        stage.setScene(scene);
        stage.setTitle("Transky");
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();



        /**
         * KeyEvent
         */

        R.scenes.get(S.Scne_Accueil).addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.SPACE) {
                R.booleanproperties.get(S.Bool_Prop_Restart_listen).set(!R.booleanproperties.get(S.Bool_Prop_Restart_listen).getValue());
                System.out.println("Restart to listen");
            }
            event.consume();
        });

        M.RecadrerStage(stage);   // mettre le stage au mileu de l'ecran
        //Load_Ressource();
    }

    public static void main(String[] args) {

        launch(args);
    }

    private void Load_Ressource()throws IOException{
        //Load_Stage(S.Stge_TeachMe, S.Scne_TeachMe, "/Pack/TeachMe/TeachMe.fxml");
    }

    /**
     * cette methode permet de charger un fichier fxml, la met dans une scene
     * puis l'assigne a un stage
     *
     * le stage est referé grace au @StageId
     * @param Fxml_Source source du fichier fxml
     * @param SceneId id de la scene
     * @param StageId id du stage
     */
    public void Load_Stage(String StageId, String SceneId, String Fxml_Source) throws IOException{
        Load_Scene(SceneId, Fxml_Source);   // chargerment du fxml dans la Scene de Id SceneID
        Stage stage = new Stage();
        stage.setScene(R.scenes.get(SceneId)); //recuperation de la Scene de Id: SceneId
        stage.initModality(Modality.APPLICATION_MODAL);   //rend les autres fenetres de l'app disable
        stage.initStyle(StageStyle.TRANSPARENT);
        R.stages.put(StageId, stage);  //add this stage in Ressource file with StageId ID
        M.RecadrerStage(stage);   // mettre le stage au mileu de l'ecran
    }
    /**
     * cette methode permet de charger un fichier fxml puis le me dans
     * une scene enreigistré dans un le Ressource
     * @param SceneId
     * @param Fxml_Source
     * @throws IOException
     */
    public void Load_Scene(String SceneId, String Fxml_Source)throws IOException{   //cette methode n'est pas dans M car un interface n'a pas la methode getClass
        StackPane root = FXMLLoader.load(getClass().getResource(Fxml_Source));
        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
        R.scenes.put(SceneId, scene);
    }

}
