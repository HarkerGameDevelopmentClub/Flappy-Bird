package listeners;

import java.util.HashSet;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

public class KeyboardListener {
	
	HashSet<String> input = new HashSet<String>();
	
	public KeyboardListener(Scene scene) {
		
		scene.setOnKeyPressed(
	            new EventHandler<KeyEvent>()
	            {
	                public void handle(KeyEvent e)
	                {
	                    String code = e.getCode().toString();
	                    input.add(code);
	                }
	            }
	        );
	     
	        scene.setOnKeyReleased(
	            new EventHandler<KeyEvent>()
	            {
	                public void handle(KeyEvent e)
	                {
	                    String code = e.getCode().toString();
	                    input.remove(code);
	                }
	            }
	        );
		
	}
	
	public boolean isKeyPressed(String key){
		return input.contains(key);
	}

}
