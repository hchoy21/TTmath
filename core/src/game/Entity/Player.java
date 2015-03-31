package game.Entity;

import game.TTmath;
import game.TextureManager;
import game.Camera.OrthoCamera;
import game.GameItems.ItemManager;
import game.Screen.ProblemScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;

public class Player extends Entity implements InputProcessor{
	private TTmath game;
	private OrthoCamera camera;
	private SpriteBatch sb;
	private ItemManager itemManager;
	private TiledMapTileLayer collisionLayer;
	private Animation[] playerA = new Animation[4];
	private TextureRegion[][] animations = new TextureRegion[4][2];
	TextureRegion currentFrame;
	static float playerHeight = 30;
	static float playerWidth = 30;
	static float tileWidth;
	static float tileHeight;
	float stateTime;
	float distX;
	float distY;
	boolean movingRight = false;
	boolean movingLeft = false;
	boolean movingUp = false;
	boolean movingDown = false;
	boolean isMoving = false;

	//tags for tiles
	//blocked
	//start
	//end
	//math

	public Player(Vector2 pos, Vector2 direction, OrthoCamera camera, TiledMapTileLayer collLayer, TTmath game, SpriteBatch sb, ItemManager itemManager) {
		super(TextureManager.PLAYER, pos, direction);
		this.game = game;
		this.sb = sb;
		this.itemManager = itemManager;
		this.camera = camera;
		this.collisionLayer = collLayer;
		tileWidth = collisionLayer.getTileWidth();
		tileHeight = collisionLayer.getTileHeight();

		//set up texture animations for player
		//forward
		for(int i=0;i<animations[0].length;i++){
			animations[0][i] = new TextureRegion(TextureManager.PLAYER, (i*32), 0, 32, 32);
		}
		//backward
		for(int i=0;i<animations[0].length;i++){
			animations[1][i] = new TextureRegion(TextureManager.PLAYER, (i*32), 32, 32, 32);
		}
		//left
		for(int i=2;i<(animations[0].length+2);i++){
			animations[2][i-2] = new TextureRegion(TextureManager.PLAYER, (i*32), 0, 32, 32);
		}
		//right
		for(int i=2;i<(animations[0].length+2);i++){
			animations[3][i-2] = new TextureRegion(TextureManager.PLAYER, (i*32), 32, 32, 32);
		}
		//set animations for player
		for(int i=0;i<playerA.length;i++){
			playerA[i] = new Animation(0.1f, animations[i]);
		}
		setTextureRegion(animations[0][0]);
		distX = this.pos.x;
		distY = this.pos.y;
		stateTime = 0f;
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void update() {
		pos.add(direction);

		//get new state time
		stateTime += Gdx.graphics.getDeltaTime();
		updateAnimation(stateTime);
		checkCollision(isMoving);
		checkForItems();
		camera.setPosition(pos.x, pos.y);
		camera.update();

		if(endReach()){
			System.out.println("U REACHED THE END");
			game.setScreen(new ProblemScreen(game, camera, sb));
		}
	}

	private void checkForItems() {
		if(itemExists(this.pos.x/tileWidth, this.pos.y/tileHeight)){
			
			System.out.println("Item exists");
		}
		
	}

	private void updateAnimation(float stateTime){
		//animation update to make it consistent
		if((distY+20)<this.pos.y){
			setTextureRegion(playerA[1].getKeyFrame(stateTime, true));
			distY = this.pos.y;
		}
		if((distY-20)>this.pos.y){
			setTextureRegion(playerA[0].getKeyFrame(stateTime, true));
			distY = this.pos.y;
		}
		if((distX+20)<this.pos.x){
			setTextureRegion(playerA[3].getKeyFrame(stateTime, true));
			distX = this.pos.x;
		}
		if((distX-20)>this.pos.x){
			setTextureRegion(playerA[2].getKeyFrame(stateTime, true));
			distX = this.pos.x;
		}
	}

	//checks the current tile of the player to see if he reached the end or not
	private boolean endReach() {
		int tileX = (int) (this.getPosition().x / collisionLayer.getTileWidth());
		int tileY = (int) (this.getPosition().y / collisionLayer.getTileHeight());
		Cell cell = collisionLayer.getCell(tileX, tileY);
		return cell != null && cell.getTile().getProperties().containsKey("end");
	}

	//gets collision based on direction
	private void checkCollision(boolean moving){
		if(moving){
			if(movingDown && collidesBottom()){
				setTextureRegion(playerA[0].getKeyFrame(stateTime, true));
				movingDown = false;
				setDirection(0,0);
			}
			if(movingUp && collidesTop()){
				setTextureRegion(playerA[1].getKeyFrame(stateTime, true));
				movingUp = false;
				setDirection(0,0);
			}
			if(movingRight && collidesRight()){
				setTextureRegion(playerA[3].getKeyFrame(stateTime, true));
				movingRight = false;
				setDirection(0,0);
			}
			if(movingLeft && collidesLeft()){
				setTextureRegion(playerA[2].getKeyFrame(stateTime, true));
				movingLeft = false;
				setDirection(0,0);
			}
			isMoving = false;
		}
	}

	/*
	 * collision check called from checkCollision
	 * added +3y more for tile detection right and left.
	 * added +5x more for tile detection top and bottom
	 * added -2y for tile detection bottom
	 * for making collision detection better
	 * added border collision check
	 */
	public boolean collidesRight() {
		if(this.pos.x>(collisionLayer.getWidth()*tileWidth-25)){
			return true;
		}
		for(float step = 0; step < playerHeight; step += collisionLayer.getTileHeight() / 2){
			if(isCellBlocked((this.getPosition().x+ + playerWidth) / tileWidth, ((this.getPosition().y + step)+3) / tileHeight))
				return true;
		}
		return false;
	}

	public boolean collidesLeft() {
		if(this.pos.x<-5){
			return true;
		}
		for(float step = 0; step < playerHeight; step += collisionLayer.getTileHeight() / 2){
			if(isCellBlocked((this.getPosition().x) / tileWidth, ((this.getPosition().y + step)+3) / tileHeight))
				return true;
		}
		return false;
	}

	public boolean collidesTop() {
		if(this.pos.y>(collisionLayer.getHeight()*tileHeight-20)){
			return true;
		}
		for(float step = 0; step < playerWidth; step += collisionLayer.getTileWidth() / 2){
			if(isCellBlocked(((this.getPosition().x + step)+5) / tileWidth, ((this.getPosition().y + playerHeight)) / tileHeight))
				return true;
		}
		return false;
	}

	public boolean collidesBottom() {
		if(this.pos.y<5){
			return true;
		}
		for(float step = 0; step < playerWidth; step += collisionLayer.getTileWidth() / 2){
			if(isCellBlocked(((this.getPosition().x + step)+5) / tileWidth, ((this.getPosition().y)-2) / tileHeight))
				return true;
		}
		return false;
	}

	//checks if the cell contains tag "blocked"
	private boolean isCellBlocked(float x, float y){
		Cell cell = collisionLayer.getCell((int) x, (int) y);
		return cell !=null && cell.getTile().getProperties().containsKey("blocked");
	}

	private boolean itemExists(float x, float y){
		Cell cell = collisionLayer.getCell((int) x, (int) y);
		return cell !=null && cell.getTile().getProperties().containsKey("item");
	}
	
	/*touch movement implemented
	 *gdx.input.getDeltaY/X refers to the distance the finger is moving from where
	 *it stopped, so any directional change will be the new origin for getDeltaY/X
	 */
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		isMoving = true; 

		if(Gdx.input.getDeltaY()>10 && !collidesBottom()){
			setTextureRegion(animations[0][0]);
			movingDown = true;
			setDirection(0,-170);
		}
		else if(Gdx.input.getDeltaY()<-10 && !collidesTop()){
			setTextureRegion(animations[1][0]);
			movingUp = true;
			setDirection(0,170);
		}
		else if(Gdx.input.getDeltaX()>10 && !collidesRight()){
			setTextureRegion(animations[3][0]);
			movingRight = true;
			setDirection(170,0);
		}
		else if(Gdx.input.getDeltaX()<-10 && !collidesLeft()){
			setTextureRegion(animations[2][0]);
			movingLeft = true;
			setDirection(-170,0);
		}

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	//after touch is released, set movement to 0
	//might need to set "isMoving" to false
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		setDirection(0, 0);
		return false;
	}

	//below methods will not be used

	//not used
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	//not used
	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	//not used
	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	//not used
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	//not used
	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}