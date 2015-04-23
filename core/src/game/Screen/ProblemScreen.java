package game.Screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

import game.Camera.OrthoCamera;
import game.MathAlgorithms.mathQCreator;
import game.TTmath;

public class ProblemScreen implements Screen{
	private BitmapFont text;
	private OrthoCamera camera;
	private TTmath game;
	private int gamemode;
	private int level;
	private int[][] answers;
	private String[] questions;
	private Label label, incorrect;
	private Stage stage;
	private Table table;
	private Skin skin;
	private TextButton buttons[];
	private mathQCreator math;
	private InputProcessor previousProcessor;
	private boolean created;
    private String XXX = "";
	SpriteBatch sb;


	//int path should be added later
	public ProblemScreen(TTmath game, OrthoCamera camera, SpriteBatch sb, int gamemode, int level){
		this.camera = camera;
		this.game = game;
		this.sb = sb;
		this.gamemode = gamemode;
		this.level = level;
		this.previousProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setCatchBackKey(true);
		createDisplay();
	}

	private void createDisplay() {
		if(created){
			return;
		}

		FreeTypeFontGenerator TEXT_8BIT = new FreeTypeFontGenerator(Gdx.files.internal("resources/Minecraftia-Regular.ttf"));
		createFont(TEXT_8BIT, 25);
		TEXT_8BIT.dispose();

		createQuestions();
		createStage();
		createLabel();
		createButton();
        createScoreLabel();
        createIncorrectLabel();
		
		created = true;
		game.problemScreen = this;
	}

    private void createQuestions() {
		math = new mathQCreator();
		math.runOperation(6,level,1,1);
		answers = math.getAnswers();
		questions = math.getQuestions();

		System.out.println("level is: " + level);

	}

	private void createButton() {
		Skin skin = new Skin();
		TextureAtlas buttonAtlas;
		TextButtonStyle textButtonStyle;
		buttons = new TextButton[5];
		text.setColor(Color.WHITE);


		for(int i = 0; i < 4; i++){
			buttonAtlas = new TextureAtlas(Gdx.files.internal("Menus/buton.pack"));
			skin.addRegions(buttonAtlas);
			textButtonStyle = new TextButtonStyle();
			textButtonStyle.font = text;
			textButtonStyle.up = skin.getDrawable("MenuItem");
			buttons[i] = new TextButton(""+answers[0][i], textButtonStyle);
            buttons[i].padTop(40 * Gdx.graphics.getDensity());
            buttons[i].padBottom(15 * Gdx.graphics.getDensity());
            buttons[i].padLeft(15 * Gdx.graphics.getDensity());
            buttons[i].padRight(15 * Gdx.graphics.getDensity());
		
			final int num = i;

			buttons[i].addListener(new InputListener() {
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

					if(buttons[num].getLabel().textEquals(""+math.getCorrectAnswer(0))){

                        // update scoreboard
                        game.incrementScore();

						/*
						 * For now will just make it go back to the menuScreen
						 */
						if(gamemode == 1){
							Gdx.input.setInputProcessor(previousProcessor);
//							game.setScreen(game.previousScreen);
//							game.manageScreens(game.previousScreen);
//							game.setScreen(game.gameScreen);
							game.correctAns();
							dispose();
						}
						else if(level == 10){
							game.setScreen(new ProblemScreen(game, camera, sb, 0, level));
							dispose();
						}
						else if(game.getCounter() == 3){
							game.resetCounter();
                            game.incrementMultiplier();
							game.setScreen(new ProblemScreen(game, camera, sb, 0, level+1));
							dispose();
						}
						else{
							game.incrementCounter();
							game.setScreen(new ProblemScreen(game, camera, sb, 0, level));
							dispose();
						}




					}
					else {


						if(gamemode == 1){
							
							Gdx.input.setInputProcessor(previousProcessor);
							game.incorrectAns();
//							game.setScreen(game.previousScreen);
//							game.manageScreens(game.previousScreen);
//							game.setScreen(game.gameScreen);
							dispose();

						}
						else{

                            // incorrect answer, reset multiplier
                            game.setMultiplier(1);

							game.incorrectAns();


                            game.setScreen(new ProblemScreen(game, camera, sb, 0, level));
							if(game.getIncorrect()==3){
								game.resetAns();
								game.resetCounter();
								game.setScreen(new GameOverScreen(game, camera, sb));
                                game.resetScore();
								dispose();
							}



						}




					}
					return true;
				}

				public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					// Do nothing?
				}
			});

			table.row();
			table.add(buttons[i]);
		}

		stage.addActor(table);
	}

	private void createLabel() {
		LabelStyle ls = new LabelStyle();
		ls.font = text;
		ls.fontColor = Color.WHITE;
		label = new Label("Answer to the following question: \n"
				+questions[0],ls);
		
		table.add(label);
		stage.addActor(table);
	}

    private void createScoreLabel(){
        LabelStyle ls = new LabelStyle();
        ls.font = text;
        ls.fontColor = Color.GREEN;
        Label scoreLabel = new Label(Integer.toString(game.getScore()), ls);
//        scoreLabel.setPosition(Gdx.graphics.getWidth(),0);
        table.add(scoreLabel);
        stage.addActor(table);
    }

    private void createIncorrectLabel(){
        LabelStyle ls = new LabelStyle();
        ls.font = text;
        ls.fontColor = Color.RED;

        switch(game.getIncorrect()){
            case 0:
                XXX = "";
                break;
            case 1:
                XXX = "X";
                break;
            case 2:
                XXX = "XX";
                break;
            case 3:
                XXX = "XXX";
                break;
            default:
                XXX = "";
                break;
        }


        incorrect = new Label(XXX, ls);
        incorrect.setPosition(0, Gdx.graphics.getHeight());
        table.row();
        table.add(incorrect);
        stage.addActor(table);
    }

	private void createStage(){
		stage = new Stage();
		table = new Table(skin);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(stage);
	}
	
	public void playStage(){
		if(Gdx.input.getInputProcessor() != stage){
			Gdx.input.setInputProcessor(stage);
		}
		stage.draw();
	}

	private void createFont(FreeTypeFontGenerator ftfg, float dp){
		text = ftfg.generateFont((int)(dp * Gdx.graphics.getDensity()));
		text.setColor(Color.RED);
	}




	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		game.clear();

		camera.update();

		stage.draw();
//		if(Gdx.input.isTouched() && (Gdx.input.getX()>(Gdx.graphics.getWidth()-75) && Gdx.input.getY()<75)){
        if(Gdx.input.isKeyPressed(Input.Keys.BACK)){
            game.resetScore();

			game.setScreen(new MenuScreen(game, camera, sb));
			game.currentScreen.dispose();
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
