package sample.Data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;


public interface R {

    Map<String, Pane> panes = new HashMap<String, Pane>();

    Map<String, Stage> stages = new HashMap<String, Stage>();
    Map<String, Scene> scenes = new HashMap<String, Scene>();

    Map<String, StringProperty>  stringproperties = new HashMap<>();
    Map<String, BooleanProperty>  booleanproperties = new HashMap<>();


    //String
    Map<String, String> strings = new HashMap<>();
    Map<String, String> strings_propmt_text = new HashMap<>();

    //Thread
    Map<String, Thread> threads = new HashMap<String, Thread>();
}

