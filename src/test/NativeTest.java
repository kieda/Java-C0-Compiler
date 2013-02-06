/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;
import com.kieda.core.AbstractCore;
import com.kieda.graphics.Renderable;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Scanner;
import javax.swing.JFrame;
import ui.Disp;
/**
 *
 * @author kieda
 */
public class NativeTest {
    public static void main(String[] args){
        new NativeTest();
        
    }
    public NativeTest(){
        
        new Thread(new Runnable() {
            BufferedImage bi = image_load("./asdf.bmp");
            int x = -100;
            int y = -100;
            int w = bi.getWidth() + 120;
            int h = bi.getHeight() + 200;
            
            JFrame jf = new JFrame(){
                {
                    addKeyListener(new KeyListener() {
                        @Override public void keyTyped(KeyEvent e) {}
                        @Override public void keyPressed(KeyEvent e) {
                            switch(e.getKeyCode()){
                                case KeyEvent.VK_LEFT:
                                    if(w > 0)
                                    w -= 2;
                                    break;
                                case KeyEvent.VK_RIGHT:w += 2;break;
                                case KeyEvent.VK_UP:h+=2;break;
                                case KeyEvent.VK_DOWN:if(h > 0)h-=2;break;
                                case KeyEvent.VK_W:y +=2;break;
                                case KeyEvent.VK_A:x +=2;break;
                                case KeyEvent.VK_S:y -=2;break;
                                case KeyEvent.VK_D:x -=2;break;
                            }
                        }
                        @Override public void keyReleased(KeyEvent e) {}
                    });
                }
                public void paint(Graphics g){
                    BufferedImage bb = image_subimage(bi, x, y, w, h);
                    jf.setSize(bb.getWidth(), bb.getHeight());
                    g.fillRect(0, 0, bb.getWidth(), bb.getHeight());
                    
                    g.drawImage(bb, 0, 0, null);
                }
            };
            
            {
                jf.setBounds(30, 30, 500, 600);
                jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                jf.setUndecorated(true);
                jf.setVisible(true);
                new Thread(new Runnable() {
                    Scanner scan = new Scanner(System.in);
                    @Override
                    public void run() {
                        while(true){
                        String[] ss = scan.nextLine().split(" ");
                        try{
                            if(ss.length == 4){
                                try{
                                    x = Integer.parseInt(ss[0]);
                                    y = Integer.parseInt(ss[1]);
                                    w = Integer.parseInt(ss[2]);
                                    h = Integer.parseInt(ss[3]);
                                } catch(Exception e){}
                            }
                            Thread.currentThread().sleep(100);
                        } catch(Exception e){}
                        }
                    }
                }).start();
            }
            @Override
            public void run() {
                while(true){
                    jf.repaint();
                    try{
                        Thread.currentThread().sleep(100);
                    } catch(Exception e){}
                }
            }
        }).start();
        
    }
    private java.awt.image.BufferedImage image_subimage(java.awt.image.BufferedImage image, int x, int y, int width, int height){
        //@requires image != NULL;
        if(!(image != null)) throw new AssertionError("requires::native function::<img>::image_t image_subimage(image_t image, int x, int y, int width, int height)::image != NULL");
        //added requires. This is not on the c0 function specification, but should be.
        //@requires width >= 0 && height >= 0;
        if(!(width > 0 && height > 0)) throw new AssertionError("requires::native function::<img>::image_t image_subimage(image_t image, int x, int y, int width, int height)::width > 0 && height > 0");
        int img_h = image.getHeight();
        int img_w = image.getWidth();
        java.awt.image.BufferedImage $$$result$$$ = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D gg =  $$$result$$$.createGraphics();
        {
        int cropx, cropy, cropw, croph;
        //where should our image be cropped to?
        int posx, posy;
        //where shall we place the cropped image?
        {
            int bot_ri_x = x + width;
            int bot_ri_y = y + height;
            if(y<= 0){
                cropy =  0;
                posy  = -y;
            } else if(y <= img_h){
                cropy = y;
                posy =  0;
            } else{
                cropy = img_h;
                posy =  -y;
            }
            if(x<= 0){
                cropx =  0;
                posx  = -x;
            } else if(x <= img_w){
                cropx = x;
                posx =  0;
            } else{
                cropx = img_w;
                posx =  -x;
            }
//            System.out.println("brx: " + bot_ri_x + " bry: " + bot_ri_y);
            cropw = (bot_ri_x <= 0)? 0 :((bot_ri_x <= img_w)? bot_ri_x - cropx: img_w - cropx);
            croph = (bot_ri_y <= 0)? 0 :((bot_ri_y <= img_h)? bot_ri_y - cropy : img_h - cropy);
        }
//        System.out.println("cx: "+ cropx + " cy: "+ cropy + " cw: "+cropw + " ch: "+croph);
        if(!(cropw == 0 ||  croph == 0))
            gg.drawImage(image.getSubimage(cropx, cropy, cropw, croph), null, posx, posy);
        }
        if(!(image_width($$$result$$$) == width)) throw new AssertionError("ensures::native function::<img>::image_t image_subimage(image_t image, int x, int y, int width, int height)::image_width(\\result) == width");
        if(!(image_height($$$result$$$) == height)) throw new AssertionError("ensures::native function::<img>::image_t image_subimage(image_t image, int x, int y, int width, int height)::image_height(\\result) == height");
        //@ensures image_width(\result) == width;
        //@ensures image_height(\result) == height;
        return $$$result$$$;
    }
    private int image_width(java.awt.image.BufferedImage image){
        //@requires image != NULL;
        if(!(image != null)) throw new AssertionError("requires::native function::<img>::int image_width(image_t image)::image != NULL");
        int $$$result$$$ = image.getWidth();
        if(!($$$result$$$ > 0)) throw new AssertionError("esnures::native function::<img>::int image_width(image_t image)::\\result > 0");
        return $$$result$$$;
        //@ensures \result > 0;
    }
    private int image_height(java.awt.image.BufferedImage image){
        //@requires image != NULL;
        if(!(image != null)) throw new AssertionError("requires::native function::<img>::int image_height(image_t image)::image != NULL");
        int $$$result$$$ = image.getHeight();
        if(!($$$result$$$ > 0)) throw new AssertionError("esnures::native function::<img>::int image_height(image_t image)::\\result > 0");
        return $$$result$$$;
        //@ensures \result > 0;
    }
    private java.awt.image.BufferedImage image_load(String path){
        java.awt.image.BufferedImage img = null;
        java.io.File input_file = new java.io.File(path);
        //the path doesn't exist or we can't read it.
        if(!input_file.canRead()) return img;
        try {
            //attempt to read the file.
            img = javax.imageio.ImageIO.read(input_file);
            return img;
        } catch (java.io.IOException e) {
            throw new AssertionError("prompt::native function::<img>::image_t image_load(string path)::File is not of a recognizable image type.");
        }
    }
}
