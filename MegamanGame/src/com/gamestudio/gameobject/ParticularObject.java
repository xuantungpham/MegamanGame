package com.gamestudio.gameobject;

import com.gamestudio.state.GameWorldState;
import com.gamestudio.effect.Animation;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class ParticularObject extends GameObject {

    public static final int LEAGUE_TEAM = 1;
    public static final int ENEMY_TEAM = 2;
    
    public static final int LEFT_DIR = 0;
    public static final int RIGHT_DIR = 1;

    public static final int ALIVE = 0;
    public static final int BEHURT = 1;
    public static final int FEY = 2;
    public static final int DEATH = 3;
    public static final int NOBEHURT = 4;
    private int state = ALIVE;
    
    private float width;
    private float height;
    private float mass;
    private float speedX;
    private float speedY;
    private int blood;
    private int maxBlood; //them
    private int damage;
    
    private int direction;

    protected Animation behurtForwardAnim, behurtBackAnim;
    
    private int teamType;
    
    private long startTimeNoBeHurt;
    private long timeForNoBeHurt;

    public ParticularObject(float x, float y, float width, float height, float mass, int blood, GameWorldState gameWorld){

        // posX and posY are the middle coordinate of the object
        super(x, y, gameWorld);
        setWidth(width);
        setHeight(height);
        setMass(mass);
        this.blood=blood;
        this.maxBlood=blood;
        
        direction = RIGHT_DIR;

    }
     public int getMaxBlood() {
        return maxBlood;
    }

    public void setMaxBlood(int maxBlood) {
        this.maxBlood = maxBlood;
    }
    
    public void setTimeForNoBehurt(long time){
        timeForNoBeHurt = time;
    }
    
    public long getTimeForNoBeHurt(){
        return timeForNoBeHurt;
    }
    
    public void setState(int state){
        this.state = state;
    }
    
    public int getState(){
        return state;
    }
    
    public void setDamage(int damage){
            this.damage = damage;
    }

    public int getDamage(){
            return damage;
    }

    
    public void setTeamType(int team){
        teamType = team;
    }
    
    public int getTeamType(){
        return teamType;
    }
    
    public void setMass(float mass){
        this.mass = mass;
    }

    public float getMass(){
            return mass;
    }

    public void setSpeedX(float speedX){
        this.speedX = speedX;
    }

    public float getSpeedX(){
        return speedX;
    }

    public void setSpeedY(float speedY){
        this.speedY = speedY;
    }

    public float getSpeedY(){
        return speedY;
    }

    public void setBlood(int blood){
        if(blood>=0)
                this.blood = blood;
        else this.blood = 0;
    }

    public int getBlood(){
        return blood;
    }

    public void setWidth(float width){
        this.width = width;
    }

    public float getWidth(){
        return width;
    }

    public void setHeight(float height){
        this.height = height;
    }

    public float getHeight(){
        return height;
    }
    
    public void setDirection(int dir){
        direction = dir;
    }
    
    public int getDirection(){
        return direction;
    }
    
    public abstract void attack();
    
    
    public boolean isObjectOutOfCameraView(){
        if(getPosX() - getGameWorld().camera.getPosX() > getGameWorld().camera.getWidthView() ||
                getPosX() - getGameWorld().camera.getPosX() < -50
            ||getPosY() - getGameWorld().camera.getPosY() > getGameWorld().camera.getHeightView()
                    ||getPosY() - getGameWorld().camera.getPosY() < -50)
            return true;
        else return false;
    }
    
    public Rectangle getBoundForCollisionWithMap(){
        Rectangle bound = new Rectangle();
        bound.x = (int) (getPosX() - (getWidth()/2));
        bound.y = (int) (getPosY() - (getHeight()/2));
        bound.width = (int) getWidth();
        bound.height = (int) getHeight();
        return bound;
    }

    public void beHurt(int damgeEat){
        setBlood(getBlood() - damgeEat);
        state = BEHURT;
        hurtingCallback();
    }

    @Override
    public void Update(){
        switch(state){
            case ALIVE:
                
                // note: SET DAMAGE FOR OBJECT NO DAMAGE
                ParticularObject object = getGameWorld().particularObjectManager.getCollisionWidthEnemyObject(this);
                if(object!=null){
                    
                    
                    if(object.getDamage() > 0){

                        // switch state to fey if object die
                        
                        
                        System.out.println("eat damage.... from collision with enemy........ "+object.getDamage());
                        beHurt(object.getDamage());
                    }
                    
                }
                
                
                
                break;
                
            case BEHURT:
                if(behurtBackAnim == null){
                    state = NOBEHURT;
                    startTimeNoBeHurt = System.nanoTime();
                    if(getBlood() == 0)
                            state = FEY;
                    
                } else {
                    behurtForwardAnim.Update(System.nanoTime());
                    if(behurtForwardAnim.isLastFrame()){
                        behurtForwardAnim.reset();
                        state = NOBEHURT;
                        if(getBlood() == 0)
                            state = FEY;
                        startTimeNoBeHurt = System.nanoTime();
                    }
                }
                
                break;
                
            case FEY:
                
                state = DEATH;
                
                break;
            
            case DEATH:
                
                
                break;
                
            case NOBEHURT:
                System.out.println("state = nobehurt");
                if(System.nanoTime() - startTimeNoBeHurt > timeForNoBeHurt)
                    state = ALIVE;
                break;
        }
        
    }
    // File: ParticularObject.java
// ... (Sau phương thức Update)

    public void drawHealthBar(Graphics2D g2) {

        // Chỉ vẽ thanh máu khi đối tượng còn sống (ALIVE) và máu nhỏ hơn máu tối đa
        if (state == ALIVE && blood < maxBlood) { 

            int currentHP = getBlood();

            // Cài đặt kích thước thanh máu (bạn có thể điều chỉnh)
            int barWidth = 40; 
            int barHeight = 6; 
            int padding = 5; // Khoảng cách từ đầu quái vật

            // Lấy vị trí của quái vật trên màn hình (đã trừ offset Camera)
            int xOnScreen = (int) (getPosX() - getGameWorld().camera.getPosX());
            int yOnScreen = (int) (getPosY() - getGameWorld().camera.getPosY());

            // 1. Xác định vị trí thanh máu (trên đầu đối tượng)
            // Tọa độ X: căn giữa thanh máu so với tâm đối tượng
            int barX = xOnScreen - barWidth / 2;
            // Tọa độ Y: Đặt lên trên đầu (tâm Y - nửa chiều cao - chiều cao thanh máu - padding)
            int barY = yOnScreen - (int)(getHeight() / 2) - barHeight - padding; 

            // 2. Tính toán độ rộng của phần máu đỏ hiện tại
            int currentHPWidth = (int) (((double)currentHP / maxBlood) * barWidth);

            // 3. Vẽ nền thanh máu (màu xám)
            g2.setColor(Color.GRAY);
            g2.fillRect(barX, barY, barWidth, barHeight);

            // 4. Vẽ máu hiện tại (màu đỏ)
            g2.setColor(Color.RED);
            g2.fillRect(barX, barY, currentHPWidth, barHeight);

            // 5. Vẽ đường viền (tùy chọn)
            g2.setColor(Color.BLACK);
            g2.drawRect(barX, barY, barWidth, barHeight);
        }

        // Sau khi chết (FEY), chúng ta không cần vẽ thanh máu nữa, logic này đã được kiểm soát bởi if (state == ALIVE).
    }

    public void drawBoundForCollisionWithMap(Graphics2D g2){
        Rectangle rect = getBoundForCollisionWithMap();
        g2.setColor(Color.BLUE);
        g2.drawRect(rect.x - (int) getGameWorld().camera.getPosX(), rect.y - (int) getGameWorld().camera.getPosY(), rect.width, rect.height);
    }

    public void drawBoundForCollisionWithEnemy(Graphics2D g2){
        Rectangle rect = getBoundForCollisionWithEnemy();
        g2.setColor(Color.RED);
        g2.drawRect(rect.x - (int) getGameWorld().camera.getPosX(), rect.y - (int) getGameWorld().camera.getPosY(), rect.width, rect.height);
    }

    public abstract Rectangle getBoundForCollisionWithEnemy();

    public abstract void draw(Graphics2D g2);
    
    public void hurtingCallback(){};
	
}
