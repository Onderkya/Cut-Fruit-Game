package com.onderkaya.fruitninja;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Fruit {

    public  boolean living=true;
    //Eğer bir elma yere düşerse o anda o elma ile yere düşenleri
    //saymaki canım azalmasın yeni elmalar düştükçe canımı azalt

    public  static float radius= 60f; //Yarı Çap yani meyvelerin boyutunu ayarlıyoruz
    //BURADAKİ İSİMLERİ İSTERSEN DEĞİŞTİRİRİZ BUNLAR GENEL TABİR SACECE static olursa direkt çağırabiliriz
    //Diğer sınıflardan
    public  enum Type{

        REGULAR,EXTRA,EXTRA2,EXTRA3,ENEMY,LIFE
    }
    //  enum yarattık  birden çok nesnemiz olacak..

    Type type;
    Vector2 pozisyon,velocity;


    //Constructer oluştururken bize nesnenin pozisyonu ve hızı gerektiğinden
    // her ikisinide istiyoruz..
    Fruit(Vector2 pozisyon,Vector2 velocity){
        this.pozisyon=pozisyon;
        this.velocity=velocity;
        type=Type.REGULAR;
    }


    //Oyundaki herhangi bir nesneye tıklandımı tıklanılmadımı bunu anlamak
    //için bu metodu yazıyoruz..
    //Vectory2 ile bir parametre istiyoruz tıklanan yerin koordinatını almamız için.
    public  boolean clicked(Vector2 click){

        //pozisyon benım nesnemin pozisyonu dst ise distance yani uzaklık anlamına geliyor
        //dst iki vektör arasındaki uzaklığı hesaplıyor. dst2 dst'e göre daha hızlı çalışıyor.
        //ondan bunu seçiyoruz..
        if (pozisyon.dst2(click) <= radius*radius+1){
            return  true;
        }
         return  false;
    }

    //final demek bundan sonra bu metod değiştirilemez.
    public final Vector2 getPosizsyon(){
        return pozisyon;
    }


    // Bu metod ise nesnemiz halen ekrandamı yoksa değil mi kontrolü için
    public boolean outOfScrenn(){

        return (pozisyon.y<-2f*radius);
    }


    //Nesnelerimiz devamlı bir hareket halinde  ve
    // hızını ve pozisyonunu güncellememiz gerekiyor sürekli

    public  void update(float deltatime){
        velocity.x-=deltatime*(Math.signum(velocity.x*5f));
        //Math.signum eğer değer 0 'dan büyükse 1 küçükse -1 gönderiyor buda x pozisyonundaki
        // bir nesnenin hem sağdan hem soldan çıkmasına yarıyor.

        velocity.y-=deltatime*(Gdx.graphics.getHeight()*0.2f);

        //Alttaki metod istediğimiz vector'ü güncelleme şansını bize veriyor
        // Velocity'i deltatime ile güncelle diyoruz..
        pozisyon.mulAdd(velocity,deltatime);
    }

}
