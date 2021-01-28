package com.onderkaya.fruitninja;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

import javax.xml.soap.Text;

public class FruitNinja extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;

	//Nesnesler
	Texture background;
	Texture apple;
	Texture heart;
	Texture cherry;
	Texture bomb;
	Texture green_apple;
	Texture banana;



	int tiklama=0;

	//Font Tipi
	BitmapFont font;
	FreeTypeFontGenerator font_type;

	//Random
	Random random= new Random();
	Array<Fruit> fruits=new Array<Fruit>();


	//Can ve Skor Bilgisi..
	int lives=0;
	int score=0;


	//Nesne oluşturma hızlarımızı ayarlıyoruz yani saniyede kaç tane vs..
	float generatorCounter=0;
	private  final float startGeneratorSpeed=1.1f;
	float generatorSpeed=1.1f;

	private  double currentTime;
	private  double gameOverTime=-1.0f; // Oyun ne zaman biteceğini kullancağımız için yazdık


	// Sesler
	Sound cut;
	Sound bomb_voice;
	Sound heart_voice;

	Music gameMusic;








	@Override
	public void create () {
		batch = new SpriteBatch();
		// Oyundaki Görsellerimizi Sisteme Oyun Açılırken Ekledik Yollarını Verdik.
		background = new Texture("ninjabackground.png");
		apple=new Texture("apple.png");
		heart=new Texture("heart.png");
		cherry=new Texture("cherry.png");
		bomb=new Texture("bomb.png");
		banana=new Texture("banana.png");
		green_apple=new Texture("green-apple.png");




		//Sesler


		cut=Gdx.audio.newSound(Gdx.files.internal("cut.mp3"));
		bomb_voice=Gdx.audio.newSound(Gdx.files.internal("bomb_voice.mp3"));
		heart_voice=Gdx.audio.newSound(Gdx.files.internal("heart_voice.mp3"));
		gameMusic=Gdx.audio.newMusic(Gdx.files.internal("gameMusic.mp3"));



		//Burada Meyvelerin Boyutlarının Kullanılacak Telefonun Genişliğimi yoksa yüksekliğimi daha yüksekse onu bölüyoruz..
		Fruit.radius=Math.max(Gdx.graphics.getHeight(),Gdx.graphics.getWidth())/10f;

		//Burada kim kullanıcının verdiği girdileri alıcağını belirtiyoruz.. Ekranımız tabiki..
		Gdx.input.setInputProcessor(this);

		//Font İşlemleri
		font_type=new FreeTypeFontGenerator(Gdx.files.internal("HelveticaNeueLight.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parametreler=new FreeTypeFontGenerator.FreeTypeFontParameter();
		parametreler.color= Color.WHITE;
		parametreler.size=Gdx.graphics.getWidth()/20;
		//parametreler.characters="0123456789 Cutoplay :.+-";
		font=font_type.generateFont(parametreler);







	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		//Meyvelerin Oluşturulma Hızlarını Kontrol Etmek için bu değişkenlere ihtiyacımız var;
		double newTime= TimeUtils.millis()/1000.0;
		//Güncel zamanı milisaniye olarak alıyoruz..
		//1000'e bölerek bir double elde ediyoruz..
		double frameTime=Math.min(newTime-currentTime,0.3);
		//bazı cihazlarda render'in çalışması farklı olabilir bazen 60 fps alabiliriz
		//bazende daha az farklı cihazlarda aynı sonucu almamız gerek ondan
		// bunu burada hesaplıyoruz kaç fps aldığımızı..
		// Yeni zamandan güncel zamanı çıkartıyoruz.. yada 0.3 saniye bir değer veriyoruz..
		float deltaTime=(float)frameTime;
		//Deltayı genelde 2 şey arasındaki farkı hesaplamak için kullanarız. frame time ile aynı
		// saadece tipini değiştiricez
		currentTime=newTime;
		// render ilk başladığında currentTime'a yeni zamanı atayacak
		//Ardından yeni zaman'ı birdaha alıcak ve ardından eski zamandan çıkartıcak
		//Buda bize render'in saniyede kaç kere çalıştığını gösterecek..




		if (lives <=0 && gameOverTime==0f){
			//gameover
			font.draw(batch,"Game Over Your Score :"+score,200,Gdx.graphics.getHeight()/2);

			gameOverTime=currentTime;
		}
		if (lives>0){

			//Game Mode oyunu burada  oynucaz..




			//generatorCounter kaç tane oluşturduğunu kontrol ediceğimiz
			//StartgenSpeed başlatma hızı
			//generatorSpeed oluşturma hızımız..


			//olarak burada generatorCounter hızımız zaten 0 olduğundan nesne yaratılacak
			//ardından 0 veya daha düşük olana dek düşecek düştüğü andada yeni meyve gelecek..
			generatorSpeed-=deltaTime*0.015f;
			//generatorCounter 0 ve altına düştüğünde yeni nesnemiz ekleniyor..
			if (generatorCounter<=0f){
				generatorCounter=generatorSpeed;
				addItem();
			}
			// düşmezsede  çıkarıyoruz..
			else{
				generatorCounter-=deltaTime;
			}

			//Döngü ile Lives'larımızı Çiziyoruz..
			for(int i=0; i<lives; i++){

				batch.draw(heart,20f+i*75f,Gdx.graphics.getHeight()-75f,75f,75f);
			}

			for(Fruit fruit: fruits){
				fruit.update(deltaTime);
				//burada nesmeiz geliyor ve onun hızını Fruit class'ımızdaki metod ile güncelliyoruz.

				//burada string ile neden yapmadık neden enum yaptık aslında tam cevabı..
				switch (fruit.type){

					case REGULAR:
						batch.draw(apple,fruit.getPosizsyon().x,fruit.getPosizsyon().y,Fruit.radius,Fruit.radius);
						break;
					case EXTRA:
						batch.draw(cherry,fruit.getPosizsyon().x,fruit.getPosizsyon().y,Fruit.radius,Fruit.radius);
						break;
					case EXTRA2:
						batch.draw(banana,fruit.getPosizsyon().x,fruit.getPosizsyon().y,Fruit.radius,Fruit.radius);
						break;
					case EXTRA3:
						batch.draw(green_apple,fruit.getPosizsyon().x,fruit.getPosizsyon().y,Fruit.radius,Fruit.radius);
						break;

					case ENEMY:
						batch.draw(bomb,fruit.getPosizsyon().x,fruit.getPosizsyon().y,80f,100f);
						break;
					case LIFE:
						batch.draw(heart,fruit.getPosizsyon().x,fruit.getPosizsyon().y,Fruit.radius,Fruit.radius);
						break;
				}


			}
			boolean holdlives=false;
			Array<Fruit> toRemove=new Array<Fruit>();
			for(Fruit fruit: fruits){
				if(fruit.outOfScrenn()){
					toRemove.add(fruit);
					if (fruit.living && fruit.type==Fruit.Type.REGULAR){
						lives--;
						holdlives=true;
						break;
					}
				}
			}
			if (holdlives){
				for(Fruit f: fruits){
					f.living=false;
				}
			}
			for (Fruit f: toRemove){
				fruits.removeValue(f,true);

			}


		}
		font.draw(batch,"Score: "+score,Gdx.graphics.getWidth()-(Gdx.graphics.getWidth()/20*5.3f)
				,Gdx.graphics.getHeight()-Gdx.graphics.getWidth()/20/2f);

		if (lives <=0 ){
			font.draw(batch,"Cut To Play",
					Gdx.graphics.getWidth()/2-Gdx.graphics.getWidth()/20,Gdx.graphics.getHeight()/2);
			tiklama=1;


		}
		else{
			if (tiklama==1) {
				tiklama=0;
				//long id =gameMusic.play();
				 gameMusic.play();
				//gameMusic.setLooping(id,true);

			}
		}


		batch.end();
	}


	//İtemları Eklediğimiz fonksiyonu yazıyoruz..
	private  void  addItem(){
		float pozisyon=random.nextFloat()* Math.max(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		// x hızımı düşültmemiz gerekiyor sadece sağ'a sola hafifden gitsede olur mantığı
		Fruit item=new Fruit(new Vector2(pozisyon,-Fruit.radius),
				new Vector2((Gdx.graphics.getWidth()/2 - pozisyon)*( 0.3f+(random.nextFloat() -0.5f)),
						Gdx.graphics.getHeight()/2));
		float type=random.nextFloat();
		if (type>0.95f){
			item.type=Fruit.Type.LIFE;
		}
		else if(type>0.80f){
			item.type=Fruit.Type.ENEMY;
		}
		else if(type>0.50f){
			item.type=Fruit.Type.EXTRA;
		}

		else if(type>0.45f){
			item.type=Fruit.Type.EXTRA2;
		}
		else if(type>0.40f){
			item.type=Fruit.Type.EXTRA3;
		}



		fruits.add(item);

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		font_type.dispose();
		apple.dispose();
		banana.dispose();
		bomb.dispose();
		bomb_voice.dispose();
		heart_voice.dispose();
		cherry.dispose();
		cut.dispose();
		green_apple.dispose();
		gameMusic.dispose();
		heart.dispose();
		background.dispose();

	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	//Kullanıcı ekrana dokundu ve sürükledi anlamına geliyor TouchDragged bize bu gerekli..
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		if (lives<=0 && currentTime-gameOverTime>2f){
			gameOverTime=0f;
			score=0;
			lives=4;
			generatorSpeed=startGeneratorSpeed;
			fruits.clear();
		}
		else{

			//Eğer meyveyi kestiyse meyve çıkacak..

			Array<Fruit> fruitsRemove=new Array<Fruit>();
			Vector2 tiklananpozisyon=new Vector2(screenX,Gdx.graphics.getHeight()- screenY);

			int skora_ekle=0;
			for(Fruit f:fruits){
				if (f.clicked(tiklananpozisyon)){
					fruitsRemove.add(f);

					switch (f.type){

						case REGULAR:
							cut.play(3f);
							skora_ekle++;
							break;
						case EXTRA:
						case EXTRA2:
						case EXTRA3:
							cut.play(5f);
							skora_ekle+=2;
						break;

						case ENEMY:
							bomb_voice.play(5f);

							lives--;
							break;
						case LIFE:

							heart_voice.play(6f);
							lives++;
							break;
					}



				}

			}
			score+=skora_ekle;

			for(Fruit f:fruitsRemove){
				fruits.removeValue(f,true);

			}
		}
		return false;
	}


	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
