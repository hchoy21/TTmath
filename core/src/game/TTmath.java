package game;

import game.Camera.OrthoCamera;
import game.Screen.GameScreen;
import game.Screen.MenuScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TTmath extends Game{

	public static int WIDTH = 480, HEIGHT = 700;
	private OrthoCamera camera;
	private SpriteBatch sb;
	public MenuScreen mainMenuScreen;
	public Screen previousScreen;


	@Override
	public void create() {
		camera = new OrthoCamera();
		camera.resize();
		sb = new SpriteBatch();
		mainMenuScreen = new MenuScreen(this, camera, sb);
		setScreen(mainMenuScreen);              
	}
	
	public void clear(){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	public void setPreviousScreen(Screen previousScreen){
		this.previousScreen = previousScreen;
	}

}
