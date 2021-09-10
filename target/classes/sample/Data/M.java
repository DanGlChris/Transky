/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.Data;

import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author DanGlChris
 */
public interface M {
    /**
     * cette methode permet de recadrer le stage au centre de l'ecran
     * @param stage
     */
    public static void RecadrerStage(Stage stage){
        stage.setX((Screen.getPrimary().getBounds().getWidth()-(stage.getScene().widthProperty().get()==0? 300: stage.getScene().widthProperty().getValue()))/2);
        stage.setY((Screen.getPrimary().getBounds().getHeight()-(stage.getScene().heightProperty().get()==0? 130: stage.getScene().heightProperty().get()))/2);
    }
    /**
     * cette methode permet de recadrer un stage en fonction d'un Accueil stage plus grand que lui
     * @param reference
     * @param stage
     */
    public static void RecadrerStage(Stage reference, Stage stage){
        stage.setX(reference.xProperty().get()+(reference.widthProperty().subtract(stage.getScene().widthProperty().get()==0? 300: stage.getScene().widthProperty().get()).getValue())/2);
        stage.setY(reference.yProperty().get()+(reference.heightProperty().subtract(stage.getScene().heightProperty().get()==0? 130: stage.getScene().heightProperty().get()).getValue())/2);
    }
    /**
     * cette methode permet de passer d'un stage a un Accueil
     * @param stage1
     * @param stage2
     * @param sens cette valeur permet de determiner le sens de transition
     *      true correspond de 1 a 2 et false de 2 a 1
     */
    public static void Change_Stage(Stage stage1, Stage stage2, boolean sens){
        if(sens){
            stage2.show();
            M.RecadrerStage(stage2);
            stage1.close();
        }else{
            stage1.show();
            stage2.close();
        }
    }
}
